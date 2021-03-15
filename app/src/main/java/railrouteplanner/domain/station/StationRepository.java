package railrouteplanner.domain.station;

import java.util.*;

public class StationRepository {
    private static StationRepository instance;

    private final Map<String, Station> stationMap;
    private final Map<String, Collection<Station>> stationNameIndex;

    private StationRepository() {
        stationMap = new HashMap<>();
        stationNameIndex = new HashMap<>();
    }

    public static StationRepository getInstance() {
        if (instance == null)
            instance = new StationRepository();
        return instance;
    }

    /**
     * reset allows retrieval of new instance.
     * CAUTION: might cause error if used inappropriately.
     */
    public static void reset() {
        instance = null;
    }

    public Station addStation(String id, String name, Date openingDate, Station prev) {
        Station cur = stationMap.get(id);
        if (cur == null) {
            cur = new Station(id, name, openingDate);
            stationMap.put(id, cur);
        }
        stationNameIndex.putIfAbsent(name, new HashSet<>());
        stationNameIndex.get(name).add(cur);

        if (prev == null)
            return cur;

        if (prev != cur && cur.getLine().equals(prev.getLine())) {
            cur.addLinkedStation(prev);
            prev.addLinkedStation(cur);
        }
        return cur;
    }

    public Collection<Station> findByName(String name) {
        return stationNameIndex.getOrDefault(name, new HashSet<>());
    }
}
