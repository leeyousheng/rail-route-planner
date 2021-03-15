package railrouteplanner.domain.station;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

public class Station {
    private final String id;
    private final String name;
    private final Date openingDate;
    private final Collection<Station> linkedStations;

    public Station(String id, String name, Date openingDate) {
        this.id = id;
        this.name = name;
        this.openingDate = openingDate;
        this.linkedStations = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public Collection<Station> getLinkedStations() {
        return linkedStations;
    }

    public boolean addLinkedStation(Station station) {
        return linkedStations.add(station);
    }

    public String getLine() {
        return id.substring(0, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
