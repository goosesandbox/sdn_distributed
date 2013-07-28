package org.odl.core;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private final List<String> hops = new ArrayList<String>();
    private int currentHop = 0;

    public Path addHop(String hop) {
        hops.add(hop);
        return this;
    }

    public Path duplicate() {
        Path clonedPath = new Path();
        clonedPath.hops.addAll(this.hops);
        return clonedPath;
    }

    public boolean contains(String hop) {
        return hops.contains(hop);
    }

    public String nextHop() {
        String nextHop = hops.get(currentHop);
        this.currentHop += 1;
        return nextHop;
    }

    public boolean hasMoreHops() {
        return this.currentHop <= (hops.size() - 1);
    }

    public boolean isVia(String viaHop) {
        return hops.contains(viaHop);
    }
}
