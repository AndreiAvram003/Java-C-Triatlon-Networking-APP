package objectprotocol;


import domain.Participant;
import domain.Referee;
import domain.Result;
import domain.Trial;
import org.example.IRefereeObserver;
import dto.DTOUtils;
import dto.ParticipantDTO;
import dto.TrialDTO;
import dto.ResultDTO;
import dto.RefereeDTO;
import org.example.Service;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientWorker implements Runnable, IRefereeObserver {
    private Service server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientWorker(Service server, Socket connection) {
        this.server = server;
        this.connection = connection;

        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        while(connected){
            try {
                Object request = input.readObject();
                Request r= (Request)request;
                System.out.println(r.type());
                System.out.println("request received");
                Response response = handleRequest((Request)request);
                if (response != null){
                    System.out.println("not null");
                    sendResponse(response);
                }
            } catch (EOFException eof) {
                // Client has closed the connection
                connected = false;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }
    private void sendResponse(Response response) throws IOException {
        output.writeObject(response);
        output.flush();
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private Response handleRequest(Request request) {
        if (request.type() == RequestType.LOGIN) {
            RefereeDTO dto = (RefereeDTO)request.data();
            try {
                Referee referee1 = server.login(dto.getName(),dto.getPassword(), this);
                RefereeDTO rdto = DTOUtils.getDTO(referee1);
                return new Response.Builder().type(ResponseType.OK).data(rdto).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.LOGOUT) {
            RefereeDTO dto = (RefereeDTO)request.data();
            try {

                server.logout(dto.getName(), dto.getPassword(), this);
                connected = false;
                return okResponse;
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_PARTICIPANTS) {
            RefereeDTO dto = (RefereeDTO)request.data();
            Referee referee = DTOUtils.getFromDTO(dto);
            try {
                List<Participant> participants = server.getParticipants(referee);
                List<ParticipantDTO> dtos = DTOUtils.getDTO(participants);
                return new Response.Builder().type(ResponseType.PARTICIPANTS).data(dtos).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.FILTERED_PARTICIPANTS) {
            TrialDTO dto = (TrialDTO)request.data();
            Trial trial = DTOUtils.getFromDTO(dto);
            try {
                List<Participant> participants = server.getParticipantsWithPointsAtTrial(trial);
                List<ParticipantDTO> dtos = DTOUtils.getDTO(participants);
                return new Response.Builder().type(ResponseType.FILTERED_PARTICIPANTS).data(dtos).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if(request.type() == RequestType.POINTS_AT_TRIAL){
            ParticipantTrialData data = (ParticipantTrialData) request.data();
            ParticipantDTO dto = data.getParticipantDTO();
            Participant participant = DTOUtils.getFromDTO(dto);
            TrialDTO trialDTO = data.getTrialDTO();
            Trial trial = DTOUtils.getFromDTO(trialDTO);
            try {
                int points = server.getTotalPointsAtTrial
                        (participant, trial);
                return new Response.Builder().type(ResponseType.POINTS_AT_TRIAL).data(points).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.ADD_RESULT) {
            ResultDTO dto = (ResultDTO)request.data();
            Result result = DTOUtils.getFromDTO(dto);
            try {
                Result result1 = server.addResult(result.getParticipant(), result.getTrial(), result.getResult());
                if(result1 == null){
                    return new Response.Builder().type(ResponseType.ERROR).data(null).build();
                }
                ResultDTO rdto = DTOUtils.getDTO(result1);
                return new Response.Builder().type(ResponseType.OK).data(rdto).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        return null;
    }

    @Override
    public void update() throws Exception {
        System.out.println("trimit update");
        Response response = new Response.Builder().type(ResponseType.RESULT_ADDED).data(null).build();

        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new Exception("Error sending response "+e);
        }
    }

}
