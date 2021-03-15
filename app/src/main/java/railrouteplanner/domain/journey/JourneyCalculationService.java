package railrouteplanner.domain.journey;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class JourneyCalculationService {
    private static JourneyCalculationService instance;

    private JourneyCalculationService() {
    }

    public static JourneyCalculationService getInstance() {
        if (instance == null)
            instance = new JourneyCalculationService();
        return instance;
    }

    /**
     * reset allows retrieval of new instance.
     * CAUTION: might cause error if used inappropriately.
     */
    public static void reset() {
        instance = null;
    }


    public Date calculateJourneyEndTime(JourneyType type, Date startTime, String line) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        int duration = findDurationOfJourney(type, TimeType.get(startTime), line);
        if (duration == -1)
            return null;

        cal.add(Calendar.MINUTE, duration);
        return cal.getTime();
    }

    /**
     * findDurationOfJourney returns duration according to journey type, time type and line.
     * returns -1 if line is closed for the day.
     */
    private int findDurationOfJourney(JourneyType journeyType, TimeType timeType, String line) {
        switch (timeType) {
            case peak:
                if (journeyType == JourneyType.ride) {
                    if (Arrays.asList("NS", "NE").contains(line))
                        return 12;

                    return 10;
                }

                return 15;
            case night:
                if (Arrays.asList("DT", "CG", "CE").contains(line)) {
                    return -1;
                }

                if (journeyType == JourneyType.ride && line.equals("TE"))
                    return 8;

                return 10;
            case nonpeak:
                if (journeyType == JourneyType.ride && Arrays.asList("DT", "TE").contains(line)) {
                    return 8;
                }

                return 10;
        }
        return -1;
    }
}
