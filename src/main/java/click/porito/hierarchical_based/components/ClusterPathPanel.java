package click.porito.hierarchical_based.components;

import click.porito.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;

public class ClusterPathPanel extends JComponent {
    private Cluster root;
    private List<Point> route;
    private int daySplit;


    public ClusterPathPanel(Cluster root, List<Point> route, int daySplit) {
        this.root = root;
        this.route = route;
        this.daySplit = daySplit;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCluster(g, root);
        drawDaySplit(g, daySplit);
        drawPath(g);
        drawPoints(g);
    }

    private void drawDaySplit(Graphics g, int i) {

        List<Cluster> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() < i){
            //일단 가장 item 수가 많은 cluster를 찾고, 혹시 같으면, distance가 먼 cluster 를 찾는다.
            Cluster maxDistanceCluster = queue.stream()
                    .max(Comparator.comparingInt(Cluster::getPointCount)
                            .thenComparing(Comparator.comparingDouble(Cluster::getDistance)))
                    .orElseThrow();

            queue.remove(maxDistanceCluster);
            //그 cluster 의 children 을 queue 에 넣는다.
            queue.addAll(maxDistanceCluster.getChildren());
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3));
        for (var cluster : queue) {
            if (cluster.isLeaf()) {
                g2d.draw(new Ellipse2D.Double(cluster.getCentroid().getX() - 15, cluster.getCentroid().getY() - 15, 30, 30));
            }
            List<Point> points = cluster.getPoints();
            double centerX = points.stream().mapToDouble(Point::getX).average().orElseThrow();
            double centerY = points.stream().mapToDouble(Point::getY).average().orElseThrow();
            Point center = new Point(centerX, centerY);
            double radius = points.stream().mapToDouble(p -> p.distance(center)).max().orElseThrow();
            g2d.draw(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2 , radius * 2));
        }
        g2d.setStroke(new BasicStroke(1));

    }

    private void drawCluster(Graphics g, Cluster cluster) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GRAY);
        if (cluster.isLeaf()) {
            return;
        }
        //하나씩 순회 조회하면서 모든 point를 포함하는 최대원을 그린다
        List<Point> points = cluster.getPoints();
        double centerX = points.stream().mapToDouble(Point::getX).average().orElseThrow();
        double centerY = points.stream().mapToDouble(Point::getY).average().orElseThrow();
        Point center = new Point(centerX, centerY);
        double radius = points.stream().mapToDouble(p -> p.distance(center)).max().orElseThrow();
        g2d.draw(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2 , radius * 2));
        for (var child : cluster.getChildren()) {
            drawCluster(g, child);
        }


    }

    private void drawPoints(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Point> points = root.getPoints();
        for (var point : points) {
            g2d.setColor(Color.BLACK);
            g2d.fill(new Ellipse2D.Double(point.getX() - 5, point.getY() - 5, 10, 10));

            //옆에 번호 써주기
            //글자 크기 작게
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(String.format("(%.2f, %.2f)", point.getX(), point.getY()), (int) point.getX(), (int) point.getY());
        }
    }

    private void drawPath(Graphics g){

        Graphics2D g2d = (Graphics2D) g;
        Path2D path = new Path2D.Double();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.RED);
        //두껍게
        g2d.setStroke(new BasicStroke(3));


        Point starting = route.get(0);
        path.moveTo(starting.getX(), starting.getY());
        for (int i = 1; i < route.size(); i++) {
            Point point = route.get(i);
            path.lineTo(point.getX(), point.getY());
        }
        ((Graphics2D) g).draw(path);
        //두께 원래대로
        g2d.setStroke(new BasicStroke(1));
    }
}
