package org.apache.axiom.om.impl;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.attachments.impl.BufferUtils;
import javax.activation.DataHandler;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.commons.logging.Log;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;

class OptimizationPolicyImpl implements OptimizationPolicy
{
    private static final Log log;
    private final OMOutputFormat format;
    private static final int UNSUPPORTED = -1;
    private static final int EXCEED_LIMIT = 1;
    
    public OptimizationPolicyImpl(final OMOutputFormat format) {
        this.format = format;
    }
    
    public boolean isOptimized(final DataHandler dataHandler, final boolean optimize) {
        if (!optimize) {
            return false;
        }
        OptimizationPolicyImpl.log.debug((Object)"Start MTOMXMLStreamWriter.isOptimizedThreshold()");
        int optimized = -1;
        if (dataHandler != null) {
            OptimizationPolicyImpl.log.debug((Object)"DataHandler fetched, starting optimized Threshold processing");
            optimized = BufferUtils.doesDataHandlerExceedLimit(dataHandler, this.format.getOptimizedThreshold());
        }
        if (optimized == -1 || optimized == 1) {
            OptimizationPolicyImpl.log.debug((Object)"node should be added to binart NodeList for optimization");
            return true;
        }
        return false;
    }
    
    public boolean isOptimized(final DataHandlerProvider dataHandlerProvider, final boolean optimize) throws IOException {
        return optimize && (this.format.getOptimizedThreshold() == 0 || this.isOptimized(dataHandlerProvider.getDataHandler(), optimize));
    }
    
    static {
        log = LogFactory.getLog((Class)OptimizationPolicyImpl.class);
    }
}
