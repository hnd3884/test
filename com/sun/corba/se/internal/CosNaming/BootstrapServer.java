package com.sun.corba.se.internal.CosNaming;

import java.util.Hashtable;
import com.sun.corba.se.spi.resolver.LocalResolver;
import org.omg.CORBA.ORBPackage.InvalidName;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import java.util.Properties;
import java.io.File;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.orb.ORB;

public class BootstrapServer
{
    private ORB orb;
    
    public static final void main(final String[] array) {
        String s = null;
        int int1 = 900;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-InitialServicesFile") && i < array.length - 1) {
                s = array[i + 1];
            }
            if (array[i].equals("-ORBInitialPort") && i < array.length - 1) {
                int1 = Integer.parseInt(array[i + 1]);
            }
        }
        if (s == null) {
            System.out.println(CorbaResourceUtil.getText("bootstrap.usage", "BootstrapServer"));
            return;
        }
        final File file = new File(s);
        if (file.exists() && !file.canRead()) {
            System.err.println(CorbaResourceUtil.getText("bootstrap.filenotreadable", file.getAbsolutePath()));
            return;
        }
        System.out.println(CorbaResourceUtil.getText("bootstrap.success", Integer.toString(int1), file.getAbsolutePath()));
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("com.sun.CORBA.ORBServerPort", Integer.toString(int1));
        final ORB orb = (ORB)org.omg.CORBA.ORB.init(array, properties);
        final LocalResolver localResolver = orb.getLocalResolver();
        orb.setLocalResolver(ResolverDefault.makeSplitLocalResolver(ResolverDefault.makeCompositeResolver(ResolverDefault.makeFileResolver(orb, file), localResolver), localResolver));
        try {
            orb.resolve_initial_references("RootPOA");
        }
        catch (final InvalidName invalidName) {
            final RuntimeException ex = new RuntimeException("This should not happen");
            ex.initCause(invalidName);
            throw ex;
        }
        orb.run();
    }
}
