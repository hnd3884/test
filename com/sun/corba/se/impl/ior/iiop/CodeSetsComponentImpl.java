package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class CodeSetsComponentImpl extends TaggedComponentBase implements CodeSetsComponent
{
    CodeSetComponentInfo csci;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CodeSetsComponentImpl && this.csci.equals(((CodeSetsComponentImpl)o).csci);
    }
    
    @Override
    public int hashCode() {
        return this.csci.hashCode();
    }
    
    @Override
    public String toString() {
        return "CodeSetsComponentImpl[csci=" + this.csci + "]";
    }
    
    public CodeSetsComponentImpl() {
        this.csci = new CodeSetComponentInfo();
    }
    
    public CodeSetsComponentImpl(final InputStream inputStream) {
        (this.csci = new CodeSetComponentInfo()).read((MarshalInputStream)inputStream);
    }
    
    public CodeSetsComponentImpl(final ORB orb) {
        if (orb == null) {
            this.csci = new CodeSetComponentInfo();
        }
        else {
            this.csci = orb.getORBData().getCodeSetComponentInfo();
        }
    }
    
    @Override
    public CodeSetComponentInfo getCodeSetComponentInfo() {
        return this.csci;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        this.csci.write((MarshalOutputStream)outputStream);
    }
    
    @Override
    public int getId() {
        return 1;
    }
}
