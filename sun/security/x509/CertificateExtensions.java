package sun.security.x509;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerOutputStream;
import java.security.cert.CertificateException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerInputStream;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import sun.security.util.Debug;

public class CertificateExtensions implements CertAttrSet<Extension>
{
    public static final String IDENT = "x509.info.extensions";
    public static final String NAME = "extensions";
    private static final Debug debug;
    private Map<String, Extension> map;
    private boolean unsupportedCritExt;
    private Map<String, Extension> unparseableExtensions;
    private static Class[] PARAMS;
    
    public CertificateExtensions() {
        this.map = Collections.synchronizedMap(new TreeMap<String, Extension>());
        this.unsupportedCritExt = false;
    }
    
    public CertificateExtensions(final DerInputStream derInputStream) throws IOException {
        this.map = Collections.synchronizedMap(new TreeMap<String, Extension>());
        this.unsupportedCritExt = false;
        this.init(derInputStream);
    }
    
    private void init(final DerInputStream derInputStream) throws IOException {
        final DerValue[] sequence = derInputStream.getSequence(5);
        for (int i = 0; i < sequence.length; ++i) {
            this.parseExtension(new Extension(sequence[i]));
        }
    }
    
    private void parseExtension(final Extension extension) throws IOException {
        try {
            final Class<?> class1 = OIDMap.getClass(extension.getExtensionId());
            if (class1 == null) {
                if (extension.isCritical()) {
                    this.unsupportedCritExt = true;
                }
                if (this.map.put(extension.getExtensionId().toString(), extension) == null) {
                    return;
                }
                throw new IOException("Duplicate extensions not allowed");
            }
            else {
                final CertAttrSet set = (CertAttrSet)class1.getConstructor((Class<?>[])CertificateExtensions.PARAMS).newInstance(extension.isCritical(), extension.getExtensionValue());
                if (this.map.put(set.getName(), (Extension)set) != null) {
                    throw new IOException("Duplicate extensions not allowed");
                }
            }
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (!extension.isCritical()) {
                if (this.unparseableExtensions == null) {
                    this.unparseableExtensions = new TreeMap<String, Extension>();
                }
                this.unparseableExtensions.put(extension.getExtensionId().toString(), new UnparseableExtension(extension, targetException));
                if (CertificateExtensions.debug != null) {
                    CertificateExtensions.debug.println("Debug info only. Error parsing extension: " + extension);
                    targetException.printStackTrace();
                    System.err.println(new HexDumpEncoder().encodeBuffer(extension.getExtensionValue()));
                }
                return;
            }
            if (targetException instanceof IOException) {
                throw (IOException)targetException;
            }
            throw new IOException(targetException);
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new IOException(ex3);
        }
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws CertificateException, IOException {
        this.encode(outputStream, false);
    }
    
    public void encode(final OutputStream outputStream, final boolean b) throws CertificateException, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final Object[] array = this.map.values().toArray();
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof CertAttrSet) {
                ((CertAttrSet)array[i]).encode(derOutputStream);
            }
            else {
                if (!(array[i] instanceof Extension)) {
                    throw new CertificateException("Illegal extension object");
                }
                ((Extension)array[i]).encode(derOutputStream);
            }
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write((byte)48, derOutputStream);
        DerOutputStream derOutputStream3;
        if (!b) {
            derOutputStream3 = new DerOutputStream();
            derOutputStream3.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream2);
        }
        else {
            derOutputStream3 = derOutputStream2;
        }
        outputStream.write(derOutputStream3.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (o instanceof Extension) {
            this.map.put(s, (Extension)o);
            return;
        }
        throw new IOException("Unknown extension type.");
    }
    
    @Override
    public Extension get(final String s) throws IOException {
        final Extension extension = this.map.get(s);
        if (extension == null) {
            throw new IOException("No extension found with name " + s);
        }
        return extension;
    }
    
    Extension getExtension(final String s) {
        return this.map.get(s);
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (this.map.get(s) == null) {
            throw new IOException("No extension found with name " + s);
        }
        this.map.remove(s);
    }
    
    public String getNameByOid(final ObjectIdentifier objectIdentifier) throws IOException {
        for (final String s : this.map.keySet()) {
            if (this.map.get(s).getExtensionId().equals((Object)objectIdentifier)) {
                return s;
            }
        }
        return null;
    }
    
    @Override
    public Enumeration<Extension> getElements() {
        return Collections.enumeration(this.map.values());
    }
    
    public Collection<Extension> getAllExtensions() {
        return this.map.values();
    }
    
    public Map<String, Extension> getUnparseableExtensions() {
        if (this.unparseableExtensions == null) {
            return Collections.emptyMap();
        }
        return this.unparseableExtensions;
    }
    
    @Override
    public String getName() {
        return "extensions";
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        return this.unsupportedCritExt;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateExtensions)) {
            return false;
        }
        final Object[] array = ((CertificateExtensions)o).getAllExtensions().toArray();
        final int length = array.length;
        if (length != this.map.size()) {
            return false;
        }
        Object o2 = null;
        for (int i = 0; i < length; ++i) {
            if (array[i] instanceof CertAttrSet) {
                o2 = ((CertAttrSet)array[i]).getName();
            }
            final Extension extension = (Extension)array[i];
            if (o2 == null) {
                o2 = extension.getExtensionId().toString();
            }
            final Extension extension2 = this.map.get(o2);
            if (extension2 == null) {
                return false;
            }
            if (!extension2.equals(extension)) {
                return false;
            }
        }
        return this.getUnparseableExtensions().equals(((CertificateExtensions)o).getUnparseableExtensions());
    }
    
    @Override
    public int hashCode() {
        return this.map.hashCode() + this.getUnparseableExtensions().hashCode();
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
    
    static {
        debug = Debug.getInstance("x509");
        CertificateExtensions.PARAMS = new Class[] { Boolean.class, Object.class };
    }
}
