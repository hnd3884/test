package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectId;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;

public final class OldJIDLObjectKeyTemplate extends OldObjectKeyTemplateBase
{
    public static final byte NULL_PATCH_VERSION = 0;
    byte patchVersion;
    
    public OldJIDLObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream inputStream, final OctetSeqHolder octetSeqHolder) {
        this(orb, n, n2, inputStream);
        octetSeqHolder.value = this.readObjectKey(inputStream);
        if (n == -1347695873 && octetSeqHolder.value.length > ((CDRInputStream)inputStream).getPosition()) {
            this.patchVersion = inputStream.read_octet();
            if (this.patchVersion == 1) {
                this.setORBVersion(ORBVersionFactory.getJDK1_3_1_01());
            }
            else {
                if (this.patchVersion <= 1) {
                    throw this.wrapper.invalidJdk131PatchLevel(new Integer(this.patchVersion));
                }
                this.setORBVersion(ORBVersionFactory.getORBVersion());
            }
        }
    }
    
    public OldJIDLObjectKeyTemplate(final ORB orb, final int n, final int n2, final int n3) {
        super(orb, n, n2, n3, "", OldJIDLObjectKeyTemplate.JIDL_OAID);
        this.patchVersion = 0;
    }
    
    public OldJIDLObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream inputStream) {
        this(orb, n, n2, inputStream.read_long());
    }
    
    @Override
    protected void writeTemplate(final OutputStream outputStream) {
        outputStream.write_long(this.getMagic());
        outputStream.write_long(this.getSubcontractId());
        outputStream.write_long(this.getServerId());
    }
    
    @Override
    public void write(final ObjectId objectId, final OutputStream outputStream) {
        super.write(objectId, outputStream);
        if (this.patchVersion != 0) {
            outputStream.write_octet(this.patchVersion);
        }
    }
}
