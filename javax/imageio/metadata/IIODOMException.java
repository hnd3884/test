package javax.imageio.metadata;

import org.w3c.dom.DOMException;

class IIODOMException extends DOMException
{
    public IIODOMException(final short code, final String message) {
        super(code, message);
    }
}
