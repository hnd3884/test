package sun.security.x509;

import java.util.Collection;
import java.util.Enumeration;
import java.security.cert.CertificateException;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import sun.security.util.DerValue;
import java.io.IOException;
import java.security.cert.CRLException;
import sun.security.util.DerInputStream;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;

public class CRLExtensions
{
    private Map<String, Extension> map;
    private boolean unsupportedCritExt;
    private static final Class[] PARAMS;
    
    public CRLExtensions() {
        this.map = Collections.synchronizedMap(new TreeMap<String, Extension>());
        this.unsupportedCritExt = false;
    }
    
    public CRLExtensions(final DerInputStream derInputStream) throws CRLException {
        this.map = Collections.synchronizedMap(new TreeMap<String, Extension>());
        this.unsupportedCritExt = false;
        this.init(derInputStream);
    }
    
    private void init(final DerInputStream derInputStream) throws CRLException {
        try {
            DerInputStream data = derInputStream;
            final byte b = (byte)derInputStream.peekByte();
            if ((b & 0xC0) == 0x80 && (b & 0x1F) == 0x0) {
                data = data.getDerValue().data;
            }
            final DerValue[] sequence = data.getSequence(5);
            for (int i = 0; i < sequence.length; ++i) {
                this.parseExtension(new Extension(sequence[i]));
            }
        }
        catch (final IOException ex) {
            throw new CRLException("Parsing error: " + ex.toString());
        }
    }
    
    private void parseExtension(final Extension extension) throws CRLException {
        try {
            final Class<?> class1 = OIDMap.getClass(extension.getExtensionId());
            if (class1 == null) {
                if (extension.isCritical()) {
                    this.unsupportedCritExt = true;
                }
                if (this.map.put(extension.getExtensionId().toString(), extension) != null) {
                    throw new CRLException("Duplicate extensions not allowed");
                }
            }
            else {
                final CertAttrSet set = (CertAttrSet)class1.getConstructor((Class<?>[])CRLExtensions.PARAMS).newInstance(extension.isCritical(), extension.getExtensionValue());
                if (this.map.put(set.getName(), (Extension)set) != null) {
                    throw new CRLException("Duplicate extensions not allowed");
                }
            }
        }
        catch (final InvocationTargetException ex) {
            throw new CRLException(ex.getTargetException().getMessage());
        }
        catch (final Exception ex2) {
            throw new CRLException(ex2.toString());
        }
    }
    
    public void encode(final OutputStream outputStream, final boolean b) throws CRLException {
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final Object[] array = this.map.values().toArray();
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof CertAttrSet) {
                    ((CertAttrSet)array[i]).encode(derOutputStream);
                }
                else {
                    if (!(array[i] instanceof Extension)) {
                        throw new CRLException("Illegal extension object");
                    }
                    ((Extension)array[i]).encode(derOutputStream);
                }
            }
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.write((byte)48, derOutputStream);
            DerOutputStream derOutputStream3 = new DerOutputStream();
            if (b) {
                derOutputStream3.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
            }
            else {
                derOutputStream3 = derOutputStream2;
            }
            outputStream.write(derOutputStream3.toByteArray());
        }
        catch (final IOException ex) {
            throw new CRLException("Encoding error: " + ex.toString());
        }
        catch (final CertificateException ex2) {
            throw new CRLException("Encoding error: " + ex2.toString());
        }
    }
    
    public Extension get(final String s) {
        String substring;
        if (new X509AttributeName(s).getPrefix().equalsIgnoreCase("x509")) {
            substring = s.substring(s.lastIndexOf(".") + 1);
        }
        else {
            substring = s;
        }
        return this.map.get(substring);
    }
    
    public void set(final String s, final Object o) {
        this.map.put(s, (Extension)o);
    }
    
    public void delete(final String s) {
        this.map.remove(s);
    }
    
    public Enumeration<Extension> getElements() {
        return Collections.enumeration(this.map.values());
    }
    
    public Collection<Extension> getAllExtensions() {
        return this.map.values();
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        return this.unsupportedCritExt;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CRLExtensions)) {
            return false;
        }
        final Object[] array = ((CRLExtensions)o).getAllExtensions().toArray();
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
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
    
    static {
        PARAMS = new Class[] { Boolean.class, Object.class };
    }
}
