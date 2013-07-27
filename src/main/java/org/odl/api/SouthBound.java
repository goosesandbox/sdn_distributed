package org.odl.api;

import org.odl.component.DeviceIdentifier;
import org.odl.component.NetworkDevice;
import org.odl.core.Path;

import java.util.List;
import java.util.Map;

public interface SouthBound extends DeviceIdentifier {
    void addFlow(String destination, Path path);
    Map<String, NetworkDevice> getAttachedDevices();
    String deviceId();
    List<Path> findPaths(String destinationDeviceId, Path path);
}
