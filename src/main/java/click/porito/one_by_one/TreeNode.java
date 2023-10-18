package click.porito.one_by_one;

import click.porito.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreeNode {
    private Point point;
    private TreeNode parent;
    private TreeNode left;
    private TreeNode right;
    private boolean isVerticalSplit;

    public TreeNode(TreeNode parent, Point point, boolean isVerticalSplit) {
        this.parent = parent;
        this.point = point;
        this.isVerticalSplit = isVerticalSplit;
    }
    public static TreeNode generateRoot(Point point){
        return new TreeNode(null, point, true);
    }

    public boolean isLeaf(){
        return left == null && right == null;
    }

    public TreeNode findNearestNode(Point point){
        TreeNode node = null;
        if (isVerticalSplit) {
            if (this.point.getX() <= point.getX()){
                node = right;
            } else {
                node = left;
            }
        } else {
            if (this.point.getY() <= point.getY()){
                node = right;
            } else {
                node = left;
            }
        }
        if (node == null){
            return this;
        } else {
            return node.findNearestNode(point);
        }
    }


    public TreeNode addPoint(Point point){
        if (isVerticalSplit){
            if (point.getX() < this.point.getX()){
                if (left == null){
                    left = new TreeNode(this, point, false);
                    return left;
                } else {
                    return left.addPoint(point);
                }
            } else {
                if (right == null){
                    right = new TreeNode(this, point, false);
                    return right;
                } else {
                    return right.addPoint(point);
                }
            }
        } else {
            if (point.getY() < this.point.getY()){
                if (left == null){
                    left = new TreeNode(this, point, true);
                    return left;
                } else {
                    return left.addPoint(point);
                }
            } else {
                if (right == null){
                    right = new TreeNode(this, point, true);
                    return right;
                } else {
                    return right.addPoint(point);
                }
            }
        }
    }
}
