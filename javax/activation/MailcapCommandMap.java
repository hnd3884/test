package javax.activation;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Vector;
import com.sun.activation.registries.MailcapFile;

public class MailcapCommandMap extends CommandMap
{
    private static MailcapFile defDB;
    private MailcapFile[] DB;
    private static final int PROG = 0;
    private static boolean debug;
    static /* synthetic */ Class class$javax$activation$MailcapCommandMap;
    
    static {
        MailcapCommandMap.defDB = null;
        MailcapCommandMap.debug = false;
        try {
            MailcapCommandMap.debug = Boolean.getBoolean("javax.activation.debug");
        }
        catch (final Throwable t) {}
    }
    
    public MailcapCommandMap() {
        final Vector vector = new Vector(5);
        vector.addElement(null);
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: load HOME");
        }
        try {
            final String property = System.getProperty("user.home");
            if (property != null) {
                final MailcapFile loadFile = this.loadFile(String.valueOf(property) + File.separator + ".mailcap");
                if (loadFile != null) {
                    vector.addElement(loadFile);
                }
            }
        }
        catch (final SecurityException ex) {}
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: load SYS");
        }
        try {
            final MailcapFile loadFile2 = this.loadFile(String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "mailcap");
            if (loadFile2 != null) {
                vector.addElement(loadFile2);
            }
        }
        catch (final SecurityException ex2) {}
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: load JAR");
        }
        this.loadAllResources(vector, "META-INF/mailcap");
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: load DEF");
        }
        final Class clazz = (MailcapCommandMap.class$javax$activation$MailcapCommandMap != null) ? MailcapCommandMap.class$javax$activation$MailcapCommandMap : (MailcapCommandMap.class$javax$activation$MailcapCommandMap = class$("javax.activation.MailcapCommandMap"));
        synchronized (clazz) {
            if (MailcapCommandMap.defDB == null) {
                MailcapCommandMap.defDB = this.loadResource("/META-INF/mailcap.default");
            }
        }
        if (MailcapCommandMap.defDB != null) {
            vector.addElement(MailcapCommandMap.defDB);
        }
        vector.copyInto(this.DB = new MailcapFile[vector.size()]);
    }
    
    public MailcapCommandMap(final InputStream inputStream) {
        this();
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: load PROG");
        }
        if (this.DB[0] == null) {
            try {
                this.DB[0] = new MailcapFile(inputStream);
            }
            catch (final IOException ex) {}
        }
    }
    
    public MailcapCommandMap(final String s) throws IOException {
        this();
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: load PROG from " + s);
        }
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile(s);
        }
    }
    
    public synchronized void addMailcap(final String s) {
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: add to PROG");
        }
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile();
        }
        this.DB[0].appendToMailcap(s);
    }
    
    private void appendCmdsToVector(final Hashtable hashtable, final Vector vector) {
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final Enumeration elements = hashtable.get(s).elements();
            while (elements.hasMoreElements()) {
                vector.insertElementAt(new CommandInfo(s, (String)elements.nextElement()), 0);
            }
        }
    }
    
    private void appendPrefCmdsToVector(final Hashtable hashtable, final Vector vector) {
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            if (!this.checkForVerb(vector, s)) {
                vector.addElement(new CommandInfo(s, ((Vector<String>)hashtable.get(s)).firstElement()));
            }
        }
    }
    
    private boolean checkForVerb(final Vector vector, final String s) {
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            if (((CommandInfo)elements.nextElement()).getCommandName().equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public synchronized DataContentHandler createDataContentHandler(final String s) {
        if (MailcapCommandMap.debug) {
            System.out.println("MailcapCommandMap: createDataContentHandler for " + s);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                if (MailcapCommandMap.debug) {
                    System.out.println("  search DB #" + i);
                }
                final Hashtable mailcapList = this.DB[i].getMailcapList(s);
                if (mailcapList != null) {
                    final Vector vector = mailcapList.get("content-handler");
                    if (vector != null) {
                        if (MailcapCommandMap.debug) {
                            System.out.println("    got content-handler");
                        }
                        try {
                            if (MailcapCommandMap.debug) {
                                System.out.println("      class " + (String)vector.firstElement());
                            }
                            return (DataContentHandler)Class.forName((String)vector.firstElement()).newInstance();
                        }
                        catch (final IllegalAccessException ex) {}
                        catch (final ClassNotFoundException ex2) {}
                        catch (final InstantiationException ex3) {}
                    }
                }
            }
        }
        return null;
    }
    
    public synchronized CommandInfo[] getAllCommands(final String s) {
        final Vector vector = new Vector();
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Hashtable mailcapList = this.DB[i].getMailcapList(s);
                if (mailcapList != null) {
                    this.appendCmdsToVector(mailcapList, vector);
                }
            }
        }
        final CommandInfo[] array = new CommandInfo[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    public synchronized CommandInfo getCommand(final String s, final String s2) {
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Hashtable mailcapList = this.DB[i].getMailcapList(s);
                if (mailcapList != null) {
                    final Vector vector = mailcapList.get(s2);
                    if (vector != null) {
                        final String s3 = (String)vector.firstElement();
                        if (s3 != null) {
                            return new CommandInfo(s2, s3);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public synchronized CommandInfo[] getPreferredCommands(final String s) {
        final Vector vector = new Vector();
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final Hashtable mailcapList = this.DB[i].getMailcapList(s);
                if (mailcapList != null) {
                    this.appendPrefCmdsToVector(mailcapList, vector);
                }
            }
        }
        final CommandInfo[] array = new CommandInfo[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    private void loadAllResources(final Vector vector, final String s) {
        boolean b = false;
        try {
            ClassLoader classLoader = SecuritySupport.getInstance().getContextClassLoader();
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            URL[] array;
            if (classLoader != null) {
                array = SecuritySupport.getInstance().getResources(classLoader, s);
            }
            else {
                array = SecuritySupport.getInstance().getSystemResources(s);
            }
            if (array != null) {
                if (MailcapCommandMap.debug) {
                    pr("MailcapCommandMap: getResources");
                }
                for (int i = 0; i < array.length; ++i) {
                    final URL url = array[i];
                    InputStream openStream = null;
                    Label_0113: {
                        if (!MailcapCommandMap.debug) {
                            break Label_0113;
                        }
                        pr("MailcapCommandMap: URL " + url);
                        try {
                            openStream = SecuritySupport.getInstance().openStream(url);
                            if (openStream != null) {
                                vector.addElement(new MailcapFile(openStream));
                                b = true;
                                if (MailcapCommandMap.debug) {
                                    pr("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                                }
                            }
                            else if (MailcapCommandMap.debug) {
                                pr("MailcapCommandMap: not loading mailcap file from URL: " + url);
                            }
                        }
                        catch (final IOException ex) {
                            if (MailcapCommandMap.debug) {
                                pr("MailcapCommandMap: " + ex);
                            }
                        }
                        catch (final SecurityException ex2) {
                            if (MailcapCommandMap.debug) {
                                pr("MailcapCommandMap: " + ex2);
                            }
                        }
                        finally {
                            try {
                                if (openStream != null) {
                                    openStream.close();
                                }
                            }
                            catch (final IOException ex3) {}
                        }
                    }
                }
            }
        }
        catch (final Exception ex4) {
            if (MailcapCommandMap.debug) {
                pr("MailcapCommandMap: " + ex4);
            }
        }
        if (!b) {
            if (MailcapCommandMap.debug) {
                pr("MailcapCommandMap: !anyLoaded");
            }
            final MailcapFile loadResource = this.loadResource("/" + s);
            if (loadResource != null) {
                vector.addElement(loadResource);
            }
        }
    }
    
    private MailcapFile loadFile(final String s) {
        MailcapFile mailcapFile = null;
        try {
            mailcapFile = new MailcapFile(s);
        }
        catch (final IOException ex) {}
        return mailcapFile;
    }
    
    private MailcapFile loadResource(final String s) {
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = SecuritySupport.getInstance().getResourceAsStream(this.getClass(), s);
            if (resourceAsStream != null) {
                final MailcapFile mailcapFile = new MailcapFile(resourceAsStream);
                if (MailcapCommandMap.debug) {
                    pr("MailcapCommandMap: successfully loaded mailcap file: " + s);
                }
                return mailcapFile;
            }
            if (MailcapCommandMap.debug) {
                pr("MailcapCommandMap: not loading mailcap file: " + s);
            }
        }
        catch (final IOException ex) {
            if (MailcapCommandMap.debug) {
                pr("MailcapCommandMap: " + ex);
            }
        }
        catch (final SecurityException ex2) {
            if (MailcapCommandMap.debug) {
                pr("MailcapCommandMap: " + ex2);
            }
        }
        finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            }
            catch (final IOException ex3) {}
        }
        return null;
    }
    
    private static final void pr(final String s) {
        System.out.println(s);
    }
}
