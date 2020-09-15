import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread{

    GameInfo client = new GameInfo();
    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;
    int port;
    String address;
    String userID;

    private Consumer<Serializable> callback;

    Client(Consumer<Serializable> call, int port, String address, String userID){
        callback = call;
        this.port = port;
        this.address = address;
        this.userID = userID;
    }

    public void run() {

        try {
            socketClient= new Socket(address,port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {}

        while(true) {

            try {

                // Reads in the gameInfo sent from the server
                GameInfo data = (GameInfo) in.readObject();

                // Assigns all the data gameinfo to the client gameinfo for the approitate client with username
                // So that only the right client updates the board with X
                if (client.username.equals(data.username)) {
                    client = data;
                    callback.accept(client);
                }

                // Goes thru the scorekeeper arraylist of nodes and adds the clients that arent already present in that arraylist
                boolean isThere;
                for (int i = 0; i < data.scoreKeeper.size(); i++){
                    isThere = false;
                    for (int j = 0; j < client.scoreKeeper.size(); j++){
                        if (data.scoreKeeper.get(i).uNm.equals(client.scoreKeeper.get(j).uNm)){
                            isThere = true;
                            break;
                        }
                    }
                    // Addes the client to the arraylist and sends it back to the server
                    if (isThere == false){
                        client.scoreKeeper.add(data.scoreKeeper.get(i));
                        send(client);
                    }
                }

                // Adds the score taken from the server gameinfo and assins it to the approriate client with the updated score
                for(int i = 0; i < data.scoreKeeper.size(); i++){
                    for(int j = 0; j < client.scoreKeeper.size(); j++){
                        if (data.scoreKeeper.get(i).uNm.equals(client.scoreKeeper.get(j).uNm)){
                            client.scoreKeeper.get(j).score = data.scoreKeeper.get(i).score;
                        }
                    }
                }

            }

            catch(Exception e) {}
        }

    }

    public void send(Serializable data) {

        try {
            out.writeObject(data);
            out.reset();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
