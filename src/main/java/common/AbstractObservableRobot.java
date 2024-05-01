package main.java.common;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractObservableRobot implements Robot, Observable {
    private Set<Observable.Observer> observers = new HashSet();

    public AbstractObservableRobot() {
    }

    public void addObserver(Observable.Observer var1) {
        this.observers.add(var1);
    }

    public void removeObserver(Observable.Observer var1) {
        this.observers.remove(var1);
    }

    public void notifyObservers() {
        this.observers.forEach((var1) -> {
            var1.update(this);
        });
    }

    @Override
    public AbstractObservableRobot clone() {
        try {
            AbstractObservableRobot cloned = (AbstractObservableRobot) super.clone();
            // Deep copy or recreate the observers set if necessary
            cloned.observers = new HashSet<>(this.observers);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);  // Since we're Cloneable, this shouldn't happen
        }
    }
}