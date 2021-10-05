import java.util.ArrayList;

public class AttackerBot implements Bot {
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	// So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example
	
	private final BoardAPI board;
	private final PlayerAPI player;
	
	AttackerBot (BoardAPI inBoard, PlayerAPI inPlayer) {
		board = inBoard;	
		player = inPlayer;
		// put your code here
	}
	
	public String getName () {
		String command;
		// put your code here
		command = "BOT";
		return(command);
	}

	public String getReinforcement () {
		String command = "";
		// put your code here
		//So as we can see things are randomised
		//What we need to do is make a list of all possible moves by looping through and adding
		//With this we'll make a list of all

		/* What reinforcePositions does is that it gives us all the possible
		 * All we gotta do is loop through and add the positions to that the bot owns to the list */

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

		int maxID = 0, minID = 0;
		for(int i = 0; i < ownCountryUnits.size(); i++){
			if(ownCountryUnits.get(maxID) < ownCountryUnits.get(i))
				maxID = i;
			if (ownCountryUnits.get(minID) > ownCountryUnits.get(i))
				minID = i;
		}

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

		for(int id: ownedCountryIDs){ //We use this to figure out if a country we own is surrounded
			int enemyCount = 0;
			int[] adjacentCountries = GameData.ADJACENT[id];
			for(int adjacentID: adjacentCountries){
				if(board.getOccupier(adjacentID) != ownership)
					enemyCount++;
			}
			if(enemyCount >= adjacentCountries.length/2){
				surroundedIDs.add(id);
				//surrounded = true;
			}
		}
		//Let's make a list of the probabilities
		int index = 0, unitsToGive = numUnits;

		for(int i = 0; i < resultingPositions.size(); i++){
			probabilityOfWinning.add(new int[numUnits]);
			for(int j = 0; j < resultingPositions.get(i).length; j++){
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

		//command = GameData.COUNTRY_NAMES[ownedCountryIDs.get(index)];
		//command = GameData.COUNTRY_NAMES[(int)(Math.random() * GameData.NUM_COUNTRIES)];
		command = command.replaceAll("\\s", "");
		command += " " + unitsToGive;
		return(command);
	}
	
	public String getPlacement (int forPlayer) {
		String command;
		// put your code here
		command = GameData.COUNTRY_NAMES[(int)(Math.random() * GameData.NUM_COUNTRIES)];
		command = command.replaceAll("\\s", "");
		return(command);
	}
	
	public String getCardExchange () {
		//String command = "skip";
		return("skip");
	}

	public String getBattle () {
		String command;
		// put your code here
		command = "skip";
		return(command);
	}

	public String getDefence (int countryId) {
		String command;
		// put your code here
		command = "1";
		return(command);
	}

	public String getMoveIn (int attackCountryId) {
		String command;
		// put your code here
		command = "0";
		return(command);
	}

	public String getFortify () {
		String command;
		// put code here
		command = "skip";
		return(command);
	}

}