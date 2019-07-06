/*
  CitizensCMD - Add-on for Citizens
  Copyright (C) 2018 Mateus Moreira
  <p>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  <p>
  A special thanks to @ExtendedClip for letting me use and modify this class from PlaceholderAPI
 */

/*
  A special thanks to @ExtendedClip for letting me use and modify this class from PlaceholderAPI
 */

package me.mattstudios.citizenscmd.utility;

import me.mattstudios.citizenscmd.CitizensCMD;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static String dayFormat;
    private static String dayPlural;
    private static String hourFormat;
    private static String hourPlural;
    private static String minuteFormat;
    private static String minutePlural;
    private static String secondFormat;
    private static String secondPlural;

    /**
     * Gets formatted time from seconds
     *
     * @param seconds The time in seconds to be converted
     * @return String with the time like "2d 2h 2m 2s"
     */
    public static String getFormattedTime(CitizensCMD plugin, long seconds, DisplayFormat format) {

        String[] messagesString = new String[4];
        messagesString[0] = plugin.getLang().getMessage(Path.SECONDS);
        messagesString[1] = plugin.getLang().getMessage(Path.MINUTES);
        messagesString[2] = plugin.getLang().getMessage(Path.HOURS);
        messagesString[3] = plugin.getLang().getMessage(Path.DAYS);

        String[] shorts = new String[4];
        String[] mediums = new String[4];
        String[] fulls = new String[4];

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
                String[] mediumsAfter = new String[4];
                String[] mediumsPlurals = new String[4];
                Pattern patternMediums = Pattern.compile("([^]]*)\\(([^]]*)\\)");
                for (int i = 0; i < mediums.length; i++) {
                    if (mediums[i].contains("(") && mediums[i].contains(")")) {
                        Matcher matcher = patternMediums.matcher(mediums[i]);
                        if (matcher.find()) {
                            mediumsAfter[i] = matcher.group(1);
                            mediumsPlurals[i] = matcher.group(2);
                        }
                    } else {
                        mediumsAfter[i] = mediums[i];
                        mediumsPlurals[i] = "";
                    }
                }
                dayFormat = " " + mediumsAfter[3];
                dayPlural = mediumsPlurals[3];
                hourFormat = " " + mediumsAfter[2];
                hourPlural = mediumsPlurals[2];
                minuteFormat = " " + mediumsAfter[1];
                minutePlural = mediumsPlurals[1];
                secondFormat = " " + mediumsAfter[0];
                secondPlural = mediumsPlurals[0];
                break;
            case FULL:
                String[] fullsAfter = new String[4];
                String[] fullsPlurals = new String[4];
                Pattern patternFulls = Pattern.compile("([^]]*)\\(([^]]*)\\)");
                for (int i = 0; i < fulls.length; i++) {
                    if (fulls[i].contains("(") && fulls[i].contains(")")) {
                        Matcher matcher = patternFulls.matcher(fulls[i]);
                        if (matcher.find()) {
                            fullsAfter[i] = matcher.group(1);
                            fullsPlurals[i] = matcher.group(2);
                        }
                    } else {
                        fullsAfter[i] = fulls[i];
                        fullsPlurals[i] = "";
                    }
                }
                dayFormat = " " + fullsAfter[3];
                dayPlural = fullsPlurals[3];
                hourFormat = " " + fullsAfter[2];
                hourPlural = fullsPlurals[2];
                minuteFormat = " " + fullsAfter[1];
                minutePlural = fullsPlurals[1];
                secondFormat = " " + fullsAfter[0];
                secondPlural = fullsPlurals[0];
                break;
        }

        if (seconds < 60) {
            if (seconds == 1 && !format.equals(DisplayFormat.SHORT))
                return seconds + secondFormat;
            return seconds + secondFormat + secondPlural;
        }

        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long secondsLeft = seconds - TimeUnit.MINUTES.toSeconds(minutes);

        if (minutes < 60) {
            if (minutes == 1 && !format.equals(DisplayFormat.SHORT)) {
                if (secondsLeft > 0) {
                    if (secondsLeft == 1)
                        return minutes + minuteFormat + " " + secondsLeft + secondFormat;
                    return minutes + minuteFormat + " " + secondsLeft + secondFormat + secondPlural;
                } else
                    return minutes + minuteFormat;
            } else {
                if (secondsLeft > 0) {
                    if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                        return minutes + minuteFormat + minutePlural + " " + secondsLeft + secondFormat;
                    return minutes + minuteFormat + minutePlural + " " + secondsLeft + secondFormat + secondPlural;
                } else
                    return minutes + minuteFormat + minutePlural;
            }
        }

        if (minutes < 1440) {
            long hours = TimeUnit.MINUTES.toHours(minutes);
            String time;
            if (hours == 1 && !format.equals(DisplayFormat.SHORT))
                time = hours + hourFormat;
            else
                time = hours + hourFormat + hourPlural;
            long leftOver = minutes - TimeUnit.HOURS.toMinutes(hours);

            if (leftOver >= 1) {
                if (leftOver == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + leftOver + minuteFormat;
                else
                    time += " " + leftOver + minuteFormat + minutePlural;
            }

            if (secondsLeft > 0)
                if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + secondsLeft + secondFormat;
                else
                    time += " " + secondsLeft + secondFormat + secondPlural;

            return time;
        }

        long days = TimeUnit.MINUTES.toDays(minutes);
        String time;
        if (days == 1 && !format.equals(DisplayFormat.SHORT))
            time = days + dayFormat;
        else
            time = days + dayFormat + dayPlural;
        long leftOver = minutes - TimeUnit.DAYS.toMinutes(days);

        if (leftOver >= 1) {
            if (leftOver < 60) {
                if (leftOver == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + leftOver + minuteFormat;
                else
                    time += " " + leftOver + minuteFormat + minutePlural;
            } else {
                long hours = TimeUnit.MINUTES.toHours(leftOver);
                if (hours == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + hours + hourFormat;
                else
                    time += " " + hours + hourFormat + hourPlural;
                long minsLeft = leftOver - TimeUnit.HOURS.toMinutes(hours);
                if (minsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                    time += " " + minsLeft + minuteFormat;
                else
                    time += " " + minsLeft + minuteFormat + minutePlural;
            }
        }

        if (secondsLeft > 0) {
            if (secondsLeft == 1 && !format.equals(DisplayFormat.SHORT))
                time += " " + secondsLeft + secondFormat;
            else
                time += " " + secondsLeft + secondFormat + secondPlural;
        }

        return time;

    }
}
