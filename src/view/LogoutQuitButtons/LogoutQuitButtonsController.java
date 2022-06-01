package view.LogoutQuitButtons;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.LoginScreen.LoginScreenController;

/**
 * Class that is used to control logout and quit buttons that are used by other FXML documents for nested logout and quit buttons
 */
public class LogoutQuitButtonsController {
    @FXML
    Button logoutButton;
    @FXML
    Button quitButton;

    /**
     * Initializes LogoutQuitButtonController to be displayed
     * 
     * @param stage Stage on which GUI will be displayed
     * @throws FileNotFoundException If file can't be found
     */
    public void start(Stage stage) throws FileNotFoundException {
        // System.out.println("Displaying Logout/Quit Buttons");
    }

    /**
     * Called by logout button
     * <p>
     * Logs current user out and returns to login screen
     * 
     * @param e ActionEvent (used by logout Button)
     * @throws IOException If file can't be found
     */
    public void logout(ActionEvent e) throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../LoginScreen/LoginScreen.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        LoginScreenController controller = loader.getController();
        controller.start(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called by quit button
     * <p>
     * Quits application
     * 
     * @param e ActionEvent (used by quit Button)
     * @throws IOException If file can't be found
     */
    public void quit(ActionEvent e) throws IOException {
        Platform.exit();
    }

}
