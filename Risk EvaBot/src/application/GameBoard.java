package application;
import static application.constants.*;
import CardsFolder.Cards;

import java.util.ArrayList;
import java.util.Objects;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
//
public class GameBoard {

	public GameBoard(GameInfo info) {

	}
	

	public void command(Main main,GameInfo info,String lastInput) throws UnsupportedAudioFileException, IOException, LineUnavailableException 
    {
		
		if (info.command == P1_NAME) {        //Gets name of player 1
			
			info = GameLogic.NamePlayer(lastInput, info);
			GameLogic.changeTable(1,info.Player1,main.list1);
			Main.Output.appendText("Please enter Name of Player2 - (Player 2 will be represented by a black dot on a territory)\n");
			return;
		}
		if (info.command == P2_NAME) {        //Gets name of player 2
			info = GameLogic.NamePlayer(lastInput, info);
			GameLogic.changeTable(1,info.Player2,main.list2);
			Main.Output.appendText("Each player will now choose their first 9 countries and place an army on each\nCountry names are case Sensitive, all country names and spellings are displayed on the board\n");
			Main.Output.appendText("Players and armies will be displayed once all 18 territories have been chosen\nNeutral countries have no black or white marker\n\n");
			Main.Output.appendText("A player can type in the first 4 (or more) characters of a territory as a substitute.\n\n");
			Main.Output.appendText(info.Player1 + ", please choose a country by entering its name\n");
			return;
		}

		if (info.command == P1_PLACE_ONE ) {        //Player 1 places starting army
			GameLogic.PlaceArmy(lastInput, info, 1);
			if (info.command == P2_PLACE_ONE) {
				//placeDot is a graphical function which adds the unit to the map
				GameLogic.PlaceDot(main, lastInput,info);
				//when we see a changeTable that means we update the table view to represent game changes
				GameLogic.changeTable(4,String.valueOf(info.P1units-27),main.list1);
				GameLogic.changeTable(3,String.valueOf(info.numTerritories[0]),main.list1);
				
				Main.Output.appendText(info.Player2 + ", choose a country by entering its name\n");
			} else {        //If input was valid p1_place_one will be updated however if it was not there was an exception in placeArmy
				Main.Output.appendText("Try again\n");
			}
			return;
		}
		
		if (info.command == P2_PLACE_ONE) {        //Player 2 places starting army
			GameLogic.PlaceArmy(lastInput, info, 2);
			if (info.command == P1_PLACE_ONE) {
				GameLogic.PlaceDot(main, lastInput,info);
				GameLogic.changeTable(4,String.valueOf(info.P2units-27),main.list2);
				GameLogic.changeTable(3,String.valueOf(info.numTerritories[1]),main.list2);
				
				Main.Output.appendText(info.Player1 + ", choose a country by entering its name\n");
			} else if (info.command == P2_PLACE_ONE) {
				Main.Output.appendText("Try again\n");
			} else {
				GameLogic.PlaceDot(main, lastInput,info);
				GameLogic.changeTable(4,String.valueOf(info.P2units-27),main.list2);
				GameLogic.changeTable(3,String.valueOf(info.numTerritories[0]),main.list2);
				//once finished run the map function to Display units generated
				//info.setNeutralPlacing(true);
				
				//loop just used for generating where neutrals are placed
				int y = 2;
				
				for(int x = 0; x<42; x++) {
					if (info.numTerritories[y] < 7 && info.ownership[x] == 0) {
						
						info.ownership[x] = y + 1;
						info.numUnits[x]++;
						info.numTerritories[y]++;
						//defining playerMarkers and adding them to the path layer i.e the country layer
						main.PlayerMarker[x]= new Circle(COUNTRY_COORD[x][0],COUNTRY_COORD[x][1],7);
						main.PlayerMarker[x].setMouseTransparent(true);
						main.PlayerMarker[x].setFill(PColor[y]);
						main.PlayerText[x] = new Text(COUNTRY_COORD[x][0]+10,COUNTRY_COORD[x][1] -5,String.valueOf(info.numUnits[x]));
						main.PlayerText[x].setMouseTransparent(true);
						main.PathLayer.getChildren().addAll(main.PlayerText[x],main.PlayerMarker[x]);
						
					}
					if(info.numTerritories[y] == 6) {
					y++;
					}
					
					if(y == 6) {
						break;
					}
				}
				
				Main.Output.appendText("End of Sprint 1\nPlayers will now take it in turns to place 3 armies in a country of their choice until all 36 units are placed\n"); 
				Main.Output.appendText("A Dice will be rolled to determine who goes first\n");
				Main.Output.appendText("Press enter to see who goes first\n\n");
				info.setCommand(4);
			}
			return;
		}
		if (info.command == 4) { // moving to placing armies 3 at a time for efficiency
			//compareDicerolls returns a command based on Dice thrown, if user one wins the comand is set to P1_PLACE_THREE etc
			info.command = GameLogic.compareDiceRolls(info,0,0,main);
			GameLogic.changeTable(4,String.valueOf(info.P2units),main.list2);
			GameLogic.changeTable(4,String.valueOf(info.P1units),main.list1);
			if (info.command == P1_PLACE_THREE) {
				Main.Output.appendText(">> " + info.Player1 + " places their armies first.\n");
				Main.Output.appendText(">> Choose one of your territories to reinforce\n");
			} else if (info.command == P2_PLACE_THREE) {
				Main.Output.appendText(">> " + info.Player2 + " places their armies first.\n");
				Main.Output.appendText(">> Choose one of your territories to reinforce\n");
			}
			return;
		}
		
		if (info.command == P1_PLACE_THREE) {
			/*We run a function where player one places 3 armies
			 * This will pass the last input that the user enters*/
			GameLogic.PlaceArmy(lastInput, info, 1);
			int index = GameLogic.getTerritoryIndex(lastInput,info);
			main.PlayerText[index].setText(String.valueOf(info.numUnits[index]));

			if (info.command == P2_PLACE_THREE) {
				GameLogic.changeTable(4,String.valueOf(info.P1units),main.list1);
				info.laststate = P2_PLACE_THREE; // control flow for neutral
				info.command = PLACE_NEUTRAL;//place neutrals if we just placed player units
				Main.Output.appendText("Place an army for neutral 1, colour: Pink\n");
			} else {
				Main.Output.appendText("Error: Try again\n");//But if the command never updated
			}
			return;
		}
		
		if (info.command == P2_PLACE_THREE) {
			/*We run a function where player two places 3 armies
			 * This will pass the last input that the user enters*/

			GameLogic.PlaceArmy(lastInput, info, 2);
			int index = GameLogic.getTerritoryIndex(lastInput,info);
			main.PlayerText[index].setText(String.valueOf(info.numUnits[index]));
			if (info.command == P1_PLACE_THREE) {
				GameLogic.changeTable(4,String.valueOf(info.P2units),main.list2);
				info.laststate = P1_PLACE_THREE;
				info.command = PLACE_NEUTRAL;
				Main.Output.appendText("Place an army for neutral 1, colour: Pink\n");
			} else {
				Main.Output.appendText("Error: Try again\n");//But if the command never updated
			}
			return;
		}

		if (info.command == PLACE_NEUTRAL) {
		
			//user gets to choose where neutrals are placed 
			if (GameLogic.PlaceArmy(lastInput, info, info.j) == 0) { //error checking
				Main.Output.appendText("Try Again\n");
			} else {
				int index = GameLogic.getTerritoryIndex(lastInput,info);
				main.PlayerText[index].setText(String.valueOf(info.numUnits[index]));
				
				info.j++;
				//self explanatory- j is updated until 7 and placed by current active user
				if (info.j == 4) {
					Main.Output.appendText("Place an army for neutral 2, colour: Purple\n");
				}
				if (info.j == 5) {
					Main.Output.appendText("Place an army for neutral 3, colour: Blue\n");
				}
				if (info.j == 6) {
					Main.Output.appendText("Place an army for neutral 4, colour: Gold\n");
				}

				if (info.j == 7) {
					info.command = info.laststate;
					if(info.P1units <= 0 && info.P2units <= 0) { 
						/*When there's all the armies are place we must move to the next stage
						 * Therefore the sprint is now over.*/
						GameLogic.changeTable(4,String.valueOf(info.P1units),main.list1);
						GameLogic.changeTable(4,String.valueOf(info.P2units),main.list2);
						Main.Output.appendText(info.Player1 + " and " + info.Player2 + " will roll to see who goes first.\n");
						Main.Output.appendText("Press Enter for result.\n");
						info.command = GOES_FIRST; 
					}
					//continuing
					else if (info.laststate == P1_PLACE_THREE) {
						Main.Output.appendText(info.Player1 + ", choose a country to reinforce\n");
					} else {
						Main.Output.appendText(info.Player2 + ", choose a country to reinforce\n");
					}
					info.laststate = 0;
					info.j = 3;
				}
			}
			return;
		}		

		if (info.command == GOES_FIRST) {
			info.command = GameLogic.compareDiceRolls(info,0,0,main);
			//same as before compareDicerolls returns a game state depending on who won
			if (info.command == P1_REINF) { // game state set to step 1 - reinforce
				Main.Output.appendText(">> " + info.Player1 + "'s turn is first.\nPress ENTER to proceed:\n");
			} else if (info.command == P2_REINF) {
				Main.Output.appendText(">> " + info.Player2 + "'s turn is first.\nPress ENTER to proceed:\n");
			}
			return;
		}
		
		if (info.command == P1_REINF || info.command == P2_REINF) {
		
			//if we reached this state and the last state was an exchange of card then we must place the reinforcements gained
			if(info.command == P1_REINF) {
				if(info.laststate == P1_EXCHANGE_CARD) {
					info.reinforceCount = info.P1units;
					info.P1units= 0;
					Main.Output.appendText(">> " + info.Player1 + " Place units equal to units gained from cards!\n");	
					GameLogic.changeTable(4,String.valueOf(info.reinforceCount),main.list1);
				}
				//otherwise we gain reinforcements from our territories
				else {
				Main.Output.appendText(">> " + info.Player1 + "'s turn COMMENCE!\n");			
				info.reinforceCount = GameLogic.calculateReinforce(info); // first user reinforcements are calculated
				GameLogic.changeTable(4,String.valueOf(info.reinforceCount),main.list1);
				Main.Output.appendText(">> " + info.Player1 + " received " + info.reinforceCount +" units from their territories\n");
				}
			}
			
			
			//same as above
			if(info.command == P2_REINF) {
				if(info.laststate == P2_EXCHANGE_CARD) {
					info.reinforceCount = info.P2units;
					info.P2units= 0;
					Main.Output.appendText(">> " + info.Player1 + "Place units equal to units gained from cards!\n");	
					GameLogic.changeTable(4,String.valueOf(info.reinforceCount),main.list1);
				}
				else {
				
				Main.Output.appendText(">> " + info.Player2 + "'s turn COMMENCE!\n");
				info.reinforceCount = GameLogic.calculateReinforce(info);
				GameLogic.changeTable(4,String.valueOf(info.reinforceCount),main.list2);
				Main.Output.appendText(">>" + info.Player2 + " received " + info.reinforceCount +" units from their territories\n");
				}
				
				}
			//error checking
			if(info.reinforceCount == 0) {
				Main.Output.appendText(">> No units to reinforce with moving to next stage");
				if(info.command == P1_REINF) {info.command = P1_TURN;}
				if(info.command == P2_REINF) {info.command = P2_TURN;}
				return;
			}
			Main.Output.appendText(">> Please choose a country to fortify\n");
			
			if(info.command == P1_REINF) {info.command = P1_REINF_ONE;}
			if(info.command == P2_REINF) {info.command = P2_REINF_ONE;}
			return;
		}
		
		if(info.command == P1_REINF_ONE || info.command == P2_REINF_ONE) {
			//choosing what country to reinforce
			//making sure input is valid with validateTerritory
			int p = 0;
			if(info.command == P1_REINF_ONE) {p=1;}
			if(info.command == P2_REINF_ONE) {p=2;}
			if(GameLogic.validateTerritory(lastInput, info, p)) {
				try {
					//try catch for error checking
					
					info.fortified = GameLogic.getTerritoryIndex(lastInput, info);
					//setting fortified id = index of input
					
					if(info.command == P1_REINF_ONE) {info.command = P1_REINF_UNIT;}
					if(info.command == P2_REINF_ONE) {info.command = P2_REINF_UNIT;}
					Main.Output.appendText("How many units will you reinforce with?");
				} catch (GameLogicException ex) {
					Main.Output.appendText(">> Error: Invalid Territory name.\n");
				}
			} else {
				Main.Output.appendText("Try Again.\n\n");
			}
			return;
		}
		
		if (info.command == P1_REINF_UNIT || info.command == P2_REINF_UNIT) {
			try {
				info.fortifyingNumber = Integer.parseInt(lastInput); 
				/*converting last input to an int
				  if user enters no numeric value NumberFormatException is caught*/
				
				if (info.fortifyingNumber >= 1 && info.fortifyingNumber <= info.reinforceCount) {
				//user can reinforce 1 unit and less than the total amount
					
					info.numUnits[info.fortified] += info.fortifyingNumber; //updating terr info
					info.reinforceCount -= info.fortifyingNumber;			  //decreasing reinforce
					main.PlayerText[info.fortified].setText(String.valueOf(info.numUnits[info.fortified]));
					
					if(info.command == P1_REINF_UNIT) {
						GameLogic.changeTable(4,String.valueOf(info.reinforceCount),main.list1);
					}
					else {
						GameLogic.changeTable(4,String.valueOf(info.reinforceCount),main.list2);
					}
					
					if(info.reinforceCount == 0) { // exit to next stage

						
						Main.Output.appendText("All reinforcements placed!\n");
						//checking for exchange card phases first
						if((info.command == P1_REINF_UNIT && info.laststate == P1_EXCHANGE_CARD)) {
							info.laststate = 0;
							Main.Output.appendText("Switch turns----Player two's turn will begin.\nPress Enter to proceed");
							info.command = P2_REINF;
							return;
						}
						if((info.command == P2_REINF_UNIT && info.laststate == P2_EXCHANGE_CARD)) {
							info.laststate = 0;
							Main.Output.appendText("Switch turns----Player ones's turn will begin.\nPress Enter to proceed");
							info.command = P1_REINF;
							return;
						}
						//otherwise we move to battle
						if(info.command == P1_REINF_UNIT) {info.command = P1_STAGE_MANAGER;}
						if(info.command == P2_REINF_UNIT) {info.command = P2_STAGE_MANAGER;}
						
						Main.Output.appendText("You can either enter 'battle' or click the black battle button to move to the attack stage\n Or You can enter 'skip' or click the castle button to move to the fortification stage.\n");
						//we add the buttons at this time so the user can only click them when about to enter these phases
						Main.PathLayer.getChildren().addAll(main.attack,main.fortify);
	
					}
					
					else {
						//if we have not placed all reinforcements we loop
						Main.Output.appendText(">> Reinforcements placed "+ info.reinforceCount + " left to place\n" );
						Main.Output.appendText(">> Please choose a country to fortify\n");
						if(info.command == P1_REINF_UNIT) {info.command = P1_REINF_ONE;}
						if(info.command == P2_REINF_UNIT) {info.command = P2_REINF_ONE;}
					}
					
					}
				else{
					Main.Output.appendText("Invalid number\nTry again\n");
					return;
				}
			} catch (NumberFormatException ex) {
				Main.Output.appendText(">> Error: Not a number.\n");
			}
			return;
		}
		
		//card set is dedicated to checking if we have any valid sets in the players cards
		
		if(info.command == P1_CARD_SET || info.command == P2_CARD_SET){
			int p=0;
			if(info.command == P1_CARD_SET){p = 1;}
			if(info.command == P2_CARD_SET){p = 2;}
			ArrayList<Cards[]> possibleMoves = GameLogic.findMatch(info, p);
			System.out.println(possibleMoves);
			if(possibleMoves != null){
				if(!possibleMoves.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					int i = 1;
					for (Cards[] set : possibleMoves) {
						sb.append("Set ").append(i).append(": ");
						for (Cards card : set)
							sb.append(card).append("\n\n");
						i++;
					}
					Main.Output.appendText("Enter the number of the set that you want to exchange.\n" + sb.toString());
					//System.out.println("Enter the number of the set that you want to exchange.\n" + sb.toString());
					if(info.command == P1_CARD_SET) {info.setCommand(P1_EXCHANGE_CARD);}
					if(info.command == P2_CARD_SET) {info.setCommand(P2_EXCHANGE_CARD);}
					return;
				}
			}
			Main.Output.appendText(">>No cards can be exchanged!\n");
			
			//System.out.println("No cards can be exchanged!\nPress Enter to Continue\n");
			if(info.command == P1_CARD_SET) {info.command = P2_REINF;Main.Output.appendText("Switch turns----Player two's turn will begin\nPress Enter to proceed");}
			if(info.command == P2_CARD_SET) {info.command = P1_REINF;Main.Output.appendText("Switch turns----Player ones's turn will begin\nPress Enter to proceed");}
			return;
		}
		
		if(info.command == P1_EXCHANGE_CARD){
			try{
				info.setSetPicked(Integer.parseInt(lastInput));
				Cards[] set = Objects.requireNonNull(GameLogic.findMatch(info, 1)).get(info.setPicked - 1);
				for(Cards card: set){
					info.P1hand.removeIf(card::equals);
				}
				switch (info.getP1exchangeCount()){
					case 4:
						info.P1units += 4;
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(6);
						break;
					case 6:
						info.P1units += 6;
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(8);
						break;
					case 8:
						info.P1units += 8;
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(10);
						break;
					case 10:
						info.P1units += 10;
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(12);
						break;
					case 12:
						info.P1units += 12;
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(15);
						break;
					case 15:
						info.P1units += 15;
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(info.getP1exchangeCount() + 5);
						break;
					default:
						Main.Output.appendText(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
					//	System.out.println(info.Player1 + " gained " + info.getP1exchangeCount() + " armies.\n");
						info.setP1exchangeCount(info.getP1exchangeCount() + 5);
						break;
				}
				Main.Output.appendText("Press Enter to Continue\n");
				info.laststate = P1_EXCHANGE_CARD;
				info.command = P1_REINF;
				return;
			}catch (NumberFormatException ex){
				Main.Output.appendText("Not a number. Pick another set\n");
				return;
			}
		}
		
		if (info.command == P2_EXCHANGE_CARD){
			try{
				info.setSetPicked(Integer.parseInt(lastInput));
				Cards[] set = Objects.requireNonNull(GameLogic.findMatch(info, 2)).get(info.setPicked - 1);
				for(Cards card: set){
					info.P2hand.removeIf(card::equals);
				}
				switch (info.getP2exchangeCount()){
					case 4: //If this is the first exchange
						info.P2units += 4;
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(6);
						break;
					case 6: //If this is te second exchange
						info.P2units += 6;
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(8);
						break;
					case 8: //If this is the third exchange
						info.P2units += 8;
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(10);
						break;
					case 10: //If this is the fourth exchange
						info.P2units += 10;
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(12);
						break;
					case 12: //If this is the fifth exchange
						info.P2units += 12;
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(15);
						break;
					case 15: //If this is the sixth exchange
						info.P2units += 15;
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(info.getP2exchangeCount() + 5);
						break;
					default: //Any exchange after the sixth
						Main.Output.appendText(info.Player2 + " gained " + info.getP2exchangeCount() + " armies.\n");
						info.setP2exchangeCount(info.getP2exchangeCount() + 5);
						break;
				}
				Main.Output.appendText("Press Enter to Continue\n");
				info.laststate = P2_EXCHANGE_CARD;
				info.command = P2_REINF;
				return;
			}catch (NumberFormatException ex){
				Main.Output.appendText("Not a number. Pick another set\n");
				return;
			}
		}
		
		//allowing user to enter phase again after attacking
		if (info.command == P1_TURN || info.command == P2_TURN) {
			/* What must we do in order to allow this to work*/
			if(info.command == P1_TURN) {
				Main.Output.appendText(">> " + info.Player1 + "'s turn continues!\n");
				ArrayList<Cards[]> possibleMoves = GameLogic.findMatch(info, 1);
				main.PathLayer.getChildren().addAll(main.attack,main.fortify);
				info.command = P1_STAGE_MANAGER;
			}
			if(info.command == P2_TURN) {
				Main.Output.appendText(">> " + info.Player2 + "'s turn continues!\n");
				ArrayList<Cards[]> possibleMoves = GameLogic.findMatch(info, 2);
				main.PathLayer.getChildren().addAll(main.attack,main.fortify);
				info.command = P2_STAGE_MANAGER;
			}
			Main.Output.appendText("You can either enter 'battle' or click the black battle button to move to the attack stage\n Or You can enter 'skip' or click the castle button to move to the fortification stage.\n");
			return;
		}
		
		
		//again phase choosing
		if (info.command == P1_STAGE_MANAGER || info.command == P2_STAGE_MANAGER) {
			/*Handles whether we want to skip the battle stage or not*/
			if (lastInput.toLowerCase().equals("skip")) {
				Main.Output.appendText("Skip to the fortification stage.\n");
				Main.Output.appendText(">> Fortification stage\nChoose which territory you will fortify:\n");
				main.PathLayer.getChildren().removeAll(main.attack,main.fortify);
				if(info.command == P1_STAGE_MANAGER) {info.command = P1_FORT_ONE;}
				if(info.command == P2_STAGE_MANAGER) {info.command = P2_FORT_ONE;}
			} else if (lastInput.toLowerCase().equals("battle")) {
				Main.Output.appendText("Proceed with the battle phase.\n");
				Main.Output.appendText(">> Which territory will you attack with.\n");
				main.PathLayer.getChildren().removeAll(main.attack,main.fortify);
				if(info.command == P1_STAGE_MANAGER) {info.command = P1_NAME_ATTACKING;}
				if(info.command == P2_STAGE_MANAGER) {info.command = P2_NAME_ATTACKING;}
			} else {
				Main.Output.appendText("Invalid Input: Try again!\n");
			}
			return;
		}
		
		
		//user chooses which territory they want to attack from 
		if (info.command == P1_NAME_ATTACKING || info.command == P2_NAME_ATTACKING) {
			int p = 0;
			if(info.command == P1_NAME_ATTACKING) {p=1;}
			if(info.command == P2_NAME_ATTACKING) {p=2;}
			if (GameLogic.validateTerritory(lastInput, info, p)) {
				/* With a try and catch we can see whether the name given is existing
				 * If not we throw a game exception and catch it. */
				try {
					info.attacker = GameLogic.getTerritoryIndex(lastInput, info);
					if(info.numUnits[info.attacker] == 1) {
						Main.Output.appendText("Attacking province must have more than 1 unit Try again.\n");
						return;
					}
					Main.Output.appendText(">> Which territory will you attack:\n");
					info.command++; //On to the next stage
				} catch (GameLogicException ex) {
					Main.Output.appendText(">> Error: Invalid Territory name.\n");
				}
			} else {
				Main.Output.appendText("Try Again.\n\n");
			}
			return;
		}
		
		//user choosers which territory they want to attack
		if (info.command == P1_NAME_DEFENDING || info.command == P2_NAME_DEFENDING) {
			//error checks
			if (info.isOwned(lastInput, 1) && info.command == P1_NAME_DEFENDING) {
				Main.Output.appendText("This territory belongs to you already!\nPick another territory.\n");
				return;
			}
			else if (info.isOwned(lastInput, 2) && info.command == P2_NAME_DEFENDING) {
				Main.Output.appendText("This territory belongs to you already!\nPick another territory.\n");
				return;
			}
				
			/* If the territory isn't owned by the info.attacker P1 then we can continue
			* This is so we can make Player 1 attack either neutral territories or Player 2*/
			else {
				//Ownership of the defending
				int ownership;
				try {
					ownership = info.getOwnership(lastInput);
				} catch (GameLogicException ex) {
					Main.Output.appendText(">> Error: Invalid Territory name.\nTry again!\n");
					return;
				}
					
				//error checking
				if (GameLogic.validateTerritory(lastInput, info, ownership)) {
					try {
						info.defender = GameLogic.getTerritoryIndex(lastInput, info);
						if(!GameLogic.isAdjacent(info.attacker,info.defender)) {
							//error for attacking non adjacents
							Main.Output.appendText(">> Defending province must be adjacent to Attacking province\n");
							return;
						}
								
						Main.Output.appendText(">> How many units will you use to attack:\n");
						info.command++;
						
					} catch (GameLogicException ex) {
						Main.Output.appendText(">> Error: Invalid Territory name.\n");
					}
				} else {
					Main.Output.appendText("Try Again.\n\n");
				}
			}
			
			return;
		}
		
		//inputting number of units you will attack with
		if (info.command == P1_NUMBER_UNITS || info.command == P2_NUMBER_UNITS) {
			try {
				/* If it can be parsed to an integer then that we proceed
				 * If not then we will say that an incorrect entry has been made*/
				info.attackingUnitNumber = Integer.parseInt(lastInput);
				if (info.attackingUnitNumber >= info.numUnits[info.attacker] || info.attackingUnitNumber < 1 || info.attackingUnitNumber > 3) {
					//If the units entered are greater than amount in territory
					//If the units entered are greater than 3
					//If the units entered are less than 1
					//If the number 
					Main.Output.appendText(">> Error: Invalid amount	.\n");
					Main.Output.appendText("Try Again.\n\n");
				} else {
					if(info.command == P1_NUMBER_UNITS) {Main.Output.appendText(">> " + info.Player2 + ", how many units are you defending with:\nYou may defend with up to 2 units.\n");}
					if(info.command == P2_NUMBER_UNITS) {Main.Output.appendText(">> " + info.Player1 + ", how many units are you defending with:\nYou may defend with up to 2 units.\n");}
					info.command++;
				}
			} catch (NumberFormatException ex) {
				Main.Output.appendText(">> Error: Not a number.\n");
			}
			return;
		}
		
		//the opposite player to the attacker decides how many units the defender will defend with
		if (info.command == P1_DEFENDING_UNITS || info.command == P2_DEFENDING_UNITS) {
				try {
					/* If it can be parsed to an integer then that we proceed
					 * If not then we will say that an incorrect entry has been made*/
					info.defendingUnitNumber = Integer.parseInt(lastInput);
					if (info.defendingUnitNumber > info.numUnits[info.defender] || info.defendingUnitNumber < 1 || info.defendingUnitNumber > 2) {
						//If the units entered are greater than or equal to or less than 0
						Main.Output.appendText(">> Error: Invalid amount	.\n");
						Main.Output.appendText("Try Again.\n\n");
					} else {
						info.command = INVADE;
						Main.Output.appendText("INVASION TIME!\nPress ENTER to Continue:\n");
						//mapComponent.invasionMarker(2, info, info.attacker, info.defender);
					}
				} catch (NumberFormatException ex) {
					Main.Output.appendText(">> Error: Not a number.\n");
				}
				return;
			}
		
		
		if (info.command == P1_FORT_ONE || info.command == P2_FORT_ONE) {
			/* Fortification works by choosing two countries to transfer units to*/
			int p = 0;
			if(info.command == P1_FORT_ONE) {p=1;}
			if(info.command == P2_FORT_ONE) {p=2;}
			
			//if the user wants to skip the fortification stage
			if(lastInput.toLowerCase().equals("skip")){
				//we need to make sure that the user also hasnt conquered any territories in their entire phase
				//if they have we need to give them a card - 1 card even if user has taken 4 territories
				if(info.invasionSuccessful){
					GameLogic.drawCard(info, p);
					Main.Output.appendText("Since you conquered a territory you can draw a card!\n");
					info.invasionSuccessful = false;
				}
				Main.Output.appendText(">>Fortification step skipped\n");
				Main.Output.appendText(">>Press enter to continue\n\n");
				if(info.command == P1_FORT_ONE) {info.command = P1_CARD_SET;}
				if(info.command == P2_FORT_ONE) {info.command = P2_CARD_SET;}
				return;
			}
			
			//otherwise error check and move to next phase
			if (GameLogic.validateTerritory(lastInput, info, p)) {
				try {
					info.fortified = GameLogic.getTerritoryIndex(lastInput, info);
					if(info.command == P1_FORT_ONE) {
						info.command = P1_FORT_TWO;
						Main.Output.appendText(info.Player1 + ", choose which territory that will fortify "
								+ constants.COUNTRY_NAMES[info.fortified] + ":(Note you can type skip here to move on to cards)\n");
					}
					if(info.command == P2_FORT_ONE) {
						info.command = P2_FORT_TWO;
						Main.Output.appendText(info.Player2 + ", choose which territory that will fortify  "
								+ constants.COUNTRY_NAMES[info.fortified] + ":(Note you can type skip here to move on to cards)\n");
					}
				} catch (GameLogicException ex) {
					Main.Output.appendText(">> Error: Invalid Territory name.\n");
				}
			} else {
				Main.Output.appendText("Try Again.\n\n");
			}
			return;
		}
		
		//choose the country that will fortify earlier country
		if (info.command == P1_FORT_TWO || info.command == P2_FORT_TWO) {
			int p = 0;
			if(info.command == P1_FORT_TWO) {p=1;}
			if(info.command == P2_FORT_TWO) {p=2;}
			
			if(lastInput.toLowerCase().equals("skip")){
				if(info.invasionSuccessful){
					GameLogic.drawCard(info, p);
					Main.Output.appendText("Since you conquered a territory you can draw a card!\n");
					info.invasionSuccessful = false;
				}
				Main.Output.appendText(">>Fortification step skipped\n");
				Main.Output.appendText(">>Press enter to continue\n\n");
				if(info.command == P1_FORT_ONE) {info.command = P1_CARD_SET;}
				if(info.command == P2_FORT_ONE) {info.command = P2_CARD_SET;}
				return;
			}
			
			//error check
			if (GameLogic.validateTerritory(lastInput, info, p)) {
				try {
					//checks country to be fortified
					info.fortifier = GameLogic.getTerritoryIndex(lastInput, info);
					
					if(info.numUnits[info.fortifier] == 1)
					{
						//errors
						Main.Output.appendText("Cant fortify with 1 unit choose again.\n\n");
						return;
					}
					//in order for a unit to reinforce there must be a conncetion of units to said territory
					//pathExists finds if there is a path of units
					if(GameLogic.pathExists(info.fortifier, info.fortified, info, p)==true) {
						if(info.command == P1_FORT_TWO) {info.command = P1_FORT_UNIT;}
						if(info.command == P2_FORT_TWO) {info.command = P2_FORT_UNIT;}
						Main.Output.appendText("How many units will you transfer?\n");
					}
					else {
						Main.Output.appendText("No path exists, try again.\n\n");
					}

				} catch (GameLogicException ex) {
					Main.Output.appendText(">> Error: Invalid Territory name.\n");
				}
			
			}else {
				Main.Output.appendText("Try Again.\n\n");
			}
			return;
		}
		
		
		
		if (info.command == P1_FORT_UNIT || info.command == P2_FORT_UNIT) {
			//in case of errors or a wrong decision users can still skip at this point
			if(lastInput.toLowerCase().equals("skip")){
				if(info.invasionSuccessful){
					if(info.command == P1_FORT_UNIT) {GameLogic.drawCard(info, 1);}
					if(info.command == P2_FORT_UNIT) {GameLogic.drawCard(info, 2);}
					Main.Output.appendText("Since you conquered a territory you can draw a card!\n");
					info.invasionSuccessful = false;
				}
				Main.Output.appendText(">>Fortification step skipped\n");
				Main.Output.appendText(">>Press enter to continue");
				if(info.command == P1_FORT_UNIT) {info.command = P1_CARD_SET;}
				if(info.command == P2_FORT_UNIT) {info.command = P2_CARD_SET;}
				return;
			}
				try {
					info.fortifyingNumber = Integer.parseInt(lastInput);
					//error checking number input- caught by NumberFormat Exception
				
					if (info.fortifyingNumber >= 0 && info.fortifyingNumber < info.numUnits[info.fortifier]) {
						//fortify positive numbers and less than the units in the fortifier
						//updating territory values
						info.numUnits[info.fortifier] -= info.fortifyingNumber;
						info.numUnits[info.fortified] += info.fortifyingNumber;
						main.PlayerText[info.fortified].setText(String.valueOf(info.numUnits[info.fortified]));
						main.PlayerText[info.fortifier].setText(String.valueOf(info.numUnits[info.fortifier]));
						if(info.invasionSuccessful){
							if(info.command == P1_FORT_UNIT) {GameLogic.drawCard(info, 1);}
							if(info.command == P2_FORT_UNIT) {GameLogic.drawCard(info, 2);}
							Main.Output.appendText("Since you conquered a territory you can draw a card!\n");
							info.invasionSuccessful = false;
						}
						if(info.command == P1_FORT_UNIT) {
							//Main.Output.appendText("Switch turns----Player two's turn will begin.\nPress Enter to proceed");
							Main.Output.appendText(">>Reinforce Complete\n>>Press enter to exchange cards!\n\n");
							info.command = P1_CARD_SET;
						}
						if(info.command == P2_FORT_UNIT) {
							//Main.Output.appendText("Switch turns----Player ones's turn will begin.\nPress Enter to proceed");
							Main.Output.appendText(">>Reinforce Complete\n>>Press enter to exchange cards\n\n");
							info.command = P2_CARD_SET;
						}
					}
					else{
						Main.Output.appendText("Invalid number\nTry again\n");
						return;
					}
				} catch (NumberFormatException ex) {
					Main.Output.appendText(">> Error: Not a number.\n");
				}
				return;
		}
		
		if (info.command == INVADE) {
			/* In this phase we invade.
			 * Since we know which player is attacking and defending based on the ownership
			 * Of the info.attacker and info.defender.
			 * All we have to do is pass the details through the function*/
			Main.Output.appendText("!!!ENGAGE!!!\n");
			GameLogic.battle(info.attacker, info.defender, info.attackingUnitNumber, info.defendingUnitNumber, info, main);
			main.PlayerMarker[info.attacker].setFill(Color.RED);
			main.PlayerMarker[info.defender].setFill(Color.BLUE);
				
			if (info.numUnits[info.defender] == 0) {
				//The invasion has then been won
				
				Color c = PColor[info.ownership[info.attacker]-1];
				main.PlayerMarker[info.attacker].setFill(c);
				main.PlayerMarker[info.defender].setFill(c);

				//!!!!!
				info.numTerritories[info.ownership[info.attacker]-1]++;
				info.numTerritories[info.ownership[info.defender]-1]--;
				GameLogic.changeTable(3, String.valueOf(info.numTerritories[0]), main.list1);
				GameLogic.changeTable(3, String.valueOf(info.numTerritories[1]), main.list2);
				//!!!!!
					
				if(info.numTerritories[0] == 0 || info.numTerritories[1] == 0) {
					info.command = WINNER;
					return;
				}
				
				info.command = INVASION_WON;
				Main.Output.appendText(constants.COUNTRY_NAMES[info.defender] + " has been conquered!\n");
				Main.Output.appendText("How many units will be transferred from the attacking territory?\n");
			} else if (info.numUnits[info.attacker] == 1 && info.ownership[info.attacker] == 1) {
				//If the info.attackers have run out of armies to use then they've lost
				info.command = P1_STAGE_MANAGER;
				//!!!!!
				Color c = PColor[info.ownership[info.attacker]-1];
				Color c2 = PColor[info.ownership[info.defender]-1];
				main.PlayerMarker[info.attacker].setFill(c);
				main.PlayerMarker[info.defender].setFill(c2);
				//!!!!!
				Main.Output.appendText("Invasion lost\n");
				Main.Output.appendText("You can either enter 'battle' to move to the attack stage or enter 'skip' to move to the fortification stage.\n");

			} else if (info.numUnits[info.attacker] == 1 && info.ownership[info.attacker] == 2) {
				//If the info.attackers have run out of armies to use then they've lost
				info.command = P2_STAGE_MANAGER;
				//!!!!!
				Color c = PColor[info.ownership[info.attacker]-1];
				Color c2 = PColor[info.ownership[info.defender]-1];
				main.PlayerMarker[info.attacker].setFill(c);
				main.PlayerMarker[info.defender].setFill(c2);
				//!!!!!
				Main.Output.appendText("Invasion lost\n");
				Main.Output.appendText("You can either enter 'battle' to move to the attack stage or enter 'skip' to move to the fortification stage.\n");
			} else {
				//If not then we have the chance to battle again or end the invasion
				info.command = BATTLE_AGAIN;
				main.PathLayer.getChildren().addAll(main.attack,main.fortify);
				Main.Output.appendText("Do you wish to attack again?\n" +
						"Enter 'battle' to attack again and 'skip' to proceed to the next stage.\n");
			}
			return;
		}
		
		if (info.command == BATTLE_AGAIN) {
			main.PathLayer.getChildren().removeAll(main.attack,main.fortify);
			if (lastInput.toLowerCase().equals("battle") && info.numUnits[info.attacker] > 1) {
				// If yes has been entered and if the number of the attacking units is not 0
				Main.Output.appendText(">> How many units will you use to attack:\n");
				if (info.ownership[info.attacker] == 1)
					info.command = P1_NUMBER_UNITS;
				else // Then it's player 1 that must decide how many they wanna battle with
					info.command = P2_NUMBER_UNITS;
			} else if (lastInput.toLowerCase().equals("skip")) {
				if (info.ownership[info.attacker] == 1) {
					info.command = P1_FORT_ONE;
					
					//these revert the colors of the units back from red and blue to their respective new colours
					Color c = PColor[info.ownership[info.attacker]-1];
					Color c2 = PColor[info.ownership[info.defender]-1];
					main.PlayerMarker[info.attacker].setFill(c);
					main.PlayerMarker[info.defender].setFill(c2);
					
					main.PlayerText[info.attacker].setText(String.valueOf(info.numUnits[info.attacker]));
					main.PlayerText[info.defender].setText(String.valueOf(info.numUnits[info.defender]));
				} else if (info.ownership[info.attacker] == 2) {// Then it's player 2's turn
					info.command = P2_FORT_ONE;
					
					Color c = PColor[info.ownership[info.attacker]-1];
					Color c2 = PColor[info.ownership[info.defender]-1];
					main.PlayerMarker[info.attacker].setFill(c);
					main.PlayerMarker[info.defender].setFill(c2);
					
					main.PlayerText[info.attacker].setText(String.valueOf(info.numUnits[info.attacker]));
					main.PlayerText[info.defender].setText(String.valueOf(info.numUnits[info.defender]));
				}
				Main.Output.appendText(">> Fortification stage\nChoose which territory you will fortify:\n");
			} else {
				Main.Output.appendText(">> Error: Invalid amount.\n");
				Main.Output.appendText("Try Again.\n");
			}
			return;
		}
		
		if (info.command == INVASION_WON) {
			try {
				int transferring = Integer.parseInt(lastInput);
				int limit = info.numUnits[info.attacker] - 1;
				if (transferring > limit || transferring < 1) {
					Main.Output.appendText("Invalid: Number out of range\nEnter an appropriate number:\n");
				} else {
					/*If this is an appropriate number then we can first change the ownership
					 * And then increase the number of the units present */
					info.ownership[info.defender] = info.ownership[info.attacker];
					info.numUnits[info.defender] += transferring;
					info.numUnits[info.attacker] -= transferring;
					main.PlayerText[info.attacker].setText(String.valueOf(info.numUnits[info.attacker]));
					main.PlayerText[info.defender].setText(String.valueOf(info.numUnits[info.defender]));
					Main.Output.appendText("Press enter to Decide next move");
						
					info.invasionSuccessful = true;
						
					if (info.ownership[info.attacker] == 1) {
						info.command = P1_TURN;
					}
					else {
						info.command = P2_TURN;
					}
				}
			} catch (NumberFormatException ex) {
				Main.Output.appendText(">> Error: Not a number.\n");
			}
			return;
		}
		
		
		//basic win state
		if(info.command == WINNER) {
			Text winningText  = new Text("");
			winningText.setFont(Font.font ("Verdana", 80));
			winningText.setFill(Color.WHITE);
			winningText.setStroke(Color.BLACK);
			if(info.numTerritories[1] == 0) {
				winningText.setText(info.Player1 +" has WON!!");
				
			}
			else {
				winningText.setText(info.Player2 +"has WON!!");
			}
			main.battle.Pause();
			main.lepanto.Pause();
			
			//music is a bit funky maybe change later
			Music WinMusic = new Music(8);
			WinMusic.Play();
			main.winHolder.getChildren().add(winningText);
			main.stage.setScene(main.winner);
		}	
	}
}