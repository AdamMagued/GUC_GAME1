# Jackaroo Card Game

A robust, object-oriented board game engine built in Java, featuring a responsive **JavaFX** user interface. This project implements a race game where players use cards to navigate marbles around a board, utilizing complex movement mechanics like splitting moves, swapping positions, and handling safe zones.

## Features

* **Interactive UI (JavaFX)**:
* **MVC Architecture**: Clean separation of logic and display using FXML (`GameView.fxml`, `RootLayout.fxml`) and Controllers (`GameViewController`, `RootLayoutController`).
* **Visual Feedback**: Dedicated `CardView` classes to render card assets dynamically with hover effects and selection states.
* **Root Layout**: Menus and layout management handled via a centralized root controller.


* **Card-Driven Mechanics**: Movement is dictated by playing cards rather than dice. Supports various card types (e.g., standard moves, `Ten`, `Wild`, `Seven`) with polymorphic behavior.
* **Intelligent Board System**:
* **Circular Track**: Manages a linked list of cells representing the board state.
* **Safe Zones**: Dedicated logic for player home paths and starting bases.
* **Collision Handling**: Logic for "bumping" opponent marbles back to their base.


* **Player AI**: Includes a `CPU` class for automated opponents, allowing for single-player gameplay.
* **Advanced Movement Logic**:
* **Splitting**: Certain cards (like the Seven) allow splitting movement values between multiple marbles.
* **Swapping**: Logic to swap positions with other marbles (e.g., using a Jack).
* **Fielding**: Rules for bringing new marbles onto the board (e.g., utilizing an Ace or King).


* **Robust Validation**: A comprehensive custom exception system (`GameException`, `IllegalMovementException`, `InvalidCardException`) ensures all moves adhere to game rules.

## Tech Stack

* **Language**: Java (Object-Oriented Design)
* **UI Framework**: JavaFX (FXML + CSS)
* **Testing**: JUnit
* **Data Storage**: CSV (Card configurations)

## Project Structure

The codebase follows a strict Model-View-Controller (MVC) pattern:

* **Main Entry**: `MainApp.java` - Launches the JavaFX stage and initializes the Root and Game layouts.
* **View (UI)**:
* `RootLayout.fxml` & `RootLayoutController`: Handles the top-level menu and window structure.
* `GameView.fxml` & `GameViewController`: Manages the main game board, user interactions, and game loop updates.
* `CardView`: Handles the graphical representation of playing cards.


* **Model (Core Logic)**:
* `Game.java`, `GameManager.java`: Manages turns, rounds, and high-level game state.
* `Board.java`, `Cell.java`: Handles physical grid, adjacency, and marble placement.
* `Player.java`, `Marble.java`: Represents game entities and their state.


* **Data & Config**: `Cards.csv` defines the deck composition and card attributes.

## Installation & Usage

### Prerequisites

* Java Development Kit (JDK) 11 or higher
* JavaFX SDK

### Compilation

To compile the project, ensure you include the JavaFX library path:

```bash
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -d bin src/**/*.java

```

### Running the Game

Execute the `MainApp` class to launch the GUI:

```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp bin ui.MainApp

```

### Running Tests

To verify the game logic using the provided test suites:

```bash
java -cp bin:junit-platform-console-standalone.jar org.junit.runner.JUnitCore test.Milestone1PublicTests

```

## Configuration

* **Cards.csv**: Defines the deck composition. You can modify this file to adjust the frequency, rank, and suit of cards available in the game.



This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
