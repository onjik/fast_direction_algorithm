package click.porito.hierarchical_based;

import click.porito.Point;
import click.porito.hierarchical_based.components.Cluster;

import java.util.*;

public class RouteAlgorithm {

    public List<Point> performRouting(Cluster root){
        LinkedList<Cluster> selectedPoints = new LinkedList<>();
        //루트의 중심점에서 가장 가까운 Leaf Cluster 를 찾는다.
//        Cluster nearestCluster = root.getAllChildren().stream().filter(Cluster::isLeaf).min(new ClusterComparator(root)).orElseThrow();
        //루트 중심점에서 가장 먼 Leaf Cluster 를 찾는다.
        Cluster nearestCluster = root.getAllContainedCluster().stream().filter(Cluster::isLeaf).max(new ClusterComparator(root)).orElseThrow();

        //선택된 point 를 selectedPoints 에 추가한다.
        selectedPoints.add(nearestCluster);

        selectPoint(root, selectedPoints);

        //실 풀기 작업을 진행한다. 꼬인 부분이 있으면 순서를 바꾼다.
        for (int i = 0; i < selectedPoints.size() - 1; i++) {
            for (int j = i + 2; j < selectedPoints.size() - 1; j++){
                Point point1 = selectedPoints.get(i).getCentroid();
                Point point2 = selectedPoints.get(i+1).getCentroid();
                Point point3 = selectedPoints.get(j).getCentroid();
                Point point4 = selectedPoints.get(j+1).getCentroid();
                //1-2, 3-4 가 서로 교차되었으면, 2 에서 3까지를 통째로 뒤집는다.
                if (isIntersected(point1, point2, point3, point4)) {
                    //일단 복사하고 뒤집는다.
                    List<Cluster> subList = selectedPoints.subList(i+1, j+1);
                    Collections.reverse(subList);
                    //뒤집은 것을 딱 그 위치 만큼 덮어씌운다.
                    for (int k = 0; k < subList.size(); k++) {
                        selectedPoints.set(i+1+k, subList.get(k));
                    }
//                    System.out.println(ConsoleColors.RED_BOLD + "swaped " + point2 + " to "+  point3 + ConsoleColors.RESET);
                }
            }
        }

        return selectedPoints.stream().map(Cluster::getCentroid).toList();

    }

    private boolean isIntersected(Point p1, Point p2, Point p3, Point p4){
        return ccw(p1, p3, p4) != ccw(p2, p3, p4) && ccw(p1, p2, p3) != ccw(p1, p2, p4);
    }
    private boolean ccw(Point p1, Point p2, Point p3) {
        return (p3.getY() - p1.getY()) * (p2.getX() - p1.getX()) > (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }


    private void selectPoint(Cluster cluster, Deque<Cluster> selectedPoints){
        /*
        전위 순회 와 비슷하게, deque의 맨 앞 혹은 맨 뒤와 가장 가까운 클러스터 쪽으로 이동한다. 이것을 leaf까지 반복한다.
        leaf까지 갔으면, deque의 가까운 쪽에 밀어 넣는다.
        그 후 한칸 부모로 올라가서 반대편에 대해서도 이러한 작업을 반복한다.
         */
        Cluster head = selectedPoints.getFirst();
        Cluster tail = selectedPoints.getLast();
        if (cluster.isLeaf()) {
            if (selectedPoints.contains(cluster)) {
                return;
            }
            boolean isHeadClose = cluster.centroidDistance(head) < cluster.centroidDistance(tail);
            if (isHeadClose) {
                selectedPoints.addFirst(cluster);
            } else {
                selectedPoints.addLast(cluster);
            }
        } else {
            //가까운 순서대로 방문한다
            List<Cluster> children = cluster.getChildren();
            children.sort(new HeadTailComparator(head, tail));
            for (var child : children) {
                selectPoint(child, selectedPoints);
            }
        }
    }

    static class ClusterComparator implements Comparator<Cluster> {

        private Cluster baseCluster;

        public ClusterComparator(Cluster root) {
            this.baseCluster = root;
        }

        @Override
        public int compare(Cluster o1, Cluster o2) {
            double d1 = baseCluster.getCentroid().distance(o1.getCentroid());
            double d2 = baseCluster.getCentroid().distance(o2.getCentroid());
            return Double.compare(d1, d2);
        }
    }


    static class PointComparator implements Comparator<Point> {
        private Point centroid;

        public PointComparator(Point centroid) {
            this.centroid = centroid;
        }

        @Override
        public int compare(Point o1, Point o2) {
            double d1 = centroid.distance(o1);
            double d2 = centroid.distance(o2);
            return Double.compare(d1, d2);
        }
    }

    static class HeadTailComparator implements Comparator<Cluster> {
        private Cluster head;
        private Cluster tail;

        public HeadTailComparator(Cluster head, Cluster tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public int compare(Cluster o1, Cluster o2) {
//            double dh1 = o1.minDistance(head);
//            double dt1 = o1.minDistance(tail);
//            double dh2 = o2.minDistance(head);
//            double dt2 = o2.minDistance(tail);
            double dh1 = o1.centroidDistance(head);
            double dt1 = o1.centroidDistance(tail);
            double dh2 = o2.centroidDistance(head);
            double dt2 = o2.centroidDistance(tail);
            double d1 = Math.min(dh1, dt1) - Math.max(dh1, dt1) * 0.5;
            double d2 = Math.min(dh2, dt2) - Math.max(dh2, dt2) * 0.5;
            return Double.compare(d1, d2);
        }
    }

}
