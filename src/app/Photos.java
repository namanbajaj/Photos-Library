package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.LoginScreen.LoginScreenController;

/**
 * Main Class that is run to start application
 */
public class Photos extends Application {

    /**
     * Initializes GUI to be displayed
     * 
     * @param stage Main stage
     * @throws IOException If file can't be found
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/LoginScreen/LoginScreen.fxml"));

        AnchorPane root = (AnchorPane) loader.load();

        LoginScreenController controller = loader.getController();
        controller.start(stage);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Main runner for Photos
     * 
     * @param args Command line arguments (Unused)
     */
    public static void main(String[] args) {
        launch();
    }

}