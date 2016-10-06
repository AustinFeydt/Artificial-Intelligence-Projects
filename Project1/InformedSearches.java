/**@author Austin Feydt
 * EECS 391
 * Project 1
 * This class represents the two search methods and their helper functions*/
import java.util.*;
public class InformedSearches {
	private HashSet<StateNode> visitedNodes;			// keeps track of already visited nodes
	private PriorityQueue<StateNode> explorableNodes;	// keeps track of nodes that have been discovered
	private String heuristic;
	private int maxNodes = 0;
	private int nodesExplored = 0;
	private int beamLimit = 0;
	
	// Used for testing
	private boolean solved = false;
	public boolean printToConsole = true;
	public int pathLength = 0;


	// 1 arg constructor to initialize the InformedSearches object based on heuristic choice
	public InformedSearches(String heuristic){
		this.setVisitedNodes(new HashSet<StateNode>());
		this.explorableNodes = new PriorityQueue<StateNode>(new StateNodeComparator());
		this.heuristic = heuristic;
	}
	
	// Clears all traces of last search
	public void resetSearch(){
		this.getVisitedNodes().clear();
		this.getExplorableNodes().clear();
		this.setSolved(false);
		this.setNodesExplored(0);
		this.pathLength = 0;
		
	}
		
	/** This method is an A* search using either a misplaced tiles heuristic or a Manhattan distance heuristic
	 * @param initialPuzzle  initial puzzle state
	 * @return  whether or not puzzle was solved*/
	public boolean aStarSearch(int[][] initialPuzzle){
		this.resetSearch();
		StateNode root = new StateNode(initialPuzzle, this.calculateHeuristic(initialPuzzle), 0, "");
		
		// Add root to the queue
		this.getExplorableNodes().add(root);
		
		// Begin recursive calls
		this.exploreNodeAStar();
		return this.isSolved();
	}
	
	//This helper method evaluates the current root node and determines what to do with the node
	public void exploreNodeAStar(){
		nodesExplored++;
		// Remove the best node to expand
		StateNode currentNode = this.getExplorableNodes().poll();
		
		// Check if hash is getting too big
		if(this.getVisitedNodes().size() > 3000){
			this.resizeHash();
		}
		
		// Checks if the polled node was null - we couldn't find a solution
		if(currentNode == null){
			if(printToConsole)
				System.out.println("No A* solution found: Invalid starting state");
		}
		// Checks if too many nodes have been expanded - exit early
		else if(nodesExplored > maxNodes){
			if(printToConsole)
				System.out.println("No A* solution found: Exceeded maxnodes ");
		}
		// Checks if we have reached the goal
		else if(currentNode.getHeuristicCost() == 0){
			if(printToConsole){
				System.out.println("A* " + this.getHeuristic() + " Solution Found in " + currentNode.getActualCost() + " moves.\n" +
						"Solution from initial state: " + currentNode.getPath() + "\n" +
						"Nodes explored for search: " + this.nodesExplored + "\n");
			}
			pathLength = currentNode.getActualCost();
			this.setSolved(true);
		}
		// if the puzzle state has already been visited, move on
		else if(!this.getVisitedNodes().add(currentNode)){
			 this.exploreNodeAStar();
		}	
		else{
			// expand the node to get it's children and add to queue
			this.getExplorableNodes().addAll(this.findChildren(currentNode));
			// continue recursive calls
			this.exploreNodeAStar();
		}
	}
	
	// Helper method to return all possible children for the root
	public ArrayList<StateNode> findChildren(StateNode root){
		ArrayList<StateNode> children = new ArrayList<StateNode>();
		// Try to move the blank all 4 directions
		int[][] upPuzzle = root.move("up");
		int[][] downPuzzle = root.move("down");
		int[][] leftPuzzle = root.move("left");
		int[][] rightPuzzle = root.move("right");
		
		// create StateNodes if applicable
		if (upPuzzle != null){
			children.add(new StateNode(upPuzzle, this.calculateHeuristic(upPuzzle), root.getActualCost() + 1, root.getPath() + "up "));
		}
		if (downPuzzle != null){
			children.add(new StateNode(downPuzzle, this.calculateHeuristic(downPuzzle), root.getActualCost() + 1, root.getPath() + "down "));
		}
		if (leftPuzzle != null){
			children.add(new StateNode(leftPuzzle, this.calculateHeuristic(leftPuzzle), root.getActualCost() + 1, root.getPath() + "left "));
		}
		if (rightPuzzle != null){
			children.add(new StateNode(rightPuzzle, this.calculateHeuristic(rightPuzzle), root.getActualCost() + 1, root.getPath() + "right "));
		}
		
		return children;
	}
	
	/** This method is an beam search using Manhattan distance heuristic and a beamLimit = k
	 * @param initialPuzzle  initial puzzle state
	 * @param k  the limit to number of states to consider
	 * @return  whether or not puzzle was solved*/
	public boolean localBeamSearch(int k, int[][] initialPuzzle){
		this.setHeuristic("h2");
		this.resetSearch();
		StateNode root = new StateNode(initialPuzzle, this.calculateHeuristic(initialPuzzle), 0, "");
		// add the root to the queue
		this.getExplorableNodes().add(root);
		this.beamLimit = k;
		// begin recursive calls
		this.exploreNodeBeam();
		return this.isSolved();
		
	}
	
	//This helper method evaluates the current root node and determines what to do with the node
	public void exploreNodeBeam(){
		StateNode currentNode = this.getExplorableNodes().poll();
		nodesExplored++;
		
		// Check if we need to resize hash
		if(this.getVisitedNodes().size() > 3000){
			this.resizeHash();
		}
		// Check if there are no more explorable nodes - search failed
		if(currentNode == null){
			if(printToConsole)
				System.out.println("Beam Search was unable to find a solution.");
		}
		// Check if too many nodes have been expanded
		else if(nodesExplored > maxNodes){
			if(printToConsole)
				System.out.println("No Beam Search Solution: Exceeded max nodes");
		}
		// Check if we reached the goal 
		else if(currentNode.getHeuristicCost() == 0){
			if(printToConsole){
				System.out.println("Beam Search Solution Found in " + currentNode.getActualCost() + " moves.\n" +
						"Solution from initial state: " + currentNode.getPath() + "\n" +
						"Nodes explored for search:" + this.nodesExplored + "\n");
			}
			pathLength = currentNode.getActualCost();
			this.setSolved(true);
		}
		// if the puzzle state has already been visited, move on
		else if(!this.getVisitedNodes().add(currentNode)){
			this.exploreNodeBeam();
		}
		else{
			// get children, add them to queue
			this.getExplorableNodes().addAll(this.findChildren(currentNode));
			// See if we need to trim the queue
			if(this.getExplorableNodes().size() > this.beamLimit)
				this.trimQueue();
			// continue recursive calls
			this.exploreNodeBeam();
		}
	}

	// this helper determines which heuristic to calculate
	public int calculateHeuristic(int[][] puzzle){
		if (this.getHeuristic().equalsIgnoreCase("h1"))
			return calculateH1(puzzle);
		else
			return calculateH2(puzzle);
	}
	
	// calculates number of tiles that are misplaced
	public static int calculateH1(int[][] puzzle){
		int misplacedCount = 0;
		int elementCounter = 0;
		for (int i = 0; i < puzzle.length; i++){
			for (int j = 0; j < puzzle[i].length; j++, elementCounter++){
				if(puzzle[i][j] != elementCounter){
					misplacedCount++;
				}
			}
		}
		return misplacedCount;
	}
	
	// calculates Manhattan distance between each tile's current position and goal position
	public static int calculateH2(int[][] puzzle){
		int manhattanDistance = 0;
		for(int i = 0; i < puzzle.length; i++){
			for(int j = 0; j < puzzle[i].length; j++){
				manhattanDistance += Math.abs(puzzle[i][j]/3 - i);
				manhattanDistance += Math.abs(puzzle[i][j]%3 - j);
			}
		}
		return manhattanDistance;
	}
	
	// resizes the hash if it gets too large
	public void resizeHash(){
		ArrayList<StateNode> tempNodes = new ArrayList<StateNode>();
		tempNodes.addAll(this.getVisitedNodes());
		this.getVisitedNodes().clear();
		tempNodes.sort(new StateNodeComparator());
		this.getVisitedNodes().addAll(tempNodes.subList(0, tempNodes.size() - 500));
	}
	
	// keeps the queue at the length of the beaLimit
	public void trimQueue(){
		ArrayList<StateNode> tempStorage = new ArrayList<StateNode>();
		for (int i = 0; i < this.beamLimit; i++){
			tempStorage.add(this.getExplorableNodes().poll());
		}
		this.getExplorableNodes().clear();
		this.getExplorableNodes().addAll(tempStorage);
	}
	
	
	public boolean setHeuristic(String heuristic){
		if(heuristic.equalsIgnoreCase("h1") || heuristic.equals("h2")){
			this.heuristic = heuristic;
			return true;
		}
		else
			return false;
		
	}
	
	public String getHeuristic(){
		return this.heuristic;
	}
	
	public void setExplorableNodes(PriorityQueue<StateNode> nodes){
		this.explorableNodes = nodes;
	}
	
	public PriorityQueue<StateNode> getExplorableNodes(){
		return this.explorableNodes;
	}


	public Set<StateNode> getVisitedNodes() {
		return this.visitedNodes;
	}


	public void setVisitedNodes(HashSet<StateNode> visitedNodes) {
		this.visitedNodes = visitedNodes;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}
	
	public int getMaxNodes(){
		return this.maxNodes;
	}
	
	public void setMaxNodes(int maxNodes){
		this.maxNodes = maxNodes;
	}
	
	public int getNodesExplored(){
		return this.nodesExplored;
	}
	
	public void setNodesExplored(int nodesExplored){
		this.nodesExplored = nodesExplored;
	}
	
	public int getBeamLimit(){
		return this.beamLimit;
	}
	
	public void setBeamLimit(int beamLimit){
		this.beamLimit = beamLimit;
	}
}
