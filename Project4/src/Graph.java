import java.util.*;

public class Graph {

    // we need to store the nodes in a hashmap so that we can access them by their name
    public HashMap<String, Node> nodesHashMap = new HashMap<>();
    static double cost;

    public static ArrayList<Node> dijkstra(Node start) {
        cost = 0;
        ArrayList<Node> path = new ArrayList<>();

        HashSet<Node> visited = new HashSet<>();
        HashMap<Node, Node> parents = new HashMap<>();
        HashMap<Node, Double> distances = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>();
        queue.add(new NodeDistance(start, 0));

        while (!queue.isEmpty()) {
            NodeDistance nodeDistance = queue.poll();
            Node node = nodeDistance.node;
            double cost = nodeDistance.cost;
            if (visited.contains(node)) {
                continue;
            }
            if (node.getEnd()) {
                Graph.cost = cost;
                while (node != null) {
                    path.add(node);
                    node = parents.get(node);
                }
                Collections.reverse(path);
                return path;
            }
            visited.add(node);
            for (Node.Edge edge : node.adjacents) {
                Node adjacent = edge.node;
                double weight = edge.weight;
                if (!visited.contains(adjacent)) {
                    double newCost = cost + weight;
                    if (!distances.containsKey(adjacent) || distances.get(adjacent) > newCost) {
                        distances.put(adjacent, newCost);
                        parents.put(adjacent, node);
                        queue.add(new NodeDistance(adjacent, newCost));
                    }
                }
            }
        }
        cost = -1;
        return null;
    }

    static class NodeDistance implements Comparable<NodeDistance> {
        public Node node;
        public double cost;
        public NodeDistance(Node node, double distance) {
            this.node = node;
            this.cost = distance;
        }

        @Override
        public int compareTo(NodeDistance nodeDistance) {
            return Double.compare(cost, nodeDistance.cost);
        }
    }
}
