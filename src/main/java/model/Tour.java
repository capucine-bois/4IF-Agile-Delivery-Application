package model;

import java.util.ArrayList;

public class Tour {

    /* ATTRIBUTES */

    private double tourLength;
    private Intersection depotAddress;
    private String departureTime;
    private ArrayList<Request> planningRequests;
    private ArrayList<ShortestPath> listShortestPaths;

    /* CONSTRUCTORS */

    /**
     * Default constructor
     */
    public Tour(){}

    /* GETTERS */

    /**
     * Getter for tour tourLength attribute
     * @return tour length
     */
    public double getTourLength() {
        return tourLength;
    }

    /**
     * Getter for depotAddress attribute
     * @return depot address
     */
    public Intersection getDepotAddress() {
        return depotAddress;
    }

    /**
     * Getter for departureTime attribute
     * @return departure time
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Getter for planningRequests attribute
     * @return planning of requests
     */
    public ArrayList<Request> getPlanningRequests() {
        return planningRequests;
    }

    /**
     * Getter for listShortestPaths attribute
     * @return list of shortest paths
     */
    public ArrayList<ShortestPath> getListShortestPaths() {
        return listShortestPaths;
    }

    /* SETTERS */

    /**
     * Setter for tourLength attribute
     * @param tourLength wanted attribute for tourLength attribute
     */
    public void setTourLength(double tourLength) {
        this.tourLength = tourLength;
    }
    /**
     * Setter for depotAddress attribute
     * @param depotAddress wanted attribute for depotAddress attribute
     */
    public void setDepotAddress(Intersection depotAddress) {
        this.depotAddress = depotAddress;
    }
    /**
     * Setter for departureTime attribute
     * @param departureTime wanted attribute for departureTime attribute
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
    /**
     * Setter for planningRequests attribute
     * @param planningRequests wanted attribute for planningRequests attribute
     */
    public void setPlanningRequests(ArrayList<Request> planningRequests) {
        this.planningRequests = planningRequests;
    }
    /**
     * Setter for listShortestPaths attribute
     * @param listShortestPaths wanted attribute for listShortestPaths attribute
     */
    public void setListShortestPaths(ArrayList<ShortestPath> listShortestPaths) {
        this.listShortestPaths = listShortestPaths;
    }
}
