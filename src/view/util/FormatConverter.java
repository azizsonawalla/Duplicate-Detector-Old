package view.util;

public class FormatConverter {

    public static String milliSecondsToTime(long milli) {                                                               // TODO: javadoc
        long seconds = milli/1000;
        long mins = seconds/60;
        long hours = mins/60;
        long days = hours/24;
        long weeks = days/7;
        long years = weeks/52;

        seconds -= mins*60;
        mins -= hours*60;
        hours -= days*24;
        days -= weeks*7;
        weeks -= years*52;

        if (years > 0) {
            return String.format("%d years %d weeks %dd", years, weeks, days);
        }
        if (weeks > 0) {
            return String.format("%d weeks %dd %dh", weeks, days, hours);
        }
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, mins, seconds);
        }
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, mins, seconds);
        }
        if (mins > 0) {
            return String.format("%dm %ds", mins, seconds);
        }
        return String.format("%ds", seconds);
    }

}
