package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of an algorithm to detect the strongly connected components
 */
public class StronglyConnectedComponents {

    public StronglyConnectedComponents () {
    }

    /**
     * Check if the intersections present in the planning are reachable
     * @param listIntersection the list of all intersections
     * @param depot the depot (start and end) address
     * @param intersectionsToTest the list of intersections in requests
     * @return true if there are in the same strongly connected part, false otherwise
     */
    public static ArrayList<Intersection> getAllUnreachableIntersections(ArrayList<Intersection> listIntersection, Intersection depot, ArrayList<Intersection> intersectionsToTest) {

        ArrayList<Intersection> intersectionsNotWithDepot = new ArrayList<>();
        ArrayList<Integer> [] listVertex = new ArrayList[listIntersection.size()];
        Integer [] colorDFSnum = new Integer[listIntersection.size()];
        Integer [] num  = foretDFSnum(listIntersection, listVertex, colorDFSnum);
        List<Integer>[] graphTranspose = getTranspose(listIntersection);

        Integer [] color = new Integer[graphTranspose.length];
        Arrays.fill(color, 0);

        for(int j = num.length-1; j>=0; j--) {
            ArrayList<Integer> set;

            if(color[num[j]]==0) {
                // map with the id of an intersection and the color
                Map<Integer, Integer> colorMap = new HashMap<>();
                for(int k=0; k<color.length; k++) {
                    if(color[k]==0) {
                        colorMap.put(k,0);
                    }
                }


                DFSrec(graphTranspose, num[j], color, colorMap);
                set = (ArrayList<Integer>) colorMap.keySet().stream().filter(x -> colorMap.get(x)==2).collect(Collectors.toList());
                if(set.contains((int)depot.getId())) {
                    checkIntersectionsWithDepot(set,intersectionsNotWithDepot, intersectionsToTest);
                    break;
                }
            }
        }
        return intersectionsNotWithDepot;
    }

    /**
     * Check if all the intersections of the planning are in the same strongly connected components of the depot
     * @param scc the strongly connected component which contains the depot
     * @param intersectionsNotWithDepot the list we fill with all intersection present in the planning but not in scc
     * @param intersectionsToTest the list containing the intersections of a, or more, request
     */
    private static void checkIntersectionsWithDepot(ArrayList<Integer> scc, ArrayList<Intersection> intersectionsNotWithDepot, ArrayList<Intersection> intersectionsToTest) {
        for(Intersection intersection : intersectionsToTest) {
            if(!scc.contains((int)intersection.getId()))
                intersectionsNotWithDepot.add(intersection);
        }
    }

    /**
     * Return an array with a predecessor for each intersection
     * @param listIntersections list of all intersections
     * @param listVertex an empty list to fill
     * @param color an array with the color of the vertices
     * @return a list of dijkstra object (thus each will have his parent)
     */
    public static Integer[] foretDFSnum(ArrayList<Intersection> listIntersections, ArrayList<Integer> [] listVertex, Integer [] color) {
        Integer [] num = new Integer[listIntersections.size()];
        int index = 0;
        for(Intersection intersection : listIntersections) {
            listVertex[index] = new ArrayList<>();
            for(int i=0; i<intersection.getAdjacentSegments().size(); i++) {
                listVertex[index].add((int)intersection.getAdjacentSegments().get(i).getDestination().getId());
            }
            color[index] = 0;
            index++;
            num[(int)intersection.getId()] = 0;
        }
        int cpt = 1;
        for(int j=0; j<listVertex.length; j++) {
            if(color[j] == 0) {
                cpt = DFSrecNUM(listVertex, color, j, num, cpt);
                cpt++;
            }
        }
        return num;
    }

    /**
     * Depth first search to put a parent for each intersection
     * @param listVertex list of all vertex
     * @param origin the vertex which we look the successor
     * @param color the array with colors of vertex
     * @param cpt the number to put in num
     * @param num the array with the order of transition to black
     */
    public static int DFSrecNUM(List<Integer> [] listVertex, Integer [] color, int origin, Integer [] num, int cpt) {
        color[origin] = 1;
        for(Integer successor : listVertex[origin]) {
            if(color[successor] == 0) {
                //objTmp.setParent(origin);
                cpt = DFSrecNUM(listVertex, color, successor,num,cpt);
                cpt++;
            }
        }
        color[origin] = 2;
        num[cpt-1] = origin;
        return cpt;
    }


    /**
     * function to get transpose of graph
     * @param listIntersection list of all intersections
     * @return an array of list of successor
     */
    public static List<Integer>[] getTranspose(ArrayList<Intersection> listIntersection)
    {
        int V = listIntersection.size();
        List<Integer>[] g = new List[V];
        for (int i = 0; i < V; i++)
            g[i] = new ArrayList<>();
        for (int v = 0; v < V; v++)
            for (int i = 0; i < listIntersection.get(v).getAdjacentSegments().size(); i++)
                g[(int)listIntersection.get(v).getAdjacentSegments().get(i).getDestination().getId()].add(v);
        return g;
    }

    /**
     * Put in black the vertices of the graph that are reachable from origin
     * Is used to blacken the SCC once other vertices are removed
     * @param graph the transposed graph
     * @param origin entry point of the DFS
     * @param color the array with color of each vertex
     * @param colorMap a map with the id of each intersection and its color
     */
    public static void DFSrec(List<Integer> [] graph, int origin, Integer [] color,  Map<Integer, Integer> colorMap) {
        color[origin] = 1;
        colorMap.replace(origin, 1);
        for(int i=0; i<graph[origin].size(); i++) {
            if(color[graph[origin].get(i)]==0) {
                DFSrec(graph, graph[origin].get(i),color, colorMap);
            }
        }
        color[origin] = 2;
        colorMap.replace(origin, 2);
    }
}
