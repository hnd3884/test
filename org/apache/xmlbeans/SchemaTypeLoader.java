package org.apache.xmlbeans;

import org.w3c.dom.DOMImplementation;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import javax.xml.namespace.QName;

public interface SchemaTypeLoader
{
    SchemaType findType(final QName p0);
    
    SchemaType findDocumentType(final QName p0);
    
    SchemaType findAttributeType(final QName p0);
    
    SchemaGlobalElement findElement(final QName p0);
    
    SchemaGlobalAttribute findAttribute(final QName p0);
    
    SchemaModelGroup findModelGroup(final QName p0);
    
    SchemaAttributeGroup findAttributeGroup(final QName p0);
    
    boolean isNamespaceDefined(final String p0);
    
    SchemaType.Ref findTypeRef(final QName p0);
    
    SchemaType.Ref findDocumentTypeRef(final QName p0);
    
    SchemaType.Ref findAttributeTypeRef(final QName p0);
    
    SchemaGlobalElement.Ref findElementRef(final QName p0);
    
    SchemaGlobalAttribute.Ref findAttributeRef(final QName p0);
    
    SchemaModelGroup.Ref findModelGroupRef(final QName p0);
    
    SchemaAttributeGroup.Ref findAttributeGroupRef(final QName p0);
    
    SchemaIdentityConstraint.Ref findIdentityConstraintRef(final QName p0);
    
    SchemaType typeForSignature(final String p0);
    
    SchemaType typeForClassname(final String p0);
    
    InputStream getSourceAsStream(final String p0);
    
    String compilePath(final String p0, final XmlOptions p1) throws XmlException;
    
    String compileQuery(final String p0, final XmlOptions p1) throws XmlException;
    
    XmlObject newInstance(final SchemaType p0, final XmlOptions p1);
    
    XmlObject parse(final String p0, final SchemaType p1, final XmlOptions p2) throws XmlException;
    
    XmlObject parse(final File p0, final SchemaType p1, final XmlOptions p2) throws XmlException, IOException;
    
    XmlObject parse(final URL p0, final SchemaType p1, final XmlOptions p2) throws XmlException, IOException;
    
    XmlObject parse(final InputStream p0, final SchemaType p1, final XmlOptions p2) throws XmlException, IOException;
    
    XmlObject parse(final XMLStreamReader p0, final SchemaType p1, final XmlOptions p2) throws XmlException;
    
    XmlObject parse(final Reader p0, final SchemaType p1, final XmlOptions p2) throws XmlException, IOException;
    
    XmlObject parse(final Node p0, final SchemaType p1, final XmlOptions p2) throws XmlException;
    
    @Deprecated
    XmlObject parse(final XMLInputStream p0, final SchemaType p1, final XmlOptions p2) throws XmlException, XMLStreamException;
    
    XmlSaxHandler newXmlSaxHandler(final SchemaType p0, final XmlOptions p1);
    
    DOMImplementation newDomImplementation(final XmlOptions p0);
    
    @Deprecated
    XMLInputStream newValidatingXMLInputStream(final XMLInputStream p0, final SchemaType p1, final XmlOptions p2) throws XmlException, XMLStreamException;
}
