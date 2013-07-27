package org.odl.logic;

import org.odl.core.Path;

import java.util.List;

public interface PathSelector {
    Path select(List<Path> paths);
}
