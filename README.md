## SDN Distributed Controller

Simple demonstration of SDN distributed controller. Motivation is to remove central controller authority.

Using distributed controller removes single point of failure of central controller. Distributed controller always queries actual topology for path to destination. During paths discovery network device utilize fabric, eliminating TCP/IP stack at all. Also request to install flow table entries can be implemented at fabric level.

Fabric is asking controller programmable logic to select optimal path. Using java we can run controller embedded on device. Path calculation is distributed on each candidate node by Reduce algorithm.

```java
Network simulator setup

ROUTER_A.connect(ROUTER_B);
ROUTER_B.connect(ROUTER_C);
ROUTER_B.connect(ROUTER_D);
ROUTER_C.connect(ROUTER_E);
ROUTER_C.connect(ROUTER_F);
ROUTER_C.connect(ROUTER_D);
ROUTER_D.connect(ROUTER_F);
ROUTER_E.connect(ROUTER_G);
ROUTER_F.connect(ROUTER_G);
ROUTER_E.connect(ROUTER_H);
ROUTER_G.connect(EUND_A);
ROUTER_H.connect(EUND_B);
ROUTER_F.connect(ROUTER_E);

// add monitoring agency to topology
ROUTER_D.connect(ROUTER_NSA);
ROUTER_NSA.connect(ROUTER_F);

checkout the code and run
mvn clean install -Prun

...
set path selection logic to first available
PACKET -> Packet{source='SOURCE_SYSTEM', destination='EUND_A', message='MESSAGE_01'}
NETWORK DEVICE -> ROUTER_A
HOPS -> ROUTER_A, ROUTER_B, ROUTER_C, ROUTER_F, ROUTER_G, EUND_A,

PACKET -> Packet{source='SOURCE_SYSTEM', destination='EUND_B', message='MESSAGE_02'}
NETWORK DEVICE -> ROUTER_A
HOPS -> ROUTER_A, ROUTER_B, ROUTER_C, ROUTER_F, ROUTER_E, ROUTER_H, EUND_B,

send all traffic via monitoring agency routers
PACKET -> Packet{source='SOURCE_SYSTEM', destination='EUND_A', message='MESSAGE_VIA_NSA'}
NETWORK DEVICE -> ROUTER_A
HOPS -> ROUTER_A, ROUTER_B, ROUTER_C, ROUTER_D, ROUTER_NSA, ROUTER_F, ROUTER_G, EUND_A,

PACKET -> Packet{source='SOURCE_SYSTEM', destination='EUND_B', message='MESSAGE_VIA_NSA'}
NETWORK DEVICE -> ROUTER_A
HOPS -> ROUTER_A, ROUTER_B, ROUTER_C, ROUTER_D, ROUTER_NSA, ROUTER_F, ROUTER_E, ROUTER_H, EUND_B,
...
```

