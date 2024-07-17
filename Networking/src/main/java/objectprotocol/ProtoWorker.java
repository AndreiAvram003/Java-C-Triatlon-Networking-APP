package objectprotocol;

import domain.Participant;
import domain.Referee;
import domain.Trial;
import dto.*;
import org.example.IRefereeObserver;
import org.example.ParticipantAlert;
import org.example.Service;

import java.io.*;
import java.net.Socket;
import java.util.List;


public class ProtoWorker implements Runnable, IRefereeObserver {
    private Service server;
    private Socket connection;

    private InputStream input;
    private OutputStream output;
    private volatile boolean connected;
    public ProtoWorker(Service server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=connection.getOutputStream() ;//new ObjectOutputStream(connection.getOutputStream());
            input=connection.getInputStream(); //new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while(connected){
            try {
                // Object request=input.readObject();
                System.out.println("Waiting requests ...");
                TriatlonProto.Request request=TriatlonProto.Request.parseDelimitedFrom(input);
                System.out.println("Request received: "+request);
                TriatlonProto.Response response=handleRequest(request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
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

    private TriatlonProto.Response handleRequest(TriatlonProto.Request request){
        TriatlonProto.Response response=null;
        switch (request.getType()){
            case LOGIN:{
                System.out.println("Login request ...");
                RefereeDTO refereeDTO=ProtoUtils.getReferee(request);

                try {
                    Referee referee = server.login(refereeDTO.getName(),refereeDTO.getPassword(), this);
                    RefereeDTO rdto = DTOUtils.getDTO(referee);
                    return ProtoUtils.createOkResponse(rdto,rdto.getTrialDTO());
                } catch (Exception e) {
                    connected=false;
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case LOGOUT:{
                System.out.println("Logout request");
                RefereeDTO refereeDTO = ProtoUtils.getReferee(request);
                try {
                    server.logout(refereeDTO.getName(), refereeDTO.getPassword(),this);
                    connected=false;
                    return ProtoUtils.createOkResponse(refereeDTO,refereeDTO.getTrialDTO());

                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GET_PARTICIPANTS:{
                System.out.println("GetParticipantsRequest ...");
                RefereeDTO refereeDTO=ProtoUtils.getReferee(request);
                Referee referee= DTOUtils.getFromDTO(refereeDTO);

                try {
                    List<Participant> participants=server.getParticipants(referee);
                    return ProtoUtils.getParticipants(participants);
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case FILTER_PARTICIPANTS:{
                System.out.println("FilterParticipantsRequest ...");
                RefereeDTO refereeDTO=ProtoUtils.getReferee(request);
                Referee referee= DTOUtils.getFromDTO(refereeDTO);

                try {
                    List<Participant> participants=server.getParticipantsWithPointsAtTrial(referee.getTrial());
                    return ProtoUtils.getParticipants(participants);
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }

            }

            case ADD_RESULT:{
                System.out.println("AddResultRequest ...");
                ResultDTO resultDTO=ProtoUtils.getResult(request);
                Participant participant = DTOUtils.getFromDTO(resultDTO.getParticipantDTO());
                Trial trial = DTOUtils.getFromDTO(resultDTO.getTrialDTO());
                int points = resultDTO.getPoints();

                try {
                    server.addResult(participant,trial,points);
                    return ProtoUtils.createOkResponse(resultDTO);
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }

            }

            case POINTS_AT_TRIAL:{
                System.out.println("PointsAtTrialRequest");
                ParticipantTrialData participantTrialData = ProtoUtils.getParticipantTrialData(request);
                Participant participant = DTOUtils.getFromDTO(participantTrialData.getParticipantDTO());
                Trial trial = DTOUtils.getFromDTO(participantTrialData.getTrialDTO());
                try{
                    int points = server.getTotalPointsAtTrial(participant,trial);
                    return ProtoUtils.createPointsResponse(points);
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
        }
        return response;
    }

    private void sendResponse(TriatlonProto.Response response) throws IOException{
        System.out.println("sending response "+response);
        response.writeDelimitedTo(output);
        //output.writeObject(response);
        output.flush();
    }

    @Override
    public void update() throws Exception {
        System.out.println("trimit update");
        TriatlonProto.Response response = ProtoUtils.resultAdded(null);
        sendResponse(response);
    }
}
