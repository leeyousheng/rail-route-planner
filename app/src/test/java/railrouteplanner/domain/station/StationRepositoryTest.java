package railrouteplanner.domain.station;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class StationRepositoryTest {
    @Before
    public void before() {
        StationRepository.reset();
    }

    @Test
    public void testGetInstance_shouldRetrieveSameInstance() {
        StationRepository ref = StationRepository.getInstance();
        assertEquals("should retrieve same instance", ref, StationRepository.getInstance());
    }

    @Test
    public void testReset_shouldRetrieveDifferentInstanceAfterReset() {
        StationRepository ref = StationRepository.getInstance();
        StationRepository.reset();
        assertNotEquals("should retrieve different instance after reset", ref, StationRepository.getInstance());
    }

    @Test
    public void addStation() {
        StationRepository repository = StationRepository.getInstance();
        Station s1 = repository.addStation("NS27", "Marina Bay", new Date(), null);
        Station s2 = repository.addStation("NS28", "Marina South Pier", new Date(), s1);
        Station s3 = repository.addStation("EW1", "Pasir Ris", new Date(), s2);

        assertNotNull("return created station", s1);
        assertTrue("should link to prev stop if same line", s2.getLinkedStations().contains(s1));
        assertTrue("should back link", s1.getLinkedStations().contains(s2));
        assertFalse("should not link if line is different", s3.getLinkedStations().contains(s2));
        assertFalse("should not link if line is different", s2.getLinkedStations().contains(s3));
    }

    @Test
    public void findByName() {
        StationRepository repository = StationRepository.getInstance();
        Station s1 = repository.addStation("NS1", "Jurong East", new Date(), null);
        Station s2 = repository.addStation("EW24", "Jurong East", new Date(), s1);

        assertEquals("should have 2 stations with name", 2, repository.findByName(s1.getName()).size());
        assertTrue("should contain ref to both stations", repository.findByName(s1.getName()).containsAll(Arrays.asList(s1, s2)));
        assertEquals("should return empty set if no such name", 0, repository.findByName("Bukit Batok").size());
    }
}
