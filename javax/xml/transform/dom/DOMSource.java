package javax.xml.transform.dom;

import org.w3c.dom.Node;
import javax.xml.transform.Source;

public class DOMSource implements Source
{
    private Node node;
    private String systemID;
    public static final String FEATURE = "http://javax.xml.transform.dom.DOMSource/feature";
    
    public DOMSource() {
    }
    
    public DOMSource(final Node node) {
        this.setNode(node);
    }
    
    public DOMSource(final Node node, final String systemId) {
        this.setNode(node);
        this.setSystemId(systemId);
    }
    
    public void setNode(final Node node) {
        this.node = node;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public void setSystemId(final String systemID) {
        this.systemID = systemID;
    }
    
    public String getSystemId() {
        return this.systemID;
    }
}
