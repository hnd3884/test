package org.apache.axiom.om.impl;

import org.apache.axiom.attachments.ConfigurableDataHandler;
import javax.activation.DataHandler;
import java.util.List;
import java.io.IOException;
import org.apache.axiom.om.util.CommonUtils;
import java.io.OutputStream;
import org.apache.axiom.mime.MultipartWriter;
import org.apache.axiom.om.OMOutputFormat;

public class OMMultipartWriter
{
    private final OMOutputFormat format;
    private final MultipartWriter writer;
    private final boolean useCTEBase64;
    private final String rootPartContentType;
    
    public OMMultipartWriter(final OutputStream out, final OMOutputFormat format) {
        this.format = format;
        this.writer = format.getMultipartWriterFactory().createMultipartWriter(out, format.getMimeBoundary());
        this.useCTEBase64 = (format != null && Boolean.TRUE.equals(format.getProperty("org.apache.axiom.om.OMFormat.use.cteBase64.forNonTextualAttachments")));
        String soapContentType;
        if (format.isSOAP11()) {
            soapContentType = "text/xml";
        }
        else {
            soapContentType = "application/soap+xml";
        }
        if (format.isOptimized()) {
            this.rootPartContentType = "application/xop+xml; charset=" + format.getCharSetEncoding() + "; type=\"" + soapContentType + "\"";
        }
        else {
            this.rootPartContentType = soapContentType + "; charset=" + format.getCharSetEncoding();
        }
    }
    
    private String getContentTransferEncoding(final String contentType) {
        if (this.useCTEBase64 && !CommonUtils.isTextualPart(contentType)) {
            return "base64";
        }
        return "binary";
    }
    
    public String getRootPartContentType() {
        return this.rootPartContentType;
    }
    
    public OutputStream writeRootPart() throws IOException {
        return this.writer.writePart(this.rootPartContentType, "binary", this.format.getRootContentId());
    }
    
    public OutputStream writePart(final String contentType, final String contentID) throws IOException {
        return this.writer.writePart(contentType, this.getContentTransferEncoding(contentType), contentID);
    }
    
    public OutputStream writePart(final String contentType, final String contentID, final List extraHeaders) throws IOException {
        return this.writer.writePart(contentType, this.getContentTransferEncoding(contentType), contentID, extraHeaders);
    }
    
    public void writePart(final DataHandler dataHandler, final String contentID, final List extraHeaders) throws IOException {
        String contentTransferEncoding = null;
        if (dataHandler instanceof ConfigurableDataHandler) {
            contentTransferEncoding = ((ConfigurableDataHandler)dataHandler).getTransferEncoding();
        }
        if (contentTransferEncoding == null) {
            contentTransferEncoding = this.getContentTransferEncoding(dataHandler.getContentType());
        }
        this.writer.writePart(dataHandler, contentTransferEncoding, contentID, extraHeaders);
    }
    
    public void writePart(final DataHandler dataHandler, final String contentID) throws IOException {
        this.writePart(dataHandler, contentID, null);
    }
    
    public void complete() throws IOException {
        this.writer.complete();
    }
}
