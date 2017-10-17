package mazeGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import maze.Cell;
import maze.Maze;
/***
 * <bold>Recursive Backtracker Generator
 * This generator uses the DFS principle to generate mazes. Starting 
 * with a maze where all walls are present, i.e., between every cell
 * is a wall, it uses the following procedure to generate a maze:
 * <ul>
 * <li>Randomly pick a starting cell.
 * <li>Pick a random unvisited neighbouring cell and move to that neighbour. 
 * In the process, carve a path (i.e, remove the wall) between the cells.
 * <li>Continue this process until we reach a cell that has no unvisited neighbours. 
 * In that case, backtrack one cell at a time, until we backtracked to a cell that 
 * has unvisited neighbours. Repeat step 2.
 * <li>When there are no more unvisited neighbours for all cells, then every cell 
 * would have been visited and we have generated a perfect maze.
 * <ul>
 * 
 *  @author Farid Farzin
 *  @version %I%, %G%
 *  @since 1.0
 */
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

	/***
	 * @see generateMaze(Maze maze) is responsible to run the whole algorithm
	 * to generate mazes. Components are as below:
	 * <ul>
	 * <li> @see maze is the maze selected by user.
	 * <li> @see stack saves the path that algorithm has passed.
	 * <li> @see visited[][] contains the cells that have been visited.
	 * <li> @see mazeRSize and @see mazeCSize in order are maze row size and column size.
	 * <li> @see unVisitedNeighs keeps number of unvisited neighbours around current vistited
	 * cell.  
	 * <li> @see direction shows the way path finder will go next step.
	 * <li> @see allVisited counts number of visited cells up to now.
	 * <li> @see mazeSizewill give number of cells in a maze.
	 * <li> @see endTrack indicates current visited cell does not have any unvisited neighbour.
	 * <li> @see tunnels keeps tunnel cells 
	 * <ul>
	 * <p>
	 * For the start we pick a random cell from the maze and we send the cell to a Depth First Search 
	 * Recursive algorithm to continue the generate the maze. 
	 * 
	 * @param maze Receives the maze defined by user
	 */
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
		if(maze.type==Maze.TUNNEL){
			tunnels  = new HashSet<Cell>();
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
	 * Unvisited cells and recursively backtrack to find another unvisited cell. 
	 * <ul>
	 * <li>At each start we send the visited cell to @see pickUnvisitedNeighbour(visitNeigh) 
	 * in order to return an unvisited neighbour or inform about no more unvisited neighbour.
	 * <li>Second step is to pick unvisited neighbours until there is no more unvisited neighbour.
	 * At each round we push the cell to a stack so that we record the path.
	 * <li> If the returned value from @see pickUnvisitedNeighbour(visitNeigh) equals the input
	 * we through last pushed cell from stack which the cell with no unvisited neighbour.
	 * Then we pop the next cell and back track in to @see dfsr(Cell cell).
	 * <li> We recursively continue the process until @see allVisited quals or greater than 
	 * @see mazeSize. In this case the maze is perfectly genetrated.
	 * 
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
			if(!stack.isEmpty()){
				cell = stack.pop();
			}

			//continue backtracking until all cells are visited
			if( allVisited<mazeSize)
				dfsr(cell);

		} catch (Exception e) {
			System.out.println(e);
		}		
	}

	/***
	 * @see pickStartingCell() picks a random cell from the maze. This cell is marked 
	 * as visited.
	 * 
	 * @return starting cell
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

		if(maze.type!=Maze.HEX){
			visited[startCell.r][startCell.c]=true;
		}else{
			visited[startCell.r][startCell.c-(startCell.r+1)/2]=true;
		}

		allVisited++;

		return startCell;
	}

	/***
	 * @see pickUnvisitedNeighbour(Cell cell) has several staps as below:
	 * <ul>
	 * <li>First of all we look around the cell and find direction to all
	 * unvisited neighbours.
	 * <li>Then we randomly pick a direction from the above list.
	 * <li>Based on maze type we remove the wall toward picked neighbour,
	 * mark new cell as visited and push it into the stack for tracking purpose.
	 * <li>If the maze is tunnel both tunnel entrance and exit will be marked as visited
	 *  and pushed into the stack.
	 * <ul>
	 * @param cell current cell
	 * @return new unvisited neighbour or the current cell in a case there is no further 
	 * unvisited neighbour
	 */
	private Cell pickUnvisitedNeighbour(Cell cell){
		int cellTotalNeighs = cell.neigh.length;
		int[] unvisitedneighbours=new int[cellTotalNeighs];
		unVisitedNeighs=0;
		//Count number of not null neighbours and 
		//set their index in an array

		// goal is to find all unvisited around the cell
		for(int i =0;i<cellTotalNeighs;i++){
			if(maze.type==Maze.HEX){
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
			return cell;
		} else {
			int index = new Random().nextInt(unVisitedNeighs);
			direction = unvisitedneighbours[index];
			Cell next = cell.neigh[direction];
			//check tunnel maze different situations
			if(maze.type==Maze.TUNNEL){
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
				if(maze.type!=Maze.HEX){
					visited[cell.r][cell.c]=true;
				}else{
					visited[cell.r][cell.c-(cell.r+1)/2]=true;
				}
				stack.push(cell);
				allVisited++;
			}
		}

		return cell;
	}
} // end of class RecursiveBacktrackerGenerator

