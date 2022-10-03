package javax.xml.crypto.dom;

import org.w3c.dom.Node;
import javax.xml.crypto.XMLStructure;

public class DOMStructure implements XMLStructure
{
    private final Node node;
    
    public DOMStructure(final Node node) {
        if (node == null) {
            throw new NullPointerException("node cannot be null");
        }
        this.node = node;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public boolean isFeatureSupported(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return false;
    }
}
