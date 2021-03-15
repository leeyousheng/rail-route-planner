package railrouteplanner.application;

import org.junit.Before;
import org.junit.Test;
import railrouteplanner.application.dataingestion.DataIngestionService;
import railrouteplanner.application.routeplanner.RoutePlannerService;
import railrouteplanner.domain.date.DateUtil;
import railrouteplanner.domain.route.Route;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommandServiceTest {
    private final DataIngestionService dataIngestionService = mock(DataIngestionService.class);
    private final RoutePlannerService routePlannerService = mock(RoutePlannerService.class);

    @Before
    public void before() {
        CommandService.reset();
        reset(dataIngestionService, routePlannerService);
    }

    @Test
    public void testGetInstance_shouldRetrieveSameInstance() {
        CommandService ref = CommandService.getInstance();
        assertEquals("should retrieve same instance given without args", ref, CommandService.getInstance());
        assertEquals("should retrieve same instance given args", ref, CommandService.getInstance(dataIngestionService, routePlannerService));

        CommandService.reset();

        CommandService ref2 = CommandService.getInstance(dataIngestionService, routePlannerService);
        assertEquals("should retrieve same instance given without args", ref2, CommandService.getInstance());
        assertEquals("should retrieve same instance given args", ref2, CommandService.getInstance(dataIngestionService, routePlannerService));
    }

    @Test
    public void testReset_shouldRetrieveDifferentInstanceAfterReset() {
        CommandService ref = CommandService.getInstance();
        CommandService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, CommandService.getInstance());

        CommandService.reset();

        ref = CommandService.getInstance(dataIngestionService, routePlannerService);
        CommandService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, CommandService.getInstance(dataIngestionService, routePlannerService));
    }

    @Test
    public void testInitialiseData_shouldSetToDefaultPathAndSucceed_givenInputd() throws IOException {
        assertTrue("should succeed if no exception thrown", CommandService.getInstance(dataIngestionService, routePlannerService).initialiseData("d"));
        verify(dataIngestionService, times(1)).ingestFromFile("src/test/resources/TestStationMap.csv");
        verify(dataIngestionService, times(1)).ingestFromFile(anyString());
    }

    @Test
    public void testInitialiseData_shouldSucceed_givenValidPath() throws IOException {
        String filePath = "correctPath";

        assertTrue("should succeed if no exception thrown", CommandService.getInstance(dataIngestionService, routePlannerService).initialiseData(filePath));
        verify(dataIngestionService, times(1)).ingestFromFile(filePath);
        verify(dataIngestionService, times(1)).ingestFromFile(anyString());
    }

    @Test
    public void testInitialiseData_shouldFail_givenIOExceptionThrown() throws IOException {
        String filePath = "incorrectPath";
        doThrow(IOException.class).when(dataIngestionService).ingestFromFile(filePath);

        assertFalse("should fail due to exception thrown", CommandService.getInstance().initialiseData(filePath));
    }

    @Test
    public void testHandleCommand_shouldCallPlanRoute_givenCommandStartsWithTravelTo() throws ParseException {
        String cmd = "travel from \"Jurong East\" to \"Changi Airport\" at 2021-01-01T07:00";

        Route route = new Route();
        String result = "sample route result";

        when(routePlannerService.planRoute("Jurong East", "Changi Airport", DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm")))
                .thenReturn(route);
        when(routePlannerService.printRoute(route, "Changi Airport")).thenReturn(result);

        assertEquals("should return printRoute result", result, CommandService.getInstance(dataIngestionService, routePlannerService).handleCommand(cmd));
        verify(routePlannerService, times(1)).planRoute("Jurong East", "Changi Airport", DateUtil.parse("2021-01-01T07:00", "yyyy-MM-dd'T'HH:mm"));
        verify(routePlannerService, times(1)).printRoute(route, "Changi Airport");
        verify(routePlannerService, times(1)).planRoute(anyString(), anyString(), any(Date.class));
        verify(routePlannerService, times(1)).printRoute(any(Route.class), anyString());
    }

    @Test
    public void testHandleCommand_shouldReturnInvalidCommandGivenInvalidCommand() throws ParseException {
        String cmd = "invalid command";

        assertEquals("should return invalid command response", "Invalid command: " + cmd, CommandService.getInstance(dataIngestionService, routePlannerService).handleCommand(cmd));
        verify(routePlannerService, never()).planRoute(anyString(), anyString(), any(Date.class));
        verify(routePlannerService, never()).printRoute(any(Route.class), anyString());
    }
}
