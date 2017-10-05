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
		// Create visited set
		HashSet<Cell> visited = new HashSet<>();
		// Create frontier set
		HashSet<Cell> frontier = new HashSet<>();
                // Create duplicate frontier set for random access
                LinkedList<Cell> frontierList = new LinkedList<>();
                
                // Add entrance to visited set
		Cell entrance = maze.entrance;
		visited.add(entrance);
                
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

                // Build maze
                while(!frontier.isEmpty()) {
                    // Select cell from frontier set
                    Random rand = new Random();
                    //Cell next = frontier.iterator().next();
                    Cell next = frontierList.get(rand.nextInt(frontier.size()));
                    System.out.println("r: " + next.r + " c: " + next.c);
                    System.out.println("frontier: ");
                    for(Cell cell : frontierList) {
                        System.out.print(","+cell.r+" "+cell.c+",");                        
                    }
                    // Add neighbours of selected cell to frontier set
                    boolean carved = false;
                    for (int i=0; i<next.neigh.length; i++) {
                        if(next.neigh[i] != null) {
                            System.out.println("r: " + next.neigh[i].r + " i: "+i + " c: " + next.neigh[i].c);
                            if (!visited.contains(next.neigh[i])) {
                                if(!frontier.contains(next.neigh[i])) {
                                    frontier.add(next.neigh[i]);
                                    frontierList.add(next.neigh[i]);                                    
                                }
                            }
                            else {
                                // Carve a path to create a new link in the maze
                                if (!carved) {
                                    next.wall[i].present = false;
                                    next.neigh[i].wall[oppoDir[i]].present = false;
                                    carved = true;                               
                                }
                            }
                        }
                    }
                    if (carved) {
                        frontier.remove(next);
                        frontierList.remove(next);
                        visited.add(next);
                    }
                }
	} // end of generateMaze()

} // end of class ModifiedPrimsGenerator
