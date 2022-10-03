package org.apache.poi.openxml4j.opc.internal.unmarshallers;

import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.w3c.dom.Document;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.xml.sax.SAXException;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.internal.PartUnmarshaller;

public final class PackagePropertiesUnmarshaller implements PartUnmarshaller
{
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
    
    @Override
    public PackagePart unmarshall(final UnmarshallContext context, InputStream in) throws InvalidFormatException, IOException {
        final PackagePropertiesPart coreProps = new PackagePropertiesPart(context.getPackage(), context.getPartName());
        if (in == null) {
            if (context.getZipEntry() != null) {
                in = ((ZipPackage)context.getPackage()).getZipArchive().getInputStream(context.getZipEntry());
            }
            else {
                if (context.getPackage() == null) {
                    throw new IOException("Error while trying to get the part input stream.");
                }
                final ZipArchiveEntry zipEntry = ZipHelper.getCorePropertiesZipEntry((ZipPackage)context.getPackage());
                in = ((ZipPackage)context.getPackage()).getZipArchive().getInputStream(zipEntry);
            }
        }
        Document xmlDoc;
        try {
            xmlDoc = DocumentHelper.readDocument(in);
            this.checkElementForOPCCompliance(xmlDoc.getDocumentElement());
        }
        catch (final SAXException e) {
            throw new IOException(e.getMessage());
        }
        coreProps.setCategoryProperty(this.loadCategory(xmlDoc));
        coreProps.setContentStatusProperty(this.loadContentStatus(xmlDoc));
        coreProps.setContentTypeProperty(this.loadContentType(xmlDoc));
        coreProps.setCreatedProperty(this.loadCreated(xmlDoc));
        coreProps.setCreatorProperty(this.loadCreator(xmlDoc));
        coreProps.setDescriptionProperty(this.loadDescription(xmlDoc));
        coreProps.setIdentifierProperty(this.loadIdentifier(xmlDoc));
        coreProps.setKeywordsProperty(this.loadKeywords(xmlDoc));
        coreProps.setLanguageProperty(this.loadLanguage(xmlDoc));
        coreProps.setLastModifiedByProperty(this.loadLastModifiedBy(xmlDoc));
        coreProps.setLastPrintedProperty(this.loadLastPrinted(xmlDoc));
        coreProps.setModifiedProperty(this.loadModified(xmlDoc));
        coreProps.setRevisionProperty(this.loadRevision(xmlDoc));
        coreProps.setSubjectProperty(this.loadSubject(xmlDoc));
        coreProps.setTitleProperty(this.loadTitle(xmlDoc));
        coreProps.setVersionProperty(this.loadVersion(xmlDoc));
        return coreProps;
    }
    
    private String readElement(final Document xmlDoc, final String localName, final String namespaceURI) {
        final Element el = (Element)xmlDoc.getDocumentElement().getElementsByTagNameNS(namespaceURI, localName).item(0);
        if (el == null) {
            return null;
        }
        return el.getTextContent();
    }
    
    private String loadCategory(final Document xmlDoc) {
        return this.readElement(xmlDoc, "category", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadContentStatus(final Document xmlDoc) {
        return this.readElement(xmlDoc, "contentStatus", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadContentType(final Document xmlDoc) {
        return this.readElement(xmlDoc, "contentType", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadCreated(final Document xmlDoc) {
        return this.readElement(xmlDoc, "created", "http://purl.org/dc/terms/");
    }
    
    private String loadCreator(final Document xmlDoc) {
        return this.readElement(xmlDoc, "creator", "http://purl.org/dc/elements/1.1/");
    }
    
    private String loadDescription(final Document xmlDoc) {
        return this.readElement(xmlDoc, "description", "http://purl.org/dc/elements/1.1/");
    }
    
    private String loadIdentifier(final Document xmlDoc) {
        return this.readElement(xmlDoc, "identifier", "http://purl.org/dc/elements/1.1/");
    }
    
    private String loadKeywords(final Document xmlDoc) {
        return this.readElement(xmlDoc, "keywords", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadLanguage(final Document xmlDoc) {
        return this.readElement(xmlDoc, "language", "http://purl.org/dc/elements/1.1/");
    }
    
    private String loadLastModifiedBy(final Document xmlDoc) {
        return this.readElement(xmlDoc, "lastModifiedBy", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadLastPrinted(final Document xmlDoc) {
        return this.readElement(xmlDoc, "lastPrinted", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadModified(final Document xmlDoc) {
        return this.readElement(xmlDoc, "modified", "http://purl.org/dc/terms/");
    }
    
    private String loadRevision(final Document xmlDoc) {
        return this.readElement(xmlDoc, "revision", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    private String loadSubject(final Document xmlDoc) {
        return this.readElement(xmlDoc, "subject", "http://purl.org/dc/elements/1.1/");
    }
    
    private String loadTitle(final Document xmlDoc) {
        return this.readElement(xmlDoc, "title", "http://purl.org/dc/elements/1.1/");
    }
    
    private String loadVersion(final Document xmlDoc) {
        return this.readElement(xmlDoc, "version", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }
    
    public void checkElementForOPCCompliance(final Element el) throws InvalidFormatException {
        final NamedNodeMap namedNodeMap = el.getAttributes();
        for (int namedNodeCount = namedNodeMap.getLength(), i = 0; i < namedNodeCount; ++i) {
            final Attr attr = (Attr)namedNodeMap.item(0);
            if (attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") && attr.getValue().equals("http://schemas.openxmlformats.org/markup-compatibility/2006")) {
                throw new InvalidFormatException("OPC Compliance error [M4.2]: A format consumer shall consider the use of the Markup Compatibility namespace to be an error.");
            }
        }
        final String elName = el.getLocalName();
        if (el.getNamespaceURI().equals("http://purl.org/dc/terms/") && !elName.equals("created") && !elName.equals("modified")) {
            throw new InvalidFormatException("OPC Compliance error [M4.3]: Producers shall not create a document element that contains refinements to the Dublin Core elements, except for the two specified in the schema: <dcterms:created> and <dcterms:modified> Consumers shall consider a document element that violates this constraint to be an error.");
        }
        if (el.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "lang") != null) {
            throw new InvalidFormatException("OPC Compliance error [M4.4]: Producers shall not create a document element that contains the xml:lang attribute. Consumers shall consider a document element that violates this constraint to be an error.");
        }
        if (el.getNamespaceURI().equals("http://purl.org/dc/terms/")) {
            if (!elName.equals("created") && !elName.equals("modified")) {
                throw new InvalidFormatException("Namespace error : " + elName + " shouldn't have the following naemspace -> " + "http://purl.org/dc/terms/");
            }
            final Attr typeAtt = el.getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (typeAtt == null) {
                throw new InvalidFormatException("The element '" + elName + "' must have the 'xsi:type' attribute present !");
            }
            if (!typeAtt.getValue().equals(el.getPrefix() + ":W3CDTF")) {
                throw new InvalidFormatException("The element '" + elName + "' must have the 'xsi:type' attribute with the value '" + el.getPrefix() + ":W3CDTF', but had '" + typeAtt.getValue() + "' !");
            }
        }
        final NodeList childElements = el.getElementsByTagName("*");
        for (int childElementCount = childElements.getLength(), j = 0; j < childElementCount; ++j) {
            this.checkElementForOPCCompliance((Element)childElements.item(j));
        }
    }
}
