package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.ext.stax.DTDReader;

final class NullDTDReader implements DTDReader
{
    static final NullDTDReader INSTANCE;
    
    static {
        INSTANCE = new NullDTDReader();
    }
    
    private NullDTDReader() {
    }
    
    public String getRootName() {
        throw new UnsupportedOperationException();
    }
    
    public String getPublicId() {
        throw new UnsupportedOperationException();
    }
    
    public String getSystemId() {
        throw new UnsupportedOperationException();
    }
}
