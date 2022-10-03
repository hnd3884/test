package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.Repository;
import org.omg.SendingContext.RunTimeOperations;

public interface CodeBaseOperations extends RunTimeOperations
{
    Repository get_ir();
    
    String implementation(final String p0);
    
    String[] implementations(final String[] p0);
    
    FullValueDescription meta(final String p0);
    
    FullValueDescription[] metas(final String[] p0);
    
    String[] bases(final String p0);
}
