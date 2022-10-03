package javax.swing.text.html.parser;

import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.Reader;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import sun.awt.AppContext;
import java.io.Serializable;
import javax.swing.text.html.HTMLEditorKit;

public class ParserDelegator extends HTMLEditorKit.Parser implements Serializable
{
    private static final Object DTD_KEY;
    
    protected static void setDefaultDTD() {
        getDefaultDTD();
    }
    
    private static synchronized DTD getDefaultDTD() {
        final AppContext appContext = AppContext.getAppContext();
        DTD dtd = (DTD)appContext.get(ParserDelegator.DTD_KEY);
        if (dtd == null) {
            DTD dtd2 = null;
            final String s = "html32";
            try {
                dtd2 = DTD.getDTD(s);
            }
            catch (final IOException ex) {
                System.out.println("Throw an exception: could not get default dtd: " + s);
            }
            dtd = createDTD(dtd2, s);
            appContext.put(ParserDelegator.DTD_KEY, dtd);
        }
        return dtd;
    }
    
    protected static DTD createDTD(final DTD dtd, final String s) {
        try {
            final InputStream resourceAsStream = getResourceAsStream(s + ".bdtd");
            if (resourceAsStream != null) {
                dtd.read(new DataInputStream(new BufferedInputStream(resourceAsStream)));
                DTD.putDTDHash(s, dtd);
            }
        }
        catch (final Exception ex) {
            System.out.println(ex);
        }
        return dtd;
    }
    
    public ParserDelegator() {
        setDefaultDTD();
    }
    
    @Override
    public void parse(final Reader reader, final HTMLEditorKit.ParserCallback parserCallback, final boolean b) throws IOException {
        new DocumentParser(getDefaultDTD()).parse(reader, parserCallback, b);
    }
    
    static InputStream getResourceAsStream(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
            @Override
            public InputStream run() {
                return ParserDelegator.class.getResourceAsStream(s);
            }
        });
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        setDefaultDTD();
    }
    
    static {
        DTD_KEY = new Object();
    }
}
