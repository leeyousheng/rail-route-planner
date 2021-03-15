package railrouteplanner.application.routeplanner;

import railrouteplanner.domain.journey.JourneyCalculationService;
import railrouteplanner.domain.journey.JourneyType;
import railrouteplanner.domain.journey.TimeType;
import railrouteplanner.domain.route.Route;
import railrouteplanner.domain.route.RouteNode;
import railrouteplanner.domain.station.Station;
import railrouteplanner.domain.station.StationRepository;

import java.util.*;

public class RoutePlannerService {
    private static RoutePlannerService instance;
    private final StationRepository stationRepository;
    private final JourneyCalculationService journeyCalculationService;

    private RoutePlannerService(StationRepository stationRepository, JourneyCalculationService journeyCalculationService) {
        this.stationRepository = stationRepository;
        this.journeyCalculationService = journeyCalculationService;
    }

    public static RoutePlannerService getInstance() {
        return getInstance(StationRepository.getInstance(), JourneyCalculationService.getInstance());
    }

    public static RoutePlannerService getInstance(StationRepository stationRepository, JourneyCalculationService journeyCalculationService) {
        if (instance == null)
            instance = new RoutePlannerService(stationRepository, journeyCalculationService);
        return instance;
    }

    /**
     * reset allows retrieval of new instance.
     * CAUTION: might cause error if used inappropriately.
     */
    public static void reset() {
        instance = null;
    }

    public Route planRoute(String sourceStationName, String destinationStationName, Date startTime) {
        PriorityQueue<RouteNode> pq = new PriorityQueue<>(Comparator.comparing(RouteNode::getEndTime));
        Route route = new Route();

        Collection<Station> sourceStations = stationRepository.findByName(sourceStationName);
        Collection<Station> destinationStations = stationRepository.findByName(destinationStationName);
        if (sourceStations.isEmpty() || destinationStations.isEmpty())
            return route;

        // Seed with initial options, can be multiple if journey starts at interchange
        sourceStations
                .stream()
                .filter(station -> startTime.after(station.getOpeningDate()))
                .forEach(station -> pq.offer(new RouteNode(JourneyType.ride, null, station, null, startTime, TimeType.get(startTime))));

        while (!pq.isEmpty()) {
            RouteNode node = pq.poll();
            if (!route.addNode(node))
                continue;

            Station cur = node.getTo();
            if (cur.getName().equals(destinationStationName)) {
                return route;
            }

            cur.getLinkedStations().stream()
                    .filter(station -> station != cur)
                    .map(station -> fastForwardToOpenedStation(station, cur, node.getEndTime()))
                    .filter(Objects::nonNull)
                    .forEach(station -> {
                        Date endTime = journeyCalculationService.calculateJourneyEndTime(JourneyType.ride, node.getEndTime(), cur.getLine());
                        if (endTime != null)
                            pq.offer(new RouteNode(JourneyType.ride, cur, station, node.getEndTime(), endTime, TimeType.get(node.getEndTime())));
                    });

            stationRepository.findByName(cur.getName()).stream()
                    .filter(station -> station != cur && station.getOpeningDate().before(node.getEndTime()))
                    .forEach(station -> {
                        Date endTime = journeyCalculationService.calculateJourneyEndTime(JourneyType.changeLine, node.getEndTime(), cur.getLine());
                        if (endTime != null)
                            pq.offer(new RouteNode(JourneyType.changeLine, cur, station, node.getEndTime(), endTime, TimeType.get(node.getEndTime())));
                    });
        }

        return route;
    }

    public String printRoute(Route route, String destinationName) {
        List<RouteNode> path = stationRepository.findByName(destinationName).stream()
                .map(route::getPath)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);

        if (path == null)
            return "Destination could not be reached.";

        if (path.size() == 0)
            return "Already at destination.";

        StringBuilder sb = new StringBuilder();
        RouteNode destNode = path.get(path.size() - 1);
        sb.append(String.format("Journey to %s (Estimated arrival - %s):%n", destinationName, destNode.getEndTime()));
        path.forEach(sb::append);
        sb.append(String.format("%s - Arrived at %s.", destNode.getEndTime(), destNode.getTo().

                getName()));
        return sb.toString();
    }

    private Station fastForwardToOpenedStation(Station cur, Station prev, Date start) {
        while (cur.getOpeningDate().after(start)) {
            Station next = null;
            for (Station s : cur.getLinkedStations()) {
                if (s == prev)
                    continue;

                next = s;
                break;
            }

            if (next == null)
                return null;

            prev = cur;
            cur = next;

        }
        return cur;
    }
}
