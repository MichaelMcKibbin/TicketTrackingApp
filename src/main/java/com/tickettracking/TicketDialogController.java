package com.tickettracking;


import com.tickettracking.Ticket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller class for managing the ticket dialog UI interaction.
 * This class handles displaying, updating, and saving ticket details through a dialog interface.
 */
public class TicketDialogController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<Ticket.Priority> priorityComboBox;  // Changed to Ticket.Priority
    @FXML
    private Button saveTicketButton;

    private Ticket ticket;
    private boolean saveClicked = false;  // Track if save was clicked
    private boolean hasUnsavedChanges = false;


//    @FXML
//    public void initialize() {
//        // Initialize the priority combo box
//        priorityComboBox.getItems().addAll(Ticket.Priority.values());
//
//        // Add listeners to update the ticket object when fields change
//        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (ticket != null) {
//                ticket.setTitle(newValue);
//            }
//        });
//
//        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (ticket != null) {
//                ticket.setDescription(newValue);
//            }
//        });
//
//        priorityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
//            if (ticket != null) {
//                ticket.setPriority(newValue);
//            }
//        });
//    }

    @FXML
    public void initialize() {
        priorityComboBox.setItems(FXCollections.observableArrayList(Ticket.Priority.values()));

        // Add listeners to track changes
        titleField.textProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);
        descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);
        priorityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);

        // Add close request handler
        Platform.runLater(() -> {
            Stage stage = (Stage) saveTicketButton.getScene().getWindow();
            stage.setOnCloseRequest(this::handleCloseRequest);
        });
    }

    private void handleCloseRequest(WindowEvent event) {
        if (hasUnsavedChanges) {
            event.consume(); // Prevent the window from closing immediately

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes");
            alert.setContentText("Would you like to save before closing?");

            ButtonType saveButton = new ButtonType("Save");
            ButtonType discardButton = new ButtonType("Discard");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    // Save the ticket
                    if (isInputValid()) {
                        handleSaveTicket();
                        closeWindow();
                    }
                } else if (result.get() == discardButton) {
                    // Close without saving
                    closeWindow();
                }
                // If cancel, do nothing and keep window open
            }
        } else {
            closeWindow();
        }
    }

    @FXML
    private void handleSaveTicket() {
        if (isInputValid()) {
            // Update ticket with current values
            ticket.setTitle(titleField.getText());
            ticket.setDescription(descriptionArea.getText());
            ticket.setPriority(priorityComboBox.getValue());

            saveClicked = true;  // Mark as saved
            hasUnsavedChanges = false; // added...

            // Close the dialog
            Stage stage = (Stage) saveTicketButton.getScene().getWindow();
            stage.close();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) saveTicketButton.getScene().getWindow();
        stage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage += "Title is required!\n";
        }
        if (priorityComboBox.getValue() == null) {
            errorMessage += "Please select a priority!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }


    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        titleField.setText(ticket.getTitle());
        descriptionArea.setText(ticket.getDescription());
        priorityComboBox.setValue(ticket.getPriority());
        hasUnsavedChanges = false; // Reset change tracking after loading
    }

    public Ticket getTicket() {
        // Create and return a new ticket from the form data
        return new Ticket(
                titleField.getText(),
                descriptionArea.getText(),
                priorityComboBox.getValue(),
                LocalDateTime.now()
        );
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

}


