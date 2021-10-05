package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import CardsFolder.Card;
import CardsFolder.Cards;

import java.io.IOException;

import static application.constants.*;
//

public class Main extends Application {
	

		// declaring variables that will be initialized inside functions in GameBoard
		// all scenes 
		protected Scene menu,game,tutorialScene,cardScene1,cardScene2,winner;	 
	
		//all panes and boxes to display objects
		protected VBox tutorialHolder;
		protected StackPane layer,winHolder;
		protected static Pane PathLayer,adjacentLayer,DiceRoll,cardHolder1,cardHolder2;
	    
		//objects to be displayed on scenes
		protected Button startGame,tutorialButton,extraInfo,cards1,cards2,attack,fortify,closeCard1,closeCard2,next;
		protected Circle[] PlayerMarker= new Circle[42];
		protected Text[] PlayerText= new Text[42];
		protected Text CardText1,cardText2;
		
		//svg paths are a special case that allowed us to have the entire territory act as a button
		protected SVGPath[] CountryButton = new SVGPath[42];

		
		// tableViews which implement observable lists
		protected TableView<TableInfo> Player1,Player2;
		protected TableColumn<TableInfo,String> info1,info2,Stage1,Stage2;
		
		static TextArea Output;
		protected TextField Input;
	
		//just basic game info and commands
		protected int tutorialNo = 1;
		private GameInfo info;
		
		//last input holds input from user-text or countryButton
		private String lastInput;
		
		//used for changing stages and things in main
		private Main main = this;
		protected Stage stage;
		
		//external resource variables
		protected Music lepanto,battle;
		protected ImageView tutorial=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("tutorial0.png")));;
		//event handlers
		EventHandler<MouseEvent> mouseEvent;
		EventHandler<ActionEvent> event;
		
		ObservableList<TableInfo> list1,list2;
	
	public void start(Stage primaryStage) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			
			stage = primaryStage;
			primaryStage.setTitle("Risk-EvaBot");
			
			//Initializing game board structures
			info = new GameInfo();
			GameBoard g  = new GameBoard(info);
			
			//defining all mouse events 
			mouseEvent = new EventHandler<MouseEvent>() {
				
				@Override
			public void handle(MouseEvent event) {
				//event type lets us know wheter the user clicked on the path or just hovered over it etc
				String eventType = event.getEventType().toString();
				//when defining the CountryButton its id is set to the country which we convert to a string
				String country = 	((Node) event.getSource()).getId();
				//index of said country
				int index = GameLogic.getTerritoryIndex(country,info);
				
				if(eventType.equals("MOUSE_CLICKED")) {
					//just play a blip whenever the button is clicked
					Music noise = null;
					//music clips are caught by many exceptions
					try {
						noise = new Music(1);
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					noise.Play();
					
					//mouseCommands is an array list containing all possible commands usable by the mouse
					//e.g if the command was asking for number of units the mouse wont enter the value of the territory
	            	
					if(mouseCommands.contains(info.command)) {
					lastInput = country;
	    			Output.appendText("\n>" + lastInput + "\n\n"); // puts the users text into the JTextArea
	    			Input.setText("");
	    		
	    			//try catch is here as command has audio files within it 
	    			try {
	    				//command runs the game logic portion of the game eg current stage
	    				g.command(main, info, lastInput);
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
	    			//changeState updates the tableView to the current stage
	    			GameLogic.changeState(main.list1, main.list2, info.command,info.laststate,info.j);
	            	}
				}
				
				if(eventType.equals("MOUSE_ENTERED")) {
					//mouse entered means we hovered over it and we need to change the path colour to highlight
					((SVGPath) event.getSource()).setFill(Color.WHITE);
					
					
					//we also draw a line to all adjacents to show which can be attacked 
					for(int x=0; x< ADJACENT[index].length;x++) {
						Line adjacent  = new Line(COUNTRY_COORD[index][0],COUNTRY_COORD[index][1], 
						
						COUNTRY_COORD[ADJACENT[index][x]][0],COUNTRY_COORD[ADJACENT[index][x]][1]); 
						adjacentLayer.getChildren().add(adjacent);
						//adjacent lines are set to transparent to the mouse doesnt recognise them and try to click them
						adjacent.setMouseTransparent(true);
					}
					
					//displaying the name of the country in big lettering
					Text countryName = new Text(COUNTRY_NAMES[index]);
					
					countryName.setFont(Font.font ("Verdana", 40));
					countryName.setStroke(Color.WHITE);
					countryName.setFill(Color.BLACK);
					countryName.setLayoutX(700);
					countryName.setLayoutY(100);
					adjacentLayer.getChildren().addAll(countryName);
					}
				if(eventType.equals("MOUSE_EXITED")) {
					//converting the countryButton back to its original colour
					((SVGPath) event.getSource()).setFill(ConColor[CONTINENT_IDS[GameLogic.getTerritoryIndex(((Node) event.getSource()).getId(),info)]]);
					//adjacent layer removes all the text and adjacent lines 
					adjacentLayer.getChildren().clear();
					
				}
				}
	            };
	            
	            //defining all other events
			event = new EventHandler<ActionEvent>() {
		            public void handle(ActionEvent e)
		            {
		            	
		            	if(e.getSource() == tutorialButton) {
	            			primaryStage.setScene(tutorialScene);
		            	}
		            	
		            	if(e.getSource() == next) {
		            		//sets to the next tutorial image
		            		if(tutorialNo < 12) {
		            			tutorial.setImage(new Image(getClass().getClassLoader().getResourceAsStream("tutorial"+tutorialNo+".png")));
		            			//note here images need to be take as a classLoader and resourceStream to be able to export to jar

		            		}
		            		
		            		if(tutorialNo == 12) {
		            			primaryStage.setScene(game);
		            		}
		            		tutorialNo++;

		            	}
		            	
		            	
		            	if(e.getSource() == Input) {
		            	//input means we got input from the command line text field
		            	lastInput = Input.getText();    //Gets input
		    			Output.appendText("\n>" + Input.getText() + "\n\n"); // puts the users text into the JTextArea
		    			
		    			Input.setText("");	
		    			
		    			Music armySound = null;
						try {
							armySound = new Music(1);
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						armySound.Play();
						//if we are at the attack phase and skip we turn back on the normal music
						if(lastInput == "skip") {
			         		lepanto.Play();
		            		battle.Pause();
		            		battle.restart();
						}
						
						//if we attack we turn on battle music
						if(lastInput == "attack") {
		            		lepanto.Pause();
		            		battle.Play();
						}
						
						
						try {
							g.command(main,info,lastInput);
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						GameLogic.changeState(main.list1, main.list2, info.command,info.laststate,info.j);
		            	}
		            	
		            	//as well as being able to initiate attack with the command line
		            	//a button will appear which will allow you to click to move to battle phase instead
		            	if(e.getSource() == attack) {
		            		
		            		lepanto.Pause();
		            		battle.Play();
		            		lastInput = "battle";
			    			Output.appendText("\n>battle\n\n"); // puts the users text into the JTextArea
			    			Input.setText("");	
			    			try {
								g.command(main,info,lastInput);
							} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			    			GameLogic.changeState(main.list1, main.list2, info.command,info.laststate,info.j);
		            	}
		            	
		            	//same concept as attack button but with fortify
		            	if(e.getSource() == fortify) {
		            		lepanto.Play();
		            		battle.Pause();
		            		battle.restart();
		            		lastInput = "skip";
			    			Output.appendText("\n>fortify\n\n"); // puts the users text into the JTextArea
			    			Input.setText("");	
			    			
			    			try {
								g.command(main,info,lastInput);
							} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			    			GameLogic.changeState(main.list1, main.list2, info.command,info.laststate,info.j);
		            	}
		            	
		            	
		            	//player 1 cards button so the user can view their cards
		            	if(e.getSource() == cards1) {
		            		
		            		int valueX= 20;
		            		int valueY = 50;
		            		Text player1text = new Text("Player 1 cards");
							
		            		player1text.setFont(Font.font ("Verdana", 40));
		            		player1text.setStroke(Color.BLACK);
		            		player1text.setFill(Color.WHITE);
		            		player1text.setLayoutX(365);
		            		player1text.setLayoutY(40);
		            		//adding the return button and player text to scene
		            		cardHolder1.getChildren().addAll(closeCard1,player1text);
		            		
		            		
		            		//test when enabled the button will add 5 cards when clicked
		            		/*for(int x = 0; x<5;x++) {
		            			info.P1hand.add(info.deck.getTop());
		            		}*/
		            		
		            		//cycle through all owned cards
		            		for(Cards Card : info.P1hand ) {
		            			//creating an image view positioning it and then adding it to panel
		            			ImageView cardView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(Card.getTerritory()+".png")));	
		            			cardView.setLayoutX(valueX);
		            			cardView.setLayoutY(valueY);
		            			valueX += 100;
		            			if(valueX == 920) {
		            				valueY += 250;
		            				valueX = 20;
		            			}
		            			
		            			cardHolder1.getChildren().add(cardView);
		            		}
		            		
		            		//play sound of book being opened to show cards
		            		Music cardOpen = null;
							try {
								cardOpen = new Music(3);
							} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							cardOpen.Play();
							primaryStage.setScene(cardScene1);

		            	}
		            	
		            	//same as cards 1 but with player 2
		            	if(e.getSource() == cards2) {
		            		int valueX= 20;
		            		int valueY = 30;
		            		
	            		Text player2text = new Text("Player 2 cards");
							
		            		player2text.setFont(Font.font ("Verdana", 40));
		            		player2text.setStroke(Color.BLACK);
		            		player2text.setFill(Color.WHITE);
		            		player2text.setLayoutX(365);
		            		player2text.setLayoutY(40);
		            		cardHolder2.getChildren().addAll(closeCard2,player2text);

		            		for(Cards Card : info.P2hand ) {
		            			//System.out.println(Card.getTerritory());
		            			ImageView cardView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(Card.getTerritory()+".png")));	
		            			cardView.setLayoutX(valueX);
		            			cardView.setLayoutY(valueY);
		            			valueX += 100;
		            			if(valueX == 130) {
		            				valueY += 100;
		            			}
		            			
		            			cardHolder2.getChildren().add(cardView);
		            		}
		            		
		   
		            		Music cardOpen = null;
							try {
								cardOpen = new Music(3);
							} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							cardOpen.Play();
							primaryStage.setScene(cardScene2);
		            	}
		            	
		            	//just closes the scene and removes all the nodes on the scene
		            	if(e.getSource() == closeCard1) {
		            		
		            		Music cardClose = null;
									try {
										cardClose = new Music(4);
									} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									cardClose.Play();
									cardHolder1.getChildren().clear();
									primaryStage.setScene(game);
				            	}
		               	if(e.getSource() == closeCard2) {
		            		primaryStage.setScene(game);
		            		Music cardClose = null;
									try {
										cardClose = new Music(4);
									} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									cardClose.Play();
									cardHolder2.getChildren().clear();
				            	}
		            	
		            }
		            };
			
			
			//defining buttons
			startGame = new Button("Start game");
			startGame.setOnAction(e -> primaryStage.setScene(game));
			
			tutorialButton = new Button("Tutorial - Important!");
			tutorialButton.setOnAction(event);
			
			next = new Button("Next tutorial slide");
			next.setOnAction(event);

			closeCard1 = new Button("Return to game");
			closeCard1.setOnAction(event);
			
			closeCard2 = new Button("Return to game");
			closeCard2.setOnAction(event);

			cards1 = new Button();
			cards1.getStyleClass().add("button-cards1");
			cards1.setLayoutX(10);
			cards1.setLayoutY(10);
			cards1.setOnAction(event);
			
			cards2 = new Button();
			cards2.getStyleClass().add("button-cards2");
			cards2.setLayoutX(10);
			cards2.setLayoutY(275);
			cards2.setOnAction(event);
			
			attack = new Button();
			attack.getStyleClass().add("button-war");
			attack.setLayoutX(600);
			attack.setLayoutY(450);
			attack.setOnAction(event);

			fortify = new Button();
			fortify.getStyleClass().add("button-castle");
			fortify.setLayoutX(400);
			fortify.setLayoutY(450);
			fortify.setOnAction(event);
			
			//defining all clickable country objects
			for(int x = 0;x<42;x++) {
				//layout
				CountryButton[x] = new SVGPath();
				CountryButton[x].setContent(constants.Paths[x]);
				CountryButton[x].setLayoutY(CountryButton[x].getLayoutX()-100);
				CountryButton[x].setPickOnBounds(false);
				//looks
				CountryButton[x].setStroke(Color.BLACK);
				CountryButton[x].setId(COUNTRY_NAMES[x]);
				CountryButton[x].setFill(ConColor[CONTINENT_IDS[x]]);
				//functionality
				CountryButton[x].setOnMouseEntered(mouseEvent);
				CountryButton[x].setOnMouseExited(mouseEvent);
				CountryButton[x].setOnMousePressed(e -> ((SVGPath) e.getSource()).setFill(Color.BLACK));
				CountryButton[x].setOnMouseReleased(e -> ((SVGPath) e.getSource()).setFill(ConColor[CONTINENT_IDS[GameLogic.getTerritoryIndex(((Node) e.getSource()).getId(),info)]]));
				CountryButton[x].setOnMouseClicked(mouseEvent);
			}
			
			//input and output fields
			Input = new TextField("Commands are input here or use the board to highlight different options");
			Input.setOnAction(event);
			
			Output = new TextArea("Welcome To Risk!\nPlease enter Name of Player1 - (Player1 Will be represented by a white dot on a territory)\n");
			Output.setMinHeight(175);
			Output.setEditable(false);
			
			//observable lists which will be added to a a table view
			list1 = FXCollections.observableArrayList(
					new TableInfo ("Player Colour","White"),
					new TableInfo("Player name", "Waiting for input"),
					new TableInfo("GameStage","Entering Name"),
					new TableInfo("Territory Count","0"),
					new TableInfo("Units to place","9"));
			
			
			list2 = FXCollections.observableArrayList(
					new TableInfo ("Player Colour","Black"),
					new TableInfo("Player name", "Waiting for input"),
					new TableInfo("GameStage","Waiting"),
					new TableInfo("Territory Count","0"),
					new TableInfo("Units to place","9"));
			

			//defining table views and adding the columns needs
			
			Player1 = new TableView<>();
			Player1.setMaxHeight(262.5);	
			
			info1 = new TableColumn("Info");
			info1.setCellValueFactory(new PropertyValueFactory<>("info"));
			info1.setMinWidth(122);
			
			Stage1 = new TableColumn("Player 1");
			Stage1.setCellValueFactory(new PropertyValueFactory<>("Case"));
			Stage1.setMinWidth(120);
			
			Player1.getColumns().addAll(Stage1,info1);
			Player1.setItems(list1);
			

			Player2 = new TableView<>();
			Player2.setMaxHeight(262.5);	
			
			info2 = new TableColumn("Info");
			info2.setCellValueFactory(new PropertyValueFactory<>("info"));
			info2.setMinWidth(122);
			
			Stage2 = new TableColumn("Player 2");
			Stage2.setCellValueFactory(new PropertyValueFactory<>("Case"));
			Stage2.setMinWidth(120);
			
			Player2.getColumns().addAll(Stage2,info2);
			Player2.setItems(list2);
			  
			//defining music player for background audio
			battle = new Music(2);
			lepanto = new Music(0);
			lepanto.Play();
			
			
			//setting up image views for backgrounds
			ImageView view = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("sea.jpg")));
			ImageView viewMenu = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("menu.jpg")));
			ImageView winnerImage =  new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Final.jpg")));

			winnerImage.setFitHeight(FRAME_HEIGHT+50);
			winnerImage.setFitWidth(FRAME_WIDTH+300);
			view.setFitHeight(FRAME_HEIGHT-150 );
			viewMenu.setFitHeight(400);
			viewMenu.setFitWidth(600);

			//defining all layouts
			HBox menuLayout = new HBox(startGame,tutorialButton);
			menuLayout.setAlignment(Pos.CENTER);
					
			adjacentLayer = new Pane();
			adjacentLayer.setMouseTransparent(true);
					
			PathLayer = new Pane(cards1,cards2);
			for(int x =0;x<42;x++) {
				PathLayer.getChildren().add(CountryButton[x]);
			}
					
			layer = new StackPane(view,PathLayer,adjacentLayer);
					
			DiceRoll = new Pane();
			cardHolder1 = new Pane();
			cardHolder2 = new Pane();
			tutorialHolder = new VBox(next,tutorial);		
			tutorialHolder.setAlignment(Pos.CENTER);
			winHolder = new StackPane(winnerImage);
			
			
			//in layouts like gameLayout we have layouts within layouts 
			//gameLayout lays the table views container and then the command and map in a horizontal fashion
			//then both of the subContainers lay their objects out vertically
			VBox mapDialogueContainer = new VBox(layer,Output,Input);	
			VBox gameInfoContainer = new VBox(Player1,Player2,DiceRoll);
			HBox gameLayout = new HBox(gameInfoContainer,mapDialogueContainer);

			StackPane menuImage = new StackPane(viewMenu,menuLayout);

			//adding layouts to scenes and adding the css written to them
			menu = new Scene(menuImage,400,400);
			menu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			tutorialScene = new Scene(tutorialHolder,1500,900);
			tutorialScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			cardScene1 = new Scene(cardHolder1,1000,500);
			cardScene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			cardHolder1.setStyle("-fx-background-color: #faebd7");
			
			cardScene2 = new Scene(cardHolder2,1000,500);
			cardScene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			cardHolder2.setStyle("-fx-background-color: #faebd7");
			
			game = new Scene(gameLayout,constants.FRAME_WIDTH+250,constants.FRAME_HEIGHT+50);
			game.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			winner = new Scene(winHolder,constants.FRAME_WIDTH+250,constants.FRAME_HEIGHT+50);
			winner.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			
			primaryStage.setResizable(false);
			primaryStage.setScene(menu);
			primaryStage.show();
	
	}

	public static void main(String[] args) {
		launch(args);
	}

}