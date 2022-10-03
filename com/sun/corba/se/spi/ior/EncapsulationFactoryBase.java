package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class EncapsulationFactoryBase implements IdentifiableFactory
{
    private int id;
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public EncapsulationFactoryBase(final int id) {
        this.id = id;
    }
    
    @Override
    public final Identifiable create(final InputStream inputStream) {
        return this.readContents(EncapsulationUtility.getEncapsulationStream(inputStream));
    }
    
    protected abstract Identifiable readContents(final InputStream p0);
}
