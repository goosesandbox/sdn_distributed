## SDN Distributed Controller

Simple demontration of SDN distributed controller. Motivation is to remove central controller authority. There are few modes SDN with distributed controller can work. Northbound, Southbound interfaces are preserved.

Using distributed controller removes single point of failure of centrall controller or cluster of centrall controller. By using distributed controller SDN also avoids unsynchronization of current topology with map of topology residing in cotroller. Distributed controller always queries actual topology for path to destination. During paths discovery network devices can utilize fabric based means of path discovery eliminating TCP/IP stack at all. Also request to install flow table entries can be implemented at fabric level.

* Full autonomous mode

    Controller contains logic which is basically path selection logic. On uknown packet arrival, topology is asked about  path to destination. Controller selects best path and ask topology to add entries to flow tables. 

* Using external application

    Each controller can be connected to cluster of message queues. Each queue has 0..n desicion agents attached. Decision agent is application which select best path and returns it in response queue. Controller then ask topology to to install new flow entries.
    

* Using REST api

    Classical scenario where each network device with embeded controller can provide Northbound API via REST.
    
* MapReduce framework(theoretical)

    A variation of using external application. Path selection is basically map-reduce, sometimes only map-filter problem. Controller can be attached to MapReduce framework. In Map phase map list is mapped to list of statistics for each device. Subsequently reduce algorithm selects best path.
