package dto;

import domain.Participant;
import domain.Referee;
import domain.Result;
import domain.Trial;

import java.util.List;


public class DTOUtils {
    public static Participant getFromDTO(ParticipantDTO participantDTO){
        Long id = participantDTO.getId();
        String name=participantDTO.getName();
        int points = participantDTO.getPoints();
        return new Participant(id,name, points);
    }
    public static ParticipantDTO getDTO(Participant participant){
        Long id = participant.getId();
        String name = participant.getName();
        int points = participant.getPoints();
        return new ParticipantDTO(id, name, points);
    }

    public static Result getFromDTO(ResultDTO resultDTO){

        Long id = resultDTO.getId();
        Participant participant = getFromDTO(resultDTO.getParticipantDTO());
        TrialDTO trialDTO = resultDTO.getTrialDTO();
        Trial trial = getFromDTO(trialDTO);
        int points = resultDTO.getPoints();
        return new Result(id,participant, trial, points);
    }

    public static ResultDTO getDTO(Result result){
        Long id = result.getId();
        ParticipantDTO participantDTO = getDTO(result.getParticipant());
        TrialDTO trialDTO = getDTO(result.getTrial());
        int points = result.getResult();
        return new ResultDTO(id, participantDTO, trialDTO, points);
    }

    public static Referee getFromDTO(RefereeDTO refereeDTO){
        Long id = refereeDTO.getId();
        String password = refereeDTO.getPassword();
        String name = refereeDTO.getName();
        TrialDTO trialDTO = refereeDTO.getTrialDTO();
        List<ParticipantDTO> participantDTOS = trialDTO.getParticipants();
        List<Participant> participants = getFromDTO(participantDTOS);
        Trial trial = new Trial(trialDTO.getId(), null, participants, trialDTO.getName());
        Referee referee = new Referee(id, name, password, trial);
        trial.setReferee(referee);
        return  referee;
    }

    public static RefereeDTO getDTO(Referee referee){
        Long id = referee.getId();
        String password = referee.getPassword();
        String name = referee.getName();
        RefereeDTO refereeDTO = new RefereeDTO(id, password, name, null);
        List<ParticipantDTO> participantDTOS = getDTO(referee.getTrial().getParticipants());
        TrialDTO trialDTO = new TrialDTO(referee.getTrial().getId(),referee.getTrial().getName(),refereeDTO,participantDTOS);
        refereeDTO.setTrialDTO(trialDTO);

        return refereeDTO;
    }

    public static TrialDTO getDTO(Trial trial){
        Long id = trial.getId();
        String name = trial.getName();
        List<Participant> participants = trial.getParticipants();
        List<ParticipantDTO> participantDTOS = getDTO(participants);
        TrialDTO trialDTO = new TrialDTO(id, name, null, participantDTOS);
        RefereeDTO refereeDTO = new RefereeDTO(trial.getReferee().getId(), trial.getReferee().getPassword(), trial.getReferee().getName(), trialDTO);
        trialDTO.setRefereeDTO(refereeDTO);
        return trialDTO;
    }

    public static Trial getFromDTO(TrialDTO trialDTO){
        Long id = trialDTO.getId();
        String name = trialDTO.getName();
        RefereeDTO refereeDTO = trialDTO.getRefereeDTO();
        Referee referee = new Referee(refereeDTO.getId(), refereeDTO.getName(), refereeDTO.getPassword(), null);
        List<ParticipantDTO> participantDTOS = trialDTO.getParticipants();
        List<Participant> participants = getFromDTO(participantDTOS);
        Trial trial = new Trial(id, referee, participants, name);
        referee.setTrial(trial);
        return trial;
    }

    public static ParticipantDTO[] getDTO(Participant[] participants){
        ParticipantDTO[] participantDTOS = new ParticipantDTO[participants.length];
        for(int i=0;i<participants.length;i++){
            participantDTOS[i]=getDTO(participants[i]);
        }
        return participantDTOS;
    }

    public static List<ParticipantDTO> getDTO(List<Participant> participants){
        ParticipantDTO[] participantDTOS = new ParticipantDTO[participants.size()];
        for(int i=0;i<participants.size();i++){
            participantDTOS[i]=getDTO(participants.get(i));
        }
        return List.of(participantDTOS);
    }

    public static Participant[] getFromDTO(ParticipantDTO[] participants){
        Participant[] participants1 = new Participant[participants.length];
        for(int i=0;i<participants.length;i++){
            participants1[i]=getFromDTO(participants[i]);
        }
        return participants1;
    }

    public static List<Participant> getFromDTO(List<ParticipantDTO> participants){
        Participant[] participants1 = new Participant[participants.size()];
        for(int i=0;i<participants.size();i++){
            participants1[i]=getFromDTO(participants.get(i));
        }
        return List.of(participants1);
    }
}
