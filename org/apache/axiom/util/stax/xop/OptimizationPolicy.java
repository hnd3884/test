package org.apache.axiom.util.stax.xop;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import java.io.IOException;
import javax.activation.DataHandler;

public interface OptimizationPolicy
{
    public static final OptimizationPolicy DEFAULT = new OptimizationPolicy() {
        public boolean isOptimized(final DataHandler dataHandler, final boolean optimize) {
            return optimize;
        }
        
        public boolean isOptimized(final DataHandlerProvider dataHandlerProvider, final boolean optimize) {
            return optimize;
        }
    };
    public static final OptimizationPolicy ALL = new OptimizationPolicy() {
        public boolean isOptimized(final DataHandler dataHandler, final boolean optimize) {
            return true;
        }
        
        public boolean isOptimized(final DataHandlerProvider dataHandlerProvider, final boolean optimize) {
            return true;
        }
    };
    
    boolean isOptimized(final DataHandler p0, final boolean p1) throws IOException;
    
    boolean isOptimized(final DataHandlerProvider p0, final boolean p1) throws IOException;
}
