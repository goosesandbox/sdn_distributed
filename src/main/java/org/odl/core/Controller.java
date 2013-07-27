package org.odl.core;

import org.odl.api.NorthBound;
import org.odl.api.SouthBound;
import org.odl.component.NetworkDevice;
import org.odl.logic.PathSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller implements NorthBound {
    private final SouthBound southBound;
    private PathSelector logic;

    public Controller(SouthBound southBound) {
        this.southBound = southBound;
    }

    public void setLogic(PathSelector logic) {
        this.logic = logic;
    }

    public void unknown(Packet packet) {
        List<Path> paths = discoverAllPaths(packet.destination, new Path());
        southBound.addFlow(packet.destination, logic.select(paths));
    }

    /**
     * Since path discovery is fundamental requirement of network, method could be implemented at fabric level.
     * Avoiding TCP/IP stack. In theory some logic could be implemented also in path discovery.
     */
    public List<Path> discoverAllPaths(String destinationDeviceId, Path path) {
        Map<String, NetworkDevice> attachedDevices = southBound.getAttachedDevices();
        // Destination device found
        if ( attachedDevices.containsKey(destinationDeviceId) ) {
            path.addHop(southBound.deviceId());
            List<Path> paths = new ArrayList<Path>();
            paths.add(path);
            return paths;
        }

        // Node already visited
        if ( path.contains(southBound.deviceId()) ) {
            List<Path> paths = new ArrayList<Path>();
            paths.add(path);
            return paths;
        }

        // No more devices attached
        if ( attachedDevices.size() == 0 ) {
            return new ArrayList<Path>();
        }

        path.addHop(southBound.deviceId());
        List<Path> paths = new ArrayList<Path>();
        for(NetworkDevice nd : attachedDevices.values()) {
            if (nd instanceof SouthBound) {
                SouthBound nextSouthBound = ((SouthBound)nd);
                List<Path> pathsDiscovered = nextSouthBound.findPaths(destinationDeviceId, path.duplicate());
                paths.addAll(pathsDiscovered);
            }
        }
        return paths;
    }
}
