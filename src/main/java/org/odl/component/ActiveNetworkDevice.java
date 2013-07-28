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
        /* Simplified logic. Does not prevent loops.
         * Instead of manually installing logic to devices, device receiving new logic must emit
         * NEW_LOGIC_EVENT containing version number. Device receving event must check its logic version
         * against emited version number.
         *
         * Since installing new logic is not an everyday task. Performance should not be an issue.
         * All packet formwarding must act according to new logic.
         *
         * As an alternative to embeded logic scenario, logic can sit externally and controller
         * consults logic by connecting to logic provider.
         */
        for (NetworkDevice nd : attachedDevices.values()) {
            if (nd instanceof Rest) {
                Rest server = (Rest)nd;
                server.post(logic);
            }
        }
    }
}
