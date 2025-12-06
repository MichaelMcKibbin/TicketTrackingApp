# ️TicketTracking

A desktop **Trouble Ticket Tracking System** built in **Java 17** and **JavaFX**, with JSON persistence, ticket filtering, comment history, visual priority indicators, and a clean UI.  
This project is ideal for learning JavaFX, MVC patterns, and desktop data management.

---

## Features

### Ticket Management
Create, view, edit, and delete support tickets.

Fields include:

- **Title**
- **Description**
- **Status** (NEW, IN_PROGRESS, ON_HOLD, RESOLVED, CLOSED)
- **Priority** (LOW, MEDIUM, HIGH, CRITICAL)
- **Assigned To**
- **Created/Updated timestamps**

---

### Comment History
- Add comments to any ticket.
- Comments include author, timestamp, and content.
- Comments are visible in the edit dialog.

---

### Search & Filtering
Filter tickets by **Status** and **Priority**.

Live text search supports:

- Ticket ID
- Title
- Status
- Priority
- Assigned To
- Description

---

### JSON Persistence
- Tickets stored in a readable `tickets.json`
- Auto-serializes timestamps using **Jackson JavaTimeModule**
- Automatic ID generation for new tickets
- File → Save / Load actions
- “All changes saved” indicator in the status bar

---

### UI Features
- Color-coded priority column (low=green → high=red)
- Double-click a ticket to open the Edit dialog
- Clean, responsive JavaFX UI
- Separate dialogs for:
    - **New Ticket**
    - **Edit Ticket**

---

## Tech Stack

| Component          | Technology                          |
|--------------------|-------------------------------------|
| Language           | Java 21                             |
| UI Toolkit         | JavaFX (FXML + Controllers)         |
| JSON Serialization | Jackson Databind + JavaTimeModule   |
| Architecture       | MVC-style separation                |
| Packaging          | Modularized with `module-info.java` |

---

## Project Structure
```
TicketTracking/
└── src/
├── main/java/com/tickettracking/
│ ├── TicketApplication.java
│ ├── MainViewController.java
│ ├── TicketDialogController.java
│ ├── EditTicketDialogController.java
│ ├── ticket model classes...
│ ├── TicketService.java
│ └── UserService.java
├── main/resources/
│ ├── views/*.fxml
│ ├── styles.css
│ └── sample_tickets.json
└── module-info.java
```

---

## Getting Started

### Prerequisites
- **Java 17+**
- **JavaFX SDK**
- An IDE like **IntelliJ IDEA**

---

### Clone the Repository

```
git clone https://github.com/MichaelMcKibbin/TicketTracking.git
cd TicketTracking
```
Run the Application in IntelliJ
Open the project (File → Open)

Set Project SDK to Java 17 or higher

Add JavaFX libraries to the module path:

Project Structure → Libraries → Add JavaFX (lib folder)

Ensure your module-info.java includes:

```
module com.tickettracking {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.logging;

    opens com.tickettracking to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.tickettracking;
}
```
Run TicketApplication.java

### Data Format (tickets.json)
Example ticket object:

```
{
  "id": "12",
  "title": "Login page not loading",
  "description": "User reports blank page on login.",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "assignedTo": "Michael",
  "createdAt": "2025-06-06T21:29:14.9359622",
  "updatedAt": null,
  "comments": [
    {
      "id": "c34",
      "ticketId": "12",
      "content": "Requested browser logs.",
      "createdBy": "Sarah",
      "createdAt": "2025-06-06T21:45:55.102334"
    }
  ]
}
```
The repository also includes ```sample_tickets.json```as seed data.

## Roadmap / Future Enhancements
Below are some potential/planned feature enhancements:

**Authentication System**
Login screen (using existing UserService)

Permissions by role: Admin, Support, Viewer

**Dashboard & Metrics**
Ticket counts by status

**Priority heatmap**

“Oldest unresolved tickets” panel

**Tags / Categorization**
Add tags like "frontend", "backend", "billing"

Multi-tag filtering

**Settings Panel**
Change JSON storage location

Auto-save toggle

Custom themes

**Import/Export**
Export tickets to CSV

Import CSV → Tickets

Backup & restore JSON

**UI Improvements**
Light/Dark mode

Icons for status & priority

Collapsible sidebar navigation

## Testing Suite
### Automated Tests (Unit Tests)
- `TicketService` – ticket persistence, validation, and ID generation
- `Ticket` – comment handling and default values
- `UserService` – default users, roles, and current user handling

[Testing.md](Testing.md) describes the testing strategy for the **TicketTracking** application.  
[GUI-manual-testing-checklist.md](GUI-manual-testing-checklist.md) provides a structured checklist for manual GUI testing.
 
### FXML controller tests (TestFX). 
- Automated tests are unlikely to be added due to the complexity of the task, and the aging test suite - TestFX is outdated.
- New JDK restrictions make automation of UI tests difficult.
- It is recommended to use manual testing for UI interactions. 
- Manual (human) tests are faster and more like