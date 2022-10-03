package org.apache.xmlbeans.impl.jam;

import org.apache.xmlbeans.impl.jam.provider.JamServiceFactoryImpl;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.xmlbeans.impl.jam.internal.JamPrinter;
import java.io.File;
import java.io.IOException;

public abstract class JamServiceFactory
{
    private static final JamServiceFactory DEFAULT;
    
    public static JamServiceFactory getInstance() {
        return JamServiceFactory.DEFAULT;
    }
    
    protected JamServiceFactory() {
    }
    
    public abstract JamServiceParams createServiceParams();
    
    public abstract JamService createService(final JamServiceParams p0) throws IOException;
    
    public abstract JamClassLoader createSystemJamClassLoader();
    
    public abstract JamClassLoader createJamClassLoader(final ClassLoader p0);
    
    public static void main(final String[] args) {
        try {
            final JamServiceParams sp = getInstance().createServiceParams();
            for (int i = 0; i < args.length; ++i) {
                sp.includeSourcePattern(new File[] { new File(".") }, args[i]);
            }
            final JamService service = getInstance().createService(sp);
            final JamPrinter jp = JamPrinter.newInstance();
            final PrintWriter out = new PrintWriter(System.out);
            final JamClassIterator j = service.getClasses();
            while (j.hasNext()) {
                out.println("-------- ");
                jp.print(j.nextClass(), out);
            }
            out.flush();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        System.out.flush();
        System.err.flush();
    }
    
    static {
        DEFAULT = new JamServiceFactoryImpl();
    }
}
