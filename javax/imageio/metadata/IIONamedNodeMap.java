package javax.imageio.metadata;

import org.w3c.dom.DOMException;
import java.util.Iterator;
import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NamedNodeMap;

class IIONamedNodeMap implements NamedNodeMap
{
    List nodes;
    
    public IIONamedNodeMap(final List nodes) {
        this.nodes = nodes;
    }
    
    @Override
    public int getLength() {
        return this.nodes.size();
    }
    
    @Override
    public Node getNamedItem(final String s) {
        for (final Node node : this.nodes) {
            if (s.equals(node.getNodeName())) {
                return node;
            }
        }
        return null;
    }
    
    @Override
    public Node item(final int n) {
        return this.nodes.get(n);
    }
    
    @Override
    public Node removeNamedItem(final String s) {
        throw new DOMException((short)7, "This NamedNodeMap is read-only!");
    }
    
    @Override
    public Node setNamedItem(final Node node) {
        throw new DOMException((short)7, "This NamedNodeMap is read-only!");
    }
    
    @Override
    public Node getNamedItemNS(final String s, final String s2) {
        return this.getNamedItem(s2);
    }
    
    @Override
    public Node setNamedItemNS(final Node namedItem) {
        return this.setNamedItem(namedItem);
    }
    
    @Override
    public Node removeNamedItemNS(final String s, final String s2) {
        return this.removeNamedItem(s2);
    }
}
