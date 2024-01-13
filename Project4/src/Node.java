import java.util.ArrayList;

public class Node {
    public int timeFrame;
    public ArrayList<Edge> adjacents;
    private boolean isEnd;
    public String name;
    public Node(String name) {
        this.name = name;
        adjacents = new ArrayList<>();
        isEnd = false;
    }

    public void setEnd() {
        isEnd = true;
    }

    public void addAdjacent(Node node, double cost) {
        adjacents.add(new Edge(node, cost));
    }

    class Edge {
        public Node node;
        public double weight;
        public Edge(Node node, double weight) {
            this.node = node;
            this.weight = weight;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean getEnd(){
        return isEnd;
    }
}