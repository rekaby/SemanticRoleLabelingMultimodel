package visualisation;

import visualisation.Edge;
import visualisation.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Graph
implements Serializable {
    private static final long serialVersionUID = 6222815112980959472L;
    public TreeMap<Integer, Node> nodes = new TreeMap();
    public List<Edge> edges = new ArrayList<Edge>();
    public Node root;

    public Graph() {
    }

    /*Graph(ArrayList<TaggedWord> t) {
        this();
        int i = 1;
        for (TaggedWord taggedWord : t) {
            this.addNode(taggedWord.word() + "-" + i++, taggedWord.tag());
        }
    }*/

    public Edge addEdge(int sourceIndex, int targetIndex, String label) {
        if (sourceIndex == -1) {
            this.root = this.nodes.get(targetIndex);
            return null;
        }
        Edge e = new Edge();
        e.source = this.nodes.get(sourceIndex);
        e.target = this.nodes.get(targetIndex);
        e.label = label;
        e.sourceIndex = sourceIndex;
        e.targetIndex = targetIndex;
        this.edges.add(e);
        e.target.parent = e.source;
        e.source.addChild(e.target);
        e.source.outEdges.add(e);
        return e;
    }

    public Node addNode(String label, String pos) {
        for (Node node : this.nodes.values()) {
            if (!node.label.equals(label)) continue;
            return node;
        }
        Node n = new Node(label, pos);
        this.nodes.put(n.idx - 1, n);
        return n;
    }

    public Node findNode(int i) {
        return this.nodes.get(i);
    }

    void setRoot(String label) throws Exception {
        for (Node node : this.nodes.values()) {
            if (!node.label.equals(label)) continue;
            this.root = node;
            return;
        }
        throw new Exception("root not found! " + label);
    }

    public StringBuilder recurse(StringBuilder b) {
        this.recurse(this.root, b);
        return b;
    }

    private void recurse(Node t, StringBuilder b) {
        b.append("(");
        b.append(t.lex + "/" + t.pos);
        for (Node child : t.children) {
            if (b.toString().contains(child.label)) continue;
            this.recurse(child, b);
        }
        b.append(")");
    }

    public List<Node> getNodeList() {
        ArrayList<Node> list = new ArrayList<Node>();
        this.getNodeList(this.root, list);
        return list;
    }

    private void getNodeList(Node node, List<Node> list) {
        list.add(node);
        for (Node child : node.children) {
            if (list.contains(child)) continue;
            this.getNodeList(child, list);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Integer i : this.nodes.keySet()) {
            s.append(this.nodes.get((Object)i).lex);
            s.append(" ");
        }
        return s.toString();
    }

    public String toDependencyString() {
        StringBuilder s = new StringBuilder();
        for (Edge edge : this.edges) {
            s.append(edge.label).append("_").append(edge.source.lex).append("_").append(edge.target.lex).append(" ");
        }
        return s.toString();
    }

    public String toPOSString() {
        StringBuilder s = new StringBuilder();
        for (Integer i : this.nodes.keySet()) {
            s.append(this.nodes.get((Object)i).lex);
            s.append("/");
            s.append(this.nodes.get((Object)i).pos);
            s.append(" ");
        }
        return s.toString();
    }

    void addEdge(Node govNode, Node depNode, String rel) {
        int sourceIndex = govNode.idx - 1;
        int targetIndex = depNode.idx - 1;
        this.addEdge(sourceIndex, targetIndex, rel);
    }
}