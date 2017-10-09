package mazeSolver;

import java.util.HashMap;
import maze.Cell;
import maze.Maze;
import static maze.Maze.NUM_DIR;
import static maze.Maze.oppoDir;


/**
 * Implements WallFollowerSolver
 */

public class WallFollowerSolver implements MazeSolver {
	
        /**
         * WallFollower Algorithim
         * The following steps guarantee that the right hand side wall is always 
         * followed when traversing the maze
         * 
         * 1. Find the wall from which the cell was entered into
         * 2. Iterate through all walls from the cell entrance in a clockwise manner
         * 3. Take the first opening, and move to the neighboring cell
         * 4. Repeat steps 1-4
         */        
	@Override
	public void solveMaze(Maze maze) {
            // TODO Auto-generated method stub

            // Starting cell
            Cell currCell = maze.entrance;

            int entranceDir = -1;
            while (currCell != maze.exit) {

                // Mark the current cell as visited
                maze.drawFtPrt(currCell);

                // 1. Find the direction from which the cell was entered
                // When cell is the entrance of the maze
                if(entranceDir == -1) {
                    for (int i=0; i<NUM_DIR; i++) {
                            if(currCell.wall[i] != null) {
                                if(currCell.neigh[i] == null && !currCell.wall[i].present) {
                                    entranceDir = i;
                                    break;
                                }                                 
                            }
                    }                        
                }

                // 2. Find an opening to travel through (i.e "following a wall")
                for (int i = entranceDir; i < entranceDir + NUM_DIR+1; i++) {
                    if (i != entranceDir) {
                        int trueDir = i%NUM_DIR;
                        // 3. Take the first opening, and travel to next cell
                        if(currCell.wall[trueDir] != null && currCell.neigh[trueDir] != null) {
                            if(!currCell.wall[trueDir].present) {
                                currCell = currCell.neigh[trueDir];
                                entranceDir = oppoDir[trueDir];
                                break;
                            }                        
                        }                        
                    }
                }
            }          
	} // end of solveMaze()
    
    
	@Override
	public boolean isSolved() {
		// TODO Auto-generated method stub
		return false;
	} // end if isSolved()
    
    
	@Override
	public int cellsExplored() {
		// TODO Auto-generated method stub
		return 0;
	} // end of cellsExplored()

} // end of class WallFollowerSolver
