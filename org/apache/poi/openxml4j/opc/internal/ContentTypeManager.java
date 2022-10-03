package org.apache.poi.openxml4j.opc.internal;

import java.util.Map;
import org.w3c.dom.Node;
import java.io.OutputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.net.URI;
import org.w3c.dom.Element;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Locale;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePartName;
import java.util.TreeMap;
import org.apache.poi.openxml4j.opc.OPCPackage;

public abstract class ContentTypeManager
{
    public static final String CONTENT_TYPES_PART_NAME = "[Content_Types].xml";
    public static final String TYPES_NAMESPACE_URI = "http://schemas.openxmlformats.org/package/2006/content-types";
    private static final String TYPES_TAG_NAME = "Types";
    private static final String DEFAULT_TAG_NAME = "Default";
    private static final String EXTENSION_ATTRIBUTE_NAME = "Extension";
    private static final String CONTENT_TYPE_ATTRIBUTE_NAME = "ContentType";
    private static final String OVERRIDE_TAG_NAME = "Override";
    private static final String PART_NAME_ATTRIBUTE_NAME = "PartName";
    protected OPCPackage container;
    private TreeMap<String, String> defaultContentType;
    private TreeMap<PackagePartName, String> overrideContentType;
    
    public ContentTypeManager(final InputStream in, final OPCPackage pkg) throws InvalidFormatException {
        this.container = pkg;
        this.defaultContentType = new TreeMap<String, String>();
        if (in != null) {
            try {
                this.parseContentTypesFile(in);
            }
            catch (final InvalidFormatException e) {
                final InvalidFormatException ex = new InvalidFormatException("Can't read content types part !");
                ex.initCause(e);
                throw ex;
            }
        }
    }
    
    public void addContentType(final PackagePartName partName, final String contentType) {
        final boolean defaultCTExists = this.defaultContentType.containsValue(contentType);
        final String extension = partName.getExtension().toLowerCase(Locale.ROOT);
        if (extension.length() == 0 || (this.defaultContentType.containsKey(extension) && !defaultCTExists) || (!this.defaultContentType.containsKey(extension) && defaultCTExists)) {
            this.addOverrideContentType(partName, contentType);
        }
        else if (!defaultCTExists) {
            this.addDefaultContentType(extension, contentType);
        }
    }
    
    private void addOverrideContentType(final PackagePartName partName, final String contentType) {
        if (this.overrideContentType == null) {
            this.overrideContentType = new TreeMap<PackagePartName, String>();
        }
        this.overrideContentType.put(partName, contentType);
    }
    
    private void addDefaultContentType(final String extension, final String contentType) {
        this.defaultContentType.put(extension.toLowerCase(Locale.ROOT), contentType);
    }
    
    public void removeContentType(final PackagePartName partName) throws InvalidOperationException {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (this.overrideContentType != null && this.overrideContentType.get(partName) != null) {
            this.overrideContentType.remove(partName);
            return;
        }
        final String extensionToDelete = partName.getExtension();
        boolean deleteDefaultContentTypeFlag = true;
        if (this.container != null) {
            try {
                for (final PackagePart part : this.container.getParts()) {
                    if (!part.getPartName().equals(partName) && part.getPartName().getExtension().equalsIgnoreCase(extensionToDelete)) {
                        deleteDefaultContentTypeFlag = false;
                        break;
                    }
                }
            }
            catch (final InvalidFormatException e) {
                throw new InvalidOperationException(e.getMessage());
            }
        }
        if (deleteDefaultContentTypeFlag) {
            this.defaultContentType.remove(extensionToDelete);
        }
        if (this.container != null) {
            try {
                for (final PackagePart part : this.container.getParts()) {
                    if (!part.getPartName().equals(partName) && this.getContentType(part.getPartName()) == null) {
                        throw new InvalidOperationException("Rule M2.4 is not respected: Nor a default element or override element is associated with the part: " + part.getPartName().getName());
                    }
                }
            }
            catch (final InvalidFormatException e) {
                throw new InvalidOperationException(e.getMessage());
            }
        }
    }
    
    public boolean isContentTypeRegister(final String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType");
        }
        return this.defaultContentType.values().contains(contentType) || (this.overrideContentType != null && this.overrideContentType.values().contains(contentType));
    }
    
    public String getContentType(final PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (this.overrideContentType != null && this.overrideContentType.containsKey(partName)) {
            return this.overrideContentType.get(partName);
        }
        final String extension = partName.getExtension().toLowerCase(Locale.ROOT);
        if (this.defaultContentType.containsKey(extension)) {
            return this.defaultContentType.get(extension);
        }
        if (this.container != null && this.container.getPart(partName) != null) {
            throw new OpenXML4JRuntimeException("Rule M2.4 exception : Part '" + partName + "' not found - this error should NEVER happen!\nCheck that your code is closing the open resources in the correct order prior to filing a bug report.\nIf you can provide the triggering file, then please raise a bug at https://bz.apache.org/bugzilla/enter_bug.cgi?product=POI and attach the file that triggers it, thanks!");
        }
        return null;
    }
    
    public void clearAll() {
        this.defaultContentType.clear();
        if (this.overrideContentType != null) {
            this.overrideContentType.clear();
        }
    }
    
    public void clearOverrideContentTypes() {
        if (this.overrideContentType != null) {
            this.overrideContentType.clear();
        }
    }
    
    private void parseContentTypesFile(final InputStream in) throws InvalidFormatException {
        try {
            final Document xmlContentTypetDoc = DocumentHelper.readDocument(in);
            final NodeList defaultTypes = xmlContentTypetDoc.getDocumentElement().getElementsByTagNameNS("http://schemas.openxmlformats.org/package/2006/content-types", "Default");
            for (int defaultTypeCount = defaultTypes.getLength(), i = 0; i < defaultTypeCount; ++i) {
                final Element element = (Element)defaultTypes.item(i);
                final String extension = element.getAttribute("Extension");
                final String contentType = element.getAttribute("ContentType");
                this.addDefaultContentType(extension, contentType);
            }
            final NodeList overrideTypes = xmlContentTypetDoc.getDocumentElement().getElementsByTagNameNS("http://schemas.openxmlformats.org/package/2006/content-types", "Override");
            for (int overrideTypeCount = overrideTypes.getLength(), j = 0; j < overrideTypeCount; ++j) {
                final Element element2 = (Element)overrideTypes.item(j);
                final URI uri = new URI(element2.getAttribute("PartName"));
                final PackagePartName partName = PackagingURIHelper.createPartName(uri);
                final String contentType2 = element2.getAttribute("ContentType");
                this.addOverrideContentType(partName, contentType2);
            }
        }
        catch (final URISyntaxException | IOException | SAXException e) {
            throw new InvalidFormatException(e.getMessage());
        }
    }
    
    public boolean save(final OutputStream outStream) {
        final Document xmlOutDoc = DocumentHelper.createDocument();
        final Element typesElem = xmlOutDoc.createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", "Types");
        xmlOutDoc.appendChild(typesElem);
        for (final Map.Entry<String, String> entry : this.defaultContentType.entrySet()) {
            this.appendDefaultType(typesElem, entry);
        }
        if (this.overrideContentType != null) {
            for (final Map.Entry<PackagePartName, String> entry2 : this.overrideContentType.entrySet()) {
                this.appendSpecificTypes(typesElem, entry2);
            }
        }
        xmlOutDoc.normalize();
        return this.saveImpl(xmlOutDoc, outStream);
    }
    
    private void appendSpecificTypes(final Element root, final Map.Entry<PackagePartName, String> entry) {
        final Element specificType = root.getOwnerDocument().createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", "Override");
        specificType.setAttribute("PartName", entry.getKey().getName());
        specificType.setAttribute("ContentType", entry.getValue());
        root.appendChild(specificType);
    }
    
    private void appendDefaultType(final Element root, final Map.Entry<String, String> entry) {
        final Element defaultType = root.getOwnerDocument().createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", "Default");
        defaultType.setAttribute("Extension", entry.getKey());
        defaultType.setAttribute("ContentType", entry.getValue());
        root.appendChild(defaultType);
    }
    
    public abstract boolean saveImpl(final Document p0, final OutputStream p1);
}
