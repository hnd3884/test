package javax.crypto;

import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Hashtable;
import java.io.ObjectInputStream;
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;
import java.security.Permission;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.PermissionCollection;

final class CryptoPermissions extends PermissionCollection implements Serializable
{
    private static final long serialVersionUID = 4946547168093391015L;
    private static final ObjectStreamField[] serialPersistentFields;
    private transient ConcurrentHashMap<String, PermissionCollection> perms;
    
    CryptoPermissions() {
        this.perms = new ConcurrentHashMap<String, PermissionCollection>(7);
    }
    
    void load(final InputStream inputStream) throws IOException, CryptoPolicyParser.ParsingException {
        final CryptoPolicyParser cryptoPolicyParser = new CryptoPolicyParser();
        cryptoPolicyParser.read(new BufferedReader(new InputStreamReader(inputStream, "UTF-8")));
        final CryptoPermission[] permissions = cryptoPolicyParser.getPermissions();
        for (int i = 0; i < permissions.length; ++i) {
            this.add(permissions[i]);
        }
    }
    
    boolean isEmpty() {
        return this.perms.isEmpty();
    }
    
    @Override
    public void add(final Permission permission) {
        if (this.isReadOnly()) {
            throw new SecurityException("Attempt to add a Permission to a readonly CryptoPermissions object");
        }
        if (!(permission instanceof CryptoPermission)) {
            return;
        }
        final CryptoPermission cryptoPermission = (CryptoPermission)permission;
        final PermissionCollection permissionCollection = this.getPermissionCollection(cryptoPermission);
        permissionCollection.add(cryptoPermission);
        this.perms.putIfAbsent(cryptoPermission.getAlgorithm(), permissionCollection);
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof CryptoPermission)) {
            return false;
        }
        final CryptoPermission cryptoPermission = (CryptoPermission)permission;
        return this.getPermissionCollection(cryptoPermission.getAlgorithm()).implies(cryptoPermission);
    }
    
    @Override
    public Enumeration<Permission> elements() {
        return new PermissionsEnumerator(this.perms.elements());
    }
    
    CryptoPermissions getMinimum(final CryptoPermissions cryptoPermissions) {
        if (cryptoPermissions == null) {
            return null;
        }
        if (this.perms.containsKey("CryptoAllPermission")) {
            return cryptoPermissions;
        }
        if (cryptoPermissions.perms.containsKey("CryptoAllPermission")) {
            return this;
        }
        final CryptoPermissions cryptoPermissions2 = new CryptoPermissions();
        final PermissionCollection collection = cryptoPermissions.perms.get("*");
        int maxKeySize = 0;
        if (collection != null) {
            maxKeySize = collection.elements().nextElement().getMaxKeySize();
        }
        final Enumeration<String> keys = this.perms.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            final PermissionCollection collection2 = this.perms.get(s);
            final PermissionCollection collection3 = cryptoPermissions.perms.get(s);
            CryptoPermission[] array;
            if (collection3 == null) {
                if (collection == null) {
                    continue;
                }
                array = this.getMinimum(maxKeySize, collection2);
            }
            else {
                array = this.getMinimum(collection2, collection3);
            }
            for (int i = 0; i < array.length; ++i) {
                cryptoPermissions2.add(array[i]);
            }
        }
        final PermissionCollection collection4 = this.perms.get("*");
        if (collection4 == null) {
            return cryptoPermissions2;
        }
        final int maxKeySize2 = collection4.elements().nextElement().getMaxKeySize();
        final Enumeration<String> keys2 = cryptoPermissions.perms.keys();
        while (keys2.hasMoreElements()) {
            final String s2 = keys2.nextElement();
            if (this.perms.containsKey(s2)) {
                continue;
            }
            final CryptoPermission[] minimum = this.getMinimum(maxKeySize2, cryptoPermissions.perms.get(s2));
            for (int j = 0; j < minimum.length; ++j) {
                cryptoPermissions2.add(minimum[j]);
            }
        }
        return cryptoPermissions2;
    }
    
    private CryptoPermission[] getMinimum(final PermissionCollection collection, final PermissionCollection collection2) {
        final Vector vector = new Vector(2);
        final Enumeration<Permission> elements = collection.elements();
        while (elements.hasMoreElements()) {
            final CryptoPermission cryptoPermission = elements.nextElement();
            final Enumeration<Permission> elements2 = collection2.elements();
            while (elements2.hasMoreElements()) {
                final CryptoPermission cryptoPermission2 = elements2.nextElement();
                if (cryptoPermission2.implies(cryptoPermission)) {
                    vector.addElement(cryptoPermission);
                    break;
                }
                if (!cryptoPermission.implies(cryptoPermission2)) {
                    continue;
                }
                vector.addElement(cryptoPermission2);
            }
        }
        final CryptoPermission[] array = new CryptoPermission[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    private CryptoPermission[] getMinimum(final int n, final PermissionCollection collection) {
        final Vector vector = new Vector(1);
        final Enumeration<Permission> elements = collection.elements();
        while (elements.hasMoreElements()) {
            final CryptoPermission cryptoPermission = elements.nextElement();
            if (cryptoPermission.getMaxKeySize() <= n) {
                vector.addElement(cryptoPermission);
            }
            else if (cryptoPermission.getCheckParam()) {
                vector.addElement(new CryptoPermission(cryptoPermission.getAlgorithm(), n, cryptoPermission.getAlgorithmParameterSpec(), cryptoPermission.getExemptionMechanism()));
            }
            else {
                vector.addElement(new CryptoPermission(cryptoPermission.getAlgorithm(), n, cryptoPermission.getExemptionMechanism()));
            }
        }
        final CryptoPermission[] array = new CryptoPermission[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    PermissionCollection getPermissionCollection(final String s) {
        PermissionCollection collection = this.perms.get("CryptoAllPermission");
        if (collection == null) {
            collection = this.perms.get(s);
            if (collection == null) {
                collection = this.perms.get("*");
            }
        }
        return collection;
    }
    
    private PermissionCollection getPermissionCollection(final CryptoPermission cryptoPermission) {
        PermissionCollection permissionCollection = this.perms.get(cryptoPermission.getAlgorithm());
        if (permissionCollection == null) {
            permissionCollection = cryptoPermission.newPermissionCollection();
        }
        return permissionCollection;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Hashtable hashtable = (Hashtable)objectInputStream.readFields().get("perms", null);
        if (hashtable != null) {
            this.perms = new ConcurrentHashMap<String, PermissionCollection>(hashtable);
        }
        else {
            this.perms = new ConcurrentHashMap<String, PermissionCollection>();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.putFields().put("perms", new Hashtable(this.perms));
        objectOutputStream.writeFields();
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("perms", Hashtable.class) };
    }
}
