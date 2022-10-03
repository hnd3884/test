package com.sun.corba.se.impl.ior;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;

public final class JIDLObjectKeyTemplate extends NewObjectKeyTemplateBase
{
    public JIDLObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream orbVersion) {
        super(orb, n, n2, orbVersion.read_long(), "", JIDLObjectKeyTemplate.JIDL_OAID);
        this.setORBVersion(orbVersion);
    }
    
    public JIDLObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream orbVersion, final OctetSeqHolder octetSeqHolder) {
        super(orb, n, n2, orbVersion.read_long(), "", JIDLObjectKeyTemplate.JIDL_OAID);
        octetSeqHolder.value = this.readObjectKey(orbVersion);
        this.setORBVersion(orbVersion);
    }
    
    public JIDLObjectKeyTemplate(final ORB orb, final int n, final int n2) {
        super(orb, -1347695872, n, n2, "", JIDLObjectKeyTemplate.JIDL_OAID);
        this.setORBVersion(ORBVersionFactory.getORBVersion());
    }
    
    @Override
    protected void writeTemplate(final OutputStream outputStream) {
        outputStream.write_long(this.getMagic());
        outputStream.write_long(this.getSubcontractId());
        outputStream.write_long(this.getServerId());
    }
}
