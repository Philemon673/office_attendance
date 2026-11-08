package office.attendance.roster.application;

import office.attendance.roster.domain.Shift;
import office.attendance.roster.domain.ShiftRepository;
import office.attendance.shared.error.BadRequestException;
import office.attendance.shared.error.ConflictException;
import office.attendance.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftService {

    private final ShiftRepository repository;

    public ShiftService(ShiftRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Shift create(String name, LocalTime startTime, LocalTime endTime, int graceMinutes) {
        String trimmed = name.trim();

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("Shift end time must be after start time");
        }
        if (repository.existsByName(trimmed)) {
            throw new ConflictException("Shift name already in use: " + trimmed);
        }

        Shift shift = Shift.create(trimmed, startTime, endTime, graceMinutes);
        repository.insert(shift);
        return shift;
    }

    @Transactional(readOnly = true)
    public Shift get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shift not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Shift> list() {
        return repository.findAll();
    }
}