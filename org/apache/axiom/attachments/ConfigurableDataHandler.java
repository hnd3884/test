package org.apache.axiom.attachments;

import java.net.URL;
import javax.activation.DataSource;
import javax.activation.DataHandler;

public class ConfigurableDataHandler extends DataHandler
{
    private String transferEncoding;
    private String contentType;
    private String contentID;
    
    public ConfigurableDataHandler(final DataSource ds) {
        super(ds);
    }
    
    public ConfigurableDataHandler(final Object data, final String type) {
        super(data, type);
    }
    
    public ConfigurableDataHandler(final URL url) {
        super(url);
    }
    
    @Override
    public String getContentType() {
        if (this.contentType != null) {
            return this.contentType;
        }
        return super.getContentType();
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
    
    public String getTransferEncoding() {
        return this.transferEncoding;
    }
    
    public void setTransferEncoding(final String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }
}
