package domain;

public class Referee extends Entity<Long>{

    private String name;

    private String password;

    private Trial trial;


    public Referee(Long aLong, String name, String password, Trial trial) {
        super(aLong);
        this.name = name;
        this.password = password;
        this.trial = trial;
    }

    public Referee(Long aLong) {
        super(aLong);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }
}
