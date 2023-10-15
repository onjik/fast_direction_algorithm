package click.porito.components;


public class ClusterPrinter {
    private static final String CLUSTER = ConsoleColors.CYAN + "[ Cluster ] " + ConsoleColors.RESET;
    private static final String LEAF = ConsoleColors.GREEN + "[ Leaf ] " + ConsoleColors.RESET;
    private static final String TAB = "\t";
    private static final String PREFIX = "├─ ";
    public static void printClusteringResult(Cluster cluster, int depth){
        if (cluster.isLeaf()) {
            System.out.println(TAB.repeat(depth) + PREFIX + LEAF + cluster.getCentroid());
        } else {
            System.out.printf( "%s" + PREFIX + CLUSTER + "centroid : %s," + ConsoleColors.RED + " ward-based-distance : %f\n" + ConsoleColors.RESET, TAB.repeat(depth), cluster.getCentroid(), cluster.getDistance());
            cluster.getChildren().forEach(c -> printClusteringResult(c, depth + 1));
        }
    }
}
