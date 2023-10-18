package click.porito.hierarchical_based.components;

import click.porito.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Cluster {
    @Setter
    private Cluster parent;
    private List<Cluster> children;

    private double distance;
    private Point centroid;

    public boolean isLeaf() {
        return children.isEmpty();
    }
    public boolean isRoot(){
        return parent == null;
    }

    public double centroidDistance(Cluster other) {
        return centroid.distance(other.centroid);
    }

    public double minDistance(Cluster other){
        //양측 모든 점중 가장 가까운 점 사이의 거리
        List<Point> otherPoints = other.getPoints();
        List<Point> points = getPoints();
        double min = Double.MAX_VALUE;
        for (var p1 : points) {
            for (var p2 : otherPoints) {
                double distance = p1.distance(p2);
                if (distance < min) {
                    min = distance;
                }
            }
        }
        return min;
    }




    public List<Cluster> getAllContainedCluster(){
        if (isLeaf()) {
            return Collections.singletonList(this);
        } else {
            List<Cluster> clusters = new LinkedList<>();
            clusters.add(this);
            for (var child : children) {
                clusters.addAll(child.getAllContainedCluster());
            }
            return clusters;
        }
    }

    public List<Point> getPoints() {
        if (isLeaf()) {
            return Collections.singletonList(centroid);
        }
        List<Point> points = new LinkedList<>();
        for (var child : children) {
            points.addAll(child.getPoints());
        }
        return points;
    }

    public double getSumOfSquaredError(Point point) {
        return getPoints().stream()
                .mapToDouble(p -> p.distance(point))
                .map(d -> d * d)
                .sum();
    }

    public double getSumOfSquaredError(){
        return getSumOfSquaredError(centroid);
    }

    public double getWardDistance(Cluster other) {
        double before = this.getSumOfSquaredError() + other.getSumOfSquaredError();
        // calculate new centroid
        Point centroid = Point.centroidFromClusters(List.of(this, other));
        double after = this.getSumOfSquaredError(centroid) + other.getSumOfSquaredError(centroid);
        return after - before;
    }

    public int getPointCount() {
        return getPoints().size();
    }

    public Cluster(List<Cluster> children,double distance, Point centroid) {
        this.children = children;
        this.distance = distance;
        this.centroid = centroid;
    }

    public static Cluster fromClusters(List<Cluster> children, double distance) {
        // calculate points
        List<Point> points = children.stream()
                .map(Cluster::getPoints)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        // calculate new centroid
        Point centroid = Point.centroid(points);

        Cluster cluster = new Cluster(children, distance, centroid);
        children.forEach(c -> c.setParent(cluster));
        return cluster;
    }

    public static Cluster leaf(Point point) {
        return new Cluster(Collections.emptyList(), 0, point);
    }




}
