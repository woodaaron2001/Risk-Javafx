package application;
import static application.constants.*;

import java.io.IOException;
//import java.lang.ProcessHandle.Info;
import java.util.ArrayList;
import java.util.Arrays;

import CardsFolder.Cards;
import CardsFolder.WildCard;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
//
//Holds functions used to control the program flow
public class GameLogic {
	//CONSTANTS SO THAT WE CAN CONTROL EVENTS
	final static int ATTACKER_WINS = 0;
	final static int DEFENDER_WINS = 1;
	
	//count used in pathExists function to count iterations
	private static int count = 0;
	//helper just defines wheter its the first time weve ran dice rolls
	private static int helper = 0;
	
	//images used for dice to be displayed

	static ImageView p1Dice = new ImageView();
	static ImageView p2Dice = new ImageView();
	//text for dice images
	static Text Player1Text = new Text(30,50,"");
	static Text Player2Text = new Text(150,50,"");
	static Text winnerText = new Text(70,200,"");
	
	
	//Changes player name to last input
	protected static GameInfo NamePlayer(String LastInput, GameInfo Info) {	
		if (Info.command == 0) {			//If player1 entered name
			Info.Player1 = LastInput;
		} else if (Info.command == 1) {		//If player2 entered name
			Info.Player2 = LastInput;
		}
		Info.command++;	//Moves the program forward
		return Info;	//Returns updated data
	}
	
	//changes the table row given to the string given
	protected static void changeTable(int pos,String s,ObservableList list) {
		list.set(pos,new TableInfo(((TableInfo) list.get(pos)).getCase(),s));
	}
		
	//changes the state in game state table row 
	protected static void changeState(ObservableList list1,ObservableList list2,int command,int laststate,int neutral) {
		
		
		ArrayList<Integer> Player1commands = new ArrayList<Integer>(Arrays.asList(0,2,5,9,10,11,12,14,15,16,21,29,31,33,38,40,42));
		
		String neutColor = " Pink";

		switch (neutral) {
			case 4:
				neutColor= " Purple";
				break;
			case 5:
				neutColor = " Blue";
				break;
			case 6:
				neutColor = " Gold";
				break;
		}
		
		//if our last state was the neutral colour state we need to accomodate for this
		//6 is state for player 1 and 5 for player 2
		if(laststate == 6) {
			list1.set(2,new TableInfo(((TableInfo) list1.get(2)).getCase(),commandNames[command]+ neutColor));
			list2.set(2,new TableInfo(((TableInfo) list2.get(2)).getCase(),"Waiting"));
			return;
		}
		else if (laststate == 5){
			list1.set(2,new TableInfo(((TableInfo) list1.get(2)).getCase(),"Waiting"));
			list2.set(2,new TableInfo(((TableInfo) list2.get(2)).getCase(),commandNames[command]+ neutColor));
			return;
		}
		
		//all states in which we need both players states to update
		if(command == 4 || command == 8 || command == 7 || command == 25 || command == 26 || command == 27) {
			list1.set(2,new TableInfo(((TableInfo) list1.get(2)).getCase(),commandNames[command]));
			list2.set(2,new TableInfo(((TableInfo) list2.get(2)).getCase(),commandNames[command]));
			return;
		}
		
		//if the command given is in the player 1 commands we update the player1 table and leave the player2 waiting 
		//else we do the opposite if it is a player 2 state
		if(Player1commands.contains(command)){
			list1.set(2,new TableInfo(((TableInfo) list1.get(2)).getCase(),commandNames[command]));
			list2.set(2,new TableInfo(((TableInfo) list2.get(2)).getCase(),"Waiting"));
		}
		else {
			list1.set(2,new TableInfo(((TableInfo) list1.get(2)).getCase(),"Waiting"));
			list2.set(2,new TableInfo(((TableInfo) list2.get(2)).getCase(),commandNames[command]));
			
		}
		
	}
	

	
	protected static void drawCard(GameInfo info, int player) {
		if(player == 1){
			//If it is player 1 then we let player one draw the top card
			//And we then remove the top card from the deck
			Cards card = info.deck.getTop();
			Main.Output.appendText(card.toString());
			info.P1hand.add(card);
		}
		else{
			//If it is player 2 then we let player one draw the top card
			//And we then remove the top card from the deck
			Cards card = info.deck.getTop();
			Main.Output.appendText(card.toString());
			info.P2hand.add(card);
		}
	}
	
	public  static boolean pathExists(int fortifier, int fortified, GameInfo info, int ownership) {
		
		int adj[] = constants.ADJACENT[fortifier];	//adjacents of fortifier							
		boolean found = false;
		
		if(isAdjacent(fortifier, fortified)) { return true; }

		//recursive function to find path 
		for(int j=0;j<adj.length;j++) {	//Loops through adjacents
			
			if(adj[j] == fortified ) { found = true; } //If fortified found and owned
			
			else if(info.ownership[adj[j]] == ownership) {						//If not fortified but owned = possible path
				count++;
				if(count >40) { return false; }
				found = pathExists(adj[j], fortified, info, ownership);			//find if this path exists
			}
			if(found == true) {return found;}	//if path found
		}
		return found;		//no path found
	}
	

	public static boolean isAdjacent(int attackID,int defendID) {
		
		for (int n : constants.ADJACENT[attackID]) { // loops throuugh adjacents of attacker
          if (n == defendID) { return true; } // if the defending province is in adjacents return true
		}
        return false;  //else return false
	}
	
	//when a player wants to trade in cards at the start of their turn gameboard uses this function

	public static ArrayList<Cards[]> findMatch(GameInfo info, int player){
		/* If we want to make a set we need to have an array of 3 cards that fit the requirement
		* 3 infantries
		* 3 cavalries
		* 3 artilleries
		* 1 of each
		* or a wild card and any other card */

		ArrayList<Cards> hand = player == 1? info.P1hand : info.P2hand; //If player 1 then it'll be his/her hand
																			//Else player 2
		if(hand.size() < 3)
			return null;
		ArrayList<Cards[]> possibleExchanges = new ArrayList<>();
		//This will keep a list of all possible sets that can be used in an exchange

		Cards card1, card2, card3;

		/* We can use a similar method to the three sums algorithm.
		* Perhaps this can be done even better using binary search*/
		for(int i = 0; i < hand.size(); i++){
			for(int j = i + 1; j < hand.size(); j++){
				for (int k = j + 1; k < hand.size(); k++){
					card1 = hand.get(i);
					card2 = hand.get(j);
					card3 = hand.get(k);
					Cards[] set = {card1, card2, card3};
					cardSort(set); //We sort it so it can be compared properly
					if(checkMatch(set)){
						possibleExchanges.add(set);
					}
				}
			}
		}
		return possibleExchanges;
	}
		
	public static boolean checkMatch(Cards[] set) {
		/* This function checks to see if the set is exchangeable*/
		boolean exchangeable = true;
		for (Cards[] sets : GameInfo.acceptedMatches) {
			exchangeable = true;
			for (int i = 0; i < sets.length && exchangeable; i++) {
				//If we come across something that doesn't match then it does not fit the criteria
				//Of this specific set.
				if(set[i] instanceof WildCard)
					return true; //If the current card is a wildcard then there is a possible exchange
								//Regardless of the others
				if (set[i].getValue() != sets[i].getValue())
					exchangeable = false; //If the values don't match then it's not suitable
			}
		}
		return exchangeable;
	}
		
	public static void cardSort(Cards[] set){
		for(int i = 0; i < set.length; ++i){
			for (int j = i; j > 0; --j){
				if(set[j].compare(set[j - 1]) == -1){
					swap(set, j - 1, j);
				}
			}
		}
	}

	public static void swap(Cards[] arr, int i, int j){
		Cards temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	
	
	//graphic function for placing dot 
	protected static void PlaceDot(Main main,String lastInput,GameInfo info) { 
		
		//gets index of last input
		int index = GameLogic.getTerritoryIndex(lastInput,info);
		//places a circle in the coordinate of the index
		main.PlayerMarker[index]= new Circle(COUNTRY_COORD[index][0],COUNTRY_COORD[index][1],7);
		main.PlayerMarker[index].setMouseTransparent(true);
		//we make sure to set the circle transparent otherwise the mouse will get caught on it
		
		//set the colour equal to the ownership of the territory
		main.PlayerMarker[index].setFill(PColor[info.ownership[index]-1]);
		//outputs the units beside a territory
		main.PlayerText[index] = new Text(COUNTRY_COORD[index][0]+10,COUNTRY_COORD[index][1] -5,String.valueOf(info.numUnits[index]));
		
		
		main.PlayerText[index].setMouseTransparent(true);
		main.PathLayer.getChildren().addAll(main.PlayerText[index],main.PlayerMarker[index]);
	}
	
	//Places an Army on the map (Right now the initial 9 per player, can be reused later for placing armies in general
	protected static int PlaceArmy(String LastInput, GameInfo info, int player) {

		/*ERROR CHECKING FOR WRONG GAME STATE MAKE SURE TO CHANGE IF UPDATING FUNCTION FOR REUSE*/
		if(info.command != P1_PLACE_ONE && info.command != P2_PLACE_ONE && info.command != PLACE_NEUTRAL && info.command != P1_PLACE_THREE && info.command != P2_PLACE_THREE) {
			throw new IllegalStateException("Wrong Game State, method should not have been called");
		}
		
		if(info.command == P1_PLACE_ONE || info.command == P2_PLACE_ONE) { player = 0; }

		boolean found = validateTerritory(LastInput, info, player);	//Boolean for correct entry
		if(!found) { return 0; } //If country entry not appropriate goes back to the same step without changing anything
		int id = getTerritoryIndex(LastInput, info);

		//If player 1 placing
		if(info.command == P1_PLACE_ONE) {		//Updates info
			info.ownership[id] = 1;
			info.P1units--;
			info.numUnits[id]++;
			info.numTerritories[0]++;
		}
		//If player 2 placing
		if(info.command == P2_PLACE_ONE) {		//Updates info
			info.ownership[id] = 2;
			info.P2units--;
			info.numUnits[id]++;
			info.numTerritories[1]++;
		}
		//If reinforcing neutral
		if(info.command == PLACE_NEUTRAL) {
			info.numUnits[id]++;
			if(player == 3) {info.N1units--;}
			else if(player == 4) { info.N2units--; }
			else if(player == 5) { info.N3units--; }
			else if(player == 6) { info.N4units--; }
			return 1;
		}

		if(info.command == P1_PLACE_ONE || info.command == P2_PLACE_ONE) {
			//Event handling
			if(info.command == P2_PLACE_ONE && info.P2units > 27) {	info.command--; } //If there is still more to place
			else { info.command++; } //If all initial 18 placed
			return 1;
		}
		
		//alternate step if we're placing 3 
		if(info.command == P1_PLACE_THREE || info.command == P2_PLACE_THREE) {
			if(info.command == P1_PLACE_THREE){ 
				info.P1units -= 3; 
				info.command = P2_PLACE_THREE;
			} 
			else { 
				info.P2units -= 3; 
				info.command = P1_PLACE_THREE;
			}
			
			info.numUnits[id] += 3;
		}
		return 1;
	}

	
	
	//Simple function for rolling dice and returning value
	protected static int rollDice() {
		return (int) (Math.random() * 6) + 1;	//Between 1 and 6
	}

	protected  void displayDice(Main main,int dice1, int dice2, int DiceCountP1,int DiceCountP2,int winner) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		//generating an image with the file path being equal to the current dice

		p1Dice.setImage(new Image(getClass().getClassLoader().getResourceAsStream("dice"+dice1+".png")));
		p2Dice.setImage(new Image(getClass().getClassLoader().getResourceAsStream("dice"+dice2+".png")));
		
		//lose and win in use for attacking
		Music loseSound= new Music(7);
		Music winSound= new Music(6);
		//if its the fist time running func we set up all variables
		if(helper++ == 0) {
			main.DiceRoll.getChildren().addAll(winnerText,p1Dice,p2Dice,Player1Text,Player2Text);
			p1Dice.setFitHeight(100);
			p2Dice.setFitHeight(100);
			p1Dice.setFitWidth(100);
			p2Dice.setFitWidth(100);
			
			p1Dice.setLayoutX(10);
			p1Dice.setLayoutY(70);
			p2Dice.setLayoutX(130);
			p2Dice.setLayoutY(70);
			
			winnerText.setFill(Color.WHITE);
			winnerText.setTextAlignment(TextAlignment.CENTER);
			
			Player1Text.setFill(Color.WHITE);
			Player1Text.setTextAlignment(TextAlignment.CENTER);

			Player2Text.setFill(Color.WHITE);
			Player2Text.setTextAlignment(TextAlignment.CENTER);
		}
		//make sure to remove any dice pre existing
		main.DiceRoll.getChildren().removeAll(p1Dice,p2Dice);
		Music roll = new Music(5);
		//we play the dice roll sound and wait 1 second before displaying
		roll.Play();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//if not invasion phase
		if(DiceCountP1 == 0) {
			winnerText.setText("PLAYER "+winner+" HAS WON!!");
			 Player1Text.setText("Player 1:");
			 Player2Text.setText("Player 2:");
		}
		else {//invasion phase
			 Player1Text.setText("ATTACKER\n Best of " +DiceCountP1+ " rolls");
			 Player2Text.setText("DEFENDER\n Best of " +DiceCountP2+ " rolls");
			if(winner == 1) {
				winnerText.setText("ATTACKER HAS WON!!");
				winSound.Play();
			}
			else {
				winnerText.setText("DEFENDER HAS WON!!");
				loseSound.Play();
			}
		}
	
		
		main.DiceRoll.getChildren().addAll(p1Dice,p2Dice);

	}
	
	protected static int compareDiceRolls(GameInfo info,int DiceCountP1, int DiceCountP2,Main main) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		int p1Dice = rollDice();
		int p2Dice = rollDice();
		GameLogic g = new GameLogic();


		if(info.command == 4){ //The step before each player takes turns placing 3 units in their desired territories
			/* This is thr transition period where we decide
			* Who will place 3 pieces down first.*/
			
			if(p1Dice > p2Dice) {
				g.displayDice(main,p1Dice,p2Dice,0,0,1);
				return P1_PLACE_THREE;}
			else if(p1Dice < p2Dice) {
				g.displayDice(main,p1Dice,p2Dice,0,0,2);
				return P2_PLACE_THREE;}
			else
				return compareDiceRolls(info,0,0,main);
		}
		else if(info.command == GOES_FIRST){
			/* Then we see who will have their turn first */
			if(p1Dice > p2Dice) {
				g.displayDice(main,p1Dice,p2Dice,0,0,1);
				return P1_REINF;}
			else if(p1Dice < p2Dice) {
				g.displayDice(main,p1Dice,p2Dice,0,0,2);
				return P2_REINF;}
			else
				return compareDiceRolls(info,0,0,main);
		}
		else if(info.command == INVADE){
			/* If there is an invasion going on
			* Then if there is a tie the defender wins*/
			
			//rolling dice in accordance to amount of defenders or attackers and comparing the best of both
			
			int temp;
			for(int x=1;x<DiceCountP1;x++);{
				temp = rollDice();
				if(temp > p1Dice)
					p1Dice = temp; 
			}
			
			
			for(int x=1;x<DiceCountP2;x++);{
				temp = rollDice();	
				if(temp > p2Dice)
					p2Dice = temp; 
			}
			
			
			if(p1Dice > p2Dice) {
				g.displayDice(main,p1Dice,p2Dice,DiceCountP1,DiceCountP2,1);
				return ATTACKER_WINS;}
			else {
				g.displayDice(main,p1Dice,p2Dice,DiceCountP1,DiceCountP2,2);
				return DEFENDER_WINS;}
		}else
			return 0;
	}

	protected static Integer calculateReinforce(GameInfo info) {
		Integer reinforceAmount = 0;//using wrappers for ints so they can be printed easier and behave as objects
		
		if(info.command == 38) {
			//reinforce count = no of user territory/3
			reinforceAmount = info.numTerritories[0]/3;
			
			//checks to see if user holds continent/s and returns value for continent/s held
			reinforceAmount += hasContinent(1,info);
		}
		
		else if(info.command == 37) {//same as above for player 2
			reinforceAmount = info.numTerritories[1]/3;
	
			reinforceAmount += hasContinent(2,info);

		}
		if(reinforceAmount < 3) {
			return 3;
		}
		return reinforceAmount;
	}
	
	protected static Integer hasContinent(int player,GameInfo info) {
		int continent=0;
		int country=0;
		Integer value = 0;
		
		boolean hasAll = true;
	
		while(continent < 6 && country <42) { // loops through all terr

			if(continent!= constants.CONTINENT_IDS[country]) { 
				//continent will be updated if the continent of the current country has moved to the next continent
				if(hasAll == true) {
					//if while iterating There was not a discrepancy between user ownership and a continent
					value +=constants.CONTINENT_VALUES[continent];
				}
				else {
					//resetting to true
					hasAll = true;
				}

				continent++;//next continent
			}
			
			//if the user does now own territory on a continent
			if(info.ownership[country] != player) {
				hasAll = false;
			}
			country++;
		}
		if(country == 42&& hasAll) {
			value +=constants.CONTINENT_VALUES[continent];
		}
		
		return value;
	}
	
	//error checking for getting index
	public static int getTerritoryIndex(String input, GameInfo info){
		if(input.length()<4) {
			Main.Output.appendText(">Text input for countires must be atleast 4 characters\n\n ");
		}
		String country;
		for(int id = 0; id < constants.NUM_COUNTRIES; id++){
			country = constants.COUNTRY_NAMES[id].toLowerCase();
			if(country.startsWith((input.substring(0, 4).toLowerCase()))){
				return id;
			}
		}
		throw new GameLogicException("Not found");
	}

	public static boolean validateTerritory(String input, GameInfo info, int ownership){
		if(input.length()<4) {
			return false;
		}
		int id; //We'll use this to traverse the COUNTRY_NAMES array
		boolean found = false;
		String country;
		for(id = 0; id < constants.NUM_COUNTRIES; id++){
			//This will continue if found is still false and there are still countries for us to check
			// i.e id < constants.Number of Countries
			country =  constants.COUNTRY_NAMES[id].toLowerCase();
			if(country.startsWith((input.substring(0, 4).toLowerCase()))
					&& info.ownership[id] == ownership) {	//If name matches and country unoccupied
				found = true;
				break;
			}
		}
		return found;
	}
	
	public static void battle(int invadeID, int defendID, Integer attackingUnits, Integer defendingUnits, GameInfo info, Main main) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		int iterate = (defendingUnits == 2 && attackingUnits >= 2) ? 2 : 1;
		//System.out.println("Iterate = " + iterate);
		/* Now that we've figured out the iteration number
		* We must now let the units fight.
		* We cam achieve this with a simple loop*/

		for(int i = 0; i < iterate; i++){
			System.out.println("Iteration " + i);
			
			int outcome;

			 outcome = compareDiceRolls(info,attackingUnits,defendingUnits,main);

			if(attackingUnits == 1 && info.numUnits[invadeID] == 1) { return; }
			
			if(outcome == ATTACKER_WINS){
				/* If player Attacker wins---
				* We basically remove one from the number of units of the defenders's
				* territory.
				* Next reduce the number of units owned by the defender */
				Main.Output.appendText(">> Attacker won battle " + i + "\n");

				defendingUnits--;
				info.numUnits[defendID]--;
				main.PlayerText[info.attacker].setText(String.valueOf(info.numUnits[info.attacker]));
				main.PlayerText[info.defender].setText(String.valueOf(info.numUnits[info.defender]));
			}
			else if(outcome == DEFENDER_WINS){
				/* If player Defender wins---
				* We remove one of the attacking units
				* Then remove one of the units of the territory of the attacking units*/
				Main.Output.appendText(">> Defender won battle " + i + "\n");

				attackingUnits--;
				info.numUnits[invadeID]--;
				main.PlayerText[info.attacker].setText(String.valueOf(info.numUnits[info.attacker]));
				main.PlayerText[info.defender].setText(String.valueOf(info.numUnits[info.defender]));
			}
		}
		Main.Output.appendText("Attacker Units Left: " + info.numUnits[invadeID] + "\n");
		Main.Output.appendText("Defending Territory Units Left: " + info.numUnits[defendID] + "\n");
	}



	/* When we are making an attacking function we need to do a few things
	* We need o see if they are able to be attacked and how do we decide that
	* */
}

class GameLogicException extends RuntimeException {
	GameLogicException(String s){
		super(s);
	}
}