package railrouteplanner.domain.route;

import org.junit.Test;
import railrouteplanner.domain.date.DateUtil;
import railrouteplanner.domain.journey.JourneyType;
import railrouteplanner.domain.journey.TimeType;
import railrouteplanner.domain.station.Station;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class RouteNodeTest {
    @Test
    public void testToString_shouldReturnStringAccordingToTheNodeConfiguration() throws ParseException {
        Station s1 = new Station("NS1", "Jurong East", new Date());
        Station s2 = new Station("NS2", "Bukit Batok", new Date());
        RouteNode r1 = new RouteNode(JourneyType.ride, s1, s2, DateUtil.parse("2021-01-01T10:00", "yyyy-MM-dd'T'HH:mm"), null, TimeType.peak);
        RouteNode r2 = new RouteNode(JourneyType.changeLine, s1, s2, DateUtil.parse("2021-01-01T10:00", "yyyy-MM-dd'T'HH:mm"), null, TimeType.peak);

        assertEquals("should return ride string", "Fri Jan 01 10:00:00 SGT 2021 - (peak) Take NS from Jurong East to Bukit Batok.\n", r1.toString());
        assertEquals("should return change line string", "Fri Jan 01 10:00:00 SGT 2021 - (peak) Change line from NS to NS at Bukit Batok.\n", r2.toString());
    }
}
