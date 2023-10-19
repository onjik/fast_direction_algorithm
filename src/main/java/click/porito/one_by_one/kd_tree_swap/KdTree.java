package click.porito.one_by_one.kd_tree_swap;

import click.porito.one_by_one.Point;

import java.util.Optional;

public class KdTree {

    public static void main(String[] args) {
        KdTree kdTree = new KdTree();
        kdTree.insert(new Point(50, 50));
        kdTree.insert(new Point(10, 70));
        kdTree.insert(new Point(40, 85));
        kdTree.insert(new Point(25, 20));
        kdTree.insert(new Point(10, 60));
        kdTree.insert(new Point(80, 85));
        kdTree.insert(new Point(70, 85));
        Point point = kdTree.nearest(new Point(10, 68)).orElseThrow();
        System.out.println(point);
    }


    private TreeNode root;
    /**
     * 가장 가까운 점을 찾는다.
     * @param point 기준점
     * @return 가장 가까운 점, 없으면 optional.empty
     */
    public Optional<Point> nearest(Point point) {
        if (root == null){
            return Optional.empty();
        }
        //주 로직 : 리프가 나올 때 까지 내려간다.
        TreeNode nearestNode = root.findNearestNode(point);
        double minDistance = nearestNode.getPoint().distance(point);
        //리프에서 루트까지 부모로 올라가면서, 최소 거리 보다 더 짧은 거리의 노드 가 있으면 갱신한다.
        TreeNode parent = nearestNode;
        while (parent.getParent() != null) {
            parent = parent.getParent();
            double distance = parent.getPoint().distance(point);
            if (distance < minDistance) {
                minDistance = distance;
                nearestNode = parent;
            }
        }
        return Optional.of(nearestNode.getPoint());
    }

    public void insert(Point point) {
        if (root == null){
            root = TreeNode.generateRoot(point);
        } else {
            root.addPoint(point);
        }
    }

    public void clear(){
        root = null;
    }

}
