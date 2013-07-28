package org.odl.component;

import org.odl.api.Rest;
import org.odl.api.SouthBound;
import org.odl.core.Controller;
import org.odl.core.Packet;
import org.odl.core.Path;
import org.odl.logic.PathSelector;

import java.util.*;

public class ActiveNetworkDevice extends NetworkDevice implements SouthBound, Rest {
    private final Map<String, NetworkDevice> attachedDevices = new HashMap<String, NetworkDevice>();
    private Map<String, String> flowTable = new HashMap<String, String>();
    private final Controller controller;

    public ActiveNetworkDevice(String deviceId) {
        super(deviceId);
        controller = new Controller(this);
    }

    @Override
    public void accept(Packet packet) {
        super.accept(packet);
        if (attachedDevices.containsKey(packet.destination)) {
            attachedDevices.get(packet.destination).accept(packet);
            return;
        }

        if( !flowTable.containsKey(packet.destination) ) {
            controller.unknown(packet);
        }

        String nextDeviceId = flowTable.get(packet.destination);
        attachedDevices.get(nextDeviceId).accept(packet);
    }

    public void connect(NetworkDevice nd) {
        this.attachedDevices.put(nd.deviceId(), nd);
    }

    @Override
    public void addFlow(String destination, Path path) {
        String nextDeviceId = path.nextHop();
        flowTable.put(destination, nextDeviceId);
        installFlowToDevices(destination, path, nextDeviceId);
    }

    private void installFlowToDevices(String destination, Path path, String nextDeviceId) {
        if (path.hasMoreHops()) {
            Map<String, NetworkDevice> attachedDevices = this.getAttachedDevices();
            SouthBound nextSouthBound = (SouthBound)attachedDevices.get(nextDeviceId);
            nextSouthBound.addFlow(destination, path);
        }
    }

    @Override
    public Map<String, NetworkDevice> getAttachedDevices() {
        return Collections.unmodifiableMap(attachedDevices);
    }

    /*
     * Best to be implemented in device fabric. There is no need to implement it in controller.
     * What we need is programmable decision logic. We do not need path searching algorithm.
     * Since each fabric is asking about next path, only directly connected devices,
     * we can skip all TCP/UDP/IP stack and ask for path using more efficient protocols.
     */
    @Override
    public Path findPath(String destinationDeviceId, Path path) {
        Path returnPath = null;
        if ( isDestinationAttached(destinationDeviceId) ) {
            returnPath = path.addHop(this.deviceId());
        } else if ( isDeviceVisited(path) ) {
            // Loop was formed. Discard current path.
            returnPath = null;
        } else if ( isEndDevice() ) {
            // We have reached end device but did not found target device. Discard current path.
            returnPath = null;
        } else {
            // queryNextDevices must return only paths to target device.
            // Other paths are discarded in previous if statements.
            List<Path> paths = queryNextDevices(destinationDeviceId,
                                                path.addHop(this.deviceId()));
            // Reduce part of MapReduce.
            // List of candidate paths is reduced based on algorithm in logic.
            // Path is then send to calling fabric. Calling fabric collects path info
            // from all connected devices and then again list of paths is reduced to one.
            // Optimal path calculation is then distributed among network nodes.
            if (paths.size() > 0) {
                returnPath = controller.decidePath(paths);
            }
        }
        return returnPath;
    }

    private List<Path> queryNextDevices(String destinationDeviceId, Path path) {
        List<Path> paths = new ArrayList<Path>();
        Map<String, NetworkDevice> attachedDevices = this.getAttachedDevices();
        for(NetworkDevice nd : attachedDevices.values()) {
            if (nd instanceof SouthBound) {
                SouthBound nextSouthBound = (SouthBound)nd;
                Path candidate = nextSouthBound.findPath(destinationDeviceId, path.duplicate());
                if (candidate != null) {
                    paths.add(candidate);
                }
            }
        }
        return paths;
    }

    private boolean isDestinationAttached(String destinationDeviceId) {
        Map<String, NetworkDevice> attachedDevices = this.getAttachedDevices();
        return attachedDevices.containsKey(destinationDeviceId);
    }

    private boolean isDeviceVisited(Path path) {
        return path.contains(this.deviceId());
    }

    private boolean isEndDevice() {
        Map<String, NetworkDevice> attachedDevices = this.getAttachedDevices();
        return attachedDevices.size() == 0;
    }

    @Override
    public void post(PathSelector logic) {
        this.flowTable.clear();
        this.controller.setLogic(logic);
        // Simplified logic.
        // Does not prevent loops.
        for (NetworkDevice nd : attachedDevices.values()) {
            if (nd instanceof Rest) {
                Rest server = (Rest)nd;
                server.post(logic);
            }
        }
    }
}
