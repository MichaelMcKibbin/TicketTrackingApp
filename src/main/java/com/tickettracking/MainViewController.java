package com.tickettracking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tickettracking.EditTicketDialogController;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * MainViewController acts as the controller for the main application view, managing the
 * display and interaction with a list of tickets, along with filtering and editing capabilities.
 *
 * This class is integrated with JavaFX components to handle user interactions, such as searching,
 * filtering, and editing tickets in a TableView. It communicates with the TicketService and
 * UserService to fetch and update ticket data, and leverages FXML-defined UI components.
 */

public class MainViewController {

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<Ticket.Status> statusFilter;
    @FXML private ComboBox<Ticket.Priority> priorityFilter;
    @FXML public Label saveIndicatorLabel;
    @FXML private TableColumn<Ticket, String> idColumn;
    @FXML private TableColumn<Ticket, String> titleColumn;
    @FXML private TableColumn<Ticket, String> statusColumn;
    @FXML private TableColumn<Ticket, Ticket.Priority> priorityColumn;
    @FXML private TableColumn<Ticket, String> assignedToColumn;
    @FXML private TableColumn<Ticket, LocalDateTime> createdAtColumn;
    @FXML public TableColumn<Ticket, String> descriptionColumn;

    private TicketService ticketService;
    private UserService userService;
    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();

    // no-args constructor
    public MainViewController() {
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    public MainViewController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @FXML
    public void initialize() {
        if (ticketService == null) {
            System.err.println("Warning: TicketService not initialized during initialization");
            return;
        }
        
        // Initialize UserService if not set
        if (userService == null) {
            userService = new UserService();
        }
        
        // Initialize table columns
        setupTableColumns();

        // Setup filters
        setupFilters();

        // Load initial data
        loadTickets();
    }

    private void setupTableColumns() {
        // Configure table columns with cell value factories
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        assignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedTo"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Format the date/time column
        createdAtColumn.setCellFactory(column -> new TableCell<Ticket, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Format the priority column with colors
        priorityColumn.setCellFactory(column -> new TableCell<Ticket, Ticket.Priority>() {
            private Timeline flashTimeline;
            
            @Override
            protected void updateItem(Ticket.Priority item, boolean empty) {
                super.updateItem(item, empty);
                
                // Stop any existing animation
                if (flashTimeline != null) {
                    flashTimeline.stop();
                }
                
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    switch (item) {
                        case CRITICAL:
                            // Create flashing animation for CRITICAL
                            flashTimeline = new Timeline(
                                new KeyFrame(Duration.seconds(0.5), e -> setStyle("-fx-text-fill: purple; -fx-font-weight: bold;")),
                                new KeyFrame(Duration.seconds(1.0), e -> setStyle("-fx-text-fill: transparent;"))
                            );
                            flashTimeline.setCycleCount(Timeline.INDEFINITE);
                            flashTimeline.play();
                            break;
                        case HIGH:
                            setStyle("-fx-text-fill: red;");
                            break;
                        case MEDIUM:
                            setStyle("-fx-text-fill: orange;");
                            break;
                        case LOW:
                            setStyle("-fx-text-fill: green;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Set the items to the TableView
        ticketTable.setItems(tickets);

        // Add row double-click handler
        ticketTable.setRowFactory(tv -> {
            TableRow<Ticket> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editTicket(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupFilters() {
        // Initialize filter ComboBoxes
        statusFilter.getItems().add(null); // "All" option
        statusFilter.getItems().addAll(Ticket.Status.values());
        
        priorityFilter.getItems().add(null); // "All" option
        priorityFilter.getItems().addAll(Ticket.Priority.values());
        
        // Add listeners to search field and combo boxes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTickets());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterTickets());
        priorityFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterTickets());
    }

private void editTicket(Ticket ticket) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/edit-ticket-dialog.fxml"));
        Parent root = loader.load();

        EditTicketDialogController controller = (EditTicketDialogController) loader.getController();
        if (controller != null) {
            controller.setTicket(ticket);
            controller.setUserService(userService);

            Stage stage = new Stage();
            stage.setTitle("Edit Ticket");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            // Save the edited ticket if it has a title
            if (ticket.getTitle() != null && !ticket.getTitle().isEmpty()) {
                ticketService.updateTicket(ticket);
            }
            
            // Refresh tickets after editing
            loadTickets();
        } else {
            throw new RuntimeException("Controller not initialized");
        }

    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Could not open edit dialog", e.getMessage());
    }
}



@FXML
public void loadTickets() {
    try {
        // Clear existing items
        tickets.clear();  // using tickets instead of ticketList

        // Get tickets from service
        List<Ticket> loadedTickets = ticketService.getAllTickets();

        // Add to observable list
        tickets.addAll(loadedTickets);

        // Debug print
        System.out.println("Loaded " + loadedTickets.size() + " tickets");
        loadedTickets.forEach(ticket -> System.out.println("Ticket: " + ticket.getTitle()));

        // Refresh the table view
        ticketTable.refresh();

    } catch (Exception e) {
        System.err.println("Error loading tickets: " + e.getMessage());
        e.printStackTrace();
    }
}


private void filterTickets() {
    String searchText = searchField.getText().toLowerCase().trim();
    Ticket.Status selectedStatus = statusFilter.getValue();
    Ticket.Priority selectedPriority = priorityFilter.getValue();
    
    ObservableList<Ticket> filteredList = tickets.filtered(ticket -> {
        // Text search filter
        boolean matchesText = searchText.isEmpty() ||
                (ticket.getTitle() != null && ticket.getTitle().toLowerCase().contains(searchText)) ||
                (ticket.getId() != null && ticket.getId().toLowerCase().contains(searchText)) ||
                (ticket.getStatus() != null && ticket.getStatus().toString().toLowerCase().contains(searchText)) ||
                (ticket.getAssignedTo() != null && ticket.getAssignedTo().toLowerCase().contains(searchText));
        
        // Status filter
        boolean matchesStatus = selectedStatus == null || ticket.getStatus() == selectedStatus;
        
        // Priority filter
        boolean matchesPriority = selectedPriority == null || ticket.getPriority() == selectedPriority;
        
        return matchesText && matchesStatus && matchesPriority;
    });
    
    ticketTable.setItems(filteredList);
}
    // Method that supports different alert types
    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Overloaded showAlert method for error alerts (backwards compatibility)
    private void showAlert(String title, String header, String content) {
        showAlert(title, header, content, Alert.AlertType.ERROR);
    }



    @FXML
    public void createNewTicket(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/edit-ticket-dialog.fxml"));
            Parent root = loader.load();

            EditTicketDialogController controller = (EditTicketDialogController) loader.getController();

            // Create a new ticket with default values
            Ticket newTicket = new Ticket();
            newTicket.setStatus(Ticket.Status.OPEN);  // Set default status
            newTicket.setPriority(Ticket.Priority.MEDIUM);  // Set default priority

            controller.setTicket(newTicket);
            controller.setUserService(userService);

            Stage stage = new Stage();
            stage.setTitle("Create New Ticket");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            // If the ticket was saved (has a title), add it to the service
            if (newTicket.getTitle() != null && !newTicket.getTitle().isEmpty()) {
                ticketService.saveTicket(newTicket);
                loadTickets(); // Refresh the table
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not create new ticket", e.getMessage());
        }
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        try {
            // The tickets are already saved automatically by TicketService
            // This method can be used for manual save confirmation
            showAlert("Success", "Save Successful", "All tickets have been saved successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not save tickets", e.getMessage());
        }
    }

    @FXML
    public void exitApplication(ActionEvent actionEvent) {
        // Save before exit
        try {
            save(null);

            // Get the stage and close it
            Stage stage = (Stage) ticketTable.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            // If save fails, ask user if they still want to exit
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Failed");
            alert.setHeaderText("Unable to save tickets");
            alert.setContentText("Do you want to exit anyway?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Stage stage = (Stage) ticketTable.getScene().getWindow();
                    stage.close();
                }
            });
        }
    }
}
