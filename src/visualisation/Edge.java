package visualisation;

import visualisation.Node;
import java.io.Serializable;

public class Edge
implements Serializable {
    public Node source;
    public Node target;
    public String label;
    public int sourceIndex;
    public int targetIndex;
    public boolean visible = false;
    public int height;

    public String toString() {
        return this.label + "[" + this.sourceIndex + "->" + this.targetIndex + "]";
    }
}
