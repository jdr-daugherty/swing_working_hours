package org.rothe.john.working_hours.model;

import lombok.With;
import lombok.val;

import java.time.Duration;
import java.util.List;

import static org.rothe.john.working_hours.model.Time.MINUTES_IN_A_DAY;
import static org.rothe.john.working_hours.model.Time.fromHoursUtc;

@With
public record TimePair(Time left, Time right) {

    public static TimePair businessNormal(Zone zone) {
        return new TimePair(Time.at(zone, 8), Time.at(zone, 17));
    }

    public static TimePair businessLunch(Zone zone) {
        return new TimePair(Time.at(zone, 12), Time.at(zone, 13));
    }

    public TimePair addHours(int hours) {
        return new TimePair(left.addHours(hours), right.addHours(hours));
    }

    public List<TimePair> splitAround(int splitHourUtc) {
        // the split is typically the border hour which appears once on the left at
        // below 24 hours and once on the right at above 24 hours.
        if (isSplitBetween(splitHourUtc) || isSplitBetween(splitHourUtc + 24)) {
            return List.of(
                    new TimePair(left, fromHoursUtc(left.zone(), splitHourUtc)),
                    new TimePair(fromHoursUtc(left.zone(), splitHourUtc), right)
            );
        }

        // not between the two
        return List.of(this);
    }

    public Duration duration() {
        return Duration.ofMinutes(adjustedRightMinutesUtc() - left.totalMinutesInUtc());
    }

    boolean isSplitBetween(int splitHourUtc) {
        val split = toSplitMinutes(splitHourUtc);
        return left.totalMinutesInUtc() < split && split < adjustedRightMinutesUtc();
    }

    private int toSplitMinutes(int hourUtc) {
        if (hourUtc == 0) {
            return MINUTES_IN_A_DAY;
        }
        return hourUtc * 60;
    }

    int adjustedRightMinutesUtc() {
        return adjustRightMinutesUtc(right.totalMinutesInUtc());
    }

    private int adjustRightMinutesUtc(int rightMinutesUtc) {
        if (rightMinutesUtc < left.totalMinutesInUtc()) {
            return rightMinutesUtc + MINUTES_IN_A_DAY;
        }
        return rightMinutesUtc;
    }

    public boolean contains(Time time) {
        return contains(adjustRightMinutesUtc(time.totalMinutesInUtc()));
    }

    private boolean contains(int totalMinutesUtc) {
        return left.totalMinutesInUtc() <= totalMinutesUtc
                && totalMinutesUtc < adjustedRightMinutesUtc();
    }
}
