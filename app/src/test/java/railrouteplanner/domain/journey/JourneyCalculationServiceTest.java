package railrouteplanner.domain.journey;

import org.junit.Before;
import org.junit.Test;
import railrouteplanner.domain.date.DateUtil;

import java.text.ParseException;

import static org.junit.Assert.*;

public class JourneyCalculationServiceTest {
    @Before
    public void before() {
        JourneyCalculationService.reset();
    }

    @Test
    public void testGetInstance_shouldRetrieveSameInstance() {
        JourneyCalculationService ref = JourneyCalculationService.getInstance();
        assertEquals("should retrieve same instance", ref, JourneyCalculationService.getInstance());
    }

    @Test
    public void testReset_shouldRetrieveDifferentInstanceAfterReset() {
        JourneyCalculationService ref = JourneyCalculationService.getInstance();
        JourneyCalculationService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, JourneyCalculationService.getInstance());
    }

    @Test
    public void testCalculateJourneyEndTime() throws ParseException {
        JourneyCalculationService service = JourneyCalculationService.getInstance();

        assertEquals("peak NS ride journey", DateUtil.parse("2021-01-04T06:12", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T06:00", "yyyy-MM-dd'T'HH:mm"), "NS"));
        assertEquals("peak other line ride journey", DateUtil.parse("2021-01-04T06:10", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T06:00", "yyyy-MM-dd'T'HH:mm"), "DT"));
        assertEquals("peak other change line journey", DateUtil.parse("2021-01-04T06:15", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.changeLine, DateUtil.parse("2021-01-04T06:00", "yyyy-MM-dd'T'HH:mm"), "NS"));

        assertEquals("nonpeak DT ride journey", DateUtil.parse("2021-01-04T09:08", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T09:00", "yyyy-MM-dd'T'HH:mm"), "DT"));
        assertEquals("nonpeak other line ride journey", DateUtil.parse("2021-01-04T09:10", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T09:00", "yyyy-MM-dd'T'HH:mm"), "NS"));
        assertEquals("nonpeak other change line journey", DateUtil.parse("2021-01-04T09:10", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.changeLine, DateUtil.parse("2021-01-04T09:00", "yyyy-MM-dd'T'HH:mm"), "DT"));

        assertNull("night DT ride journey", service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T22:00", "yyyy-MM-dd'T'HH:mm"), "DT"));
        assertEquals("night TE line ride journey", DateUtil.parse("2021-01-04T22:08", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T22:00", "yyyy-MM-dd'T'HH:mm"), "TE"));
        assertEquals("night NS line ride journey", DateUtil.parse("2021-01-04T22:10", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.ride, DateUtil.parse("2021-01-04T22:00", "yyyy-MM-dd'T'HH:mm"), "NS"));
        assertEquals("night other change line journey", DateUtil.parse("2021-01-04T22:10", "yyyy-MM-dd'T'HH:mm"), service.calculateJourneyEndTime(JourneyType.changeLine, DateUtil.parse("2021-01-04T22:00", "yyyy-MM-dd'T'HH:mm"), "NS"));
    }
}
