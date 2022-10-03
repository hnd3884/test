package org.apache.xerces.dom;

import org.apache.xerces.dom3.as.DOMASException;
import org.apache.xerces.dom3.as.ASContentModel;
import org.apache.xerces.dom3.as.ASEntityDeclaration;
import org.apache.xerces.dom3.as.ASNotationDeclaration;
import org.apache.xerces.dom3.as.ASAttributeDeclaration;
import org.apache.xerces.dom3.as.ASElementDeclaration;
import org.apache.xerces.dom3.as.ASObjectList;
import org.apache.xerces.dom3.as.ASNamedObjectMap;
import org.apache.xerces.dom3.as.ASObject;
import org.w3c.dom.DOMException;
import org.apache.xerces.impl.xs.SchemaGrammar;
import java.util.Vector;
import org.apache.xerces.dom3.as.ASModel;

public class ASModelImpl implements ASModel
{
    boolean fNamespaceAware;
    protected Vector fASModels;
    protected SchemaGrammar fGrammar;
    
    public ASModelImpl() {
        this.fNamespaceAware = true;
        this.fGrammar = null;
        this.fASModels = new Vector();
    }
    
    public ASModelImpl(final boolean fNamespaceAware) {
        this.fNamespaceAware = true;
        this.fGrammar = null;
        this.fASModels = new Vector();
        this.fNamespaceAware = fNamespaceAware;
    }
    
    public short getAsNodeType() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASModel getOwnerASModel() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setOwnerASModel(final ASModel asModel) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public String getNodeName() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setNodeName(final String s) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public String getPrefix() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setPrefix(final String s) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public String getLocalName() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setLocalName(final String s) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public String getNamespaceURI() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setNamespaceURI(final String s) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASObject cloneASObject(final boolean b) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public boolean getIsNamespaceAware() {
        return this.fNamespaceAware;
    }
    
    public short getUsageLocation() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public String getAsLocation() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setAsLocation(final String s) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public String getAsHint() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setAsHint(final String s) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public boolean getContainer() {
        return this.fGrammar != null;
    }
    
    public ASNamedObjectMap getElementDeclarations() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASNamedObjectMap getAttributeDeclarations() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASNamedObjectMap getNotationDeclarations() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASNamedObjectMap getEntityDeclarations() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASNamedObjectMap getContentModelDeclarations() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void addASModel(final ASModel asModel) {
        this.fASModels.addElement(asModel);
    }
    
    public ASObjectList getASModels() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void removeAS(final ASModel asModel) {
        this.fASModels.removeElement(asModel);
    }
    
    public boolean validate() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void importASObject(final ASObject asObject) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void insertASObject(final ASObject asObject) {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASElementDeclaration createASElementDeclaration(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASAttributeDeclaration createASAttributeDeclaration(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASNotationDeclaration createASNotationDeclaration(final String s, final String s2, final String s3, final String s4) throws DOMException {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASEntityDeclaration createASEntityDeclaration(final String s) throws DOMException {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public ASContentModel createASContentModel(final int n, final int n2, final short n3) throws DOMASException {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public SchemaGrammar getGrammar() {
        return this.fGrammar;
    }
    
    public void setGrammar(final SchemaGrammar fGrammar) {
        this.fGrammar = fGrammar;
    }
    
    public Vector getInternalASModels() {
        return this.fASModels;
    }
}
