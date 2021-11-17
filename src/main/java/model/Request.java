package model;

public class Request {

    /* ATTRIBUTES */

    private int pickupDuration;
    private int deliveryDuration;

    private Intersection pickupAddress;
    private Intersection deliveryAddress;

    /* CONSTRUCTORS */

    public Request(int pickupDuration, int deliveryDuration, Intersection pickupAddress, Intersection deliveryAddress) {
        this.pickupDuration = pickupDuration;
        this.deliveryDuration = deliveryDuration;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
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
}
