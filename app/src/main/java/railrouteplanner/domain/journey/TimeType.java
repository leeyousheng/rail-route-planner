package railrouteplanner.domain.journey;

import java.util.Calendar;
import java.util.Date;

public enum TimeType {
    peak, night, nonpeak;

    public static TimeType get(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        if (day > 1 && day < 7 && ((hour >= 6 && hour < 9) || (hour >= 18 && hour < 21))) {
            return peak;
        }

        if (hour >= 22 || hour < 6) {
            return night;
        }
        return nonpeak;
    }
}
