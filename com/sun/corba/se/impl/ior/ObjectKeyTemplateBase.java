package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import java.util.Iterator;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;

public abstract class ObjectKeyTemplateBase implements ObjectKeyTemplate
{
    public static final String JIDL_ORB_ID = "";
    private static final String[] JIDL_OAID_STRINGS;
    public static final ObjectAdapterId JIDL_OAID;
    private ORB orb;
    protected IORSystemException wrapper;
    private ORBVersion version;
    private int magic;
    private int scid;
    private int serverid;
    private String orbid;
    private ObjectAdapterId oaid;
    private byte[] adapterId;
    
    @Override
    public byte[] getAdapterId() {
        return this.adapterId.clone();
    }
    
    private byte[] computeAdapterId() {
        final ByteBuffer byteBuffer = new ByteBuffer();
        byteBuffer.append(this.getServerId());
        byteBuffer.append(this.orbid);
        byteBuffer.append(this.oaid.getNumLevels());
        final Iterator iterator = this.oaid.iterator();
        while (iterator.hasNext()) {
            byteBuffer.append((String)iterator.next());
        }
        byteBuffer.trimToSize();
        return byteBuffer.toArray();
    }
    
    public ObjectKeyTemplateBase(final ORB orb, final int magic, final int scid, final int serverid, final String orbid, final ObjectAdapterId oaid) {
        this.orb = orb;
        this.wrapper = IORSystemException.get(orb, "oa.ior");
        this.magic = magic;
        this.scid = scid;
        this.serverid = serverid;
        this.orbid = orbid;
        this.oaid = oaid;
        this.adapterId = this.computeAdapterId();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ObjectKeyTemplateBase)) {
            return false;
        }
        final ObjectKeyTemplateBase objectKeyTemplateBase = (ObjectKeyTemplateBase)o;
        return this.magic == objectKeyTemplateBase.magic && this.scid == objectKeyTemplateBase.scid && this.serverid == objectKeyTemplateBase.serverid && this.version.equals(objectKeyTemplateBase.version) && this.orbid.equals(objectKeyTemplateBase.orbid) && this.oaid.equals(objectKeyTemplateBase.oaid);
    }
    
    @Override
    public int hashCode() {
        return 37 * (37 * (37 * (37 * (37 * (37 * 17 + this.magic) + this.scid) + this.serverid) + this.version.hashCode()) + this.orbid.hashCode()) + this.oaid.hashCode();
    }
    
    @Override
    public int getSubcontractId() {
        return this.scid;
    }
    
    @Override
    public int getServerId() {
        return this.serverid;
    }
    
    @Override
    public String getORBId() {
        return this.orbid;
    }
    
    @Override
    public ObjectAdapterId getObjectAdapterId() {
        return this.oaid;
    }
    
    @Override
    public void write(final ObjectId objectId, final OutputStream outputStream) {
        this.writeTemplate(outputStream);
        objectId.write(outputStream);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        this.writeTemplate(outputStream);
    }
    
    protected abstract void writeTemplate(final OutputStream p0);
    
    protected int getMagic() {
        return this.magic;
    }
    
    public void setORBVersion(final ORBVersion version) {
        this.version = version;
    }
    
    @Override
    public ORBVersion getORBVersion() {
        return this.version;
    }
    
    protected byte[] readObjectKey(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final byte[] array = new byte[read_long];
        inputStream.read_octet_array(array, 0, read_long);
        return array;
    }
    
    @Override
    public CorbaServerRequestDispatcher getServerRequestDispatcher(final ORB orb, final ObjectId objectId) {
        return orb.getRequestDispatcherRegistry().getServerRequestDispatcher(this.scid);
    }
    
    static {
        JIDL_OAID_STRINGS = new String[] { "TransientObjectAdapter" };
        JIDL_OAID = new ObjectAdapterIdArray(ObjectKeyTemplateBase.JIDL_OAID_STRINGS);
    }
}
