package model;

import observer.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A tour is composed of many (shortest) paths, all connected between them, to accomplish every request that must
 * be made during the same trip. It has access to these requests.
 * A tour also has a depot address where the delivery man starts and ends its travel, and a tour length in meters,
 * which is the sum of the length of its paths.
 * As it extends Observable, an instance can notify observer when their attributes change.
 */
public class Tour extends Observable {

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

    /**
     * Adding a request in the planning requests.
     * @param request the request to add
     */
    public void addRequest(Request request) {
        planningRequests.add(request);
    }

    /* actual methods */
    public void  computeTour(Map<Intersection, ArrayList<Segment>> adjacenceMap) {
        Map<Intersection, ArrayList<ShortestPath>> dist = new HashMap<>();
        // get the points useful for the computing : pick-up address, delivery address, depot
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        for(Request req : planningRequests) {
            listUsefulPoints.add(req.getPickupAddress());
            listUsefulPoints.add(req.getDeliveryAddress());
        }
        listUsefulPoints.add(depotAddress);

        for(Intersection startPoint : listUsefulPoints) {
            // gets the ends points useful for the computing according to the start point
            ArrayList<Intersection> listUsefulEndPoints = new ArrayList<>();
            for(Intersection endPoint : listUsefulPoints) {
                if(endPoint!=startPoint && !isRequest(startPoint,endPoint) && !(isPickUp(startPoint) && endPoint==depotAddress)) {
                    listUsefulEndPoints.add(endPoint);
                }
            }
            ArrayList<ShortestPath> shortestPathsFromStartPoint = dijkstra(adjacenceMap,listUsefulEndPoints, startPoint);
            dist.put(startPoint,shortestPathsFromStartPoint);
        }
        // call TSP with dist
    }

    private ArrayList<ShortestPath> dijkstra(Map<Intersection, ArrayList<Segment>> adjacenceMap, ArrayList<Intersection> listUsefulEndPoints, Intersection origin) {
        Map<Intersection, Double> dist = new HashMap<>();
        Map<Intersection, Intersection> parent = new HashMap<>();
        Map<Intersection, Integer> color = new HashMap<>(); // 0 = blanc, 1 = gris, 2 = noir
        ArrayList<ShortestPath> listShortestPathFromOrigin = new ArrayList<>();
        for (Intersection noeud : adjacenceMap.keySet()) {
            dist.put(noeud, Double.MAX_VALUE);
            parent.put(noeud, null);
            color.put(noeud, 0);
        }
        dist.replace(origin, (double) 0);
        color.replace(origin, 1);
        while (color.containsValue(1)) {
            List<Intersection> noeudsGris = color.entrySet().stream().filter(x -> x.getValue() == 1).map(Map.Entry::getKey).collect(Collectors.toList());
            Intersection noeudGrisAvecDistMin = dist.entrySet().stream().filter(x -> noeudsGris.contains(x.getKey())).min(Map.Entry.comparingByValue()).get().getKey();
            for (Intersection noeudAdj : adjacenceMap.get(noeudGrisAvecDistMin).stream().map(Segment::getDestination).collect(Collectors.toList())) {
                int colorNoeudAdj = color.get(noeudAdj);
                if (colorNoeudAdj == 0 || colorNoeudAdj == 1 ) {
                    relacher(noeudGrisAvecDistMin, noeudAdj, adjacenceMap.get(noeudGrisAvecDistMin).stream().filter(i -> i.getDestination() == noeudAdj).findFirst().get().getLength(),parent, dist);
                    if (colorNoeudAdj == 0) {
                        color.replace(noeudAdj, 1);
                    }
                }
            }
            color.replace(noeudGrisAvecDistMin, 2);
            if(listUsefulEndPoints.contains(noeudGrisAvecDistMin)) {
                // questions : comment récupérer les segments depuis ici ? Le faire après "relacher" plutôt ?
                Intersection tempoIntersection = noeudGrisAvecDistMin;
                ArrayList<Segment> listSegments = new ArrayList<>();
                while(parent.get(tempoIntersection)!=null) {
                    Intersection tmpParent = parent.get(tempoIntersection);
                    Intersection finalTempoIntersection = tempoIntersection;
                    listSegments.add(0,adjacenceMap.get(tmpParent).stream().filter(s -> s.getDestination() == finalTempoIntersection).findFirst().get());
                    tempoIntersection = tmpParent;
                }
                ShortestPath shortestPath = new ShortestPath(dist.get(noeudGrisAvecDistMin),listSegments,origin,noeudGrisAvecDistMin);
                listShortestPathFromOrigin.add((shortestPath));
                if(listShortestPathFromOrigin.size()==listUsefulEndPoints.size())
                    break;
            }
        }
        return listShortestPathFromOrigin;
    }

    public boolean isPickUp(Intersection startPoint) {
        boolean retour = false;
        for(Request req: planningRequests) {
            if(req.getPickupAddress()==startPoint) {
                retour = true;
                break;
            }
        }
        return retour;
    }

    public boolean isRequest(Intersection startPoint, Intersection endPoint) {
        boolean retour = false;
        for(Request req: planningRequests) {
            if(req.getDeliveryAddress() == endPoint && req.getPickupAddress()==startPoint) {
                retour = true;
                break;
            }
        }
        return retour;
    }

    private static void relacher(Intersection noeudInit, Intersection noeudDest, double cout, Map<Intersection, Intersection> parent, Map<Intersection, Double> dist) {
        if (dist.get(noeudDest) > dist.get(noeudInit) + cout) {
            dist.replace(noeudDest, dist.get(noeudInit) + cout);
            parent.replace(noeudDest, noeudInit);
        }
    }
}
