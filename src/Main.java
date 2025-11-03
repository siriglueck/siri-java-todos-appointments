//package hellofx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main extends Application {

    // ===== TODO =====
    private String[] todos = new String[100];
    private int count = 0;
    private ListView<String> listView = new ListView<>();

    // ===== Termin =====
    private List<Termin> termineList = new ArrayList<>();
    private ListView<HBox> termineListView = new ListView<>();

    @Override
    public void start(Stage stage) {

         // === Tab 1: Basic To-Do List ===
        VBox todoTabContent = createTodoTab(); 
        Tab todoTab = new Tab("Todos", todoTabContent);
        todoTab.setClosable(false);

        // === Tab 2: Termins ===
        VBox terminTabContent = createTerminenTab(); 
        Tab terminTab = new Tab("Termin", terminTabContent);
        terminTab.setClosable(false);

        TabPane tabPane = new TabPane(todoTab, terminTab);

        stage.setScene(new Scene(tabPane, 400, 600));
        stage.setTitle("Siri - Todos & Termin");
        stage.show();
    }


    // =======================
    // === UTILITIES ========
    // =======================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Simple Termin model class
    static class Termin {
        String date, time, place, contact, category, topic;

        Termin(String date, String time, String place, String contact, String category, String topic) {
            this.date = date;
            this.time = time;
            this.place = place;
            this.contact = contact;
            this.category = category;
            this.topic = topic;
        }

        @Override
        public String toString() {
            return String.format("%s %s  |  %s  |  %s  |  %s", date, time, place, contact, topic);
        }
    }


    // =======================
    // === TODO LIST TAB ====
    // =======================

    private VBox createTodoTab() {

        // ใช้ cell factory เพื่อใส่สีและขีดฆ่าข้อความที่ทำเสร็จ
        listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("✔ ")) {
                        setStyle("-fx-text-fill: gray; -fx-strikethrough: true;");
                    } else {
                        setStyle("-fx-text-fill: black; -fx-strikethrough: false;");
                    }
                }
            }
        });

        TextField inputField = new TextField();
        inputField.setPromptText("Geben Todos-item hier ein...");

        // Buttons
        Button addButton = new Button("hinzufügen");
        Button editButton = new Button("bearbeiten");
        Button deleteButton = new Button("löschen");
        Button markDoneButton = new Button("Als erledigt markieren");

        // Button actions
        addButton.setOnAction(e -> addTodo(inputField.getText()));
        editButton.setOnAction(e -> editSelectedTodo());
        deleteButton.setOnAction(e -> deleteSelectedTodo());
        markDoneButton.setOnAction(e -> markTodoDone(markDoneButton));

        markDoneButton.setStyle("-fx-background-color: #2e85e2ff; -fx-text-fill: white;");
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("✔ ")) {
                markDoneButton.setText("Markierung entfernen");
                markDoneButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;"); 
            } else {
                markDoneButton.setText("Als erledigt markieren");
                markDoneButton.setStyle("-fx-background-color: #2e85e2ff; -fx-text-fill: white;"); 
            }
        });

        // Layout
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, markDoneButton);
        VBox layout = new VBox(10, inputField, buttonBox, listView);
        layout.setStyle("-fx-padding: 10;");

        // Load saved todos.txt
        loadTodosFromFile();

        return layout;
    }

    private void addTodo(String task) {
        if (task.isEmpty()) return;
        if (count >= todos.length) {
            System.out.println("List is full!");
            return;
        }
        todos[count++] = task; // add to array
        updateListView();
        
    }

     private void editSelectedTodo() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Keine Aufgabe ausgewählt", "Bitte wählen Sie eine Aufgabe zum Bearbeiten aus.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(todos[selectedIndex]);
        dialog.setTitle("Aufgabe bearbeiten");
        dialog.setHeaderText("Bearbeiten Sie Ihre ausgewählte Aufgabe");
        dialog.setContentText("Aufgabe:");

        dialog.showAndWait().ifPresent(newTask -> {
            todos[selectedIndex] = newTask;
            updateListView();
        });
    }

    private void deleteSelectedTodo() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Keine Aufgabe ausgewählt", "Bitte wählen Sie eine Aufgabe zum Löschen aus.");
            return;
        }

        // Shift array elements left to remove item
        for (int i = selectedIndex; i < count - 1; i++) {
            todos[i] = todos[i + 1];
        }
        count--;
        updateListView();
    }

    private void markTodoDone(Button markDoneButton) {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Keine Aufgabe ausgewählt", "Bitte wählen Sie eine Aufgabe zum Markieren aus.");
            return;
        }

        String selectedItem = listView.getItems().get(selectedIndex);
        // หา index จริงใน todos[]
        int realIndex = -1;
        for (int i = 0; i < count; i++) {
            if (todos[i].equals(selectedItem)) {
                realIndex = i;
                break;
            }
        }
        if (realIndex == -1) return;

        if (!todos[realIndex].startsWith("✔ ")) {
            todos[realIndex] = "✔ " + todos[realIndex];
            markDoneButton.setText("Unmark");
            markDoneButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        } else {
            todos[realIndex] = todos[realIndex].substring(2);
            markDoneButton.setText("Mark Done");
            markDoneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }

        updateListView(); // เรียงใหม่และรีเฟรช
    }

   private void updateListView() {
        listView.getItems().clear();
            // เรียงรายการ (not done ก่อน done หลัง)
        List<String> sortedTodos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (!todos[i].startsWith("✔ ")) sortedTodos.add(todos[i]);
        }
        for (int i = 0; i < count; i++) {
            if (todos[i].startsWith("✔ ")) sortedTodos.add(todos[i]);
        }

        // อัปเดต array และ listView
        todos = sortedTodos.toArray(new String[0]);
        count = todos.length;
        listView.getItems().setAll(sortedTodos);

        
        saveTodosToFile();
    }

        private void saveTodosToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("todos.txt"))) {
            for (int i = 0; i < count; i++) {
                writer.write(todos[i]);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTodosFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("todos.txt"))) {
            String line;
            while ((line = reader.readLine()) != null && count < todos.length) {
                todos[count++] = line;
            }
            updateListView();
        } catch (IOException e) {
            // file might not exist first time
        }
    }

    // =======================
    // === TERMINEN TAB ======
    // =======================
    private VBox createTerminenTab() {

        TextField topicField = new TextField();
        topicField.setPromptText("z.B. Geburtstag feiern");

        TextField dateField = new TextField();
        dateField.setPromptText("z.B. 2025-11-03");

        TextField timeField = new TextField();
        timeField.setPromptText("z.B. 14:30");

        TextField placeField = new TextField();
        placeField.setPromptText("z.B. Hauptbahnhof");

        TextField contactField = new TextField();
        contactField.setPromptText("z.B. Max Mustermann");

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Familie", "Beruf", "Arzt", "Hobby", "Freunde");
        categoryBox.setPromptText("auswählen");

        Button addButton = new Button("Speichern");
        Button editButton = new Button("Bearbeiten");
        Button deleteButton = new Button("Löschen");

        addButton.setOnAction(e -> 
            addTermin(dateField.getText(), timeField.getText(), placeField.getText(),
                    contactField.getText(), categoryBox.getValue(), topicField.getText())
        );
        editButton.setOnAction(e -> editSelectedTermin());
        deleteButton.setOnAction(e -> deleteSelectedTermin());

        HBox controlBox = new HBox(10, addButton, editButton, deleteButton);

        // GridPane for input fields with left-aligned labels
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Labels and text fields with appropriate grid positioning
        grid.add(new Label("Titel:"), 0, 0);
        grid.add(topicField, 1, 0);

        grid.add(new Label("Datum (YYYY-MM-DD):"), 0, 1);
        grid.add(dateField, 1, 1);

        grid.add(new Label("Uhrzeit (HH:mm):"), 0, 2);
        grid.add(timeField, 1, 2);

        grid.add(new Label("Ort/Treffpunkt:"), 0, 3);
        grid.add(placeField, 1, 3);

        grid.add(new Label("Kontaktperson:"), 0, 4);
        grid.add(contactField, 1, 4);

        grid.add(new Label("Kategorie:"), 0, 5);
        grid.add(categoryBox, 1, 5);

        // Add the control buttons and list view
        VBox layout = new VBox(10, grid, controlBox, termineListView);
        layout.setPadding(new Insets(10));

        // Load saved appointments
        loadTermineFromFile();

        return layout;
    }

     private void addTermin(String date, String time, String place, String contact, String category, String topic) {
        if (topic.isEmpty() || date.isEmpty() || time.isEmpty() || place.isEmpty() || contact.isEmpty() || category == null) {
            showAlert("Fehlende Daten", "Bitte füllen Sie alle Felder aus und wählen Sie eine Kategorie.");
            return;
        }

        try {
            // Validate date and time
            LocalDate.parse(date);
            LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            showAlert("Ungültiges Datums-/Zeitformat", "Verwenden Sie JJJJ-MM-TT für das Datum und HH:mm für die Uhrzeit.");
            return;
        }

        Termin termin = new Termin(date, time, place, contact, category, topic);
        termineList.add(termin);

        sortTermine();
        updateTermineListView();
        saveTermineToFile();
    }

    private void editSelectedTermin() {
        int index = termineListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showAlert("Kein Eintrag ausgewählt", "Bitte wählen Sie einen Termin zur Bearbeitung aus.");
            return;
        }

        Termin selected = termineList.get(index);

        // Create custom dialog
        Dialog<Termin> dialog = new Dialog<>();
        dialog.setTitle("Termin bearbeiten");
        dialog.setHeaderText("Termindetails aktualisieren");

        // Buttons
        ButtonType saveButtonType = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create input fields pre-filled with existing data
        TextField topicField = new TextField(selected.topic);
        TextField dateField = new TextField(selected.date);
        TextField timeField = new TextField(selected.time);
        TextField placeField = new TextField(selected.place);
        TextField contactField = new TextField(selected.contact);

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Familie", "Beruf", "Arzt", "Hobby", "Freunde");
        categoryBox.setValue(selected.category);

        // Layout inside dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        grid.add(new Label("Titel:"), 0, 0);
        grid.add(topicField, 1, 0);
        grid.add(new Label("Datum (YYYY-MM-DD):"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Uhrzeit (HH:mm):"), 0, 2);
        grid.add(timeField, 1, 2);
        grid.add(new Label("Ort/Treffpunkt:"), 0, 3);
        grid.add(placeField, 1, 3);
        grid.add(new Label("Kontaktperson:"), 0, 4);
        grid.add(contactField, 1, 4);
        grid.add(new Label("Kategorie:"), 0, 5);
        grid.add(categoryBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Termin(
                    dateField.getText(),
                    timeField.getText(),
                    placeField.getText(),
                    contactField.getText(),
                    categoryBox.getValue(),
                    topicField.getText()
                );
            }
            return null;
        });

        // Show dialog and get result
        Optional<Termin> result = dialog.showAndWait();

        result.ifPresent(updated -> {
            // Validate date/time formats
            try {
                LocalDate.parse(updated.date);
                LocalTime.parse(updated.time);
            } catch (DateTimeParseException e) {
                showAlert("Ungültiges Datums-/Zeitformat", "Verwenden Sie JJJJ-MM-TT für das Datum und HH:mm für die Uhrzeit.");
                return;
            }

            // Replace the old Termin
            termineList.set(index, updated);
            sortTermine();
            updateTermineListView();
            saveTermineToFile();
        });
    }

    private void deleteSelectedTermin() {
        int index = termineListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showAlert("Kein Eintrag ausgewählt", "Bitte wählen Sie einen Termin zum Löschen aus.");
            return;
        }
        termineList.remove(index);
        updateTermineListView();
        saveTermineToFile();
    }

    private void updateTermineListView() {
        termineListView.getItems().clear();
        for (Termin t : termineList) {
            termineListView.getItems().add(createTerminDisplay(t));
        }
    }

    private void sortTermine() {
        termineList.sort(Comparator.comparing((Termin t) -> LocalDate.parse(t.date))
                                   .thenComparing(t -> LocalTime.parse(t.time)));
    }

    private void saveTermineToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("termine.txt"))) {
            for (Termin t : termineList) {
                writer.write(String.join("|", t.date, t.time, t.place, t.contact, t.category, t.topic));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTermineFromFile() {
        termineList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("termine.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    termineList.add(new Termin(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            // ignore if file not found
        }
        sortTermine();
        updateTermineListView();
    }

    private HBox createTerminDisplay(Termin t) {
        Label topicLabel = new Label(t.topic);
        topicLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label info = new Label(String.format("%s %s | %s | %s", t.date, t.time, t.place, t.contact));
        info.setStyle("-fx-font-size: 12px;");

        Label categoryLabel = new Label(t.category);
        categoryLabel.setPadding(new Insets(3, 8, 3, 8));
        categoryLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        categoryLabel.setStyle("-fx-background-radius: 5;" + getCategoryColor(t.category));

        VBox detailsBox = new VBox(2, topicLabel, info);
        HBox box = new HBox(10, detailsBox, categoryLabel);
        box.setPadding(new Insets(5));
        return box;
    }

    private String getCategoryColor(String category) {
        switch (category) {
            case "Familie": return "-fx-background-color: #1E90FF;"; // blue
            case "Beruf":   return "-fx-background-color: #228B22;"; // green
            case "Arzt":    return "-fx-background-color: #B22222;"; // red
            case "Hobby":   return "-fx-background-color: #DAA520;"; // gold
            case "Freunde":   return "-fx-background-color: #ec30a4ff;"; // pink
            default:        return "-fx-background-color: gray;";
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
