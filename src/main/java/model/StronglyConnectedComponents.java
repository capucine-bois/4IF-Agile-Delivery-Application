package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of an algorithm to detect the strongly connected components
 */
public class StronglyConnectedComponents {

    public StronglyConnectedComponents () {
    }

    /**
     * Check if the two intersections in parameter are in the same strongly connected part
     * @param listIntersection the list of all intersections
     * @return true if there are in the same strongly connected part, false otherwise
     */
    public static ArrayList<Intersection> getAllUnreachableIntersections(ArrayList<Intersection> listIntersection, Intersection depot, ArrayList<Request> planning) {

        ArrayList<Intersection> intersectionsNotWithDepot = new ArrayList<>();
        ArrayList<Integer> [] listVertex = new ArrayList[listIntersection.size()];
        Integer [] colorDFSnum = new Integer[listIntersection.size()];
        Integer [] num  = foretDFSnum(listIntersection, listVertex, colorDFSnum);
        List<Integer>[] graphTranspose = getTranspose(listIntersection);

        Integer [] color = new Integer[graphTranspose.length];
        for(int i=0; i<color.length; i++) {
            color[i] = 0;
        }

        for(int j = num.length-1; j>=0; j--) {
            ArrayList<Integer> set = new ArrayList<>();

            if(color[num[j]]==0) {
                // map with the id of an intersection and the color
                Map<Integer, Integer> B = new HashMap<>();
                for(int k=0; k<color.length; k++) {
                    if(color[k]==0) {
                        B.put(k,0);
                    }
                }


                DFSrec(graphTranspose, num[j], color, B);
                set = (ArrayList<Integer>) B.keySet().stream().filter(x -> B.get(x)==2).collect(Collectors.toList());
                if(set.contains((int)depot.getId())) {
                    checkIntersectionsWithDepot(set,intersectionsNotWithDepot, planning);
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
     * @param planning the planning request
     */
    private static void checkIntersectionsWithDepot(ArrayList<Integer> scc, ArrayList<Intersection> intersectionsNotWithDepot, ArrayList<Request> planning) {
        for(Request req : planning) {
            if(!scc.contains((int)req.getDeliveryAddress().getId()))
                intersectionsNotWithDepot.add(req.getDeliveryAddress());
            if(!scc.contains((int)req.getPickupAddress().getId()))
                intersectionsNotWithDepot.add(req.getPickupAddress());
        }
    }

    /**
     * Return an array with a predecessor for each intersection
     * @param listIntersections list of all intersections
     * @param listVertex an empty list to fill
     * @param color an array with the color of the vertex
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
     * @param listIntersection
     * @return an array of list of successor
     */
    public static List<Integer>[] getTranspose(ArrayList<Intersection> listIntersection)
    {
        int V = listIntersection.size();
        List<Integer>[] g = new List[V];
        for (int i = 0; i < V; i++)
            g[i] = new ArrayList<Integer>();
        for (int v = 0; v < V; v++)
            for (int i = 0; i < listIntersection.get(v).getAdjacentSegments().size(); i++)
                g[(int)listIntersection.get(v).getAdjacentSegments().get(i).getDestination().getId()].add(v);
        return g;
    }

    /**
     * Put in black the vertex of the graph for a same strongly connected component
     * @param graph the transpose graph
     * @param vertex
     * @param color the array with color of each vertex
     */
    public static void DFSrec(List<Integer> [] graph, int vertex, Integer [] color,  Map<Integer, Integer> B) {
        color[vertex] = 1;
        B.replace(vertex, 1);
        for(int i=0; i<graph[vertex].size(); i++) {
            if(color[graph[vertex].get(i)]==0) {
                DFSrec(graph, graph[vertex].get(i),color, B);
            }
        }
        color[vertex] = 2;
        B.replace(vertex, 2);
    }
}
