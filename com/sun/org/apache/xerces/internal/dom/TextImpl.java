package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;
import org.w3c.dom.CharacterData;

public class TextImpl extends CharacterDataImpl implements CharacterData, Text
{
    static final long serialVersionUID = -5294980852957403469L;
    
    public TextImpl() {
    }
    
    public TextImpl(final CoreDocumentImpl ownerDoc, final String data) {
        super(ownerDoc, data);
    }
    
    public void setValues(final CoreDocumentImpl ownerDoc, final String data) {
        this.flags = 0;
        this.nextSibling = null;
        this.previousSibling = null;
        this.setOwnerDocument(ownerDoc);
        super.data = data;
    }
    
    @Override
    public short getNodeType() {
        return 3;
    }
    
    @Override
    public String getNodeName() {
        return "#text";
    }
    
    public void setIgnorableWhitespace(final boolean ignore) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isIgnorableWhitespace(ignore);
    }
    
    @Override
    public boolean isElementContentWhitespace() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalIsIgnorableWhitespace();
    }
    
    @Override
    public String getWholeText() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.fBufferStr == null) {
            this.fBufferStr = new StringBuffer();
        }
        else {
            this.fBufferStr.setLength(0);
        }
        if (this.data != null && this.data.length() != 0) {
            this.fBufferStr.append(this.data);
        }
        this.getWholeTextBackward(this.getPreviousSibling(), this.fBufferStr, this.getParentNode());
        final String temp = this.fBufferStr.toString();
        this.fBufferStr.setLength(0);
        this.getWholeTextForward(this.getNextSibling(), this.fBufferStr, this.getParentNode());
        return temp + this.fBufferStr.toString();
    }
    
    protected void insertTextContent(final StringBuffer buf) throws DOMException {
        final String content = this.getNodeValue();
        if (content != null) {
            buf.insert(0, content);
        }
    }
    
    private boolean getWholeTextForward(Node node, final StringBuffer buffer, final Node parent) {
        boolean inEntRef = false;
        if (parent != null) {
            inEntRef = (parent.getNodeType() == 5);
        }
        while (node != null) {
            final short type = node.getNodeType();
            if (type == 5) {
                if (this.getWholeTextForward(node.getFirstChild(), buffer, node)) {
                    return true;
                }
            }
            else {
                if (type != 3 && type != 4) {
                    return true;
                }
                ((NodeImpl)node).getTextContent(buffer);
            }
            node = node.getNextSibling();
        }
        if (inEntRef) {
            this.getWholeTextForward(parent.getNextSibling(), buffer, parent.getParentNode());
            return true;
        }
        return false;
    }
    
    private boolean getWholeTextBackward(Node node, final StringBuffer buffer, final Node parent) {
        boolean inEntRef = false;
        if (parent != null) {
            inEntRef = (parent.getNodeType() == 5);
        }
        while (node != null) {
            final short type = node.getNodeType();
            if (type == 5) {
                if (this.getWholeTextBackward(node.getLastChild(), buffer, node)) {
                    return true;
                }
            }
            else {
                if (type != 3 && type != 4) {
                    return true;
                }
                ((TextImpl)node).insertTextContent(buffer);
            }
            node = node.getPreviousSibling();
        }
        if (inEntRef) {
            this.getWholeTextBackward(parent.getPreviousSibling(), buffer, parent.getParentNode());
            return true;
        }
        return false;
    }
    
    @Override
    public Text replaceWholeText(final String content) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final Node parent = this.getParentNode();
        if (content == null || content.length() == 0) {
            if (parent != null) {
                parent.removeChild(this);
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
        Text currentNode = null;
        if (this.isReadOnly()) {
            final Text newNode = this.ownerDocument().createTextNode(content);
            if (parent == null) {
                return newNode;
            }
            parent.insertBefore(newNode, this);
            parent.removeChild(this);
            currentNode = newNode;
        }
        else {
            this.setData(content);
            currentNode = this;
        }
        for (Node prev = currentNode.getPreviousSibling(); prev != null && (prev.getNodeType() == 3 || prev.getNodeType() == 4 || (prev.getNodeType() == 5 && this.hasTextOnlyChildren(prev))); prev = currentNode, prev = prev.getPreviousSibling()) {
            parent.removeChild(prev);
        }
        for (Node next = currentNode.getNextSibling(); next != null && (next.getNodeType() == 3 || next.getNodeType() == 4 || (next.getNodeType() == 5 && this.hasTextOnlyChildren(next))); next = currentNode, next = next.getNextSibling()) {
            parent.removeChild(next);
        }
        return currentNode;
    }
    
    private boolean canModifyPrev(final Node node) {
        boolean textLastChild = false;
        for (Node prev = node.getPreviousSibling(); prev != null; prev = prev.getPreviousSibling()) {
            final short type = prev.getNodeType();
            if (type == 5) {
                Node lastChild = prev.getLastChild();
                if (lastChild == null) {
                    return false;
                }
                while (lastChild != null) {
                    final short lType = lastChild.getNodeType();
                    if (lType == 3 || lType == 4) {
                        textLastChild = true;
                    }
                    else {
                        if (lType != 5) {
                            return !textLastChild;
                        }
                        if (!this.canModifyPrev(lastChild)) {
                            return false;
                        }
                        textLastChild = true;
                    }
                    lastChild = lastChild.getPreviousSibling();
                }
            }
            else if (type != 3) {
                if (type != 4) {
                    return true;
                }
            }
        }
        return true;
    }
    
    private boolean canModifyNext(final Node node) {
        boolean textFirstChild = false;
        for (Node next = node.getNextSibling(); next != null; next = next.getNextSibling()) {
            final short type = next.getNodeType();
            if (type == 5) {
                Node firstChild = next.getFirstChild();
                if (firstChild == null) {
                    return false;
                }
                while (firstChild != null) {
                    final short lType = firstChild.getNodeType();
                    if (lType == 3 || lType == 4) {
                        textFirstChild = true;
                    }
                    else {
                        if (lType != 5) {
                            return !textFirstChild;
                        }
                        if (!this.canModifyNext(firstChild)) {
                            return false;
                        }
                        textFirstChild = true;
                    }
                    firstChild = firstChild.getNextSibling();
                }
            }
            else if (type != 3) {
                if (type != 4) {
                    return true;
                }
            }
        }
        return true;
    }
    
    private boolean hasTextOnlyChildren(final Node node) {
        Node child = node;
        if (child == null) {
            return false;
        }
        for (child = child.getFirstChild(); child != null; child = child.getNextSibling()) {
            final int type = child.getNodeType();
            if (type == 5) {
                return this.hasTextOnlyChildren(child);
            }
            if (type != 3 && type != 4 && type != 5) {
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
    
    @Override
    public Text splitText(final int offset) throws DOMException {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (offset < 0 || offset > this.data.length()) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        final Text newText = this.getOwnerDocument().createTextNode(this.data.substring(offset));
        this.setNodeValue(this.data.substring(0, offset));
        final Node parentNode = this.getParentNode();
        if (parentNode != null) {
            parentNode.insertBefore(newText, this.nextSibling);
        }
        return newText;
    }
    
    public void replaceData(final String value) {
        this.data = value;
    }
    
    public String removeData() {
        final String olddata = this.data;
        this.data = "";
        return olddata;
    }
}
