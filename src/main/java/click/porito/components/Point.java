package click.porito.components;

import lombok.Getter;
import lombok.Setter;

import java.awt.geom.Line2D;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Getter
@Setter
public final class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public void addToX(double x) {
        this.x += x;
    }

    public void addToY(double y) {
        this.y += y;
    }

    public static Point randomPoint(double minX, double maxX, double minY, double maxY) {
        Random random = new Random();
        double x = random.nextDouble(minX, maxX);
        double y = random.nextDouble(minY, maxY);
        return new Point(x, y);
    }

    public static Point centroid(List<Point> points) {
        Point centroid = new Point(0, 0);
        for (var point : points) {
            centroid.addToX(point.getX());
            centroid.addToY(point.getY());
        }
        centroid.setX(centroid.getX() / points.size());
        centroid.setY(centroid.getY() / points.size());
        return centroid;
    }

    public static Point centroidFromClusters(List<Cluster> clusters){
        List<Point> points = clusters.stream().map(Cluster::getPoints).flatMap(List::stream).toList();
        return centroid(points);
    }

    public Line2D getLineTo(Point other) {
        return new Line2D.Double(this.x, this.x, other.getX(), other.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Point) obj;
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(that.x) &&
                Double.doubleToLongBits(this.y) == Double.doubleToLongBits(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

}
