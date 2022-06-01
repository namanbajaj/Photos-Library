package view.AlbumDisplay;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Album;
import model.User;
import view.LogoutQuitButtons.LogoutQuitButtonsController;
import view.PhotosDisplay.PhotosDisplayController;
import view.Search.SearchController;

/**
 * Controller class for Album Display Controller, which allows for many
 * operations on Album objects (creation, search, deletion, open, rename)
 */
public class AlbumDisplayController {

    /**
     * LogoutQuitButtonsController for nested buttons
     */
    @FXML
    protected LogoutQuitButtonsController logoutQuitButtonsController;

    @FXML
    Button createAlbumButton;
    @FXML
    Button searchAlbumButton;
    @FXML
    Button deleteAlbumButton;
    @FXML
    Button openAlbumButton;
    @FXML
    Button renameAlbumButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;

    @FXML
    TextField albumfield;

    @FXML
    TableView<Album> albumTable;

    /**
     * List to be inserted into ListView of albums
     */
    private ObservableList<Album> obsList;

    /**
     * User that is being viewed/altered
     */
    User user;

    /**
     * Initializes AlbumDisplayController to be displayed
     * 
     * @param stage Stage on which GUI will be displayed
     * @param u     List of albums that User can see
     */
    public void start(Stage stage, User u) {
        /**
         * TableView (wip)
         */
        // System.out.println(u);
        user = u;

        // TableColumn<Album, String> col1 = new TableColumn<>("Album Name");
        albumTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        albumTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("size"));
        albumTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("range"));

        // albumTable.getColumns().add(col1);

        // albumTable.getItems().add(new Album("Album 1"));

        // ArrayList<Album> arraylist = new ArrayList<>();

        // arraylist.add(new Album("Album 1"));
        // arraylist.add(new Album("Album 2"));
        // arraylist.add(new Album("Album 3"));
        // arraylist.add(new Album("Album 4"));

        obsList = FXCollections.observableArrayList(u.albums);

        albumTable.setItems(obsList);

        albumTable.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> showItem());

    }

    /**
     * Used to show details of selected Album
     */
    private void showItem() {
        Album value = albumTable.getSelectionModel().getSelectedItem();
        // System.out.println(value);
        if (value != null) {
            albumfield.setText(value.name);
        }
    }

    /**
     * Displays Alert if user tries to perform operation on 'null' album (nothing
     * selected)
     */
    private void noAlbumSelectedWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Album Warning");
        alert.setHeaderText("No Album Selected!");
        alert.showAndWait();
    }

    /**
     * Used to overwrite details of user when user changes details of specific album
     */
    private void overwriteUser() {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(
                    new FileOutputStream("src\\data\\users" + File.separator + user.name + ".dat"));
            oos.writeObject(user);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Creates album, called when button to create album is clicked by user
     * 
     * @param e ActionEvent (used by Button)
     */
    public void createAlbum(ActionEvent e) {
        // System.out.println("ADD album");

        albumTable.getSelectionModel().clearSelection();

        albumfield.setText("");
        albumfield.setEditable(true);
        albumfield.setPromptText("Enter Album Name");

        // set buttons visible
        okButton.setVisible(true);
        cancelButton.setVisible(true);

        okButton.setOnAction((ActionEvent a) -> {
            String name = String.valueOf(albumfield.getText());
            // System.out.println(name);

            for (Album alb : user.albums) {
                if (alb.name.equals(name)) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Album Warning");
                    alert.setHeaderText("Duplicate Album!");
                    alert.showAndWait();
                    okButton.setVisible(false);
                    cancelButton.setVisible(false);
                    albumfield.setEditable(false);
                    albumfield.setText("");
                    albumfield.setPromptText("Selected Album Name");
                    return;
                }
            }

            Album add = new Album(name);

            obsList.add(add);
            user.albums.add(add);

            overwriteUser();

            okButton.setVisible(false);
            cancelButton.setVisible(false);
            albumfield.setEditable(false);
            albumfield.setText("");
            albumfield.setPromptText("Selected Album Name");
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            okButton.setVisible(false);
            cancelButton.setVisible(false);
            albumfield.setEditable(false);
            albumfield.setText("");
            albumfield.setPromptText("Selected Album Name");
        });
    }

    /**
     * Changes to GUI display for Searching through user's albums
     * 
     * @param e ActionEvent (used by Button)
     * @throws IOException If file can't be found
     */
    public void searchAlbum(ActionEvent e) throws IOException {
        Stage stage = (Stage) searchAlbumButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Search/Search.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        SearchController controller = loader.getController();
        controller.start(stage, user);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens selected album in PhotoDisplayController
     * 
     * @param e ActionEvent (used by Button)
     * @throws IOException If file can't be found
     */
    public void openAlbum(ActionEvent e) throws IOException {
        Stage stage = (Stage) openAlbumButton.getScene().getWindow();

        int index = albumTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noAlbumSelectedWarning();
            return;
        }

        // System.out.println(user.albums.get(index) + " selected");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../PhotosDisplay/PhotosDisplay.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        PhotosDisplayController controller = loader.getController();
        controller.start(stage, user.albums.get(index), user, index);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes selected album
     * 
     * @param e ActionEvent (used by Button)
     */
    public void deleteAlbum(ActionEvent e) {
        int index = albumTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noAlbumSelectedWarning();
            return;
        }

        // System.out.println(album + " selected");

        albumfield.setText("Click OK to Confim");
        // albumfield.setEditable(true);
        // albumfield.setPromptText("Enter New Album Name");

        okButton.setVisible(true);
        cancelButton.setVisible(true);

        okButton.setOnAction((ActionEvent a) -> {
            // System.out.println("Removing: " + user.albums.get(index));

            user.albums.remove(index);
            obsList.remove(index);

            overwriteUser();

            // IMPORTANT to update tableView
            albumTable.refresh();

            okButton.setVisible(false);
            cancelButton.setVisible(false);
            albumfield.setEditable(false);
            albumfield.setText("");
            albumfield.setPromptText("Selected Album Name");
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            okButton.setVisible(false);
            cancelButton.setVisible(false);
            albumfield.setEditable(false);
            albumfield.setText("");
            albumfield.setPromptText("Selected Album Name");
        });
    }

    /**
     * Renames selected album
     * 
     * @param e ActionEvent (used by Button)
     */
    public void renameAlbum(ActionEvent e) {
        int index = albumTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noAlbumSelectedWarning();
            return;
        }

        Album album = user.albums.get(index);
        // System.out.println(album + " selected");

        albumfield.setText("");
        albumfield.setEditable(true);
        albumfield.setPromptText("Enter New Album Name");

        okButton.setVisible(true);
        cancelButton.setVisible(true);

        okButton.setOnAction((ActionEvent a) -> {
            String newName = String.valueOf(albumfield.getText());
            // System.out.println("New name: " + newName);

            // System.out.println(user.albums.get(index).name);

            album.name = newName;

            overwriteUser();

            // IMPORTANT to update tableView
            albumTable.refresh();

            okButton.setVisible(false);
            cancelButton.setVisible(false);
            albumfield.setEditable(false);
            albumfield.setText("");
            albumfield.setPromptText("Selected Album Name");
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            okButton.setVisible(false);
            cancelButton.setVisible(false);
            albumfield.setEditable(false);
            albumfield.setText("");
            albumfield.setPromptText("Selected Album Name");
        });
    }

}
