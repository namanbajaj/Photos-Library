package view.PhotoView;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import view.LogoutQuitButtons.LogoutQuitButtonsController;
import view.PhotosDisplay.PhotosDisplayController;

/**
 * Controller class for Photo View
 */
public class PhotoView {

    /**
     * LogoutQuitButtonsController for nested buttons
     */
    @FXML
    protected LogoutQuitButtonsController logoutQuitButtonsController;

    @FXML
    ImageView photoview;

    @FXML
    Button nextPhoto;
    @FXML
    Button previousPhoto;

    @FXML
    ListView<Tag> taglistview;

    @FXML
    TextField datefield;

    @FXML
    TextField captionfield;

    @FXML
    Button backbutton;

    /**
     * User that is having its photos displayed
     */
    User user;

    /**
     * List of photos that is being displayed
     */
    ArrayList<Photo> photos;

    /**
     * Used to iterate through list of photos
     */
    int counter;

    /**
     * Used to go back to previous page (PhotosDisplayController)
     */
    int index;

    /**
     * Initializes PhotoView to be displayed
     * 
     * @param stage  Stage on which GUI will be displayed
     * @param a      Album being displayed
     * @param u      User whose albums is being displayed
     * @param in     Index of photos
     * @param indexT Index of album in user list
     * @throws IOException If file can't be found
     */
    public void start(Stage stage, Album a, User u, int in, int indexT) throws IOException {
        photos = a.photos;
        user = u;
        counter = in;
        index = indexT;

        // load first image
        String first = null;
        if (photos.size() != 0)
            first = photos.get(counter).location;

        if (first != null)
            setFields(first);
    }

    /**
     * Sets fields to appropriate values given location of image
     * 
     * @param first Location of photo that fields is being set to
     * @throws FileNotFoundException
     */
    private void setFields(String first) throws FileNotFoundException {
        photoview.setImage(new Image(new FileInputStream(first)));
        datefield.setText(new SimpleDateFormat("hh:mm:ss a MM/dd/yyyy").format(photos.get(counter).date));
        captionfield.setText(photos.get(counter).caption);
        ObservableList<Tag> tagObsList;
        tagObsList = FXCollections.observableArrayList(photos.get(counter).tags);
        taglistview.setItems(tagObsList);
    }

    /**
     * Used to go to next image in manual slideshow
     * 
     * @param e ActionEvent (used by Button)
     * @throws IOException If file can't be found
     */
    public void nextImage(ActionEvent e) throws IOException {
        counter++;
        if (counter == photos.size())
            counter = 0;
        String first = photos.get(counter).location;
        if (first != null)
            setFields(first);
    }

    /**
     * Used to go to previous image in manual slideshow
     * 
     * @param e ActionEvent (used by Button)
     * @throws IOException If file can't be found
     */
    public void previousImage(ActionEvent e) throws IOException {
        counter--;
        if (counter == -1)
            counter = photos.size() - 1;
        String first = photos.get(counter).location;
        if (first != null)
            setFields(first);
    }

    /**
     * Used to goBack to AlbumDisplay
     * 
     * @throws IOException If file can't be found
     */
    public void goBack() throws IOException {
        Stage stage = (Stage) backbutton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../PhotosDisplay/PhotosDisplay.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        PhotosDisplayController controller = loader.getController();
        controller.start(stage, user.albums.get(index), user, index);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // private void addPhotoHelper() {
    // okButton.setVisible(true);
    // }

    // private void overwriteUser() {
    // ObjectOutputStream oos;
    // try {
    // oos = new ObjectOutputStream(
    // new FileOutputStream("src\\data\\users" + File.separator + user.name +
    // ".dat"));
    // oos.writeObject(user);

    // } catch (IOException e1) {
    // e1.printStackTrace();
    // }
    // }

    // private void invalidLocWarning() {
    // Alert alert = new Alert(AlertType.WARNING);
    // alert.setTitle("Invalid Location Warning");
    // alert.setHeaderText("Photo Not Found");
    // alert.showAndWait();
    // }

    // public void addPhoto(ActionEvent e) throws IOException {
    // // System.out.println("ADD PHOTO");
    // startAdd();

    // okButton.setOnAction((ActionEvent a) -> {
    // // BufferedImage b_one;

    // try {
    // // System.out.println("OK BUTTON CLICKED");
    // String location = String.valueOf(addPhotoTextField.getText());
    // // b_one = ImageIO.read(new File(location));
    // // Image one = SwingFXUtils.toFXImage(b_one, null);
    // // photoview.setImage(one);
    // photoview.setImage(new Image(new FileInputStream(location)));
    // user.albums.get(index).addPhoto(location);
    // // System.out.println(user);
    // // System.out.println(user.albums.get(index));
    // overwriteUser();
    // finishAdd();
    // } catch (IOException e1) {
    // // System.out.println("INVALID LOCATION");
    // invalidLocWarning();
    // }
    // });

    // cancelButton.setOnAction((ActionEvent a) -> {
    // finishAdd();
    // });

    // }

    // private void finishAdd() {
    // addPhotoTextField.setText("");
    // addPhotoTextField.setEditable(false);
    // okButton.setVisible(false);
    // cancelButton.setVisible(false);
    // }

    // private void startAdd() {
    // addPhotoTextField.setText("");
    // addPhotoTextField.setEditable(true);
    // okButton.setVisible(true);
    // cancelButton.setVisible(true);
    // }
}
