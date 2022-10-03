package org.apache.poi.openxml4j.opc.internal.marshallers;

import java.util.Optional;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.poi.ooxml.util.DocumentHelper;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.w3c.dom.Document;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;

public class PackagePropertiesMarshaller implements PartMarshaller
{
    private static final NamespaceImpl namespaceDC;
    private static final NamespaceImpl namespaceCoreProperties;
    private static final NamespaceImpl namespaceDcTerms;
    private static final NamespaceImpl namespaceXSI;
    protected static final String KEYWORD_CATEGORY = "category";
    protected static final String KEYWORD_CONTENT_STATUS = "contentStatus";
    protected static final String KEYWORD_CONTENT_TYPE = "contentType";
    protected static final String KEYWORD_CREATED = "created";
    protected static final String KEYWORD_CREATOR = "creator";
    protected static final String KEYWORD_DESCRIPTION = "description";
    protected static final String KEYWORD_IDENTIFIER = "identifier";
    protected static final String KEYWORD_KEYWORDS = "keywords";
    protected static final String KEYWORD_LANGUAGE = "language";
    protected static final String KEYWORD_LAST_MODIFIED_BY = "lastModifiedBy";
    protected static final String KEYWORD_LAST_PRINTED = "lastPrinted";
    protected static final String KEYWORD_MODIFIED = "modified";
    protected static final String KEYWORD_REVISION = "revision";
    protected static final String KEYWORD_SUBJECT = "subject";
    protected static final String KEYWORD_TITLE = "title";
    protected static final String KEYWORD_VERSION = "version";
    PackagePropertiesPart propsPart;
    Document xmlDoc;
    
    @Override
    public boolean marshall(final PackagePart part, final OutputStream out) throws OpenXML4JException {
        if (!(part instanceof PackagePropertiesPart)) {
            throw new IllegalArgumentException("'part' must be a PackagePropertiesPart instance.");
        }
        this.propsPart = (PackagePropertiesPart)part;
        this.xmlDoc = DocumentHelper.createDocument();
        final Element rootElem = this.xmlDoc.createElementNS(PackagePropertiesMarshaller.namespaceCoreProperties.getNamespaceURI(), this.getQName("coreProperties", PackagePropertiesMarshaller.namespaceCoreProperties));
        DocumentHelper.addNamespaceDeclaration(rootElem, PackagePropertiesMarshaller.namespaceCoreProperties.getPrefix(), PackagePropertiesMarshaller.namespaceCoreProperties.getNamespaceURI());
        DocumentHelper.addNamespaceDeclaration(rootElem, PackagePropertiesMarshaller.namespaceDC.getPrefix(), PackagePropertiesMarshaller.namespaceDC.getNamespaceURI());
        DocumentHelper.addNamespaceDeclaration(rootElem, PackagePropertiesMarshaller.namespaceDcTerms.getPrefix(), PackagePropertiesMarshaller.namespaceDcTerms.getNamespaceURI());
        DocumentHelper.addNamespaceDeclaration(rootElem, PackagePropertiesMarshaller.namespaceXSI.getPrefix(), PackagePropertiesMarshaller.namespaceXSI.getNamespaceURI());
        this.xmlDoc.appendChild(rootElem);
        this.addCategory();
        this.addContentStatus();
        this.addContentType();
        this.addCreated();
        this.addCreator();
        this.addDescription();
        this.addIdentifier();
        this.addKeywords();
        this.addLanguage();
        this.addLastModifiedBy();
        this.addLastPrinted();
        this.addModified();
        this.addRevision();
        this.addSubject();
        this.addTitle();
        this.addVersion();
        return true;
    }
    
    private Element setElementTextContent(final String localName, final NamespaceImpl namespace, final Optional<String> property) {
        return this.setElementTextContent(localName, namespace, property, property.orElse(null));
    }
    
    private String getQName(final String localName, final NamespaceImpl namespace) {
        return namespace.getPrefix().isEmpty() ? localName : (namespace.getPrefix() + ':' + localName);
    }
    
    private Element setElementTextContent(final String localName, final NamespaceImpl namespace, final Optional<?> property, final String propertyValue) {
        if (!property.isPresent()) {
            return null;
        }
        final Element root = this.xmlDoc.getDocumentElement();
        Element elem = (Element)root.getElementsByTagNameNS(namespace.getNamespaceURI(), localName).item(0);
        if (elem == null) {
            elem = this.xmlDoc.createElementNS(namespace.getNamespaceURI(), this.getQName(localName, namespace));
            root.appendChild(elem);
        }
        elem.setTextContent(propertyValue);
        return elem;
    }
    
    private Element setElementTextContent(final String localName, final NamespaceImpl namespace, final Optional<?> property, final String propertyValue, final String xsiType) {
        final Element element = this.setElementTextContent(localName, namespace, property, propertyValue);
        if (element != null) {
            element.setAttributeNS(PackagePropertiesMarshaller.namespaceXSI.getNamespaceURI(), this.getQName("type", PackagePropertiesMarshaller.namespaceXSI), xsiType);
        }
        return element;
    }
    
    private void addCategory() {
        this.setElementTextContent("category", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getCategoryProperty());
    }
    
    private void addContentStatus() {
        this.setElementTextContent("contentStatus", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getContentStatusProperty());
    }
    
    private void addContentType() {
        this.setElementTextContent("contentType", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getContentTypeProperty());
    }
    
    private void addCreated() {
        this.setElementTextContent("created", PackagePropertiesMarshaller.namespaceDcTerms, this.propsPart.getCreatedProperty(), this.propsPart.getCreatedPropertyString(), "dcterms:W3CDTF");
    }
    
    private void addCreator() {
        this.setElementTextContent("creator", PackagePropertiesMarshaller.namespaceDC, this.propsPart.getCreatorProperty());
    }
    
    private void addDescription() {
        this.setElementTextContent("description", PackagePropertiesMarshaller.namespaceDC, this.propsPart.getDescriptionProperty());
    }
    
    private void addIdentifier() {
        this.setElementTextContent("identifier", PackagePropertiesMarshaller.namespaceDC, this.propsPart.getIdentifierProperty());
    }
    
    private void addKeywords() {
        this.setElementTextContent("keywords", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getKeywordsProperty());
    }
    
    private void addLanguage() {
        this.setElementTextContent("language", PackagePropertiesMarshaller.namespaceDC, this.propsPart.getLanguageProperty());
    }
    
    private void addLastModifiedBy() {
        this.setElementTextContent("lastModifiedBy", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getLastModifiedByProperty());
    }
    
    private void addLastPrinted() {
        this.setElementTextContent("lastPrinted", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getLastPrintedProperty(), this.propsPart.getLastPrintedPropertyString());
    }
    
    private void addModified() {
        this.setElementTextContent("modified", PackagePropertiesMarshaller.namespaceDcTerms, this.propsPart.getModifiedProperty(), this.propsPart.getModifiedPropertyString(), "dcterms:W3CDTF");
    }
    
    private void addRevision() {
        this.setElementTextContent("revision", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getRevisionProperty());
    }
    
    private void addSubject() {
        this.setElementTextContent("subject", PackagePropertiesMarshaller.namespaceDC, this.propsPart.getSubjectProperty());
    }
    
    private void addTitle() {
        this.setElementTextContent("title", PackagePropertiesMarshaller.namespaceDC, this.propsPart.getTitleProperty());
    }
    
    private void addVersion() {
        this.setElementTextContent("version", PackagePropertiesMarshaller.namespaceCoreProperties, this.propsPart.getVersionProperty());
    }
    
    static {
        namespaceDC = new NamespaceImpl("dc", "http://purl.org/dc/elements/1.1/");
        namespaceCoreProperties = new NamespaceImpl("cp", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
        namespaceDcTerms = new NamespaceImpl("dcterms", "http://purl.org/dc/terms/");
        namespaceXSI = new NamespaceImpl("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }
    
    private static class NamespaceImpl
    {
        private final String prefix;
        private final String namespaceURI;
        
        NamespaceImpl(final String prefix, final String namespaceURI) {
            this.prefix = prefix;
            this.namespaceURI = namespaceURI;
        }
        
        public String getPrefix() {
            return this.prefix;
        }
        
        public String getNamespaceURI() {
            return this.namespaceURI;
        }
    }
}
