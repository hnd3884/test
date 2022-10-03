package com.sshtools.publickey;

import com.maverick.ssh.components.SshRsaPublicKey;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Enumeration;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.maverick.ssh.components.SshHmac;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.maverick.util.Base64;
import com.maverick.ssh.components.SshPublicKey;
import java.math.BigInteger;
import com.maverick.ssh.components.ComponentManager;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.maverick.ssh.SshException;
import java.io.File;
import java.util.Hashtable;
import com.maverick.ssh.HostKeyVerification;

public abstract class AbstractKnownHostsKeyVerification implements HostKeyVerification
{
    private Hashtable g;
    private Hashtable f;
    private String e;
    private boolean b;
    private boolean d;
    private File c;
    
    public AbstractKnownHostsKeyVerification() throws SshException {
        this(null);
    }
    
    public File getKnownHostsFile() {
        return this.c;
    }
    
    public AbstractKnownHostsKeyVerification(String absolutePath) throws SshException {
        this.g = new Hashtable();
        this.f = new Hashtable();
        this.d = true;
        InputStream inputStream = null;
        if (absolutePath == null) {
            String property = "";
            try {
                property = System.getProperty("user.home");
            }
            catch (final SecurityException ex) {}
            this.c = new File(property, ".ssh" + File.separator + "known_hosts");
            absolutePath = this.c.getAbsolutePath();
        }
        else {
            this.c = new File(absolutePath);
        }
        try {
            if (System.getSecurityManager() != null) {
                System.getSecurityManager().checkRead(absolutePath);
            }
            if (this.c.exists()) {
                inputStream = new FileInputStream(this.c);
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String trim = line.trim();
                    if (!trim.equals("")) {
                        final StringTokenizer stringTokenizer = new StringTokenizer(trim, " ");
                        if (!stringTokenizer.hasMoreTokens()) {
                            this.onInvalidHostEntry(trim);
                        }
                        else {
                            final String s = (String)stringTokenizer.nextElement();
                            String s2 = null;
                            try {
                                if (!stringTokenizer.hasMoreTokens()) {
                                    this.onInvalidHostEntry(trim);
                                }
                                else {
                                    Integer.parseInt(s2 = (String)stringTokenizer.nextElement());
                                    if (!stringTokenizer.hasMoreTokens()) {
                                        this.onInvalidHostEntry(trim);
                                    }
                                    else {
                                        final String s3 = (String)stringTokenizer.nextElement();
                                        if (!stringTokenizer.hasMoreTokens()) {
                                            this.onInvalidHostEntry(trim);
                                        }
                                        else {
                                            this.c(s, ComponentManager.getInstance().createRsaPublicKey(new BigInteger((String)stringTokenizer.nextElement()), new BigInteger(s3), 1), true);
                                        }
                                    }
                                }
                            }
                            catch (final OutOfMemoryError outOfMemoryError) {
                                throw new SshException("Error parsing known_hosts file, is your file corrupt? " + this.c.getAbsolutePath(), 17);
                            }
                            catch (final NumberFormatException ex2) {
                                if (!stringTokenizer.hasMoreTokens()) {
                                    this.onInvalidHostEntry(trim);
                                }
                                else {
                                    final String s4 = (String)stringTokenizer.nextElement();
                                    try {
                                        SshPublicKey sshPublicKey;
                                        if (s2 != null) {
                                            sshPublicKey = SshPublicKeyFileFactory.decodeSSH2PublicKey(s2, Base64.decode(s4));
                                        }
                                        else {
                                            sshPublicKey = SshPublicKeyFileFactory.decodeSSH2PublicKey(Base64.decode(s4));
                                        }
                                        this.c(s, sshPublicKey, true);
                                    }
                                    catch (final IOException ex3) {
                                        this.onInvalidHostEntry(trim);
                                    }
                                    catch (final OutOfMemoryError outOfMemoryError2) {
                                        throw new SshException("Error parsing known_hosts file, is your file corrupt? " + this.c.getAbsolutePath(), 17);
                                    }
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
                inputStream.close();
                this.b = this.c.canWrite();
            }
            else {
                new File(this.c.getParent()).mkdirs();
                final FileOutputStream fileOutputStream = new FileOutputStream(this.c);
                fileOutputStream.write(this.toString().getBytes());
                fileOutputStream.close();
                this.b = true;
            }
            this.e = absolutePath;
        }
        catch (final IOException ex4) {
            this.b = false;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex5) {}
            }
        }
    }
    
    public void setHashHosts(final boolean d) {
        this.d = d;
    }
    
    protected void onInvalidHostEntry(final String s) throws SshException {
    }
    
    public boolean isHostFileWriteable() {
        return this.b;
    }
    
    public abstract void onHostKeyMismatch(final String p0, final SshPublicKey p1, final SshPublicKey p2) throws SshException;
    
    public abstract void onUnknownHost(final String p0, final SshPublicKey p1) throws SshException;
    
    public void allowHost(final String s, final SshPublicKey sshPublicKey, final boolean b) throws SshException {
        if (this.d) {
            final SshHmac sshHmac = (SshHmac)ComponentManager.getInstance().supportedHMacsCS().getInstance("hmac-sha1");
            final byte[] array = new byte[sshHmac.getMacLength()];
            ComponentManager.getInstance().getRND().nextBytes(array);
            sshHmac.init(array);
            sshHmac.update(s.getBytes());
            this.c("|1|" + Base64.encodeBytes(array, false) + "|" + Base64.encodeBytes(sshHmac.doFinal(), false), sshPublicKey, b);
        }
        else {
            this.c(s, sshPublicKey, b);
        }
        if (b) {
            try {
                this.saveHostFile();
            }
            catch (final IOException ex) {
                throw new SshException("knownhosts file could not be saved! " + ex.getMessage(), 5);
            }
        }
    }
    
    public Hashtable allowedHosts() {
        return this.g;
    }
    
    public synchronized void removeAllowedHost(final String s) {
        if (this.g.containsKey(s)) {
            this.g.remove(s);
        }
    }
    
    public boolean verifyHost(final String s, final SshPublicKey sshPublicKey) throws SshException {
        return this.b(s, sshPublicKey, true);
    }
    
    private synchronized boolean b(final String s, final SshPublicKey sshPublicKey, final boolean b) throws SshException {
        Object hostName = null;
        String hostAddress = null;
        if (System.getProperty("maverick.knownHosts.enableReverseDNS", "true").equalsIgnoreCase("true")) {
            try {
                final InetAddress byName = InetAddress.getByName(s);
                hostName = byName.getHostName();
                hostAddress = byName.getHostAddress();
            }
            catch (final UnknownHostException ex) {}
        }
        final Enumeration keys = this.g.keys();
        while (keys.hasMoreElements()) {
            final String s2 = (String)keys.nextElement();
            if (s2.startsWith("|1|")) {
                if (this.c(s2, s)) {
                    return this.b(s2, sshPublicKey);
                }
                if (hostAddress != null && this.c(s2, hostAddress)) {
                    return this.b(s2, sshPublicKey);
                }
            }
            else if (s2.equals(s)) {
                return this.b(s2, sshPublicKey);
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(s2, ",");
            while (stringTokenizer.hasMoreElements()) {
                final String s3 = (String)stringTokenizer.nextElement();
                if ((hostName != null && s3.equals(hostName)) || (hostAddress != null && s3.equals(hostAddress))) {
                    return this.b(s2, sshPublicKey);
                }
            }
        }
        final Enumeration keys2 = this.f.keys();
        while (keys2.hasMoreElements()) {
            final String s4 = (String)keys2.nextElement();
            if (s4.startsWith("|1|")) {
                if (this.c(s4, s)) {
                    return this.b(s4, sshPublicKey);
                }
                if (hostAddress != null && this.c(s4, hostAddress)) {
                    return this.b(s4, sshPublicKey);
                }
            }
            else if (s4.equals(s)) {
                return this.b(s4, sshPublicKey);
            }
            final StringTokenizer stringTokenizer2 = new StringTokenizer(s4, ",");
            while (stringTokenizer2.hasMoreElements()) {
                final String s5 = (String)stringTokenizer2.nextElement();
                if ((hostName != null && s5.equals(hostName)) || (hostAddress != null && s5.equals(hostAddress))) {
                    return this.b(s4, sshPublicKey);
                }
            }
        }
        if (!b) {
            return false;
        }
        this.onUnknownHost(s, sshPublicKey);
        return this.b(s, sshPublicKey, false);
    }
    
    private boolean c(final String s, final String s2) throws SshException {
        final SshHmac sshHmac = (SshHmac)ComponentManager.getInstance().supportedHMacsCS().getInstance("hmac-sha1");
        final String substring = s.substring("|1|".length());
        final String substring2 = substring.substring(0, substring.indexOf("|"));
        final byte[] decode = Base64.decode(substring.substring(substring.indexOf("|") + 1));
        sshHmac.init(Base64.decode(substring2));
        sshHmac.update(s2.getBytes());
        return Arrays.equals(decode, sshHmac.doFinal());
    }
    
    private boolean b(final String s, final SshPublicKey sshPublicKey) throws SshException {
        final SshPublicKey b = this.b(s, sshPublicKey.getAlgorithm());
        if (b != null && sshPublicKey.equals(b)) {
            return true;
        }
        if (b == null) {
            this.onUnknownHost(s, sshPublicKey);
        }
        else {
            this.onHostKeyMismatch(s, b, sshPublicKey);
        }
        return this.c(s, sshPublicKey);
    }
    
    private boolean c(final String s, final SshPublicKey sshPublicKey) {
        final SshPublicKey b = this.b(s, sshPublicKey.getAlgorithm());
        return b != null && b.equals(sshPublicKey);
    }
    
    private synchronized SshPublicKey b(final String s, final String s2) {
        try {
            final Iterator iterator = this.f.keySet().iterator();
            while (iterator.hasNext()) {
                final String s3 = (String)iterator.next();
                if (s3.startsWith("|") && this.c(s3, s)) {
                    return (SshPublicKey)((Hashtable)this.f.get(s3)).get(s2);
                }
            }
        }
        catch (final SshException ex) {}
        if (this.f.containsKey(s)) {
            return (SshPublicKey)this.f.get(s).get(s2);
        }
        try {
            final Iterator iterator2 = this.g.keySet().iterator();
            while (iterator2.hasNext()) {
                final String s4 = (String)iterator2.next();
                if (s4.startsWith("|") && this.c(s4, s)) {
                    return (SshPublicKey)((Hashtable)this.g.get(s4)).get(s2);
                }
            }
        }
        catch (final SshException ex2) {}
        if (this.g.containsKey(s)) {
            return (SshPublicKey)this.g.get(s).get(s2);
        }
        return null;
    }
    
    private synchronized void c(final String s, final SshPublicKey sshPublicKey, final boolean b) {
        if (b) {
            if (!this.g.containsKey(s)) {
                this.g.put(s, new Hashtable<String, Hashtable>());
            }
            this.g.get(s).put(sshPublicKey.getAlgorithm(), sshPublicKey);
        }
        else {
            if (!this.f.containsKey(s)) {
                this.f.put(s, new Hashtable<String, Hashtable>());
            }
            this.f.get(s).put(sshPublicKey.getAlgorithm(), sshPublicKey);
        }
    }
    
    public synchronized void saveHostFile() throws IOException {
        if (!this.b) {
            throw new IOException("Host file is not writeable.");
        }
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(new File(this.e));
            fileOutputStream.write(this.toString().getBytes());
            fileOutputStream.close();
        }
        catch (final IOException ex) {
            throw new IOException("Could not write to " + this.e);
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("");
        final String property = System.getProperty("line.separator");
        final Enumeration keys = this.g.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final Hashtable hashtable = this.g.get(s);
            final Enumeration keys2 = hashtable.keys();
            while (keys2.hasMoreElements()) {
                final SshPublicKey sshPublicKey = (SshPublicKey)hashtable.get(keys2.nextElement());
                if (sshPublicKey instanceof SshRsaPublicKey && ((SshRsaPublicKey)sshPublicKey).getVersion() == 1) {
                    final SshRsaPublicKey sshRsaPublicKey = (SshRsaPublicKey)sshPublicKey;
                    sb.append(s + " " + String.valueOf(sshRsaPublicKey.getModulus().bitLength()) + " " + sshRsaPublicKey.getPublicExponent() + " " + sshRsaPublicKey.getModulus() + property);
                }
                else {
                    try {
                        sb.append(s + " " + sshPublicKey.getAlgorithm() + " " + Base64.encodeBytes(sshPublicKey.getEncoded(), true) + property);
                    }
                    catch (final SshException ex) {}
                }
            }
        }
        return sb.toString();
    }
}
