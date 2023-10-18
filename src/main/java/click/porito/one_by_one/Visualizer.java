package click.porito.one_by_one;

import click.porito.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Visualizer extends JFrame {


    public static void main(String[] args) {
        Visualizer visualizer = new Visualizer();
        visualizer.setVisible(true);
    }

    LinkedList<Point> path;
    private Point[] candidatePoints;

    public Visualizer() {
        path = new LinkedList<>();
        candidatePoints = new Point[4];
        //초기 후보값
        Random random = new Random();
        for (int i = 0; i < candidatePoints.length; i++) {
            candidatePoints[i] = new Point(random.nextDouble(10,1000), random.nextDouble(10, 800));
        }


        setTitle("Visualizer");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        InsertionPathManager pathManager = new InsertionPathManager(path);

        //경로 그려주는 패널
        PathPanel pathPanel = new PathPanel(path, candidatePoints);
        add(pathPanel, BorderLayout.CENTER);


        JButton[] buttons = new JButton[4];
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[index] = new JButton(String.valueOf(i));
            buttons[index].addActionListener(e -> {
                Point point = candidatePoints[index];
                pathManager.addPoint(point);
                pathPanel.lastInsertedPoint = point;
                updateCandidate();
                revalidate();
                repaint();
            });
        }

        //버튼 : 누르면 초기화
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            pathPanel.lastInsertedPoint = null;
            pathManager.clear();
            revalidate();
            repaint();
        });
        //버튼 3개를 세로로 묶어서 넣는다.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        Stream.of(buttons).forEach(buttonPanel::add);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();

    }

    private void updateCandidate(){
        Random random = new Random();
        for (int i = 0; i < candidatePoints.length; i++) {
            candidatePoints[i] = new Point(random.nextDouble(10, 1000), random.nextDouble(10, 700));
        }
    }

    static class PathPanel extends JComponent {
        private List<Point> route;
        private Point[] candidatePoints;
        private Point lastInsertedPoint = null;

        public PathPanel(List<Point> route, Point[] candidatePoints) {
            this.route = route;
            this.candidatePoints = candidatePoints;
            setBackground(Color.BLACK);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!route.isEmpty()){
                drawPath(g);
            }
            drawPoints(g);
            drawCandidatePoints(g);
            drawLastInsertedPoint(g);

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

        private void drawPoints(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (var point : route) {
                g2d.setColor(Color.BLACK);
                g2d.fill(new Ellipse2D.Double(point.getX() - 5, point.getY() - 5, 10, 10));

                //옆에 번호 써주기
                //글자 크기 작게
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(String.format("(%.2f, %.2f)", point.getX(), point.getY()), (int) point.getX(), (int) point.getY());
            }
        }

        private void drawCandidatePoints(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            for (int i = 0; i < candidatePoints.length; i++) {
                g2d.setColor(Color.GREEN);
                int radius = 5;
                Point point = candidatePoints[i];
                g2d.fill(new Ellipse2D.Double(point.getX() - radius, point.getY() - radius, radius * 2, radius * 2));
            }
            //옆에 번호 써주기
            //글자 크기 작게
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 40));
            for (int i = 0; i < candidatePoints.length; i++) {
                Point point = candidatePoints[i];
                g2d.drawString(String.valueOf(i), (int) point.getX(), (int) point.getY());
            }


        }

        private void drawLastInsertedPoint(Graphics g){
            if(lastInsertedPoint == null) return;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLUE);
            int radius = 10;
            g2d.fill(new Ellipse2D.Double(lastInsertedPoint.getX() - radius, lastInsertedPoint.getY() - radius, radius * 2, radius * 2));
        }
    }

}
