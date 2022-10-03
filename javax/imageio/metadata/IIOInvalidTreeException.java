package javax.imageio.metadata;

import org.w3c.dom.Node;
import javax.imageio.IIOException;

public class IIOInvalidTreeException extends IIOException
{
    protected Node offendingNode;
    
    public IIOInvalidTreeException(final String s, final Node offendingNode) {
        super(s);
        this.offendingNode = null;
        this.offendingNode = offendingNode;
    }
    
    public IIOInvalidTreeException(final String s, final Throwable t, final Node offendingNode) {
        super(s, t);
        this.offendingNode = null;
        this.offendingNode = offendingNode;
    }
    
    public Node getOffendingNode() {
        return this.offendingNode;
    }
}
