package railrouteplanner.domain.route;

import railrouteplanner.domain.journey.JourneyType;
import railrouteplanner.domain.journey.TimeType;
import railrouteplanner.domain.station.Station;

import java.util.Date;

public class RouteNode {
    private final JourneyType journeyType;
    private final Station from;
    private final Station to;
    private final Date startTime;
    private final Date endTime;
    private final TimeType timeType;

    public RouteNode(JourneyType journeyType, Station from, Station to, Date startTime, Date endTime, TimeType timeType) {
        this.journeyType = journeyType;
        this.from = from;
        this.to = to;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeType = timeType;
    }

    public JourneyType getJourneyType() {
        return journeyType;
    }

    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    @Override
    public String toString() {
        switch (journeyType) {
            case ride:
                return String.format("%s - (%s) Take %s from %s to %s.%n", startTime, timeType, from.getLine(), from.getName(), to.getName());
            case changeLine:
                return String.format("%s - (%s) Change line from %s to %s at %s.%n", startTime, timeType, from.getLine(), to.getLine(), to.getName());
        }
        return "";
    }
}
