

package objectprotocol;

import dto.ParticipantDTO;
import dto.TrialDTO;

import java.io.Serializable;

public class ParticipantTrialData implements Serializable {
    private ParticipantDTO participantDTO;
    private TrialDTO trialDTO;

    public ParticipantTrialData(ParticipantDTO participantDTO, TrialDTO trialDTO) {
        this.participantDTO = participantDTO;
        this.trialDTO = trialDTO;
    }

    public ParticipantDTO getParticipantDTO() {
        return participantDTO;
    }

    public TrialDTO getTrialDTO() {
        return trialDTO;
    }
}
