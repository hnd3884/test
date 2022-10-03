package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.orb.ORBVersion;
import java.util.Iterator;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import java.util.List;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import sun.corba.EncapsInputStreamFactory;
import org.omg.IOP.TaggedProfile;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.IdentifiableBase;

public class IIOPProfileImpl extends IdentifiableBase implements IIOPProfile
{
    private ORB orb;
    private IORSystemException wrapper;
    private ObjectId oid;
    private IIOPProfileTemplate proftemp;
    private ObjectKeyTemplate oktemp;
    protected String codebase;
    protected boolean cachedCodebase;
    private boolean checkedIsLocal;
    private boolean cachedIsLocal;
    private GIOPVersion giopVersion;
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IIOPProfileImpl)) {
            return false;
        }
        final IIOPProfileImpl iiopProfileImpl = (IIOPProfileImpl)o;
        return this.oid.equals(iiopProfileImpl.oid) && this.proftemp.equals(iiopProfileImpl.proftemp) && this.oktemp.equals(iiopProfileImpl.oktemp);
    }
    
    @Override
    public int hashCode() {
        return this.oid.hashCode() ^ this.proftemp.hashCode() ^ this.oktemp.hashCode();
    }
    
    @Override
    public ObjectId getObjectId() {
        return this.oid;
    }
    
    @Override
    public TaggedProfileTemplate getTaggedProfileTemplate() {
        return this.proftemp;
    }
    
    @Override
    public ObjectKeyTemplate getObjectKeyTemplate() {
        return this.oktemp;
    }
    
    private IIOPProfileImpl(final ORB orb) {
        this.codebase = null;
        this.cachedCodebase = false;
        this.checkedIsLocal = false;
        this.cachedIsLocal = false;
        this.giopVersion = null;
        this.orb = orb;
        this.wrapper = IORSystemException.get(orb, "oa.ior");
    }
    
    public IIOPProfileImpl(final ORB orb, final ObjectKeyTemplate oktemp, final ObjectId oid, final IIOPProfileTemplate proftemp) {
        this(orb);
        this.oktemp = oktemp;
        this.oid = oid;
        this.proftemp = proftemp;
    }
    
    public IIOPProfileImpl(final InputStream inputStream) {
        this((ORB)inputStream.orb());
        this.init(inputStream);
    }
    
    public IIOPProfileImpl(final ORB orb, final org.omg.IOP.TaggedProfile taggedProfile) {
        this(orb);
        if (taggedProfile == null || taggedProfile.tag != 0 || taggedProfile.profile_data == null) {
            throw this.wrapper.invalidTaggedProfile();
        }
        final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(orb, taggedProfile.profile_data, taggedProfile.profile_data.length);
        encapsInputStream.consumeEndian();
        this.init(encapsInputStream);
    }
    
    private void init(final InputStream inputStream) {
        final GIOPVersion giopVersion = new GIOPVersion();
        giopVersion.read(inputStream);
        final IIOPAddressImpl iiopAddressImpl = new IIOPAddressImpl(inputStream);
        final ObjectKey create = this.orb.getObjectKeyFactory().create(EncapsulationUtility.readOctets(inputStream));
        this.oktemp = create.getTemplate();
        this.oid = create.getId();
        this.proftemp = IIOPFactories.makeIIOPProfileTemplate(this.orb, giopVersion, iiopAddressImpl);
        if (giopVersion.getMinor() > 0) {
            EncapsulationUtility.readIdentifiableSequence(this.proftemp, this.orb.getTaggedComponentFactoryFinder(), inputStream);
        }
        if (this.uncachedGetCodeBase() == null) {
            final JavaCodebaseComponent comp = LocalCodeBaseSingletonHolder.comp;
            if (comp != null) {
                if (giopVersion.getMinor() > 0) {
                    this.proftemp.add(comp);
                }
                this.codebase = comp.getURLs();
            }
            this.cachedCodebase = true;
        }
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        this.proftemp.write(this.oktemp, this.oid, outputStream);
    }
    
    @Override
    public int getId() {
        return this.proftemp.getId();
    }
    
    @Override
    public boolean isEquivalent(final TaggedProfile taggedProfile) {
        if (!(taggedProfile instanceof IIOPProfile)) {
            return false;
        }
        final IIOPProfile iiopProfile = (IIOPProfile)taggedProfile;
        return this.oid.equals(iiopProfile.getObjectId()) && this.proftemp.isEquivalent(iiopProfile.getTaggedProfileTemplate()) && this.oktemp.equals(iiopProfile.getObjectKeyTemplate());
    }
    
    @Override
    public ObjectKey getObjectKey() {
        return IORFactories.makeObjectKey(this.oktemp, this.oid);
    }
    
    @Override
    public org.omg.IOP.TaggedProfile getIOPProfile() {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.orb);
        encapsOutputStream.write_long(this.getId());
        this.write(encapsOutputStream);
        return TaggedProfileHelper.read(encapsOutputStream.create_input_stream());
    }
    
    private String uncachedGetCodeBase() {
        final Iterator iteratorById = this.proftemp.iteratorById(25);
        if (iteratorById.hasNext()) {
            return ((JavaCodebaseComponent)iteratorById.next()).getURLs();
        }
        return null;
    }
    
    @Override
    public synchronized String getCodebase() {
        if (!this.cachedCodebase) {
            this.cachedCodebase = true;
            this.codebase = this.uncachedGetCodeBase();
        }
        return this.codebase;
    }
    
    @Override
    public ORBVersion getORBVersion() {
        return this.oktemp.getORBVersion();
    }
    
    @Override
    public synchronized boolean isLocal() {
        if (!this.checkedIsLocal) {
            this.checkedIsLocal = true;
            this.cachedIsLocal = (this.orb.isLocalHost(this.proftemp.getPrimaryAddress().getHost()) && this.orb.isLocalServerId(this.oktemp.getSubcontractId(), this.oktemp.getServerId()) && this.orb.getLegacyServerSocketManager().legacyIsLocalServerPort(this.proftemp.getPrimaryAddress().getPort()));
        }
        return this.cachedIsLocal;
    }
    
    @Override
    public Object getServant() {
        if (!this.isLocal()) {
            return null;
        }
        final ObjectAdapterFactory objectAdapterFactory = this.orb.getRequestDispatcherRegistry().getObjectAdapterFactory(this.oktemp.getSubcontractId());
        final ObjectAdapterId objectAdapterId = this.oktemp.getObjectAdapterId();
        ObjectAdapter find;
        try {
            find = objectAdapterFactory.find(objectAdapterId);
        }
        catch (final SystemException ex) {
            this.wrapper.getLocalServantFailure(ex, objectAdapterId.toString());
            return null;
        }
        return find.getLocalServant(this.oid.getId());
    }
    
    @Override
    public synchronized GIOPVersion getGIOPVersion() {
        return this.proftemp.getGIOPVersion();
    }
    
    @Override
    public void makeImmutable() {
        this.proftemp.makeImmutable();
    }
    
    private static class LocalCodeBaseSingletonHolder
    {
        public static JavaCodebaseComponent comp;
        
        static {
            final String localCodebase = JDKBridge.getLocalCodebase();
            if (localCodebase == null) {
                LocalCodeBaseSingletonHolder.comp = null;
            }
            else {
                LocalCodeBaseSingletonHolder.comp = IIOPFactories.makeJavaCodebaseComponent(localCodebase);
            }
        }
    }
}
