package office.attendance.roster.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RosterEntryRepository {

    /** Insert, or replace the shift if the employee already has an entry that day. */
    void assign(RosterEntry entry);

    Optional<RosterEntry> findByEmployeeAndDate(UUID employeeId, LocalDate date);

    List<RosterEntry> findBetween(LocalDate from, LocalDate to);

    List<RosterEntry> findByEmployeeBetween(UUID employeeId, LocalDate from, LocalDate to);
}