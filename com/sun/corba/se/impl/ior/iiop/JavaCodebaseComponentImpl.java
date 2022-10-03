package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class JavaCodebaseComponentImpl extends TaggedComponentBase implements JavaCodebaseComponent
{
    private String URLs;
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof JavaCodebaseComponentImpl && this.URLs.equals(((JavaCodebaseComponentImpl)o).getURLs());
    }
    
    @Override
    public int hashCode() {
        return this.URLs.hashCode();
    }
    
    @Override
    public String toString() {
        return "JavaCodebaseComponentImpl[URLs=" + this.URLs + "]";
    }
    
    @Override
    public String getURLs() {
        return this.URLs;
    }
    
    public JavaCodebaseComponentImpl(final String urLs) {
        this.URLs = urLs;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        outputStream.write_string(this.URLs);
    }
    
    @Override
    public int getId() {
        return 25;
    }
}
