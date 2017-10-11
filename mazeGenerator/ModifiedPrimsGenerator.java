package mazeGenerator;

import maze.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import static maze.Maze.deltaC;
import static maze.Maze.deltaR;
import static maze.Maze.oppoDir;
import static maze.Maze.NUM_DIR;

/**
 * Generate a maze using a modified Prim's algorithm
 * 
 * @author s3586372 Rahul Raghavan
 */
public class ModifiedPrimsGenerator implements MazeGenerator {

        /**
         * Method to check if cell is within the maze
         * @param maze
         * @param r row index
         * @param c column index
         * @return 
         */
        private boolean isIn(Maze maze, int r, int c) {
            return r >= 0 && r < maze.sizeR && c >= 0 && c < maze.sizeC;
        }

        /**
         * Method to generate the maze using Prim's
         * @param maze 
         * 
         * Initiate/Setup Maze Generation:
         * ===============================
         * 1: Start with empty visited set
         * 2: Add entrance to visited set
         * 3: Add neighbours of entrance to frontier set
         * 
         * Generate Maze
         * =============
         * 4: Pick a cell in the frontier set at random
         * 5: Pick a neighbour of the frontier set within the visited set
         * 6: carve a path between the frontier set cell and the visited set cell
         * 7: repeat 4-6 until the frontier set is empty
         * 
         */
	@Override
	public void generateMaze(Maze maze) {
                /**
                 * Initiation Steps:
                 * - Setup frontier set and visited set to compute Prim's
                 * algorithm.
                 * - Compute the algorithm for the first Cell - the entrance
                 */
		// Create visited set
		HashSet<Cell> visited = new HashSet<>();
		// Create frontier set
		HashSet<Cell> frontier = new HashSet<>();
                // Create duplicate frontier set for random access
                LinkedList<Cell> frontierList = new LinkedList<>();
                
                // Add entrance to visited set
		Cell entrance = maze.entrance;
		visited.add(entrance);
                
                // Carve a path from outside the maze to the entrance
                for (int i=0; i<NUM_DIR; i++) {
                    if (!isIn(maze, entrance.r + deltaR[i], entrance.c + deltaC[i]) && entrance.wall[i] != null && entrance.wall[i].present) {
                        entrance.wall[i].present = false;
                    }
                }
                
                // Add neighbours of entrance to frontier set
                for(Cell cell : entrance.neigh) {
                    // If cell is within the maze
                    if (cell != null) {                     
                        if (isIn(maze, cell.r, cell.c)) {
                            if(!frontier.contains(cell)) {
                                frontier.add(cell);
                                frontierList.add(cell);                                
                            }
                        }
                    }
                }

                /**
                 * Maze Generation Steps:
                 * - Run Prim's algorithm for all cells, while carving a path from
                 * the current cell to the next (by removing the wall between
                 * the cells)
                 */
                while(!frontier.isEmpty()) {
                    // Select a random cell from frontier set
                    Random rand = new Random();
                    Cell next = frontierList.get(rand.nextInt(frontier.size()));
                    
                    // Add neighbours of selected cell to frontier set
                    boolean carved = false;
                    // Iterate through all neighbours of the selected cell
                    for (int i=0; i<next.neigh.length; i++) {
                        // Check that the neighbour is valid and exists
                        if(next.neigh[i] != null) {
                            /**
                             * If the neighbour has not been visited before, add
                             * it to the frontier set
                             */                            
                            if (!visited.contains(next.neigh[i])) {
                                // Check that the neighbour is not already in the frontier set
                                if(!frontier.contains(next.neigh[i])) {
                                    // Add the neighbour to the frontier set
                                    frontier.add(next.neigh[i]);
                                    frontierList.add(next.neigh[i]);                                    
                                }
                            }
                            /**
                             * If the neighbour has been visited, carve a path 
                             * to it
                             **/
                            else {
                                /**
                                 * Check if we have already carved a path from this cell
                                 * - There should only be one oath out of a cell
                                 * in order for the maze to be perfect.
                                 */
                                if (!carved) {
                                    next.wall[i].present = false;
                                    next.neigh[i].wall[oppoDir[i]].present = false;
                                    carved = true;                               
                                }
                            }
                        }
                    }
                    /**
                     * If a path was successfully carved from the randomly 
                     * selected cell to one of its neighbours - remove it from 
                     * the frontier set and add it to the visited set.
                     */
                    if (carved) {
                        frontier.remove(next);
                        frontierList.remove(next);
                        visited.add(next);
                    }
                }
	} // end of generateMaze()

} // end of class ModifiedPrimsGenerator
