package com.tickettracking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Represents the main application class for the Ticket Tracking System.
 * This class extends the JavaFX Application class and serves as the entry point
 * for initializing and launching the application.
 *
 * The application uses an `FXMLLoader` to load the main view from an FXML resource,
 * sets up the scene and stage for the graphical user interface, and initializes
 * necessary dependencies for the application's controllers.
 *
 * Features include:
 * - Initialization and setup of the graphical user interface using JavaFX.
 * - Use of a `TicketService` to manage ticket data and handle actions like
 *   retrieval, creation, updating, and deletion of tickets.
 * - Dynamic controller injection for better scalability and testability.
 */
public class TicketApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Create the TicketService
        TicketService ticketService = new TicketService();

        // Create the FXMLLoader
        FXMLLoader fxmlLoader = new FXMLLoader(TicketApplication.class.getResource("/views/main-view.fxml"));

        // Set up the controller factory
        fxmlLoader.setControllerFactory(param -> {
            if (param == MainViewController.class) {
                MainViewController controller = new MainViewController();
                controller.setTicketService(ticketService);
                return controller;
            }
            return null;
        });

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Ticket Tracking System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


