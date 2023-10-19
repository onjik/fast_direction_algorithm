package click.porito.one_by_one.kd_tree_swap;

import click.porito.one_by_one.PathManager;
import click.porito.one_by_one.Point;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
어디에 추가될지 결정하며 경로를 만들어 간다.
- 가장 가까운 점을 찾는다.
- 그 점, 그리고 그 점과 연결된 점, 그리고 맨 양쪽 맨 끝에 대해서, 최단 거리 순열을 찾는다.
 */
public class InsertionPathManager implements PathManager {

    private LinkedList<Point> path;
    private KdTree kdTree;

    public InsertionPathManager(LinkedList<Point> path) {
        this.path = path;
        kdTree = new KdTree();
    }

    public InsertionPathManager() {
        this.path = new LinkedList<>();
        kdTree = new KdTree();
    }


    @Override
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
                    if (PathManager.doIntersect(point1, point2, point3, point4)) {
                        //일단 복사하고 뒤집는다.
                        List<Point> subList = path.subList(i+1, j+1);
                        Collections.reverse(subList);
                        //뒤집은 것을 딱 그 위치 만큼 덮어씌운다.
                        for (int k = 0; k < subList.size(); k++) {
                            path.set(i+1+k, subList.get(k));
                        }
                        j--;
//                        System.out.println(ConsoleColors.RED_BOLD + "swaped " + point2 + " to "+  point3 + ConsoleColors.RESET);
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

    @Override
    public void clear(){
        path.clear();
        kdTree.clear();
    }

    @Override
    public void setPath(List<Point> path) {
        this.path = new LinkedList<>(path);
        kdTree.clear();
        for (Point point : path) {
            kdTree.insert(point);
        }
    }

    @Override
    public LinkedList<Point> getPath() {
        return path;
    }
}
