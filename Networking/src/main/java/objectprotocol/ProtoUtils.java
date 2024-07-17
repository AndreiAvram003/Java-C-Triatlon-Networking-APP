package objectprotocol;


import domain.Participant;
import domain.Result;
import dto.ParticipantDTO;
import dto.RefereeDTO;
import dto.ResultDTO;
import dto.TrialDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ProtoUtils {
    public static TriatlonProto.Request createLoginRequest(RefereeDTO referee) {
        TriatlonProto.RefereeDTO refereeDTO = TriatlonProto.RefereeDTO.newBuilder().setPassword(referee.getPassword()).setName(referee.getName()).build();
        TriatlonProto.Request request = TriatlonProto.Request.newBuilder().setType(TriatlonProto.Request.Type.LOGIN)
                .setRefereeDto(refereeDTO).build();
        return request;
    }

    public static TriatlonProto.Request createLogoutRequest(RefereeDTO referee) {
        TriatlonProto.RefereeDTO refereeDTO = TriatlonProto.RefereeDTO.newBuilder().setId(referee.getId()).build();
        TriatlonProto.Request request = TriatlonProto.Request.newBuilder().setType(TriatlonProto.Request.Type.LOGOUT)
                .setRefereeDto(refereeDTO).build();
        return request;
    }

    public static TriatlonProto.Request createGetParticipantsRequest(RefereeDTO referee) {
        TriatlonProto.TrialDTO trialDTO = TriatlonProto.TrialDTO.newBuilder().setId(referee.getTrialDTO().getId()).setName(referee.getTrialDTO().getName()).addAllParticipantsDto(referee.getTrialDTO().getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList())).build();
        TriatlonProto.RefereeDTO refereeDTO = TriatlonProto.RefereeDTO.newBuilder().setId(referee.getId()).setName(referee.getName()).setPassword(referee.getPassword()).setTrialDto(trialDTO).build();
        TriatlonProto.Request request = TriatlonProto.Request.newBuilder().setType(TriatlonProto.Request.Type.GET_PARTICIPANTS)
                .setRefereeDto(refereeDTO).build();
        return request;
    }

    public static TriatlonProto.Request createGetFilteredParticipantsRequest(RefereeDTO refereeDTO, TrialDTO trialDTO) {
        TriatlonProto.TrialDTO trialDTO2 = TriatlonProto.TrialDTO.newBuilder().setId(trialDTO.getId()).setName(trialDTO.getName()).addAllParticipantsDto(trialDTO.getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList())).build();
        TriatlonProto.RefereeDTO refereeDTO2 = TriatlonProto.RefereeDTO.newBuilder().setId(refereeDTO.getId()).setPassword(refereeDTO.getPassword()).setName(refereeDTO.getName()).setTrialDto(trialDTO2).build();
        TriatlonProto.Request request = TriatlonProto.Request.newBuilder().setType(TriatlonProto.Request.Type.FILTER_PARTICIPANTS)
                .setRefereeDto(refereeDTO2).build();
        return request;
    }

    public static TriatlonProto.Request getTotalPointsAtTrial(ParticipantTrialData participantTrialData){
        TriatlonProto.ParticipantDTO participantDTO = TriatlonProto.ParticipantDTO.newBuilder().setId(participantTrialData.getParticipantDTO().getId()).setName(participantTrialData.getParticipantDTO().getName()).setPoints(participantTrialData.getParticipantDTO().getPoints()).build();
        TriatlonProto.RefereeDTO refereeDTO = TriatlonProto.RefereeDTO.newBuilder().setId(participantTrialData.getTrialDTO().getRefereeDTO().getId()).setPassword(participantTrialData.getTrialDTO().getRefereeDTO().getPassword()).setName(participantTrialData.getTrialDTO().getRefereeDTO().getName()).build();
        TriatlonProto.TrialDTO trialDTO = TriatlonProto.TrialDTO.newBuilder().setId(participantTrialData.getTrialDTO().getId()).setName(participantTrialData.getTrialDTO().getName()).addAllParticipantsDto(participantTrialData.getTrialDTO().getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList())).setRefereeDto(refereeDTO).build();
        TriatlonProto.ParticipantTrialData participantTrialData1 = TriatlonProto.ParticipantTrialData.newBuilder().setParticipantDto(participantDTO).setTrialDto(trialDTO).build();
        TriatlonProto.Request request = TriatlonProto.Request.newBuilder().setType(TriatlonProto.Request.Type.POINTS_AT_TRIAL)
                .setParticipantTrialData(participantTrialData1).build();
        return request;
    }

    public static TriatlonProto.Request resultAddedRequest(ResultDTO result) {
        List<TriatlonProto.ParticipantDTO> participantDTOS = result.getTrialDTO().getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList());
        TriatlonProto.RefereeDTO refereeDTO = TriatlonProto.RefereeDTO.newBuilder().setId(result.getTrialDTO().getRefereeDTO().getId()).setPassword(result.getTrialDTO().getRefereeDTO().getPassword()).setName(result.getTrialDTO().getRefereeDTO().getName()).build();
        TriatlonProto.ParticipantDTO participantDTO = TriatlonProto.ParticipantDTO.newBuilder().setId(result.getParticipantDTO().getId()).setName(result.getParticipantDTO().getName()).setPoints(result.getParticipantDTO().getPoints()).build();
        TriatlonProto.TrialDTO trialDTO = TriatlonProto.TrialDTO.newBuilder().setId(result.getTrialDTO().getId()).setName(result.getTrialDTO().getName()).setRefereeDto(refereeDTO).addAllParticipantsDto(participantDTOS).build();
        TriatlonProto.ResultDTO resultDTO = TriatlonProto.ResultDTO.newBuilder().setParticipantDto(participantDTO).setTrialDto(trialDTO).setPoints(result.getPoints()).build();
        TriatlonProto.Request request = TriatlonProto.Request.newBuilder().setType(TriatlonProto.Request.Type.ADD_RESULT)
                .setResultDto(resultDTO).build();
        return request;
    }


    public static TriatlonProto.Response createOkResponse(RefereeDTO refereeDTO,TrialDTO trialDTO) {
        TriatlonProto.TrialDTO trialDTO2 = TriatlonProto.TrialDTO.newBuilder().setId(trialDTO.getId()).setName(trialDTO.getName()).addAllParticipantsDto(trialDTO.getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList())).build();
        TriatlonProto.RefereeDTO refereeDTO2 = TriatlonProto.RefereeDTO.newBuilder().setId(refereeDTO.getId()).setPassword(refereeDTO.getPassword()).setName(refereeDTO.getName()).setTrialDto(trialDTO2).build();

        TriatlonProto.Response response = TriatlonProto.Response.newBuilder()
                .setType(TriatlonProto.Response.Type.OK).setRefereeDto(refereeDTO2).build();
        return response;
    }

    public static TriatlonProto.Response createOkResponse(ResultDTO resultDTO){
        TriatlonProto.ParticipantDTO participantDTO = TriatlonProto.ParticipantDTO.newBuilder().setId(resultDTO.getParticipantDTO().getId()).setName(resultDTO.getParticipantDTO().getName()).setPoints(resultDTO.getParticipantDTO().getPoints()).build();
        TriatlonProto.TrialDTO trialDTO = TriatlonProto.TrialDTO.newBuilder().setId(resultDTO.getTrialDTO().getId()).setName(resultDTO.getTrialDTO().getName()).addAllParticipantsDto(resultDTO.getTrialDTO().getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList())).build();
        TriatlonProto.ResultDTO resultDTO2 = TriatlonProto.ResultDTO.newBuilder().setId(resultDTO.getId()).setParticipantDto(participantDTO).setTrialDto(trialDTO).setPoints(resultDTO.getPoints()).build();
        TriatlonProto.Response response = TriatlonProto.Response.newBuilder()
                .setType(TriatlonProto.Response.Type.OK).setResultDto(resultDTO2).build();
        return response;
    }

    public static TriatlonProto.Response createErrorResponse(String text) {
        TriatlonProto.Response response = TriatlonProto.Response.newBuilder()
                .setType(TriatlonProto.Response.Type.ERROR)
                .setError(text).build();
        return response;
    }


    public static String getError(TriatlonProto.Response response) {
        String errorMessage = response.getError();
        return errorMessage;
    }

    public static List<ParticipantDTO> getParticipants(TriatlonProto.Response response) {
        List<TriatlonProto.ParticipantDTO> participants = response.getParticipantsDtoList();
        List<ParticipantDTO> participantsDTO = participants.stream()
                .map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints()))
                .collect(Collectors.toList());
        return participantsDTO;
    }

    public static ResultDTO getResult(TriatlonProto.Response response) {
        TriatlonProto.ResultDTO resultDTO = response.getResultDto();
        ParticipantDTO participantDTO = new ParticipantDTO(resultDTO.getParticipantDto().getId(), resultDTO.getParticipantDto().getName(), resultDTO.getParticipantDto().getPoints());
        RefereeDTO refereeDTO = new RefereeDTO(resultDTO.getTrialDto().getRefereeDto().getId(), resultDTO.getTrialDto().getRefereeDto().getPassword(), resultDTO.getTrialDto().getRefereeDto().getName(), null);
        TrialDTO trialDTO = new TrialDTO(resultDTO.getTrialDto().getId(), resultDTO.getTrialDto().getName(), refereeDTO, resultDTO.getTrialDto().getParticipantsDtoList().stream()
                .map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints()))
                .collect(Collectors.toList()));
        refereeDTO.setTrialDTO(trialDTO);

        return new ResultDTO(resultDTO.getId(), participantDTO, trialDTO, resultDTO.getPoints());
    }

    public static Integer getPoints(TriatlonProto.Response response) {
        return response.getPoints();
    }

    public static RefereeDTO getReferee(TriatlonProto.Request request) {
        TrialDTO trialDTO = new TrialDTO(request.getRefereeDto().getTrialDto().getId(), request.getRefereeDto().getTrialDto().getName(), null, request.getRefereeDto().getTrialDto().getParticipantsDtoList().stream()
                .map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints()))
                .collect(Collectors.toList()));
        RefereeDTO refereeDTO = new RefereeDTO(request.getRefereeDto().getId(), request.getRefereeDto().getPassword(), request.getRefereeDto().getName(), trialDTO);
        trialDTO.setRefereeDTO(refereeDTO);
        return refereeDTO;
    }

    public static List<ParticipantDTO> getParticipants(TriatlonProto.Request request) {
        List<TriatlonProto.ParticipantDTO> participants = request.getRefereeDto().getTrialDto().getParticipantsDtoList();
        List<ParticipantDTO> participantsDTO = participants.stream()
                .map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints()))
                .collect(Collectors.toList());
        return participantsDTO;
    }

    public static ResultDTO getResult(TriatlonProto.Request request) {
        TriatlonProto.ResultDTO resultDTO = request.getResultDto();
        ParticipantDTO participantDTO = new ParticipantDTO(resultDTO.getParticipantDto().getId(), resultDTO.getParticipantDto().getName(), resultDTO.getParticipantDto().getPoints());
        RefereeDTO refereeDTO = new RefereeDTO(resultDTO.getTrialDto().getRefereeDto().getId(), resultDTO.getTrialDto().getRefereeDto().getPassword(), resultDTO.getTrialDto().getRefereeDto().getName(), null);
        TrialDTO trialDTO = new TrialDTO(resultDTO.getTrialDto().getId(), resultDTO.getTrialDto().getName(), refereeDTO, resultDTO.getTrialDto().getParticipantsDtoList().stream()
                .map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints()))
                .collect(Collectors.toList()));
        refereeDTO.setTrialDTO(trialDTO);

        return new ResultDTO(resultDTO.getId(), participantDTO, trialDTO, resultDTO.getPoints());
    }


    public static TriatlonProto.Response getParticipants(List<Participant> participants) {
        TriatlonProto.Response.Builder response = TriatlonProto.Response.newBuilder()
                .setType(TriatlonProto.Response.Type.PARTICIPANTS);
        for (Participant participant : participants) {
            TriatlonProto.ParticipantDTO participantDTO = TriatlonProto.ParticipantDTO.newBuilder().setId(participant.getId()).setName(participant.getName()).setPoints(participant.getPoints()).build();
            response.addParticipantsDto(participantDTO);
        }

        return response.build();
    }


    public static ParticipantTrialData getParticipantTrialData(TriatlonProto.Request request) {
        TriatlonProto.ParticipantTrialData participantTrialData = request.getParticipantTrialData();
        ParticipantDTO participantDTO = new ParticipantDTO(participantTrialData.getParticipantDto().getId(), participantTrialData.getParticipantDto().getName(), participantTrialData.getParticipantDto().getPoints());
        RefereeDTO refereeDTO = new RefereeDTO(participantTrialData.getTrialDto().getRefereeDto().getId(), participantTrialData.getTrialDto().getRefereeDto().getPassword(), participantTrialData.getTrialDto().getRefereeDto().getName(), null);
        TrialDTO trialDTO = new TrialDTO(participantTrialData.getTrialDto().getId(), participantTrialData.getTrialDto().getName(), refereeDTO, participantTrialData.getTrialDto().getParticipantsDtoList().stream()
                .map(p -> new ParticipantDTO(p.getId(), p.getName(), p.getPoints()))
                .collect(Collectors.toList()));
        refereeDTO.setTrialDTO(trialDTO);
        return new ParticipantTrialData(participantDTO, trialDTO);

    }

    public static TriatlonProto.Response resultAdded(Result result) {
        TriatlonProto.Response.Builder response = TriatlonProto.Response.newBuilder().setType(TriatlonProto.Response.Type.RESULT_ADDED);
/*        TriatlonProto.ParticipantDTO participantDTO = TriatlonProto.ParticipantDTO.newBuilder().setId(result.getParticipant().getId()).setName(result.getParticipant().getName()).setPoints(result.getParticipant().getPoints()).build();
        TriatlonProto.RefereeDTO refereeDTO = TriatlonProto.RefereeDTO.newBuilder().setId(result.getTrial().getReferee().getId()).setPassword(result.getTrial().getReferee().getPassword()).setName(result.getTrial().getReferee().getName()).build();
        TriatlonProto.TrialDTO trialDTO = TriatlonProto.TrialDTO.newBuilder().setId(result.getTrial().getId()).setName(result.getTrial().getName()).setRefereeDto(refereeDTO).addAllParticipantsDto(result.getTrial().getParticipants().stream()
                .map(p -> TriatlonProto.ParticipantDTO.newBuilder().setId(p.getId()).setName(p.getName()).setPoints(p.getPoints()).build())
                .collect(Collectors.toList())).build();
        refereeDTO.toBuilder().setTrialDto(trialDTO);
        TriatlonProto.ResultDTO resultDTO = TriatlonProto.ResultDTO.newBuilder().setId(result.getId()).setParticipantDto(participantDTO).setTrialDto(trialDTO).setPoints(result.getResult()).build();
        response.setResultDto(resultDTO);*/
        return response.build();
    }

    public static TriatlonProto.Response createPointsResponse(int totalPointsAtTrial) {
        TriatlonProto.Response response = TriatlonProto.Response.newBuilder()
                .setType(TriatlonProto.Response.Type.POINTS_AT_TRIAL)
                .setPoints(totalPointsAtTrial).build();
        return response;
    }
}
