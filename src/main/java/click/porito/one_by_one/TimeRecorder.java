package click.porito.one_by_one;

import click.porito.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class TimeRecorder {
    public static void main(String[] args) {
        LinkedList<Point> path = new LinkedList<>();
        InsertionPathManager insertionPathManager = new InsertionPathManager(path);
        for (int i = 1; i < 50; i++) {
            int size = path.size();

            Random random = new Random();
            List<Point> points = Stream.generate(() -> new Point(random.nextDouble() * 1000, random.nextDouble() * 1000))
                    .limit(100)
                    .toList();

            long start = System.currentTimeMillis();
            //add points
            for (Point point : points) {
                insertionPathManager.addPoint(point);
            }
            long end = System.currentTimeMillis();
            System.out.printf("%d ,%d\n", size, end - start); //path 가 몇개 있을 때, 걸린 시간
        }
    }
}
