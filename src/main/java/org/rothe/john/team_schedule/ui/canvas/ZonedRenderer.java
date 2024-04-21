package org.rothe.john.team_schedule.ui.canvas;

import lombok.Getter;
import lombok.val;
import org.rothe.john.team_schedule.util.Palette;
import org.rothe.john.team_schedule.util.Zones;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

import static java.time.temporal.ChronoField.OFFSET_SECONDS;

@Getter
public abstract class ZonedRenderer extends AbstractRenderer {
    private final ZoneId zoneId;

    protected ZonedRenderer(ZoneId zoneId, Palette palette) {
        super(palette.fill(zoneId), palette.line(zoneId));
        this.zoneId = zoneId;
    }

    public String getZoneIdString() {
        return Zones.getZoneIdString(zoneId);
    }

    public String getLocationDisplayString() {
        return Zones.getLocationDisplayString(zoneId);
    }

    public int getUtcOffset() {
        return Zones.getUtcOffset(zoneId);
    }

    protected static int normalizeHour(int hour) {
        int h = hour % 24;
        if(h < 0) {
            return h + 24;
        }
        return h;
    }

    protected int timeToColumnStart(LocalTime time) {
        return timeToColumnStart(toMinutesUtc(time));
    }

    protected int timeToColumnCenter(LocalTime time) {
        return timeToColumnCenter(toMinutesUtc(time));
    }

    private int toMinutesUtc(LocalTime time) {
        return normalizeHour(time.getHour() - getUtcOffset()) * 60 + time.getMinute();
    }
}