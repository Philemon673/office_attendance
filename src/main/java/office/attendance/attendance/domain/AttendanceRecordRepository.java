package office.attendance.attendance.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRecordRepository {
    /** Returns false if the employee already has a record for that date. */
    boolean insertIfAbsent(AttendanceRecord record);

    Optional<AttendanceRecord> findByEmployeeAndDate(UUID employeeId, LocalDate date);

    /** Returns false if the record was already checked out. */
    boolean markCheckedOut(AttendanceRecord record);

    List<AttendanceRecord> findByEmployeeBetween(UUID employeeId, LocalDate from, LocalDate to);
}
