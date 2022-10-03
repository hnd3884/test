package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.Base64;
import java.math.BigInteger;

public final class DOMCryptoBinary extends DOMStructure
{
    private final BigInteger bigNum;
    private final String value;
    
    public DOMCryptoBinary(final BigInteger bigNum) {
        if (bigNum == null) {
            throw new NullPointerException("bigNum is null");
        }
        this.bigNum = bigNum;
        this.value = Base64.encode(bigNum);
    }
    
    public DOMCryptoBinary(final Node node) throws MarshalException {
        this.value = node.getNodeValue();
        try {
            this.bigNum = Base64.decodeBigIntegerFromText((Text)node);
        }
        catch (final Exception ex) {
            throw new MarshalException(ex);
        }
    }
    
    public BigInteger getBigNum() {
        return this.bigNum;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        node.appendChild(DOMUtils.getOwnerDocument(node).createTextNode(this.value));
    }
}
