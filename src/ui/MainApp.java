package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.controller.GameViewController;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private GameViewController gameViewController;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("Jackaroo Card Game");
            
            // Show the game view directly
            showGameView();
            
            // Configure stage
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(800);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showGameView() {
        try {
            // Load game view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/GameView.fxml"));
            BorderPane gameView = (BorderPane) loader.load();
            
            // Create scene
            Scene scene = new Scene(gameView);
            primaryStage.setScene(scene);
            
            // Get the controller
            gameViewController = loader.getController();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public GameViewController getGameViewController() {
        return gameViewController;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 