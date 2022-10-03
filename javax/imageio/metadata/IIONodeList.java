package javax.imageio.metadata;

import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NodeList;

class IIONodeList implements NodeList
{
    List nodes;
    
    public IIONodeList(final List nodes) {
        this.nodes = nodes;
    }
    
    @Override
    public int getLength() {
        return this.nodes.size();
    }
    
    @Override
    public Node item(final int n) {
        if (n < 0 || n > this.nodes.size()) {
            return null;
        }
        return this.nodes.get(n);
    }
}
