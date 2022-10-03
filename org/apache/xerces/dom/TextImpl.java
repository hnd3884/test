package org.apache.xerces.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;
import org.w3c.dom.CharacterData;

public class TextImpl extends CharacterDataImpl implements CharacterData, Text
{
    static final long serialVersionUID = -5294980852957403469L;
    
    public TextImpl() {
    }
    
    public TextImpl(final CoreDocumentImpl coreDocumentImpl, final String s) {
        super(coreDocumentImpl, s);
    }
    
    public void setValues(final CoreDocumentImpl ownerDocument, final String data) {
        this.flags = 0;
        this.nextSibling = null;
        this.previousSibling = null;
        this.setOwnerDocument(ownerDocument);
        super.data = data;
    }
    
    public short getNodeType() {
        return 3;
    }
    
    public String getNodeName() {
        return "#text";
    }
    
    public void setIgnorableWhitespace(final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isIgnorableWhitespace(b);
    }
    
    public boolean isElementContentWhitespace() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalIsIgnorableWhitespace();
    }
    
    public String getWholeText() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final StringBuffer sb = new StringBuffer();
        if (this.data != null && this.data.length() != 0) {
            sb.append(this.data);
        }
        this.getWholeTextBackward(this.getPreviousSibling(), sb, this.getParentNode());
        final String string = sb.toString();
        sb.setLength(0);
        this.getWholeTextForward(this.getNextSibling(), sb, this.getParentNode());
        return string + sb.toString();
    }
    
    protected void insertTextContent(final StringBuffer sb) throws DOMException {
        final String nodeValue = this.getNodeValue();
        if (nodeValue != null) {
            sb.insert(0, nodeValue);
        }
    }
    
    private boolean getWholeTextForward(Node nextSibling, final StringBuffer sb, final Node node) {
        int n = 0;
        if (node != null) {
            n = ((node.getNodeType() == 5) ? 1 : 0);
        }
        while (nextSibling != null) {
            final short nodeType = nextSibling.getNodeType();
            if (nodeType == 5) {
                if (this.getWholeTextForward(nextSibling.getFirstChild(), sb, nextSibling)) {
                    return true;
                }
            }
            else {
                if (nodeType != 3 && nodeType != 4) {
                    return true;
                }
                ((NodeImpl)nextSibling).getTextContent(sb);
            }
            nextSibling = nextSibling.getNextSibling();
        }
        if (n != 0) {
            this.getWholeTextForward(node.getNextSibling(), sb, node.getParentNode());
            return true;
        }
        return false;
    }
    
    private boolean getWholeTextBackward(Node previousSibling, final StringBuffer sb, final Node node) {
        int n = 0;
        if (node != null) {
            n = ((node.getNodeType() == 5) ? 1 : 0);
        }
        while (previousSibling != null) {
            final short nodeType = previousSibling.getNodeType();
            if (nodeType == 5) {
                if (this.getWholeTextBackward(previousSibling.getLastChild(), sb, previousSibling)) {
                    return true;
                }
            }
            else {
                if (nodeType != 3 && nodeType != 4) {
                    return true;
                }
                ((TextImpl)previousSibling).insertTextContent(sb);
            }
            previousSibling = previousSibling.getPreviousSibling();
        }
        if (n != 0) {
            this.getWholeTextBackward(node.getPreviousSibling(), sb, node.getParentNode());
            return true;
        }
        return false;
    }
    
    public Text replaceWholeText(final String data) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final Node parentNode = this.getParentNode();
        if (data == null || data.length() == 0) {
            if (parentNode != null) {
                parentNode.removeChild(this);
            }
            return null;
        }
        if (this.ownerDocument().errorChecking) {
            if (!this.canModifyPrev(this)) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (!this.canModifyNext(this)) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
        }
        Text text;
        if (this.isReadOnly()) {
            final Text textNode = this.ownerDocument().createTextNode(data);
            if (parentNode == null) {
                return textNode;
            }
            parentNode.insertBefore(textNode, this);
            parentNode.removeChild(this);
            text = textNode;
        }
        else {
            this.setData(data);
            text = this;
        }
        for (Node node = text.getPreviousSibling(); node != null && (node.getNodeType() == 3 || node.getNodeType() == 4 || (node.getNodeType() == 5 && this.hasTextOnlyChildren(node))); node = text.getPreviousSibling()) {
            parentNode.removeChild(node);
        }
        for (Node node2 = text.getNextSibling(); node2 != null && (node2.getNodeType() == 3 || node2.getNodeType() == 4 || (node2.getNodeType() == 5 && this.hasTextOnlyChildren(node2))); node2 = text.getNextSibling()) {
            parentNode.removeChild(node2);
        }
        return text;
    }
    
    private boolean canModifyPrev(final Node node) {
        boolean b = false;
        for (Node node2 = node.getPreviousSibling(); node2 != null; node2 = node2.getPreviousSibling()) {
            final short nodeType = node2.getNodeType();
            if (nodeType == 5) {
                Node node3 = node2.getLastChild();
                if (node3 == null) {
                    return false;
                }
                while (node3 != null) {
                    final short nodeType2 = node3.getNodeType();
                    if (nodeType2 == 3 || nodeType2 == 4) {
                        b = true;
                    }
                    else {
                        if (nodeType2 != 5) {
                            return !b;
                        }
                        if (!this.canModifyPrev(node3)) {
                            return false;
                        }
                        b = true;
                    }
                    node3 = node3.getPreviousSibling();
                }
            }
            else if (nodeType != 3) {
                if (nodeType != 4) {
                    return true;
                }
            }
        }
        return true;
    }
    
    private boolean canModifyNext(final Node node) {
        boolean b = false;
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            final short nodeType = node2.getNodeType();
            if (nodeType == 5) {
                Node node3 = node2.getFirstChild();
                if (node3 == null) {
                    return false;
                }
                while (node3 != null) {
                    final short nodeType2 = node3.getNodeType();
                    if (nodeType2 == 3 || nodeType2 == 4) {
                        b = true;
                    }
                    else {
                        if (nodeType2 != 5) {
                            return !b;
                        }
                        if (!this.canModifyNext(node3)) {
                            return false;
                        }
                        b = true;
                    }
                    node3 = node3.getNextSibling();
                }
            }
            else if (nodeType != 3) {
                if (nodeType != 4) {
                    return true;
                }
            }
        }
        return true;
    }
    
    private boolean hasTextOnlyChildren(final Node node) {
        if (node == null) {
            return false;
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final short nodeType = node2.getNodeType();
            if (nodeType == 5) {
                return this.hasTextOnlyChildren(node2);
            }
            if (nodeType != 3 && nodeType != 4 && nodeType != 5) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isIgnorableWhitespace() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalIsIgnorableWhitespace();
    }
    
    public Text splitText(final int n) throws DOMException {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (n < 0 || n > this.data.length()) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        final Text textNode = this.getOwnerDocument().createTextNode(this.data.substring(n));
        this.setNodeValue(this.data.substring(0, n));
        final Node parentNode = this.getParentNode();
        if (parentNode != null) {
            parentNode.insertBefore(textNode, this.nextSibling);
        }
        return textNode;
    }
    
    public void replaceData(final String data) {
        this.data = data;
    }
    
    public String removeData() {
        final String data = this.data;
        this.data = "";
        return data;
    }
}
