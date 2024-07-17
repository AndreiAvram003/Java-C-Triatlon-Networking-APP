package domain;

import java.util.List;

public class Trial extends Entity<Long>{

    private Referee referee;

    private List<Participant> participants;
    private String name;


    @Override
    public String toString() {
        return "Trial{" +
                "referee=" + referee +
                ", participants=" + participants +
                ", name='" + name + '\'' +
                '}';
    }

    public Trial(Long aLong, Referee referee, List<Participant> participants, String name) {
        super(aLong);
        this.referee = referee;
        this.participants = participants;
        this.name = name;
    }
    public Trial(Referee referee, List<Participant> participants, String name) {
        super(null);
        this.referee = referee;
        this.participants = participants;
        this.name = name;
    }

    public Trial(Long trialId) {
        this(trialId,null,null,null);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Referee getReferee() {
        return referee;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
