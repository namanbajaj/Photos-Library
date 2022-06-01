package view.AdminControlPanel;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import view.LogoutQuitButtons.LogoutQuitButtonsController;


/**
 * Controller class for Admin Control Panel, which allows for creation and deletion of user
 */
public class AdminControlPanel {

    /**
     * LogoutQuitButtonsController for nested buttons
     */
    @FXML
    protected LogoutQuitButtonsController logoutQuitButtonsController;

    @FXML
    ListView<String> userListView;

    @FXML
    Button createUser;
    @FXML
    Button deleteUser;

    @FXML
    Text enterUserText;
    @FXML
    TextField enterUserTextField;
    @FXML
    Button createUserOk;
    @FXML
    Button createUserCancel;

    /**
     * List to be inserted into ListView of users
     */
    private ObservableList<String> obsList;

    /**
     * Initializes AdminControlPanel to be displayed
     * 
     * @param stage Stage on which GUI will be displayed
     * @param userList List of users that Admin can see
     */
    public void start(Stage stage, ArrayList<String> userList) {
        obsList = FXCollections.observableArrayList(userList);
        userListView.setItems(obsList.sorted());
    }
    
    /**
     * Creates user, called when button to create user is clicked
     */
    public void createUser() {
        createUserHelper(true);

        createUserOk.setOnAction((ActionEvent a) -> {
            String name = String.valueOf(enterUserTextField.getText());
            if (!obsList.contains(name) && !name.equals("admin")) {
                obsList.add(name);
                try {
                    FileWriter fw = new FileWriter(new File("src/data/userlist.txt"), true);
                    if (obsList.size() == 1)
                        fw.write(name);
                    else
                        fw.write("\n" + name);
                    fw.flush();
                    fw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                User u = new User(name);

                ObjectOutputStream oos;
                try {
                    oos = new ObjectOutputStream(new FileOutputStream("src\\data\\users" + File.separator + name + ".dat"));
                    oos.writeObject(u);
                    oos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else 
                invalidUser();

            createUserHelper(false);
        });

        createUserCancel.setOnAction((ActionEvent a) -> {
            createUserHelper(false);
        });
    }

    /**
     * Helper method for createUser
     * <p>
     * Called to simplify hiding certain elements
     * 
     * @param visible Whether certain elements remain visible or hidden
     */
    private void createUserHelper(boolean visible) {
        enterUserText.setVisible(visible);
        enterUserTextField.setVisible(visible);
        createUserOk.setVisible(visible);
        createUserCancel.setVisible(visible);
        deleteUser.setVisible(!visible);
        enterUserTextField.setText("");
    }

    /**
     * Displays Alert if user enters invalid username
     */
    private void invalidUser() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Invalid User Warning");
        alert.setHeaderText("User name entered is not valid.\nEnsure that name is not \"admin\" or duplicate");
        alert.showAndWait();
    }

    /**
     * Deletes user, called when button to delete a certain user is clicked
     */
    public void deleteUser() {
        String selected = userListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarningNoUserSelected();
            return;
        }

        obsList.remove(selected);

        try {
            File users = new File("src/data/userlist.txt");
            FileWriter fw = new FileWriter(users, false);

            boolean firstUser = true;
            for (String user : obsList) {
                if (!user.equals(selected))
                    if (firstUser) {
                        fw.write(user);
                        firstUser = false;
                    } else
                        fw.write("\n" + user);
            }

            fw.flush();
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // System.out.println(selected);
        File userfile = new File("src/data/users" + File.separator + selected + ".dat");
        // System.out.println(userfile.toString());
        // System.out.println(userfile.delete());
        userfile.delete();
    }

    /**
     * Displays Alert if admin tries to delete on 'null' user (no user is selected)
     */
    private void showWarningNoUserSelected() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("No User Selected Warning");
        alert.setHeaderText("No User Selected to be Deleted");
        alert.showAndWait();
    }
}
