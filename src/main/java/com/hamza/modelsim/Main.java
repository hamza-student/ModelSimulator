package com.hamza.modelsim;

import com.hamza.modelsim.abstractcomponents.Point;
import com.hamza.modelsim.abstractcomponents.Wire;
import com.hamza.modelsim.components.Canvas;
import com.hamza.modelsim.components.MenuBar;
import com.hamza.modelsim.components.Terminal;
import com.hamza.modelsim.constants.Colors;
import com.hamza.modelsim.constants.TerminalConstants;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {
    public static ObservableList<Terminal> inputTerminals;
    public static ObservableList<Terminal> outputTerminals;
    public static ObservableList<Wire> wires;
    public static Scene scene;
    public static Canvas canvas;

    private Point mousePosition;

    @Override
    public void start(Stage mainStage) {
        mainStage.setTitle("Model simulator");
        mainStage.setFullScreen(true);
        mainStage.setFullScreenExitHint("");
        mainStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        inputTerminals = FXCollections.observableArrayList();
        outputTerminals = FXCollections.observableArrayList();
        wires = FXCollections.observableArrayList();
        mousePosition = new Point();

        VBox root = new VBox();
        root.setBackground(Background.fill(Colors.backgroundColor));
        root.setFillWidth(true);
        root.setSpacing(0);

        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main.css")).toExternalForm());

        canvas = new Canvas();
        MenuBar menuBar = new MenuBar(scene);

        scene.setOnMouseMoved(e -> {
            mousePosition.setX(e.getSceneX());
            mousePosition.setY(e.getSceneY());
        });

        root.getChildren().add(canvas.getDrawable());
        root.getChildren().add(menuBar.getDrawable());

        double xPosition = 0;
        double yPosition = 8;
        double paddingY = yPosition * 2;
        double borderWidth = 20;
        double borderHeight = Screen.getPrimary().getBounds().getHeight() - paddingY - menuBar.getHeight();

        Rectangle inputTerminalsBase = makeRectangle(
                xPosition, yPosition,
                borderWidth, borderHeight, Colors.bordersColor
        );
        Rectangle outputTerminalsBase = makeRectangle(
                Screen.getPrimary().getBounds().getWidth() - xPosition - borderWidth, yPosition,
                borderWidth, borderHeight, Colors.bordersColor
        );

        canvas.add(inputTerminalsBase);
        canvas.add(outputTerminalsBase);

        // INPUT terminals

        // TODO: You're able to fix this SOS, do it then...
        // displayTestTerminal(canvas, inputTerminalsBase);

        inputTerminalsBase.setOnMouseClicked(addNewInputTerminal(canvas));

        for(var terminal : inputTerminals)
            canvas.add(terminal.getDrawable());

        // OUTPUT terminals
        outputTerminalsBase.setOnMouseClicked(addNewOutputTerminal(canvas));
        for(var terminal : outputTerminals)
            canvas.add(terminal.getDrawable());


        scene.setFill(Colors.backgroundColor);
        mainStage.setScene(scene);
        mainStage.show();
    }

    @NotNull
    private EventHandler<MouseEvent> addNewOutputTerminal(Canvas canvas) {
        return e -> {
            outputTerminals.add(new Terminal(e.getSceneY() - TerminalConstants.height / 2, false, true));

            for (var terminal : outputTerminals) {
                canvas.getDrawable().getChildren().removeIf(terminal.getDrawable()::equals);
            }
            for (var terminal : outputTerminals)
                canvas.add(terminal.getDrawable());
        };
    }

    @NotNull
    private EventHandler<MouseEvent> addNewInputTerminal(Canvas canvas) {
        return e -> {
            inputTerminals.add(new Terminal(e.getSceneY() - TerminalConstants.height / 2, true, true));

            for (var terminal : inputTerminals) {
                canvas.getDrawable().getChildren().removeIf(terminal.getDrawable()::equals);
            }
            for (var terminal : inputTerminals)
                canvas.add(terminal.getDrawable());
        };
    }

    private void displayTestTerminal(Canvas canvas, @NotNull Rectangle leftBorder) {
        Terminal inputDisplayTerminal = new Terminal(0, true, false);
        inputDisplayTerminal.getDrawable().setOnMouseEntered(Event::consume);
        inputDisplayTerminal.getDrawable().setOnMouseExited(Event::consume);
        inputDisplayTerminal.getDrawable().setOnMouseMoved(Event::consume);

        leftBorder.setOnMouseEntered(e -> {
            inputDisplayTerminal.setY(e.getSceneY() - inputDisplayTerminal.getDrawable().getPrefHeight() / 2);
            canvas.add(inputDisplayTerminal.getDrawable());
        });
        leftBorder.setOnMouseMoved(
            e -> inputDisplayTerminal.setY(e.getSceneY() - inputDisplayTerminal.getDrawable().getPrefHeight() / 2)
        );
        leftBorder.setOnMouseExited(
            e -> canvas.getDrawable().getChildren().removeIf(inputDisplayTerminal.getDrawable()::equals)
        );
    }

    public Rectangle makeRectangle(double x, double y, double width, double height, Color color) {
        Rectangle rect = new Rectangle();
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setFill(color);
        return rect;
    }

    public static void main(String[] args) { launch(args); }
}