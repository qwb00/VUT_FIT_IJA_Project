/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 */
package main.java.simulation.states;

import main.java.common.Environment;

/**
 * The State interface represents a state in the simulation.
 * It provides a method to restore the state of the environment.
 */
public interface State {
    /**
     * Restores the state of the environment
     *
     * @param environment The environment to restore
     */
    void restore(Environment environment);
}
