/**@author Austin Feydt
 * EECS 391
 * Project 1
 * This class represents a comparator for StateNodes*/
import java.util.*;
public class StateNodeComparator implements Comparator<StateNode> {
	
	//Compares the TotalCost of two StateNodes
	public int compare(StateNode node1, StateNode node2){
		if (node1.getTotalCost() < node2.getTotalCost())
			return -1;
		else if(node1.getTotalCost() == node2.getTotalCost())
			return 0;
		else
			return 1;
	}
	
	//Returns whether or not two Comparators are equal
	public boolean equals(StateNodeComparator comparator){
		return super.equals(comparator);
	}

}
