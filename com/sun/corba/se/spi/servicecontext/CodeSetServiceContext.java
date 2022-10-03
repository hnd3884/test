package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;

public class CodeSetServiceContext extends ServiceContext
{
    public static final int SERVICE_CONTEXT_ID = 1;
    private CodeSetComponentInfo.CodeSetContext csc;
    
    public CodeSetServiceContext(final CodeSetComponentInfo.CodeSetContext csc) {
        this.csc = csc;
    }
    
    public CodeSetServiceContext(final InputStream inputStream, final GIOPVersion giopVersion) {
        super(inputStream, giopVersion);
        (this.csc = new CodeSetComponentInfo.CodeSetContext()).read((MarshalInputStream)this.in);
    }
    
    @Override
    public int getId() {
        return 1;
    }
    
    public void writeData(final OutputStream outputStream) throws SystemException {
        this.csc.write((MarshalOutputStream)outputStream);
    }
    
    public CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
        return this.csc;
    }
    
    @Override
    public String toString() {
        return "CodeSetServiceContext[ csc=" + this.csc + " ]";
    }
}
