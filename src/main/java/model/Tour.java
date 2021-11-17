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

    public Tour(Intersection depotAddress, String departureTime, ArrayList<Request> planningRequests) {
        this.depotAddress = depotAddress;
        this.departureTime = departureTime;
        this.planningRequests = planningRequests;
    }

    /* GETTERS */

    public double getTourLength() {
        return tourLength;
    }

    public Intersection getDepotAddress() {
        return depotAddress;
    }

    public ArrayList<Request> getPlanningRequests() {
        return planningRequests;
    }

    public ArrayList<ShortestPath> getListShortestPaths() {
        return listShortestPaths;
    }

    /* SETTERS */

    public void setTourLength(double tourLength) {
        this.tourLength = tourLength;
    }

    public void setDepotAddress(Intersection depotAddress) {
        this.depotAddress = depotAddress;
    }

    public void setPlanningRequests(ArrayList<Request> planningRequests) {
        this.planningRequests = planningRequests;
    }

    public void setListShortestPaths(ArrayList<ShortestPath> listShortestPaths) {
        this.listShortestPaths = listShortestPaths;
    }
}
