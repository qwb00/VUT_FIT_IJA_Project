package main.java.simulation.states;

import main.java.common.Environment;

public interface State {
    /**
     * Restores the state of the environment
     *
     * @param environment The environment to restore
     */
    void restore(Environment environment);
}
