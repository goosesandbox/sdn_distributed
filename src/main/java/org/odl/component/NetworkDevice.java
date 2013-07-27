package org.odl.component;

import org.odl.core.Packet;

public abstract class NetworkDevice implements DeviceIdentifier {
    private final String deviceId;
    private Packet lastPacket;

    protected NetworkDevice(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String deviceId() {
        return this.deviceId;
    }

    public void accept(Packet packet) {
        System.out.print(deviceId() + ", ");
        this.lastPacket = packet;
    }

    public Packet getLastPacket() {
        return this.lastPacket;
    }
}
