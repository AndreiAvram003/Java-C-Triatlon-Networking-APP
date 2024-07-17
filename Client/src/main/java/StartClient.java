

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objectprotocol.ProtoProxy;
import objectprotocol.ServicesProxy;
import org.example.Service;
import presentation.LoginController;
import presentation.MainController;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("bd.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set.");
            //System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }

        String serverIP = serverProps.getProperty("server.host");
        int serverPort = 55555;

        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException e) {
        }

        System.out.println("Using server on host: " + serverIP);
        System.out.println("Using server on port: " + serverPort);
        Service server = new ProtoProxy(serverIP, serverPort);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent loginRoot = fxmlLoader.load();
        LoginController ctrl = fxmlLoader.getController();

        // Create an instance of MainController and pass it to LoginController

        stage.setScene(new Scene(loginRoot));
        ctrl.setService(server);
        stage.show();

    }
}