package click.porito.one_by_one.simple_linear;

import click.porito.one_by_one.PathManager;
import click.porito.one_by_one.Point;

import java.util.LinkedList;
import java.util.List;

/**
 * 단순하게 하나씩 비교하면서 가장 최적의 삽입 위치를 찾는 방식
 *
 */
public class LinearPathManager implements PathManager {
    private List<Point> path;

    public LinearPathManager(List<Point> path) {
        this.path = path;
    }

    public LinearPathManager() {
        this.path = new LinkedList<>();
    }

    @Override
    public List<Point> getPath() {
        return path;
    }

    @Override
    public void setPath(List<Point> path) {
        this.path = path;
    }

    @Override
    public int addPoint(Point point) {
        if (path.size() < 2) {
            path.add(point);
            return path.indexOf(point);
        }
        double minDistance = Double.MAX_VALUE;
        int insertAt = -1;
        //양끝 값 비교
        double headDl = path.get(0).distance(point);
        if (headDl < minDistance) {
            minDistance = headDl;
            insertAt = 0;
        }
        double tailDl = path.get(path.size() - 1).distance(point);
        if (tailDl < minDistance) {
            minDistance = tailDl;
            insertAt = path.size();
        }
        // 중간 값 비교
        for (int i = 0; i < path.size() - 1; i++) {
            //원래 경로와 이것을 끼어넣었을 때 경로의 길이를 비교한다.
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            double baseDistance = p1.distance(p2);
            double newDistance = p1.distance(point) + point.distance(p2);
            double dl = newDistance - baseDistance;
            if (dl < minDistance) {
                minDistance = dl;
                insertAt = i + 1;
            }
        }
        path.add(insertAt, point);
        return insertAt;
    }

    @Override
    public void clear() {
        path.clear();
    }
}
