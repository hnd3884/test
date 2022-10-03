package com.adventnet.webclient.util;

import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import java.util.Properties;
import java.io.InputStream;
import org.xml.sax.EntityResolver;

public class EntityResolverImpl implements EntityResolver
{
    private InputStream dtdFileStream;
    private Properties dtdMappings;
    private String dtdFileName;
    
    public EntityResolverImpl(final Properties mappings) {
        this.dtdFileStream = null;
        this.dtdMappings = null;
        this.dtdFileName = null;
        this.dtdMappings = mappings;
    }
    
    public EntityResolverImpl(final InputStream dtdStream, final Properties mappings) {
        this(mappings);
        this.dtdFileStream = dtdStream;
    }
    
    public EntityResolverImpl(final String dtdFileName) {
        this.dtdFileStream = null;
        this.dtdMappings = null;
        this.dtdFileName = null;
        this.dtdFileName = dtdFileName;
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        BufferedInputStream bis = null;
        if (this.dtdFileName != null) {
            final FileInputStream dtdFileStream = new FileInputStream(this.dtdFileName);
            bis = new BufferedInputStream(dtdFileStream);
        }
        else if (this.dtdFileStream != null) {
            bis = new BufferedInputStream(this.dtdFileStream);
        }
        else {
            bis = new BufferedInputStream(this.getClass().getResourceAsStream(this.dtdMappings.getProperty(publicId)));
        }
        final InputSource inputSource = new InputSource(bis);
        return inputSource;
    }
}
