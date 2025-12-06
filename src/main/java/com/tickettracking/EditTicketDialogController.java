package com.tickettracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.tickettracking.Comment;
import com.tickettracking.Ticket;
import com.tickettracking.UserService;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for handling the Edit Ticket dialog.
 * Provides functionality to update ticket details, manage ticket comments,
 * and validate input fields in the dialog.
 */
public class EditTicketDialogController {
    @FXML private TextField titleField;
    @FXML private ComboBox<Ticket.Status> statusComboBox;
    @FXML private ComboBox<Ticket.Priority> priorityComboBox;
    @FXML private ComboBox<String> assignedToComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private ListView<String> commentsListView;
    @FXML private TextField newCommentField;

    private Ticket ticket;
    private UserService userService;

    @FXML
    public void initialize() {
        // Initialize combo boxes with enum values
        statusComboBox.getItems().setAll(Ticket.Status.values());
        priorityComboBox.getItems().setAll(Ticket.Priority.values());
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
        if (userService != null && assignedToComboBox != null) {
            assignedToComboBox.getItems().clear();
            assignedToComboBox.getItems().add(null); // "Unassigned" option
            assignedToComboBox.getItems().addAll(userService.getUsernames());
        }
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;

        // Populate fields with ticket data
        if (ticket != null) {
            titleField.setText(ticket.getTitle());
            statusComboBox.setValue(ticket.getStatus());
            priorityComboBox.setValue(ticket.getPriority());
            assignedToComboBox.setValue(ticket.getAssignedTo());
            descriptionArea.setText(ticket.getDescription());
            loadComments();
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        if (ticket != null) {
            ticket.setTitle(titleField.getText().trim());
            ticket.setStatus(statusComboBox.getValue());
            ticket.setPriority(priorityComboBox.getValue());
            ticket.setAssignedTo(assignedToComboBox.getValue());
            ticket.setDescription(descriptionArea.getText().trim());
        }
        closeDialog();
    }
    
    private boolean validateInput() {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Title is required", "Please enter a title for the ticket.");
            return false;
        }
        
        if (statusComboBox.getValue() == null) {
            showAlert("Validation Error", "Status is required", "Please select a status for the ticket.");
            return false;
        }
        
        if (priorityComboBox.getValue() == null) {
            showAlert("Validation Error", "Priority is required", "Please select a priority for the ticket.");
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleAddComment() {
        String commentText = newCommentField.getText().trim();
        if (!commentText.isEmpty() && ticket != null && userService != null) {
            String author = userService.getCurrentUser() != null ? 
                userService.getCurrentUser().getUsername() : "Unknown";
            Comment comment = new Comment(commentText, author);
            ticket.addComment(comment);
            newCommentField.clear();
            loadComments();
        }
    }
    
    private void loadComments() {
        if (ticket != null && commentsListView != null) {
            ObservableList<String> commentStrings = FXCollections.observableArrayList();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Comment comment : ticket.getComments()) {
                String commentDisplay = String.format("[%s] %s: %s", 
                    comment.getCreatedAt().format(formatter),
                    comment.getCreatedBy(),
                    comment.getContent());
                commentStrings.add(commentDisplay);
            }
            
            commentsListView.setItems(commentStrings);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        titleField.getScene().getWindow().hide();
    }
}
