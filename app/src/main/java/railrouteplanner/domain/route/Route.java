package railrouteplanner.domain.route;

import railrouteplanner.domain.station.Station;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Route {
    private final Map<String, RouteNode> route;

    public Route() {
        this.route = new HashMap<>();
    }

    public boolean addNode(RouteNode node) {
        return route.putIfAbsent(node.getTo().getId(), node) == null;
    }

    public List<RouteNode> getPath(Station destination) {
        RouteNode cur = route.get(destination.getId());
        if (cur == null)
            return null;

        LinkedList<RouteNode> path = new LinkedList<>();
        while (cur.getFrom() != null) {
            path.addFirst(cur);
            cur = route.get(cur.getFrom().getId());
        }
        return path;
    }
}
