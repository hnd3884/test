package sun.security.util;

import sun.security.jca.Providers;
import java.security.Provider;
import java.util.jar.JarException;
import java.util.Hashtable;
import java.io.IOException;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.security.CodeSigner;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.util.HashMap;

public class ManifestEntryVerifier
{
    private static final Debug debug;
    HashMap<String, MessageDigest> createdDigests;
    ArrayList<MessageDigest> digests;
    ArrayList<byte[]> manifestHashes;
    private String name;
    private Manifest man;
    private boolean skip;
    private JarEntry entry;
    private CodeSigner[] signers;
    private static final char[] hexc;
    
    public ManifestEntryVerifier(final Manifest man) {
        this.name = null;
        this.skip = true;
        this.signers = null;
        this.createdDigests = new HashMap<String, MessageDigest>(11);
        this.digests = new ArrayList<MessageDigest>();
        this.manifestHashes = new ArrayList<byte[]>();
        this.man = man;
    }
    
    public void setEntry(final String name, final JarEntry entry) throws IOException {
        this.digests.clear();
        this.manifestHashes.clear();
        this.name = name;
        this.entry = entry;
        this.skip = true;
        this.signers = null;
        if (this.man == null || name == null) {
            return;
        }
        this.skip = false;
        Attributes attributes = this.man.getAttributes(name);
        if (attributes == null) {
            attributes = this.man.getAttributes("./" + name);
            if (attributes == null) {
                attributes = this.man.getAttributes("/" + name);
                if (attributes == null) {
                    return;
                }
            }
        }
        for (final Map.Entry entry2 : attributes.entrySet()) {
            final String string = entry2.getKey().toString();
            if (string.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
                final String substring = string.substring(0, string.length() - 7);
                MessageDigest instance = this.createdDigests.get(substring);
                if (instance == null) {
                    try {
                        instance = MessageDigest.getInstance(substring, SunProviderHolder.instance);
                        this.createdDigests.put(substring, instance);
                    }
                    catch (final NoSuchAlgorithmException ex) {}
                }
                if (instance == null) {
                    continue;
                }
                instance.reset();
                this.digests.add(instance);
                this.manifestHashes.add(Base64.getMimeDecoder().decode((String)entry2.getValue()));
            }
        }
    }
    
    public void update(final byte b) {
        if (this.skip) {
            return;
        }
        for (int i = 0; i < this.digests.size(); ++i) {
            this.digests.get(i).update(b);
        }
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        if (this.skip) {
            return;
        }
        for (int i = 0; i < this.digests.size(); ++i) {
            this.digests.get(i).update(array, n, n2);
        }
    }
    
    public JarEntry getEntry() {
        return this.entry;
    }
    
    public CodeSigner[] verify(final Hashtable<String, CodeSigner[]> hashtable, final Hashtable<String, CodeSigner[]> hashtable2) throws JarException {
        if (this.skip) {
            return null;
        }
        if (this.digests.isEmpty()) {
            throw new SecurityException("digest missing for " + this.name);
        }
        if (this.signers != null) {
            return this.signers;
        }
        for (int i = 0; i < this.digests.size(); ++i) {
            final MessageDigest messageDigest = this.digests.get(i);
            final byte[] array = this.manifestHashes.get(i);
            final byte[] digest = messageDigest.digest();
            if (ManifestEntryVerifier.debug != null) {
                ManifestEntryVerifier.debug.println("Manifest Entry: " + this.name + " digest=" + messageDigest.getAlgorithm());
                ManifestEntryVerifier.debug.println("  manifest " + toHex(array));
                ManifestEntryVerifier.debug.println("  computed " + toHex(digest));
                ManifestEntryVerifier.debug.println();
            }
            if (!MessageDigest.isEqual(digest, array)) {
                throw new SecurityException(messageDigest.getAlgorithm() + " digest error for " + this.name);
            }
        }
        this.signers = hashtable2.remove(this.name);
        if (this.signers != null) {
            hashtable.put(this.name, this.signers);
        }
        return this.signers;
    }
    
    static String toHex(final byte[] array) {
        final StringBuffer sb = new StringBuffer(array.length * 2);
        for (int i = 0; i < array.length; ++i) {
            sb.append(ManifestEntryVerifier.hexc[array[i] >> 4 & 0xF]);
            sb.append(ManifestEntryVerifier.hexc[array[i] & 0xF]);
        }
        return sb.toString();
    }
    
    static {
        debug = Debug.getInstance("jar");
        hexc = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    private static class SunProviderHolder
    {
        private static final Provider instance;
        
        static {
            instance = Providers.getSunProvider();
        }
    }
}
