package mazeGenerator;

import java.util.*;

import maze.Maze;
import maze.Cell;
import static maze.Maze.oppoDir;

public class GrowingTreeGenerator implements MazeGenerator {
	// Growing tree maze generator. As it is very general, here we implement as "usually pick the most recent cell, but occasionally pick a random cell"
	
	double threshold = 0.1;
	
	@Override
	public void generateMaze(Maze maze) {
            Cell currCell = maze.entrance;
            
            // setup travel cells
            Stack<Cell> travelCells = new Stack<>();
            HashSet<Cell> visitedCells = new HashSet<>();
            Cell lastAddedCell = currCell;
            travelCells.push(currCell);
             
            while(!travelCells.isEmpty()) {
                // Mark the current cell as visited
                maze.drawFtPrt(currCell);
                visitedCells.add(currCell);
                System.out.println("r:" + currCell.r + " c:"+currCell.c);
                System.out.println(travelCells.size());
                // Pick a cell from the travel set to visit
                currCell = travelCells.pop();
                
                // Iterate through all neighbours of the selected cell
                HashMap<Cell, Integer> neighbours = new HashMap<>();
                boolean carved = false;
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
                    
                    // Add the neighbour to the travel set
                    travelCells.push(currCell);
                    travelCells.push(currCell.neigh[dir]);
                }
                else {
                    // All neighbours have been visited for this cell
                    // Remove cell from the travel set
                    System.out.println("reoved");
                    travelCells.remove(currCell);
                }
            }

	}

}
