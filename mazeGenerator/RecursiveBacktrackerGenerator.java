package mazeGenerator;

import java.util.Random;
import java.util.Stack;
import maze.Cell;
import maze.Maze;

public class RecursiveBacktrackerGenerator implements MazeGenerator {

	private Maze maze;
	private int r;
	private int c;
	Stack<Cell> stack;
	boolean visited[][];
	int mazeCSize;
	int mazeRSize;
	int unVisitedNeighs;
	int direction;
	int allVisited;
	int mazeSize;
	boolean endTrack;

	@Override
	public void generateMaze(Maze maze) {
		this.maze = maze;
		//visited = new HashSet();
		allVisited =0;
		stack = new Stack<Cell>();
		// TODO Auto-generated method stub
		mazeCSize = maze.sizeR;
		mazeRSize = maze.sizeC;
//		if(maze.type==maze.HEX){
//			mazeSize =mazeRSize*(mazeCSize + (mazeRSize + 1) / 2);
//		}
//		else{
			mazeSize =mazeRSize*mazeCSize;
		//}
		visited = new boolean[mazeRSize][mazeCSize];

		//pick a starting cell
		Cell startingCell = pickStartingCell();
		dfsr(startingCell);
		

			for(int j=0; j<mazeCSize;j++){
				for(int i=0;i<mazeRSize;i++){
				System.out.print(visited[i][j]+" ");
			}
			System.out.println();
		}

	} // end of generateMaze()

	private void dfsr(Cell cell){
		//pick a random unvisited cell
		try {
		Cell visitNeigh = cell;
		Cell newNeighbour = pickUnvisitedNeighbour(visitNeigh);
		while(newNeighbour!=visitNeigh){
			visitNeigh = newNeighbour;
			newNeighbour = pickUnvisitedNeighbour(visitNeigh);
			endTrack=false;
		}
		if(newNeighbour==visitNeigh && !stack.isEmpty() && !endTrack){
			stack.pop();
			endTrack=true;
		}
		if(!stack.isEmpty())
			cell = stack.pop();
		int stackSize = stack.size();
		if(stackSize==1)
			System.out.println();

			if( allVisited<mazeSize)
				dfsr(cell);
		} catch (Exception e) {
			System.out.println(e);
		}		
	}

	private Cell pickStartingCell(){
		//Randomly pick a starting cell
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

	private Cell pickUnvisitedNeighbour(Cell cell){
//		System.out.println("All visited: "+allVisited+",Stack size:"+stack.size());

			int cellTotalNeighs = cell.neigh.length;
			//Cell neighbour=null;
			int[] unvisitedneighbours=new int[cellTotalNeighs];
			unVisitedNeighs=0;
			try {
			//Count number of not null neighbours and 
			//set their index in an array
				 
			for(int i =0;i<cellTotalNeighs;i++){
				if(maze.type!=maze.HEX){
				if(cell.neigh[i]!=null && !visited[cell.neigh[i].r][cell.neigh[i].c]){
					unvisitedneighbours[unVisitedNeighs]=i;
					unVisitedNeighs++;
				}
				}else{
					if(cell.neigh[i]!=null && !visited[cell.neigh[i].r][cell.neigh[i].c-(cell.neigh[i].r+1)/2]){
						unvisitedneighbours[unVisitedNeighs]=i;
						unVisitedNeighs++;
					}
				}
			}
			//If there is any not null neighbour then pick one randomly
			if(unVisitedNeighs!=0){
			
				int index = new Random().nextInt(unVisitedNeighs);
				direction = unvisitedneighbours[index];
				maze.map[cell.r][cell.c].wall[direction].present=false;
				cell=cell.neigh[direction];
				System.out.println(cell.r+", "+cell.c);

				if(maze.type!=maze.HEX){
					visited[cell.r][cell.c]=true;
				}else{
					visited[cell.r][cell.c-(cell.r+1)/2]=true;
				}

				stack.push(cell);
				allVisited++;
			}
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}

		return cell;
	}
} // end of class RecursiveBacktrackerGenerator
