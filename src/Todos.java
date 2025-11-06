import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Todos extends VBox {
    private List<String> todos = new ArrayList<>();
    private ListView<String> listView = new ListView<>();

    public Todos() {


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
        inputField.setPromptText("Geben Sie Todos-item hier ein...");

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
        // Toggle the button
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

        // Assign layout to this extended VBox
        getChildren().add(layout);

    }

    private void addTodo(String task) {
        if (task.isEmpty()) return;
        todos.add(task);
        updateListView();
    }

    private void editSelectedTodo() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            Helper.showAlert("Keine Aufgabe ausgewählt", "Bitte wählen Sie eine Aufgabe zum Bearbeiten aus.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(todos.get(selectedIndex));
        dialog.setTitle("Aufgabe bearbeiten");
        dialog.setHeaderText("Bearbeiten Sie Ihre ausgewählte Aufgabe");
        dialog.setContentText("Aufgabe:");

        dialog.showAndWait().ifPresent(newTask -> {
            todos.set(selectedIndex, newTask);
            updateListView();
        });
    }

    private void deleteSelectedTodo() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            Helper.showAlert("Keine Aufgabe ausgewählt", "Bitte wählen Sie eine Aufgabe zum Löschen aus.");
            return;
        }

        todos.remove(selectedIndex);
        updateListView();
    }

    private void markTodoDone(Button markDoneButton) {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            Helper.showAlert("Keine Aufgabe ausgewählt", "Bitte wählen Sie eine Aufgabe zum Markieren aus.");
            return;
        }

        String item = todos.get(selectedIndex);
        if (!item.startsWith("✔ ")) {
            todos.set(selectedIndex, "✔ " + item);
            markDoneButton.setText("Markierung entfernen");
            markDoneButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        } else {
            todos.set(selectedIndex, item.substring(2));
            markDoneButton.setText("Als erledigt markieren");
            markDoneButton.setStyle("-fx-background-color: #2e85e2ff; -fx-text-fill: white;");
        }

        updateListView();
    }

    // Sort, Update and Save ListView
    private void updateListView() {
        List<String> sortedTodos = new ArrayList<>();

        // Not done first
        for (String todo : todos) {
            if (!todo.startsWith("✔ ")) {
                sortedTodos.add(todo);
            };
        }
        // Done last
        for (String todo : todos) {
            if (todo.startsWith("✔ ")) {
                sortedTodos.add(todo);
            };
        }

        listView.getItems().setAll(sortedTodos);
        todos = new ArrayList<>(sortedTodos); // keep todos in same order
        saveTodosToFile();
    }

     // Save todos to file
    private void saveTodosToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("todos.txt"))) {
            for (String todo : todos) {
                writer.write(todo);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load todos from file
    private void loadTodosFromFile() {
        todos.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("todos.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                todos.add(line);
            }
        } catch (IOException e) {
            // file might not exist on first run
        }
        updateListView();
    }

}
