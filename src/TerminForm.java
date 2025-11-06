import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class TerminForm extends GridPane {
    // Variable for other classes to access these values
    private TextField topicField;
    private TextField dateField;
    private TextField timeField;
    private TextField placeField;
    private TextField contactField;
    private ComboBox<String> categoryBox;

    public TerminForm() {
        // Create the form and buttons in the main view
        this.topicField = new TextField();
        topicField.setPromptText("z.B. Geburtstag feiern");

        this.dateField = new TextField();
        dateField.setPromptText("z.B. 2025-11-03");

        this.timeField = new TextField();
        timeField.setPromptText("z.B. 14:30");

        this.placeField = new TextField();
        placeField.setPromptText("z.B. Hauptbahnhof");

        this.contactField = new TextField();
        contactField.setPromptText("z.B. Max Mustermann");

        this.categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Familie", "Beruf", "Arzt", "Hobby", "Freunde");
        categoryBox.setPromptText("auswählen");

        // Style the form
        // GridPane for input fields with left-aligned labels
        // GridPane grid = new GridPane();
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10));

        // Labels and text fields with appropriate grid positioning
        this.add(new Label("Titel:"), 0, 0);
        this.add(topicField, 1, 0);

        this.add(new Label("Datum (YYYY-MM-DD):"), 0, 1);
        this.add(dateField, 1, 1);

        this.add(new Label("Uhrzeit (HH:mm):"), 0, 2);
        this.add(timeField, 1, 2);

        this.add(new Label("Ort/Treffpunkt:"), 0, 3);
        this.add(placeField, 1, 3);

        this.add(new Label("Kontaktperson:"), 0, 4);
        this.add(contactField, 1, 4);

        this.add(new Label("Kategorie:"), 0, 5);
        this.add(categoryBox, 1, 5);

        
    }

    // --- GETTERS --- 
    public String getTopic() {
        return topicField.getText();
    }

    public String getDate() {
        return dateField.getText();
    }

    public String getTime() {
        return timeField.getText();
    }

    public String getPlace() {
        return placeField.getText();
    }

    public String getContact() {
        return contactField.getText();
    }

    public String getCategory() {
        return categoryBox.getValue();
    }

    // --- SETTERS ---
    public void setTopic(String topic) {
        topicField.setText(topic);
    }

    public void setDate(String date) {
        dateField.setText(date);
    }

    public void setTime(String time) {
        timeField.setText(time);
    }

    public void setPlace(String place) {
        placeField.setText(place);
    }

    public void setContact(String contact) {
        contactField.setText(contact);
    }

    public void setCategory(String category) {
        categoryBox.setValue(category);
    }

}
    

