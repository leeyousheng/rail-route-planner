package railrouteplanner.application.dataingestion;

import railrouteplanner.domain.station.Station;
import railrouteplanner.domain.station.StationRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataIngestionService {
    private static DataIngestionService instance;
    private final StationRepository stationRepository;
    private final SimpleDateFormat simpleDateFormat;

    private DataIngestionService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
        this.simpleDateFormat = new SimpleDateFormat("dd MMMMM yyyy");
    }

    public static DataIngestionService getInstance() {
        return getInstance(StationRepository.getInstance());
    }

    public static DataIngestionService getInstance(StationRepository stationRepository) {
        if (instance == null)
            instance = new DataIngestionService(stationRepository);
        return instance;
    }

    /**
     * reset allows retrieval of new instance.
     * CAUTION: might cause error if used inappropriately.
     */
    public static void reset() {
        instance = null;
    }

    public void ingestFromFile(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        String line = br.readLine();
        Station prev = null;
        while (line != null) {
            if (!line.equals("Station Code,Station Name,Opening Date")) { // ignore header
                // station details: 0 - code, 1 - name, 2 - opening date
                String[] stationDetails = line.split(",");

                try {
                    String actualDateStr = patchDate(stationDetails[2]);
                    Date openingDate = simpleDateFormat.parse(actualDateStr);
                    prev = stationRepository.addStation(stationDetails[0], stationDetails[1], openingDate, prev);
                } catch (ParseException e) {
                    throw new IOException(e.getMessage());
                }
            }

            line = br.readLine();
        }

    }

    private String patchDate(String dateStr) {
        if (dateStr.charAt(0) >= '0' && dateStr.charAt(0) <= '9')
            return dateStr;

        return "1 " + dateStr;
    }
}
