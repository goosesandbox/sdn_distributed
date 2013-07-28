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
                    return found;
                }};}},

    CORBA_ZEROMQ_REST {
        @Override
        public PathSelector logic() {
            // We can connect to logic sitting somewhere in the Cloud.
            // Using network connection we can used more advanced algorithms of path discovery
            // This way we can bypass Reduce algorithm of path search and always use whole set
            // of candidate paths.
            throw new UnsupportedOperationException();
        }};


    public abstract PathSelector logic();
}
