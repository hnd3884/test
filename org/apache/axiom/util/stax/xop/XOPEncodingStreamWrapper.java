package org.apache.axiom.util.stax.xop;

import java.util.Collections;
import java.util.Set;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import javax.activation.DataHandler;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class XOPEncodingStreamWrapper implements MimePartProvider
{
    private final Map dataHandlerObjects;
    private final ContentIDGenerator contentIDGenerator;
    private final OptimizationPolicy optimizationPolicy;
    
    public XOPEncodingStreamWrapper(final ContentIDGenerator contentIDGenerator, final OptimizationPolicy optimizationPolicy) {
        this.dataHandlerObjects = new LinkedHashMap();
        this.contentIDGenerator = contentIDGenerator;
        this.optimizationPolicy = optimizationPolicy;
    }
    
    private String addDataHandler(final Object dataHandlerObject, final String existingContentID) {
        final String contentID = this.contentIDGenerator.generateContentID(existingContentID);
        this.dataHandlerObjects.put(contentID, dataHandlerObject);
        return contentID;
    }
    
    protected String processDataHandler(final DataHandler dataHandler, final String existingContentID, final boolean optimize) throws IOException {
        if (this.optimizationPolicy.isOptimized(dataHandler, optimize)) {
            return this.addDataHandler(dataHandler, existingContentID);
        }
        return null;
    }
    
    protected String processDataHandler(final DataHandlerProvider dataHandlerProvider, final String existingContentID, final boolean optimize) throws IOException {
        if (this.optimizationPolicy.isOptimized(dataHandlerProvider, optimize)) {
            return this.addDataHandler(dataHandlerProvider, existingContentID);
        }
        return null;
    }
    
    public Set getContentIDs() {
        return Collections.unmodifiableSet(this.dataHandlerObjects.keySet());
    }
    
    public boolean isLoaded(final String contentID) {
        final Object dataHandlerObject = this.dataHandlerObjects.get(contentID);
        if (dataHandlerObject == null) {
            throw new IllegalArgumentException("No DataHandler object found for content ID '" + contentID + "'");
        }
        return dataHandlerObject instanceof DataHandler || ((DataHandlerProvider)dataHandlerObject).isLoaded();
    }
    
    public DataHandler getDataHandler(final String contentID) throws IOException {
        final Object dataHandlerObject = this.dataHandlerObjects.get(contentID);
        if (dataHandlerObject == null) {
            throw new IllegalArgumentException("No DataHandler object found for content ID '" + contentID + "'");
        }
        if (dataHandlerObject instanceof DataHandler) {
            return (DataHandler)dataHandlerObject;
        }
        return ((DataHandlerProvider)dataHandlerObject).getDataHandler();
    }
}
