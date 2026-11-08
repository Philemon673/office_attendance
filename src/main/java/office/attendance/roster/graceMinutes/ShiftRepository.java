package office.attendance.roster.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository {

    void insert(Shift shift);

    Optional<Shift> findById(UUID id);

    List<Shift> findAll();

    boolean existsByName(String name);
}