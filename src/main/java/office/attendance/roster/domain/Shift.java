package office.attendance.roster.domain;


import java.time.LocalTime;
import java.util.UUID;

public class Shift {
    private final UUID id;
    private final String name;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int graceMinutes;

    public Shift(UUID id, String name, LocalTime startTime, LocalTime endTime, int graceMinutes) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.graceMinutes = graceMinutes;
    }

    public static Shift create(String name, LocalTime startTime, LocalTime endTime, int graceMinutes) {
        return new Shift(UUID.randomUUID(), name, startTime, endTime, graceMinutes);
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public int getGraceMinutes() {
        return graceMinutes;
    }
}