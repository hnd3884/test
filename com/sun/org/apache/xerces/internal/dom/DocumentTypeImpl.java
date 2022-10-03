package com.sun.org.apache.xerces.internal.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import java.io.ObjectStreamField;
import java.util.Map;
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
    private Map<String, UserDataRecord> userData;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public DocumentTypeImpl(final CoreDocumentImpl ownerDocument, final String name) {
        super(ownerDocument);
        this.doctypeNumber = 0;
        this.userData = null;
        this.name = name;
        this.entities = new NamedNodeMapImpl(this);
        this.notations = new NamedNodeMapImpl(this);
        this.elements = new NamedNodeMapImpl(this);
    }
    
    public DocumentTypeImpl(final CoreDocumentImpl ownerDocument, final String qualifiedName, final String publicID, final String systemID) {
        this(ownerDocument, qualifiedName);
        this.publicID = publicID;
        this.systemID = systemID;
    }
    
    @Override
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicID;
    }
    
    @Override
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
    
    @Override
    public String getInternalSubset() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalSubset;
    }
    
    @Override
    public short getNodeType() {
        return 10;
    }
    
    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final DocumentTypeImpl newnode = (DocumentTypeImpl)super.cloneNode(deep);
        newnode.entities = this.entities.cloneMap(newnode);
        newnode.notations = this.notations.cloneMap(newnode);
        newnode.elements = this.elements.cloneMap(newnode);
        return newnode;
    }
    
    @Override
    public String getTextContent() throws DOMException {
        return null;
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
    }
    
    @Override
    public boolean isEqualNode(final Node arg) {
        if (!super.isEqualNode(arg)) {
            return false;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final DocumentTypeImpl argDocType = (DocumentTypeImpl)arg;
        if ((this.getPublicId() == null && argDocType.getPublicId() != null) || (this.getPublicId() != null && argDocType.getPublicId() == null) || (this.getSystemId() == null && argDocType.getSystemId() != null) || (this.getSystemId() != null && argDocType.getSystemId() == null) || (this.getInternalSubset() == null && argDocType.getInternalSubset() != null) || (this.getInternalSubset() != null && argDocType.getInternalSubset() == null)) {
            return false;
        }
        if (this.getPublicId() != null && !this.getPublicId().equals(argDocType.getPublicId())) {
            return false;
        }
        if (this.getSystemId() != null && !this.getSystemId().equals(argDocType.getSystemId())) {
            return false;
        }
        if (this.getInternalSubset() != null && !this.getInternalSubset().equals(argDocType.getInternalSubset())) {
            return false;
        }
        final NamedNodeMapImpl argEntities = argDocType.entities;
        if ((this.entities == null && argEntities != null) || (this.entities != null && argEntities == null)) {
            return false;
        }
        if (this.entities != null && argEntities != null) {
            if (this.entities.getLength() != argEntities.getLength()) {
                return false;
            }
            for (int index = 0; this.entities.item(index) != null; ++index) {
                final Node entNode1 = this.entities.item(index);
                final Node entNode2 = argEntities.getNamedItem(entNode1.getNodeName());
                if (!((NodeImpl)entNode1).isEqualNode(entNode2)) {
                    return false;
                }
            }
        }
        final NamedNodeMapImpl argNotations = argDocType.notations;
        if ((this.notations == null && argNotations != null) || (this.notations != null && argNotations == null)) {
            return false;
        }
        if (this.notations != null && argNotations != null) {
            if (this.notations.getLength() != argNotations.getLength()) {
                return false;
            }
            for (int index2 = 0; this.notations.item(index2) != null; ++index2) {
                final Node noteNode1 = this.notations.item(index2);
                final Node noteNode2 = argNotations.getNamedItem(noteNode1.getNodeName());
                if (!((NodeImpl)noteNode1).isEqualNode(noteNode2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    void setOwnerDocument(final CoreDocumentImpl doc) {
        super.setOwnerDocument(doc);
        this.entities.setOwnerDocument(doc);
        this.notations.setOwnerDocument(doc);
        this.elements.setOwnerDocument(doc);
    }
    
    @Override
    protected int getNodeNumber() {
        if (this.getOwnerDocument() != null) {
            return super.getNodeNumber();
        }
        if (this.doctypeNumber == 0) {
            final CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.doctypeNumber = cd.assignDocTypeNumber();
        }
        return this.doctypeNumber;
    }
    
    @Override
    public String getName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    @Override
    public NamedNodeMap getEntities() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.entities;
    }
    
    @Override
    public NamedNodeMap getNotations() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.notations;
    }
    
    @Override
    public void setReadOnly(final boolean readOnly, final boolean deep) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setReadOnly(readOnly, deep);
        this.elements.setReadOnly(readOnly, true);
        this.entities.setReadOnly(readOnly, true);
        this.notations.setReadOnly(readOnly, true);
    }
    
    public NamedNodeMap getElements() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.elements;
    }
    
    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        if (this.userData == null) {
            this.userData = new HashMap<String, UserDataRecord>();
        }
        if (data == null) {
            if (this.userData != null) {
                final UserDataRecord udr = this.userData.remove(key);
                if (udr != null) {
                    return udr.fData;
                }
            }
            return null;
        }
        final UserDataRecord udr = this.userData.put(key, new UserDataRecord(data, handler));
        if (udr != null) {
            return udr.fData;
        }
        return null;
    }
    
    @Override
    public Object getUserData(final String key) {
        if (this.userData == null) {
            return null;
        }
        final UserDataRecord udr = this.userData.get(key);
        if (udr != null) {
            return udr.fData;
        }
        return null;
    }
    
    @Override
    protected Map<String, UserDataRecord> getUserDataRecord() {
        return this.userData;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        final Hashtable<String, UserDataRecord> ud = (this.userData == null) ? null : new Hashtable<String, UserDataRecord>(this.userData);
        final ObjectOutputStream.PutField pf = out.putFields();
        pf.put("name", this.name);
        pf.put("entities", this.entities);
        pf.put("notations", this.notations);
        pf.put("elements", this.elements);
        pf.put("publicID", this.publicID);
        pf.put("systemID", this.systemID);
        pf.put("internalSubset", this.internalSubset);
        pf.put("doctypeNumber", this.doctypeNumber);
        pf.put("userData", ud);
        out.writeFields();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField gf = in.readFields();
        this.name = (String)gf.get("name", null);
        this.entities = (NamedNodeMapImpl)gf.get("entities", null);
        this.notations = (NamedNodeMapImpl)gf.get("notations", null);
        this.elements = (NamedNodeMapImpl)gf.get("elements", null);
        this.publicID = (String)gf.get("publicID", null);
        this.systemID = (String)gf.get("systemID", null);
        this.internalSubset = (String)gf.get("internalSubset", null);
        this.doctypeNumber = gf.get("doctypeNumber", 0);
        final Hashtable<String, UserDataRecord> ud = (Hashtable<String, UserDataRecord>)gf.get("userData", null);
        if (ud != null) {
            this.userData = new HashMap<String, UserDataRecord>(ud);
        }
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("name", String.class), new ObjectStreamField("entities", NamedNodeMapImpl.class), new ObjectStreamField("notations", NamedNodeMapImpl.class), new ObjectStreamField("elements", NamedNodeMapImpl.class), new ObjectStreamField("publicID", String.class), new ObjectStreamField("systemID", String.class), new ObjectStreamField("internalSubset", String.class), new ObjectStreamField("doctypeNumber", Integer.TYPE), new ObjectStreamField("userData", Hashtable.class) };
    }
}
