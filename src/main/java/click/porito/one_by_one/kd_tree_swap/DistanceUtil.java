package click.porito.one_by_one.kd_tree_swap;

import click.porito.one_by_one.Point;

import java.util.List;

public class DistanceUtil {
    public static double distanceOfPath(List<Point> points) {
        double sum = 0;
        //하나씩 줄여가면서, 두개씩 묶어서 거리를 구한다.
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            sum += p1.distance(p2);
        }
        return sum;
    }
}
