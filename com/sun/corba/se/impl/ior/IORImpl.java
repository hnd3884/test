package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import java.util.HashMap;
import com.sun.corba.se.spi.ior.TaggedProfile;
import org.omg.IOP.IORHelper;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import java.io.IOException;
import com.sun.corba.se.impl.orbutil.HexOutputStream;
import java.io.StringWriter;
import sun.corba.OutputStreamFactory;
import org.omg.CORBA_2_3.portable.OutputStream;
import java.util.List;
import org.omg.CORBA_2_3.portable.InputStream;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;

public class IORImpl extends IdentifiableContainerBase implements IOR
{
    private String typeId;
    private ORB factory;
    private boolean isCachedHashValue;
    private int cachedHashValue;
    IORSystemException wrapper;
    private IORTemplateList iortemps;
    
    @Override
    public ORB getORB() {
        return this.factory;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof IOR)) {
            return false;
        }
        final IOR ior = (IOR)o;
        return super.equals(o) && this.typeId.equals(ior.getTypeId());
    }
    
    @Override
    public synchronized int hashCode() {
        if (!this.isCachedHashValue) {
            this.cachedHashValue = (super.hashCode() ^ this.typeId.hashCode());
            this.isCachedHashValue = true;
        }
        return this.cachedHashValue;
    }
    
    public IORImpl(final ORB orb) {
        this(orb, "");
    }
    
    public IORImpl(final ORB factory, final String typeId) {
        this.factory = null;
        this.isCachedHashValue = false;
        this.iortemps = null;
        this.factory = factory;
        this.wrapper = IORSystemException.get(factory, "oa.ior");
        this.typeId = typeId;
    }
    
    public IORImpl(final ORB orb, final String s, final IORTemplate iorTemplate, final ObjectId objectId) {
        this(orb, s);
        (this.iortemps = IORFactories.makeIORTemplateList()).add(iorTemplate);
        this.addTaggedProfiles(iorTemplate, objectId);
        this.makeImmutable();
    }
    
    private void addTaggedProfiles(final IORTemplate iorTemplate, final ObjectId objectId) {
        final ObjectKeyTemplate objectKeyTemplate = iorTemplate.getObjectKeyTemplate();
        final Iterator<Object> iterator = iorTemplate.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next().create(objectKeyTemplate, objectId));
        }
    }
    
    public IORImpl(final ORB orb, final String s, final IORTemplateList iortemps, final ObjectId objectId) {
        this(orb, s);
        this.iortemps = iortemps;
        final Iterator<Object> iterator = iortemps.iterator();
        while (iterator.hasNext()) {
            this.addTaggedProfiles(iterator.next(), objectId);
        }
        this.makeImmutable();
    }
    
    public IORImpl(final InputStream inputStream) {
        this((ORB)inputStream.orb(), inputStream.read_string());
        EncapsulationUtility.readIdentifiableSequence(this, this.factory.getTaggedProfileFactoryFinder(), inputStream);
        this.makeImmutable();
    }
    
    @Override
    public String getTypeId() {
        return this.typeId;
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_string(this.typeId);
        EncapsulationUtility.writeIdentifiableSequence(this, outputStream);
    }
    
    @Override
    public String stringify() {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.factory);
        encapsOutputStream.putEndian();
        this.write(encapsOutputStream);
        final StringWriter stringWriter = new StringWriter();
        try {
            encapsOutputStream.writeTo(new HexOutputStream(stringWriter));
        }
        catch (final IOException ex) {
            throw this.wrapper.stringifyWriteError(ex);
        }
        return "IOR:" + stringWriter;
    }
    
    @Override
    public synchronized void makeImmutable() {
        this.makeElementsImmutable();
        if (this.iortemps != null) {
            this.iortemps.makeImmutable();
        }
        super.makeImmutable();
    }
    
    @Override
    public org.omg.IOP.IOR getIOPIOR() {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.factory);
        this.write(encapsOutputStream);
        return IORHelper.read(encapsOutputStream.create_input_stream());
    }
    
    @Override
    public boolean isNil() {
        return this.size() == 0;
    }
    
    @Override
    public boolean isEquivalent(final IOR ior) {
        final Iterator<Object> iterator = this.iterator();
        final Iterator<Object> iterator2 = ior.iterator();
        while (iterator.hasNext() && iterator2.hasNext()) {
            if (!iterator.next().isEquivalent(iterator2.next())) {
                return false;
            }
        }
        return iterator.hasNext() == iterator2.hasNext();
    }
    
    private void initializeIORTemplateList() {
        final HashMap hashMap = new HashMap();
        this.iortemps = IORFactories.makeIORTemplateList();
        final Iterator<Object> iterator = this.iterator();
        Object objectId = null;
        while (iterator.hasNext()) {
            final TaggedProfile taggedProfile = iterator.next();
            final TaggedProfileTemplate taggedProfileTemplate = taggedProfile.getTaggedProfileTemplate();
            final ObjectKeyTemplate objectKeyTemplate = taggedProfile.getObjectKeyTemplate();
            if (objectId == null) {
                objectId = taggedProfile.getObjectId();
            }
            else if (!objectId.equals(taggedProfile.getObjectId())) {
                throw this.wrapper.badOidInIorTemplateList();
            }
            IORTemplate iorTemplate = (IORTemplate)hashMap.get(objectKeyTemplate);
            if (iorTemplate == null) {
                iorTemplate = IORFactories.makeIORTemplate(objectKeyTemplate);
                hashMap.put(objectKeyTemplate, iorTemplate);
                this.iortemps.add(iorTemplate);
            }
            iorTemplate.add(taggedProfileTemplate);
        }
        this.iortemps.makeImmutable();
    }
    
    @Override
    public synchronized IORTemplateList getIORTemplates() {
        if (this.iortemps == null) {
            this.initializeIORTemplateList();
        }
        return this.iortemps;
    }
    
    @Override
    public IIOPProfile getProfile() {
        IIOPProfile iiopProfile = null;
        final Iterator iteratorById = this.iteratorById(0);
        if (iteratorById.hasNext()) {
            iiopProfile = (IIOPProfile)iteratorById.next();
        }
        if (iiopProfile != null) {
            return iiopProfile;
        }
        throw this.wrapper.iorMustHaveIiopProfile();
    }
}
