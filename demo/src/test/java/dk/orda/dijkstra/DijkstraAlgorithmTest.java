package dk.orda.dijkstra;

import dk.orda.overlaynetwork.statistics.dijkstra.Dijkstra;
import dk.orda.overlaynetwork.statistics.dijkstra.Graph;
import dk.orda.overlaynetwork.statistics.dijkstra.Node;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DijkstraAlgorithmTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void whenSPPSolved_thenCorrect() {

        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");

        nodeA.addDestination(nodeB, 10);
        nodeA.addDestination(nodeC, 15);

        nodeB.addDestination(nodeD, 12);
        nodeB.addDestination(nodeF, 15);

        nodeC.addDestination(nodeE, 10);

        nodeD.addDestination(nodeE, 2);
        nodeD.addDestination(nodeF, 1);

        nodeF.addDestination(nodeE, 5);

        Graph graph = new Graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);

        graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);

        List<Node> shortestPathForNodeB = Arrays.asList(nodeA);
        List<Node> shortestPathForNodeC = Arrays.asList(nodeA);
        List<Node> shortestPathForNodeD = Arrays.asList(nodeA, nodeB);
        List<Node> shortestPathForNodeE = Arrays.asList(nodeA, nodeB, nodeD);
        List<Node> shortestPathForNodeF = Arrays.asList(nodeA, nodeB, nodeD);

        for (Node node : graph.getNodes()) {
            switch (node.getName()) {
                case "B":
                    assertTrue(node
                        .getShortestPath()
                        .equals(shortestPathForNodeB));
                    break;
                case "C":
                    assertTrue(node
                        .getShortestPath()
                        .equals(shortestPathForNodeC));
                    break;
                case "D":
                    assertTrue(node
                        .getShortestPath()
                        .equals(shortestPathForNodeD));
                    break;
                case "E":
                    assertTrue(node
                        .getShortestPath()
                        .equals(shortestPathForNodeE));
                    break;
                case "F":
                    assertTrue(node
                        .getShortestPath()
                        .equals(shortestPathForNodeF));
                    break;
            }
        }
    }

    @Test
    public void SimpleTestWithConsoleOutput() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");

        nodeA.addDestination(nodeB, 10);
        nodeA.addDestination(nodeC, 15);

        nodeB.addDestination(nodeD, 12);
        nodeB.addDestination(nodeF, 15);

        nodeC.addDestination(nodeE, 10);

        nodeD.addDestination(nodeE, 2);
        nodeD.addDestination(nodeF, 1);

        nodeF.addDestination(nodeE, 5);

        Graph graph = new Graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);

        graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);

        System.out.println("distance/steps to node B: " + nodeB.getDistance() + "/" + nodeB.getShortestPath().size());
        System.out.println("distance/steps to node C: " + nodeC.getDistance() + "/" + nodeC.getShortestPath().size());
        System.out.println("distance/steps to node D: " + nodeD.getDistance() + "/" + nodeD.getShortestPath().size());
        System.out.println("distance/steps to node E: " + nodeE.getDistance() + "/" + nodeE.getShortestPath().size());
        System.out.println("distance/steps to node F: " + nodeF.getDistance() + "/" + nodeF.getShortestPath().size());
    }
}
