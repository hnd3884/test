package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

public abstract class CharacterDataImpl extends ChildNode
{
    static final long serialVersionUID = 7931170150428474230L;
    protected String data;
    private static transient NodeList singletonNodeList;
    
    public CharacterDataImpl() {
    }
    
    protected CharacterDataImpl(final CoreDocumentImpl ownerDocument, final String data) {
        super(ownerDocument);
        this.data = data;
    }
    
    @Override
    public NodeList getChildNodes() {
        return CharacterDataImpl.singletonNodeList;
    }
    
    @Override
    public String getNodeValue() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data;
    }
    
    protected void setNodeValueInternal(final String value) {
        this.setNodeValueInternal(value, false);
    }
    
    protected void setNodeValueInternal(final String value, final boolean replace) {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final String oldvalue = this.data;
        ownerDocument.modifyingCharacterData(this, replace);
        ownerDocument.modifiedCharacterData(this, oldvalue, this.data = value, replace);
    }
    
    @Override
    public void setNodeValue(final String value) {
        this.setNodeValueInternal(value);
        this.ownerDocument().replacedText(this);
    }
    
    public String getData() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data;
    }
    
    @Override
    public int getLength() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data.length();
    }
    
    public void appendData(final String data) {
        if (this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        if (data == null) {
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setNodeValue(this.data + data);
    }
    
    public void deleteData(final int offset, final int count) throws DOMException {
        this.internalDeleteData(offset, count, false);
    }
    
    void internalDeleteData(final int offset, final int count, final boolean replace) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            if (count < 0) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
                throw new DOMException((short)1, msg);
            }
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int tailLength = Math.max(this.data.length() - count - offset, 0);
        try {
            final String value = this.data.substring(0, offset) + ((tailLength > 0) ? this.data.substring(offset + count, offset + count + tailLength) : "");
            this.setNodeValueInternal(value, replace);
            ownerDocument.deletedText(this, offset, count);
        }
        catch (final StringIndexOutOfBoundsException e) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException((short)1, msg2);
        }
    }
    
    public void insertData(final int offset, final String data) throws DOMException {
        this.internalInsertData(offset, data, false);
    }
    
    void internalInsertData(final int offset, final String data, final boolean replace) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        try {
            final String value = new StringBuffer(this.data).insert(offset, data).toString();
            this.setNodeValueInternal(value, replace);
            ownerDocument.insertedText(this, offset, data.length());
        }
        catch (final StringIndexOutOfBoundsException e) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException((short)1, msg2);
        }
    }
    
    public void replaceData(final int offset, final int count, final String data) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException((short)7, msg);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        ownerDocument.replacingData(this);
        final String oldvalue = this.data;
        this.internalDeleteData(offset, count, true);
        this.internalInsertData(offset, data, true);
        ownerDocument.replacedCharacterData(this, oldvalue, this.data);
    }
    
    public void setData(final String value) throws DOMException {
        this.setNodeValue(value);
    }
    
    public String substringData(final int offset, final int count) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int length = this.data.length();
        if (count < 0 || offset < 0 || offset > length - 1) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException((short)1, msg);
        }
        final int tailIndex = Math.min(offset + count, length);
        return this.data.substring(offset, tailIndex);
    }
    
    static {
        CharacterDataImpl.singletonNodeList = new NodeList() {
            @Override
            public Node item(final int index) {
                return null;
            }
            
            @Override
            public int getLength() {
                return 0;
            }
        };
    }
}
