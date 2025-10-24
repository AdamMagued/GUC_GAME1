package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import engine.Game;
import engine.board.Cell;
import engine.board.CellType;
import model.Colour;
import model.card.Card;
import model.player.Player;
import model.player.CPU;
import model.player.Marble;
import model.card.standard.Seven;
import exception.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

public class GameViewController {
    @FXML private GridPane gameBoard;
    @FXML private Label currentPlayerLabel;
    @FXML private Label nextPlayerLabel;
    @FXML private Label firePitTopCard;
    @FXML private Label statusLabel;
    @FXML private HBox playerCardsArea;
    @FXML private VBox playerInfoArea;
    @FXML private Spinner<Integer> splitDistanceSpinner;
    @FXML private ToggleGroup cardGroup;
    
    private Game game;
    private Map<Colour, Color> colourMap;
    private Map<Cell, StackPane> cellNodes;
    private static final int CELL_SIZE = 40;
    private static final int MARBLE_SIZE = 30;
    
    @FXML
    public void initialize() {
        // Initialize game first
        initializeGame();
        
        // Setup UI components after game initialization
        Platform.runLater(() -> {
            setupBoard();
            setupColourMap();
            setupKeyboardShortcuts();
            updateUI();
        });
    }

    private void initializeGame() {
        try {
            // Show name input dialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Welcome to Jackaroo");
            dialog.setHeaderText("Enter your name:");
            dialog.setContentText("Name:");

            Optional<String> result = dialog.showAndWait();
            String playerName = result.orElse("Player 1");

            game = new Game(playerName);
            cellNodes = new HashMap<>();
            
            // Initialize split distance spinner
            SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 3);
            splitDistanceSpinner.setValueFactory(valueFactory);
            
        } catch (IOException e) {
            showError("Failed to initialize game", e.getMessage());
        }
    }

    private void setupColourMap() {
        colourMap = new HashMap<>();
        colourMap.put(Colour.RED, Color.RED);
        colourMap.put(Colour.GREEN, Color.GREEN);
        colourMap.put(Colour.BLUE, Color.BLUE);
        colourMap.put(Colour.YELLOW, Color.YELLOW);
    }

    private void setupBoard() {
        // Clear existing board
        gameBoard.getChildren().clear();
        gameBoard.getColumnConstraints().clear();
        gameBoard.getRowConstraints().clear();
        
        // Set grid constraints to ensure proper sizing
        gameBoard.setGridLinesVisible(true);
        gameBoard.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2;");
        gameBoard.setMaxSize(CELL_SIZE * 10, CELL_SIZE * 10);
        gameBoard.setMinSize(CELL_SIZE * 10, CELL_SIZE * 10);
        
        // Create board cells
        for (int i = 0; i < 10; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(CELL_SIZE);
            colConstraints.setPrefWidth(CELL_SIZE);
            colConstraints.setMaxWidth(CELL_SIZE);
            gameBoard.getColumnConstraints().add(colConstraints);
            
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(CELL_SIZE);
            rowConstraints.setPrefHeight(CELL_SIZE);
            rowConstraints.setMaxHeight(CELL_SIZE);
            gameBoard.getRowConstraints().add(rowConstraints);
            
            for (int j = 0; j < 10; j++) {
                StackPane cellPane = createCellPane();
                gameBoard.add(cellPane, j, i);
                
                // Store reference to cell node
                Cell cell = game.getBoard().getTrack().get(i * 10 + j);
                cellNodes.put(cell, cellPane);
                
                // Add click handler
                final int row = i;
                final int col = j;
                cellPane.setOnMouseClicked(e -> handleCellClick(cell));
            }
        }
        
        // Force layout update
        gameBoard.requestLayout();
    }

    private StackPane createCellPane() {
        StackPane cellPane = new StackPane();
        cellPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        cellPane.setMinSize(CELL_SIZE, CELL_SIZE);
        cellPane.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #f8f8f8;");
        
        // Add hover effect
        cellPane.setOnMouseEntered(e -> {
            String currentStyle = cellPane.getStyle();
            cellPane.setStyle(currentStyle + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        });
        
        cellPane.setOnMouseExited(e -> {
            String currentStyle = cellPane.getStyle().replace("; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0)", "");
            cellPane.setStyle(currentStyle);
        });
        
        return cellPane;
    }

    private void handleCellClick(Cell cell) {
        if (cell.getMarble() != null) {
            try {
                game.selectMarble(cell.getMarble());
                updateUI();
            } catch (InvalidMarbleException e) {
                showError("Invalid Marble Selection", e.getMessage());
            }
        }
    }

    private void updateUI() {
        // Check for game end
        Colour winner = game.checkWin();
        if (winner != null) {
            // Update the board one last time before showing game over
            updateBoard();
            updatePlayerCards();
            updatePlayerInfoPanels();
            
            // Show game over dialog
            showGameOverDialog(winner);
            return;
        }
        
        // Update player info
        Colour currentColour = game.getActivePlayerColour();
        Colour nextColour = game.getNextPlayerColour();
        
        // Find players by color
        Player currentPlayer = findPlayerByColour(currentColour);
        Player nextPlayer = findPlayerByColour(nextColour);
        
        currentPlayerLabel.setText(currentPlayer.getName() + 
                                " (" + currentColour + ")");
        nextPlayerLabel.setText("Next: " + nextPlayer.getName() + 
                              " (" + nextColour + ")");
        
        Card topCard = !game.getFirePit().isEmpty() ? game.getFirePit().get(game.getFirePit().size() - 1) : null;
        firePitTopCard.setText(topCard != null ? topCard.toString() : "Empty");
        
        // Update board
        updateBoard();
        
        // Update player cards
        updatePlayerCards();
        
        // Update player info panels
        updatePlayerInfoPanels();
    }

    private Player findPlayerByColour(Colour colour) {
        for (Player player : game.getPlayers()) {
            if (player.getColour() == colour) {
                return player;
            }
        }
        return null;
    }

    private void updateBoard() {
        // Clear all marbles
        cellNodes.values().forEach(cell -> {
            cell.getChildren().clear();
            
            // Reset cell style based on type
            Cell boardCell = cellNodes.entrySet().stream()
                .filter(entry -> entry.getValue() == cell)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
                
            if (boardCell != null) {
                String baseStyle = "-fx-border-color: black; -fx-border-width: 1;";
                
                switch (boardCell.getCellType()) {
                    case BASE:
                        cell.setStyle(baseStyle + "; -fx-background-color: #e0e0e0;");
                        break;
                    case SAFE:
                        cell.setStyle(baseStyle + "; -fx-background-color: #c8e6c9;"); // Light green
                        break;
                    case ENTRY:
                        cell.setStyle(baseStyle + "; -fx-background-color: #fff9c4;"); // Light yellow
                        break;
                    default:
                        if (boardCell.isTrap()) {
                            cell.setStyle(baseStyle + "; -fx-background-color: #ffcdd2;"); // Light red for trap
                        } else {
                            cell.setStyle(baseStyle + "; -fx-background-color: #f8f8f8;");
                        }
                }
            }
        });
        
        // Update marbles
        for (Cell cell : cellNodes.keySet()) {
            StackPane cellPane = cellNodes.get(cell);
            Marble marble = cell.getMarble();
            
            if (marble != null) {
                Circle marbleCircle = new Circle(MARBLE_SIZE / 2);
                marbleCircle.setFill(colourMap.get(marble.getColour()));
                
                // Add marble highlight effect
                marbleCircle.setStroke(Color.BLACK);
                marbleCircle.setStrokeWidth(2);
                
                // Add selection effect for marbles
                Player currentPlayer = findPlayerByColour(game.getActivePlayerColour());
                if (currentPlayer != null && game.getBoard().getActionableMarbles().contains(marble)) {
                    marbleCircle.setEffect(new javafx.scene.effect.DropShadow(10, Color.GOLD));
                }
                
                cellPane.getChildren().add(marbleCircle);
            }
        }
    }

    private void updatePlayerCards() {
        playerCardsArea.getChildren().clear();
        
        Player currentPlayer = findPlayerByColour(game.getActivePlayerColour());
        if (currentPlayer != null) {
            for (Card card : currentPlayer.getHand()) {
                Button cardButton = createCardButton(card);
                playerCardsArea.getChildren().add(cardButton);
            }
        }
    }

    private Button createCardButton(Card card) {
        Button cardButton = new Button(card.toString());
        cardButton.setMinSize(80, 120);
        cardButton.setPrefSize(80, 120);
        cardButton.setWrapText(true);
        cardButton.setStyle("-fx-background-color: white; -fx-border-color: black; " +
                          "-fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Add hover effect
        cardButton.setOnMouseEntered(e -> 
            cardButton.setStyle(cardButton.getStyle() + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);"));
        
        cardButton.setOnMouseExited(e -> 
            cardButton.setStyle(cardButton.getStyle().replace("; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0)", "")));
        
        // Add selection effect
        Player currentPlayer = findPlayerByColour(game.getActivePlayerColour());
        if (currentPlayer != null && currentPlayer.getSelectedCard() == card) {
            cardButton.setStyle(cardButton.getStyle() + "; -fx-border-color: gold; -fx-border-width: 2;");
        }
        
        cardButton.setOnAction(e -> handleCardSelection(card));
        return cardButton;
    }

    private void updatePlayerInfoPanels() {
        playerInfoArea.getChildren().clear();
        
        for (Player player : game.getPlayers()) {
            VBox playerPanel = new VBox(5);
            playerPanel.setStyle("-fx-padding: 10; -fx-border-color: " + 
                               toHexString(colourMap.get(player.getColour())) + 
                               "; -fx-border-width: 2; -fx-background-color: white;");
            
            Label nameLabel = new Label(player.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            
            Label colourLabel = new Label("Color: " + player.getColour());
            Label cardsLabel = new Label("Cards: " + player.getHand().size());
            Label marblesLabel = new Label("Marbles in Home: " + player.getMarbles().size());
            
            playerPanel.getChildren().addAll(nameLabel, colourLabel, cardsLabel, marblesLabel);
            
            // Highlight current player
            if (player.getColour() == game.getActivePlayerColour()) {
                playerPanel.setStyle(playerPanel.getStyle() + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
            }
            
            playerInfoArea.getChildren().add(playerPanel);
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    @FXML
    private void handleFieldMarble() {
        try {
            game.fieldMarble();
            updateUI();
        } catch (CannotFieldException | IllegalDestroyException e) {
            showError("Cannot Field Marble", e.getMessage());
        }
    }
    
    @FXML
    private void handlePlaySelected() {
        try {
            // Update split distance if Seven card
            Player currentPlayer = findPlayerByColour(game.getActivePlayerColour());
            if (currentPlayer.getSelectedCard() instanceof Seven) {
                game.editSplitDistance(splitDistanceSpinner.getValue());
            }
            
            currentPlayer.play();
            
            // If successful, end turn
            game.endPlayerTurn();
            
            // Update UI first
            updateUI();
            
            // Check for winner after UI update
            Colour winner = game.checkWin();
            if (winner == null) {
                // If no winner and CPU player is next, schedule their turn
                Player nextPlayer = findPlayerByColour(game.getActivePlayerColour());
                if (nextPlayer instanceof CPU) {
                    scheduleCPUTurn();
                }
            }
            
        } catch (GameException e) {
            showError("Cannot Play Selection", e.getMessage());
        }
    }
    
    @FXML
    private void handleDeselectAll() {
        game.deselectAll();
        updateUI();
    }

    private void handleCardSelection(Card card) {
        try {
            game.selectCard(card);
            updateUI();
        } catch (InvalidCardException e) {
            showError("Invalid Card Selection", e.getMessage());
        }
    }

    private void scheduleCPUTurn() {
        // Add 1 second delay before CPU turn
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    try {
                        game.playPlayerTurn();
                        game.endPlayerTurn();
                        
                        // Update UI first
                        updateUI();
                        
                        // Check for winner after UI update
                        Colour winner = game.checkWin();
                        if (winner == null) {
                            // If no winner and next player is CPU, schedule their turn
                            Player nextPlayer = findPlayerByColour(game.getActivePlayerColour());
                            if (nextPlayer instanceof CPU) {
                                scheduleCPUTurn();
                            }
                        }
                        
                    } catch (GameException e) {
                        showError("CPU Turn Error", e.getMessage());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void setupKeyboardShortcuts() {
        // Wait for the scene to be ready
        Platform.runLater(() -> {
            if (gameBoard != null && gameBoard.getScene() != null) {
                gameBoard.getScene().addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                    if (e.getCode() == KeyCode.SPACE) {
                        handleFieldMarble();
                    }
                });
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWinner(Colour winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("We have a winner!");
        alert.setContentText("Player " + winner + " has won the game!");
        alert.showAndWait();
    }

    private void showGameOverDialog(Colour winner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over!");
            
            Player winningPlayer = findPlayerByColour(winner);
            String winnerName = winningPlayer != null ? winningPlayer.getName() : "Unknown";
            
            alert.setContentText(winnerName + " (" + winner + ") has won the game!");
            
            // Add buttons to start new game or exit
            ButtonType newGameButton = new ButtonType("New Game");
            ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(newGameButton, exitButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == newGameButton) {
                    try {
                        // Show name input dialog for new game
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("New Game");
                        dialog.setHeaderText("Enter your name:");
                        dialog.setContentText("Name:");

                        Optional<String> nameResult = dialog.showAndWait();
                        String playerName = nameResult.orElse("Player 1");

                        // Create new game instance
                        game = new Game(playerName);
                        
                        // Reset UI
                        cellNodes = new HashMap<>();
                        setupBoard();
                        setupColourMap();
                        
                        // Initialize split distance spinner
                        SpinnerValueFactory<Integer> valueFactory = 
                            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 3);
                        splitDistanceSpinner.setValueFactory(valueFactory);
                        
                        updateUI();
                    } catch (IOException e) {
                        showError("Failed to start new game", e.getMessage());
                        Platform.exit();
                    }
                } else {
                    Platform.exit();
                }
            }
        });
    }
} 