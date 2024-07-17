package dto;

import domain.Trial;

import java.io.Serializable;


public class RefereeDTO implements Serializable{
    private Long id;
    private String password;

    private TrialDTO trialDTO;

    private String name;

    public RefereeDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RefereeDTO(Long id, String password, String name, TrialDTO trialDTO) {
        this.id = id;
        this.password = password;
        this.trialDTO = trialDTO;
        this.name = name;
    }

    public TrialDTO getTrialDTO() {
        return trialDTO;
    }

    public void setTrialDTO(TrialDTO trialDTO) {
        this.trialDTO = trialDTO;
    }
}
