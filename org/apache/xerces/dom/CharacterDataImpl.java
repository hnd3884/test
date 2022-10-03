package org.apache.xerces.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

public abstract class CharacterDataImpl extends ChildNode
{
    static final long serialVersionUID = 7931170150428474230L;
    protected String data;
    private static final transient NodeList singletonNodeList;
    
    public CharacterDataImpl() {
    }
    
    protected CharacterDataImpl(final CoreDocumentImpl coreDocumentImpl, final String data) {
        super(coreDocumentImpl);
        this.data = data;
    }
    
    public NodeList getChildNodes() {
        return CharacterDataImpl.singletonNodeList;
    }
    
    public String getNodeValue() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data;
    }
    
    protected void setNodeValueInternal(final String s) {
        this.setNodeValueInternal(s, false);
    }
    
    protected void setNodeValueInternal(final String data, final boolean b) {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final String data2 = this.data;
        ownerDocument.modifyingCharacterData(this, b);
        ownerDocument.modifiedCharacterData(this, data2, this.data = data, b);
    }
    
    public void setNodeValue(final String nodeValueInternal) {
        this.setNodeValueInternal(nodeValueInternal);
        this.ownerDocument().replacedText(this);
    }
    
    public String getData() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data;
    }
    
    public int getLength() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data.length();
    }
    
    public void appendData(final String s) {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (s == null) {
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setNodeValue(this.data + s);
    }
    
    public void deleteData(final int n, final int n2) throws DOMException {
        this.internalDeleteData(n, n2, false);
    }
    
    void internalDeleteData(final int n, final int n2, final boolean b) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (n2 < 0) {
                throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
            }
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int max = Math.max(this.data.length() - n2 - n, 0);
        try {
            this.setNodeValueInternal(this.data.substring(0, n) + ((max > 0) ? this.data.substring(n + n2, n + n2 + max) : ""), b);
            ownerDocument.deletedText(this, n, n2);
        }
        catch (final StringIndexOutOfBoundsException ex) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
    }
    
    public void insertData(final int n, final String s) throws DOMException {
        this.internalInsertData(n, s, false);
    }
    
    void internalInsertData(final int n, final String s, final boolean b) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        try {
            this.setNodeValueInternal(new StringBuffer(this.data).insert(n, s).toString(), b);
            ownerDocument.insertedText(this, n, s.length());
        }
        catch (final StringIndexOutOfBoundsException ex) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
    }
    
    public void replaceData(final int n, final int n2, final String s) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        ownerDocument.replacingData(this);
        final String data = this.data;
        this.internalDeleteData(n, n2, true);
        this.internalInsertData(n, s, true);
        ownerDocument.replacedCharacterData(this, data, this.data);
    }
    
    public void setData(final String nodeValue) throws DOMException {
        this.setNodeValue(nodeValue);
    }
    
    public String substringData(final int n, final int n2) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int length = this.data.length();
        if (n2 < 0 || n < 0 || n > length - 1) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        return this.data.substring(n, Math.min(n + n2, length));
    }
    
    static {
        singletonNodeList = new NodeList() {
            public Node item(final int n) {
                return null;
            }
            
            public int getLength() {
                return 0;
            }
        };
    }
}
