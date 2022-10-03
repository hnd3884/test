package javax.xml.crypto.dom;

import org.w3c.dom.Node;
import javax.xml.crypto.URIReference;

public interface DOMURIReference extends URIReference
{
    Node getHere();
}
