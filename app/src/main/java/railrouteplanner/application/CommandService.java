package railrouteplanner.application;

import railrouteplanner.application.dataingestion.DataIngestionService;
import railrouteplanner.application.routeplanner.RoutePlannerService;
import railrouteplanner.domain.date.DateUtil;
import railrouteplanner.domain.route.Route;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class CommandService {
    private static CommandService instance;
    private final DataIngestionService dataIngestionService;
    private final RoutePlannerService routePlannerService;

    private CommandService(DataIngestionService dataIngestionService, RoutePlannerService routePlannerService) {
        this.dataIngestionService = dataIngestionService;
        this.routePlannerService = routePlannerService;
    }

    /**
     * reset allows retrieval of new instance.
     * CAUTION: might cause error if used inappropriately.
     */
    public static void reset() {
        instance = null;
    }

    public static CommandService getInstance() {
        return getInstance(DataIngestionService.getInstance(), RoutePlannerService.getInstance());
    }

    public static CommandService getInstance(DataIngestionService dataIngestionService, RoutePlannerService routePlannerService) {
        if (instance == null)
            instance = new CommandService(dataIngestionService, routePlannerService);
        return instance;
    }

    public String handleCommand(String command) throws ParseException {
        String[] commandParts = command.split("\"");
        switch (commandParts[0]) {
            case "travel from ":
                String sourceName = commandParts[1];
                String destName = commandParts[3];
                Date startTime = DateUtil.parse(commandParts[4].split("at ")[1], "yyyy-MM-dd'T'HH:mm");
                Route route = routePlannerService.planRoute(sourceName, destName, startTime);
                return routePlannerService.printRoute(route, destName);
            default:
                return "Invalid command: " + commandParts[0];
        }
    }

    /**
     * initialiseData ingests the data using filepath and populate the station repository
     *
     * @param filepath to source file
     * @return true when data is initialised successfully
     */
    public boolean initialiseData(String filepath) {
        if (filepath.equals("d")) // load default from requirements
            filepath = "src/test/resources/TestStationMap.csv";

        try {
            dataIngestionService.ingestFromFile(filepath);
            System.out.println("data initialisation success");
            return true;
        } catch (IOException e) {
            System.out.printf("invalid path: %s%n%n", filepath);
        }

        return false;
    }
}
