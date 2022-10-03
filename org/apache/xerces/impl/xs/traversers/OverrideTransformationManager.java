package org.apache.xerces.impl.xs.traversers;

import java.util.ArrayList;
import org.apache.xerces.impl.xs.SchemaSymbols;
import java.util.Iterator;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.Element;
import java.util.HashMap;

public final class OverrideTransformationManager
{
    public static final int STATE_INCLUDE = 1;
    public static final int STATE_CONTINUE = 2;
    public static final int STATE_DUPLICATE = 3;
    public static final int STATE_COLLISION = 4;
    private static String[] fGlobalComponentNames;
    private static String[] fCompositeComponentNames;
    private OverrideTransformer fOverrideTransformationHandler;
    private final HashMap fSystemId2ContextMap;
    private int fCurrentState;
    private final XSDHandler fSchemaHandler;
    
    public OverrideTransformationManager(final XSDHandler fSchemaHandler, final OverrideTransformer fOverrideTransformationHandler) {
        this.fSystemId2ContextMap = new HashMap();
        this.fCurrentState = 1;
        this.fSchemaHandler = fSchemaHandler;
        this.fOverrideTransformationHandler = fOverrideTransformationHandler;
    }
    
    public void reset() {
        this.fCurrentState = 1;
        if (this.fSystemId2ContextMap.size() != 0) {
            this.fSystemId2ContextMap.clear();
        }
    }
    
    public Element transform(final String s, final Element element, final Element element2) {
        boolean b = false;
        Element transform;
        try {
            transform = this.fOverrideTransformationHandler.transform(element, element2);
        }
        catch (final OverrideTransformException ex) {
            return element2;
        }
        if (transform != null) {
            b = true;
        }
        else {
            transform = element2;
        }
        if (this.checkSchemaDependencies(this.nullToEmptyString(s), element, transform, b)) {
            return transform;
        }
        return null;
    }
    
    public void addSchemaRoot(final String s, final Element element) {
        this.setDocumentMapForSystemId(this.nullToEmptyString(s), this.createDocumentMap(element, DocumentContext.IS_ORIGINAL));
    }
    
    public void checkSchemaRoot(final String s, final Element element, final Element element2) {
        final String nullToEmptyString = this.nullToEmptyString(s);
        if (this.includeSchemaDependencies(nullToEmptyString, element2, DocumentContext.IS_ORIGINAL)) {
            this.fCurrentState = 1;
            return;
        }
        final DocumentContext documentMapForSystemId = this.getDocumentMapForSystemId(nullToEmptyString);
        final Iterator schemaArray = documentMapForSystemId.getSchemaArray();
        while (schemaArray.hasNext()) {
            if (DocumentContext.IS_ORIGINAL == documentMapForSystemId.getSchemaState((Element)schemaArray.next())) {
                this.fCurrentState = 3;
                return;
            }
        }
        this.fCurrentState = 4;
        this.fSchemaHandler.reportSchemaError("src-override-collision.1", new Object[] { s, DOMUtil.getLocalName(element) }, element);
    }
    
    public int getCurrentState() {
        return this.fCurrentState;
    }
    
    public void setOverrideHandler(final OverrideTransformer fOverrideTransformationHandler) {
        this.fOverrideTransformationHandler = fOverrideTransformationHandler;
    }
    
    private DocumentContext createDocumentMap(final Element element, final Boolean b) {
        final DocumentContext documentContext = new DocumentContext();
        documentContext.addSchemasToArray(element, b);
        return documentContext;
    }
    
    public boolean hasGlobalDecl(final Element element) {
        return this.hasComponentsTypes(element, OverrideTransformationManager.fGlobalComponentNames);
    }
    
    public boolean hasCompositionalDecl(final Element element) {
        return this.hasComponentsTypes(element, OverrideTransformationManager.fCompositeComponentNames);
    }
    
    private boolean checkSchemaDependencies(final String s, final Element element, final Element element2, final boolean b) {
        final Boolean b2 = b ? DocumentContext.IS_TRANSFORMED : DocumentContext.IS_ORIGINAL;
        if (this.includeSchemaDependencies(s, element2, b2)) {
            this.fCurrentState = 1;
            return true;
        }
        boolean b3 = false;
        final DocumentContext documentMapForSystemId = this.getDocumentMapForSystemId(s);
        final Iterator schemaArray = documentMapForSystemId.getSchemaArray();
        while (schemaArray.hasNext()) {
            if (this.checkDuplicateElements(documentMapForSystemId, (Element)schemaArray.next(), element2, b2)) {
                this.fCurrentState = 3;
                return false;
            }
            b3 = true;
        }
        if (b3) {
            if (this.hasGlobalDecl(element2)) {
                this.fCurrentState = 4;
                this.fSchemaHandler.reportSchemaError("src-override-collision.2", new Object[] { s }, element);
                return false;
            }
            documentMapForSystemId.addSchemasToArray(element2);
        }
        this.fCurrentState = 2;
        return true;
    }
    
    private boolean includeSchemaDependencies(final String s, final Element element, final Boolean b) {
        if (!this.isSchemaAlreadyTraversed(s)) {
            this.setDocumentMapForSystemId(s, this.createDocumentMap(element, b));
            return true;
        }
        return false;
    }
    
    private boolean isSchemaAlreadyTraversed(final String s) {
        return this.fSystemId2ContextMap.get(s) != null;
    }
    
    private boolean checkDuplicateElements(final DocumentContext documentContext, final Element element, final Element element2, final Boolean b) {
        final Boolean schemaState = documentContext.getSchemaState(element);
        if (schemaState == DocumentContext.IS_TRANSFORMED && b == DocumentContext.IS_TRANSFORMED) {
            return this.compareComponents(element, element2);
        }
        return schemaState == DocumentContext.IS_ORIGINAL && b == DocumentContext.IS_ORIGINAL;
    }
    
    private boolean compareComponents(final Element element, final Element element2) {
        element.normalize();
        element2.normalize();
        return element.isEqualNode(element2);
    }
    
    private DocumentContext getDocumentMapForSystemId(final String s) {
        return this.fSystemId2ContextMap.get(s);
    }
    
    private void setDocumentMapForSystemId(final String s, final DocumentContext documentContext) {
        this.fSystemId2ContextMap.put(s, documentContext);
    }
    
    private String nullToEmptyString(final String s) {
        return (s == null) ? "" : s;
    }
    
    private boolean hasComponentsTypes(final Element element, final String[] array) {
        for (Element element2 = DOMUtil.getFirstChildElement(element); element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
            final String localName = this.getLocalName(element2);
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals(localName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private String getLocalName(final Node node) {
        final String localName = DOMUtil.getLocalName(node);
        if (localName.indexOf(":") > -1) {
            return localName.split(":")[1];
        }
        return localName;
    }
    
    static {
        OverrideTransformationManager.fGlobalComponentNames = new String[] { SchemaSymbols.ELT_ATTRIBUTEGROUP, SchemaSymbols.ELT_ATTRIBUTE, SchemaSymbols.ELT_COMPLEXTYPE, SchemaSymbols.ELT_SIMPLETYPE, SchemaSymbols.ELT_ELEMENT, SchemaSymbols.ELT_NOTATION, SchemaSymbols.ELT_GROUP };
        OverrideTransformationManager.fCompositeComponentNames = new String[] { SchemaSymbols.ELT_INCLUDE, SchemaSymbols.ELT_OVERRIDE, SchemaSymbols.ELT_REDEFINE };
    }
    
    private static final class DocumentContext
    {
        private final ArrayList fRootElementList;
        private final HashMap fSchema2StateMap;
        private static final Boolean IS_ORIGINAL;
        private static final Boolean IS_TRANSFORMED;
        
        DocumentContext() {
            this.fRootElementList = new ArrayList();
            this.fSchema2StateMap = new HashMap();
        }
        
        void addSchemasToArray(final Element element) {
            this.fRootElementList.add(element);
        }
        
        void addSchemasToArray(final Element element, final Boolean b) {
            this.addSchemasToArray(element);
            this.fSchema2StateMap.put(element, b);
        }
        
        Boolean getSchemaState(final Element element) {
            return this.fSchema2StateMap.get(element);
        }
        
        Iterator getSchemaArray() {
            return this.fRootElementList.iterator();
        }
        
        void clear() {
            this.fRootElementList.clear();
            this.fSchema2StateMap.clear();
        }
        
        static {
            IS_ORIGINAL = Boolean.TRUE;
            IS_TRANSFORMED = Boolean.FALSE;
        }
    }
}
