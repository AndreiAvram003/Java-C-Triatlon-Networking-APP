package repository;

import domain.Entity;

import java.util.List;
import java.util.Optional;

public interface Repository <T extends Entity<ID>,ID> {
    Optional<T> save(T obj);
    Optional<T> getById(ID id);
    Optional<T> update(T obj);
    Optional<T> deleteById(ID id);

    List<T> getAll();


}
