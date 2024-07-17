package dto;

import java.io.Serializable;

public class ResultDTO implements Serializable {
    private Long id;
    private ParticipantDTO participantDTO;
    private TrialDTO trialDTO;
    private int points;

    public ResultDTO(Long id, ParticipantDTO participantDTO, TrialDTO trialDTO, int points) {
        this.id = id;
        this.participantDTO = participantDTO;
        this.trialDTO = trialDTO;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public ParticipantDTO getParticipantDTO() {
        return participantDTO;
    }

    public TrialDTO getTrialDTO() {
        return trialDTO;
    }

    public int getPoints() {
        return points;
    }
}
