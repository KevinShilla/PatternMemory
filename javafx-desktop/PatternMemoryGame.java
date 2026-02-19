import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class PatternMemoryGame extends Application {

    private static final int PADDING = 10;
    private static final Color LIT_COLOR = Color.YELLOW;
    private static final Color DEFAULT_COLOR = Color.GRAY;
    private static final Color BACKGROUND_COLOR = Color.DARKSLATEGRAY;

    private List<Rectangle> squares = new ArrayList<>();
    private List<Integer> sequence = new ArrayList<>();
    private int currentStep = 0;
    private int currentRound = 1;

    private int gridSize;
    private int squareSize;

    @Override
    public void start(Stage primaryStage) {
        Button button3x3 = new Button("3 x 3");
        Button button6x6 = new Button("6 x 6");
        Button button12x12 = new Button("12 x 12");

        button3x3.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #8BC34A; -fx-text-fill: white;");
        button6x6.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #03A9F4; -fx-text-fill: white;");
        button12x12.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-color: #FF5722; -fx-text-fill: white;");

        button3x3.setOnAction(e -> startGame(3));
        button6x6.setOnAction(e -> startGame(6));
        button12x12.setOnAction(e -> startGame(12));

        VBox vbox = new VBox(20, button3x3, button6x6, button12x12);
        vbox.setStyle("-fx-background-color: #303030; -fx-padding: 20px; -fx-alignment: center;");
        vbox.setSpacing(20);

        Scene scene = new Scene(vbox, 300, 200, BACKGROUND_COLOR);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pattern Memory Game");
        primaryStage.show();
    }

    private void startGame(int gridSize) {
        this.gridSize = gridSize;
        this.squareSize = 400 / gridSize;
        this.currentStep = 0;
        this.currentRound = 1;
        this.squares.clear();
        this.sequence.clear();

        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #303030; -fx-padding: 20px; -fx-alignment: center;");
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Rectangle square = createSquare(i, j);
                gridPane.add(square, j, i);
                squares.add(square);
            }
        }

        Scene scene = new Scene(gridPane, 400 + PADDING, 400 + PADDING, BACKGROUND_COLOR);
        Stage gameStage = new Stage();
        gameStage.setScene(scene);
        gameStage.setTitle("Pattern Memory Game");
        gameStage.show();

        playNextRound();
    }

    private Rectangle createSquare(int row, int col) {
        Rectangle square = new Rectangle(squareSize, squareSize, DEFAULT_COLOR);
        square.setArcWidth(10);
        square.setArcHeight(10);
        square.setOnMouseClicked(event -> handleSquareClick(squares.indexOf(square)));
        return square;
    }

    private void handleSquareClick(int index) {
        if (sequence.size() > currentStep && sequence.get(currentStep) == index) {
            animateSquare(squares.get(index), LIT_COLOR, DEFAULT_COLOR);
            currentStep++;
            if (currentStep == currentRound) {
                currentRound++;
                currentStep = 0;
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        playNextRound();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } else {
            showGameOver();
        }
    }

    private void playNextRound() {
        if (currentRound > gridSize * gridSize) {
            showGameOver();
            return;
        }

        int newSquare;
        do {
            newSquare = (int) (Math.random() * squares.size());
        } while (!sequence.isEmpty() && newSquare == sequence.get(sequence.size() - 1));

        sequence.add(newSquare);
        highlightSequence();
    }

    private void highlightSequence() {
        new Thread(() -> {
            try {
                for (int index : sequence) {
                    animateSquare(squares.get(index), LIT_COLOR, DEFAULT_COLOR);
                    Thread.sleep(700);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void animateSquare(Rectangle square, Color fromColor, Color toColor) {
        FillTransition ft = new FillTransition(Duration.millis(500), square, fromColor, toColor);
        ft.play();
    }

    private void showGameOver() {
        Stage gameOverStage = new Stage(StageStyle.UNDECORATED);

        Label gameOverLabel = new Label("Game Over! Your score: " + (currentRound - 1));
        gameOverLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-text-fill: red;");

        Button button = new Button("New Game");
        button.setOnAction(e -> {
            gameOverStage.close();
            startGame(gridSize);
        });
        button.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #03A9F4; -fx-text-fill: white;");

        VBox gameOverPane = new VBox(10, gameOverLabel, button);
        gameOverPane.setStyle("-fx-background-color: #303030; -fx-padding: 20px; -fx-alignment: center;");

        Scene gameOverScene = new Scene(gameOverPane, 300, 100);
        gameOverStage.setScene(gameOverScene);
        gameOverStage.setTitle("Game Over");
        gameOverStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
