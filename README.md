## SDN Distributed Controller

Simple demontration of SDN distributed controller. Motivation is to remove central controller authority.   

Using distributed controller removes single point of failure of centrall controller. Distributed controller always queries actual topology for path to destination. During paths discovery network device utilize fabric, eliminating TCP/IP stack at all. Also request to install flow table entries can be implemented at fabric level.

Fabric is asking controller programmable logic to select optimal path. Using java we can run controller embeded on device. Path calculation is distributed on each candidate node by Reduce algorithm.
