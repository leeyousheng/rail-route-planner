package railrouteplanner.domain.station;

import org.junit.Test;
import railrouteplanner.domain.date.DateUtil;

import java.text.ParseException;

import static org.junit.Assert.*;

public class StationTest {
    @Test
    public void testConstructor_shouldInstantiateWithEmptyLinkedStations() throws ParseException {
        Station ref = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        assertTrue("should instantiate with empty linked", ref.getLinkedStations().isEmpty());
    }

    @Test
    public void testAddLinkedStation() throws ParseException {
        Station ref = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station other = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));

        assertTrue("should be able to add 1st instance", ref.addLinkedStation(other));
        assertEquals("should have 1 result", 1, ref.getLinkedStations().size());
        assertEquals("should be linked to other station", other, ref.getLinkedStations().iterator().next());
        assertFalse("should not add recurring", ref.addLinkedStation(other));
        assertEquals("should remain at 1 resource", 1, ref.getLinkedStations().size());
    }

    @Test
    public void testGetLine_shouldReturnTheFront2Letters() throws ParseException {
        Station ref = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));

        assertEquals("should just return the first 2 letters of the id", "NS", ref.getLine());
    }

    @Test
    public void testEquality() throws ParseException {
        Station ref = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));

        assertEquals("should use id as key", ref, new Station("NS1", "other", null));
    }
}
