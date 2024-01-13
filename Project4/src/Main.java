import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Osman Selim Yüksel
 */

// thanks CEK!

public class Main {
    static String airports_csv = "cases/airports/INTER-3.csv";
    static String directions_csv = "cases/directions/INTER-3.csv";
    static String weather_csv = "cases/weather.csv";
    static String missions_in = "cases/missions/INTER-3.in";
    static String task1_out = "task1.out";
    static String task2_out = "task2.out";

    static FileWriter fileWriter1;
    static FileWriter fileWriter2;
    static HashMap<String, Integer> weatherHashmap = new HashMap<>();  // key: airport name + time, value: weather code
    static HashMap<String, Airport> airportHashmap = new HashMap<>();  // key: airport code, value: airport object having all the info about the airport
    static HashMap<String, ArrayList<Airport>> directionsHashmap = new HashMap<>();  // key: airport code, value: list of airport codes that can be reached from the key airport
    static int planeModel;

    public static void main(String[] args) throws Exception {

        // java Main <airports-csv> <directions-csv> <weather-csv> <missions-in> <task1-out> <task2-out>
        // our code should execute with the command above

        airports_csv = args[0];
        directions_csv = args[1];
        weather_csv = args[2];
        missions_in = args[3];
        task1_out = args[4];
        task2_out = args[5];

        fileWriter1 = new FileWriter(task1_out);
        fileWriter2 = new FileWriter(task2_out);

        read();

        fileWriter1.close();  // do not forget to flush each time you write!
        fileWriter2.close();  // do not forget to flush each time you write!

    }
    private static void read() throws IOException {

        // we will read the weather.csv file
        // for each airfield name, we will create hashmap
        // key: time, value: weather code
        // we will store this hashmap in a hashmap
        // key: airfield name, value: hashmap

        Scanner scanner = new Scanner(new File(weather_csv));
        scanner.nextLine();
        // create the ultimate hashmap
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] weatherInfo = line.split(",");

            String airfieldName = weatherInfo[0];
            int time = Integer.parseInt(weatherInfo[1]) / 21600; // convert the time to 6 hour intervals
            int weatherCode = Integer.parseInt(weatherInfo[2]);
            String key = airfieldName + time;
            weatherHashmap.put(key, weatherCode);

        }

        // read the airports.csv file
        Scanner scanner2 = new Scanner(new File(airports_csv));
        scanner2.nextLine();

        // file format:AirportCode,AirfieldName,Latitude,Longitude,ParkingCost
        while (scanner2.hasNextLine()){
            String line = scanner2.nextLine();
            String[] airportInfo = line.split(",");
            String airportCode = airportInfo[0];
            String airfieldName = airportInfo[1];
            double latitude = Double.parseDouble(airportInfo[2]);
            double longitude = Double.parseDouble(airportInfo[3]);
            int parkingCost = Integer.parseInt(airportInfo[4]);
            Airport airport = new Airport(airportCode, airfieldName, latitude, longitude, parkingCost);
            // add the airport to the airport hashmap
            airportHashmap.put(airportCode, airport);

        }


        // read the directions.csv file
        Scanner scanner3 = new Scanner(new File(directions_csv));
        scanner3.nextLine();
        // file format: from,to  ---> LTBZ, LTAJ

        while (scanner3.hasNextLine()){
            String line = scanner3.nextLine();
            String[] directionInfo = line.split(",");
            String from = directionInfo[0];
            String to = directionInfo[1];
            // get the airport object from the airport hashmap
            Airport fromAirport = airportHashmap.get(from);
            Airport toAirport = airportHashmap.get(to);

            // add the from airport to the directions hashmap
            if (directionsHashmap.containsKey(fromAirport.airportCode)){
                directionsHashmap.get(fromAirport.airportCode).add(toAirport);
            } else {
                ArrayList<Airport> airports = new ArrayList<>();
                airports.add(toAirport);
                directionsHashmap.put(fromAirport.airportCode, airports);
            }

        }

        // read the plane model
        Scanner scanner4 = new Scanner(new File(missions_in));

        // Carreidas 160  --> 1
        // Orion III   --> 2
        // Skyfleet S570  --> 3
        // T-16 Skyhopper  --> 4
        String s = scanner4.nextLine();
        if (s.startsWith("C")){
            planeModel = 1;
        } else if (s.startsWith("O")){
            planeModel = 2;
        } else if (s.startsWith("S")){
            planeModel = 3;
        } else if (s.startsWith("T")){
            planeModel = 4;
        }

        // read the missions.in
        // file format : TR-0044 LTAB 1681959600 1682326800   ||   fromAirportCode, toAirportCode, timeOrigin, deadline
        while (scanner4.hasNextLine()){
            String line = scanner4.nextLine();
            String[] missionInfo = line.split(" ");
            String fromAirportCode = missionInfo[0];
            String toAirportCode = missionInfo[1];
            int timeOrigin = Integer.parseInt(missionInfo[2]) / 21600;
            int deadline = Integer.parseInt(missionInfo[3]) / 21600;

            // task 1
            task1(fromAirportCode, toAirportCode, timeOrigin);
            // task 2
            task2(fromAirportCode, toAirportCode, timeOrigin, deadline);
        }
    }



    private static void task2(String fromAirportCode, String toAirportCode, int timeOrigin, int deadline) throws IOException {
        int numOfTimeFrames = (deadline - timeOrigin) + 1;
        // the only difference between task 1 and task2 is that we need to take the time into account
        // note that time changes the weather, which also changes the cost of the edge
        // our solution is that for each time frame, an airport will be represented by a different node
        // for example, if we have 3 time frames, and we have 2 airports, we will have 6 nodes
        // and parking operation will be represented by an edge from node A_0 to node A_1

        // create the graph
        Graph graph = new Graph();
        for (String airportCode : airportHashmap.keySet()){

            for (int i = 0; i < numOfTimeFrames; i++) {
                Node node = new Node(airportCode);
                node.timeFrame = timeOrigin + i;
                graph.nodesHashMap.put(airportCode+"-"+i, node);
            }
        }


        // first we need to add the edges to represent the parking operation
        // we will add the edges to the nodes
        // we will calculate the cost of the edges and add it to the edge object
        for (String airportCode : airportHashmap.keySet()){
            // get the airport object from the airport hashmap
            Airport airport = airportHashmap.get(airportCode);
            // get the parking cost of the airport
            int parkingCost = airport.parkingCost;
            // traverse the time frames
            for (int i = 0; i < numOfTimeFrames-1; i++) {
                Node node = graph.nodesHashMap.get(airportCode+"-"+i);
                // get the node from the graph
                Node adjacent = graph.nodesHashMap.get(airportCode+"-"+(i+1));
                // if assert fails, it means that the airport is not in the graph
                assert adjacent != null;
                // calculate the cost of the edge, which is the parking cost
                double cost = parkingCost;
                // add the edge to the node, representing the parking operation
                node.addAdjacent(adjacent, cost);
            }
        }

        // now we need to add the edges to represent the flights
        // we need to take the time into account
        // which means that if it takes 2 time frames to go from airport A to airport B, we need to add an edge from airport A_i to airport B_i+2
        for (String airportCode : directionsHashmap.keySet()){

            // get the list of airports that can be reached from this airport
            ArrayList<Airport> airports = directionsHashmap.get(airportCode);
            // traverse the list of airports
            // the only thing to pay attention is that if i flight takes say,6 hours, we need to add an edge from airport A_i to airport B_i+6
            for (int i = 0; i < numOfTimeFrames; i++) {
                // get the node from the graph
                Node node = graph.nodesHashMap.get(airportCode+"-"+i);
                if (airportCode.equals(toAirportCode)) node.setEnd();

                for (Airport adjacentAirport : airports){
                    double distance = calculateDistance(airportHashmap.get(airportCode), adjacentAirport);
                    int time = calculateTime(distance)/6;

                    if (i+time >= numOfTimeFrames) continue;

                    // get the node from the graph
                    Node adjacent = graph.nodesHashMap.get(adjacentAirport.airportCode + "-" + (time+i));
                    // calculate the cost of the edge
                    double cost = calculateCost(airportHashmap.get(airportCode), adjacentAirport, timeOrigin+i, timeOrigin+i+time);
                    // add the edge to the node
                    node.addAdjacent(adjacent, cost);
                }
            }
        }

        // now we have the graph, we can find the shortest path
        Node start = graph.nodesHashMap.get(fromAirportCode+"-"+0);

        String last = "";
        ArrayList<Node> path = Graph.dijkstra(start);
        if (path == null) {
            System.out.println("No possible solution.");
            fileWriter2.write("No possible solution.\n");
            fileWriter2.flush();
            return;
        }
        for (Node node : path){
            System.out.print(node.name + " ");
            if (last.equals(node.name))
                fileWriter2.write("PARK ");
            else
                fileWriter2.write(node.name + " ");
            last = node.name;
            fileWriter2.flush();
        }

        // we need to round the distance to 5 decimal point before printing
        // if the number is 1.223 we need to print 1.22300
        // if the number is 1.223456 we need to print 1.22346
        double cost = Math.round(Graph.cost * 100000.0) / 100000.0;
        String stringCost = cost + "";
        while (stringCost.split("\\.")[1].length() < 5){
            stringCost += "0";
        }
        System.out.println(stringCost);
        fileWriter2.write(stringCost + "\n");
        fileWriter2.flush();

    }
    private static void task1(String fromAirportCode, String toAirportCode, int timeOrigin) throws IOException {

        // create the graph
        // add the nodes
        // add the edges, while adding the edges, calculate the cost of the edge and add it to the edge object
        // find the shortest path

        Graph graph = new Graph();

        // traverse the airports hashmap
        for (String airportCode : airportHashmap.keySet()){
            // create a node for each airport
            Node node = new Node(airportCode);
            graph.nodesHashMap.put(airportCode, node);

        }

        // traverse the directions hashmap
        for (String airportCode : directionsHashmap.keySet()){

            // get the node from the graph
            Node node = graph.nodesHashMap.get(airportCode);

            // get the list of airports that can be reached from this airport
            ArrayList<Airport> airports = directionsHashmap.get(airportCode);

            // traverse the list of airports
            for (Airport adjacentAirport : airports){
                // get the node from the graph
                Node adjacent = graph.nodesHashMap.get(adjacentAirport.airportCode);
                // calculate the cost of the edge
                double cost = calculateCost(airportHashmap.get(airportCode), adjacentAirport, timeOrigin, timeOrigin);
                // add the edge to the node
                node.addAdjacent(adjacent, cost);

            }
        }

        // now we have the graph, we can find the shortest path
        Node start = graph.nodesHashMap.get(fromAirportCode);
        Node end = graph.nodesHashMap.get(toAirportCode);
        end.setEnd();

        for (Node node : Graph.dijkstra(start)){
            System.out.print(node.name + " ");
            fileWriter1.write(node.name + " ");
            fileWriter1.flush();
        }

        // we need to round the distance to 5 decimal point before printing
        // if the number is 1.223 we need to print 1.22300
        // if the number is 1.223456 we need to print 1.22346
        double cost = Math.round(Graph.cost * 100000.0) / 100000.0;
        String stringCost = cost + "";
        while (stringCost.split("\\.")[1].length() < 5){
            stringCost += "0";
        }
        System.out.println(stringCost);
        fileWriter1.write(stringCost + "\n");
        fileWriter1.flush();

    }

    private static double calculateCost(Airport from, Airport to, int timeOriginFrom, int timeOriginTo){

        // calculate the weather multiplier
        Airport fromAirport = airportHashmap.get(from.airportCode);
        String fromKey = fromAirport.airfieldName + timeOriginFrom;
        int fromWeatherCode = weatherHashmap.get(fromKey);

        Airport toAirport = airportHashmap.get(to.airportCode);
        String toKey = toAirport.airfieldName + timeOriginTo;
        int toWeatherCode = weatherHashmap.get(toKey);

        double W_D = CalculateWeatherMultiplier(fromWeatherCode);
        double W_L = CalculateWeatherMultiplier(toWeatherCode);

        double distance = calculateDistance(from, to);

        return 300 * W_L * W_D + distance;
    }

    private static double CalculateWeatherMultiplier(int weatherCode){
        // we need to convert the weather code to a binary number
        String binaryString = Integer.toBinaryString(weatherCode);
        // this number should be 5 digits long
        // if it is not, we need to add zeros to the beginning
        while (binaryString.length() < 5){
            binaryString = "0" + binaryString;
        }

        int B_w = Integer.parseInt(binaryString.substring(0, 1));
        int B_r = Integer.parseInt(binaryString.substring(1, 2));
        int B_s = Integer.parseInt(binaryString.substring(2, 3));
        int B_h = Integer.parseInt(binaryString.substring(3, 4));
        int B_b = Integer.parseInt(binaryString.substring(4, 5));

        double W = (B_w*1.05 + (1 - B_w))*
                (B_r*1.05 + (1 - B_r))*
                (B_s*1.10 + (1 - B_s))*
                (B_h*1.15 + (1 - B_h))*
                (B_b*1.20 + (1 - B_b));

        return W;
    }

    private static double calculateDistance(Airport from, Airport to){
        double lat1 = from.latitude;
        double lon1 = from.longitude;
        double lat2 = to.latitude;
        double lon2 = to.longitude;
        double R = 6371; // kilometres
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2-lat1);
        double deltaLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);

        return 2 * R * Math.asin(Math.sqrt(a));
    }

    private static int calculateTime(double distance){

        if (planeModel == 1){
            if (distance <= 175)
                return 6;
            else if (175<distance && distance<=350)
                return 12;
            else
                return 18;

        } else if (planeModel == 2){
            if (distance <= 1500)
                return 6;
            else if (1500<distance && distance<=3000)
                return 12;
            else
                return 18;

        } else if (planeModel == 3){
            if (distance <= 500)
                return 6;
            else if (500<distance && distance<=1000)
                return 12;
            else
                return 18;

        } else {
            if (distance <= 2500)
                return 6;
            else if (2500<distance && distance<=5000)
                return 12;
            else
                return 18;
        }

    }
}