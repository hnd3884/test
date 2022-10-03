package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class InvalidTypeForEncoding extends UserException
{
    public InvalidTypeForEncoding() {
        super(InvalidTypeForEncodingHelper.id());
    }
    
    public InvalidTypeForEncoding(final String s) {
        super(InvalidTypeForEncodingHelper.id() + "  " + s);
    }
}
