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

        String strYears = years > 0 ? years + " years " : "";
        String strWeeks = weeks > 0 ? weeks + " weeks " : "";
        String strDays = days > 0 ? days + " days " : "";
        String strHours = hours > 0 ? hours + "h " : "";
        String strMins = mins > 0 ? mins + "m " : "";
        String strSecs = seconds + "s ";

        return (strYears + strWeeks + strDays + strHours + strMins + strSecs).trim();
    }

    public static String sensibleDiskSpaceValue(long bytes) {                                                           // TODO: javadoc
        double kb = bytes/1000.;
        double mb = kb/1000.;
        double gb = mb/1000.;
        double tb = gb/1000.;

        if ((long)tb > 0) {
            return String.format("%.2f Tb", tb);
        }
        if ((long)gb > 0) {
            return String.format("%.2f Gb", gb);
        }
        if ((long)mb > 0) {
            return String.format("%.2f Mb", mb);
        }
        if ((long)kb > 0) {
            return String.format("%.2f Kb", kb);
        }
        return String.format("%d bytes", bytes);
    }

}
