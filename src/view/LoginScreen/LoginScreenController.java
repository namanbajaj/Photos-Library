package view.LoginScreen;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import view.AdminControlPanel.AdminControlPanel;
import view.AlbumDisplay.AlbumDisplayController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

/**
 * Controller class for LoginScreenController, first GUI display that user sees when opening application
 */
public class LoginScreenController {
    @FXML
    TextField loginField;

    /**
     * User list loaded from .txt file
     */
    ArrayList<String> userList;
    
    /**
     * User list loaded from .dat files
     */
    ArrayList<User> users;
    
    /**
     * User that user logs into, used to be sent into other controller classes
     */
    User user;

    /**
     * Initializes LoginScreenController to be displayed
     * 
     * @param stage Stage on which GUI will be displayed
     * @throws FileNotFoundException If file can't be found
     */
    public void start(Stage stage) throws FileNotFoundException {
        userList = loadUsersfromTxt();
        users = loadUsersfromDat();
    }

    /**
     * Loads users from .txt file and returns list of those users
     * 
     * @return String list of users loaded from .txt file
     * @throws FileNotFoundException If file can't be found
     */
    private ArrayList<String> loadUsersfromTxt() throws FileNotFoundException {
        ArrayList<String> list = new ArrayList<String>();
        Scanner scanner = new Scanner(new File("src/data/userlist.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            list.add(line);
        }
        scanner.close();

        return list;
    }

    /**
     * Loads users from .dat files in src/data/users
     * 
     * @return User list of users loaded from .dat files
     */
    private ArrayList<User> loadUsersfromDat() {
        ArrayList<User> list = new ArrayList<User>();
        for (String line : userList) {
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(
                        new FileInputStream("src\\data\\users" + File.separator + line + ".dat"));
                User u = (User) ois.readObject();
                // System.out.println(u);
                list.add(u);
                // System.out.println(line);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * Gets username from login field
     * 
     * @param e ActionEvent (used to enter)
     * @throws IOException If file can't be found
     */
    public void getUsername(ActionEvent e) throws IOException {
        String userEntered = String.valueOf(loginField.getText());

        if (userEntered.equals("admin"))
            switchToAdminControlPanel(e);
        else if (userList.contains(userEntered)) {
            for (User u : users)
                if (u.name.equals(userEntered))
                    user = u;
            switchToAlbumDisplay(e);
        } else
            userNotPresentWarning();

    }

    /**
     * Displays Alert if user tries to login with name of user that doesn't exist
     */
    private void userNotPresentWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Missing User Warning");
        alert.setHeaderText("User Not Present");
        alert.showAndWait();
    }

    /**
     * Switches to album display for entered user
     * 
     * @param e ActionEvent (used to enter)
     * @throws IOException If file can't be found
     */
    public void switchToAlbumDisplay(ActionEvent e) throws IOException {
        Stage stage = (Stage) loginField.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../AlbumDisplay/AlbumDisplay.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        AlbumDisplayController controller = loader.getController();
        controller.start(stage, user);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Switches to admin control panel if username 'admin' is entered
     * 
     * @param e ActionEvent (used to enter)
     * @throws IOException If file can't be found
     */
    public void switchToAdminControlPanel(ActionEvent e) throws IOException {
        Stage stage = (Stage) loginField.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../AdminControlPanel/AdminControlPanel.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        AdminControlPanel controller = loader.getController();
        controller.start(stage, userList);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
