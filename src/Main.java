import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) {

         // === Tab 1: Todos ===
        Todos todos = new Todos();
        VBox todoTabContent = todos; 
        Tab todoTab = new Tab("Todos", todoTabContent);
        todoTab.setClosable(false);

        // === Tab 2: Termine ===
        Termine termine = new Termine();
        VBox terminTabContent = termine; 
        Tab terminTab = new Tab("Termin", terminTabContent);
        terminTab.setClosable(false);

        TabPane tabPane = new TabPane(todoTab, terminTab);

        stage.setScene(new Scene(tabPane, 400, 600));
        stage.setTitle("Siri - Todos & Termine");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
