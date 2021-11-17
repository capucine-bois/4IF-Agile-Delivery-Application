package model;

import java.util.ArrayList;

public class Tour {

    /* ATTRIBUTES */

    private double tourLength;
    private Intersection depotAddress;
    private ArrayList<Request> planningRequests;
    private ArrayList<ShortestPath> listShortestPaths;

    /* CONSTRUCTORS */

    public Tour(double tourLength, Intersection depotAddress, ArrayList<Request> planningRequests, ArrayList<ShortestPath> listShortestPaths) {
        this.tourLength = tourLength;
        this.depotAddress = depotAddress;
        this.planningRequests = planningRequests;
        this.listShortestPaths = listShortestPaths;
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
