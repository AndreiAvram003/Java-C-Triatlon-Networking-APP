package domain;

public class Result extends Entity<Long>{

    private Participant participant;

    private Trial trial;

    private Integer result;


    public Result(Long aLong, Participant participant, Trial trial,Integer result) {
        super(aLong);
        this.participant = participant;
        this.trial = trial;
        this.result = result;
    }

    public Result(Participant participant, Trial trial,Integer result) {
        super(null);
        this.participant = participant;
        this.trial = trial;
        this.result = result;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
