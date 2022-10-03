package org.apache.xerces.dom;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import java.util.Hashtable;
import org.w3c.dom.DocumentType;

public class DocumentTypeImpl extends ParentNode implements DocumentType
{
    static final long serialVersionUID = 7751299192316526485L;
    protected String name;
    protected NamedNodeMapImpl entities;
    protected NamedNodeMapImpl notations;
    protected NamedNodeMapImpl elements;
    protected String publicID;
    protected String systemID;
    protected String internalSubset;
    private int doctypeNumber;
    private Hashtable userData;
    
    public DocumentTypeImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.doctypeNumber = 0;
        this.userData = null;
        this.name = name;
        this.entities = new NamedNodeMapImpl(this);
        this.notations = new NamedNodeMapImpl(this);
        this.elements = new NamedNodeMapImpl(this);
    }
    
    public DocumentTypeImpl(final CoreDocumentImpl coreDocumentImpl, final String s, final String publicID, final String systemID) {
        this(coreDocumentImpl, s);
        this.publicID = publicID;
        this.systemID = systemID;
    }
    
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicID;
    }
    
    public String getSystemId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.systemID;
    }
    
    public void setInternalSubset(final String internalSubset) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.internalSubset = internalSubset;
    }
    
    public String getInternalSubset() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalSubset;
    }
    
    public short getNodeType() {
        return 10;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public Node cloneNode(final boolean b) {
        final DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)super.cloneNode(b);
        documentTypeImpl.entities = this.entities.cloneMap(documentTypeImpl);
        documentTypeImpl.notations = this.notations.cloneMap(documentTypeImpl);
        documentTypeImpl.elements = this.elements.cloneMap(documentTypeImpl);
        return documentTypeImpl;
    }
    
    public String getTextContent() throws DOMException {
        return null;
    }
    
    public void setTextContent(final String s) throws DOMException {
    }
    
    public boolean isEqualNode(final Node node) {
        if (!super.isEqualNode(node)) {
            return false;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)node;
        if ((this.getPublicId() == null && documentTypeImpl.getPublicId() != null) || (this.getPublicId() != null && documentTypeImpl.getPublicId() == null) || (this.getSystemId() == null && documentTypeImpl.getSystemId() != null) || (this.getSystemId() != null && documentTypeImpl.getSystemId() == null) || (this.getInternalSubset() == null && documentTypeImpl.getInternalSubset() != null) || (this.getInternalSubset() != null && documentTypeImpl.getInternalSubset() == null)) {
            return false;
        }
        if (this.getPublicId() != null && !this.getPublicId().equals(documentTypeImpl.getPublicId())) {
            return false;
        }
        if (this.getSystemId() != null && !this.getSystemId().equals(documentTypeImpl.getSystemId())) {
            return false;
        }
        if (this.getInternalSubset() != null && !this.getInternalSubset().equals(documentTypeImpl.getInternalSubset())) {
            return false;
        }
        final NamedNodeMapImpl entities = documentTypeImpl.entities;
        if ((this.entities == null && entities != null) || (this.entities != null && entities == null)) {
            return false;
        }
        if (this.entities != null && entities != null) {
            if (this.entities.getLength() != entities.getLength()) {
                return false;
            }
            for (int n = 0; this.entities.item(n) != null; ++n) {
                final Node item = this.entities.item(n);
                if (!((NodeImpl)item).isEqualNode(entities.getNamedItem(item.getNodeName()))) {
                    return false;
                }
            }
        }
        final NamedNodeMapImpl notations = documentTypeImpl.notations;
        if ((this.notations == null && notations != null) || (this.notations != null && notations == null)) {
            return false;
        }
        if (this.notations != null && notations != null) {
            if (this.notations.getLength() != notations.getLength()) {
                return false;
            }
            for (int n2 = 0; this.notations.item(n2) != null; ++n2) {
                final Node item2 = this.notations.item(n2);
                if (!((NodeImpl)item2).isEqualNode(notations.getNamedItem(item2.getNodeName()))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected void setOwnerDocument(final CoreDocumentImpl coreDocumentImpl) {
        super.setOwnerDocument(coreDocumentImpl);
        this.entities.setOwnerDocument(coreDocumentImpl);
        this.notations.setOwnerDocument(coreDocumentImpl);
        this.elements.setOwnerDocument(coreDocumentImpl);
    }
    
    protected int getNodeNumber() {
        if (this.getOwnerDocument() != null) {
            return super.getNodeNumber();
        }
        if (this.doctypeNumber == 0) {
            this.doctypeNumber = ((CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation()).assignDocTypeNumber();
        }
        return this.doctypeNumber;
    }
    
    public String getName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public NamedNodeMap getEntities() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.entities;
    }
    
    public NamedNodeMap getNotations() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.notations;
    }
    
    public void setReadOnly(final boolean b, final boolean b2) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setReadOnly(b, b2);
        this.elements.setReadOnly(b, true);
        this.entities.setReadOnly(b, true);
        this.notations.setReadOnly(b, true);
    }
    
    public NamedNodeMap getElements() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.elements;
    }
    
    public Object setUserData(final String s, final Object o, final UserDataHandler userDataHandler) {
        if (this.userData == null) {
            this.userData = new Hashtable();
        }
        if (o == null) {
            if (this.userData != null) {
                final UserDataRecord remove = this.userData.remove(s);
                if (remove != null) {
                    return remove.fData;
                }
            }
            return null;
        }
        final UserDataRecord put = this.userData.put(s, new UserDataRecord(o, userDataHandler));
        if (put != null) {
            return put.fData;
        }
        return null;
    }
    
    public Object getUserData(final String s) {
        if (this.userData == null) {
            return null;
        }
        final UserDataRecord value = this.userData.get(s);
        if (value != null) {
            return value.fData;
        }
        return null;
    }
    
    protected Hashtable getUserDataRecord() {
        return this.userData;
    }
}
