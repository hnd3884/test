package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import java.io.Serializable;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA_2_3.portable.InputStream;

public class UEInfoServiceContext extends ServiceContext
{
    public static final int SERVICE_CONTEXT_ID = 9;
    private Throwable unknown;
    
    public UEInfoServiceContext(final Throwable unknown) {
        this.unknown = null;
        this.unknown = unknown;
    }
    
    public UEInfoServiceContext(final InputStream inputStream, final GIOPVersion giopVersion) {
        super(inputStream, giopVersion);
        this.unknown = null;
        try {
            this.unknown = (Throwable)this.in.read_value();
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            this.unknown = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
        }
    }
    
    @Override
    public int getId() {
        return 9;
    }
    
    public void writeData(final OutputStream outputStream) throws SystemException {
        outputStream.write_value(this.unknown);
    }
    
    public Throwable getUE() {
        return this.unknown;
    }
    
    @Override
    public String toString() {
        return "UEInfoServiceContext[ unknown=" + this.unknown.toString() + " ]";
    }
}
