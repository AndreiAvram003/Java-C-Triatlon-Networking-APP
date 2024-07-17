package presentation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import domain.Participant;
import domain.Referee;
import domain.Trial;
import org.example.IRefereeObserver;
import org.example.ParticipantAlert;
import org.example.Service;

import java.util.List;

public class ReportController implements IRefereeObserver {

    ObservableList<Participant> model = FXCollections.observableArrayList();
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn tableColumnParticipantName;
    @FXML
    public TableColumn tableColumnParticipantPoints;

    Service service;
    Referee referee;


    public void initializeTable() {
        tableColumnParticipantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnParticipantPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        tableView.setItems(model);
    }




    public void displayParticipantsAndPointsAtTrial(Trial trial) throws Exception {
        List<Participant> participants = service.getParticipantsWithPointsAtTrial(trial);
        model.clear(); // Curățăm modelul pentru a elimina datele anterioare

        for (Participant participant : participants) {
            int pointsAtTrial = service.getTotalPointsAtTrial(participant,trial); // Obținem punctele pentru participant la proba dată
            participant.setPoints(pointsAtTrial); // Actualizăm punctele participantului în model
            model.add(participant); // Adăugăm fiecare participant în modelul TableView
        }
    }
    public void setService(Service service, Referee referee) throws Exception {
        this.service = service;
        this.referee = referee;
        System.out.println(referee.getTrial());
        displayParticipantsAndPointsAtTrial(referee.getTrial());
    }

    @Override
    public void update() throws Exception {
        Platform.runLater(() -> {
            ParticipantAlert.showMessage(null, Alert.AlertType.INFORMATION,"New points added","The list was modified");
            try {
                displayParticipantsAndPointsAtTrial(referee.getTrial());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

