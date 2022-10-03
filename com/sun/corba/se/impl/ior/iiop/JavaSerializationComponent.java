package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class JavaSerializationComponent extends TaggedComponentBase
{
    private byte version;
    private static JavaSerializationComponent singleton;
    
    public static JavaSerializationComponent singleton() {
        if (JavaSerializationComponent.singleton == null) {
            synchronized (JavaSerializationComponent.class) {
                JavaSerializationComponent.singleton = new JavaSerializationComponent((byte)1);
            }
        }
        return JavaSerializationComponent.singleton;
    }
    
    public JavaSerializationComponent(final byte version) {
        this.version = version;
    }
    
    public byte javaSerializationVersion() {
        return this.version;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        outputStream.write_octet(this.version);
    }
    
    @Override
    public int getId() {
        return 1398099458;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof JavaSerializationComponent && this.version == ((JavaSerializationComponent)o).version;
    }
    
    @Override
    public int hashCode() {
        return this.version;
    }
}
