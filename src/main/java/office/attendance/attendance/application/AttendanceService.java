package office.attendance.attendance.application;

import office.attendance.attendance.domain.AttendanceEvent;
import office.attendance.attendance.domain.AttendanceEventRepository;
import office.attendance.attendance.domain.AttendanceEventType;
import office.attendance.attendance.domain.AttendanceRecord;
import office.attendance.attendance.domain.AttendanceRecordRepository;
import office.attendance.attendance.domain.CheckedInEvent;
import office.attendance.employee.application.EmployeeService;
import office.attendance.employee.domain.Employee;
import office.attendance.qr.application.QrTokenService;
import office.attendance.roster.application.RosterService;
import office.attendance.roster.application.ShiftAssignment;
import office.attendance.shared.error.BadRequestException;
import office.attendance.shared.error.ConflictException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService {

    private static final long MAX_RANGE_DAYS = 93;

    private final QrTokenService qrTokenService;
    private final RosterService rosterService;
    private final EmployeeService employeeService;
    private final AttendanceRecordRepository records;
    private final AttendanceEventRepository events;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;
    private final ZoneId zone;
    private final Duration opensBefore;

    public AttendanceService(QrTokenService qrTokenService,
                             RosterService rosterService,
                             EmployeeService employeeService,
                             AttendanceRecordRepository records,
                             AttendanceEventRepository events,
                             ApplicationEventPublisher publisher,
                             Clock clock,
                             AttendanceProperties properties) {
        this.qrTokenService = qrTokenService;
        this.rosterService = rosterService;
        this.employeeService = employeeService;
        this.records = records;
        this.events = events;
        this.publisher = publisher;
        this.clock = clock;
        this.zone = ZoneId.of(properties.zone());
        this.opensBefore = Duration.ofMinutes(properties.checkInOpensMinutesBefore());
    }

    @Transactional
    public AttendanceRecord checkIn(UUID employeeId, String qrToken) {
        requireValidToken(qrToken);

        Instant now = clock.instant();
        LocalDate today = LocalDate.ofInstant(now, zone);

        Employee employee = employeeService.get(employeeId);
        if (!employee.isActive()) {
            throw new ConflictException("Employee is inactive");
        }

        ShiftAssignment shift = rosterService.findShiftFor(employeeId, today)
                .orElseThrow(() -> new ConflictException("You are not rostered to work today"));

        Instant shiftStart = today.atTime(shift.startTime()).atZone(zone).toInstant();
        Instant shiftEnd = today.atTime(shift.endTime()).atZone(zone).toInstant();
        Instant opensAt = shiftStart.minus(opensBefore);

        if (now.isBefore(opensAt)) {
            throw new BadRequestException("Check-in opens at "
                    + DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(opensAt));
        }
        if (now.isAfter(shiftEnd)) {
            throw new ConflictException("Your shift has already ended");
        }

        AttendanceRecord record = AttendanceRecord.checkIn(
                employeeId, today, shift.shiftId(), shiftStart, shift.graceMinutes(), now);

        if (!records.insertIfAbsent(record)) {
            throw new ConflictException("You have already checked in today");
        }
        events.insert(new AttendanceEvent(UUID.randomUUID(), employeeId, AttendanceEventType.CHECK_IN, now));

        publisher.publishEvent(new CheckedInEvent(
                record.getId(), employeeId, employee.getFullName(), employee.getEmail(),
                shift.shiftName(), now, record.getStatus(), record.getLateMinutes()));

        return record;
    }

    @Transactional
    public AttendanceRecord checkOut(UUID employeeId, String qrToken) {
        requireValidToken(qrToken);

        Instant now = clock.instant();
        LocalDate today = LocalDate.ofInstant(now, zone);

        AttendanceRecord record = records.findByEmployeeAndDate(employeeId, today)
                .orElseThrow(() -> new ConflictException("You have not checked in today"));
        if (record.getCheckOutAt() != null) {
            throw new ConflictException("You have already checked out today");
        }

        AttendanceRecord updated = record.checkedOut(now);
        if (!records.markCheckedOut(updated)) {
            throw new ConflictException("You have already checked out today");
        }
        events.insert(new AttendanceEvent(UUID.randomUUID(), employeeId, AttendanceEventType.CHECK_OUT, now));
        return updated;
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> myRecords(UUID employeeId, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new BadRequestException("'to' must not be before 'from'");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new BadRequestException("Date range too large (max " + MAX_RANGE_DAYS + " days)");
        }
        return records.findByEmployeeBetween(employeeId, from, to);
    }

    private void requireValidToken(String qrToken) {
        if (!qrTokenService.isValid(qrToken)) {
            throw new BadRequestException("Invalid QR code");
        }
    }
}
