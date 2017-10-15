package mazeSolver;

import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

import javax.xml.ws.Endpoint;

import com.sun.xml.internal.ws.server.EndpointFactory;

import maze.Cell;
import maze.Maze;

/**
 * Implements the BiDirectional recursive backtracking maze solving algorithm.
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

	@Override
	public boolean isSolved() {
		return isSolved;
	} // end if isSolved()

	@Override
	public int cellsExplored() {
		return cellsExplored;
	} // end of cellsExplored()
	
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
	 * Unvisited cells and recursively backtrack to find another unvisited cell
	 * @param startcell
	 */
	private void dfsr(){
		//pick a random unvisited cell from start and end points
		try {
			while(!isSolved && (allVisited1<mazeSize || allVisited2<mazeSize)){
			//flip flop helps to check cells from start and end point one by one 
			if(flipflop){ //flip flop check the start point
				// continue picking unvisited neighbour until there is no more unvisited neighbor
				if(enteranceFront!=visitNeigh1){
					visitNeigh1 = enteranceFront;
					enteranceFront = pickUnvisitedNeighStart(visitNeigh1);
					startPath.add(enteranceFront);
					if(endPath.contains(enteranceFront)){
						isSolved=true;
						throw new Exception("Maze is solved");
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
						throw new Exception("Maze is solved");
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
	 * Pick a random unvisited cell
	 * @param cell
	 * @return
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
						if(cell.tunnelTo.neigh[i]!=null && !visited1[cell.tunnelTo.neigh[i].r][cell.tunnelTo.neigh[i].c]
								&& !maze.map[cell.tunnelTo.r][cell.tunnelTo.c].wall[i].present){
							unvisitedneighbours[unVisitedNeighs1]=i;
							unVisitedNeighs1++;
						}
					}
				} else
				//if this neighbor is not null and is not marked as visited then pick the index and count 
				//as unvisited neighbour. there is no difference between tunnel and normal maze to count unvisited neighbours
				if(cell.neigh[i]!=null && !visited1[cell.neigh[i].r][cell.neigh[i].c]
						&& !maze.map[cell.r][cell.c].wall[i].present){
					unvisitedneighbours[unVisitedNeighs1]=i;
					unVisitedNeighs1++;
				}
			}
		}
		//if there is no unvisited neighbour around the cell then return
		if(unVisitedNeighs1==0){
			System.out.println("Enter: There is no unvisted neighbor around this cell "+cell.r+", "+cell.c);
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
		System.out.println("Enter: "+cell.r+", "+cell.c);
		maze.drawFtPrt(cell);
		return cell;
	}
	
	/**
	 * Pick an unvisited cell from exit point
	 * @param cell
	 * @return
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
						if(cell.tunnelTo.neigh[i]!=null && !visited2[cell.tunnelTo.neigh[i].r][cell.tunnelTo.neigh[i].c]
								&& !maze.map[cell.tunnelTo.r][cell.tunnelTo.c].wall[i].present){
							unvisitedneighbours[unVisitedNeighs2]=i;
							unVisitedNeighs2++;
						}
					}
				} else
				//if this neighbor is not null and is not marked as visited then pick the index and count 
				//as unvisited neighbour. there is no difference between tunnel and normal maze to count unvisited neighbours
				if(cell.neigh[i]!=null && !visited2[cell.neigh[i].r][cell.neigh[i].c]
						&& !maze.map[cell.r][cell.c].wall[i].present){
					unvisitedneighbours[unVisitedNeighs2]=i;
					unVisitedNeighs2++;
				}
			}
		}
		//if there is no unvisited neighbour around the cell then return
		if(unVisitedNeighs2==0){
			System.out.println("Exit: There is no unvisted neighbor around this cell "+cell.r+", "+cell.c);
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
		System.out.println("exit: "+cell.r+", "+cell.c);
		maze.drawFtPrt(cell);
		return cell;
	}
} // end of class RecursiveBacktrackerGenerator
