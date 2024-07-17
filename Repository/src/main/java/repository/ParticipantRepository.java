package repository;

import domain.Participant;

import java.util.List;

public interface ParticipantRepository extends Repository<Participant,Long> {



    List<Participant> getAllSorted();
}
