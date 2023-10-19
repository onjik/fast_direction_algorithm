package click.porito.hierarchical_based;

import click.porito.one_by_one.Point;
import click.porito.hierarchical_based.components.Cluster;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeChecker {

    public static void main(String[] args) {


        HierarchicalClustering alg = new HierarchicalClustering();
        RouteAlgorithm routeAlgorithm = new RouteAlgorithm();
        for (int i = 5; i < 700; i += 100) {
            List<Point> points = Stream.generate(() -> Point.randomPoint(0,1000,0,1000)).limit(i).collect(Collectors.toList());
            long start = System.nanoTime();
            Cluster cluster = alg.performClustering(points);
            long clusterEnd = System.nanoTime();
            List<Point> route = routeAlgorithm.performRouting(cluster);
            long routeEnd = System.nanoTime();
            // 마이크로 초
            System.out.printf("%d, %.3f, %.3f\n", i, (clusterEnd - start) / 1e6, (routeEnd - clusterEnd) / 1e6);
        }

    }
}
