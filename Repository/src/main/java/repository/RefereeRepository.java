package repository;

import domain.Referee;

import java.util.Optional;

public interface RefereeRepository extends Repository<Referee,Long> {
    Referee getByUsernameAndPassword(String username, String password);
}
