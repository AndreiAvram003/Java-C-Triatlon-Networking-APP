package dto;

import java.io.Serializable;


public class ParticipantDTO implements Serializable{
    private Long id;
    private String name;
    private int points;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ParticipantDTO(Long id, String name, int points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

}
