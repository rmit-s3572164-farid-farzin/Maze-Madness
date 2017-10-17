package mazeSolver;

import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import maze.Cell;
import maze.Maze;

/**
 * <bold>Bidirectional Recursive Backtracker</bold>
 * This solver performs DFS (Depth First Search) searches starting at
 *  both the entrance and exit. When starting at the entrance of the 
 *  maze, the solver will initially randomly choose an adjacent 
 *  unvisited cell. It moves to that cell (which is the current front
 *  for DFS starting at the entrance), update its visit status, then 
 *  selects another random unvisited neighbour. It continues this process 
 *  until it hits a deadend (no unvisited neighbours), then it backtracks
 *  to a previous cell that has an unvisited neighbour. Randomly select 
 *  one of the unvisited neighbour and repeat process until we reached the 
 *  exit (this is always possible for a perfect maze). The path from 
 *  entrance to exit is the solution.
 *  <p>
 *  When the two DFS fronts first meet, the path from the entrance to the point
 *  they meet, and the path from the exit to the meeting point forms the two 
 *  halves of a shortest path (in terms of cell visited) from entrance to exit.
 *  Combine these paths to get the final path solution.
 *  
 *  @author Farid Farzin
 *  @version %I%, %G%
 *  @since 1.0
 */
public class BiDirectionalRecursiveBacktrackerSolver implements MazeSolver {

	private Maze maze;
	Stack<Cell> stack1;
	Stack<Cell> stack2;

	boolean visited1[][];
	boolean visited2[][];

	int mazeCSize;
	int mazeRSize;
	int unVisitedNeighs1;
	int unVisitedNeighs2;

	int direction1;
	int direction2;

	int allVisited1;
	int allVisited2;

	int mazeSize;
	Cell exitFront;
	Cell enteranceFront;
	int cellsExplored;
	HashSet<Cell> tunnels1;
	HashSet<Cell> tunnels2;

	HashSet<Cell> startPath;
	HashSet<Cell> endPath;

	Cell visitNeigh1;
	Cell visitNeigh2;

	boolean flipflop;
	boolean isSolved;
	boolean tunnelExit1;
	boolean tunnelExit2;

	/***
	 * @see solveMaze(Maze maze) is responsible to run the whole algorithm
	 * to solve mazes. Components are as below:
	 * <ul>
	 * <li>visited maze to reflect which cells are visited. @see visited1 and @see visited2
	 * <li> @see stack1  and @see stack2 to save the paths of solvers
	 * <li> @see unVisitedNeighs1 and unVisitedNeighs2 to list all unvisited neighbours of current cell
	 * <li> @see direction1 and direction2 to indicate the new direction to next neighbour
	 * <li> @see allVisited1 and @see allVisited2 to indicate whether all cells in the maze are visited
	 * <li> @see enteranceFront and exitFront to save the DFS fronts.
	 * <li> In a case the maze is tunnel @see tunnels1 and @see tunnels2 will save the tunnels cells
	 * <li> @see tunnelExit1 and @see tunnelExit2 indicate whether we have exited from a tunnel.
	 * <li> @see isSolved announce if the maze is solved and two paths reach each other.
	 * <li> @see flipflop: this variable is very important as it allows both paths to go ahead simultaneously 
	 * <ul>
	 * <p>
	 * Solver sequence is as below:
	 * <ul>
	 * <li>We pick enterance and exit as starting points and mark them as visited.
	 * <li>run DFSR to visit unvisited cells and recursively backtrack if there is no unvisited neighbour around.
	 * <li>If both paths hit each other algorithm is stopped and returns maze is solved.
	 * <ul> 
	 * <p>
	 */
	@Override
	public void solveMaze(Maze maze) {
		try {
			this.maze = maze;
			allVisited1 =0;
			allVisited2 =0;
			visitNeigh1=null;
			visitNeigh2=null;
			isSolved=false;
			tunnelExit1=false;
			tunnelExit2=false;
			stack1 = new Stack<Cell>();
			stack2 = new Stack<Cell>();

			mazeCSize = maze.sizeR;
			mazeRSize = maze.sizeC;
			mazeSize =mazeRSize*mazeCSize;
			visited1 = new boolean[mazeRSize][mazeCSize];
			visited2 = new boolean[mazeRSize][mazeCSize];

			//maze pick start points
			pickStartingCell();
			startPath  = new HashSet<Cell>();
			endPath  = new HashSet<Cell>();
			//find maze tunnels
			if(maze.type==Maze.TUNNEL){
				tunnels1  = new HashSet<Cell>();	
				tunnels2  = new HashSet<Cell>();			

				for(int i=0; i<mazeRSize;i++){
					for(int j=0;j<mazeCSize;j++){
						if(maze.map[i][j].tunnelTo!=null){
							tunnels1.add(maze.map[i][j]);
							tunnels2.add(maze.map[i][j]);
						}
					}
				}
			}
			flipflop = false;

			dfsr();

		} catch (Exception e) {
			System.out.println(e);
		}
	} // end of solveMaze()

	/***
	 * In the case maze is solved this method returns true to maze tester
	 */
	@Override
	public boolean isSolved() {
		return isSolved;
	} // end if isSolved()

	/***
	 * cellsExplored counts up all visited cells until maze is solved
	 */
	@Override
	public int cellsExplored() {
		return cellsExplored;
	} // end of cellsExplored()

	/***
	 * Since solving the maze start at both enterance and exit points then these 
	 * cells are marked as visited. formula to calculate visited cell in hex type is different 
	 * from other types
	 * @see pickStartingCell()
	 */
	private void pickStartingCell(){
		cellsExplored++;
		//Randomly pick a starting cell
		if(maze.type!=Maze.HEX){
			visited1[maze.entrance.r][maze.entrance.c]=true;
			visited2[maze.exit.r][maze.exit.c]=true;
		}else{
			visited1[maze.entrance.r][maze.entrance.c-(maze.entrance.r+1)/2]=true;
			visited2[maze.exit.r][maze.exit.c-(maze.exit.r+1)/2]=true;
		}
		allVisited1++;
		allVisited2++;		
		enteranceFront = maze.entrance;
		exitFront = maze.exit;
		maze.drawFtPrt(maze.entrance);
		maze.drawFtPrt(maze.exit);
	}	

	/**
	 * Depth First Search Recursive algorithm is used to visit all 
	 * Unvisited cells and recursively backtrack to find another unvisited cell.
	 * <p>
	 * @see dfsr() always check the flip flop and decides which paths should be followed as per flip flop 
	 * status. 
	 * <p>
	 * New cell is checked in @see pickUnvisitedNeighStart(visited cell) and if visited cell in previous 
	 * round is different from the path front part 1 will work: there is an unvisited neighbour 
	 * around so new cell will be marked as visited cell and new path front.
	 * <p>
	 *  If there is no unvisited cell so visited cell equal path front and means we have to 
	 *  back track the cell. So we pull one cell from stack at a time and check whether there is
	 *  any unvisited neighbour around.
	 *  <p>
	 *  After each step we compare the path with other path to see is they hit each other 
	 */
	private void dfsr(){
		//pick a random unvisited cell from start and end points
		try {
			while(!isSolved && (allVisited1<mazeSize || allVisited2<mazeSize)){
				//flip flop helps to check cells from start and end point one by one 
				if(flipflop){ //flip flop check the start point
					//Part 1
					// continue picking unvisited neighbour until there is no more unvisited neighbor
					if(enteranceFront!=visitNeigh1){
						visitNeigh1 = enteranceFront;
						enteranceFront = pickUnvisitedNeighStart(visitNeigh1);
						startPath.add(enteranceFront);
						if(endPath.contains(enteranceFront)){
							isSolved=true;
						}
						flipflop=false;					
					}
					//if all neighbours are visited then backtraack one step
					if(enteranceFront==visitNeigh1 && !stack1.isEmpty() && flipflop){
						enteranceFront = stack1.pop();
						flipflop=false;
						//continue backtracking until all cells are visited
						if( allVisited1<mazeSize)
							dfsr();
					}
				} else{ //flip flop check the end point
					// continue picking unvisited neighbour until there is no more unvisited neighbor
					if(exitFront!=visitNeigh2){
						visitNeigh2 = exitFront;
						exitFront = pickUnvisitedNeighEnd(visitNeigh2);
						endPath.add(exitFront);
						if(startPath.contains(exitFront)){
							isSolved=true;
						}
						flipflop=true;
					}
					//if all neighbours are visited then backtraack one step
					if(exitFront==visitNeigh2 && !stack2.isEmpty() && !flipflop){
						exitFront = stack2.pop();
						flipflop=true;
						//continue backtracking until all cells are visited
						if( allVisited2<mazeSize)
							dfsr();
					}
				}
			}
			if(endPath.contains(enteranceFront)||startPath.contains(exitFront))
				return;
		} catch (Exception e) {
			System.out.println(e);
		}		
	}

	/***
	 * To see unvisited neighbour around path front what we do is to look around and pick all the unvisited
	 * neighbours. From there we pick a random neighbour from unvisited neighbours. If there is no unvisited
	 * neighbour then we return the cell as output. Unvisited neighbours are the ones which have no wall with
	 * path front, are not null and unvisited.
	 * <p> 
	 * If the maze is tunnel and the path front is indicating a tunnel then we mark that 
	 * tunnel entrance and the exit as visited and push both cells into stack to draw the path. Below method is for entrance path
	 * @param cell current path front cell
	 * @return unvisited cell or the current cell if there is no unvisited neighbour.
	 */
	private Cell pickUnvisitedNeighStart(Cell cell){
		int cellTotalNeighs = cell.neigh.length;
		int[] unvisitedneighbours=new int[cellTotalNeighs];
		unVisitedNeighs1=0;
		//Count number of not null neighbours and 
		//set their index in an array
		// goal is to find all unvisited around the cell
		for(int i =0;i<cellTotalNeighs;i++){
			if(maze.type==Maze.HEX){
				//if this neighbor is not null and is not marked as visited then pick the index and count 
				//as unvisited neighbour
				if(cell.neigh[i]!=null && !visited1[cell.neigh[i].r][cell.neigh[i].c-(cell.neigh[i].r+1)/2]
						&& !maze.map[cell.r][cell.c].wall[i].present){
					unvisitedneighbours[unVisitedNeighs1]=i;
					unVisitedNeighs1++;
				}
			}else{
				//if maze is tunnel in visited cell is tunnel
				if(maze.type==Maze.TUNNEL && cell.tunnelTo!=null){
					//if it is tunnel exit
					if(tunnelExit1){
						if(cell.neigh[i]!=null && !visited1[cell.neigh[i].r][cell.neigh[i].c]
								&& !maze.map[cell.r][cell.c].wall[i].present){
							unvisitedneighbours[unVisitedNeighs1]=i;
							unVisitedNeighs1++;
						}
					}else{ //if is tunnel entrance
						if(cell.tunnelTo.neigh[i]!=null && 
								!visited1[cell.tunnelTo.neigh[i].r][cell.tunnelTo.neigh[i].c]
										&& !maze.map[cell.tunnelTo.r][cell.tunnelTo.c].wall[i].present){
							unvisitedneighbours[unVisitedNeighs1]=i;
							unVisitedNeighs1++;
						}
					}
				} else
					//if this neighbor is not null and is not marked as visited then pick the index and count 
					//as unvisited neighbour. there is no difference between tunnel and normal maze to count 
					//unvisited neighbours
					if(cell.neigh[i]!=null && !visited1[cell.neigh[i].r][cell.neigh[i].c]
							&& !maze.map[cell.r][cell.c].wall[i].present){
						unvisitedneighbours[unVisitedNeighs1]=i;
						unVisitedNeighs1++;
					}
			}
		}
		//if there is no unvisited neighbour around the cell then return
		if(unVisitedNeighs1==0){
			return cell;
		} else {		//otherwise go to pick the unvisited neighbour
			int index = new Random().nextInt(unVisitedNeighs1);
			direction1 = unvisitedneighbours[index];
			Cell next = cell.neigh[direction1];
			//check tunnel maze different situations
			if(maze.type==Maze.TUNNEL){
				if(tunnels1.contains(next)){
					if(!visited1[next.r][next.c]){
						//if probable neighbour is tunnel and it is not visited yet
						//then current cell's wall to be removed, tunnel cell neighbor marked as visited, 
						//tunnel exit marked as visited, and next cell to be the tunnel exit
						visited1[next.r][next.c]=true;
						visited1[next.tunnelTo.r][next.tunnelTo.c]=true;
						stack1.push(cell);
						stack1.push(next);
						maze.drawFtPrt(next);
						startPath.add(next);
						cell = next.tunnelTo;
						cellsExplored++;
						cellsExplored++;
						allVisited1++;
						allVisited1++;
						tunnelExit1=true;
					} else {
						//if the probable neighbor is a tunnel and is visited before then
						//search in unvisited neighbors to find an unvisited neighbor
						//remove the wall toward that neighbor, mark new neighbor as visited
						//
						int newIndex;
						do{
							newIndex = new Random().nextInt(unVisitedNeighs1);
						}while(newIndex==index);
						direction1 = unvisitedneighbours[newIndex];
						stack1.push(cell);
						cellsExplored++;
						cell=cell.neigh[direction1];
						visited1[cell.r][cell.c]=true;
						allVisited1++;
						tunnelExit1=false;
					}

				}else{ //cell in the tunnel maze in not tunnel
					//if the maze is tunnel but probable unvisited neighbor is not a tunnel
					//then the wall toward that cell to be removed, that neighbour is next cell, and
					//that unvisited neighbor should be marked as visited
					stack1.push(cell);
					cellsExplored++;
					cell =next;
					visited1[cell.r][cell.c]=true;
					allVisited1++;
				}
			}else{//maze is not tunnel
				//if the maze is not tunnel
				// then the wall toward that cell to be removed,
				//unvisited neighbour to be next cell and 
				//depends on the maze type next cell marked as visited 
				stack1.push(cell);
				cellsExplored++;
				cell = next;
				if(maze.type!=Maze.HEX){
					visited1[cell.r][cell.c]=true;
				}else{
					visited1[cell.r][cell.c-(cell.r+1)/2]=true;
				}
				allVisited1++;
			}
		}
		maze.drawFtPrt(cell);
		return cell;
	}

	/***
	 * To see unvisited neighbour around path front what we do is to look around and pick all the unvisited
	 * neighbours. From there we pick a random neighbour from unvisited neighbours. If there is no unvisited
	 * neighbour then we return the cell as output. Unvisited neighbours are the ones which have no wall with
	 * path front, are not null and unvisited.
	 * <p> 
	 * If the maze is tunnel and the path front is indicating a tunnel then we mark that 
	 * tunnel entrance and the exit as visited and push both cells into stack to draw the path. Below method is for exit path
	 * @param cell current path front cell
	 * @return unvisited cell or the current cell if there is no unvisited neighbour.
	 */
	private Cell pickUnvisitedNeighEnd(Cell cell){
		int cellTotalNeighs = cell.neigh.length;
		int[] unvisitedneighbours=new int[cellTotalNeighs];
		unVisitedNeighs2=0;
		//Count number of not null neighbours and 
		//set their index in an array
		// goal is to find all unvisited around the cell
		for(int i =0;i<cellTotalNeighs;i++){
			if(maze.type==Maze.HEX){
				//if this neighbor is not null and is not marked as visited then pick the index and count 
				//as unvisited neighbour
				if(cell.neigh[i]!=null && !visited2[cell.neigh[i].r][cell.neigh[i].c-(cell.neigh[i].r+1)/2]
						&& !maze.map[cell.r][cell.c].wall[i].present){
					unvisitedneighbours[unVisitedNeighs2]=i;
					unVisitedNeighs2++;
				}
			}else{
				//if maze is tunnel in visited cell is tunnel
				if(maze.type==Maze.TUNNEL && cell.tunnelTo!=null){
					//if it is tunnel exit
					if(tunnelExit2){
						if(cell.neigh[i]!=null && !visited2[cell.neigh[i].r][cell.neigh[i].c]
								&& !maze.map[cell.r][cell.c].wall[i].present){
							unvisitedneighbours[unVisitedNeighs2]=i;
							unVisitedNeighs2++;
						}
					}else{ //if is tunnel entrance
						if(cell.tunnelTo.neigh[i]!=null && 
								!visited2[cell.tunnelTo.neigh[i].r][cell.tunnelTo.neigh[i].c]
										&& !maze.map[cell.tunnelTo.r][cell.tunnelTo.c].wall[i].present){
							unvisitedneighbours[unVisitedNeighs2]=i;
							unVisitedNeighs2++;
						}
					}
				} else
					//if this neighbor is not null and is not marked as visited then pick the index and count 
					//as unvisited neighbour. there is no difference between tunnel and normal maze to count
					// unvisited neighbours
					if(cell.neigh[i]!=null && !visited2[cell.neigh[i].r][cell.neigh[i].c]
							&& !maze.map[cell.r][cell.c].wall[i].present){
						unvisitedneighbours[unVisitedNeighs2]=i;
						unVisitedNeighs2++;
					}
			}
		}
		//if there is no unvisited neighbour around the cell then return
		if(unVisitedNeighs2==0){
			return cell;
		} else {		//otherwise go to pick the unvisited neighbour
			int index = new Random().nextInt(unVisitedNeighs2);
			direction2 = unvisitedneighbours[index];
			Cell next = cell.neigh[direction2];
			//check tunnel maze different situations
			if(maze.type==Maze.TUNNEL){
				if(tunnels2.contains(next)){
					if(!visited2[next.r][next.c]){
						//if probable neighbour is tunnel and it is not visited yet
						//then current cell's wall to be removed, tunnel cell neighbor marked as visited, 
						//tunnel exit marked as visited, and next cell to be the tunnel exit
						visited2[next.r][next.c]=true;
						visited2[next.tunnelTo.r][next.tunnelTo.c]=true;
						stack2.push(cell);
						stack2.push(next);
						maze.drawFtPrt(next);
						endPath.add(next);
						cell = next.tunnelTo;
						cellsExplored++;
						cellsExplored++;
						allVisited2++;
						allVisited2++;
						tunnelExit2=true;
					} else {
						//if the probable neighbor is a tunnel and is visited before then
						//search in unvisited neighbors to find an unvisited neighbor
						//remove the wall toward that neighbor, mark new neighbor as visited
						//
						int newIndex;
						do{
							newIndex = new Random().nextInt(unVisitedNeighs2);
						}while(newIndex==index);
						direction2 = unvisitedneighbours[newIndex];
						stack2.push(cell);
						cellsExplored++;
						cell=cell.neigh[direction2];
						visited2[cell.r][cell.c]=true;
						allVisited2++;
						tunnelExit2=false;
					}

				}else{ //cell in the tunnel maze in not tunnel
					//if the maze is tunnel but probable unvisited neighbor is not a tunnel
					//then the wall toward that cell to be removed, that neighbour is next cell, and
					//that unvisited neighbor should be marked as visited
					stack2.push(cell);
					cellsExplored++;
					cell =next;
					visited2[cell.r][cell.c]=true;
					allVisited2++;
				}
			}else{//maze is not tunnel
				//if the maze is not tunnel
				// then the wall toward that cell to be removed,
				//unvisited neighbour to be next cell and 
				//depends on the maze type next cell marked as visited 
				stack2.push(cell);
				cellsExplored++;
				cell = next;
				if(maze.type!=Maze.HEX){
					visited2[cell.r][cell.c]=true;
				}else{
					visited2[cell.r][cell.c-(cell.r+1)/2]=true;
				}
				allVisited2++;
			}
		}
		maze.drawFtPrt(cell);
		return cell;
	}
} // end of class RecursiveBacktrackerGenerator
