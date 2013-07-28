package org.odl.core;

import org.odl.api.NorthBound;
import org.odl.api.SouthBound;
import org.odl.logic.PathSelector;

import java.util.List;

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
        Path selectedPath = southBound.findPath(packet.destination, new Path());

        /*
         * Path starts with current device. We need to move pointer to the next one.
         */
        selectedPath.nextHop();
        southBound.addFlow(packet.destination, selectedPath);
    }

    public Path decidePath(List<Path> paths) {
        return logic.select(paths);
    }
}
