package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import java.util.List;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplateBase;

public class IIOPProfileTemplateImpl extends TaggedProfileTemplateBase implements IIOPProfileTemplate
{
    private ORB orb;
    private GIOPVersion giopVersion;
    private IIOPAddress primary;
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IIOPProfileTemplateImpl)) {
            return false;
        }
        final IIOPProfileTemplateImpl iiopProfileTemplateImpl = (IIOPProfileTemplateImpl)o;
        return super.equals(o) && this.giopVersion.equals(iiopProfileTemplateImpl.giopVersion) && this.primary.equals(iiopProfileTemplateImpl.primary);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.giopVersion.hashCode() ^ this.primary.hashCode();
    }
    
    @Override
    public TaggedProfile create(final ObjectKeyTemplate objectKeyTemplate, final ObjectId objectId) {
        return IIOPFactories.makeIIOPProfile(this.orb, objectKeyTemplate, objectId, this);
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return this.giopVersion;
    }
    
    @Override
    public IIOPAddress getPrimaryAddress() {
        return this.primary;
    }
    
    public IIOPProfileTemplateImpl(final ORB orb, final GIOPVersion giopVersion, final IIOPAddress primary) {
        this.orb = orb;
        this.giopVersion = giopVersion;
        this.primary = primary;
        if (this.giopVersion.getMinor() == 0) {
            this.makeImmutable();
        }
    }
    
    public IIOPProfileTemplateImpl(final InputStream inputStream) {
        final byte read_octet = inputStream.read_octet();
        final byte read_octet2 = inputStream.read_octet();
        this.giopVersion = GIOPVersion.getInstance(read_octet, read_octet2);
        this.primary = new IIOPAddressImpl(inputStream);
        this.orb = (ORB)inputStream.orb();
        if (read_octet2 > 0) {
            EncapsulationUtility.readIdentifiableSequence(this, this.orb.getTaggedComponentFactoryFinder(), inputStream);
        }
        this.makeImmutable();
    }
    
    @Override
    public void write(final ObjectKeyTemplate objectKeyTemplate, final ObjectId objectId, final OutputStream outputStream) {
        this.giopVersion.write(outputStream);
        this.primary.write(outputStream);
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)outputStream.orb(), ((CDROutputStream)outputStream).isLittleEndian());
        objectKeyTemplate.write(objectId, encapsOutputStream);
        EncapsulationUtility.writeOutputStream(encapsOutputStream, outputStream);
        if (this.giopVersion.getMinor() > 0) {
            EncapsulationUtility.writeIdentifiableSequence(this, outputStream);
        }
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        this.giopVersion.write(outputStream);
        this.primary.write(outputStream);
        if (this.giopVersion.getMinor() > 0) {
            EncapsulationUtility.writeIdentifiableSequence(this, outputStream);
        }
    }
    
    @Override
    public int getId() {
        return 0;
    }
    
    @Override
    public boolean isEquivalent(final TaggedProfileTemplate taggedProfileTemplate) {
        return taggedProfileTemplate instanceof IIOPProfileTemplateImpl && this.primary.equals(((IIOPProfileTemplateImpl)taggedProfileTemplate).primary);
    }
}
