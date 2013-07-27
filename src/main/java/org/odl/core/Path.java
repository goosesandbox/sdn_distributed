package org.odl.core;

import java.util.LinkedList;

public class Path {
    private final LinkedList<String> hops = new LinkedList<String>();

    public void addHop(String hop) {
        hops.add(hop);
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
        return hops.removeFirst();
    }

    public boolean hasMoreHops() {
        return hops.size() > 0;
    }

    public boolean isVia(String viaHop) {
        return hops.contains(viaHop);
    }
}
