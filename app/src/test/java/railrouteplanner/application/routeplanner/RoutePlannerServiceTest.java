package railrouteplanner.application.routeplanner;

import org.junit.Before;
import org.junit.Test;
import railrouteplanner.domain.date.DateUtil;
import railrouteplanner.domain.journey.JourneyCalculationService;
import railrouteplanner.domain.journey.JourneyType;
import railrouteplanner.domain.journey.TimeType;
import railrouteplanner.domain.route.Route;
import railrouteplanner.domain.route.RouteNode;
import railrouteplanner.domain.station.Station;
import railrouteplanner.domain.station.StationRepository;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoutePlannerServiceTest {
    private final StationRepository stationRepository = mock(StationRepository.class);
    private final JourneyCalculationService journeyCalculationService = mock(JourneyCalculationService.class);

    @Before
    public void before() {
        RoutePlannerService.reset();
        reset(stationRepository, journeyCalculationService);
    }

    @Test
    public void testGetInstance_shouldRetrieveSameInstance() {
        RoutePlannerService ref = RoutePlannerService.getInstance();
        assertEquals("should retrieve same instance given without args", ref, RoutePlannerService.getInstance());
        assertEquals("should retrieve same instance given args", ref, RoutePlannerService.getInstance(stationRepository, journeyCalculationService));
        RoutePlannerService.reset();

        RoutePlannerService ref2 = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        assertEquals("should retrieve same instance given without args", ref2, RoutePlannerService.getInstance());
        assertEquals("should retrieve same instance given args", ref2, RoutePlannerService.getInstance(stationRepository, journeyCalculationService));
    }

    @Test
    public void testReset_shouldRetrieveDifferentInstanceAfterReset() {
        RoutePlannerService ref = RoutePlannerService.getInstance();
        RoutePlannerService.reset();
        assertNotEquals("should retrieve different instance after reset without args", ref, RoutePlannerService.getInstance());

        RoutePlannerService.reset();

        ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        RoutePlannerService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, RoutePlannerService.getInstance(stationRepository, journeyCalculationService));
    }

    @Test
    public void testPlanRoute_shouldReturnRoute_givenReachableDestination() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        Station source = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station destination = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        source.addLinkedStation(destination);

        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertEquals("should only have 1 node in path", 1, path.size());
        assertEquals("from should be source station", source, path.get(0).getFrom());
        assertEquals("to should be destination station", destination, path.get(0).getTo());
        assertEquals("start time of route should be start time", DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), path.get(0).getStartTime());
        assertEquals("end time of route should be after calculation time", DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), path.get(0).getEndTime());
        verify(stationRepository, times(2)).findByName(source.getName());
        verify(stationRepository, times(1)).findByName(destination.getName());
        verify(stationRepository, times(3)).findByName(anyString());
        verify(journeyCalculationService, times(1)).calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString());
    }

    @Test
    public void testPlanRoute_shouldReturnRoute_givenReachableDestinationWithUnopenedStation() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        Station source = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station intermediate = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 3000"));
        Station destination = new Station("NS3", "Bukit Gombak", DateUtil.parse("10 March 1990"));
        source.addLinkedStation(intermediate);
        intermediate.addLinkedStation(source);
        intermediate.addLinkedStation(destination);
        destination.addLinkedStation(intermediate);

        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(stationRepository.findByName(intermediate.getName())).thenReturn(Collections.singleton(intermediate));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertEquals("should only have 1 node in path", 1, path.size());
        assertEquals("from should be source station", source, path.get(0).getFrom());
        assertEquals("to should be destination station", destination, path.get(0).getTo());
        assertEquals("start time of route should be start time", DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), path.get(0).getStartTime());
        assertEquals("end time of route should be after calculation time", DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), path.get(0).getEndTime());
    }

    @Test
    public void testPlanRoute_shouldReturnNullPath_givenReachableDestinationWithUnopenedInterchange() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        Station intermediate = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station intermediate2 = new Station("EW24", "Jurong East", DateUtil.parse("5 November 3000"));
        Station destination = new Station("EW25", "Chinese Garden", DateUtil.parse("10 March 1990"));
        source.addLinkedStation(intermediate);
        intermediate.addLinkedStation(source);
        intermediate2.addLinkedStation(destination);
        destination.addLinkedStation(intermediate2);

        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(stationRepository.findByName(intermediate.getName())).thenReturn(new HashSet<>(Arrays.asList(intermediate, intermediate2)));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertNull("should not have a path", path);
    }

    @Test
    public void testPlanRoute_shouldReturnNullPath_givenSourceIsNotYetOpened() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 3000"));
        Station destination = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        source.addLinkedStation(destination);
        destination.addLinkedStation(source);

        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertNull("should not have a path", path);
    }

    @Test
    public void testPlanRoute_shouldReturnNullPath_givenDestinationIsNotYetOpened() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        Station destination = new Station("NS1", "Jurong East", DateUtil.parse("10 March 3000"));
        source.addLinkedStation(destination);
        destination.addLinkedStation(source);

        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertNull("should not have a path", path);
    }

    @Test
    public void testPlanRoute_shouldReturnRoute_givenSourceHaveMultipleLines() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        Station source = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station source2 = new Station("EW24", "Jurong East", DateUtil.parse("5 November 1988"));
        Station destination = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        source.addLinkedStation(destination);

        when(stationRepository.findByName(source.getName())).thenReturn(new HashSet<>(Arrays.asList(source, source2)));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertEquals("should only have 1 node in path", 1, path.size());
        assertEquals("from should be source station", source, path.get(0).getFrom());
        assertEquals("to should be destination station", destination, path.get(0).getTo());
        assertEquals("start time of route should be start time", DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), path.get(0).getStartTime());
        assertEquals("end time of route should be after calculation time", DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), path.get(0).getEndTime());
    }

    @Test
    public void testPlanRoute_shouldReturnRoute_givenDestinationHaveMultipleLines() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        Station destination = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station destination2 = new Station("EW24", "Jurong East", DateUtil.parse("5 November 1988"));
        source.addLinkedStation(destination);

        when(stationRepository.findByName(destination.getName())).thenReturn(new HashSet<>(Arrays.asList(destination, destination2)));
        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"));

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        List<RouteNode> path = route.getPath(destination);

        assertEquals("should only have 1 node in path", 1, path.size());
        assertEquals("from should be source station", source, path.get(0).getFrom());
        assertEquals("to should be destination station", destination, path.get(0).getTo());
        assertEquals("start time of route should be start time", DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), path.get(0).getStartTime());
        assertEquals("end time of route should be after calculation time", DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), path.get(0).getEndTime());
    }

    @Test
    public void testPlanRoute_shouldReturnRoute_givenUnreachableDestination() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        Station destination = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));

        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));

        assertNotNull("route will still be returned when unreachable", ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm")));
    }

    @Test
    public void testPlanRoute_shouldReturnNull_givenInvalidSourceOrDestination() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        String invalidDestination = "invalid";
        Station validStation = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));

        when(stationRepository.findByName(invalidDestination)).thenReturn(new HashSet<>());
        when(stationRepository.findByName(validStation.getName())).thenReturn(Collections.singleton(validStation));

        assertNotNull("route will still be returned when invalid destination", ref.planRoute(validStation.getName(), invalidDestination, DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm")));
        assertNotNull("route will still be returned when invalid source", ref.planRoute(invalidDestination, validStation.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm")));
    }

    @Test
    public void testPrintRoute_shouldPrintUnreachableResult_givenPathIsNull() {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        when(stationRepository.findByName(anyString())).thenReturn(Collections.emptySet());

        assertEquals("should print unreachable if invalid destination name", "Destination could not be reached.", ref.printRoute(new Route(), "Invalid name"));
    }

    @Test
    public void testPrintRoute_shouldPrintAlreadyAtStationResult_givenPathSize0() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        Route route = new Route();
        route.addNode(new RouteNode(JourneyType.ride, null, source, null, DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak));

        assertEquals("should print already at destination give path size is 0", "Already at destination.", ref.printRoute(route, "Bukit Batok"));
    }

    @Test
    public void testPrintRoute_shouldPrintResult_givenValidPath() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);
        Route route = new Route();
        Station source = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        Station intermediate = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station intermediate2 = new Station("EW24", "Jurong East", DateUtil.parse("5 November 1988"));
        Station destination = new Station("EW25", "Chinese Garden", DateUtil.parse("5 November 1988"));
        route.addNode(new RouteNode(JourneyType.ride, null, source, null, DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), TimeType.nonpeak));
        route.addNode(new RouteNode(JourneyType.ride, source, intermediate, DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"), DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), TimeType.peak));
        route.addNode(new RouteNode(JourneyType.changeLine, intermediate, intermediate2, DateUtil.parse("2021-01-01T07:10", "yyyy-MM-dd'T'HH:mm"), DateUtil.parse("2021-01-01T07:20", "yyyy-MM-dd'T'HH:mm"), TimeType.peak));
        route.addNode(new RouteNode(JourneyType.ride, intermediate2, destination, DateUtil.parse("2021-01-01T07:20", "yyyy-MM-dd'T'HH:mm"), DateUtil.parse("2021-01-01T07:30", "yyyy-MM-dd'T'HH:mm"), TimeType.night));

        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));

        String refRes = "Journey to Chinese Garden (Estimated arrival - Fri Jan 01 07:30:00 SGT 2021):\n" +
                "Fri Jan 01 07:00:00 SGT 2021 - (peak) Take NS from Bukit Batok to Jurong East.\n" +
                "Fri Jan 01 07:10:00 SGT 2021 - (peak) Change line from NS to EW at Jurong East.\n" +
                "Fri Jan 01 07:20:00 SGT 2021 - (night) Take EW from Jurong East to Chinese Garden.\n" +
                "Fri Jan 01 07:30:00 SGT 2021 - Arrived at Chinese Garden.";

        assertEquals("should print user friendly result", refRes, ref.printRoute(route, destination.getName()));
    }

    @Test
    public void testPlanRoute_shouldPrintUnreachableResult_givenLineClosed() throws ParseException {
        RoutePlannerService ref = RoutePlannerService.getInstance(stationRepository, journeyCalculationService);

        Station source = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        Station destination = new Station("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"));
        source.addLinkedStation(destination);

        when(stationRepository.findByName(source.getName())).thenReturn(Collections.singleton(source));
        when(stationRepository.findByName(destination.getName())).thenReturn(Collections.singleton(destination));
        when(journeyCalculationService.calculateJourneyEndTime(any(JourneyType.class), any(Date.class), anyString())).thenReturn(null);

        Route route = ref.planRoute(source.getName(), destination.getName(), DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));

        assertEquals("should print unreachable if route is null", "Destination could not be reached.", ref.printRoute(route, "Bukit Batok"));
    }

}
