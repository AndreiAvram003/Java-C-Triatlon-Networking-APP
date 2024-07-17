package dto;

import java.io.Serializable;
import java.util.List;

public class TrialDTO implements Serializable {
    private Long id;
    private String name;

    private RefereeDTO refereeDTO;

    private List<ParticipantDTO> participants;


    public RefereeDTO getRefereeDTO() {
        return refereeDTO;
    }

    public void setRefereeDTO(RefereeDTO refereeDTO) {
        this.refereeDTO = refereeDTO;
    }

    public TrialDTO(Long id, String name, RefereeDTO refereeDTO, List<ParticipantDTO> participants) {
        this.id = id;
        this.name = name;
        this.refereeDTO = refereeDTO;
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }
}
