import java.util.ArrayList;

// put your code here

public class EvaBot implements Bot {
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	// So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example
	
	private BoardAPI board;
	private PlayerAPI player;
	private int BotID;
	private boolean hasAttacked;
	ArrayList<Integer> bordering = new ArrayList<Integer>();
	

	EvaBot (BoardAPI inBoard, PlayerAPI inPlayer) {
		board = inBoard;	
		player = inPlayer;
		// put your code here
		
		hasAttacked = false;
		BotID = player.getId();
		return;
	}

	
	public String getName () {
		String command = "";
		// put your code here
		command = "EvaBot";
		return(command);
	}
	
	public String getReinforcement () {
		String command;
		// put your code here
		//So as we can see things are randomised
		//What we need to do is make a list of all possible moves by looping through and adding
		//With this we'll make a list of all

		/* What reinforcePositions does is that it gives us all the possible
		 * All we gotta do is loop through and add the positions to that the bot owns to the list */

		ArrayList<Integer> ownCountryUnits = new ArrayList<>();//We use this to
		ArrayList<Integer> ownedCountryIDs = new ArrayList<>();

		int ownership = BotID;
		int countryID;

		for(int i = 0; i < GameData.NUM_COUNTRIES; i++){
			int occupier = board.getOccupier(i);
			if(occupier == BotID){
				countryID = GameData.COUNTRY_IDS[i];
				//Number of units per country
				ownedCountryIDs.add(countryID);
				ownCountryUnits.add(board.getNumUnits(countryID));
			}
		}
		//We've acquired our list
		//Now we have to generate all possible board positions
		int numUnits = player.getNumUnits(); //This will tell us how many units we can use.
		ArrayList<int[]> resultingPositions = new ArrayList<>();
		ArrayList<int[]> probabilityOfWinning = new ArrayList<>();

		for (Integer ownCountryUnit : ownCountryUnits) {
			int[] arrayOfUpdates = new int[numUnits + 1];
			for (int j = 0; j <= numUnits; j++)
				arrayOfUpdates[j] = ownCountryUnit + j;
			//Then we can now add this array to the list of resulting positions
			resultingPositions.add(arrayOfUpdates);
		}
		/* Now  this is the hard part we now have to make assumptions of things
		 * We have to think about what exactly needs to be done depending on certain situations that are set in place
		 * We can do this by thinking about certain things
		 *
		 * //When to give the max
		 * 1. Is this he most reinforced country? If so give it the max
		 * 2. Is this country grouped? Then we can give it the max
		 *
		 * //When to give in between
		 * 1. If there are more than one min we consider
		 * 2. If there is then we can just randomise it.
		 *
		 * //When to give the minimum
		 * 1. If it is not surrounded we can give it units
		 * 2. If the max units plus the min is not less than the opposing country with the most units
		 * 3. */

		//boolean surrounded = false;boolean duplicateMax = false;

		int maxID = getMaxIndex(ownedCountryIDs), minID = getMinIndex(ownedCountryIDs);
		int max = ownCountryUnits.get(maxID);
		ArrayList<Integer> surroundedIDs = new ArrayList<>();
		ArrayList<Integer> maxIDs = new ArrayList<>();
		/* We can do this by checking to see if over half the adjacent countries are opposing */

		//int count = 0; //We'll use count to see if there are duplicates

		for(int i = 0; i < ownCountryUnits.size(); i++){
			int units = ownCountryUnits.get(i);
			if(max == units){
				//If we find a match we can increase the count found
				//count++;
				maxIDs.add(ownedCountryIDs.get(i));
			}
			//duplicateMax = true;
		}

		for(int id: ownedCountryIDs) //We use this to figure out if a country we own is surrounded
			if(isSurrounded(id))
				surroundedIDs.add(id);

		//Let's make a list of the probabilities
		int index = 0, unitsToGive = numUnits;

		for(int i = 0; i < resultingPositions.size(); i++){
			probabilityOfWinning.add(new int[numUnits]);
			for(int j = 0; j < numUnits; j++){
				if(maxIDs.contains(ownedCountryIDs.get(i))){
					probabilityOfWinning.get(i)[numUnits - 1] = 80; //80% chance of winning
				}
				if(surroundedIDs.contains(ownedCountryIDs.get(i))){
					//If it's a surrounded country we will be more inclined to give it the max
					if(probabilityOfWinning.get(i)[numUnits - 1] != 80)
						probabilityOfWinning.get(i)[numUnits - 1] = 80;
					else
						probabilityOfWinning.get(i)[numUnits - 1] = 100;
				}

				if(probabilityOfWinning.get(i)[j] != 80 || probabilityOfWinning.get(i)[j] != 100)
					probabilityOfWinning.get(i)[j] = 20;
				if(probabilityOfWinning.get(index)[unitsToGive - 1] < probabilityOfWinning.get(i)[j]){
					index = i;
					unitsToGive = j + 1;
				}
			}
		}
		

		command = GameData.COUNTRY_NAMES[ownedCountryIDs.get(index)];
		//command = GameData.COUNTRY_NAMES[(int)(Math.random() * GameData.NUM_COUNTRIES)];
		command = command.replaceAll("\\s", "");
		command += " " + unitsToGive;
		return(command);
	}
	
	public String getPlacement (int forPlayer) {

		String command = "";
		// put your code here
		
		//getPlacement is for neutrals and works off the same idea as player reinforcements
		command = GameData.COUNTRY_NAMES[getGreatestDiff(forPlayer,1)];
		command = command.replaceAll("\\s", "");
		return(command);
	}
	
	public String getCardExchange () {
		String command = "skip";

		if(player.isForcedExchange()) {
			int i=0,c=0,a=0,w=0;
			
			for(Card it : player.getCards()) {
				String type = it.getInsigniaName();
				if(type.equals("Infantry")) {i++;}
				else if(type.equals("Cavalry")) {c++;}
				else if(type.equals("Artillary")) {a++;}
				else {w++;}
			}
			
			if(i>=3) {command = "iii";}
			else if(c>=3) {command = "ccc";}
			else if(a>=3) {command = "aaa";}
			else if(i>=1 && c>=1 && a>=1) {command = "ica";}
			else if(i==2 && w>=1) {command = "iiw";}
			else if(c==2 && w>=1) {command = "ccw";}
			else if(a==2 && w>=1) {command = "aaw";}
			else if(i==1 && w==2) {command = "iww";}
			else if(c==1 && w==2) {command = "cww";}
			else if(a==1 && w==2) {command = "aww";}
			else if(i==1 && c==1 && w>=1) {command = "icw";}
			else if(i==1 && w>=1 && a==1) {command = "iwa";}
			else if(w>=1 && c==1 && a==1) {command = "wca";}
		}

		return(command);
	}

	public String getBattle () {
		
		//in order to increase the number of cards gained one strategy is to only attack once per turn
		//as such if we have already attacked and won we move onto the fortification stage
		if(hasAttacked) {
			hasAttacked = false;
			return "skip";}




		//strongestDiff finds the country with the weakest neighbour
		int StrongestDiff = getGreatestDiff(BotID, 0);

		//if there wasnt enough units to attack with or no bordering we skip our battle
		if(StrongestDiff  == -1) {return "skip";}

		//the attacked unit is just the weakest neighbour of the attacker
		int weakest = getWeakestNeighbour(StrongestDiff);

		if(weakest  == -1) {return "skip";}

		//Attack with the max number of units
		int units = getMaxRoll(StrongestDiff);

		//if we have 1 unit on every tile then units will be 0 which is impossible to attack with
		if(units == 0) {
			return "skip";
		}

		//parses into command
		return parseString(StrongestDiff,weakest,units);
	}

	public String getDefence (int countryId) {
		String command = "";
		// put your code here

		//defend with up to 2 rolls
		int units = getMaxRoll(countryId)-1;
		if(units <= 0) {return "1";}
		command += units;

		return(command);
	}

	public String getMoveIn (int attackCountryId) {

		String command = "";

		// put your code here
		
		hasAttacked = true;
		
		//if a country only borders friendlies it will move in its entire unit count
		if(checkBorderless(attackCountryId)) {
			command = String.valueOf(board.getNumUnits(attackCountryId)-1);
		}
		//otherwise it will move in half (there should probably be a formula
		//which computes relative strength of each node but this will do for now 
		else {
			command = String.valueOf(board.getNumUnits(attackCountryId)/2);
		}
		
		//again sleeps for testing
		return(command);
	}

	
	public String getFortify () {

		/* BRIEF COMMENT ON WHAT NEEDS TO BE DONE
		* We need to see possible reasons as to why a certain move would be more desirable than the other
		* What situations are we looking for?
		*
		* Well we can look for the country with the most units and see if it is not surrounded.
		* Pass it on to the country that needs it more, i.e a country that is surrounded
		* If none exists we can simply remove that country from the list and find the next max
		*
		* Follow the same process and so on so forth.*/


		String command = "";

		ArrayList<Integer> ownCountryUnits = new ArrayList<>();//We use this to
		ArrayList<Integer> ownedCountryIDs = new ArrayList<>();

		int ownership = player.getId();
		int countryID;

		for(int i = 0; i < GameData.NUM_COUNTRIES; i++){
			int occupier = board.getOccupier(i);
			if(occupier == player.getId()){
				countryID = GameData.COUNTRY_IDS[i];
				//Number of units per country
				ownedCountryIDs.add(countryID);
				ownCountryUnits.add(board.getNumUnits(countryID));
			}
		}

		//We first need to decide which territory we are going to use.
		ArrayList<ArrayList<Integer>> ownCountryAndAllies = new ArrayList<>();
		for(int i = 0; i < ownedCountryIDs.size(); i++){
			int[] adjacent = GameData.ADJACENT[i];
			ownCountryAndAllies.add(new ArrayList<>());
			if(contains(adjacent, ownedCountryIDs.get(i))){
				for (int k : adjacent) {
					if (board.getOccupier(k) == ownership)
						ownCountryAndAllies.get(i).add(k);
				}
			}
		}
		if(ownCountryAndAllies.isEmpty())
			return "skip";
		boolean found = false;
		//We now have a each country with all it's adjacent allies
		int sender = ownedCountryIDs.get(0);
		for(int i = 0; i < ownCountryAndAllies.size() && !found; i++){
			int maxID = getMaxIndex(ownCountryUnits);
			for(int id: ownCountryAndAllies.get(i)){
				if(isSurrounded(id)){
					sender = i;
					//receiver = id;
					countryID = id;
					found = true;
					break;
				}
			}
			if(!found)
				ownCountryAndAllies.remove(i);
		}

		if(!found){
			return ("skip");
		}

		//For now we assume that it should be given half of the units
		int sentUnits = (int)(Math.random() * ownCountryUnits.get(sender) / 2) + 1;

		//strongest now again refers to the largest difference between our node and another 
		//e.g we have 5 more units than our neighbour
		//we then find the largestConnected and move the units from there to the frontline
		//note also you could do it so you move all the non bordering units which probably should take precedence


		int Strongest = getGreatestDiff(BotID, 0);
		int fortifier = findLargestConnected(Strongest);
		if(Strongest == -1) {return "skip";}
		//if findlargestConnected returned -1 it ws not valid for some reason and we skip
		if(fortifier == -1) {return "skip";}
		
		int units = board.getNumUnits(Strongest)-1;
		
		if(units  == 0) {return "skip";}

		return parseString(Strongest, sender, sentUnits);
		//return parseString(fortifier,Strongest,units);
	}

	
	//end of public code these are the functions used to figure out moves
	
	

	
	private int getGreatestDiff(int id,int flag) {
		int unitDiff = (flag == 1)? 1000: -1000;
		int unitDiffIndex = -1;
		
		for(int Count = 0; Count <GameData.NUM_COUNTRIES;Count++) {
			if(board.getOccupier(Count) == id) {
				for(int Adj = 0;Adj < GameData.ADJACENT[Count].length ;Adj++) {
					

					//flag 1 finds the weakest pair e.g our 1 unit to their 10
					if(flag == 1 && board.getOccupier(GameData.ADJACENT[Count][Adj]) == getEnemyId()) {

						if(unitDiff > getUnitDifference(Count,GameData.ADJACENT[Count][Adj])) {
							unitDiff = getUnitDifference(Count,GameData.ADJACENT[Count][Adj]);
							unitDiffIndex = Count;
					}	
					
					}
					
					//flag 0 finds the strongest pair e.g our 10 to their 1
					if(flag == 0 && board.getOccupier(GameData.ADJACENT[Count][Adj]) != BotID) {

						if(unitDiff < getUnitDifference(Count,GameData.ADJACENT[Count][Adj])) {
							unitDiff = getUnitDifference(Count,GameData.ADJACENT[Count][Adj]);
							unitDiffIndex = Count;

					}
					
					}
					
					
					
				}	
			}
		}
		
		//if we find that there is somehow all equal amounts of difference then we instead return our largest node
		if(unitDiffIndex == -1) {
			for(int Count = 0; Count <GameData.NUM_COUNTRIES;Count++) {
				if(board.getOccupier(Count) == id) {
					if(unitDiffIndex == -1) {unitDiffIndex = Count;}
					if(board.getNumUnits(Count) > board.getNumUnits(unitDiffIndex)) {
						unitDiffIndex = Count; 
					
				}
				}
			}
		}
		
		return unitDiffIndex;
	}


	private int getUnitDifference(int id, int id2) {
		return board.getNumUnits(id) - board.getNumUnits(id2);
	}
	
	private int getEnemyId(){
		if(BotID == 0){ return 1; }
		return 0;
	}
	
	//finds weakestNode in relation to our node 
	private int getWeakestNeighbour(int id) {
				int leastIndex = -1;
				int least = -1000;
				for(int Adj = 0;Adj < GameData.ADJACENT[id].length ;Adj++) {

				if(board.getOccupier(GameData.ADJACENT[id][Adj]) != BotID && least < getUnitDifference(id,GameData.ADJACENT[id][Adj])) {

					least = getUnitDifference(id,GameData.ADJACENT[id][Adj]);
					leastIndex = GameData.ADJACENT[id][Adj];

				}
				}
				//if least is less than 0 means that all neighbours are stronger than us and we shouldnt attack 
				if(least < 0) {
					return -1;
				}
				
	return leastIndex;
	}
	
	
	//checks whether a country only borders friendlies 
	private boolean checkBorderless(int id) {
	
		for(int Adj = 0;Adj < GameData.ADJACENT[id].length ;Adj++) {
			if(board.getOccupier(GameData.ADJACENT[id][Adj]) != id) {
				return false;
			}
			
		}
			return true;
	}
	
	
	//finds the maximum number of rolls we can use 
	private int getMaxRoll(int id) {
		if(board.getNumUnits(id) > 3) {return 3;}
		return board.getNumUnits(id)-1;
	}
	
	//utility for parsing command input
	private String parseString(int attacker,int defender, int units) {
		String returnString = GameData.COUNTRY_NAMES[attacker];
		returnString = returnString.replaceAll("\\s", "");
		String defenderName = GameData.COUNTRY_NAMES[defender];
		defenderName = defenderName.replaceAll("\\s", "");
		
		returnString += " " + defenderName + " " + units;
		
		return returnString;
	}
	
	//finds all friendly nodes connected to a node and returns the largest node
	private int findLargestConnected(int id) {
		int biggestIndex = -1;
		int biggest = -1;
		
		for(int Count = 0; Count <GameData.NUM_COUNTRIES;Count++) {
			if(board.getOccupier(Count) == BotID) {
				if(board.isConnected(id, Count)) {
					
					if(biggest < board.getNumUnits(Count)) {
						biggest = board.getNumUnits(Count);
						biggestIndex = Count;
					}
					
				}
				
		}
		}
		return biggestIndex;
	}


	public boolean isSurrounded(int countryID){
		int[] adjacents = GameData.ADJACENT[countryID];
		int occupier = board.getOccupier(countryID);
		int count = 0;
		for(int i: adjacents){
			if(board.getOccupier(i) != occupier)
				count++;
		}
		return count >= adjacents.length;
	}

	public int getMaxIndex(ArrayList<Integer> list){
		if(list.isEmpty())
			throw new IllegalArgumentException("Empty Array List");
		int index = 0;
		for(int i = 0; i < list.size(); i++){
			if(list.get(index) < list.get(i))
				index = i;
		}
		return index;
	}

	public int getMinIndex(ArrayList<Integer> list){
		if(list.isEmpty())
			throw new IllegalArgumentException("Empty Array List");
		int index = 0;
		for(int i = 0; i < list.size(); i++){
			if(list.get(index) > list.get(i))
				index = i;
		}
		return index;
	}

	public boolean isAllyNear(int countryID){
		int[] adjacents = GameData.ADJACENT[countryID];
		int occupier = board.getOccupier(countryID);
		for(int i: adjacents){
			if(board.getOccupier(i) == occupier)
				return true;
		}
		return false;
	}

	public boolean contains(int[] array, int number){
		for (int j : array) {
			if (j == number)
				return true;
		}
		return false;
	}
	
}
