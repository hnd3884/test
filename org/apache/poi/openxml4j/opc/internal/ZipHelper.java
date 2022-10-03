package org.apache.poi.openxml4j.opc.internal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import java.io.File;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.poi.openxml4j.util.ZipArchiveThresholdInputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.util.Internal;

@Internal
public final class ZipHelper
{
    private static final String FORWARD_SLASH = "/";
    
    private ZipHelper() {
    }
    
    public static ZipArchiveEntry getCorePropertiesZipEntry(final ZipPackage pkg) {
        final PackageRelationship corePropsRel = pkg.getRelationshipsByType("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties").getRelationship(0);
        if (corePropsRel == null) {
            return null;
        }
        return new ZipArchiveEntry(corePropsRel.getTargetURI().getPath());
    }
    
    public static String getOPCNameFromZipItemName(final String zipItemName) {
        if (zipItemName == null) {
            throw new IllegalArgumentException("zipItemName cannot be null");
        }
        if (zipItemName.startsWith("/")) {
            return zipItemName;
        }
        return "/" + zipItemName;
    }
    
    public static String getZipItemNameFromOPCName(final String opcItemName) {
        if (opcItemName == null) {
            throw new IllegalArgumentException("opcItemName cannot be null");
        }
        String retVal;
        for (retVal = opcItemName; retVal.startsWith("/"); retVal = retVal.substring(1)) {}
        return retVal;
    }
    
    public static URI getZipURIFromOPCName(final String opcItemName) {
        if (opcItemName == null) {
            throw new IllegalArgumentException("opcItemName");
        }
        String retVal;
        for (retVal = opcItemName; retVal.startsWith("/"); retVal = retVal.substring(1)) {}
        try {
            return new URI(retVal);
        }
        catch (final URISyntaxException e) {
            return null;
        }
    }
    
    private static void verifyZipHeader(final InputStream stream) throws NotOfficeXmlFileException, IOException {
        final InputStream is = FileMagic.prepareToCheckMagic(stream);
        final FileMagic fm = FileMagic.valueOf(is);
        switch (fm) {
            case OLE2: {
                throw new OLE2NotOfficeXmlFileException("The supplied data appears to be in the OLE2 Format. You are calling the part of POI that deals with OOXML (Office Open XML) Documents. You need to call a different part of POI to process this data (eg HSSF instead of XSSF)");
            }
            case XML: {
                throw new NotOfficeXmlFileException("The supplied data appears to be a raw XML file. Formats such as Office 2003 XML are not supported");
            }
            default: {}
        }
    }
    
    public static ZipArchiveThresholdInputStream openZipStream(final InputStream stream) throws IOException {
        final InputStream checkedStream = FileMagic.prepareToCheckMagic(stream);
        verifyZipHeader(checkedStream);
        return new ZipArchiveThresholdInputStream((InputStream)new ZipArchiveInputStream(checkedStream));
    }
    
    public static ZipSecureFile openZipFile(final File file) throws IOException, NotOfficeXmlFileException {
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist");
        }
        if (file.isDirectory()) {
            throw new IOException("File is a directory");
        }
        try (final FileInputStream input = new FileInputStream(file)) {
            verifyZipHeader(input);
        }
        return new ZipSecureFile(file);
    }
    
    public static ZipSecureFile openZipFile(final String path) throws IOException {
        return openZipFile(new File(path));
    }
}
