package dk.orda.dijkstra;

import dk.orda.overlaynetwork.statistics.dijkstra.Dijkstra;
import dk.orda.overlaynetwork.statistics.dijkstra.Graph;
import dk.orda.overlaynetwork.statistics.dijkstra.Node;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortestPathTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Test
    public void ValidPathTest() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");

        nodeA.addDestination(nodeB);
        nodeA.addDestination(nodeC);
//        nodeB.addDestination(nodeD);

        Graph graph = new Graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);

        graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);

        System.out.println("distance/steps to node B: " + nodeB.getDistance() + "/" + nodeB.getShortestPath().size());
        System.out.println("distance/steps to node C: " + nodeC.getDistance() + "/" + nodeC.getShortestPath().size());
        System.out.println("distance/steps to node D: " + nodeD.getDistance() + "/" + nodeD.getShortestPath().size());
        System.out.println("infinity = " + Integer.MAX_VALUE);
    }
}
