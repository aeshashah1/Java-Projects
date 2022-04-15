package transit;

import java.util.ArrayList;


public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
		TNode tempWalkZero= new TNode();
		TNode tempBusZero = new TNode();
		TNode tempTrainZero = new TNode();
		int busIndex = busStops.length-1;
		int trainIndex = trainStations.length-1;

		for (int i = locations.length-1; i>=0; i--){
			TNode walkNew = new TNode(locations[i], tempWalkZero, null);
			if (tempWalkZero.getLocation()==0){
				walkNew.setNext(null);
			}
			tempWalkZero = walkNew;
			if (busIndex > -1 && locations[i] == busStops[busIndex]){
				TNode busNew = new TNode(busStops[busIndex--], tempBusZero, tempWalkZero);
				if (tempBusZero.getLocation()==0){
					busNew.setNext(null);
				}
				tempBusZero = busNew;
			}
			if (trainIndex>-1 && locations[i] == trainStations[trainIndex]){
				TNode trainNew = new TNode(trainStations[trainIndex--], tempTrainZero, tempBusZero);
				if (tempTrainZero.getLocation()==0){
					trainNew.setNext(null);
				}
				tempTrainZero = trainNew;
			}
		}
		TNode walkZero = new TNode(0, tempWalkZero, null);
		TNode busZero = new TNode(0, tempBusZero, walkZero);
		trainZero = new TNode(0, tempTrainZero, busZero);
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 */
	public void removeTrainStation(int station) {
		TNode ptr = trainZero;
		while (ptr.getNext()!=null && ptr.getNext().getLocation()!=station){
			ptr = ptr.getNext();
		}
		if (ptr.getNext()==null) return;
		ptr.setNext(ptr.getNext().getNext());
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 */
	public void addBusStop(int busStop) {
		TNode busTraversal = trainZero.getDown();
		TNode walkTraversal = busTraversal.getDown();
		while(walkTraversal.getNext()!=null && walkTraversal.getLocation()!=busStop){
			walkTraversal = walkTraversal.getNext();
			if (busTraversal.getNext()!=null && walkTraversal.getLocation()==busTraversal.getNext().getLocation()) busTraversal = busTraversal.getNext();
		}
		if (walkTraversal.getLocation()<busStop || busTraversal.getLocation()==busStop) return;
		TNode newBusStop = new TNode(busStop, busTraversal.getNext(), walkTraversal);
		busTraversal.setNext(newBusStop);
	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 */
	public ArrayList <TNode> bestPath(int destination) {
		ArrayList<TNode> visited = new ArrayList<>();
		TNode visiting = trainZero;
		while (visiting.getLocation() != destination || visiting.getDown() != null) {
			visited.add(visiting);
			if (visiting.getNext() != null && visiting.getNext().getLocation() <= destination) visiting = visiting.getNext();
			else if (visiting.getDown() != null) visiting = visiting.getDown();
		}
		visited.add(visiting);
		return visited;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 */
	public TNode duplicate() {
		TNode newFirst = new TNode();
		TNode tempFirst = newFirst;
		TNode old = trainZero;
		TNode oldTemp;
		TNode newTemp;

		while(old != null)
		{
			newTemp = tempFirst;
			oldTemp = old.getNext();
			while(oldTemp != null)
			{
				newTemp.setNext(new TNode(oldTemp.getLocation()));
				newTemp = newTemp.getNext();
				oldTemp = oldTemp.getNext();
			}
			old = old.getDown();
			if(old != null)
			{
				tempFirst.setDown(new TNode(old.getLocation()));
				tempFirst = tempFirst.getDown();
			}
		}
		TNode newTC;
		tempFirst = newFirst;
		while(tempFirst.getDown() != null)
		{
			newTemp = tempFirst;
			newTC = tempFirst.getDown();
			while(newTemp != null)
			{
				while(newTC.getLocation() < newTemp.getLocation())
				{
					newTC = newTC.getNext();
				}
				newTemp.setDown(newTC);
				newTemp = newTemp.getNext();
			}
			tempFirst = tempFirst.getDown();
		}
		return newFirst;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 */
	public void addScooter(int[] scooterStops) {
		TNode scooterFirst = new TNode();
		TNode scootTemp = scooterFirst;
		TNode walkTemp = trainZero.getDown().getDown();
		TNode busTemp = trainZero.getDown();
		scooterFirst.setDown(walkTemp);

		for(int i = 0; i < scooterStops.length; i++)
		{
			scootTemp.setNext(new TNode(scooterStops[i]));
			scootTemp = scootTemp.getNext();
			while(walkTemp.getLocation() < scooterStops[i])
			{
				walkTemp = walkTemp.getNext();
			}
			scootTemp.setDown(walkTemp);
		}

		scootTemp = scooterFirst;
		while(busTemp != null)
		{
			while(scootTemp != null && scootTemp.getLocation() < busTemp.getLocation())
			{
				scootTemp = scootTemp.getNext();
			}
			busTemp.setDown(scootTemp);
			busTemp = busTemp.getNext();
		}
	}


	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
