package main.java.simulation.states;

import main.java.common.Environment;

public interface State {
    void restore(Environment environment);
}
