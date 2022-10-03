package com.sun.corba.se.impl.ior;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.activation.POANameHelper;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;

public final class POAObjectKeyTemplate extends NewObjectKeyTemplateBase
{
    public POAObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream orbVersion) {
        super(orb, n, n2, orbVersion.read_long(), orbVersion.read_string(), new ObjectAdapterIdArray(POANameHelper.read(orbVersion)));
        this.setORBVersion(orbVersion);
    }
    
    public POAObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream orbVersion, final OctetSeqHolder octetSeqHolder) {
        super(orb, n, n2, orbVersion.read_long(), orbVersion.read_string(), new ObjectAdapterIdArray(POANameHelper.read(orbVersion)));
        octetSeqHolder.value = this.readObjectKey(orbVersion);
        this.setORBVersion(orbVersion);
    }
    
    public POAObjectKeyTemplate(final ORB orb, final int n, final int n2, final String s, final ObjectAdapterId objectAdapterId) {
        super(orb, -1347695872, n, n2, s, objectAdapterId);
        this.setORBVersion(ORBVersionFactory.getORBVersion());
    }
    
    public void writeTemplate(final OutputStream outputStream) {
        outputStream.write_long(this.getMagic());
        outputStream.write_long(this.getSubcontractId());
        outputStream.write_long(this.getServerId());
        outputStream.write_string(this.getORBId());
        this.getObjectAdapterId().write(outputStream);
    }
}
