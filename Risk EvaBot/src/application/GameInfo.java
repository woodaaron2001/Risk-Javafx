package application;
import CardsFolder.Card;
import CardsFolder.CardType;
import CardsFolder.Cards;

import static application.constants.COUNTRY_NAMES;

import java.awt.Color;
import java.util.ArrayList;
//
//Class for storing info on the current state of the game
public class GameInfo {

	static ArrayList<Cards[]> acceptedMatches = new ArrayList<>();
	protected ArrayList<Cards> P1hand;
	protected ArrayList<Cards> P2hand;
	protected int P1exchangeCount;
	protected int P2exchangeCount;

	int setPicked;
	protected Deck deck;

	//invasion fortificaion and reinforce variables
	int attacker, defender, fortifyingNumber;
	Integer attackingUnitNumber, defendingUnitNumber;
	int fortifier, fortified;
	Integer reinforceCount;
	
	boolean invasionSuccessful;
	
	int j=3;
	int laststate = 0;
	
	//Player status
	protected String Player1;
	protected String Player2;
	
	
	GameInfo(){
		//defining p1 and p2 card list
		P1hand = new ArrayList<>();
		P2hand = new ArrayList<>();
		//creating a new deck to draw from 
		deck = new Deck();
		
		invasionSuccessful = false;
		P1exchangeCount = 4;
		P2exchangeCount = 4;

		//defining the sets that are valid for bonus units
		Cards[] set1 = {new Card(CardType.INFANTRY, COUNTRY_NAMES[0]),
				new Card(CardType.INFANTRY, COUNTRY_NAMES[0]),
				new Card(CardType.INFANTRY, COUNTRY_NAMES[0])};
		Cards[] set2 = {new Card(CardType.CAVALRY, COUNTRY_NAMES[0]),
				new Card(CardType.CAVALRY, COUNTRY_NAMES[0]),
				new Card(CardType.CAVALRY, COUNTRY_NAMES[0])};
		Cards[] set3 = {new Card(CardType.ARTILLERY, COUNTRY_NAMES[0]),
				new Card(CardType.ARTILLERY, COUNTRY_NAMES[0]),
				new Card(CardType.ARTILLERY, COUNTRY_NAMES[0])};
		Cards[] set4 = {new Card(CardType.INFANTRY, COUNTRY_NAMES[0]),
				new Card(CardType.CAVALRY, COUNTRY_NAMES[0]),
				new Card(CardType.ARTILLERY, COUNTRY_NAMES[0])};

		acceptedMatches.add(set1);
		acceptedMatches.add(set2);
		acceptedMatches.add(set3);
		acceptedMatches.add(set4);
	}
	
	//constants
	protected int P1units = constants.INIT_UNITS_PLAYER;
	protected int P2units = constants.INIT_UNITS_PLAYER;
	protected int N1units = constants.INIT_UNITS_NEUTRAL - 6;
	protected int N2units = constants.INIT_UNITS_NEUTRAL - 6;
	protected int N3units = constants.INIT_UNITS_NEUTRAL - 6;
	protected int N4units = constants.INIT_UNITS_NEUTRAL - 6;

	protected boolean neutralPlacing = false; //This will help us determine whether or not neutral pieces can be placed
	
	//Country status
	protected int[] ownership = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	protected int[] numUnits = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	//number of territories owned by each player
	protected int[] numTerritories = {0,0,0,0,0,0};
								//What step the game is on (Event handling)
	protected int command = 0; // Command is used for program control ie. what process occurs when the user presses enter

	public int getOwnership(String territory) {
		/* This function gets the ownership of the ownership of a territory*/
		int ownershipNum;
		String country;
		for(int id = 0; id < constants.NUM_COUNTRIES; id++){
			country = constants.COUNTRY_NAMES[id].toLowerCase();
			if(country.startsWith((territory.substring(0, 4).toLowerCase()))){
				return this.ownership[id];
			}
		}
		throw new GameLogicException("Not a real territory");
	}

	public boolean isOwned(String lastInput, int ownershipNum) {
		/* This function returns true if the input is a territory owned by a given
		* player. The player os represented by their ownership number. */
		for(int i = 0; i < this.ownership.length; ++i){
			if(i == ownershipNum && constants.COUNTRY_NAMES[i].toLowerCase().startsWith(lastInput.substring(0, 4).toLowerCase())
					&& this.ownership[i] == ownershipNum) { return true; }
		}
  	return false;
  	}
	
	//card functions
	public int getP1exchangeCount() {
		return P1exchangeCount;
	}

	public int getP2exchangeCount() {
		return P2exchangeCount;
	}

	public void setP1exchangeCount(int p1exchangeCount) {
		P1exchangeCount = p1exchangeCount;
	}

	public void setP2exchangeCount(int p2exchangeCount) {
		P2exchangeCount = p2exchangeCount;
	}

	public void setSetPicked(int setPicked) {
		this.setPicked = setPicked;
	}

  public void setCommand(int command) {
		this.command = command;
	}

	public void setNeutralPlacing(boolean neutralPlacing) {
		this.neutralPlacing = neutralPlacing;
	}
}