import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TicTacToe extends Application{


    TextField s1;
    Button serverChoice;
    Button Title = new Button();
    Scene startScene;
    Server serverConnection;
    ListView<String> listItems;
    int port;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("This is server");

        s1 = new TextField("Enter Port Number");
        s1.setMaxWidth(135);
        serverChoice = new Button("Server On");
        serverChoice.setStyle("-fx-background-color: royalblue");
        serverChoice.setTextFill(Color.WHITE);

        serverChoice.setOnAction(e->{ primaryStage.setScene(createServerGui());
            primaryStage.setTitle("This is the Server");
            port = Integer.parseInt(s1.getText());
            System.out.println("Port Number is: " + port);
            serverConnection = new Server(data -> {
                Platform.runLater(()->{
                    listItems.getItems().add(data.toString());
                });
            }, port);
        });

        BorderPane startPane = new BorderPane();
        VBox buttonBox = new VBox(10, s1, serverChoice);
        buttonBox.setAlignment(Pos.CENTER);
        startPane.setCenter(buttonBox);
        startPane.setStyle("-fx-background-color: #4f4747");

        listItems = new ListView<String>();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        startScene = new Scene(startPane, 500,500);
        primaryStage.setScene(startScene);
        primaryStage.show();

    }

    public Scene createServerGui() {

        BorderPane bPane = new BorderPane();

        // Image for the tictactoe
        Image pic1 = new Image("tttmarqlrg.gif");
        ImageView v1 = new ImageView();
        v1.setFitHeight(100);
        v1.setFitWidth(500);
        v1.setImage(pic1);

        TextField title = new TextField("Tic-Tac-Toe!!!");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 23));

        listItems.setPrefWidth(500);
        listItems.setPrefHeight(460);

        VBox vbox = new VBox(0, v1, listItems);

        bPane.setCenter(vbox);

        bPane.setStyle("-fx-background-color: coral");

        return new Scene(bPane, 500, 500);

    }


}
