package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class IdentifiableBase implements Identifiable, WriteContents
{
    @Override
    public final void write(final OutputStream outputStream) {
        EncapsulationUtility.writeEncapsulation(this, outputStream);
    }
}
