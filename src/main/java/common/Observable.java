/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * Interface for objects that can be observed.
 * Classes implementing this interface allow other objects to observe their state changes.
 */
package main.java.common;

public interface Observable {
    /**
     * Adds an observer to this observable object.
     *
     * @param var1 The observer to add.
     */
    void addObserver(Observer var1);

    /**
     * Removes an observer from this observable object.
     *
     * @param var1 The observer to remove.
     */
    void removeObserver(Observer var1);

    /**
     * Notifies all registered observers about a change in this observable object.
     */
    void notifyObservers();

    /**
     * Interface for objects that can observe changes in observable objects.
     */
    interface Observer {
        /**
         * Updates the observer based on a change in the observable object.
         *
         * @param var1 The observable object that changed.
         */
        void update(Observable var1);
    }
}
