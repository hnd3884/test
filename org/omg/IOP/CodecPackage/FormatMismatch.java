package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class FormatMismatch extends UserException
{
    public FormatMismatch() {
        super(FormatMismatchHelper.id());
    }
    
    public FormatMismatch(final String s) {
        super(FormatMismatchHelper.id() + "  " + s);
    }
}
