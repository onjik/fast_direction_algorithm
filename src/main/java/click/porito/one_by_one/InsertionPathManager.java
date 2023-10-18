package click.porito.one_by_one;

import click.porito.hierarchical_based.components.Cluster;
import click.porito.hierarchical_based.components.ConsoleColors;
import click.porito.Point;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
어디에 추가될지 결정하며 경로를 만들어 간다.
- 가장 가까운 점을 찾는다.
- 그 점, 그리고 그 점과 연결된 점, 그리고 맨 양쪽 맨 끝에 대해서, 최단 거리 순열을 찾는다.
 */
public class InsertionPathManager {

    private LinkedList<Point> path;
    private KdTree kdTree;

    public InsertionPathManager(LinkedList<Point> path) {
        this.path = path;
        kdTree = new KdTree();
    }

    public int addPoint(Point point){
        int minIndex = -1;
        if (path.size() < 2) {
            path.add(point);
            minIndex = path.indexOf(point);
        } else {
            //가장 가까운 점을 찾는다.
            minIndex = nearestIndex(point);
//            System.out.println(path.get(minIndex) + " is nearest to " + point);
            //중간에 선과 교차하는지 검사
            Point nearest = path.get(minIndex);

            int nextIndex, previousIndex;
            if (insertOnFront(minIndex, point)){
                //앞쪽에 추가한다면?
                previousIndex = minIndex - 1;
                nextIndex = minIndex + 1;
                path.add(minIndex, point);
            } else {
                //뒤쪽에 추가한다면?
                previousIndex = minIndex;
                nextIndex = minIndex + 2;
                path.add(minIndex + 1, point);
            }
            previousIndex = Math.max(previousIndex, 0);
            nextIndex = Math.min(nextIndex, path.size() - 1);

//            //실 풀기 작업을 진행한다. 꼬인 부분이 있으면 순서를 바꾼다.
//            for (int i = previousIndex; i < nextIndex; i++) {
//                for (int j = 0; j < path.size() - 1; j++){
//                    if (i == j || i == j - 1 || i == j + 1)continue;
//                    Point point1 = path.get(i);
//                    Point point2 = path.get(i+1);
//                    Point point3 = path.get(j);
//                    Point point4 = path.get(j+1);
//                    //1-2, 3-4 가 서로 교차되었으면, 2 에서 3까지를 통째로 뒤집는다.
//                    if (doIntersect(point1, point2, point3, point4)) {
//                        System.out.println(point1 + " " + point2 + " " + point3 + " " + point4 + " is intersected");
//                        //일단 복사하고 뒤집는다.
//                        int from = Math.min(i + 1, j);
//                        int to = Math.max(i + 1, j +1);
//                        List<Point> subList = path.subList(from, to);
//                        Collections.reverse(subList);
//                        //뒤집은 것을 딱 그 위치 만큼 덮어씌운다.
//                        for (int k = 0; k < subList.size(); k++) {
//                            path.set(from+k, subList.get(k));
//                        }
//                        j --;
//                    System.out.println(ConsoleColors.RED_BOLD + "swaped " + point2 + " to "+  point3 + ConsoleColors.RESET);
//                    }
//                }
//            }

            //TODO 실풀기 작업의 시간복잡도가 과도하게 높음. 이것을 개선해야 함.

            //실 풀기 작업을 진행한다. 꼬인 부분이 있으면 순서를 바꾼다.
            for (int i = 0; i < path.size() - 1; i++) {
                for (int j = i + 2; j < path.size() - 1; j++){
                    Point point1 = path.get(i);
                    Point point2 = path.get(i+1);
                    Point point3 = path.get(j);
                    Point point4 = path.get(j+1);
                    //1-2, 3-4 가 서로 교차되었으면, 2 에서 3까지를 통째로 뒤집는다.
                    if (doIntersect(point1, point2, point3, point4)) {
                        //일단 복사하고 뒤집는다.
                        List<Point> subList = path.subList(i+1, j+1);
                        Collections.reverse(subList);
                        //뒤집은 것을 딱 그 위치 만큼 덮어씌운다.
                        for (int k = 0; k < subList.size(); k++) {
                            path.set(i+1+k, subList.get(k));
                        }
                        j--;
                        System.out.println(ConsoleColors.RED_BOLD + "swaped " + point2 + " to "+  point3 + ConsoleColors.RESET);
                    }
                }
            }

        }
        //kdTree 에도 추가한다.
        kdTree.insert(point);
        if (path.size() < 3){
            return minIndex;
        }


        return minIndex;
    }

    static boolean onSegment(Point p, Point q, Point r)
    {
        if (q.getX() <= Math.max(p.getX(), r.getY()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
                q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY()))
            return true;

        return false;
    }

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are collinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    static int orientation(Point p, Point q, Point r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
        // for details of below formula. 
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
                (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (val == 0) return 0; // collinear 

        return (val > 0)? 1: 2; // clock or counterclock wise 
    }

    // The main function that returns true if line segment 'p1q1' 
// and 'p2q2' intersect. 
    static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)
    {
        // Find the four orientations needed for general and 
        // special cases 
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case 
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases 
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1 
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are collinear and q2 lies on segment p1q1 
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2 
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are collinear and q1 lies on segment p2q2 
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases 
    }

    private boolean isIntersected(Point p1, Point p2, Point p3, Point p4){
        return ccw(p1, p3, p4) != ccw(p2, p3, p4) && ccw(p1, p2, p3) != ccw(p1, p2, p4);
    }
    private boolean ccw(Point p1, Point p2, Point p3) {
        return (p3.getY() - p1.getY()) * (p2.getX() - p1.getX()) > (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }

    private int nearestIndex(Point point){
        Point nearestPoint = kdTree.nearest(point).orElseThrow();
        return path.indexOf(nearestPoint);
    }

    private boolean insertOnFront(final int nearestIndex,final Point point){
        Point nearest = path.get(nearestIndex);

        //case 1: nearest 앞쪽에 추가한다면?
        List<Point> pathIfInsertOnFront = new LinkedList<>();
        List<Point> pathIfInsertOnBack = new LinkedList<>();
        if (nearestIndex == 0){
            Point next = path.get(1);
            pathIfInsertOnFront = List.of(point, nearest, next);
            pathIfInsertOnBack = List.of(nearest, point, next);
        } else if (nearestIndex == path.size() -1) {
            Point previous = path.get(path.size() - 2);
            pathIfInsertOnFront = List.of(previous, point, nearest);
            pathIfInsertOnBack = List.of(previous, nearest, point);
        } else {
            //앞 뒤가 모두 있는 경우
            Point next = path.get(nearestIndex + 1);
            Point previous = path.get(nearestIndex - 1);
            pathIfInsertOnFront = List.of(previous, point, nearest, next);
            pathIfInsertOnBack = List.of(previous, nearest, point, next);
        }
        double distanceIfInsertOnFront = DistanceUtil.distanceOfPath(pathIfInsertOnFront);
        double distanceIfInsertOnBack = DistanceUtil.distanceOfPath(pathIfInsertOnBack);

        return distanceIfInsertOnFront < distanceIfInsertOnBack;
    }

    public void clear(){
        path.clear();
        kdTree.clear();
    }


}
