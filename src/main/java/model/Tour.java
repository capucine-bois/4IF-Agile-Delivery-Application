package model;

import observer.Observable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A tour is composed of a list of shortest paths. This list corresponds to all path which need to accomplish every request that must
 * be made during the same trip. It has access to these requests.
 * A tour also has a depot address where the delivery man starts and ends its travel, a tour length in meters,
 * which is the sum of the length of its paths and a departure's time.
 * As it extends Observable, an instance can notify observer when their attributes change.
 */
public class Tour extends Observable {

    /* ATTRIBUTES */

    // km/h
    private final double speed = 15;

    /**
     * The total length of the tour
     */
    private double tourLength;

    /**
     * The intersection corresponding to the depot
     */
    private Intersection depotAddress;

    /**
     * The time of starting for the tour
     */
    private String departureTime;

    private SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");

    private Calendar cal;


    /**
     * All requests the tour need to cover
     */
    private ArrayList<Request> planningRequests;

    /**
     * A list of shortest path which is used to print the best path
     */
    private ArrayList<ShortestPath> listShortestPaths;

    /* CONSTRUCTORS */

    /**
     * Constructor initializing the planning requests and the list of the shortest paths
     */
    public Tour() {
        planningRequests = new ArrayList<>();
        listShortestPaths = new ArrayList<>();
    }

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

    public SimpleDateFormat getParser() {
        return parser;
    }

    public Calendar getCal() {
        return cal;
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
        this.cal = Calendar.getInstance();
        try {
            this.cal.setTime(parser.parse(departureTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlanningRequests(ArrayList<Request> planningRequests) {
        this.planningRequests = planningRequests;
    }


    public void setListShortestPaths(ArrayList<ShortestPath> listShortestPaths) {
        this.listShortestPaths = listShortestPaths;
    }

    public void setParser(SimpleDateFormat parser) {
        this.parser = parser;
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    /* METHODS */

    /**
     * Adding a request in the planning requests.
     * @param request the request to add
     */
    public void addRequest(Request request) {
        planningRequests.add(request);
    }

    /**
     * Clear lists in tour
     */
    public void clearLists() {
        planningRequests.clear();
        listShortestPaths.clear();
    }

    /**
     * Method which calls dijkstra method, creates a graphe according to the result of dijkstra and then calls the TSP method
     * @param allIntersectionsList the list with all intersections of the map
     */
    public void  computeTour(List<Intersection> allIntersectionsList) {

        ArrayList<Node> listNodes = new ArrayList<>();
        // get the points useful for the computing : pick-up address, delivery address, depot
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        for(Request req : planningRequests) {
            listUsefulPoints.add(req.getPickupAddress());
            listUsefulPoints.add(req.getDeliveryAddress());
        }
        listUsefulPoints.add(depotAddress);

        int iteratorNumber = 1;
        for(Intersection startPoint : listUsefulPoints) {
            // gets the ends points useful for the computing according to the start point
            ArrayList<Intersection> listUsefulEndPoints = new ArrayList<>();
            for(Intersection endPoint : listUsefulPoints) {
                //if(endPoint!=startPoint && isPossiblePath(startPoint,endPoint) && !(isPickUp(startPoint) && endPoint==depotAddress) && !(startPoint==depotAddress && isDelivery(endPoint))) {
                if(isPossiblePath(startPoint,endPoint)) {
                    listUsefulEndPoints.add(endPoint);
                }
            }
            ArrayList<ShortestPath> shortestPathsFromStartPoint = dijkstra(allIntersectionsList,listUsefulEndPoints, startPoint);
            Node nodeToAdd = null;
            if(startPoint!=depotAddress) {
                nodeToAdd = new Node(startPoint,shortestPathsFromStartPoint,iteratorNumber);
                listNodes.add(nodeToAdd);
                iteratorNumber++;
            } else {
                listNodes.add(0,new Node(depotAddress,shortestPathsFromStartPoint,0));
            }
        }



        // Run Tour
        TSP tsp = new TSP1();
        Graph g = new CompleteGraph(listNodes, this);
        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, g);
        this.setTourLength(tsp.getSolutionCost());

        // print the cost of the solution
        System.out.print("Solution of cost "+this.tourLength+" found in "
                +(System.currentTimeMillis() - startTime)+"ms : ");

        // print the solution with number which correspond to the order in the planning request
        Integer[] intersectionsOrder = tsp.getBestSol();
        for(int i = 0; i<intersectionsOrder.length; i++) {
            System.out.print(intersectionsOrder[i] + "  ");
        }
        System.out.println("0");


        Intersection previous = null;
        for (Integer index: intersectionsOrder) {
            Intersection currentIntersection;
            if (index == 0)
                currentIntersection = listUsefulPoints.get(listUsefulPoints.size()-1);
            else
                currentIntersection = listUsefulPoints.get(index-1);
            if (previous != null) {
                Intersection finalPrevious = previous;
                for (ShortestPath p: listNodes.stream().filter(x -> x.getIntersection() == finalPrevious).findFirst().get().getListArcs()) {
                    if (p.getEndAddress().equals(currentIntersection))
                        listShortestPaths.add(p);
                }
            }
            previous = currentIntersection;
        }

        Intersection finalPrevious1 = previous;
        for (ShortestPath p: listNodes.stream().filter(x -> x.getIntersection() == finalPrevious1).findFirst().get().getListArcs()) {
            if (p.getEndAddress().equals(depotAddress))
                listShortestPaths.add(p);
        }

        /*

        /*
        // print order
        long intm;
        for (ShortestPath p: listShortestPaths) {
            System.out.println(p.getStartAddress().getId() + " -> " +
                    p.getEndAddress().getId());
            intm = p.getStartAddress().getId();
            System.out.print( intm );
            for(Segment s : p.getListSegments()){
                System.out.print(" -> " );
                intm = s.getDestination().getId();
                System.out.print( intm );
            }
            System.out.println();
        }*/


        notifyObservers();
    }


    /**
     * Algorithm dijkstra : compute all shortest paths between a point and the points in the list "listUsefulEndPoints"
     * @param listIntersections the list with all intersections
     * @param listUsefulEndPoints the intersections which can be reached by the origin Intersection (for example a delivery address cannot reach his pick-up address)
     * @param origin the intersection from which we search the shortest paths
     * @return listShortestPathFromOrigin, the list of shortest paths from the origin to the intersection in the list of useful end points
     */
    ArrayList<ShortestPath> dijkstra(List<Intersection> listIntersections, ArrayList<Intersection> listUsefulEndPoints, Intersection origin) {
        ArrayList<ObjectDijkstra> listDijkstra = new ArrayList<>();
        ArrayList<ShortestPath> listShortestPathFromOrigin = new ArrayList<>();
        for (Intersection noeud : listIntersections) {
            ObjectDijkstra object = new ObjectDijkstra(noeud,null,Double.MAX_VALUE,0);
            listDijkstra.add(object);
        }

        listDijkstra.stream().filter(x -> x.getIntersection()==origin).findFirst().get().setDist(0.0);
        listDijkstra.stream().filter(x -> x.getIntersection()==origin).findFirst().get().setColor(1);

        while (listDijkstra.stream().anyMatch(x -> x.getColor() == 1)) {
            ObjectDijkstra noeudGrisAvecDistMin = listDijkstra.stream().filter(x-> x.getColor() == 1).min(Comparator.comparing(ObjectDijkstra::getDist)).get();
            List<ObjectDijkstra> listDest = listDijkstra.stream()
                    .filter(x -> noeudGrisAvecDistMin.getIntersection().getAdjacentSegments()
                            .stream().map(Segment::getDestination).collect(Collectors.toList()).contains(x.getIntersection())).collect(Collectors.toList());

            for (ObjectDijkstra noeudAdj :listDest) {

                int colorNoeudAdj = noeudAdj.getColor();
                if (colorNoeudAdj == 0 || colorNoeudAdj == 1 ) {
                    double length = noeudGrisAvecDistMin.getIntersection().getAdjacentSegments().stream().filter(x -> x.getDestination() == noeudAdj.getIntersection()).findFirst().get().getLength();
                    relax(noeudGrisAvecDistMin, noeudAdj, length,listDijkstra);
                    if (colorNoeudAdj == 0) {
                        noeudAdj.setColor(1);
                    }
                }
            }
            noeudGrisAvecDistMin.setColor(2);
            if(listUsefulEndPoints.contains(noeudGrisAvecDistMin.getIntersection())) {
                ObjectDijkstra tempoIntersection = noeudGrisAvecDistMin;
                ArrayList<Segment> listSegments = new ArrayList<>();
                while(findParent(tempoIntersection,listDijkstra)!=null) {
                    ObjectDijkstra finalTempoIntersection1 = tempoIntersection;
                    ObjectDijkstra tmpParent = finalTempoIntersection1.getParent();
                    listSegments.add(0,tmpParent.getIntersection().getAdjacentSegments().stream().filter(x -> x.getDestination() == finalTempoIntersection1.getIntersection()).findFirst().get());
                    tempoIntersection = tmpParent;
                }
                ShortestPath shortestPath = new ShortestPath(noeudGrisAvecDistMin.getDist(),listSegments,origin,noeudGrisAvecDistMin.getIntersection());
                listShortestPathFromOrigin.add((shortestPath));
                if(listShortestPathFromOrigin.size()==listUsefulEndPoints.size())
                    break;
            }
        }
        return listShortestPathFromOrigin;
    }




    /**
     * Return true if the two intersections in parameters make a possible path
     * @param startPoint the beginning of the segment
     * @param endPoint the end of a segment begin by startPoint
     * @return true if the path is possible, false otherwise
     */
    public boolean isPossiblePath(Intersection startPoint, Intersection endPoint) {
        boolean retour = true;
        for(Request req: planningRequests) {
            // case where the path is not possible : the start and the end are the same, the start is a pick-up address and the end
            // is the depot, the start is the depot and the end a delivery address, the start is a delivery address and the end is the
            // pick-up links to this delivery address
            if((req.getDeliveryAddress() == startPoint && req.getPickupAddress()==endPoint)
                    || (startPoint==depotAddress && req.getDeliveryAddress()==endPoint)
                    || (req.getPickupAddress()==startPoint && endPoint==depotAddress)
                    || startPoint==endPoint) {
                retour = false;
                break;
            }
        }
        return retour;
    }

    /**
     * Check if the path is shorter
     * @param noeudInit the node which contains the intersection where the segment begins
     * @param noeudDest the node which contains the intersection where the segment ends
     * @param cost the cost of the segment
     * @param listDijkstra the list of all dijkstra objects with their infos (the Intersection with its cost, parent and color)
     */
    private void relax(ObjectDijkstra noeudInit, ObjectDijkstra noeudDest, double cost, ArrayList<ObjectDijkstra> listDijkstra) {
        if(noeudDest.getDist() > noeudInit.getDist() + cost) {
            listDijkstra.stream().filter(x -> x==noeudDest).findFirst().get().setDist(noeudInit.getDist() + cost);
            listDijkstra.stream().filter(x -> x==noeudDest).findFirst().get().setParent(noeudInit); }
    }

    /**
     * Return the parent of the intersection of the dijkstra object in parameter
     * @param intersectionToFind the dijkstra object which contains the intersection we want to get the parent of
     * @param listDijkstra the list of all ObjectDijkstra
     * @return the parent of the intersection
     */
    public ObjectDijkstra findParent(ObjectDijkstra intersectionToFind, ArrayList<ObjectDijkstra> listDijkstra) {
        for(ObjectDijkstra obj : listDijkstra) {
            if(obj==intersectionToFind) {
                return obj.getParent();
            }
        }
        return null;
    }

    /**
     * This inner class groups an intersection and its distance to the origin, its color and its parent (which is a dijkstra object so we also get the parent's distance and color and its parent itself)
     */
    class ObjectDijkstra {
        private Intersection intersection;
        private ObjectDijkstra parent;
        private Double dist;
        private Integer color;

        public Intersection getIntersection() {
            return intersection;
        }

        public void setIntersection(Intersection intersection) {
            this.intersection = intersection;
        }

        public ObjectDijkstra getParent() {
            return parent;
        }

        public void setParent(ObjectDijkstra parent) {
            this.parent = parent;
        }

        public Double getDist() {
            return dist;
        }

        public void setDist(Double dist) {
            this.dist = dist;
        }

        public Integer getColor() {
            return color;
        }

        public void setColor(Integer color) {
            this.color = color;
        }


        /**
         * Constructor initialize all parameter of the dijkstra object
         * @param intersection
         * @param parent
         * @param dist
         * @param color
         */
        public ObjectDijkstra(Intersection intersection, ObjectDijkstra parent, Double dist, Integer color) {
            this.intersection = intersection;
            this.parent = parent;
            this.dist = dist;
            this.color = color;
        }


    }

    /**
     * Check if two tours have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof Tour) {
            Tour t = (Tour) o;
            check = t.getDepotAddress().equals(this.getDepotAddress()) &&
                    t.getTourLength() == this.getTourLength() &&
                    t.getDepartureTime() == this.getDepartureTime() &&
                    t.getPlanningRequests().equals(this.getPlanningRequests()) &&
                    t.getListShortestPaths().equals(this.getListShortestPaths());
        } else {
            check = false;
        }
        return check;
    }

    public double metersToSeconds(double meters) {
        return (meters/(speed*1000))*60*60;
    }
}
