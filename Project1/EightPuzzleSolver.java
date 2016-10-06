/**@author Austin Feydt
 * EECS 391
 * Project 1
 * This class represents an Eight Puzzle application */
import java.util.*;
import java.io.*;
public class EightPuzzleSolver {
	
	static StateNode currentState = new StateNode(new int[3][3], 0,0,"");
	static Random moveGenerator = new Random(1111);
	static 	InformedSearches searcher = new InformedSearches("");


	// this main method either reads from a file or from console input
	public static void main(String[] args){
		String userChoice = "";
		Scanner keyboard = new Scanner(System.in);
		
		if(args.length == 0){
			printCommands();
			do{
				System.out.println("Please enter a command:");
				userChoice = keyboard.nextLine();
				evaluateInput(userChoice);
			}while(!userChoice.equalsIgnoreCase("exit"));
			
			keyboard.close();
			System.exit(0);
			
		}
		// attempts to read the file and follow commands in it
		else if(args.length == 1){
			String path = args[0];
			File file = new File(path);
			try{
				BufferedReader br = new BufferedReader(new FileReader(file));
				System.out.println("File has successfully been opened.\n" +
						"Reading file...");

				String line;
				while ((line = br.readLine()) != null){
					evaluateInput(line);
				}
				br.close();
			}
			catch(IOException notFound){
				System.out.println(notFound.getMessage());
			}
		}			
	}
	
	// This method prints a intro paragraph to users in console
	public static void printCommands(){
		System.out.println("Welcome to Austin Feydt's 8-Puzzle Solver!\n" + 
				"Available commands:\n" +
				"setState <state> : sets the puzzle state using input\n" +
				"randomizeState n : makes n random moves from goal state, and overwrite previous state to this random state\n"+
				"printState : prints the current puzzle state\n" +
				"move <direction> : moves the blank tile 'up', 'down', 'left', or 'right\n" +
				"solve A-star <heuristic> : solves the puzzle from current state using A-star using heuristic 'h1' or 'h2'\n\t" +
				"h1 = # of misplaced tiles\n\t" +
				"h2 = Manhattan distance\n" +
				"solve beam <k> : uses h2 : solves the puzzle from current state using local beam search w/ 'k' states using heuristic 'h2'\n" +
				"maxNodes <n> : specifies the max number of nodes to be considered during search\n" +
				"heuristictest : tests both heuristic calculations\n" +
				"runexperiments : runs the experiment section of the writeup\n" +
				"exit : exits the program");
	}
	
	// This method tries to determine what command user is trying to use
	public static void evaluateInput(String userChoice){
		userChoice = userChoice.toLowerCase();
		// Split the input string by spaces
		String[] tokens = userChoice.split(" ");
		int tokenLength = tokens.length;
		
		// look at the first token 
		// 1 case per user command available
		switch(tokens[0]){
			case "setstate":{
				if(tokenLength == 4)
					currentState.setState(tokens[1] + tokens[2] + tokens[3]);
				else
					System.out.println("Invalid input: State should have format: 'xxx xxx xxx'\n");
				break;
			}
			
			case "randomizestate":{
				if(tokenLength == 2)
					randomizeState(Integer.parseInt(tokens[1]));
				else
					System.out.println("Invalid input: Please enter the number of moves you'd like to randomly make!\n");
				break;
			}
			
			case "printstate":{
				currentState.printState();
				break;
			}
			
			case "move":{
				if(tokenLength == 2){
					int[][] movedPuzzle = currentState.move(tokens[1]);
					if(movedPuzzle == null)
						System.out.println("Invalid input: Tile could not be moved that direction.\n");
					else
						currentState.setState(movedPuzzle);
				}
				else
					System.out.println("Invalid input: Specify what direction you'd like to move tile.\n");
				break;
			}
			
			case "solve":{
				boolean solved = false;
				if(tokenLength == 3 ){
					if(tokens[1].equalsIgnoreCase("a-star")){
						if(searcher.setHeuristic(tokens[2]))
							try{
								solved = searcher.aStarSearch(currentState.getState());
							}
							catch(StackOverflowError e){
								System.out.println("Memory allocation exceeded before max-nodes was reached. Please choose a smaller max node number.");
							}
						else
							System.out.println("Invalid input: please enter 'h1' or 'h2' for heuristic\n");
					}
					else if(tokens[1].equalsIgnoreCase("beam")){
						int k = Integer.parseInt(tokens[2]);
						if(k > 0)
							try{
								solved = searcher.localBeamSearch(k, currentState.getState());
							}
							catch(StackOverflowError e){
								System.out.println("Memory allocation exceeded before max-nodes was reached. Please choose a smaller max node number.");
							}
						else
							System.out.println("Invalid input: k must be greater than 0\n");
					}
					else
					System.out.println("Invalid input: only 'a-star' and 'beam' searches supported\n");
				}
				if (solved){
					randomizeState(0);
				}
				break;
			}
			
			case "maxnodes":{
				if (tokenLength == 2 && Integer.parseInt(tokens[1]) > 0){
					searcher.setMaxNodes(Integer.parseInt(tokens[1]));
				}
				else
					System.out.println("Invalid input: Please choose a positive integer for the max number of nodes\n");
				break;
			}
			
			case "heuristictest":{
				System.out.println("Testing heuristic costs:");
				System.out.println("H1 (misplaced tiles):" + InformedSearches.calculateH1(currentState.getState()));
				System.out.println("H2 (Manhattan distance):" + InformedSearches.calculateH2(currentState.getState()) + "\n");
				break;
			}
			
			case "runexperiments":{
				testSearchMethods();
			}
			case "exit":{
				System.exit(0);
				break;
			}
			
			default:{
				System.out.println("Invalid input: Unrecognized command\n");
				break;
			}
		}
		
	}
		
	// creates a random solvable state by making "moves" moves from goal state
	public static void randomizeState (int moves){
		int nextMove = -1;
		currentState.setState("b12 345 678");
		
		// Tries to move a tile in one direction, and if its not possible, it moves it the other direction
		for(int count = 0; count < moves; count++){
			nextMove = moveGenerator.nextInt(4);
			switch (nextMove){
				case 0:{
					if(currentState.move("up") == null)
						currentState.setState(currentState.move("down"));
					else
						currentState.setState(currentState.move("up"));
					break;
				}
				case 1:{
					if(currentState.move("down") == null)
						currentState.setState(currentState.move("up"));
					else
						currentState.setState(currentState.move("down"));
					break;
				}
				case 2:{
					if(currentState.move("left") == null)
						currentState.setState(currentState.move("right"));
					else
						currentState.setState(currentState.move("left"));
					break;
				}
				case 3:{
					if(currentState.move("right") == null)
						currentState.setState(currentState.move("left"));
					else
						currentState.setState(currentState.move("right"));
					break;
				}
			}
		}
		
	}
	
	// This method tests certain aspects of the search algorithms
	public static void testSearchMethods(){
		double h1Solvable500Max = 0, h1Solvable1000Max = 0, h1Solvable2000Max = 0;
		double h2Solvable500Max = 0, h2Solvable1000Max = 0, h2Solvable2000Max = 0;
		double beamSolvable500Max = 0, beamSolvable1000Max = 0, beamSolvable2000Max = 0;
		double h1SolvablePathLength = 0, h2SolvablePathLength = 0, beamSolvablePathLength = 0;
		System.out.println("COUNTING COMPLETED PUZZLES (this will take a bit)");
		searcher.printToConsole = false;
		
		
		//MaxNodes = 500 TEST
		//Testing the same 500 states for all 3 search algorithms
		for (int i = 0; i < 500; i++){
			//Set the maxNodes to 500
			searcher.setMaxNodes(500);
					
			//Randomize the state
			randomizeState(200);
					
			//Test H1
			searcher.setHeuristic("h1");
			searcher.aStarSearch(currentState.getState());
			if(searcher.isSolved())
				h1Solvable500Max++;
					
			//Test H2
			searcher.setHeuristic("h2");
			searcher.aStarSearch(currentState.getState());
			if(searcher.isSolved())
				h2Solvable500Max++;
					
			//Test beamsearch
			searcher.localBeamSearch(50, currentState.getState());
			if(searcher.isSolved())
				beamSolvable500Max++;
		}
		
		//MaxNodes = 1000 TEST
		//Testing the same 500 states for all 3 search algorithms
		for (int i = 0; i < 500; i++){
			//Set the maxNodes to 1000
			searcher.setMaxNodes(1000);
					
			//Randomize the state
			randomizeState(200);
					
			//Test H1
			searcher.setHeuristic("h1");
			searcher.aStarSearch(currentState.getState());
			if(searcher.isSolved())
				h1Solvable1000Max++;
					
			//Test H2
			searcher.setHeuristic("h2");
			searcher.aStarSearch(currentState.getState());
			if(searcher.isSolved())
				h2Solvable1000Max++;
					
			//Test beamsearch
			searcher.localBeamSearch(50, currentState.getState());
			if(searcher.isSolved())
				beamSolvable1000Max++;
		}
		
		//MaxNodes = 2000 TEST
		//Testing the same 500 states for all 3 search algorithms
		for (int i = 0; i < 500; i++){
			//Set the maxNodes to 2000
			searcher.setMaxNodes(2000);
					
			//Randomize the state
			randomizeState(200);
					
			//Test H1
			searcher.setHeuristic("h1");
			searcher.aStarSearch(currentState.getState());
			if(searcher.isSolved()){
				h1Solvable2000Max++;
				h1SolvablePathLength += searcher.pathLength;
			}	
					
			//Test H2
			searcher.setHeuristic("h2");
			searcher.aStarSearch(currentState.getState());
			if(searcher.isSolved()){
				h2Solvable2000Max++;
				h2SolvablePathLength += searcher.pathLength;
			}
					
			//Test beamsearch
			searcher.localBeamSearch(50, currentState.getState());
			if(searcher.isSolved()){
				beamSolvable2000Max++;
				beamSolvablePathLength += searcher.pathLength;
			}
		}
		
		System.out.println("RESULTS:" + 
				"\n\tFraction of solvable puzzles from A* Search with H1" + 
				"\n\t\tMaxNodes=500: " + h1Solvable500Max/500 +
				"\n\t\tMaxNodes=1000: " + h1Solvable1000Max/500 + 
				"\n\t\tMaxNodes=2000: " + h1Solvable2000Max/500 + 
				"\n\tFraction of solvable puzzles from A* Search with H2" + 
				"\n\t\tMaxNodes=500: " + h2Solvable500Max/500 +
				"\n\t\tMaxNodes=1000: " + h2Solvable1000Max/500 + 
				"\n\t\tMaxNodes=2000: " + h2Solvable2000Max/500 + 
				"\n\tFraction of solvable puzzles from beam search with k = 50" + 
				"\n\t\tMaxNodes=500: " + beamSolvable500Max/500 +
				"\n\t\tMaxNodes=1000: " + beamSolvable1000Max/500 + 
				"\n\t\tMaxNodes=2000: " + beamSolvable2000Max/500 + 
				"\n\tAverage Solution Length for A* with H1:" + h1SolvablePathLength/h1Solvable2000Max +
				"\n\tAverage Solution Length for A* with H2:" + h2SolvablePathLength/h2Solvable2000Max +
				"\n\tAverage Solution Length for Beam with k = 50:" + beamSolvablePathLength/beamSolvable2000Max);	
	}
}
