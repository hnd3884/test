package javax.xml.crypto.dsig.dom;

import java.security.Key;
import javax.xml.crypto.KeySelector;
import org.w3c.dom.Node;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dom.DOMCryptoContext;

public class DOMValidateContext extends DOMCryptoContext implements XMLValidateContext
{
    private Node node;
    
    public DOMValidateContext(final KeySelector keySelector, final Node node) {
        if (keySelector == null) {
            throw new NullPointerException("key selector is null");
        }
        if (node == null) {
            throw new NullPointerException("node is null");
        }
        this.setKeySelector(keySelector);
        this.node = node;
    }
    
    public DOMValidateContext(final Key key, final Node node) {
        if (key == null) {
            throw new NullPointerException("validatingKey is null");
        }
        if (node == null) {
            throw new NullPointerException("node is null");
        }
        this.setKeySelector(KeySelector.singletonKeySelector(key));
        this.node = node;
    }
    
    public void setNode(final Node node) {
        if (node == null) {
            throw new NullPointerException();
        }
        this.node = node;
    }
    
    public Node getNode() {
        return this.node;
    }
}
