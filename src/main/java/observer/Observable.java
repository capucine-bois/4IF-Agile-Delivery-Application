package observer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Observable class, that can notify observers.
 * Used for some model classes, which inherit from Observable to notify GUI when their attributes change.
 */
public class Observable {

    /**
     * Collection of all observers.
     */
    private Collection<Observer> obs;

    /**
     * Default constructor. Initialize collection of observers as an empty ArrayList.
     */
    public Observable() {
        obs = new ArrayList<Observer>();
    }

    /**
     * Add observer to collection of observers.
     * @param o the observer to add
     */
    public void addObserver(Observer o) {
        if (!obs.contains(o)) obs.add(o);
    }

    /**
     * Notify every observer by calling their update method.
     * @param arg notification data
     */
    public void notifyObservers(Object arg) {
        for (Observer o : obs)
            o.update(this, arg);
    }

    /**
     * Default notification where observers are notified without receiving notification data.
     */
    public void notifyObservers() {
        notifyObservers(null);
    }
}
