package com.sun.org.apache.xalan.internal.xsltc;

import java.util.Map;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public interface DOM
{
    public static final int FIRST_TYPE = 0;
    public static final int NO_TYPE = -1;
    public static final int NULL = 0;
    public static final int RETURN_CURRENT = 0;
    public static final int RETURN_PARENT = 1;
    public static final int SIMPLE_RTF = 0;
    public static final int ADAPTIVE_RTF = 1;
    public static final int TREE_RTF = 2;
    
    DTMAxisIterator getIterator();
    
    String getStringValue();
    
    DTMAxisIterator getChildren(final int p0);
    
    DTMAxisIterator getTypedChildren(final int p0);
    
    DTMAxisIterator getAxisIterator(final int p0);
    
    DTMAxisIterator getTypedAxisIterator(final int p0, final int p1);
    
    DTMAxisIterator getNthDescendant(final int p0, final int p1, final boolean p2);
    
    DTMAxisIterator getNamespaceAxisIterator(final int p0, final int p1);
    
    DTMAxisIterator getNodeValueIterator(final DTMAxisIterator p0, final int p1, final String p2, final boolean p3);
    
    DTMAxisIterator orderNodes(final DTMAxisIterator p0, final int p1);
    
    String getNodeName(final int p0);
    
    String getNodeNameX(final int p0);
    
    String getNamespaceName(final int p0);
    
    int getExpandedTypeID(final int p0);
    
    int getNamespaceType(final int p0);
    
    int getParent(final int p0);
    
    int getAttributeNode(final int p0, final int p1);
    
    String getStringValueX(final int p0);
    
    void copy(final int p0, final SerializationHandler p1) throws TransletException;
    
    void copy(final DTMAxisIterator p0, final SerializationHandler p1) throws TransletException;
    
    String shallowCopy(final int p0, final SerializationHandler p1) throws TransletException;
    
    boolean lessThan(final int p0, final int p1);
    
    void characters(final int p0, final SerializationHandler p1) throws TransletException;
    
    Node makeNode(final int p0);
    
    Node makeNode(final DTMAxisIterator p0);
    
    NodeList makeNodeList(final int p0);
    
    NodeList makeNodeList(final DTMAxisIterator p0);
    
    String getLanguage(final int p0);
    
    int getSize();
    
    String getDocumentURI(final int p0);
    
    void setFilter(final StripFilter p0);
    
    void setupMapping(final String[] p0, final String[] p1, final int[] p2, final String[] p3);
    
    boolean isElement(final int p0);
    
    boolean isAttribute(final int p0);
    
    String lookupNamespace(final int p0, final String p1) throws TransletException;
    
    int getNodeIdent(final int p0);
    
    int getNodeHandle(final int p0);
    
    DOM getResultTreeFrag(final int p0, final int p1);
    
    DOM getResultTreeFrag(final int p0, final int p1, final boolean p2);
    
    SerializationHandler getOutputDomBuilder();
    
    int getNSType(final int p0);
    
    int getDocument();
    
    String getUnparsedEntityURI(final String p0);
    
    Map<String, Integer> getElementsWithIDs();
    
    void release();
}
