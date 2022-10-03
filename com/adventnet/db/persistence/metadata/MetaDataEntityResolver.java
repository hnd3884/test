package com.adventnet.db.persistence.metadata;

import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import org.xml.sax.InputSource;
import java.util.logging.Logger;
import org.xml.sax.ext.EntityResolver2;

public class MetaDataEntityResolver implements EntityResolver2
{
    private static final Logger LOGGER;
    private boolean handlePublicIDs;
    String ddXMLDirectory;
    
    public MetaDataEntityResolver() {
        this.handlePublicIDs = false;
        this.ddXMLDirectory = null;
    }
    
    public void handlePublicID(final boolean handle) {
        this.handlePublicIDs = handle;
    }
    
    public void setDDXMLDir(final String dir) {
        this.ddXMLDirectory = dir;
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        MetaDataEntityResolver.LOGGER.log(Level.INFO, "Entered resolveEntity :: publicId :: [{0}], systemId :: [{1}]", new Object[] { publicId, systemId });
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public InputSource getExternalSubset(final String name, final String baseURI) throws SAXException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public InputSource resolveEntity(final String name, final String publicId, final String baseURI, final String systemId) throws SAXException, IOException {
        MetaDataEntityResolver.LOGGER.log(Level.INFO, "Entered resolveEntity2 :: systemId :: [{0}], publicId :: [{1}], name :: [{2}], baseURI :: [{3}]", new Object[] { systemId, publicId, name, baseURI });
        boolean defaultDTD = false;
        if (publicId != null) {
            if (!this.handlePublicIDs) {
                throw new SAXException("The DOCTYPE of a data-dictionary.xml should contain only systemId as \"data-dictionary.dtd\"");
            }
            defaultDTD = true;
        }
        if (systemId == null || systemId.indexOf("http:") >= 0) {
            throw new SAXException("SystemID cannot be [null] or an URL :: [" + systemId + "]");
        }
        BufferedInputStream bis = null;
        if (defaultDTD || "data-dictionary.dtd".equals(systemId)) {
            bis = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("dtd/data-dictionary.dtd"));
        }
        else {
            final File file = new File(URLDecoder.decode(this.ddXMLDirectory + File.separator + systemId, "UTF-8"));
            MetaDataEntityResolver.LOGGER.log(Level.INFO, "Resolved systemId File is :: [{0}]", file);
            bis = new BufferedInputStream(new FileInputStream(file));
        }
        return new InputSource(bis);
    }
    
    static {
        LOGGER = Logger.getLogger(MetaDataEntityResolver.class.getName());
    }
}
