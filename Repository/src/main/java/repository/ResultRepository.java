package repository;

import domain.Participant;
import domain.Result;

import java.util.List;

public interface ResultRepository extends Repository<Result, Long> {

    int getTotalPointsAtTrial(Long id, Long id1);

    List<Participant> getParticipantsWithPointsAtTrial(Long id);

}
