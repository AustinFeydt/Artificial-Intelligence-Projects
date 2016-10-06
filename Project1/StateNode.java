/**@author Austin Feydt
 * EECS 391
 * Project 1
 * This class represents a StateNode and its available functions*/
import java.util.Arrays;
public class StateNode {
	private int state [][];
	private int heuristicCost;
	private int actualCost;
	private int totalCost;
	private String path;
	
	// 4-arg constructor to initialize the StateNode and its associated values
	public StateNode(int[][] state, int heuristicCost, int actualCost, String path){
		this.state = state;
		this.heuristicCost = heuristicCost;
		this.actualCost = actualCost;
		this.setTotalCost(this.getHeuristicCost() + this.getActualCost());
		this.path = path;
	}
	
	// Sets the state to desired state, if it is a valid state
	public void setState(String state){
		state = state.replace(" ", "");
		if(this.validateState(state)){
			this.buildPuzzle(state);
		}
	}
	
	// Prints the layout of the puzzle state to the console
	public void printState(){
		System.out.println("Current State:");
		for(int i = 0; i < this.state.length; i++){
			System.out.print("|");
			for (int j = 0; j < this.state[i].length; j++){
				if(this.state[i][j] == 0)
					System.out.print("b|");
				else
					System.out.print(this.state[i][j] + "|");
			}
			System.out.print("\n");
		}
		System.out.println("\n");
	}
	
	// Moves the blank tile in the give direction, if it is a valid move
	public int[][] move(String direction){
		int[] blankCoordinates  = this.findBlankCoordinates();
		
		switch (direction){
			case "up":{
				if (blankCoordinates[0] != 0){
					return this.swapTiles(blankCoordinates[0], blankCoordinates[1], blankCoordinates[0] -1, blankCoordinates[1]);
					
				}
				else{
					return null;
				}
			}
			case "down":{
				if(blankCoordinates[0] != 2){
					return this.swapTiles(blankCoordinates[0], blankCoordinates[1], blankCoordinates[0] + 1, blankCoordinates[1]);
				}
				else{
					return null;
				}
			}
			case "left":{
				if(blankCoordinates[1] != 0){
					return this.swapTiles(blankCoordinates[0], blankCoordinates[1], blankCoordinates[0], blankCoordinates[1] - 1);
				}
				else{
					return null;
				}
			}
			case "right":{
				if(blankCoordinates[1] != 2){
					return this.swapTiles(blankCoordinates[0], blankCoordinates[1], blankCoordinates[0], blankCoordinates[1] + 1);
				}
				else{
					return null;
				}
			}
			default:{
				return null;
			}
		}
	}
	
	// Checks to see if incoming state assigment is a legal assignment
	private boolean validateState (String state){
		
		if(state.length() != 9){
			System.out.println("Invalid input: Please only choose 9 values for the state!\n");
			return false;
		}
		
		for (int i = 0; i < state.length(); i++){
			if ((Character.getNumericValue(state.charAt(i)) < 1 || Character.getNumericValue(state.charAt(i)) > 8) && state.charAt(i) != 'b'){
				System.out.println("Invalid input: Please only use numbers 1,8 and 'b'!\n");
				return false;
			}
			
			else if(state.indexOf(state.charAt(i)) != state.lastIndexOf(state.charAt(i))){
				System.out.println("Invalid input: Please only use each entry once!\n");	
				return false;
			}
		}
		return true;
		
	}
	
	// Builds the 3x3 array with the values in the incoming state
	public void buildPuzzle(String state){
		int rowIndex = 0;
		int colIndex = 0;
		
		for(int i = 0; i < state.length(); i++){
			if(state.charAt(i) == 'b'){
				this.state[rowIndex][colIndex] = 0;
			}
			else{
				this.state[rowIndex][colIndex] = Character.getNumericValue(state.charAt(i));
			}
			
			colIndex++;
			
			if (colIndex == 3){
				colIndex = 0;
				rowIndex++;
			}	
		}
	}
	
	// Find the current coordinates of the blank tile on the puzzle board
	public int[] findBlankCoordinates(){
		int[] coordinates = new int[2];
		
		for (int i = 0; i < this.state.length; i++){
			for (int j = 0; j < this.state[i].length; j++)
				if (this.state[i][j] == 0){
					coordinates[0] = i;
					coordinates[1] = j;
				}
		}
		return coordinates;
	}
	
	// Swaps two tiles on a puzzle board
	public int[][] swapTiles(int blankRow, int blankCol, int newRow, int newCol){
		int[][] newPuzzle = this.copyArray(this.getState());
		int tempNum = newPuzzle[newRow][newCol];
		newPuzzle[blankRow][blankCol] = tempNum;
		newPuzzle[newRow][newCol] = 0;

		return newPuzzle;
	}
	
	// Copies contents from one array to another
	private int[][] copyArray(int[][] array){
		int[][] newArray = new int[array.length][array.length];
		for(int i = 0; i < array.length; i++){
			for(int j = 0; j < array[i].length; j++){
				newArray[i][j] = array[i][j];
			}
		}
		return newArray;
	}	
	
	@Override
	// Overridden equals method to compare the puzzle states 
	public boolean equals(Object obj){
		StateNode compare = (StateNode)obj;
		for (int i = 0; i < this.getState().length; i++){
			if (!Arrays.equals(this.getState()[i], compare.getState()[i])){
				return false;
			}
		}
		return true;
	}
	
	@Override 
	public int hashCode(){
		return 5*( this.getState()[0][0] + this.getState()[0][1] + this.getState()[0][2]);
	}
	


	public int[][] getState() {
		return this.state;
	}

	public void setState(int[][] state) {
		this.state = state;
	}

	public int getHeuristicCost() {
		return this.heuristicCost;
	}

	public void setHeuristicCost(int heuristicCost) {
		this.heuristicCost = heuristicCost;
	}
	
	public int getActualCost(){
		return this.actualCost;
	}
	
	public void setActualCost(int actualCost){
		this.actualCost = actualCost;
	}
	
	public int getTotalCost() {
		return this.totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
