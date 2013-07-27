package org.odl;

import org.odl.component.*;
import org.odl.core.Packet;
import org.odl.logic.Strategy;

public class Test {
    public static void main(String[] args) {
        // devices on hand
        ActiveNetworkDevice ROUTER_A = new ActiveNetworkDevice("ROUTER_A");
        ActiveNetworkDevice ROUTER_B = new ActiveNetworkDevice("ROUTER_B");
        ActiveNetworkDevice ROUTER_C = new ActiveNetworkDevice("ROUTER_C");
        ActiveNetworkDevice ROUTER_D = new ActiveNetworkDevice("ROUTER_D");
        ActiveNetworkDevice ROUTER_E = new ActiveNetworkDevice("ROUTER_E");
        ActiveNetworkDevice ROUTER_F = new ActiveNetworkDevice("ROUTER_F");
        ActiveNetworkDevice ROUTER_G = new ActiveNetworkDevice("ROUTER_G");
        ActiveNetworkDevice ROUTER_H = new ActiveNetworkDevice("ROUTER_H");
        ActiveNetworkDevice ROUTER_NSA = new ActiveNetworkDevice("ROUTER_NSA");
        EndUserNetworkDevice EUND_A = new EndUserNetworkDevice("EUND_A");
        EndUserNetworkDevice EUND_B = new EndUserNetworkDevice("EUND_B");

        // physical topology
        ROUTER_A.attach(ROUTER_B);
        ROUTER_B.attach(ROUTER_C);
        ROUTER_B.attach(ROUTER_D);
        ROUTER_C.attach(ROUTER_E);
        ROUTER_C.attach(ROUTER_F);
        ROUTER_C.attach(ROUTER_D);
        ROUTER_D.attach(ROUTER_F);
        ROUTER_E.attach(ROUTER_G);
        ROUTER_F.attach(ROUTER_G);
        ROUTER_E.attach(ROUTER_H);
        ROUTER_G.attach(EUND_A);
        ROUTER_H.attach(EUND_B);

        // add monitoring agency to topology
        ROUTER_D.attach(ROUTER_NSA);
        ROUTER_NSA.attach(ROUTER_F);
        ROUTER_F.attach(ROUTER_E);

        System.out.println("set path selection logic to first available");
        ROUTER_A.post(Strategy.FIRST_AVAILABLE.logic());
        Packet packet = new Packet("SOURCE_SYSTEM", "EUND_A", "MESSAGE_01");
        send(packet, ROUTER_A, EUND_A);

        packet = new Packet("SOURCE_SYSTEM", "EUND_B", "MESSAGE_02");
        send(packet, ROUTER_A, EUND_B);

        System.out.println("send all traffic via monitoring agency routers");
        ROUTER_A.post(Strategy.VIA_NSA.logic());
        packet = new Packet("SOURCE_SYSTEM", "EUND_A", "MESSAGE_VIA_NSA");
        send(packet, ROUTER_A, ROUTER_NSA);

        packet = new Packet("SOURCE_SYSTEM", "EUND_B", "MESSAGE_VIA_NSA");
        send(packet, ROUTER_A, ROUTER_NSA);
    }

    private static void assertEquals(Packet sent, Packet received) {
        if (!sent.equals(received)) {
            throw new RuntimeException("packet sent != packet received");
        }
    }

    private static void send(Packet packet, NetworkDevice nd, NetworkDevice copareWith) {
        System.out.println("PACKET -> " + packet);
        System.out.println("NETWORK DEVICE -> " + nd.deviceId());
        System.out.print("HOPS -> ");
        nd.accept(packet);
        System.out.print("\n\n");
        assertEquals(packet, copareWith.getLastPacket());
    }
}
