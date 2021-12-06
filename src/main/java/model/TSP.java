package model;

/**
 * The implementation of TSP
 */
public interface TSP {
    /**
     * Search for the shortest cost hamiltonian circuit in <code>g</code> within <code>timeLimit</code> milliseconds
     * (returns the best found tour whenever the time limit is reached)
     * Warning: The computed tour always start from vertex 0
     * @param timeLimit the max computation time
     * @param g the graph with all the costs
     */
    void searchSolution(int timeLimit, Graph g, Tour tour);

    /**
     * @return the total cost of the solution computed by <code>searchSolution</code>
     * (-1 if <code>searchSolution</code> has not been called yet).
     */
    double getSolutionCost();

    Integer[] getBestSol();
}
