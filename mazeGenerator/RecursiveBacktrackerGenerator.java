package mazeGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import maze.Cell;
import maze.Maze;

public class RecursiveBacktrackerGenerator implements MazeGenerator {

	private Maze maze;
	Stack<Cell> stack;
	boolean visited[][];
	int mazeCSize;
	int mazeRSize;
	int unVisitedNeighs;
	int direction;
	int allVisited;
	int mazeSize;
	boolean endTrack;
	HashSet<Cell> tunnels;

	@Override
	public void generateMaze(Maze maze) {
		
		this.maze = maze;
		allVisited =0;
		stack = new Stack<Cell>();
		mazeCSize = maze.sizeR;
		mazeRSize = maze.sizeC;
		mazeSize =mazeRSize*mazeCSize;
		visited = new boolean[mazeRSize][mazeCSize];
		//if the maze is tunnel find all tunnels cells
		if(maze.type==maze.TUNNEL){
			tunnels  = new HashSet();
			for(int i=0; i<mazeRSize;i++){
				for(int j=0;j<mazeCSize;j++){
					if(maze.map[i][j].tunnelTo!=null){
						tunnels.add(maze.map[i][j]);
					}
				}
			}
		}
		//pick a starting cell
		Cell startingCell = pickStartingCell();
		dfsr(startingCell);
	} // end of generateMaze()

	/**
	 * Depth First Search Recursive algorithm is used to visit all 
	 * Unvisited cells and recursively backtrack to find another unvisited cell
	 * @param cell
	 */
	private void dfsr(Cell cell){
		//pick a random unvisited cell
		try {
			Cell visitNeigh = cell;
			//pick new unvisited neighbour
			Cell newNeighbour = pickUnvisitedNeighbour(visitNeigh);
			// continue picking unvisited neighbour until there is no more unvisited neighbor
			while(newNeighbour!=visitNeigh){
				visitNeigh = newNeighbour;
				newNeighbour = pickUnvisitedNeighbour(visitNeigh);
				endTrack=false;
			}
			//if all neighbours are visited then backtraack one step
			if(newNeighbour==visitNeigh && !stack.isEmpty() && !endTrack){
				stack.pop();
				endTrack=true;
			}
			if(!stack.isEmpty())
				cell = stack.pop();
			//continue backtracking until all cells are visited
			if( allVisited<mazeSize)
				dfsr(cell);
		} catch (Exception e) {
			System.out.println(e);
		}		
	}

	/***
	 * To start the maze firstly pick a random cell
	 * @return
	 */
	private Cell pickStartingCell(){
		//Randomly pick a starting cell
		int r=0;
		int c=0;
		Cell startCell=null;
		while (startCell==null || visited[startCell.r][startCell.c]) {
			r = new Random().nextInt(mazeRSize);
			c = new Random().nextInt(mazeCSize);
			startCell = maze.map[r][c];
		};
		if(maze.type!=maze.HEX){

			visited[startCell.r][startCell.c]=true;
		}else{
			visited[startCell.r][startCell.c-(startCell.r+1)/2]=true;
		}
		allVisited++;
		System.out.println(startCell.r+", "+startCell.c);

		return startCell;
	}

	/***
	 * Pick a random unvisited cell
	 * @param cell
	 * @return
	 */
	private Cell pickUnvisitedNeighbour(Cell cell){
		int cellTotalNeighs = cell.neigh.length;
		int[] unvisitedneighbours=new int[cellTotalNeighs];
		unVisitedNeighs=0;
		//Count number of not null neighbours and 
		//set their index in an array

		// goal is to find all unvisited around the cell
		for(int i =0;i<cellTotalNeighs;i++){
			if(maze.type==maze.HEX){
				//if this neighbor is not null and is not marked as visited then pick the index and count 
				//as unvisited neighbour
				if(cell.neigh[i]!=null && !visited[cell.neigh[i].r][cell.neigh[i].c-(cell.neigh[i].r+1)/2]){
					unvisitedneighbours[unVisitedNeighs]=i;
					unVisitedNeighs++;
				}
			}else {
				//if this neighbor is not null and is not marked as visited then pick the index and count 
				//as unvisited neighbour. there is no difference between tunnel and normal maze to count unvisited neighbours
				if(cell.neigh[i]!=null && !visited[cell.neigh[i].r][cell.neigh[i].c]){
					unvisitedneighbours[unVisitedNeighs]=i;
					unVisitedNeighs++;
				}
			}
		}
		//if there is no unvisited neighbour around the cell then return
		//otherwise go to pick the unvisited neighbour
		if(unVisitedNeighs==0){
			System.out.println("There is no unvisted neighbor around this cell...");
			return cell;
		} else {
			int index = new Random().nextInt(unVisitedNeighs);
			direction = unvisitedneighbours[index];
			Cell next = cell.neigh[direction];
			//check tunnel maze different situations
			if(maze.type==maze.TUNNEL){
				if(tunnels.contains(next)){
					if(!visited[next.r][next.c]){
						//if probable neighbour is tunnel and it is not visited yet
						//then current cell's wall to be removed, tunnel cell neighbor marked as visited, 
						//tunnel exit marked as visited, and next cell to be the tunnel exit
						maze.map[cell.r][cell.c].wall[direction].present=false;
						visited[next.r][next.c]=true;
						visited[next.tunnelTo.r][next.tunnelTo.c]=true;
						//maze.map[cell.tunnelTo.r][cell.tunnelTo.c].wall[direction].present=false;
						cell = next.tunnelTo;
						stack.push(cell);
						allVisited++;
						allVisited++;
					} else {
						//if the probable neighbor is a tunnel and is visited before then
						//search in unvisited neighbors to find an unvisited neighbor
						//remove the wall toward that neighbor, mark new neighbor as visited
						//
						int newIndex;
						do{
							newIndex = new Random().nextInt(unVisitedNeighs);
						}while(newIndex==index);
						direction = unvisitedneighbours[newIndex];
						maze.map[cell.r][cell.c].wall[direction].present=false;
						cell=cell.neigh[direction];
						visited[cell.r][cell.c]=true;
						stack.push(cell);
						allVisited++;
					}

				}else{ //cell in the tunnel maze in not tunnel
					//if the maze is tunnel but probable unvisited neighbor is not a tunnel
					//then the wall toward that cell to be removed, that neighbour is next cell, and
					//that unvisited neighbor should be marked as visited
					maze.map[cell.r][cell.c].wall[direction].present=false;
					cell =next;
					visited[cell.r][cell.c]=true;
					stack.push(cell);
					allVisited++;
				}


			}else{//maze is not tunnel
				//if the maze is not tunnel
				// then the wall toward that cell to be removed,
				//unvisited neighbour to be next cell and 
				//depends on the maze type next cell marked as visited 
				maze.map[cell.r][cell.c].wall[direction].present=false;
				cell = next;
				if(maze.type!=maze.HEX){
					visited[cell.r][cell.c]=true;
				}else{
					visited[cell.r][cell.c-(cell.r+1)/2]=true;
				}
				stack.push(cell);
				allVisited++;
			}
		}

		System.out.println(cell.r+", "+cell.c);
		System.out.println(allVisited+"/"+mazeSize);
		return cell;
	}
} // end of class RecursiveBacktrackerGenerator

