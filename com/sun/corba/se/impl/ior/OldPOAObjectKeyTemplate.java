package com.sun.corba.se.impl.ior;

import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.ORBVersion;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;

public final class OldPOAObjectKeyTemplate extends OldObjectKeyTemplateBase
{
    public OldPOAObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream inputStream) {
        this(orb, n, n2, inputStream.read_long(), inputStream.read_long(), inputStream.read_long());
    }
    
    public OldPOAObjectKeyTemplate(final ORB orb, final int n, final int n2, final InputStream inputStream, final OctetSeqHolder octetSeqHolder) {
        this(orb, n, n2, inputStream);
        octetSeqHolder.value = this.readObjectKey(inputStream);
    }
    
    public OldPOAObjectKeyTemplate(final ORB orb, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(orb, n, n2, n3, Integer.toString(n4), new ObjectAdapterIdNumber(n5));
    }
    
    public void writeTemplate(final OutputStream outputStream) {
        outputStream.write_long(this.getMagic());
        outputStream.write_long(this.getSubcontractId());
        outputStream.write_long(this.getServerId());
        outputStream.write_long(Integer.parseInt(this.getORBId()));
        outputStream.write_long(((ObjectAdapterIdNumber)this.getObjectAdapterId()).getOldPOAId());
    }
    
    @Override
    public ORBVersion getORBVersion() {
        if (this.getMagic() == -1347695874) {
            return ORBVersionFactory.getOLD();
        }
        if (this.getMagic() == -1347695873) {
            return ORBVersionFactory.getNEW();
        }
        throw new INTERNAL();
    }
}
