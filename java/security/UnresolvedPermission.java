package java.security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import sun.misc.IOUtils;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Hashtable;
import java.io.ObjectInputStream;
import java.security.cert.CertificateEncodingException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import sun.security.util.Debug;
import java.io.Serializable;

public final class UnresolvedPermission extends Permission implements Serializable
{
    private static final long serialVersionUID = -4821973115467008846L;
    private static final Debug debug;
    private String type;
    private String name;
    private String actions;
    private transient Certificate[] certs;
    private static final Class[] PARAMS0;
    private static final Class[] PARAMS1;
    private static final Class[] PARAMS2;
    
    public UnresolvedPermission(final String type, final String name, final String actions, final Certificate[] array) {
        super(type);
        if (type == null) {
            throw new NullPointerException("type can't be null");
        }
        this.type = type;
        this.name = name;
        this.actions = actions;
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (!(array[i] instanceof X509Certificate)) {
                    this.certs = array.clone();
                    break;
                }
            }
            if (this.certs == null) {
                int j = 0;
                int n = 0;
                while (j < array.length) {
                    ++n;
                    while (j + 1 < array.length && ((X509Certificate)array[j]).getIssuerDN().equals(((X509Certificate)array[j + 1]).getSubjectDN())) {
                        ++j;
                    }
                    ++j;
                }
                if (n == array.length) {
                    this.certs = array.clone();
                }
                if (this.certs == null) {
                    final ArrayList list = new ArrayList();
                    for (int k = 0; k < array.length; ++k) {
                        list.add(array[k]);
                        while (k + 1 < array.length && ((X509Certificate)array[k]).getIssuerDN().equals(((X509Certificate)array[k + 1]).getSubjectDN())) {
                            ++k;
                        }
                    }
                    list.toArray(this.certs = new Certificate[list.size()]);
                }
            }
        }
    }
    
    Permission resolve(final Permission permission, final Certificate[] array) {
        if (this.certs != null) {
            if (array == null) {
                return null;
            }
            for (int i = 0; i < this.certs.length; ++i) {
                boolean b = false;
                for (int j = 0; j < array.length; ++j) {
                    if (this.certs[i].equals(array[j])) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    return null;
                }
            }
        }
        try {
            final Class<? extends Permission> class1 = permission.getClass();
            if (this.name == null && this.actions == null) {
                try {
                    return (Permission)class1.getConstructor((Class<?>[])UnresolvedPermission.PARAMS0).newInstance(new Object[0]);
                }
                catch (final NoSuchMethodException ex) {
                    try {
                        return (Permission)class1.getConstructor((Class<?>[])UnresolvedPermission.PARAMS1).newInstance(this.name);
                    }
                    catch (final NoSuchMethodException ex2) {
                        return (Permission)class1.getConstructor((Class<?>[])UnresolvedPermission.PARAMS2).newInstance(this.name, this.actions);
                    }
                }
            }
            if (this.name != null && this.actions == null) {
                try {
                    return (Permission)class1.getConstructor((Class<?>[])UnresolvedPermission.PARAMS1).newInstance(this.name);
                }
                catch (final NoSuchMethodException ex3) {
                    return (Permission)class1.getConstructor((Class<?>[])UnresolvedPermission.PARAMS2).newInstance(this.name, this.actions);
                }
            }
            return (Permission)class1.getConstructor((Class<?>[])UnresolvedPermission.PARAMS2).newInstance(this.name, this.actions);
        }
        catch (final NoSuchMethodException ex4) {
            if (UnresolvedPermission.debug != null) {
                UnresolvedPermission.debug.println("NoSuchMethodException:\n  could not find proper constructor for " + this.type);
                ex4.printStackTrace();
            }
            return null;
        }
        catch (final Exception ex5) {
            if (UnresolvedPermission.debug != null) {
                UnresolvedPermission.debug.println("unable to instantiate " + this.name);
                ex5.printStackTrace();
            }
            return null;
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UnresolvedPermission)) {
            return false;
        }
        final UnresolvedPermission unresolvedPermission = (UnresolvedPermission)o;
        if (!this.type.equals(unresolvedPermission.type)) {
            return false;
        }
        if (this.name == null) {
            if (unresolvedPermission.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(unresolvedPermission.name)) {
            return false;
        }
        if (this.actions == null) {
            if (unresolvedPermission.actions != null) {
                return false;
            }
        }
        else if (!this.actions.equals(unresolvedPermission.actions)) {
            return false;
        }
        if ((this.certs == null && unresolvedPermission.certs != null) || (this.certs != null && unresolvedPermission.certs == null) || (this.certs != null && unresolvedPermission.certs != null && this.certs.length != unresolvedPermission.certs.length)) {
            return false;
        }
        for (int n = 0; this.certs != null && n < this.certs.length; ++n) {
            boolean b = false;
            for (int i = 0; i < unresolvedPermission.certs.length; ++i) {
                if (this.certs[n].equals(unresolvedPermission.certs[i])) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                return false;
            }
        }
        for (int n2 = 0; unresolvedPermission.certs != null && n2 < unresolvedPermission.certs.length; ++n2) {
            boolean b2 = false;
            for (int j = 0; j < this.certs.length; ++j) {
                if (unresolvedPermission.certs[n2].equals(this.certs[j])) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.type.hashCode();
        if (this.name != null) {
            hashCode ^= this.name.hashCode();
        }
        if (this.actions != null) {
            hashCode ^= this.actions.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public String getActions() {
        return "";
    }
    
    public String getUnresolvedType() {
        return this.type;
    }
    
    public String getUnresolvedName() {
        return this.name;
    }
    
    public String getUnresolvedActions() {
        return this.actions;
    }
    
    public Certificate[] getUnresolvedCerts() {
        return (Certificate[])((this.certs == null) ? null : ((Certificate[])this.certs.clone()));
    }
    
    @Override
    public String toString() {
        return "(unresolved " + this.type + " " + this.name + " " + this.actions + ")";
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new UnresolvedPermissionCollection();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.certs == null || this.certs.length == 0) {
            objectOutputStream.writeInt(0);
        }
        else {
            objectOutputStream.writeInt(this.certs.length);
            for (int i = 0; i < this.certs.length; ++i) {
                final Certificate certificate = this.certs[i];
                try {
                    objectOutputStream.writeUTF(certificate.getType());
                    final byte[] encoded = certificate.getEncoded();
                    objectOutputStream.writeInt(encoded.length);
                    objectOutputStream.write(encoded);
                }
                catch (final CertificateEncodingException ex) {
                    throw new IOException(ex.getMessage());
                }
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        Hashtable hashtable = null;
        ArrayList list = null;
        objectInputStream.defaultReadObject();
        if (this.type == null) {
            throw new NullPointerException("type can't be null");
        }
        final int int1 = objectInputStream.readInt();
        if (int1 > 0) {
            hashtable = new Hashtable(3);
            list = new ArrayList<Certificate>((int1 > 20) ? 20 : int1);
        }
        else if (int1 < 0) {
            throw new IOException("size cannot be negative");
        }
        for (int i = 0; i < int1; ++i) {
            final String utf = objectInputStream.readUTF();
            CertificateFactory instance;
            if (hashtable.containsKey(utf)) {
                instance = (CertificateFactory)hashtable.get(utf);
            }
            else {
                try {
                    instance = CertificateFactory.getInstance(utf);
                }
                catch (final CertificateException ex) {
                    throw new ClassNotFoundException("Certificate factory for " + utf + " not found");
                }
                hashtable.put(utf, instance);
            }
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.readExactlyNBytes(objectInputStream, objectInputStream.readInt()));
            try {
                list.add(instance.generateCertificate(byteArrayInputStream));
            }
            catch (final CertificateException ex2) {
                throw new IOException(ex2.getMessage());
            }
            byteArrayInputStream.close();
        }
        if (list != null) {
            this.certs = list.toArray(new Certificate[int1]);
        }
    }
    
    static {
        debug = Debug.getInstance("policy,access", "UnresolvedPermission");
        PARAMS0 = new Class[0];
        PARAMS1 = new Class[] { String.class };
        PARAMS2 = new Class[] { String.class, String.class };
    }
}
