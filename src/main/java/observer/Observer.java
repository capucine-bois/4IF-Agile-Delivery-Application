package observer;

/**
 * Interface used to tag classes as Observer.
 */
public interface Observer {
    /**
     * Method called by observable instances when they notify observers.
     * @param observed observable instance which send the notification
     *
     */
    void update(Observable observed);
}