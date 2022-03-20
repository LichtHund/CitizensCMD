package me.mattstudios.citizenscmd.utility;

public class TimeUtil {

    private final int days;
    private final int hours;
    private final int minutes;
    private final int seconds;

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;

    public TimeUtil(long seconds) {
        days = (int) (seconds / SECONDS_IN_DAY);
        seconds = seconds - ((long) days * SECONDS_IN_DAY);
        hours = (int) (seconds / SECONDS_IN_HOUR);
        seconds = seconds - ((long) hours * SECONDS_IN_HOUR);
        minutes = (int) (seconds / SECONDS_IN_MINUTE);
        this.seconds = (int) (seconds - (minutes * SECONDS_IN_MINUTE));
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }
}
