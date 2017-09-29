package mazeGenerator;

import maze.Maze;

public class ModifiedPrimsGenerator implements MazeGenerator {

	public ArrayList<Cell> getNeighbours(Maze maze, Cell cell) {
		ArrayList<Cell> neighbours = new ArrayList<>();
		for(int i =0; i < maze.sizeR; i++) {
			for (int j=0; j<maze.NUM_DIR; j++) {
				Cell neigh = maze.map[i + maze.deltaR[k]][i + maze.deltaC[k]];
				neighbours.add();
			}
			return neighbours;
		}
	}

	@Override
	public void generateMaze(Maze maze) {
		// TODO Auto-generated method stub

		// Create visited set
		ArrayList<Cell> visited = new ArrayList<>();
		// Create frontier set
		ArrayList<Cell> frontier = new ArrayList<>();

		Cell entrance = maze.entrance;
		ArrayList<Cell> neighbours = getNeighbours(maze, entrance);
		visited.add(entrance);

	} // end of generateMaze()

} // end of class ModifiedPrimsGenerator
