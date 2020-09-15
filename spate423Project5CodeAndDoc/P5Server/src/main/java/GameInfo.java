import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class GameInfo implements Serializable {

    String username;
    boolean easy;
    boolean medium;
    boolean expert;
    boolean serverChose;
    boolean gameDecided;
    String evaluation;
    int twoMoveCounter;
    int userScore;
    int gamesPlayed;
    ArrayList<ScoreNode> scoreKeeper = new ArrayList<>();

    Boolean idSet = false;
    String whoWon;
    ArrayList<Integer> clientCount = new ArrayList<>();
    ArrayList<Integer> inGameClients = new ArrayList<>();
    ArrayList<String> tttBoard;
    boolean boardfull;
    boolean isAdded;

    boolean gameAddedto_gamesArray = false;


    GameInfo(){
        initBoard();
    }

    ArrayList<String> initBoard(){
        tttBoard = new ArrayList<>();
        for (int i = 0; i < 9; i++){
            tttBoard.add("b");
        }
        return tttBoard;
    }

    void bubbleSort() {
        int n = scoreKeeper.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (scoreKeeper.get(j).score > scoreKeeper.get(j + 1).score) {
                    int temp = scoreKeeper.get(j).score;
                    scoreKeeper.get(j).score = scoreKeeper.get(j + 1).score;
                    scoreKeeper.get(j + 1).score = temp;
                }
            }
        }
    }

    public String getWhoWon () {
    return whoWon;
}
    public void setWhoWon (String whoWon){
    this.whoWon = whoWon;
}

}

class ScoreNode implements Serializable{
    String uNm;
    int score;
    ScoreNode(String uName){
        uNm = uName;
        score = 0;
    }
}