package view.Search;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import view.AlbumDisplay.AlbumDisplayController;

/**
 * Controller class for search
 */
public class SearchController {
    @FXML
    Button daterangebutton;
    @FXML
    Button tagbutton;

    @FXML
    Button backbutton;

    @FXML
    Text totext;
    @FXML
    DatePicker beginningrange;
    @FXML
    DatePicker endrange;

    @FXML
    Button dosearchbutton;
    @FXML
    Button cancelsearchbutton;

    @FXML
    ImageView searchresults;

    @FXML
    Button nextimagebutton;
    @FXML
    Button previousimagebutton;

    @FXML
    Button finishsearchbutton;
    @FXML
    Button createalbumbutton;

    @FXML
    Text tagsearchtext;
    @FXML
    Text tagonetext;
    @FXML
    Text keyonetext;
    @FXML
    Text valueonetext;
    @FXML
    Text tagtwotext;
    @FXML
    Text keytwotext;
    @FXML
    Text valuetwotext;

    @FXML
    TextField keyonetextfield;
    @FXML
    TextField valueonetextfield;
    @FXML
    TextField keytwotextfield;
    @FXML
    TextField valuetwotextfield;

    @FXML
    ChoiceBox<String> andordropdown;

    @FXML
    Text enteralbumnametext;
    @FXML
    TextField albumnamefield;
    @FXML
    Button okbuttonalbumname;
    @FXML
    Button cancelbuttonalbumname;

    /**
     * User that is being searched
     */
    User user;

    /**
     * Used to properly set imageView
     */
    int index;

    /**
     * List of photos that meets requirements set by user
     */
    ArrayList<Photo> validPhotos;

    /**
     * Initializes Search to be displayed
     * 
     * @param stage Stage on which GUI will be displayed
     * @param u     User that is sent in by AlbumDisplayController
     */
    public void start(Stage stage, User u) {
        user = u;
    }

    /**
     * Searches photos by date
     * 
     * @param e ActionEvent used by Button
     */
    public void searchByDate(ActionEvent e) {
        searchByDateHelper(false);

        dosearchbutton.setOnAction((ActionEvent a) -> {
            if (beginningrange.getValue() != null && endrange.getValue() != null
                    && endrange.getValue().compareTo(beginningrange.getValue()) > 0) {
                Date startdate = Date.from(beginningrange.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date enddate = Date.from(endrange.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                // // System.out.println(test.toString());
                // // System.out.println(startdate);
                // // System.out.println(test.compareTo(startdate));

                validPhotos = new ArrayList<Photo>();

                for (int i = 0; i < user.albums.size(); i++)
                    for (Photo p : user.albums.get(i).photos)
                        if (startdate.compareTo(p.date) < 0 && enddate.compareTo(p.date) > 0)
                            validPhotos.add(p);

                // check for duplicate photos in ValidPhotos
                ArrayList<String> templocs = new ArrayList<String>();
                ArrayList<Photo> temp = new ArrayList<Photo>();
                for (int i = 0; i < validPhotos.size(); i++) {
                    if (!templocs.contains(validPhotos.get(i).location)){
                        temp.add(validPhotos.get(i));
                        templocs.add(validPhotos.get(i).location);
                    }
                }
                validPhotos = temp;

                tagsearchtext.setVisible(false);
                display();
            }

            else
                invalidDateWarning();
        });

        cancelsearchbutton.setOnAction((ActionEvent a) -> {
            searchByDateHelper(true);
        });
    }

    /**
     * Helper method for searching by date
     * 
     * @param visible Whether to show or hide certain elements
     */
    private void searchByDateHelper(boolean visible){
        daterangebutton.setVisible(visible);
        tagbutton.setVisible(visible);
        dosearchbutton.setVisible(!visible);
        cancelsearchbutton.setVisible(!visible);
        totext.setVisible(!visible);
        beginningrange.setVisible(!visible);
        endrange.setVisible(!visible);
    }

    /**
     * Helper method to display images on imageView
     */
    public void display() {
        if (validPhotos.size() > 0) {
            finishsearchbutton.setVisible(true);
            createalbumbutton.setVisible(true);
            dosearchbutton.setVisible(false);
            cancelsearchbutton.setVisible(false);
            totext.setVisible(false);
            beginningrange.setVisible(false);
            endrange.setVisible(false);
            try {
                searchresults.setVisible(true);
                nextimagebutton.setVisible(true);
                previousimagebutton.setVisible(true);

                int index = 0;
                searchresults.setImage(new Image(new FileInputStream(validPhotos.get(index).location)));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } else {
            noResultsFound();
        }
    }

    /**
     * Goes to next image on imageView
     */
    public void nextImage() {
        index++;
        if (index == validPhotos.size())
            index = 0;

        try {
            searchresults
                    .setImage(new Image(new FileInputStream(validPhotos.get(index).location)));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Goes to previous image on imageView
     */
    public void previousImage() {
        index--;
        if (index == -1)
            index = validPhotos.size() - 1;

        try {
            searchresults
                    .setImage(new Image(new FileInputStream(validPhotos.get(index).location)));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 'Finishes' search - reallows user to decide what to search by
     */
    public void finishSearch() {
        finishsearchbutton.setVisible(false);
        createalbumbutton.setVisible(false);
        searchresults.setVisible(false);
        nextimagebutton.setVisible(false);
        previousimagebutton.setVisible(false);
        daterangebutton.setVisible(true);
        tagbutton.setVisible(true);
    }

    /**
     * Displays alert if no image with user set requirements found
     */
    private void noResultsFound() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("No Photos Found");
        alert.setHeaderText("Photo range contains no photos from user \'" + user.name + "\'");
        alert.showAndWait();
    }

    /**
     * Displays alert if user attempts to search with invalid dates
     */
    private void invalidDateWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Invalid Date Warning");
        alert.setHeaderText("Invalid date entered.\nEnsure end date is not earlier than start date.");
        alert.showAndWait();
    }

    /**
     * Used to search by tag
     * 
     * @param e ActionEvent (used by Button)
     */
    public void searchByTag(ActionEvent e) {
        daterangebutton.setVisible(false);
        tagbutton.setVisible(false);
        searchByTagHelper(true);

        andordropdown.getItems().clear();
        andordropdown.getItems().addAll("and", "or");
        andordropdown.getSelectionModel().select(1);

        dosearchbutton.setOnAction((ActionEvent a) -> {
            validPhotos = new ArrayList<Photo>();

            String keyone = String.valueOf(keyonetextfield.getText());
            String valueone = String.valueOf(valueonetextfield.getText());

            String keytwo = String.valueOf(keytwotextfield.getText());
            String valuetwo = String.valueOf(valuetwotextfield.getText());

            // and - index 0, or - index 1
            int andor = andordropdown.getSelectionModel().getSelectedIndex();

            // System.out.println(keyone + " - " + valueone + "\n" + keytwo + " - " + valuetwo);

            // invalid options
            if (keyone == "" || valueone == "" || (andor == 0 && (keytwo == "" || valuetwo == ""))
                    || (andor == 1 && ((keytwo == "" && valuetwo != "") || (keytwo != "" && valuetwo == "")))) {
                invalidTagSearch();
                return;
            }

            else {
                // only one tag
                if (andor == 1 && keytwo == "" && valuetwo == "") {
                    for (int i = 0; i < user.albums.size(); i++)
                        for (int j = 0; j < user.albums.get(i).photos.size(); j++)
                            for (Tag t : user.albums.get(i).photos.get(j).tags)
                                if (t.name.equals(keyone) && t.value.equals(valueone))
                                    validPhotos.add(user.albums.get(i).photos.get(j));
                }

                // and
                else if (andor == 0) {
                    for (int i = 0; i < user.albums.size(); i++) {
                        ArrayList<Photo> curPhotos = user.albums.get(i).photos;
                        for (int j = 0; j < curPhotos.size(); j++) {
                            ArrayList<Tag> curTags = curPhotos.get(j).tags;
                            for (int k = 0; k < curTags.size(); k++) {
                                for (int l = k + 1; l < curTags.size(); l++) {
                                    if ((curTags.get(k).name.equals(keyone) && curTags.get(k).value.equals(valueone))
                                            && (curTags.get(l).name.equals(keytwo)
                                                    && curTags.get(l).value.equals(valuetwo)))
                                        validPhotos.add(user.albums.get(i).photos.get(j));
                                }
                            }
                        }
                    }
                }

                // or
                else {
                    for (int i = 0; i < user.albums.size(); i++)
                        for (int j = 0; j < user.albums.get(i).photos.size(); j++)
                            for (Tag t : user.albums.get(i).photos.get(j).tags)
                                if ((t.name.equals(keyone) && t.value.equals(valueone))
                                        || (t.name.equals(keytwo) && t.value.equals(valuetwo)))
                                    validPhotos.add(user.albums.get(i).photos.get(j));
                }

                // check for duplicates
                ArrayList<String> templocs = new ArrayList<String>();
                ArrayList<Photo> temp = new ArrayList<Photo>();
                for (int i = 0; i < validPhotos.size(); i++) {
                    if (!templocs.contains(validPhotos.get(i).location)){
                        temp.add(validPhotos.get(i));
                        templocs.add(validPhotos.get(i).location);
                    }
                }
                validPhotos = temp;

                searchByTagHelper(false);
                display();
            }

        });

        cancelsearchbutton.setOnAction((ActionEvent a) -> {
            daterangebutton.setVisible(true);
            tagbutton.setVisible(true);
            searchByTagHelper(false);
        });

    }

    /**
     * Displays alert is user enters invalid tag values
     */
    private void invalidTagSearch() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Invalid Tag Search Warning");
        alert.setHeaderText(
                "Invalid search entered.\nEnsure that if doing search with one tag, set dropdown to \'or\' and leave key two and value two fields blank.\nTag one values must be filled in.");
        alert.showAndWait();
    }

    /**
     * Helper method for searching by tag - sets certain fields visible/invisible
     * 
     * @param visible sets certain fields visible/invisible
     */
    private void searchByTagHelper(boolean visible) {
        dosearchbutton.setVisible(visible);
        cancelsearchbutton.setVisible(visible);

        tagsearchtext.setVisible(visible);

        tagonetext.setVisible(visible);
        keyonetext.setVisible(visible);
        valueonetext.setVisible(visible);
        tagtwotext.setVisible(visible);
        keytwotext.setVisible(visible);
        valuetwotext.setVisible(visible);

        keyonetextfield.setVisible(visible);
        valueonetextfield.setVisible(visible);
        keytwotextfield.setVisible(visible);
        valuetwotextfield.setVisible(visible);

        andordropdown.setVisible(visible);
    }

    /**
     * Overwrites user if user altered (new album created with search results)
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
     * Creates album with search results
     */
    public void createAlbum() {
        // System.out.println("SIZE IS " + validPhotos.size());
        createAlbumHelper(true);

        okbuttonalbumname.setOnAction((ActionEvent a) -> {
            String name = String.valueOf(albumnamefield.getText());

            for (Album alb : user.albums) {
                if (alb.name.equals(name)) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Album Warning");
                    alert.setHeaderText("Duplicate Album!");
                    alert.showAndWait();
                    return;
                }
            }

            Album album = new Album(name);

            int counter = 0;

            for (Photo p : validPhotos) {
                album.addPhoto(p.location);
                album.photos.get(counter).caption = p.caption;
                album.photos.get(counter).tags = p.tags;
                counter++;
            }

            user.albums.add(album);
            overwriteUser();

            createAlbumHelper(false);
        });

        cancelbuttonalbumname.setOnAction((ActionEvent a) -> {
            createAlbumHelper(false);
        });
    }

    /**
     * Helper method for creating album
     * <p>
     * Sets certain elements visible/invisible
     * 
     * @param visible Set certain elements visible/invisible
     */
    private void createAlbumHelper(boolean visible) {
        enteralbumnametext.setVisible(visible);
        albumnamefield.setVisible(visible);
        okbuttonalbumname.setVisible(visible);
        cancelbuttonalbumname.setVisible(visible);
    }

    /**
     * Go back to AlbumDisplay
     * 
     * @throws IOException If stream can't be opened
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

}
