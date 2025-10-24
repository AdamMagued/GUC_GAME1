package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class RootLayoutController {
    
    @FXML
    private void handleNewGame() {
        // TODO: Implement new game logic
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Jackaroo Card Game");
        alert.setContentText("A card game implementation using JavaFX.");
        alert.showAndWait();
    }
} 