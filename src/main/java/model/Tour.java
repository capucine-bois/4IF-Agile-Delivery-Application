package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tour {

    /* ATTRIBUTES */

    private double tourLength;
    private Intersection depotAddress;
    private String departureTime;
    private ArrayList<Request> planningRequests;
    private ArrayList<ShortestPath> listShortestPaths;

    /* CONSTRUCTORS */

    public Tour(){}

    /* GETTERS */

    public double getTourLength() {
        return tourLength;
    }

    public Intersection getDepotAddress() {
        return depotAddress;
    }

    public String getDepartureTime() {
        return departureTime;
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

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setPlanningRequests(ArrayList<Request> planningRequests) {
        this.planningRequests = planningRequests;
    }

    public void setListShortestPaths(ArrayList<ShortestPath> listShortestPaths) {
        this.listShortestPaths = listShortestPaths;
    }

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
