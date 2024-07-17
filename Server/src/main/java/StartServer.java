import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.*;
import org.example.Service;
import service.ServiceImpl;
import utils.AbstractServer;
import utils.ProjectConcurrentServer;
import utils.ProtobuffServer;
import utils.ServerException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static final Logger logger = LogManager.getLogger();
    private static final int defaultPort = 55555;

    public static void main(String[] args) {
        logger.traceEntry();
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("bd.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
            //System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        RefereeRepository refereeDBRepo = new RefereeDBRepo(serverProps);
        TrialRepository trialDBRepo = new TrialDBRepo(serverProps);
        ResultRepository resultDBRepo = new ResultDBRepo(serverProps);
        ParticipantRepository participantDBRepo = new ParticipantDBRepo(serverProps);

        Service projectServices = new ServiceImpl(participantDBRepo, trialDBRepo, resultDBRepo, refereeDBRepo);

        int projectServerPort = defaultPort;
        try {
            projectServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef) {
            logger.error(nef);
            logger.info("Using default port: {}", defaultPort);
        }

        logger.info("Starting server on port: {}", projectServerPort);

        AbstractServer server = new ProtobuffServer(projectServerPort, projectServices);

        try {
            server.start();
        } catch (ServerException e) {
            logger.error(e);
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                logger.error(e);
            }
        }

    }
}
