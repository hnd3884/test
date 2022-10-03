package javax.xml.crypto.dsig.dom;

import javax.xml.crypto.KeySelector;
import java.security.Key;
import org.w3c.dom.Node;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dom.DOMCryptoContext;

public class DOMSignContext extends DOMCryptoContext implements XMLSignContext
{
    private Node parent;
    private Node nextSibling;
    
    public DOMSignContext(final Key key, final Node parent) {
        if (key == null) {
            throw new NullPointerException("signingKey cannot be null");
        }
        if (parent == null) {
            throw new NullPointerException("parent cannot be null");
        }
        this.setKeySelector(KeySelector.singletonKeySelector(key));
        this.parent = parent;
    }
    
    public DOMSignContext(final Key key, final Node parent, final Node nextSibling) {
        if (key == null) {
            throw new NullPointerException("signingKey cannot be null");
        }
        if (parent == null) {
            throw new NullPointerException("parent cannot be null");
        }
        if (nextSibling == null) {
            throw new NullPointerException("nextSibling cannot be null");
        }
        this.setKeySelector(KeySelector.singletonKeySelector(key));
        this.parent = parent;
        this.nextSibling = nextSibling;
    }
    
    public DOMSignContext(final KeySelector keySelector, final Node parent) {
        if (keySelector == null) {
            throw new NullPointerException("key selector cannot be null");
        }
        if (parent == null) {
            throw new NullPointerException("parent cannot be null");
        }
        this.setKeySelector(keySelector);
        this.parent = parent;
    }
    
    public DOMSignContext(final KeySelector keySelector, final Node parent, final Node nextSibling) {
        if (keySelector == null) {
            throw new NullPointerException("key selector cannot be null");
        }
        if (parent == null) {
            throw new NullPointerException("parent cannot be null");
        }
        if (nextSibling == null) {
            throw new NullPointerException("nextSibling cannot be null");
        }
        this.setKeySelector(keySelector);
        this.parent = parent;
        this.nextSibling = nextSibling;
    }
    
    public void setParent(final Node parent) {
        if (parent == null) {
            throw new NullPointerException("parent is null");
        }
        this.parent = parent;
    }
    
    public void setNextSibling(final Node nextSibling) {
        this.nextSibling = nextSibling;
    }
    
    public Node getParent() {
        return this.parent;
    }
    
    public Node getNextSibling() {
        return this.nextSibling;
    }
}
