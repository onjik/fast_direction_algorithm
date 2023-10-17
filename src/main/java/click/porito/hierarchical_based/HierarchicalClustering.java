package click.porito.hierarchical_based;

import click.porito.hierarchical_based.components.Cluster;
import click.porito.hierarchical_based.components.Point;

import java.util.*;
import java.util.stream.Collectors;

public class HierarchicalClustering {


    public Cluster performClustering(List<Point> points) {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("points must not be empty");
        }
        List<Cluster> clusters = points.stream().map(Cluster::leaf).collect(Collectors.toList());

        return merge(clusters);


    }

    private Cluster merge(List<Cluster> clusters){

        while (clusters.size() > 1) {
            Double minDistance = Double.MAX_VALUE;
            Cluster minI = null;
            Cluster minJ = null;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    Cluster clusterI = clusters.get(i);
                    Cluster clusterJ = clusters.get(j);
                    double wardDistance = clusterI.getWardDistance(clusterJ);
                    if (wardDistance < minDistance) {
                        minDistance = wardDistance;
                        minI = clusterI;
                        minJ = clusterJ;
                    }
                }
            }
            clusters.remove(minJ);
            clusters.remove(minI);
            ArrayList<Cluster> childs = new ArrayList<>(2);
            childs.add(minI);
            childs.add(minJ);
            Cluster merged = Cluster.fromClusters(childs, minDistance);
            clusters.add(merged);
        }
        return clusters.get(0);
    }





}
