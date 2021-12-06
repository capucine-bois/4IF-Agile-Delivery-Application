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
     * The arrival time at the pickup point.
     */
    private String pickupArrivalTime;

    /**
     * The arrival time at the delivery point.
     */
    private String deliveryArrivalTime;

    /**
     * The departure time at the pickup point.
     */
    private String pickupDepartureTime;

    /**
     * The departure time at the delivery point.
     */
    private String deliveryDepartureTime;

    /**
     * The selected state of the pickup.
     */
    private boolean pickupSelected;

    /**
     * The selected state of the delivery.
     */
    private boolean deliverySelected;

    /**
     * The color of the request.
     */
    private Color color;

    public static Color lastColor = Color.red;

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
        this.pickupSelected = false;
        this.deliverySelected = false;
        this.color = lastColor;
        getNextColor();
    }

    public Request() {
        this.pickupDuration = 0;
        this.deliveryDuration = 0;
        this.pickupSelected = false;
        this.deliverySelected = false;
        this.color = lastColor;
        getNextColor();
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

    public String getPickupArrivalTime() {
        return pickupArrivalTime;
    }

    public String getDeliveryArrivalTime() {
        return deliveryArrivalTime;
    }

    public String getPickupDepartureTime() {
        return pickupDepartureTime;
    }

    public String getDeliveryDepartureTime() {
        return deliveryDepartureTime;
    }

    public boolean isPickupSelected() {
        return pickupSelected;
    }

    public boolean isDeliverySelected() {
        return deliverySelected;
    }

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

    public void setPickupArrivalTime(String pickupArrivalTime) {
        this.pickupArrivalTime = pickupArrivalTime;
    }

    public void setDeliveryArrivalTime(String deliveryArrivalTime) {
        this.deliveryArrivalTime = deliveryArrivalTime;
    }

    public void setPickupDepartureTime(String pickupDepartureTime) {
        this.pickupDepartureTime = pickupDepartureTime;
    }

    public void setDeliveryDepartureTime(String deliveryDepartureTime) {
        this.deliveryDepartureTime = deliveryDepartureTime;
    }

    public void setPickupSelected(boolean pickupSelected) {
        this.pickupSelected = pickupSelected;
    }

    public void setDeliverySelected(boolean deliverySelected) {
        this.deliverySelected = deliverySelected;
    }

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

    private void getNextColor() {
        float[] hsv = new float[3];
        Color.RGBtoHSB(lastColor.getRed(), lastColor.getGreen(), lastColor.getBlue(), hsv);
        double goldenRatioConjugate = 0.618033988749895;
        hsv[0] += goldenRatioConjugate;
        hsv[0] %= 1;
        lastColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
    }
}
