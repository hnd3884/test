package org.omg.IOP.CodecFactoryPackage;

import org.omg.CORBA.UserException;

public final class UnknownEncoding extends UserException
{
    public UnknownEncoding() {
        super(UnknownEncodingHelper.id());
    }
    
    public UnknownEncoding(final String s) {
        super(UnknownEncodingHelper.id() + "  " + s);
    }
}
