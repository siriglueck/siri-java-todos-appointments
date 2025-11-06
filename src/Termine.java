import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

public class Termine extends VBox {

    private List<Termin> termineList = new ArrayList<>();
    private ListView<HBox> termineListView = new ListView<>();
    private TerminForm terminForm;
    
    // Appoitment(s) = Termin (sg.) , Termine (Plural)

    public Termine() {
        
        terminForm = new TerminForm();
        Button addButton = new Button("Speichern");
        Button editButton = new Button("Bearbeiten");
        Button deleteButton = new Button("Löschen");

        // Add actions
        addButton.setOnAction(e -> 
            addTermin(
                terminForm.getDate(), 
                terminForm.getTime(), 
                terminForm.getPlace(),
                terminForm.getContact(), 
                terminForm.getCategory(), 
                terminForm.getTopic()
            )
        );
        editButton.setOnAction(e -> editSelectedTermin());
        deleteButton.setOnAction(e -> deleteSelectedTermin());

        HBox controlBox = new HBox(10, addButton, editButton, deleteButton);

        // Add the control buttons and list view
        VBox layout = new VBox(10, terminForm, controlBox, termineListView);
        layout.setPadding(new Insets(10));

        // Load saved appointments
        loadTermineFromFile();

        // Assign layout to this extended VBox class 
        getChildren().add(layout);
    }

    
    private void addTermin(String date, String time, String place, String contact, String category, String topic) {
        if (topic.isEmpty() || date.isEmpty() || time.isEmpty() || place.isEmpty() || contact.isEmpty() || category == null) {
            Helper.showAlert("Fehlende Daten", "Bitte füllen Sie alle Felder aus und wählen Sie eine Kategorie.");
            return;
        }

        try {
            // Validate date and time
            LocalDate.parse(date);
            LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            Helper.showAlert("Ungültiges Datums-/Zeitformat", "Verwenden Sie JJJJ-MM-TT für das Datum und HH:mm für die Uhrzeit.");
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
            Helper.showAlert("Kein Eintrag ausgewählt", "Bitte wählen Sie einen Termin zur Bearbeitung aus.");
            return;
        }

        Termin selected = termineList.get(index);

        // Create custom dialog - Pop up windows for editing the list
        Dialog<Termin> dialog = new Dialog<>();
        dialog.setTitle("Termin bearbeiten");
        dialog.setHeaderText("Termindetails aktualisieren");

        // Buttons
        ButtonType saveButtonType = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
         
        TerminForm editForm = new TerminForm();
        
        dialog.getDialogPane().setContent(editForm);
        
        // Create input fields pre-filled with existing data
        editForm.setDate(selected.date);
        editForm.setTime(selected.time);
        editForm.setPlace(selected.place);
        editForm.setContact(selected.contact);
        editForm.setCategory(selected.category);
        editForm.setTopic(selected.topic);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Termin(
                    editForm.getDate(),
                    editForm.getTime(),
                    editForm.getPlace(),
                    editForm.getContact(),
                    editForm.getCategory(),
                    editForm.getTopic()
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
                Helper.showAlert("Ungültiges Datums-/Zeitformat", "Verwenden Sie JJJJ-MM-TT für das Datum und HH:mm für die Uhrzeit.");
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
            Helper.showAlert("Kein Eintrag ausgewählt", "Bitte wählen Sie einen Termin zum Löschen aus.");
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

        Label info = new Label(String.format("%s | %s | %s | %s", t.date, t.time, t.place, t.contact));
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

    
}
