package railrouteplanner.application.dataingestion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import railrouteplanner.domain.date.DateUtil;
import railrouteplanner.domain.station.Station;
import railrouteplanner.domain.station.StationRepository;
import railrouteplanner.util.TestDataUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataIngestionServiceTest {
    private final StationRepository stationRepository = mock(StationRepository.class);
    private final TestDataUtil testDataUtil = new TestDataUtil();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void before() {
        DataIngestionService.reset();
        reset(stationRepository);
        testDataUtil.setFolder(folder);
    }

    @Test
    public void testGetInstance_shouldRetrieveSameInstance() {
        DataIngestionService ref = DataIngestionService.getInstance();
        assertEquals("should retrieve same instance given without args", ref, DataIngestionService.getInstance());
        assertEquals("should retrieve same instance given args", ref, DataIngestionService.getInstance(stationRepository));

        DataIngestionService.reset();

        DataIngestionService ref2 = DataIngestionService.getInstance(stationRepository);
        assertEquals("should retrieve same instance given without args", ref2, DataIngestionService.getInstance());
        assertEquals("should retrieve same instance given args", ref2, DataIngestionService.getInstance(stationRepository));
    }

    @Test
    public void testReset_shouldRetrieveDifferentInstanceAfterReset() {
        DataIngestionService ref = DataIngestionService.getInstance();
        DataIngestionService.reset();
        assertNotEquals("should retrieve different instance after reset without args", ref, DataIngestionService.getInstance());

        DataIngestionService.reset();

        ref = DataIngestionService.getInstance(stationRepository);
        DataIngestionService.reset();
        assertNotEquals("should retrieve different instance after reset", ref, DataIngestionService.getInstance(stationRepository));
    }

    @Test
    public void testIngestFromFile_givenEmptyFile() throws IOException {
        File file = testDataUtil.createTestDataFile();

        DataIngestionService.getInstance(stationRepository).ingestFromFile(file.getPath());

        verify(stationRepository, never()).addStation(anyString(), anyString(), any(Date.class), any(Station.class));
    }

    @Test
    public void test_shouldIgnoreHeaders() throws IOException, ParseException {
        File file = testDataUtil.createTestDataFile(
                "Station Code,Station Name,Opening Date",
                "NS1,Jurong East,10 March 1990",
                "Station Code,Station Name,Opening Date",
                "NS2,Bukit Batok,10 March 1990"
        );

        Station prev = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        when(stationRepository.addStation("NS1", "Jurong East", DateUtil.parse("10 March 1990"), null)).thenReturn(prev);

        DataIngestionService.getInstance(stationRepository).ingestFromFile(file.getPath());

        verify(stationRepository, times(1)).addStation("NS1", "Jurong East", DateUtil.parse("10 March 1990"), null);
        verify(stationRepository, times(1)).addStation("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"), prev);
        verify(stationRepository, times(2)).addStation(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void testIngestFromFile_givenNoHeaders() throws IOException, ParseException {
        File file = testDataUtil.createTestDataFile(
                "NS1,Jurong East,10 March 1990",
                "NS2,Bukit Batok,10 March 1990"
        );

        Station prev = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        when(stationRepository.addStation("NS1", "Jurong East", DateUtil.parse("10 March 1990"), null)).thenReturn(prev);

        DataIngestionService.getInstance(stationRepository).ingestFromFile(file.getPath());

        verify(stationRepository, times(1)).addStation("NS1", "Jurong East", DateUtil.parse("10 March 1990"), null);
        verify(stationRepository, times(1)).addStation("NS2", "Bukit Batok", DateUtil.parse("10 March 1990"), prev);
        verify(stationRepository, times(2)).addStation(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void testIngestFromFile_givenNoExactDayForOpeningDate() throws IOException, ParseException {
        File file = testDataUtil.createTestDataFile(
                "NS1,Jurong East,10 March 1990",
                "NS2,Bukit Batok,March 1990"
        );

        Station prev = new Station("NS1", "Jurong East", DateUtil.parse("10 March 1990"));
        when(stationRepository.addStation("NS1", "Jurong East", DateUtil.parse("10 March 1990"), null)).thenReturn(prev);

        DataIngestionService.getInstance(stationRepository).ingestFromFile(file.getPath());

        verify(stationRepository, times(1)).addStation("NS1", "Jurong East", DateUtil.parse("10 March 1990"), null);
        verify(stationRepository, times(1)).addStation("NS2", "Bukit Batok", DateUtil.parse("1 March 1990"), prev);
        verify(stationRepository, times(2)).addStation(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void testIngestFromFile_shouldThrowException_givenInvalidInput() throws IOException {
        File file = testDataUtil.createTestDataFile("NS1,Jurong East,10-03-1990");

        assertThrows("should throw IOException if incorrect date format", IOException.class, () -> DataIngestionService.getInstance(stationRepository).ingestFromFile(file.getPath()));
        assertThrows("should throw IOException if incorrect filepath", IOException.class, () -> DataIngestionService.getInstance(stationRepository).ingestFromFile("wrongPath"));
    }

    @Test
    public void testIngestFromFile_givenActualRailData() throws IOException {
        DataIngestionService.getInstance().ingestFromFile("src/test/resources/TestStationMap.csv");
    }
}
