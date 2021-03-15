# Rail Route Planner

## Assumptions

1. To decrease complexity of input, it is assumed that the input file stations are ordered thus no parsing of input file
   is needed.
    - Improvement: think of a way to effectively parse an unordered input.
1. If stations are not built as of that time, it is assumed that the next station is still a standard duration away. (
   eg. NE1 -> NE2 == NE1 -> NE3 whereby NE2 is not built yet)
1. Change in timing hours (peak, non-peak, night) only takes effect at the time the train starts its journey to the next
   station.
    - This means that a train might be travelling down DT line, if it have to proceed to the next station in the night
      hours, it will not be able to.

## Solution

This task can be broken down into 3 parts:

1. Ingestion of data **O(n)**
    - From the ingested data, a graph of interconnected station nodes will be created.
    - Linkage between stations should be 2 way to allow flexibility of transversal.
    - A `Map` data-structure can be used to allow for **O(1)** search of source station.
    - Ingestion on data should have a time complexity of **O(n)** if assumed input is sorted
    - Leverage on the ideology of `Dijkstra's algorithm`, a `PriorityQueue` data-structure could be used to allow the
      solution to have a time complexity of **O(nlogn)** due to insertion of nodes into the queue.
2. Pathfinding **O(nlogn)**
    - Priority queue should use the station timing as *Priority* and ordered in ascending order.
    - Outline of the flow should be as follows:
        1. Poll from `PriorityQueue`.
        2. Store into the `Route` object keeping track of visited and previous node.
        2. Skip if already visited.
        3. Check if it is the destination station. If it is the destination station, return route chain.
        4. Calculate the end time depending on time type(ie. peak, nonpeak, night) and journey type (ie. changeLine,
           ride)
            - Skip adjacent if it is not built yet and just store the next existing station into account.
        5. If destination is not reachable, `Route` will be returned with destination mapping pointing to null;
3. Output to user **0(n)**
   Path will be derived from the `Route`.
    - If path is null, display "no route found"
    - If encountered destination, display entry in the following format `<time>` - Arrived at destination.
    - If encountered a station, display entries in the following format `<time>` - `<line>`
      from `<current station name>` to `<next station name>`
    - If encountered an interchange, display entries in the following format `<time>` - Change line
      from `<current line>` to `<next line>` at `<current station name>`.

## Interaction with the application

### Pre-requisite

This application has been tested using gradle for java11. Please install java11 before starting the application.

### Start Application

Perform command `./gradlew run --console plain` in the project's root directory.

### Perform unit test

Perform command `./gradlew test` in the project's root directory.

## Architecture

### Onion Architecture

The application architecture is based on the concept of `Onion Architecture` whereby there will be domain layer and
application layer.

Generally, the domain layer is where most of the components are generic enough to be used by the top layer (Application
layer).

Therefore it can be expected that the application layer handles the integrations between domain services and application
services.

### Singleton Services

Singleton way of instantiating services follows very similar to the Spring-boot concept using the default scope
of `Singleton`.

This allows reduction of instantiation needed and do not need to overuse using `static` functions. It also allows data
to be shared between services (ie. StationRepository).
