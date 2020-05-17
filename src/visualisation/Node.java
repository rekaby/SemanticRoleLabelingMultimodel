package visualisation;

/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.chaoticity.dependensee.Edge
 *  edu.stanford.nlp.util.Pair
 */


import visualisation.Edge;
import visualisation.Pair;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Node
implements Serializable {
    private static final long serialVersionUID = -8871645177307943816L;
    public String label;
    public int idx;
    public String lex;
    public String pos;
    public List<Node> children;
    public List<Edge> outEdges;
    public int degree = 1;
    public Node parent;
    public Rectangle2D position = new Rectangle();

    public Node(String lex, int idx, String pos) {
        this.label = lex + "-" + idx;
        this.lex = lex;
        this.idx = idx;
        this.pos = pos;
        this.children = new ArrayList<Node>();
        this.outEdges = new ArrayList<Edge>();
    }

    public Node(String label, String pos) {
        this.label = label;
        this.lex = label.substring(0, label.lastIndexOf("-"));
        this.idx = Integer.parseInt(label.substring(label.lastIndexOf("-") + 1));
        this.pos = pos;
        this.children = new ArrayList<Node>();
        this.outEdges = new ArrayList<Edge>();
    }

    public void addChild(Node c) {
        for (Node node : this.children) {
            if (!node.label.equalsIgnoreCase(c.label)) continue;
            return;
        }
        this.children.add(c);
        ++this.degree;
    }

    public String toString() {
        return this.lex;
    }

    public int getPathLength(Node n) {
        LinkedList<Pair> q = new LinkedList<Pair>();
        HashSet<Node> marked = new HashSet<Node>();
        q.add(new Pair((Object)this, (Object)0));
        marked.add(this);
        while (!q.isEmpty()) {
            Pair v = (Pair)q.remove();
            if (v.first == n) {
                return (Integer)v.second;
            }
            if (((Node)v.first).parent != null && !marked.contains(((Node)v.first).parent)) {
                q.add(new Pair((Object)((Node)v.first).parent, (Object)((Integer)v.second + 1)));
                marked.add(((Node)v.first).parent);
            }
            for (Node node : ((Node)v.first).children) {
                q.add(new Pair((Object)node, (Object)((Integer)v.second + 1)));
                marked.add(node);
            }
        }
        return Integer.MAX_VALUE;
    }

    public String getRelationToParent() {
        Object rel = null;
        if (this.parent == null) {
            return null;
        }
        for (Edge e : this.parent.outEdges) {
            if (e.target != this) continue;
            return e.label;
        }
        return null;
    }

    public String DFS() {
        StringBuilder b = new StringBuilder();
        HashSet<Node> done = new HashSet<Node>();
        done.add(this);
        this.DFS(this, done, b);
        return b.toString();
    }

    private void DFS(Node node, Set<Node> done, StringBuilder b) {
        for (Edge e : node.outEdges) {
            if (!"amod".equalsIgnoreCase(e.label) && !"advmod".equalsIgnoreCase(e.label) || done.contains(e.target)) continue;
            this.DFS(e.target, done, b);
            done.add(e.target);
        }
        b.append(" ").append(node.lex).append("/").append(node.pos);
    }
}