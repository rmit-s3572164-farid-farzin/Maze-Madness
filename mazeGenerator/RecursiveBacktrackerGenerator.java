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

	@Override
	public void generateMaze(Maze maze) {
		this.maze = maze;
		//visited = new HashSet();
		allVisited =0;
		stack = new Stack<Cell>();
		// TODO Auto-generated method stub
		mazeCSize = maze.sizeR;
		mazeRSize = maze.sizeC;
		if(maze.type==maze.HEX){
			visited = new boolean[mazeRSize][mazeCSize + (mazeRSize + 1) / 2];
			mazeSize =mazeRSize*(mazeCSize + (mazeRSize + 1) / 2);
		}
		else{
			visited = new boolean[mazeRSize][mazeCSize];
			mazeSize =mazeRSize*mazeCSize;
		}
		//pick a starting cell
		Cell startingCell = pickStartingCell();
		dfsr(startingCell);

	} // end of generateMaze()

	private void dfsr(Cell cell){
		//pick a random unvisited cell
		try {
		Cell newVisitNeigh = cell;
		Cell newNeighbour = pickUnvisitedNeighbour(newVisitNeigh);
		while(newNeighbour!=null){
			newVisitNeigh = newNeighbour;
			newNeighbour = pickUnvisitedNeighbour(newVisitNeigh);

		}
		if(newNeighbour==null && !stack.isEmpty()){
			cell = stack.pop();
			if(!stack.isEmpty())
				cell = stack.pop();
		}
		boolean perfecMaze = maze.isPerfect();

			if( allVisited<=mazeSize)
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
		return startCell;
	}

	private Cell pickUnvisitedNeighbour(Cell cell){
//		System.out.println("All visited: "+allVisited+",Stack size:"+stack.size());
		System.out.println(cell.r+", "+cell.c);

			visited[cell.r][cell.c]=true;
			stack.push(cell);
			allVisited++;
			int cellTotalNeighs = cell.neigh.length;
			Cell neighbour=null;
			int[] unvisitedneighbours=new int[cellTotalNeighs];
			int count=0;
			try {
			//Count number of not null neighbours and 
			//set their index in an array
			for(int i =0;i<cellTotalNeighs;i++){
				if(cell.neigh[i]!=null && !visited[cell.neigh[i].r][cell.neigh[i].c]){
					unvisitedneighbours[count]=i;
					count++;
				}
			}
			unVisitedNeighs = count;
			//If there is any not null neighbour then pick one randomly
			if(unVisitedNeighs!=0){
				int index = new Random().nextInt(count);
				direction = unvisitedneighbours[index];
				neighbour=cell.neigh[direction];
				maze.map[cell.r][cell.c].wall[direction].present=false;
			}
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}

		return neighbour;
	}
} // end of class RecursiveBacktrackerGenerator
