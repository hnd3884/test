package org.apache.xerces.jaxp.datatype;

import java.io.ObjectStreamException;
import java.io.Serializable;

final class SerializedDuration implements Serializable
{
    private static final long serialVersionUID = 3897193592341225793L;
    private final String lexicalValue;
    
    public SerializedDuration(final String lexicalValue) {
        this.lexicalValue = lexicalValue;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return new DatatypeFactoryImpl().newDuration(this.lexicalValue);
    }
}
