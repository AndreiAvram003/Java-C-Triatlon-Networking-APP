package presentation;

import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import domain.Referee;
import org.example.ParticipantAlert;
import org.example.Service;

import java.io.IOException;


public class LoginController {
    public Label RefereeLabel;
    public ListView participantsListView;


    @FXML
    private TextField passwordField;
    @FXML
    private TextField usernameField;

    Service service;

    private MainController mainController;

    public LoginController(MainController mainController) {
        this.mainController = mainController;
    }



    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public LoginController() {
        // This constructor is needed for FXMLLoader to instantiate the controller
    }


    public void handleLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-window.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            MainController mainController = loader.getController();
            mainController.setServiceOnly(service);
            Referee wantedRef = service.login(usernameField.getText(), passwordField.getText(), mainController);
            mainController.setReferee(wantedRef);
            if (wantedRef != null) {
                mainController.setService(service, wantedRef);
                mainController.initialize(wantedRef.getName());
                mainController.initializeTable();
                mainController.initializeResultTable();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Main Window");
                stage.show();

                // Close the login window
                ((Stage) usernameField.getScene().getWindow()).close();
            } else {
                ParticipantAlert.showErrorMessage(null, "Wrong username or password! ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setService(Service service) {
        this.service = service;
    }



}
