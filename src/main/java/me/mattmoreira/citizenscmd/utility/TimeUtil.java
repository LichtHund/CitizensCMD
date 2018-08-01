/**
 * CitizensCMD - Add-on for Citizens
 * Copyright (C) 2018 Mateus Moreira
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A special thanks to @ExtendedClip for letting me use and modify this class from PlaceholderAPI
 */

package me.mattmoreira.citizenscmd.utility;

import me.mattmoreira.citizenscmd.CitizensCMD;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static String dayFormat;
    private static String hourFormat;
    private static String minuteFormat;
    private static String secondFormat;

    /**
     * Gets formatted time from seconds
     *
     * @param seconds The time in seconds to be converted
     * @return String with the time like "2d 2h 2m 2s"
     */
    public static String getFormattedTime(long seconds, DisplayFormat format) {

        String messagesString[] = new String[4];
        messagesString[0] = CitizensCMD.getPlugin().getLang().getMessage(Path.SECONDS);
        messagesString[1] = CitizensCMD.getPlugin().getLang().getMessage(Path.MINUTES);
        messagesString[2] = CitizensCMD.getPlugin().getLang().getMessage(Path.HOURS);
        messagesString[3] = CitizensCMD.getPlugin().getLang().getMessage(Path.DAYS);

        String shorts[] = new String[4];
        String mediums[] = new String[4];
        String fulls[] = new String[4];

        Pattern pattern = Pattern.compile("\\[([^]]*)], \\[([^]]*)], \\[([^]]*)]");
        for (int i = 0; i < messagesString.length; i++) {
            Matcher matcher = pattern.matcher(messagesString[i]);
            if (matcher.find()) {
                shorts[i] = matcher.group(1);
                mediums[i] = matcher.group(2);
                fulls[i] = matcher.group(3);
            }
        }

        switch (format) {
            case SHORT:
                dayFormat = shorts[3];
                hourFormat = shorts[2];
                minuteFormat = shorts[1];
                secondFormat = shorts[0];
                break;
            case MEDIUM:
                dayFormat = " " + mediums[3];
                hourFormat = " " + mediums[2];
                minuteFormat = " " + mediums[1];
                secondFormat = " " + mediums[0];
                break;
            case FULL:
                dayFormat = " " + fulls[3];
                hourFormat = " " + fulls[2];
                minuteFormat = " " + fulls[1];
                secondFormat = " " + fulls[0];
                break;
        }

        if (seconds < 60) {
            if (seconds == 1 && !format.equals(DisplayFormat.SHORT))
                return seconds + secondFormat.substring(0, secondFormat.length() - 1);
            return seconds + secondFormat;
        }

        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long secondsLeft = seconds - TimeUnit.MINUTES.toSeconds(minutes);

        if (minutes < 60) {
            if (minutes == 1 && !format.equals(DisplayFormat.SHORT)) {
                if (secondsLeft > 0) {
                    if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                        return String.valueOf(minutes + minuteFormat.substring(0, secondFormat.length() - 1) + " " + secondsLeft + secondFormat.substring(0, secondFormat.length() - 1));
                    return String.valueOf(minutes + minuteFormat.substring(0, secondFormat.length() - 1) + " " + secondsLeft + secondFormat);
                } else
                    return String.valueOf(minutes + minuteFormat.substring(0, secondFormat.length() - 1));
            } else {
                if (secondsLeft > 0) {
                    if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                        return String.valueOf(minutes + minuteFormat + " " + secondsLeft + secondFormat.substring(0, secondFormat.length() - 1));
                    return String.valueOf(minutes + minuteFormat + " " + secondsLeft + secondFormat);
                } else
                    return String.valueOf(minutes + minuteFormat);
            }
        }

        if (minutes < 1440) {
            long hours =  TimeUnit.MINUTES.toHours(minutes);
            String time;
            if (hours == 1 && !format.equals(DisplayFormat.SHORT))
                time = hours + hourFormat.substring(0, hourFormat.length() - 1);
            else
                time = hours + hourFormat;
            long leftOver = minutes - TimeUnit.HOURS.toMinutes(hours);

            if (leftOver >= 1) {
                if (leftOver == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + leftOver + minuteFormat.substring(0, minuteFormat.length() - 1);
                else
                    time += " " + leftOver + minuteFormat;
            }

            if (secondsLeft > 0)
                if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + secondsLeft + secondFormat.substring(0, secondFormat.length() - 1);
                else
                    time += " " + secondsLeft + secondFormat;

            return time;
        }

        long days = TimeUnit.MINUTES.toDays(minutes);
        String time;
        if (days == 1 && !format.equals(DisplayFormat.SHORT))
            time = days + dayFormat.substring(0, dayFormat.length() - 1);
        else
            time = days + dayFormat;
        long leftOver = minutes - TimeUnit.DAYS.toMinutes(days);

        if (leftOver >= 1) {
            if (leftOver < 60) {
                if (leftOver == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + leftOver + minuteFormat.substring(0, minuteFormat.length() - 1);
                else
                    time += " " + leftOver + minuteFormat;
            } else {
                long hours = TimeUnit.MINUTES.toHours(leftOver);
                if (hours == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + hours + hourFormat.substring(0, hourFormat.length() - 1);
                else
                    time += " " + hours + hourFormat;
                long minsLeft = leftOver - TimeUnit.HOURS.toMinutes(hours);
                if (minsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + minsLeft + minuteFormat.substring(0, minuteFormat.length() - 1);
                else
                    time += " " + minsLeft + minuteFormat;
            }
        }

        if (secondsLeft > 0) {
            if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                time += " " + secondsLeft + secondFormat.substring(0, secondFormat.length() - 1);
            else
                time += " " + secondsLeft + secondFormat;
        }

        return time;

    }
}
