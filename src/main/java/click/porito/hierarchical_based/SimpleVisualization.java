package click.porito.hierarchical_based;


import click.porito.one_by_one.Point;
import click.porito.hierarchical_based.components.Cluster;
import click.porito.hierarchical_based.components.ClusterPathPanel;
import click.porito.hierarchical_based.components.ClusterPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleVisualization {
    private static final int N = 10;
    private static final int MAX_X = 1500;
    private static final int MAX_Y = 800;

    private static final int EDGE = 20;

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new BorderLayout());

        // Create and add components for cluster and point input
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("클러스터 수 :"));
        JTextField clusterInput = new JTextField(10);
        clusterInput.setText("3");
        inputPanel.add(clusterInput);
        inputPanel.add(new JLabel("점의 수 :"));
        JTextField pointInput = new JTextField(10);
        pointInput.setText("10");
        inputPanel.add(pointInput);

        panel.add(inputPanel, BorderLayout.NORTH);


        // 버튼
        JButton addButton = new JButton("Add ClusterPathPanel");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if the input is valid
                String clusterInputText = clusterInput.getText();
                if (!clusterInputText.matches("\\d+")) {
                    JOptionPane.showMessageDialog(frame, "Please input a number");
                    return;
                }
                int daySplit = Integer.parseInt(clusterInputText);

                String pointInputText = pointInput.getText();
                if (!pointInputText.matches("\\d+")) {
                    JOptionPane.showMessageDialog(frame, "Please input a number");
                    return;
                }
                int pointCount = Integer.parseInt(pointInputText);

                // Remove the existing ClusterPathPanel
                Arrays.stream(panel.getComponents()).filter(c -> c instanceof ClusterPathPanel).forEach(panel::remove);


                // Do Clustering
                List<Point> points = Stream.generate(() -> Point.randomPoint(EDGE, MAX_X * 0.8 - EDGE, EDGE, MAX_Y * 0.8 - EDGE * 2)).limit(pointCount).collect(Collectors.toList());
                HierarchicalClustering alg = new HierarchicalClustering();
                Cluster cluster = alg.performClustering(points);
                RouteAlgorithm routeAlgorithm = new RouteAlgorithm();
                List<Point> route = routeAlgorithm.performRouting(cluster);

                // Add a new ClusterPathPanel
                if (cluster.getPointCount() < daySplit) {
                    daySplit = cluster.getPointCount();
                }
                panel.add(new ClusterPathPanel(cluster,route,daySplit), BorderLayout.CENTER);
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);

                ClusterPrinter.printClusteringResult(cluster, 0);

                // Repaint the panel
                panel.revalidate();
                panel.repaint();
                frame.revalidate();
                frame.repaint();
            }
        });
        addButton.setSize(100, 100);
        panel.add(addButton, BorderLayout.SOUTH);



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(MAX_X, MAX_Y);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    //swing 으로 점 찍어주는 컴포넌트
    static class PointComponent extends JComponent {
        private List<Point> points;

        public PointComponent(List<Point> points) {
            this.points = points;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Point prev = null;
            for (var point : points) {
                g.fillOval((int) point.getX(), (int) point.getY(), 10, 10);
                //옆에 좌표 써주기
                g.drawString(String.format("(%.2f, %.2f)", point.getX(), point.getY()), (int) point.getX(), (int) point.getY());
                if (prev != null) {
                    g.drawLine((int) prev.getX(), (int) prev.getY(), (int) point.getX(), (int) point.getY());
                }
                prev = point;
            }
        }
    }



}