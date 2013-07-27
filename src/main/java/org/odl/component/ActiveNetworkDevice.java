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

    public void attach(NetworkDevice nd) {
        this.attachedDevices.put(nd.deviceId(), nd);
    }

    @Override
    public void addFlow(String destination, Path path) {
        String nextDeviceId = path.nextHop();
        flowTable.put(destination, nextDeviceId);
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

    /**
     *
     * @see {@link org.odl.core.Controller#discoverAllPaths(String, Path)}
     */
    @Override
    public List<Path> findPaths(String destinationDeviceId, Path path) {
        // please do see corresponding doc in controller
        return controller.discoverAllPaths(destinationDeviceId, path);
    }

    @Override
    public void post(PathSelector logic) {
        /* FIXME:
         *
         * We fake topology reseting by clearing flow table of entry network device. whic is OK for testing.
         * Regular way would be topology reseting. Here we post logic in bytecode format. Inproduction
         * we communicate via controller Northbound API.
         */
        this.flowTable.clear();
        this.controller.setLogic(logic);
        for (NetworkDevice nd : attachedDevices.values()) {
            if (nd instanceof Rest) {
                Rest server = (Rest)nd;
                server.post(logic);
            }
        }
    }
}
