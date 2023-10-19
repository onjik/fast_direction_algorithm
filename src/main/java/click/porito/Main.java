package click.porito;

import click.porito.one_by_one.Visualizer;
import click.porito.one_by_one.kd_tree_swap.InsertionPathManager;
import click.porito.one_by_one.simple_linear.LinearPathManager;

public class Main {
    public static void main(String[] args) {
//        Visualizer visualizer = new Visualizer(new LinearPathManager());
        Visualizer visualizer = new Visualizer(new LinearPathManager());
        visualizer.setVisible(true);

    }
}
