package presentation;

import domain.Result;
import domain.Trial;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import domain.Participant;
import domain.Referee;
import org.example.IRefereeObserver;
import org.example.ParticipantAlert;
import org.example.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Thread.sleep;

public class MainController implements IRefereeObserver {
    @FXML
    public TableView tableViewResults;
    @FXML
    public TableColumn tableColumnResultParticipantName;
    @FXML
    public TableColumn tableColumnResultParticipantPoints;
    @FXML
    public TextField pointsTextField;

    ObservableList<Participant> model = FXCollections.observableArrayList();

    ObservableList<Participant> modelResults = FXCollections.observableArrayList();

    @FXML
    public Label helloLabel;
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn tableColumnParticipantName;
    @FXML
    public TableColumn tableColumnParticipantPoints;

    private FXMLLoader loader;

    Service service;
    domain.Referee referee;

    private MainController mainController;
    public MainController() {
        // constructor fără parametri
    }
    @FXML
    public void initialize(){
        initializeTable();
    }

    public void initializeTable() {
        tableColumnParticipantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnParticipantPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        tableView.setItems(model);
    }

    public void setController (MainController projectCtrl) {
        this.mainController = projectCtrl;
    }

    public void initialize(String username) {
        helloLabel.setText("Hello, " + username + "!");
    }

    private void initModel() throws Exception {
        model.clear();
        System.out.println(service);
        Iterable<Participant> participants = service.getParticipants(referee);
        for (Participant participant : participants) {
            System.out.println(participant.getName()  + " " + participant.getPoints());
            model.add(participant);
        }
    }

    private void initResultModel() throws Exception {
        modelResults.clear();
        System.out.println(service);
        Iterable<Participant> participants = service.getParticipantsWithPointsAtTrial(referee.getTrial());

        for (Participant participant : participants) {
            int pointsAtTrial = service.getTotalPointsAtTrial(participant,referee.getTrial()); // Obținem punctele pentru participant la proba dată
            participant.setPoints(pointsAtTrial); // Actualizăm punctele participantului în model
            modelResults.add(participant); // Adăugăm fiecare participant în modelul TableView
        }
    }
    public void setService(Service service,Referee referee) throws Exception {
        this.service = service;
        this.referee = referee;
        initModel();
        initResultModel();
    }






    public void addResult(ActionEvent actionEvent) {
        try{
            Participant participant = (Participant) tableViewResults.getSelectionModel().getSelectedItem();
            if(participant!=null){
                Trial trial = referee.getTrial();
                int points = Integer.parseInt(pointsTextField.getText());
                Result result = service.addResult(participant, trial, points);
                if(result==null) {
                    ParticipantAlert.showMessage(null, Alert.AlertType.INFORMATION, "Add result", "There is already a result for this participant at this trial!");
                }
                else {
                    ParticipantAlert.showMessage(null, Alert.AlertType.INFORMATION, "Save result", "The result has been saved successfully!");
                }
            }
            else{
                ParticipantAlert.showMessage(null, Alert.AlertType.INFORMATION, "Add result", "You must select a participant!");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void viewReport(ActionEvent actionEvent) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/report-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("View Report");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            if (referee != null){
                ReportController reportController = loader.getController();
                reportController.setService(service,referee);
                reportController.initializeTable();
                dialogStage.show();
            }
            else
            {
                ParticipantAlert.showErrorMessage(null, "You are not logged in!");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void logout(ActionEvent actionEvent) {
        try {
            service.logout(referee.getPassword(), referee.getName(), this);
        } catch (Exception e) {
            ParticipantAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }
        Stage stage = (Stage) helloLabel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void update() throws Exception {

        Platform.runLater(() -> {
            System.out.println("S-a ajuns main controller update");
            ParticipantAlert.showMessage(null, Alert.AlertType.INFORMATION,"Points added","The list was updated");
            try {
                System.out.println("S a facut un update");
                initModel();
                initResultModel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void initializeResultTable() {
        tableColumnResultParticipantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnResultParticipantPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        tableViewResults.setItems(modelResults);
    }

    public void setServiceOnly(Service service) {
        this.service = service;
    }

    public void setReferee(Referee login) {
        this.referee = login;
    }

    public void setLoader(FXMLLoader mainLoader) {
        this.loader = mainLoader;
    }
}
