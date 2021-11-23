package model;

/**
 * A request represents pickup and delivery (both at the same time).
 * A request has a pickup address (where to get the package), and a delivery address (where to deliver the package).
 * For both addresses, a duration specifies the action’s process (pickup/delivery duration).
 */
public class Request {

    /* ATTRIBUTES */

    /**
     * The duration it takes to pickup the package when the delivery person is at the pickup address.
     */
    private int pickupDuration;

    /**
     * The duration it takes to deliver the package when the delivery person is at the delivery address.
     */
    private int deliveryDuration;

    /**
     * The address of the pickup point.
     */
    private Intersection pickupAddress;

    /**
     * The address of the delivery point.
     */
    private Intersection deliveryAddress;

    /**
     * The selected state of the request.
     */
    private boolean selected;

    /* CONSTRUCTORS */

    /**
     * Complete constructor
     * @param pickupDuration duration for pickup action
     * @param deliveryDuration duration for delivery action
     * @param pickupAddress address of pickup point
     * @param deliveryAddress address of delivery point
     */
    public Request(int pickupDuration, int deliveryDuration, Intersection pickupAddress, Intersection deliveryAddress) {
        this.pickupDuration = pickupDuration;
        this.deliveryDuration = deliveryDuration;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.selected = false;
    }

    /* GETTERS */

    /**
     * Getter for pickupDuration attribute
     * @return pickup duration
     */
    public int getPickupDuration() {
        return pickupDuration;
    }

    /**
     * Getter for deliveryDuration attribute
     * @return delivery duration
     */
    public int getDeliveryDuration() {
        return deliveryDuration;
    }

    /**
     * Getter for pickupAddress attribute
     * @return pickup address
     */
    public Intersection getPickupAddress() {
        return pickupAddress;
    }

    /**
     * Getter for deliveryAddress attribute
     * @return delivery address
     */
    public Intersection getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Getter for selection attribute
     * @return selected state
     */
    public boolean isSelected() {
        return selected;
    }

    /* SETTERS */

    /**
     * Setter for pickupDuration attribute
     * @param pickupDuration new pickup duration
     */
    public void setPickupDuration(int pickupDuration) {
        this.pickupDuration = pickupDuration;
    }

    /**
     * Setter for deliveryDuration attribute
     * @param deliveryDuration new delivery duration
     */
    public void setDeliveryDuration(int deliveryDuration) {
        this.deliveryDuration = deliveryDuration;
    }

    /**
     * Setter for pickupAddress attribute
     * @param pickupAddress new pickup address
     */
    public void setPickupAddress(Intersection pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    /**
     * Setter for deliveryAddress attribute
     * @param deliveryAddress new delivery address
     */
    public void setDeliveryAddress(Intersection deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Setter for selection attribute
     * @param selected new selected state (true or false)
     */
    public void setSelected(boolean selected) { this.selected = selected; }

    /**
     * Check if two requests have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof Request) {
            Request r = (Request) o;
            check = r.getDeliveryAddress().equals(this.getDeliveryAddress()) &&
                    r.getPickupAddress().equals(this.getPickupAddress()) &&
                    r.getDeliveryDuration() == this.getDeliveryDuration() &&
                    r.getPickupDuration() == this.getPickupDuration();
        } else {
            check = false;
        }
        return check;
    }
}
