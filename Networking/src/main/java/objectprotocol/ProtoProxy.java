package objectprotocol;

import domain.Participant;
import domain.Referee;
import domain.Result;
import domain.Trial;
import dto.*;
import org.example.IRefereeObserver;
import org.example.Service;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Ref;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtoProxy implements Service {
    private String host;
    private int port;

    private IRefereeObserver client;
    private OutputStream output;

    private InputStream input;
    private Socket connection;

    private BlockingQueue<TriatlonProto.Response> responses;
    private volatile boolean finished;

    public ProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingDeque<>();
    }

    private void closeConnection() {
        finished = true;
        try {
            output.close();
            input.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(TriatlonProto.Request request) throws Exception {
        if (output == null) {
            initializeConnection();
        }
        try {
            request.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending request: " + e.getMessage());
        }
    }

    private TriatlonProto.Response readResponse() {
        TriatlonProto.Response response = null;
        try {
            response = responses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            input = connection.getInputStream();

            output.flush();
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
        RefereeDTO refereeDTO = new RefereeDTO(null, password, username, null);
        TriatlonProto.Request request = ProtoUtils.createLoginRequest(refereeDTO);
        sendRequest(request);
        TriatlonProto.Response response = readResponse();

        if (response.getType() == TriatlonProto.Response.Type.OK) {
            TriatlonProto.RefereeDTO receivedRefereeDTO = response.getRefereeDto();
            List<TriatlonProto.ParticipantDTO> participantDTOs = receivedRefereeDTO.getTrialDto().getParticipantsDtoList();
            List<ParticipantDTO> participants = participantDTOs.stream().map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints())).toList();
            TrialDTO trialDTO = new TrialDTO(receivedRefereeDTO.getTrialDto().getId(),receivedRefereeDTO.getTrialDto().getName(),null,participants);
            RefereeDTO refDto = new RefereeDTO(receivedRefereeDTO.getId(), receivedRefereeDTO.getPassword(), receivedRefereeDTO.getName(),trialDTO);
            trialDTO.setRefereeDTO(refDto);
            Referee referee = DTOUtils.getFromDTO(refDto);
            this.client = client;
            return referee;
        } else {
            closeConnection();
            return null;
        }
    }

    @Override
    public void logout(String username, String password, IRefereeObserver client) throws Exception {
        RefereeDTO refereeDTO = new RefereeDTO(null, password, username, null);
        TriatlonProto.Request request = ProtoUtils.createLogoutRequest(refereeDTO);
        sendRequest(request);
        TriatlonProto.Response response = readResponse();
        if (response.getType() == TriatlonProto.Response.Type.ERROR) {
            throw new Exception(response.getError());
        }
        closeConnection();
    }

    @Override
    public List<Participant> getParticipants(Referee referee) throws Exception {
        RefereeDTO refereeDTO = DTOUtils.getDTO(referee);
        TriatlonProto.Request request = ProtoUtils.createGetParticipantsRequest(refereeDTO);
        sendRequest(request);
        TriatlonProto.Response response = readResponse();

        if (response.getType() == TriatlonProto.Response.Type.PARTICIPANTS) {
            List<TriatlonProto.ParticipantDTO> participantDTOs = response.getParticipantsDtoList();
            List<ParticipantDTO> participants = participantDTOs.stream().map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints())).toList();
            return DTOUtils.getFromDTO(participants);
        }
        throw new Exception("Error retrieving participants");
    }

    @Override
    public Result addResult(Participant participant, Trial trial, int points) throws Exception {
        Result result = new Result(participant, trial, points);
        ResultDTO resultDTO = DTOUtils.getDTO(result);
        TriatlonProto.Request request = ProtoUtils.resultAddedRequest(resultDTO);
        sendRequest(request);
        TriatlonProto.Response response = readResponse();
        if (response.getType() == TriatlonProto.Response.Type.ERROR) {
            return null;
        }
        return result;
    }

    @Override
    public int getTotalPointsAtTrial(Participant participant, Trial trial) throws Exception {
        ParticipantDTO participantDTO = DTOUtils.getDTO(participant);
        TrialDTO trialDTO = DTOUtils.getDTO(trial);
        ParticipantTrialData participantTrialData = new ParticipantTrialData(participantDTO, trialDTO);
        TriatlonProto.Request request = ProtoUtils.getTotalPointsAtTrial(participantTrialData);
        sendRequest(request);
        TriatlonProto.Response response = readResponse();
        if (response.getType() == TriatlonProto.Response.Type.POINTS_AT_TRIAL) {
            return response.getPoints();
        }
        throw new Exception("Error retrieving total points");
    }

    @Override
    public List<Participant> getParticipantsWithPointsAtTrial(Trial trial) throws Exception {
        TrialDTO trialDTO = DTOUtils.getDTO(trial);
        RefereeDTO refereeDTO = trialDTO.getRefereeDTO();
        TriatlonProto.Request request = ProtoUtils.createGetFilteredParticipantsRequest(refereeDTO,trialDTO);
        sendRequest(request);
        TriatlonProto.Response response = readResponse();

        if (response.getType() == TriatlonProto.Response.Type.PARTICIPANTS) {
            List<TriatlonProto.ParticipantDTO> participantDTOs = response.getParticipantsDtoList();
            List<ParticipantDTO> participants = participantDTOs.stream().map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints())).toList();
            return DTOUtils.getFromDTO(participants);
        }
        throw new Exception("Error retrieving participants");
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    System.out.println("Reading response");
                    TriatlonProto.Response response = TriatlonProto.Response.parseDelimitedFrom(connection.getInputStream());
                    System.out.println("Response received: " + response);
                    if (response.getType() == TriatlonProto.Response.Type.RESULT_ADDED) {
                        handleUpdate();
                    } else {
                        try{
                        responses.put(response);}
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                } catch (SocketException e) {
                    finished = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void handleUpdate() {
        try {
            client.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
