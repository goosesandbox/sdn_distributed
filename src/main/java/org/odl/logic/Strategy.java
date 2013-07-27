package org.odl.logic;

import org.odl.core.Path;

import java.util.List;

public enum Strategy {
    FIRST_AVAILABLE {
        @Override
        public PathSelector logic() {
            return  new PathSelector() {
                @Override
                public Path select(List<Path> paths) {
                    Path path = paths.get(0);
                    if (path.hasMoreHops()) {
                        path.nextHop();
                    }
                    return path;
                }};}},

    VIA_NSA {
        @Override
        public PathSelector logic() {
            return new PathSelector() {
                @Override
                public Path select(List<Path> paths) {
                    Path found = null;
                    for(Path path : paths) {
                        if (path.isVia("ROUTER_NSA")) {
                            found = path;
                            break;
                        }
                    }
                    if (found.hasMoreHops()) {
                        found.nextHop();
                    }
                    return found;
                }};}},

    FASTEST_PATH {
        @Override
        public PathSelector logic() {
            // we need statistics from fabric to determine fastest path
            // sum latencies and select
            throw new UnsupportedOperationException();
        }},

    MAXIMUM_BANDWIDTH {
        @Override
        public PathSelector logic() {
            // we need fabric data to select optimum path
            // select path with largest of smallest
            throw new UnsupportedOperationException();
        }};

    public abstract PathSelector logic();
}
