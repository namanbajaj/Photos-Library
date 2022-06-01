package view.PhotosDisplay;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import view.AlbumDisplay.AlbumDisplayController;
import view.LogoutQuitButtons.LogoutQuitButtonsController;
import view.PhotoView.PhotoView;

/**
 * Controller class for Photo Display
 */
public class PhotosDisplayController {

    /**
     * LogoutQuitButtonsController for nested buttons
     */
    @FXML
    protected LogoutQuitButtonsController logoutQuitButtonsController;

    @FXML
    Button addPhotoButton;
    // @FXML
    // Button searchPhotoButton;
    @FXML
    Button deletePhotoButton;
    @FXML
    Button openPhotoButton;
    @FXML
    Button renamePhotoButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;
    @FXML
    Button tagsButton;
    @FXML
    Button deleteTagButton;
    @FXML
    Button locationTagButton;
    @FXML
    Button personTagButton;
    @FXML
    Button customTagButton;
    @FXML
    Button tagCancelButton;
    @FXML
    Button tagOkButton;
    @FXML
    Button movePhotoButton;
    @FXML
    Button copyPhotoButton;
    @FXML
    Button backbutton;

    @FXML
    TextField dateField;
    @FXML
    TextField captionField;
    @FXML
    TextField inputField;
    @FXML
    Text instructionText;
    @FXML
    Text tagsText;
    @FXML
    Text tagInstructionText;

    @FXML
    ListView<Tag> tagsListView;

    @FXML
    TableView<Photo> photoTable;

    /**
     * List to be inserted into ListView of photos
     */
    private ObservableList<Photo> obsList;

    /**
     * Album that is sent in by AlbumDisplayController
     */
    Album album;

    /**
     * User that is sent in by AlbumDisplayController
     */
    User user;

    /**
     * Used for going back pages
     */
    int indexTest;

    /**
     * Initializes PhotosDisplayController to be displayed
     * <p>
     * Initializes tableView and other fields as well
     * 
     * @param stage Stage on which GUI will be displayed
     * @param album Album that is sent in
     * @param user  User that is sent in
     * @param in    Index used for going back pages
     */
    public void start(Stage stage, Album album, User user, int in) {
        // System.out.println("Inside Album: " + album);
        this.album = album;
        this.user = user;
        indexTest = in;

        photoTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("location"));
        photoTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("caption"));
        TableColumn<Photo, String> thumbnails = new TableColumn<>("Thumbnails");
        thumbnails.setCellValueFactory(new PropertyValueFactory<>("location"));
        thumbnails.setPrefWidth(160);

        thumbnails.setCellFactory(new Callback<TableColumn<Photo, String>, TableCell<Photo, String>>() {
            public TableCell<Photo, String> call(TableColumn<Photo, String> param) {
                TableCell<Photo, String> cell = new TableCell<>() {
                    ImageView imageview = new ImageView();

                    public void updateItem(String location, boolean empty) {
                        if (location != null) {
                            HBox box = new HBox();
                            box.setSpacing(10);
                            imageview.setFitHeight(100);
                            imageview.setFitWidth(160);
                            try {
                                imageview.setImage(new Image(new FileInputStream(location)));
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            box.getChildren().add(imageview);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });

        photoTable.getColumns().set(0, thumbnails);

        // TableColumn<Photo, ?> firstColumn = photoTable.getColumns().get(0);

        obsList = FXCollections.observableArrayList(album.photos);
        photoTable.setItems(obsList);

        photoTable.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> showItem());
    }

    /**
     * Used to goBack to AlbumDisplay
     * 
     * @throws IOException If file can't be found
     */
    public void goBack() throws IOException {
        Stage stage = (Stage) backbutton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../AlbumDisplay/AlbumDisplay.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        AlbumDisplayController controller = loader.getController();
        controller.start(stage, user);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Used to show details of current selected item
     */
    public void showItem() {
        Photo value = photoTable.getSelectionModel().getSelectedItem();
        // System.out.println(value);
        if (value != null) {
            captionField.setText(value.caption);
            // dateField.setText(value.date.toString());
            dateField.setText(new SimpleDateFormat("hh:mm:ss a dd/MM/yyyy").format(value.date));
            ObservableList<Tag> tagObsList;
            tagObsList = FXCollections.observableArrayList(value.tags);
            tagsListView.setItems(tagObsList);
        }
    }

    /**
     * Alert that is displayed user attempts operation on 'null' image (nothing
     * selected)
     */
    private void noPhotoSelectedWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Photo Warning");
        alert.setHeaderText("No Photo Selected!");
        alert.showAndWait();
    }

    /**
     * Alert that is displayed user attempts to move/copy photo to album that
     * doesn't exist
     */
    private void albumNotFoundWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Photo Warning");
        alert.setHeaderText("Album Not Found!");
        alert.showAndWait();
    }

    /**
     * Helper method that toggles certain buttons
     */
    private void toggleButtons() {
        if (addPhotoButton.isVisible()) {
            addPhotoButton.setVisible(false);
            // searchPhotoButton.setVisible(false);
            deletePhotoButton.setVisible(false);
            renamePhotoButton.setVisible(false);
            deleteTagButton.setVisible(false);
            tagsButton.setVisible(false);
            openPhotoButton.setVisible(false);
            movePhotoButton.setVisible(false);
            copyPhotoButton.setVisible(false);
            tagsListView.setVisible(false);
            okButton.setVisible(true);
            cancelButton.setVisible(true);
            instructionText.setVisible(true);
            dateField.setVisible(false);
            captionField.setVisible(false);
            tagsText.setVisible(false);
            inputField.setVisible(true);
        } else {
            addPhotoButton.setVisible(true);
            // searchPhotoButton.setVisible(true);
            deletePhotoButton.setVisible(true);
            renamePhotoButton.setVisible(true);
            deleteTagButton.setVisible(true);
            tagsButton.setVisible(true);
            openPhotoButton.setVisible(true);
            movePhotoButton.setVisible(true);
            copyPhotoButton.setVisible(true);
            tagsListView.setVisible(true);
            okButton.setVisible(false);
            cancelButton.setVisible(false);
            instructionText.setVisible(false);
            dateField.setVisible(true);
            captionField.setVisible(true);
            tagsText.setVisible(true);
            inputField.setVisible(false);
        }
    }

    /**
     * Used to overwrite details of user when user changes details of specific photo
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
     * Adds photo to album that user is in
     * 
     * @param e ActionEvent (used by Button)
     */
    public void addPhoto(ActionEvent e) {
        toggleButtons();
        inputField.setPromptText("Location");
        inputField.setText("");
        instructionText.setText("Enter Photo Location on Disk");

        okButton.setOnAction((ActionEvent a) -> {
            // System.out.println("OK BUTTON CLICKED");
            String location = String.valueOf(inputField.getText());
            for (Photo photoInAlbum : album.photos) {
                if (photoInAlbum.location.equals(location)) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Photo Warning");
                    alert.setHeaderText("Duplicate Photo!");
                    alert.showAndWait();
                    toggleButtons();
                    return;
                }
            }

            for (Album alb : user.albums) {
                if (!alb.name.equals(album.name)) {
                    for (Photo photoInOtherAlbum : alb.photos) {
                        if (photoInOtherAlbum.location.equals(location)) {
                            album.photos.add(photoInOtherAlbum);
                            album.updateDateRange();
                            album.size++;
                            obsList.add(album.photos.get(album.photos.size() - 1));
                            overwriteUser();
                            photoTable.refresh();
                            toggleButtons();
                            return;
                        }
                    }
                }
            }
            // b_one = ImageIO.read(new File(location));
            // Image one = SwingFXUtils.toFXImage(b_one, null);
            album.addPhoto(location);
            obsList.add(album.photos.get(album.photos.size() - 1));
            overwriteUser();
            photoTable.refresh();
            toggleButtons();
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            toggleButtons();
        });
    }

    /**
     * Deletes selected photo from album that user is in
     * 
     * @param e ActionEvent (used by Button)
     */
    public void deletePhoto(ActionEvent e) {
        int index = photoTable.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        toggleButtons();
        inputField.setVisible(false);

        Photo selectedPhoto = album.photos.get(index);

        // inputField.setPromptText("New Caption");
        instructionText.setText("Click OK to Confirm Deletion");

        okButton.setOnAction((ActionEvent a) -> {
            // System.out.println("OK BUTTON CLICKED");
            album.photos.remove(index);
            album.updateDateRange();
            album.size--;
            obsList.remove(selectedPhoto);
            overwriteUser();
            photoTable.refresh();
            toggleButtons();
            inputField.setVisible(false);
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            toggleButtons();
            inputField.setVisible(false);
        });
    }

    /**
     * Opens selected photo in PhotoView
     * 
     * @param e ActionEvent (used by Button)
     * @throws IOException If file can't be found
     */
    public void openPhoto(ActionEvent e) throws IOException {
        Stage stage = (Stage) openPhotoButton.getScene().getWindow();
        int index = photoTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../PhotoView/PhotoView.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        PhotoView controller = loader.getController();
        controller.start(stage, album, user, index, indexTest);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Recaptions selected photo
     * 
     * @param e ActionEvent (used by Button)
     */
    public void renamePhoto(ActionEvent e) {
        int index = photoTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        toggleButtons();

        Photo selectedPhoto = album.photos.get(index);

        inputField.setPromptText("New Caption");
        instructionText.setText("Enter New Caption");
        //
        okButton.setOnAction((ActionEvent a) -> {
            // System.out.println("OK BUTTON CLICKED");
            String newCaption = String.valueOf(inputField.getText());
            selectedPhoto.caption = newCaption;
            overwriteUser();
            photoTable.refresh();
            toggleButtons();
            showItem();
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            toggleButtons();
        });
    }

    /**
     * Method creates tags
     * 
     * @param e ActionEvent (used by Button)
     */
    public void openTags(ActionEvent e) {
        int index = photoTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        Photo selectedPhoto = album.photos.get(index);

        toggleButtons();
        tagsListView.setVisible(true);
        okButton.setVisible(false);
        cancelButton.setVisible(false);
        locationTagButton.setVisible(true);
        personTagButton.setVisible(true);
        customTagButton.setVisible(true);
        tagInstructionText.setVisible(true);
        inputField.setVisible(false);
        instructionText.setVisible(false);
        tagsText.setVisible(true);

        locationTagButton.setOnAction((ActionEvent a) -> {
            dateField.setText("location");
            tagsHelper(selectedPhoto);
        });

        personTagButton.setOnAction((ActionEvent a) -> {
            dateField.setText("person");
            tagsHelper(selectedPhoto);
        });

        customTagButton.setOnAction((ActionEvent a) -> {
            dateField.setText("");
            dateField.setPromptText("Enter Tag Name");
            dateField.setEditable(true);
            tagsHelper(selectedPhoto);
        });
    }

    /**
     * Helper method to help create tags
     * 
     * @param selectedPhoto photo that is having its tag altered
     */
    public void tagsHelper(Photo selectedPhoto) {
        locationTagButton.setVisible(false);
        personTagButton.setVisible(false);
        customTagButton.setVisible(false);
        tagInstructionText.setVisible(false);

        dateField.setVisible(true);
        captionField.setVisible(true);
        captionField.setEditable(true);
        captionField.setText("");
        captionField.setPromptText("Enter Tag Value");

        tagOkButton.setVisible(true);
        tagCancelButton.setVisible(true);

        tagOkButton.setOnAction((ActionEvent a) -> {
            String name = dateField.getText();
            String value = captionField.getText();
            selectedPhoto.tags.add(new Tag(name, value));
            overwriteUser();
            toggleButtons();
            showItem();
            tagOkButton.setVisible(false);
            tagCancelButton.setVisible(false);
        });

        tagCancelButton.setOnAction((ActionEvent a) -> {
            toggleButtons();
            showItem();
            tagOkButton.setVisible(false);
            tagCancelButton.setVisible(false);
        });
    }

    /**
     * Deletes selected tag
     * 
     * @param e ActionEvent (used by Button)
     */
    public void deleteTag(ActionEvent e) {
        int index = photoTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        int tagIndex = tagsListView.getSelectionModel().getSelectedIndex();

        if (tagIndex < 0) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Photo Warning");
            alert.setHeaderText("No Tag Selected!");
            alert.showAndWait();
        }

        Photo selectedPhoto = photoTable.getSelectionModel().getSelectedItem();
        Tag selectedTag = tagsListView.getSelectionModel().getSelectedItem();

        selectedPhoto.tags.remove(selectedTag);
        tagsListView.getItems().remove(selectedTag);
        overwriteUser();
    }

    /**
     * Copies selected photo to another album
     * 
     * @param e ActionEvent (used by Button)
     */
    public void copyPhoto(ActionEvent e) {
        int index = photoTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        toggleButtons();

        Photo selectedPhoto = album.photos.get(index);

        inputField.setPromptText("Album Name");
        instructionText.setText("Enter Name of Album to Copy Photo to");

        okButton.setOnAction((ActionEvent a) -> {
            // System.out.println("OK BUTTON CLICKED");
            String newAlbum = String.valueOf(inputField.getText());
            boolean albumFound = false;
            for (Album alb : user.albums) {
                if (alb.name.equals(newAlbum)) {
                    for (Photo ph : alb.photos) {
                        if (selectedPhoto.location.equals(ph.location)) {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Photo Warning");
                            alert.setHeaderText("Duplicate Photo Present!");
                            alert.showAndWait();
                            toggleButtons();
                            return;
                        }
                    }
                    albumFound = true;
                    alb.photos.add(selectedPhoto);
                    alb.updateDateRange();
                    alb.size++;
                }
            }

            if (!albumFound) {
                albumNotFoundWarning();
                return;
            }

            overwriteUser();
            photoTable.refresh();
            toggleButtons();
            showItem();
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            toggleButtons();
        });
    }

    /**
     * Moves selected photo to another album
     * 
     * @param e ActionEvent (used by Button)
     */
    public void movePhoto(ActionEvent e) {
        int index = photoTable.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            noPhotoSelectedWarning();
            return;
        }

        toggleButtons();

        Photo selectedPhoto = album.photos.get(index);

        inputField.setPromptText("Album Name");
        instructionText.setText("Enter Name of Album to Move Photo to");

        okButton.setOnAction((ActionEvent a) -> {
            // System.out.println("OK BUTTON CLICKED");
            String newAlbum = String.valueOf(inputField.getText());
            boolean albumFound = false;
            for (Album alb : user.albums) {
                if (alb.name.equals(newAlbum)) {
                    for (Photo ph : alb.photos) {
                        if (selectedPhoto.location.equals(ph.location)) {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Photo Warning");
                            alert.setHeaderText("Duplicate Photo Present!");
                            alert.showAndWait();
                            toggleButtons();
                            return;
                        }
                    }
                    albumFound = true;
                    alb.photos.add(selectedPhoto);
                    alb.updateDateRange();
                    alb.size++;
                }
            }

            if (!albumFound) {
                albumNotFoundWarning();
                return;
            }

            album.photos.remove(index);
            album.size--;
            obsList.remove(selectedPhoto);
            album.updateDateRange();
            overwriteUser();
            photoTable.refresh();
            toggleButtons();
            showItem();
        });

        cancelButton.setOnAction((ActionEvent a) -> {
            toggleButtons();
        });
    }
}
