package office.attendance.roster.application;

import office.attendance.employee.application.EmployeeService;
import office.attendance.employee.domain.Employee;
import office.attendance.roster.domain.RosterEntry;
import office.attendance.roster.domain.RosterEntryRepository;
import office.attendance.roster.domain.Shift;
import office.attendance.shared.error.BadRequestException;
import office.attendance.shared.error.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Service
public class RosterService {

    private static final int MAX_DATES_PER_REQUEST = 62;
    private static final long MAX_RANGE_DAYS = 93;

    private final RosterEntryRepository repository;
    private final ShiftService shiftService;
    private final EmployeeService employeeService;

    public RosterService(RosterEntryRepository repository,
                         ShiftService shiftService,
                         EmployeeService employeeService) {
        this.repository = repository;
        this.shiftService = shiftService;
        this.employeeService = employeeService;
    }

    @Transactional
    public List<RosterEntry> assign(UUID employeeId, UUID shiftId, Collection<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            throw new BadRequestException("At least one date is required");
        }
        Set<LocalDate> uniqueDates = new TreeSet<>(dates);
        if (uniqueDates.size() > MAX_DATES_PER_REQUEST) {
            throw new BadRequestException("Too many dates in one request (max " + MAX_DATES_PER_REQUEST + ")");
        }

        Employee employee = employeeService.get(employeeId);   // 404 if missing
        if (!employee.isActive()) {
            throw new ConflictException("Employee is inactive: " + employeeId);
        }
        shiftService.get(shiftId);                              // 404 if missing

        for (LocalDate date : uniqueDates) {
            repository.assign(RosterEntry.create(employeeId, shiftId, date));
        }

        LocalDate first = ((TreeSet<LocalDate>) uniqueDates).first();
        LocalDate last = ((TreeSet<LocalDate>) uniqueDates).last();
        return repository.findByEmployeeBetween(employeeId, first, last).stream()
                .filter(e -> uniqueDates.contains(e.getWorkDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RosterEntry> view(LocalDate from, LocalDate to) {
        validateRange(from, to);
        return repository.findBetween(from, to);
    }

    @Transactional(readOnly = true)
    public List<RosterEntry> viewForEmployee(UUID employeeId, LocalDate from, LocalDate to) {
        validateRange(from, to);
        employeeService.get(employeeId);
        return repository.findByEmployeeBetween(employeeId, from, to);
    }

    /** Used by the attendance module: which shift is this employee working on this date? */
    @Transactional(readOnly = true)
    public Optional<ShiftAssignment> findShiftFor(UUID employeeId, LocalDate date) {
        return repository.findByEmployeeAndDate(employeeId, date)
                .map(entry -> {
                    Shift shift = shiftService.get(entry.getShiftId());
                    return new ShiftAssignment(
                            shift.getId(), shift.getName(),
                            shift.getStartTime(), shift.getEndTime(), shift.getGraceMinutes());
                });
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new BadRequestException("'to' must not be before 'from'");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new BadRequestException("Date range too large (max " + MAX_RANGE_DAYS + " days)");
        }
    }
}