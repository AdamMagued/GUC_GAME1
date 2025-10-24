package ui.view;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public class CardView extends StackPane {
    private String rank;
    private String suit;
    private boolean isSelected = false;
    private static final double CARD_WIDTH = 80;
    private static final double CARD_HEIGHT = 120;
    private static final double CORNER_RADIUS = 10;
    
    public CardView(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        
        // Create card background with rounded corners
        Rectangle cardBase = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        cardBase.setFill(Color.WHITE);
        cardBase.setStroke(Color.BLACK);
        cardBase.setArcWidth(CORNER_RADIUS);
        cardBase.setArcHeight(CORNER_RADIUS);
        
        // Create card content
        Text cardText = new Text(rank + "\n" + suit);
        cardText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.4, 0.4, 0.3));
        cardBase.setEffect(dropShadow);
        
        getChildren().addAll(cardBase, cardText);
        
        // Add click handler
        setOnMouseClicked(event -> {
            toggleSelection();
        });
        
        // Add hover effect
        setOnMouseEntered(event -> {
            if (!isSelected) {
                cardBase.setFill(Color.LIGHTGRAY);
                setTranslateY(-5); // Lift the card slightly
            }
        });
        
        setOnMouseExited(event -> {
            if (!isSelected) {
                cardBase.setFill(Color.WHITE);
                setTranslateY(0); // Return to original position
            }
        });
        
        // Add some margin around the card
        setMargin(this, new javafx.geometry.Insets(5));
    }
    
    public void toggleSelection() {
        isSelected = !isSelected;
        Rectangle cardBase = (Rectangle) getChildren().get(0);
        cardBase.setFill(isSelected ? Color.LIGHTBLUE : Color.WHITE);
        setTranslateY(isSelected ? -10 : 0); // Lift selected cards higher
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public String getRank() {
        return rank;
    }
    
    public String getSuit() {
        return suit;
    }
} 