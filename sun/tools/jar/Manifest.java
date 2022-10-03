package sun.tools.jar;

import java.io.PrintStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.MessageDigest;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import sun.net.www.MessageHeader;
import java.util.Vector;

public class Manifest
{
    private Vector<MessageHeader> entries;
    private byte[] tmpbuf;
    private Hashtable<String, MessageHeader> tableEntries;
    static final String[] hashes;
    static final byte[] EOL;
    static final boolean debug = false;
    static final String VERSION = "1.0";
    
    static final void debug(final String s) {
    }
    
    public Manifest() {
        this.entries = new Vector<MessageHeader>();
        this.tmpbuf = new byte[512];
        this.tableEntries = new Hashtable<String, MessageHeader>();
    }
    
    public Manifest(final byte[] array) throws IOException {
        this(new ByteArrayInputStream(array), false);
    }
    
    public Manifest(final InputStream inputStream) throws IOException {
        this(inputStream, true);
    }
    
    public Manifest(InputStream inputStream, final boolean b) throws IOException {
        this.entries = new Vector<MessageHeader>();
        this.tmpbuf = new byte[512];
        this.tableEntries = new Hashtable<String, MessageHeader>();
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        while (true) {
            inputStream.mark(1);
            if (inputStream.read() == -1) {
                break;
            }
            inputStream.reset();
            final MessageHeader messageHeader = new MessageHeader(inputStream);
            if (b) {
                this.doHashes(messageHeader);
            }
            this.addEntry(messageHeader);
        }
    }
    
    public Manifest(final String[] array) throws IOException {
        this.entries = new Vector<MessageHeader>();
        this.tmpbuf = new byte[512];
        this.tableEntries = new Hashtable<String, MessageHeader>();
        final MessageHeader messageHeader = new MessageHeader();
        messageHeader.add("Manifest-Version", "1.0");
        messageHeader.add("Created-By", "Manifest JDK " + System.getProperty("java.version"));
        this.addEntry(messageHeader);
        this.addFiles(null, array);
    }
    
    public void addEntry(final MessageHeader messageHeader) {
        this.entries.addElement(messageHeader);
        final String value = messageHeader.findValue("Name");
        debug("addEntry for name: " + value);
        if (value != null) {
            this.tableEntries.put(value, messageHeader);
        }
    }
    
    public MessageHeader getEntry(final String s) {
        return this.tableEntries.get(s);
    }
    
    public MessageHeader entryAt(final int n) {
        return this.entries.elementAt(n);
    }
    
    public Enumeration<MessageHeader> entries() {
        return this.entries.elements();
    }
    
    public void addFiles(final File file, final String[] array) throws IOException {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            File file2;
            if (file == null) {
                file2 = new File(array[i]);
            }
            else {
                file2 = new File(file, array[i]);
            }
            if (file2.isDirectory()) {
                this.addFiles(file2, file2.list());
            }
            else {
                this.addFile(file2);
            }
        }
    }
    
    private final String stdToLocal(final String s) {
        return s.replace('/', File.separatorChar);
    }
    
    private final String localToStd(String s) {
        s = s.replace(File.separatorChar, '/');
        if (s.startsWith("./")) {
            s = s.substring(2);
        }
        else if (s.startsWith("/")) {
            s = s.substring(1);
        }
        return s;
    }
    
    public void addFile(final File file) throws IOException {
        final String localToStd = this.localToStd(file.getPath());
        if (this.tableEntries.get(localToStd) == null) {
            final MessageHeader messageHeader = new MessageHeader();
            messageHeader.add("Name", localToStd);
            this.addEntry(messageHeader);
        }
    }
    
    public void doHashes(final MessageHeader messageHeader) throws IOException {
        final String value = messageHeader.findValue("Name");
        if (value == null || value.endsWith("/")) {
            return;
        }
        for (int i = 0; i < Manifest.hashes.length; ++i) {
            final FileInputStream fileInputStream = new FileInputStream(this.stdToLocal(value));
            try {
                final MessageDigest instance = MessageDigest.getInstance(Manifest.hashes[i]);
                int read;
                while ((read = fileInputStream.read(this.tmpbuf, 0, this.tmpbuf.length)) != -1) {
                    instance.update(this.tmpbuf, 0, read);
                }
                messageHeader.set(Manifest.hashes[i] + "-Digest", Base64.getMimeEncoder().encodeToString(instance.digest()));
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new JarException("Digest algorithm " + Manifest.hashes[i] + " not available.");
            }
            finally {
                fileInputStream.close();
            }
        }
    }
    
    public void stream(final OutputStream outputStream) throws IOException {
        PrintStream printStream;
        if (outputStream instanceof PrintStream) {
            printStream = (PrintStream)outputStream;
        }
        else {
            printStream = new PrintStream(outputStream);
        }
        final MessageHeader messageHeader = this.entries.elementAt(0);
        if (messageHeader.findValue("Manifest-Version") == null) {
            final String property = System.getProperty("java.version");
            if (messageHeader.findValue("Name") == null) {
                messageHeader.prepend("Manifest-Version", "1.0");
                messageHeader.add("Created-By", "Manifest JDK " + property);
            }
            else {
                printStream.print("Manifest-Version: 1.0\r\nCreated-By: " + property + "\r\n\r\n");
            }
            printStream.flush();
        }
        messageHeader.print(printStream);
        for (int i = 1; i < this.entries.size(); ++i) {
            this.entries.elementAt(i).print(printStream);
        }
    }
    
    public static boolean isManifestName(String s) {
        if (s.charAt(0) == '/') {
            s = s.substring(1, s.length());
        }
        s = s.toUpperCase();
        return s.equals("META-INF/MANIFEST.MF");
    }
    
    static {
        hashes = new String[] { "SHA" };
        EOL = new byte[] { 13, 10 };
    }
}
