package domain;

public class Participant extends Entity<Long>{
    private String name;

    private Integer points;


    public Participant(Long aLong,String name, Integer points) {
        super(aLong);
        this.name = name;
        this.points = points;
    }

    public Participant(String name, Integer points) {
        super(null);
        this.name = name;
        this.points = points;
    }

    public Participant(Long participantId) {
        this(participantId,null,0);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "domain.Participant{" +
                "id=" + super.getId() + '\'' +
                "name='" + name + '\'' +
                ", points=" + points +
                '}';
    }
}
