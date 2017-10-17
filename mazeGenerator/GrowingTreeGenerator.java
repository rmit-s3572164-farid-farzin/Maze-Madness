package mazeGenerator;

import java.util.*;

import maze.Maze;
import maze.Cell;
import static maze.Maze.oppoDir;

/**
 * Generate a maze using a growing tree generator using a mixture of two different
 * strategies used to select a cell to pick from the travel set. The ratio is specified
 * by the threshold value.
 * 
 * Strategy 1 involves selecting the newest cell added to the travel set.
 * Strategy 2 involves selecting a random cell from the travel set.
 * 
 * @author Huligan
 */
public class GrowingTreeGenerator implements MazeGenerator {
	// Growing tree maze generator. As it is very general, here we implement as "usually pick the most recent cell, but occasionally pick a random cell"
	
	double threshold = 0.1;
	
        /**
         * Method to generate the maze using the mixture of strategies for growing tree
         * @param maze 
         */
	@Override
	public void generateMaze(Maze maze) {
            // Get the maze entrance
            Cell currCell = maze.entrance;
            
            /**
             * Setup the travel cells - which is the set from which the cells to 
             * travel to next are picked.
             * 
             * The travel set is implemented as a stack data type in order to
             * easily retrieve the last added cell to the data structure in order
             * to perform strategy 1 cell selection. The nomenclature "travel set"
             * and "travel stack" are used interchangeably in this implementation.
             */
            Stack<Cell> travelCells = new Stack<>();
            HashSet<Cell> visitedCells = new HashSet<>();
            
            // Add the entrance to the travel set, and set this as the last added cell
            Cell lastAddedCell = currCell;
            travelCells.push(currCell);
            
            /**
             * Keep selecting a cell from the travel set using either of the two
             * strategies until the set is empty
             */
            while(!travelCells.isEmpty()) {
                // Mark the current cell as visited
                maze.drawFtPrt(currCell);
                visitedCells.add(currCell);

                /** 
                 * Select a cell to travel to from the travel set
                 * The method of selection takes two forms:
                 * 1. Select a random cell from the travel set
                 * 2. Select the last cell added to the travel set
                 * 
                 */
                // 1. Select method #1 based on a probability specified by the threshold value
                if(new java.util.Random().nextInt((int)(threshold*100.00)) == 0) {
                    // Select a random cell from the travel set
                    int nextIndex = new java.util.Random().nextInt(travelCells.size());
                    currCell = travelCells.get(nextIndex);                 
                }
                // 2. In all other cases, Select using method #2 (latest cell)
                else {
                    // Pick the last added cell to the travel stack
                    currCell = travelCells.peek();
                }
                
                // Iterate through all neighbours of the selected cell
                HashMap<Cell, Integer> neighbours = new HashMap<>();
                for (int i=0; i<currCell.neigh.length; i++) {
                    // Check that the neighbour is valid and exists
                    if(currCell.neigh[i] != null) {
                        /**
                         * If the neighbour has not been visited before, add
                         * it to the list of neighbours.
                         */                            
                        if (!visitedCells.contains(currCell.neigh[i])) {
                            neighbours.put(currCell.neigh[i], i);
                        }
                    }
                }

                // Select a random neighbour of the cell to travel to   
                if(!neighbours.isEmpty()) {
                    // Get all the neighbours of the cell in a random accessible 
                    // data structure - array list
                    ArrayList<Cell> neighbourCells = new ArrayList<>();
                    neighbourCells.addAll(neighbours.keySet());
                    
                    // Select a neighbour at random 
                    Random rand = new Random();
                    Cell neighbour = neighbourCells.get(rand.nextInt(neighbours.size()));
                    
                    // Get direction of selected neighbour
                    int dir = neighbours.get(neighbour);
                    // Carve a path to the selected neighbour
                    currCell.wall[dir].present = false;
                    currCell.neigh[dir].wall[oppoDir[dir]].present = false;
                    
                    // Add the newly selected neighbour to the travel set
                    travelCells.push(currCell.neigh[dir]);
                    // Mark the newly selected neighbour as visited for future reference
                    visitedCells.add(currCell.neigh[dir]);
                }
                else {
                    // All neighbours have been visited for this cell
                    // Remove cell from the travel set
                    travelCells.remove(currCell);
                }
            }

	}

}
