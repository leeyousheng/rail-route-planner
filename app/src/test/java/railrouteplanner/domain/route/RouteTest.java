package railrouteplanner.domain.route;

import org.junit.Test;
import railrouteplanner.domain.date.DateUtil;
import railrouteplanner.domain.journey.JourneyType;
import railrouteplanner.domain.journey.TimeType;
import railrouteplanner.domain.station.Station;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

public class RouteTest {
    @Test
    public void testAddNode() throws ParseException {
        Route route = new Route();
        Station s1 = new Station("NS1", "Jurong East", new Date());
        Station s2 = new Station("NS2", "Bukit Batok", new Date());

        assertTrue("should add first instance of destination", route.addNode(new RouteNode(JourneyType.ride, null, s2, null, DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak)));
        assertFalse("should not add subsequent instance of destination", route.addNode(new RouteNode(JourneyType.ride, s1, s2, null, DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak)));
        assertEquals("should be empty path as the only node is the initial station", 0, route.getPath(s2).size());
    }

    @Test
    public void testGetPath() throws ParseException {
        Route route = new Route();
        Station s1 = new Station("NS1", "Jurong East", new Date());
        Station s2 = new Station("NS2", "Bukit Batok", new Date());
        Station s3 = new Station("NS3", "Bukit Gombak", new Date());

        RouteNode r1 = new RouteNode(JourneyType.ride, null, s1, null, DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak);
        RouteNode r2 = new RouteNode(JourneyType.ride, s1, s2, DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), DateUtil.parse("2021-01-01T07:20", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak);
        RouteNode r3 = new RouteNode(JourneyType.ride, s2, s3, DateUtil.parse("2021-01-01T07:20", "yyyy-MM-dd'T'HH:mm"), DateUtil.parse("2021-01-01T07:30", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak);
        route.addNode(r1);
        route.addNode(r2);
        route.addNode(r3);

        assertEquals("should have 2 steps", 2, route.getPath(s3).size());
        assertEquals("should return node r2 first", r2, route.getPath(s3).get(0));
        assertEquals("should return node r3 second", r3, route.getPath(s3).get(1));
    }

    @Test
    public void testGetPath_shouldReturnNull_givenInvalidDestination() throws ParseException {
        Route route = new Route();
        Station s1 = new Station("NS1", "Jurong East", new Date());
        Station s2 = new Station("NS2", "Bukit Batok", new Date());
        Station s3 = new Station("NS3", "Bukit Gombak", new Date());

        RouteNode r1 = new RouteNode(JourneyType.ride, null, s1, null, DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak);
        RouteNode r2 = new RouteNode(JourneyType.ride, s1, s2, DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), DateUtil.parse("2021-01-01T07:20", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak);
        route.addNode(r1);
        route.addNode(r2);
        
        assertNull(route.getPath(s3));
    }
}
