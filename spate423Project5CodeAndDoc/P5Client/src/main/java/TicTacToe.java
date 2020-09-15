import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.control.TextArea;
// import com.sun.xml.internal.ws.api.server.Container;
// import sun.font.BidiUtils;

import java.util.ArrayList;

public class TicTacToe extends Application{
    PauseTransition pause1 = new PauseTransition(Duration.seconds(3));
    PauseTransition pause2 = new PauseTransition(Duration.seconds(1));

    Boolean gameStart = false;

    // Difficiulty buttons
    Button easy = new Button("Easy");
    Button medium = new Button("Medium");
    Button expert = new Button("Expert");
    Button quit = new Button("Quit");

    // Textfields that display names and general msg
    TextField gInfoTitle = new TextField("GameInfo");
    TextField myId = new TextField(" Username");
    TextField myScore = new TextField(" My Score");
    TextField gamesPlayed = new TextField(" Games played");

    // Textfield for the scoreboard
    TextField topScore = new TextField("LeaderBoard");
    TextField firstPos = new TextField("Rank 1: ");
    TextField secondPos = new TextField("Rank 2: ");
    TextField thirdPos = new TextField("Rank 3: ");

    Button refresh = new Button("Refresh LeaderBoard");

    TextField s1,s2,s3, pauseStatement;
    Font stmntFont = Font.font("ALGERIAN", 12);
    Button clientChoice;
    Client clientConnection;
    ListView<String> listItems2;
    ListView<Integer> clientlistItems = new ListView<Integer>();
    int port;

    // Buttons represent the tiles of the gameboard
    Button b0 = new Button();
    Button b1 = new Button();
    Button b2 = new Button();
    Button b3 = new Button();
    Button b4 = new Button();
    Button b5 = new Button();
    Button b6 = new Button();
    Button b7 = new Button();
    Button b8 = new Button();

    TextField winText = new TextField();

    VBox leftSide;
    VBox rightSide;

    TextArea gameText = new TextArea("Choose your Difficulty");

    MenuBar menu = new MenuBar();

    Button player1Pic = new Button();
    Button player2Pic = new Button();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Tic-Tac-Toe!!!");

        // Menu bar for Third Scene
        Menu mOne = new Menu("Option");
        MenuItem mTwo = new MenuItem("Replay");
        mOne.getItems().addAll(mTwo);
        menu.getMenus().addAll((mOne));

        // Textfield for the first scene
        s1 = new TextField("Enter Port Number");
        s2 = new TextField("Enter IP Address");
        s3 = new TextField("Enter User ID");
        pauseStatement = new TextField("CLICK THE BUTTON ON THE RIGHT TO REVEAL YOUR OPPONENT'S MOVE");
        pauseStatement.setAlignment(Pos.CENTER);
        pauseStatement.setFont(stmntFont);
        pauseStatement.setVisible(false);
        s1.setMaxWidth(135);
        s1.setAlignment(Pos.CENTER);
        s2.setMaxWidth(130);
        s2.setAlignment(Pos.CENTER);
        s3.setMaxWidth(125);
        s3.setAlignment(Pos.CENTER);
        player1Pic.setStyle("-fx-background-color: coral;");
        player2Pic.setStyle("-fx-background-color: coral;");
        clientChoice = new Button("Connect");

        // This is when the client enters port and address and presses connect
        // Change the scene and check for input
        clientChoice.setOnAction(e-> {primaryStage.setScene(clientGUIfirstScene());
            primaryStage.setTitle("This is a client");
            port = Integer.parseInt(s1.getText());
            System.out.println("Port Number is: " + port);
            System.out.println("Address is: " + s2.getText());
            System.out.println("User ID is: " + s3.getText());
            myId.setText(s3.getText() + ": Username");

            clientConnection = new Client(data->{
                Platform.runLater(()->{

                    // Checks to see if server has made a move using MinMax and then calls printx method that displays
                    // the X on the board
                    if ((clientConnection.client.serverChose == true)){
                        printX();
                        clientConnection.client.serverChose = false;
                    }
                    // Checks to see if the game has been won or lost or tied and disables the btns and displays text
                    if (clientConnection.client.gameDecided == true){
                        b0.setDisable(true); b1.setDisable(true);
                        b2.setDisable(true); b3.setDisable(true);
                        b4.setDisable(true); b5.setDisable(true);
                        b6.setDisable(true); b7.setDisable(true);
                        b8.setDisable(true);
                        winText.setText(clientConnection.client.evaluation);
                        winText.setDisable(true);
                        myScore.setText(clientConnection.client.userScore + ": My Score");
                    }
                });
            }, port, s2.getText(),s3.getText());
            clientConnection.start();
            // Gets the text from the textfield and assisngs it to the gameInfo string var
            clientConnection.client.username = s3.getText();
            System.out.println(clientConnection.client.username);
            // Creates the new scoreNode class and addes it to the arraylist of nodes for each client
            ScoreNode tmpNode = new ScoreNode(clientConnection.client.username);
            clientConnection.client.scoreKeeper.add(tmpNode);
        });

        // Tile 1 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b0.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(0).equals("b")){
                clientConnection.client.tttBoard.set(0, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b0.setText("O");
                b0.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b0.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 2 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b1.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(1).equals("b")){
                clientConnection.client.tttBoard.set(1, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b1.setText("O");
                b1.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b1.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 3 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b2.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(2).equals("b")) {
                clientConnection.client.tttBoard.set(2, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b2.setText("O");
                b2.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b2.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 4 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b3.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(3).equals("b")) {
                clientConnection.client.tttBoard.set(3, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b3.setText("O");
                b3.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b3.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 5 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b4.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(4).equals("b")) {
                clientConnection.client.tttBoard.set(4, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b4.setText("O");
                b4.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b4.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 6 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b5.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(5).equals("b")) {
                clientConnection.client.tttBoard.set(5, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b5.setText("O");
                b5.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b5.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 7 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b6.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(6).equals("b")) {
                clientConnection.client.tttBoard.set(6, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b6.setText("O");
                b6.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b6.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 8 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b7.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(7).equals("b")) {
                clientConnection.client.tttBoard.set(7, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b7.setText("O");
                b7.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b7.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Tile 9 for the game board and once clicked if sets O for player move if that tile is b and sends to server with new info
        b8.setOnAction(event -> {
            System.out.println(clientConnection.client.tttBoard);
            if(clientConnection.client.tttBoard.get(8).equals("b")) {
                clientConnection.client.tttBoard.set(8, "O");
                System.out.println(clientConnection.client.tttBoard + "\n");
                b8.setText("O");
                b8.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b8.setTextFill(Color.WHITE);
                clientConnection.send(clientConnection.client);
            }
        });

        // Refreshes the scoreboard and sets the score for the top 3 clients
        // Once pressed it bubble sorts the arraylist of nodes and sets the textfields
        refresh.setOnAction(event -> {
            clientConnection.client.bubbleSort();
            for (int i = clientConnection.client.scoreKeeper.size()-1; i>=0  ; i--) {
                System.out.println("Sorted list: " + clientConnection.client.scoreKeeper.get(i).uNm +
                        " Score is: " + clientConnection.client.scoreKeeper.get(i).score);
            }
            // If there is only 1 node in arraylist
            if (clientConnection.client.scoreKeeper.size() == 1){
                firstPos.setText("1) " + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-1).uNm + " : "
                        + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-1).score);
            }
            // If there is only 2 nodes in arraylist
            if (clientConnection.client.scoreKeeper.size() == 2){
                firstPos.setText("1) " + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-1).uNm + " : "
                        + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-1).score);
                secondPos.setText("2) " + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-2).uNm + " : "
                        + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-2).score);
            }
            // If there are more than 2 nodes in arraylist
            if (clientConnection.client.scoreKeeper.size() > 2) {
                firstPos.setText("1) " + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-1).uNm + " : "
                        + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-1).score);
                secondPos.setText("2) " + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-2).uNm + " : "
                        + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-2).score);
                thirdPos.setText("3) " + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-3).uNm + " : "
                        + clientConnection.client.scoreKeeper.get(clientConnection.client.scoreKeeper.size()-3).score);
            }
        });

        // Replay button that changes the scene to second scene and resets all the variables that are needed again
        mTwo.setOnAction(e->{
            primaryStage.setScene(clientGUIfirstScene());
            b0.setText(""); b1.setText(""); b2.setText("");
            b3.setText(""); b4.setText(""); b5.setText("");
            b6.setText(""); b7.setText(""); b8.setText("");
            clientConnection.client.initBoard();
            clientConnection.client.easy = false;
            clientConnection.client.medium = false;
            clientConnection.client.expert = false;
            clientConnection.client.boardfull = false;
            clientConnection.client.gameDecided = false;
            clientConnection.client.twoMoveCounter = 0;
            b0.setDisable(false); b1.setDisable(false);
            b2.setDisable(false); b3.setDisable(false);
            b4.setDisable(false); b5.setDisable(false);
            b6.setDisable(false); b7.setDisable(false);
            b8.setDisable(false);
            winText.clear();
        });

        // Quit button in the second scene
        quit.setOnAction(event -> {
            Platform.exit();
        });

        VBox buttonBox = new VBox(10, s1, s2, s3, clientChoice);
        buttonBox.setAlignment(Pos.CENTER);
        BorderPane startPane = new BorderPane();
        startPane.setCenter(buttonBox);
        startPane.setStyle("-fx-background-color: lightpink");

        Scene startScene = new Scene(startPane, 500,500);

        listItems2 = new ListView<String>();

        // If easy btn pressed then changes the scene
        easy.setOnAction(event -> {
            primaryStage.setScene(createClientGui());
            clientConnection.client.easy = true;
            clientConnection.client.gamesPlayed++;
            gamesPlayed.setText(clientConnection.client.gamesPlayed + ": Games Played");
        });

        // If medium btn pressed then changes the scene
        medium.setOnAction(event -> {
            primaryStage.setScene(createClientGui());
            clientConnection.client.gamesPlayed++;
            gamesPlayed.setText(clientConnection.client.gamesPlayed + ": Games Played");
            clientConnection.client.medium = true;
        });

        // If expert btn pressed then changes the scene
        expert.setOnAction(event -> {
            primaryStage.setScene(createClientGui());
            clientConnection.client.gamesPlayed++;
            gamesPlayed.setText(clientConnection.client.gamesPlayed + ": Games Played");
            clientConnection.client.expert = true;
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setScene(startScene);
        primaryStage.show();

    }

    public Scene clientGUIfirstScene(){

        BorderPane bpane = new BorderPane();

        VBox center = new VBox(20, easy, medium, expert);
        center.setAlignment(Pos.CENTER);
        bpane.setCenter(center);

        // Image of TicTacToe
        Image pic1 = new Image("tttmarqlrg.gif");
        ImageView v1 = new ImageView();
        v1.setFitHeight(100);
        v1.setFitWidth(500);
        v1.setImage(pic1);
        bpane.setTop(v1);

        // Sets the style and position of each btn and textfield
        topScore.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        topScore.setStyle("-fx-background-color: gold");
        topScore.setAlignment(Pos.CENTER);

        firstPos.setStyle("-fx-background-color: gold");
        firstPos.setAlignment(Pos.CENTER);

        secondPos.setStyle("-fx-background-color: gold");
        secondPos.setAlignment(Pos.CENTER);

        thirdPos.setStyle("-fx-background-color: gold");
        thirdPos.setAlignment(Pos.CENTER);

        refresh.setAlignment(Pos.CENTER);

        VBox leaderbox = new VBox(25, firstPos, secondPos, thirdPos);

        leftSide = new VBox(40, topScore, leaderbox, refresh);
        leftSide.setAlignment(Pos.CENTER);

        bpane.setMargin(leftSide, new Insets(0,0,0,0));
        bpane.setLeft(leftSide);

        gInfoTitle.setStyle("-fx-background-color: gold");
        gInfoTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        gInfoTitle.setAlignment(Pos.CENTER);

        myId.setStyle("-fx-background-color: gold");
        myId.setPrefWidth(200);
        myId.setAlignment(Pos.CENTER);

        myScore.setStyle("-fx-background-color: gold");
        myScore.setAlignment(Pos.CENTER);

        gamesPlayed.setStyle("-fx-background-color: gold");
        gamesPlayed.setAlignment(Pos.CENTER);

        VBox gInfoBox = new VBox(25, myId, myScore, gamesPlayed);

        rightSide = new VBox(40, gInfoTitle, gInfoBox, quit);
        rightSide.setAlignment(Pos.CENTER);

        bpane.setMargin(rightSide, new Insets(0,0,0,0));
        bpane.setRight(rightSide);

        TextField diffText = new TextField("Choose your Difficulty To Start!");
        diffText.setStyle("-fx-background-color: gold");
        diffText.setAlignment(Pos.CENTER);

        diffText.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        bpane.setBottom(diffText);

        bpane.setStyle("-fx-background-color: gold");

        return new Scene(bpane, 500, 500);
    }

    // Second scene when you press connect
    public Scene createClientGui() {

        BorderPane bPane = new BorderPane();
        // Top menu
        bPane.setTop(menu);

        // this sets the size of the board using btns and style them
        // Used in hbox and vbox to make a tictactoe board
        b0.setPrefSize(75,75);
        b1.setPrefSize(75,75);
        b2.setPrefSize(75,75);
        b3.setPrefSize(75,75);
        b4.setPrefSize(75,75);
        b5.setPrefSize(75,75);
        b6.setPrefSize(75,75);
        b7.setPrefSize(75,75);
        b8.setPrefSize(75,75);

        b0.setStyle("-fx-background-color: black;");
        b1.setStyle("-fx-background-color: black;");
        b2.setStyle("-fx-background-color: black;");
        b3.setStyle("-fx-background-color: black;");
        b4.setStyle("-fx-background-color: black;");
        b5.setStyle("-fx-background-color: black;");
        b6.setStyle("-fx-background-color: black;");
        b7.setStyle("-fx-background-color: black;");
        b8.setStyle("-fx-background-color: black;");

        HBox hBox1 = new HBox(10, b0, b1, b2);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setStyle("-fx-background-color: white");
        hBox1.setMaxSize(320,320);

        HBox hBox2 = new HBox(10, b3, b4, b5);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setStyle("-fx-background-color: white");
        hBox2.setMaxSize(320,320);

        HBox hBox3 = new HBox(10, b6, b7, b8);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.setStyle("-fx-background-color: white");
        hBox3.setMaxSize(320,320);

        VBox vBox = new VBox(10, hBox1, hBox2, hBox3);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: white");
        vBox.setMaxSize(320,320);

        bPane.setCenter(vBox);

        bPane.setBottom(winText);
        winText.setStyle("-fx-background-color: black;" +
                "-fx-text-inner-color: white");
        winText.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        winText.setAlignment(Pos.CENTER);

        bPane.setStyle("-fx-background-color: black");

        return new Scene(bPane, 500, 500);
    }

    // Method used to display the server "X" move once the server has made a move
    void printX(){
        // It has pause transition which makes it look real
        pause2.play();
        // Checks the position at arraylist and see if it equals X and if it does then it sets text to X
        pause2.setOnFinished(event -> {
            if (clientConnection.client.tttBoard.get(0).equals("X")) {
                b0.setText("X");
                b0.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b0.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(1).equals("X")) {
                b1.setText("X");
                b1.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b1.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(2).equals("X")) {
                b2.setText("X");
                b2.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b2.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(3).equals("X")) {
                b3.setText("X");
                b3.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b3.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(4).equals("X")) {
                b4.setText("X");
                b4.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b4.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(5).equals("X")) {
                b5.setText("X");
                b5.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b5.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(6).equals("X")) {
                b6.setText("X");
                b6.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b6.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(7).equals("X")) {
                b7.setText("X");
                b7.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b7.setTextFill(Color.WHITE);
            }
            if (clientConnection.client.tttBoard.get(8).equals("X")) {
                b8.setText("X");
                b8.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                b8.setTextFill(Color.WHITE);
            }
        });
    }

}
