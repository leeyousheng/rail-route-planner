package railrouteplanner.application;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {
    private final CommandService commandService = mock(CommandService.class);
    private final Scanner sc = mock(Scanner.class);

    @Before
    public void before() {
        ApplicationService.reset();
        reset(commandService, sc);
    }

    @Test
    public void testGetInstance_shouldRetrieveSameInstance() {
        ApplicationService ref = ApplicationService.getInstance();
        assertEquals("should retrieve same instance given without args", ref, ApplicationService.getInstance());
        assertEquals("should retrieve same instance given args", ref, ApplicationService.getInstance(commandService, sc));

        ApplicationService.reset();

        ApplicationService ref2 = ApplicationService.getInstance(commandService, sc);
        assertEquals("should retrieve same instance given without args", ref2, ApplicationService.getInstance());
        assertEquals("should retrieve same instance given args", ref2, ApplicationService.getInstance(commandService, sc));
    }

    @Test
    public void testReset_shouldRetrieveDifferentInstanceAfterReset() {
        ApplicationService ref = ApplicationService.getInstance();
        ApplicationService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, ApplicationService.getInstance());

        ApplicationService.reset();

        ref = ApplicationService.getInstance(commandService, sc);
        ApplicationService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, ApplicationService.getInstance(commandService, sc));
    }

    @Test
    public void testRun_shouldInitialiseDataAndHandleCommand_givenDataInitSuccess() throws ParseException {
        String filepath = "correctPath";
        String travelCmd = "travel from Jurong East to Bukit Batok at 2021-01-01T11:11";

        when(sc.nextLine()).thenReturn(filepath, travelCmd, "quit");
        when(commandService.initialiseData(filepath)).thenReturn(true);

        ApplicationService.getInstance(commandService, sc).run();

        verify(commandService, times(1)).initialiseData(filepath);
        verify(commandService, times(1)).initialiseData(anyString());
        verify(commandService, times(1)).handleCommand(travelCmd);
        verify(commandService, times(1)).handleCommand(anyString());
    }

    @Test
    public void testRun_shouldInitialiseData_givenInitialDataInitFailedButSubsequentPasses() throws ParseException {
        String failedPath = "wrongPath";
        String successPath = "correctPath";
        String travelCmd = "travel from Jurong East to Bukit Batok at 2021-01-01T11:11";

        when(sc.nextLine()).thenReturn(failedPath, successPath, travelCmd, travelCmd, "quit");
        when(commandService.initialiseData(failedPath)).thenReturn(false);
        when(commandService.initialiseData(successPath)).thenReturn(true);

        ApplicationService.getInstance(commandService, sc).run();

        verify(commandService, times(1)).initialiseData(failedPath);
        verify(commandService, times(1)).initialiseData(successPath);
        verify(commandService, times(2)).initialiseData(anyString());
        verify(commandService, times(2)).handleCommand(travelCmd);
        verify(commandService, times(2)).initialiseData(anyString());
    }

    @Test
    public void testRun_shouldSkipCommandHandling_givenDataNotInitialised() throws ParseException {
        String failedPath = "wrongPath";

        when(sc.nextLine()).thenReturn(failedPath, failedPath, "quit");
        when(commandService.initialiseData(failedPath)).thenReturn(false);

        ApplicationService.getInstance(commandService, sc).run();

        verify(commandService, times(2)).initialiseData(failedPath);
        verify(commandService, times(2)).initialiseData(anyString());
        verify(commandService, never()).handleCommand(anyString());
    }

    @Test
    public void testRun_shouldSkipCommandHandling_givenTerminateCommand() throws ParseException {
        String filePath = "correctPath";

        when(sc.nextLine()).thenReturn(filePath, "quit");
        when(commandService.initialiseData(filePath)).thenReturn(true);

        ApplicationService.getInstance(commandService, sc).run();

        verify(commandService, times(1)).initialiseData(filePath);
        verify(commandService, times(1)).initialiseData(anyString());
        verify(commandService, never()).handleCommand(anyString());
    }

    @Test
    public void testRun_shouldSkipDataInitialisation_givenTerminateCommand() throws ParseException {
        when(sc.nextLine()).thenReturn("quit");

        ApplicationService.getInstance(commandService, sc).run();

        verify(commandService, never()).initialiseData(anyString());
        verify(commandService, never()).handleCommand(anyString());
    }
}
