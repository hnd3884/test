package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import javax.xml.crypto.OctetStreamData;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import javax.xml.crypto.URIReferenceException;
import java.net.URI;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.URIReference;
import org.apache.poi.util.POILogger;
import javax.xml.crypto.URIDereferencer;

public class OOXMLURIDereferencer implements URIDereferencer, SignatureConfig.SignatureConfigurable
{
    private static final POILogger LOG;
    private SignatureConfig signatureConfig;
    private URIDereferencer baseUriDereferencer;
    
    @Override
    public void setSignatureConfig(final SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
    
    @Override
    public Data dereference(final URIReference uriReference, final XMLCryptoContext context) throws URIReferenceException {
        if (this.baseUriDereferencer == null) {
            this.baseUriDereferencer = this.signatureConfig.getSignatureFactory().getURIDereferencer();
        }
        if (null == uriReference) {
            throw new NullPointerException("URIReference cannot be null");
        }
        if (null == context) {
            throw new NullPointerException("XMLCryptoContext cannot be null");
        }
        URI uri;
        try {
            uri = new URI(uriReference.getURI());
        }
        catch (final URISyntaxException e) {
            throw new URIReferenceException("could not URL decode the uri: " + uriReference.getURI(), e);
        }
        final PackagePart part = this.findPart(uri);
        if (part == null) {
            OOXMLURIDereferencer.LOG.log(1, new Object[] { "cannot resolve, delegating to base DOM URI dereferencer", uri });
            return this.baseUriDereferencer.dereference(uriReference, context);
        }
        InputStream dataStream;
        try {
            dataStream = part.getInputStream();
            if (part.getPartName().toString().endsWith(".rels")) {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int ch;
                while ((ch = dataStream.read()) != -1) {
                    if (ch != 10) {
                        if (ch == 13) {
                            continue;
                        }
                        bos.write(ch);
                    }
                }
                dataStream = new ByteArrayInputStream(bos.toByteArray());
            }
        }
        catch (final IOException e2) {
            throw new URIReferenceException("I/O error: " + e2.getMessage(), e2);
        }
        return new OctetStreamData(dataStream, uri.toString(), null);
    }
    
    private PackagePart findPart(final URI uri) {
        OOXMLURIDereferencer.LOG.log(1, new Object[] { "dereference", uri });
        final String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            OOXMLURIDereferencer.LOG.log(1, new Object[] { "illegal part name (expected)", uri });
            return null;
        }
        PackagePartName ppn;
        try {
            ppn = PackagingURIHelper.createPartName(path);
        }
        catch (final InvalidFormatException e) {
            OOXMLURIDereferencer.LOG.log(5, new Object[] { "illegal part name (not expected)", uri });
            return null;
        }
        return this.signatureConfig.getOpcPackage().getPart(ppn);
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)OOXMLURIDereferencer.class);
    }
}
