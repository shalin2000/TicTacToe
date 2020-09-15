import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;
import java.util.Scanner;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server {

//    int tempCount = 0;
    int gameCount = 0;
    ArrayList<GameInfo> games= new ArrayList<GameInfo>();
    ArrayList<ScoreNode> sList = new ArrayList<>();
    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    int port;
    GameInfo client1GM = new GameInfo();

    Server(Consumer<Serializable> call, int port){
        this.port = port;
        callback = call;
        server = new TheServer();
        server.start();

    }

    public class TheServer extends Thread{

        public void run() {

            try(ServerSocket mysocket = new ServerSocket(port);){
                System.out.println("Port is: " + port);
                System.out.println("Server is waiting for a client!");
                while(true) {

                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: " + "client #" + count + "             Connection: " + count);
                    clients.add(c);
                    c.start();
                    count++;
                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }


    class ClientThread extends Thread{

        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        int myId;

        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
        }

        // Updates the client with the gameInfo object
        public void updateClients(GameInfo data) {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(data);
                    t.out.reset();
                }
                catch(Exception e) {}
            }
        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            while(true) {
                try {

                    // reads the gameInfo that is being sent from the client
                    GameInfo data = (GameInfo) in.readObject();

                    // Goes thru the scorekeeper arraylist of nodes and adds the clients that arent already present in that arraylist
                    boolean isThere;
                    for (int i = 0; i < data.scoreKeeper.size(); i++){
                        isThere = false;
                        for (int j = 0; j < sList.size(); j++){
                            if (data.scoreKeeper.get(i).uNm.equals(sList.get(j).uNm)){
                                isThere = true;
                                break;
                            }
                        }
                        // Addes the client to the arraylist and sends it back to the server
                        if (isThere == false){
                            sList.add(data.scoreKeeper.get(i));
                        }
                    }
                    client1GM = data;
                    client1GM.scoreKeeper = sList;

                    // Addes each game into a arraylist to keep track of differnt games going on
                    if (((data.easy == true) || (data.medium == true) || (data.expert == true)) && (client1GM.gameAddedto_gamesArray == false)){
                        client1GM.gameAddedto_gamesArray = true;
                        games.add(client1GM);
                    }

                    // If the board is full and has no b left then it evalutes the method whoWon ands updates the client
                    if ((!client1GM.tttBoard.get(0).equals("b")) && (!client1GM.tttBoard.get(1).equals("b")) &&
                            (!client1GM.tttBoard.get(2).equals("b")) && (!client1GM.tttBoard.get(3).equals("b")) &&
                            (!client1GM.tttBoard.get(4).equals("b")) && (!client1GM.tttBoard.get(5).equals("b")) &&
                            (!client1GM.tttBoard.get(6).equals("b")) && (!client1GM.tttBoard.get(7).equals("b")) &&
                            (!client1GM.tttBoard.get(8).equals("b"))){
                        client1GM.boardfull = true;
                        client1GM.evaluation = whoWon();
                        callback.accept("Game Outcome - for user " + client1GM.username + " - " + client1GM.evaluation);
                        updateClients(client1GM);
                    }

                    // Assigns the score to slist accordingly depending on the usernames
                    for (int j = 0; j < client1GM.scoreKeeper.size(); j++){
                        if (client1GM.username.equals(sList.get(j).uNm)){
                            sList.get(j).score = client1GM.userScore;
                        }
                    }

////////////////////////////////////////////////////////////////////////////////////////////////////
                    // Finds the next move for easy difficutly
                    if ((data.easy == true) && (client1GM.boardfull == false)){
                        // generate random number
                        Random randomGenerator = new Random();
                        int randomInt;
                        Boolean moveAccepted = false;
                        // gets random number and checks to see if that tile is taken and if not then sets X
                        while(moveAccepted == false) {
                            randomInt = randomGenerator.nextInt(9);
                            if(client1GM.tttBoard.get(randomInt).equals("b")){
                                moveAccepted = true;
                                client1GM.serverChose = true;

                                // If game is not over then it sets X there
                                if (client1GM.gameDecided == false) {
                                    client1GM.tttBoard.set(randomInt, "X");
                                }
                                // Updates the listview and keeps track of the score in sList arraylist of nodes
                                callback.accept("Difficulty Easy - Updated Board for user - " + client1GM.username + " - " + client1GM.tttBoard);
                                client1GM.evaluation = whoWon();
                                for (int j = 0; j < client1GM.scoreKeeper.size(); j++){
                                    if (client1GM.username.equals(sList.get(j).uNm)){
                                        sList.get(j).score = client1GM.userScore;
                                    }
                                }
                                // If game is decided then it removes that game from the games arraylist and updates the client
                                if (client1GM.gameDecided == true) {
                                    for (int i = 0; i < games.size(); i++){
                                        if (client1GM.username.equals(games.get(i).username)){
                                            games.remove(i);
                                        }
                                    }
                                    client1GM.gameAddedto_gamesArray = true;
                                    callback.accept("Game Outcome - for user " + client1GM.username + " - " + client1GM.evaluation);
                                }
                                client1GM.scoreKeeper = sList;
                                updateClients(client1GM);
                            }
                            // If the board is full and has no b left then it evalutes the method whoWon ands updates the client
                            if ((!client1GM.tttBoard.get(0).equals("b")) && (!client1GM.tttBoard.get(1).equals("b")) &&
                                    (!client1GM.tttBoard.get(2).equals("b")) && (!client1GM.tttBoard.get(3).equals("b")) &&
                                    (!client1GM.tttBoard.get(4).equals("b")) && (!client1GM.tttBoard.get(5).equals("b")) &&
                                    (!client1GM.tttBoard.get(6).equals("b")) && (!client1GM.tttBoard.get(7).equals("b")) &&
                                    (!client1GM.tttBoard.get(8).equals("b"))){
                                whoWon();
                                break;
                            }
                        }
                        updateClients(client1GM);
                    }

////////////////////////////////////////////////////////////////////////////////////////////////////
                    // Finds the next move for medium difficutly
                    if ((data.medium == true) && (client1GM.boardfull == false)){
                        // Used this statment to make sure that the expert mode is used only twice in the medium mode
                        if (client1GM.twoMoveCounter < 2){
                            client1GM.twoMoveCounter++;
                            boolean serverWin = false;
                            int winState = 0;
                            // Using the minmax algorithm
                            Object[] temp = client1GM.tttBoard.toArray();
                            String[] temp2 = new String[9];
                            for (int i = 0; i < 9; i++) {
                                temp2[i] = temp[i].toString();
                            }
                            MinMax minmax = new MinMax(temp2);
                            minmax.findMoves();
                            ArrayList<Integer> zeroNum = new ArrayList<>();
                            // For loop that goes through the statelist that was made using minmax and then
                            // puts value 0 into a arraylist which will be used later
                            for (int x = 0; x < minmax.stateList.size(); x++) {
                                Node tmp = minmax.stateList.get(x);
                                if (tmp.getMinMax() == 0) {
                                    zeroNum.add(x);
                                }
                                // used for instant win
                                if (tmp.getMinMax() == 10) {
                                    winState = x;
                                    serverWin = true;
                                    break;
                                }
                            }
                            if (serverWin == false) {
                                // Gets a random number and chooses it from the arraylist that had 0 from minmax
                                Random randomGenerator = new Random();
                                int randomInt;
                                randomInt = randomGenerator.nextInt(zeroNum.size());
                                // gets that random int from zero arraylist and sets it to the board
                                for (int i = 0; i < client1GM.tttBoard.size(); i++) {
                                    String l = minmax.stateList.get(zeroNum.get(randomInt)).getstate()[i];
                                    client1GM.tttBoard.set(i, l);
                                }
                                client1GM.serverChose = true;
                                callback.accept("Difficulty Medium - Updated Board for user - " + client1GM.username + " - " + client1GM.tttBoard);
                                client1GM.evaluation = whoWon();
                                // Updates the listview and keeps track of the score in sList arraylist of nodes
                                for (int j = 0; j < client1GM.scoreKeeper.size(); j++){
                                    if (client1GM.username.equals(sList.get(j).uNm)){
                                        sList.get(j).score = client1GM.userScore;
                                    }
                                }
                                // If game is decided then it removes that game from the games arraylist and updates the client
                                if (client1GM.gameDecided == true) {
                                    for (int i = 0; i < games.size(); i++){
                                        if (client1GM.username.equals(games.get(i).username)){
                                            games.remove(i);
                                        }
                                    }
                                    client1GM.gameAddedto_gamesArray = true;
                                    callback.accept("Game Outcome - for user " + client1GM.username + " - " + client1GM.evaluation);
                                }
                                client1GM.scoreKeeper = sList;
                                updateClients(client1GM);
                            }
                        }
                        // Easy difficulty used meaning complete random
                        else {
                            // gets random number
                            Random randomGenerator = new Random();
                            int randomInt;
                            Boolean moveAccepted = false;
                            // gets random number and checks to see if that tile is taken and if not then sets X
                            while(moveAccepted == false) {
                                randomInt = randomGenerator.nextInt(9);
                                if(client1GM.tttBoard.get(randomInt).equals("b")){
                                    moveAccepted = true;
                                    client1GM.serverChose = true;
                                    // If game is not over then it sets X there
                                    if (client1GM.gameDecided == false) {
                                        client1GM.tttBoard.set(randomInt, "X");
                                    }
                                    // Updates the listview and keeps track of the score in sList arraylist of nodes
                                    callback.accept("Difficulty Medium - Updated Board for user - " + client1GM.username + " - " + client1GM.tttBoard);
                                    client1GM.evaluation = whoWon();
                                    for (int j = 0; j < client1GM.scoreKeeper.size(); j++){
                                        if (client1GM.username.equals(sList.get(j).uNm)){
                                            sList.get(j).score = client1GM.userScore;
                                        }
                                    }
                                    // If game is decided then it removes that game from the games arraylist and updates the client
                                    if (client1GM.gameDecided == true) {
                                        for (int i = 0; i < games.size(); i++){
                                            if (client1GM.username.equals(games.get(i).username)){
                                                games.remove(i);
                                            }
                                        }
                                        client1GM.gameAddedto_gamesArray = true;
                                        callback.accept("Game Outcome - for user " + client1GM.username + " - " + client1GM.evaluation);
                                    }
                                    client1GM.scoreKeeper = sList;
                                    updateClients(client1GM);
                                }
                                // If the board is full and has no b left then it evalutes the method whoWon ands updates the client
                                if ((!client1GM.tttBoard.get(0).equals("b")) && (!client1GM.tttBoard.get(1).equals("b")) &&
                                        (!client1GM.tttBoard.get(2).equals("b")) && (!client1GM.tttBoard.get(3).equals("b")) &&
                                        (!client1GM.tttBoard.get(4).equals("b")) && (!client1GM.tttBoard.get(5).equals("b")) &&
                                        (!client1GM.tttBoard.get(6).equals("b")) && (!client1GM.tttBoard.get(7).equals("b")) &&
                                        (!client1GM.tttBoard.get(8).equals("b"))){
                                    whoWon();
                                    break;
                                }
                            }
                        }
                    }

////////////////////////////////////////////////////////////////////////////////////////////////////
                    // Finds the next move for expert difficutly
                    if ((data.expert == true) && (client1GM.boardfull == false)){
                        boolean serverWin = false;
                        int winState = 0;
                        // Using the minmax algorithm
                        Object[] temp = client1GM.tttBoard.toArray();
                        String[] temp2 = new String[9];
                        for(int i = 0; i < 9; i++) {
                            temp2[i] = temp[i].toString();
                        }
                        MinMax minmax = new MinMax(temp2);
                        minmax.findMoves();
                        ArrayList<Integer> zeroNum = new ArrayList<>();
                        // For loop that goes through the statelist that was made using minmax and then
                        // puts value 0 into a arraylist which will be used later
                        for(int x = 0; x < minmax.stateList.size(); x++ ){
                            Node tmp = minmax.stateList.get(x);
                            // used for the best move
                            if (tmp.getMinMax() == 0){
                                zeroNum.add(x);
                            }
                            // used for instant win
                            if (tmp.getMinMax() == 10){
                                winState = x;
                                serverWin = true;
                                break;
                            }
                        }
                        // This is a direct win if server can make it
                        if (serverWin == true){
                            // used for the move that will make the server win and sets the board to that statelist
                            for (int i = 0; i < client1GM.tttBoard.size(); i++) {
                                String l = minmax.stateList.get(winState).getstate()[i];
                                client1GM.tttBoard.set(i, l);
                            }
                            // Updates the listview and keeps track of the score in sList arraylist of nodes
                            client1GM.serverChose = true;
                            callback.accept("Difficulty Expert - Updated Board for user - " + client1GM.username + " - " + client1GM.tttBoard);
                            client1GM.evaluation = whoWon();
                            for (int j = 0; j < client1GM.scoreKeeper.size(); j++){
                                if (client1GM.username.equals(sList.get(j).uNm)){
                                    sList.get(j).score = client1GM.userScore;
                                }
                            }
                            // If game is decided then it removes that game from the games arraylist and updates the client
                            if (client1GM.gameDecided == true) {
                                for (int i = 0; i < games.size(); i++){
                                    if (client1GM.username.equals(games.get(i).username)){
                                        games.remove(i);
                                    }
                                }
                                client1GM.gameAddedto_gamesArray = true;
                                callback.accept("Game Outcome - for user " + client1GM.username + " - " + client1GM.evaluation);
                            }
                            client1GM.scoreKeeper = sList;
                            updateClients(client1GM);
                        }
                        // Not direct win but the best move for server
                        if (serverWin == false) {
                            // gets random numbers and takes the best move from the zero arraylist
                            Random randomGenerator = new Random();
                            int randomInt;
                            randomInt = randomGenerator.nextInt(zeroNum.size());
                            // gets that random int from zero arraylist and sets it to the board
                            for (int i = 0; i < client1GM.tttBoard.size(); i++) {
                                String l = minmax.stateList.get(zeroNum.get(randomInt)).getstate()[i];
                                client1GM.tttBoard.set(i, l);
                            }
                            // Updates the listview and keeps track of the score in sList arraylist of nodes
                            client1GM.serverChose = true;
                            callback.accept("Difficulty Expert - Updated Board for user - " + client1GM.username + " - " + client1GM.tttBoard);
                            client1GM.evaluation = whoWon();
                            for (int j = 0; j < client1GM.scoreKeeper.size(); j++){
                                if (client1GM.username.equals(sList.get(j).uNm)){
                                    sList.get(j).score = client1GM.userScore;
                                }
                            }
                            // If game is decided then it removes that game from the games arraylist and updates the client
                            if (client1GM.gameDecided == true) {
                                for (int i = 0; i < games.size(); i++){
                                    if (client1GM.username.equals(games.get(i).username)){
                                        games.remove(i);
                                    }
                                }
                                client1GM.gameAddedto_gamesArray = true;
                                callback.accept("Game Outcome - for user " + client1GM.username + " - " + client1GM.evaluation);
                            }
                            client1GM.scoreKeeper = sList;
                            updateClients(client1GM);
                        }
                    }

                }

                catch(Exception e) {
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    for (int i = 0; i < client1GM.clientCount.size(); i++){
                        if (client1GM.clientCount.get(i) == myId){
                            client1GM.clientCount.remove(i);
                        }
                    }
                    System.out.println("New list is: " + client1GM.clientCount);
                    updateClients(client1GM);
                    clients.remove(this);
                    break;
                }
            }
        }//end of run

        // Used to determine who the winner is based on the move chosen and returns a string
        public String whoWon(){
            // Statements to check if client has won
            if(client1GM.tttBoard.get(0).equals("O") && client1GM.tttBoard.get(1).equals("O") && client1GM.tttBoard.get(2).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(3).equals("O") && client1GM.tttBoard.get(4).equals("O") && client1GM.tttBoard.get(5).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(6).equals("O") && client1GM.tttBoard.get(7).equals("O") && client1GM.tttBoard.get(8).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(0).equals("O") && client1GM.tttBoard.get(3).equals("O") && client1GM.tttBoard.get(6).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(1).equals("O") && client1GM.tttBoard.get(4).equals("O") && client1GM.tttBoard.get(7).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(2).equals("O") && client1GM.tttBoard.get(5).equals("O") && client1GM.tttBoard.get(8).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(0).equals("O") && client1GM.tttBoard.get(4).equals("O") && client1GM.tttBoard.get(8).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            if(client1GM.tttBoard.get(2).equals("O") && client1GM.tttBoard.get(4).equals("O") && client1GM.tttBoard.get(6).equals("O")) //horizontal top
            {
                client1GM.gameDecided = true;
                client1GM.userScore++;
                return "Player Won!";
            }

            // Statements to see if Server has won
            if(client1GM.tttBoard.get(0).equals("X") && client1GM.tttBoard.get(1).equals("X") && client1GM.tttBoard.get(2).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(3).equals("X") && client1GM.tttBoard.get(4).equals("X") && client1GM.tttBoard.get(5).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(6).equals("X") && client1GM.tttBoard.get(7).equals("X") && client1GM.tttBoard.get(8).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(0).equals("X") && client1GM.tttBoard.get(3).equals("X") && client1GM.tttBoard.get(6).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(1).equals("X") && client1GM.tttBoard.get(4).equals("X") && client1GM.tttBoard.get(7).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(2).equals("X") && client1GM.tttBoard.get(5).equals("X") && client1GM.tttBoard.get(8).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(0).equals("X") && client1GM.tttBoard.get(4).equals("X") && client1GM.tttBoard.get(8).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            if(client1GM.tttBoard.get(2).equals("X") && client1GM.tttBoard.get(4).equals("X") && client1GM.tttBoard.get(6).equals("X")) //horizontal top
            {
                client1GM.gameDecided = true;
                return "Server Won!";
            }

            for (int i = 0; i < client1GM.tttBoard.size(); i++){
                if (client1GM.tttBoard.get(i).equals("b")){
                    client1GM.gameDecided = false;
                    return "";
                }
            }
            client1GM.gameDecided = true;
            return "Tie!";
        }
    }//end of client thread

}
