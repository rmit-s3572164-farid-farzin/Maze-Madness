package mazeSolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import maze.Cell;
import maze.Maze;
import static maze.Maze.NUM_DIR;
import static maze.Maze.oppoDir;

/**
 * Implements the WallFollower algorithm to solve an input maze
 * 
 * @author s3586372 Rahul Raghavan
 */
public class WallFollowerSolver implements MazeSolver {
        // Stores the set of all explored cells
        private HashSet exploredCells = new HashSet<>();
	
        // Stores the path of cells travelled by the explorer
        private LinkedList<Cell> exploredPath = new LinkedList<>();
        // Store the entrance and exit of the maze to calculate isSolved
        private Cell mazeEntrance = null;
        private Cell mazeExit = null;
        
        /**
         * WallFollower Algorithim
         * - The wallfollower algorithm works by picking a wall from the entrance
         * and following it to the end.
         * - The following steps guarantee that the right hand side wall is always 
         * followed when traversing the maze
         * 
         * 1. Find the wall from which the cell was entered into
         * 2. Iterate through all walls from the cell entrance in a clockwise manner
         * 3. Take the first opening, and move to the neighboring cell
         * 4. Repeat steps 1-3
         */        
	@Override
	public void solveMaze(Maze maze) {
            // Store entrance and exit to compute isSolved
            mazeEntrance = maze.entrance;
            mazeExit = maze.exit;
            
            // Starting cell
            Cell currCell = maze.entrance;
            int entranceDir = -1;
            
            // Iterate through all cells until the exit is reached
            while (currCell != maze.exit) {
                // Mark the current cell as visited
                maze.drawFtPrt(currCell);
                // Add cell to the list of all explored cells
                exploredCells.add(currCell);
                exploredPath.add(currCell);

                /**
                 * 1. Find the direction from which the first cell (entrance) 
                 * was entered into:
                 * - Find all walls of the cell
                 * - Pick the direction which has no wall present, but the neighbour
                 * in that direction is null
                 * - This is the direction from which the maze was entered into
                 */
                if(entranceDir == -1) {
                    for (int i=0; i<NUM_DIR; i++) {
                            // Check if direction is valid
                            if(currCell.wall[i] != null) {
                                // Check if neighbour does not exist and wall does not exist
                                if(currCell.neigh[i] == null && !currCell.wall[i].present) {
                                    // Choose this direction
                                    entranceDir = i;
                                    break;
                                }                                 
                            }
                    }                        
                }

                /**
                 * 2. Find an opening to travel through (i.e "following a wall")
                 * - Iterate through all directions (360 degrees), but leaving the 
                 * direction from where you entered the cell as the last iterated 
                 * value to ensure that backtracking is the option of last resort.
                 * 
                 * 3. Find the direction with the first "opening" - i.e where there
                 * is no wall present
                 * 
                 * This guarantees that a wall is always being followed, and if
                 * there is a wall break (or no wall to follow in a particular 
                 * cell - then always turn right.
                */
                // Iterate through all directions, including the starting 
                for (int i = entranceDir; i < entranceDir + NUM_DIR+1; i++) {
                    if (i != entranceDir) {
                        // Normalise the direction (to obtain a value between 0-6)
                        int trueDir = i%NUM_DIR;
                        // 3. Take the first opening, and travel to next cell
                        if(currCell.wall[trueDir] != null && currCell.neigh[trueDir] != null) {
                            // Check that wall does not exist
                            if(!currCell.wall[trueDir].present) {
                                // Move to the next cell
                                currCell = currCell.neigh[trueDir];
                                
                                // If the next cell is the exit - solver is complete
                                // So add the exit to the necessary data structures
                                if(currCell.equals(mazeExit)) {
                                    maze.drawFtPrt(currCell);
                                    exploredCells.add(currCell);
                                    exploredPath.add(currCell);                                    
                                }
                
                                // The entrance for the new cell is the opposite direction of travel
                                entranceDir = oppoDir[trueDir];
                                break;
                            }                        
                        }                        
                    }
                }
            }          
	} // end of solveMaze()
    
    
        /**
         * 
         * @return boolean solved if maze has been solved
         */
	@Override
	public boolean isSolved() {
                /**
                 * If there are no valid entrance and exit to the maze, the maze
                 * is not solved.
                 */
                if (mazeEntrance == null || mazeExit == null) {
                    return false;
                }
                /**
                 * If the first cell visited is not the entrance, the maze is
                 * not solved.
                 */
                if (!exploredPath.peek().equals(mazeEntrance)) {
                    return false;
                }
                /**
                 * Check if each cell is able to validly visit the next cell
                 */
                Cell currCell = null;
                Cell nextCell = null;
                while (exploredPath.size() > 0) {
                    currCell = exploredPath.pop();
                    nextCell = exploredPath.peek();
                    // Check if it is possible to traverse from current cell to the next
                    if(currCell != null && nextCell != null) {
                        boolean valid = false;
                        // Iterate through all directions
                        for (int i=0; i<NUM_DIR; i++) {
                            // If neighbour is found, check if there is a wall present
                            if(currCell.neigh[i] == nextCell) {
                                if (!currCell.wall[i].present) {
                                    valid = true;
                                }
                            }                                  
                        }
                        if (!valid) {
                            return false;
                        }
                    }
                }
                if (!currCell.equals(mazeExit)) {
                    return false;
                }
		return true;
	} // end if isSolved()
    

        /**
         * Returns the size of all unique cells explored while solving the maze.
         * This is computed by storing each cell visited in a HashSet which 
         * only returns the unique Cells.
         */        
	@Override
	public int cellsExplored() {
		return exploredCells.size();
	} // end of cellsExplored()

} // end of class WallFollowerSolver
