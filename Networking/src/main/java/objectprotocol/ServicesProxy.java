package objectprotocol;

import domain.Participant;
import domain.Referee;
import domain.Result;
import domain.Trial;
import dto.*;
import org.example.IRefereeObserver;
import org.example.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServicesProxy implements Service {
    private String host;
    private int port;

    private IRefereeObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;


    private BlockingQueue<Response> responses;
    private volatile boolean finished;

    public ServicesProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingDeque<>();
    }



    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws Exception {
        if (output == null) {
            initializeConnection(); // Inițializați conexiunea dacă output este null
        }
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try{
            response = responses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    @Override
    public Referee login(String username, String password, IRefereeObserver client) throws Exception {
        initializeConnection();
        RefereeDTO dto = new RefereeDTO(null,password,username,null);
        this.client = client;
        System.out.println(client);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(dto).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            RefereeDTO refereeDTO = (RefereeDTO) response.data();
            TrialDTO trialDTO  = refereeDTO.getTrialDTO();
            Trial trial = DTOUtils.getFromDTO(trialDTO);
            Referee referee = new Referee(null,refereeDTO.getPassword(),refereeDTO.getName(),trial);
            referee.setId(refereeDTO.getId());
            this.client = client;
            System.out.println(client);
            return referee;
        } else {
            closeConnection();
            return null;
        }
    }

    @Override
    public void logout(String username, String password, IRefereeObserver client) throws Exception {
        RefereeDTO refereeDTO = new RefereeDTO(null,password,username,null);
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(refereeDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new Exception(response.data().toString());
        }
        closeConnection();
    }

    @Override
    public List<Participant> getParticipants(Referee referee) throws Exception {
        RefereeDTO refereeDTO = DTOUtils.getDTO(referee);
        Request request = new Request.Builder().type(RequestType.GET_PARTICIPANTS).data(refereeDTO).build();
        sendRequest(request);
        Response response = readResponse();
        this.client = client;

        if (response.type() == ResponseType.PARTICIPANTS) {
           List<ParticipantDTO> participantDTOS = (List<ParticipantDTO>) response.data();

            return DTOUtils.getFromDTO(participantDTOS);
        }
        throw new Exception("There was an error");
    }

    @Override
    public Result addResult(Participant participant, Trial trial, int points) throws Exception {


        Result result = new Result(participant, trial, points);
        ResultDTO resultDTO = DTOUtils.getDTO(result);
        Request request = new Request.Builder().type(RequestType.ADD_RESULT).data(resultDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            return null;
        }

        return result;
    }

    @Override
    public int getTotalPointsAtTrial(Participant participant, Trial trial) throws Exception {

        ParticipantDTO participantDTO = DTOUtils.getDTO(participant);
        TrialDTO trialDTO = DTOUtils.getDTO(trial);
        ParticipantTrialData participantTrialData = new ParticipantTrialData(participantDTO,trialDTO);
        Request request = new Request.Builder().type(RequestType.POINTS_AT_TRIAL).data(participantTrialData).build();
        sendRequest(request);
        this.client = client;
        Response response = readResponse();
        if(response.type() == ResponseType.POINTS_AT_TRIAL){
            return (int) response.data();
        }
        throw new Exception("There was an error");
    }

    @Override
    public List<Participant> getParticipantsWithPointsAtTrial(Trial trial) throws Exception {
        TrialDTO trialDTO = DTOUtils.getDTO(trial);
        Request request = new Request.Builder().type(RequestType.FILTERED_PARTICIPANTS).data(trialDTO).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.FILTERED_PARTICIPANTS) {
            List<ParticipantDTO> participantDTOS = (List<ParticipantDTO>) response.data();

            return DTOUtils.getFromDTO(participantDTOS);
        }
        throw new Exception("There was an error");
    }



    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    //logger.info("Response received");
                    Response r= (Response) response;
                    System.out.println(r.type());
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            responses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (SocketException e){
                    finished=true;
                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    private void handleUpdate(Response response) {
        try {
            System.out.println("apelez update controller");
            System.out.println(client);
            client.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.RESULT_ADDED;
    }
}
