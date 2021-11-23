package model;

import java.awt.*;

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

    /**
     * The color of the request.
     */
    private Color color;

    /* CONSTRUCTORS */

    /**
     * Complete constructor
     * @param pickupDuration duration for pickup action
     * @param deliveryDuration duration for delivery action
     * @param pickupAddress address of pickup point
     * @param deliveryAddress address of delivery point
     * @param color color of the request
     */
    public Request(int pickupDuration, int deliveryDuration, Intersection pickupAddress, Intersection deliveryAddress, Color color) {
        this.pickupDuration = pickupDuration;
        this.deliveryDuration = deliveryDuration;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.selected = false;
        this.color = color;
    }

    /* GETTERS */

    public int getPickupDuration() {
        return pickupDuration;
    }

    public int getDeliveryDuration() {
        return deliveryDuration;
    }

    public Intersection getPickupAddress() {
        return pickupAddress;
    }

    public Intersection getDeliveryAddress() {
        return deliveryAddress;
    }

    public boolean isSelected() {
        return selected;
    }

    /**
     * Getter for color attribute
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /* SETTERS */

    public void setPickupDuration(int pickupDuration) {
        this.pickupDuration = pickupDuration;
    }

    public void setDeliveryDuration(int deliveryDuration) {
        this.deliveryDuration = deliveryDuration;
    }

    public void setPickupAddress(Intersection pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setDeliveryAddress(Intersection deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    /**
     * Setter for color attribute
     * @param color new color
     */
    public void setColor(Color color) {
        this.color = color;
    }

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
