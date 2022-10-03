package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.core.ClonePolicy;
import javax.activation.DataSource;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.util.base64.Base64Utils;
import java.io.IOException;
import org.apache.axiom.om.OMException;
import javax.activation.DataHandler;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.core.CharacterData;

public final class TextContent implements CharacterData
{
    private final String value;
    private final String mimeType;
    private String contentID;
    private Object dataHandlerObject;
    private boolean optimize;
    private boolean binary;
    
    public TextContent(final String value) {
        this.value = value;
        this.mimeType = null;
    }
    
    public TextContent(final String value, final String mimeType, final boolean optimize) {
        this.value = value;
        this.mimeType = mimeType;
        this.binary = true;
        this.optimize = optimize;
    }
    
    public TextContent(final Object dataHandlerObject, final boolean optimize) {
        this.value = null;
        this.mimeType = null;
        this.dataHandlerObject = dataHandlerObject;
        this.binary = true;
        this.optimize = optimize;
    }
    
    public TextContent(final String contentID, final DataHandlerProvider dataHandlerProvider, final boolean optimize) {
        this.value = null;
        this.mimeType = null;
        this.contentID = contentID;
        this.dataHandlerObject = dataHandlerProvider;
        this.binary = true;
        this.optimize = optimize;
    }
    
    private TextContent(final TextContent other) {
        this.value = other.value;
        this.mimeType = other.mimeType;
        this.contentID = other.contentID;
        this.dataHandlerObject = other.dataHandlerObject;
        this.optimize = other.optimize;
        this.binary = other.binary;
    }
    
    public boolean isOptimize() {
        return this.optimize;
    }
    
    public void setOptimize(final boolean optimize) {
        this.optimize = optimize;
        if (optimize) {
            this.binary = true;
        }
    }
    
    public boolean isBinary() {
        return this.binary;
    }
    
    public void setBinary(final boolean binary) {
        this.binary = binary;
    }
    
    public String getContentID() {
        if (this.contentID == null) {
            this.contentID = UIDGenerator.generateContentId();
        }
        return this.contentID;
    }
    
    public void setContentID(final String contentID) {
        this.contentID = contentID;
    }
    
    public Object getDataHandlerObject() {
        return this.dataHandlerObject;
    }
    
    public DataHandler getDataHandler() {
        if (this.dataHandlerObject != null) {
            if (this.dataHandlerObject instanceof DataHandlerProvider) {
                try {
                    this.dataHandlerObject = ((DataHandlerProvider)this.dataHandlerObject).getDataHandler();
                }
                catch (final IOException ex) {
                    throw new OMException((Throwable)ex);
                }
            }
            return (DataHandler)this.dataHandlerObject;
        }
        if (this.binary) {
            return new DataHandler((DataSource)new ByteArrayDataSource(Base64Utils.decode(this.value), this.mimeType));
        }
        throw new OMException("No DataHandler available");
    }
    
    @Override
    public String toString() {
        if (this.dataHandlerObject != null) {
            try {
                return Base64Utils.encode(this.getDataHandler());
            }
            catch (final Exception e) {
                throw new OMException((Throwable)e);
            }
        }
        return this.value;
    }
    
    public char[] toCharArray() {
        if (this.dataHandlerObject != null) {
            try {
                return Base64Utils.encodeToCharArray(this.getDataHandler());
            }
            catch (final IOException ex) {
                throw new OMException((Throwable)ex);
            }
        }
        return this.value.toCharArray();
    }
    
    public <T> CharacterData clone(final ClonePolicy<T> policy, final T options) {
        if (this.binary && options instanceof OMCloneOptions && ((OMCloneOptions)options).isFetchDataHandlers()) {
            this.getDataHandler().getDataSource();
        }
        return new TextContent(this);
    }
}
