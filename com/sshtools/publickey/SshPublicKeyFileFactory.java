package com.sshtools.publickey;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.components.SshPublicKey;

public class SshPublicKeyFileFactory
{
    public static final int OPENSSH_FORMAT = 0;
    public static final int SECSH_FORMAT = 1;
    public static final int SSH1_FORMAT = 2;
    
    public static SshPublicKey decodeSSH2PublicKey(final byte[] array) throws IOException {
        try {
            final String string = new ByteArrayReader(array).readString();
            try {
                final SshPublicKey sshPublicKey = (SshPublicKey)ComponentManager.getInstance().supportedPublicKeys().getInstance(string);
                sshPublicKey.init(array, 0, array.length);
                return sshPublicKey;
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            throw new IOException("An error occurred parsing a public key file! Is the file corrupt?");
        }
    }
    
    public static SshPublicKey decodeSSH2PublicKey(final String s, final byte[] array) throws IOException {
        try {
            final SshPublicKey sshPublicKey = (SshPublicKey)ComponentManager.getInstance().supportedPublicKeys().getInstance(s);
            sshPublicKey.init(array, 0, array.length);
            return sshPublicKey;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public static SshPublicKeyFile parse(final byte[] array) throws IOException {
        try {
            try {
                return new OpenSSHPublicKeyFile(array);
            }
            catch (final IOException ex) {
                try {
                    return new SECSHPublicKeyFile(array);
                }
                catch (final IOException ex2) {
                    try {
                        return new f(array);
                    }
                    catch (final Exception ex3) {
                        throw new IOException("Unable to parse key, format could not be identified");
                    }
                }
            }
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            throw new IOException("An error occurred parsing a public key file! Is the file corrupt?");
        }
    }
    
    public static SshPublicKeyFile parse(final InputStream inputStream) throws IOException {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            while ((read = inputStream.read()) > -1) {
                byteArrayOutputStream.write(read);
            }
            return parse(byteArrayOutputStream.toByteArray());
        }
        finally {
            try {
                inputStream.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static SshPublicKeyFile create(final SshPublicKey sshPublicKey, final String s, final int n) throws IOException {
        switch (n) {
            case 0: {
                return new OpenSSHPublicKeyFile(sshPublicKey, s);
            }
            case 1: {
                return new SECSHPublicKeyFile(sshPublicKey, s);
            }
            case 2: {
                return new f(sshPublicKey);
            }
            default: {
                throw new IOException("Invalid format type specified!");
            }
        }
    }
    
    public static void createFile(final SshPublicKey sshPublicKey, final String s, final int n, final File file) throws IOException {
        final SshPublicKeyFile create = create(sshPublicKey, s, n);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(create.getFormattedKey());
            fileOutputStream.flush();
        }
        finally {
            fileOutputStream.close();
        }
    }
    
    public static void convertFile(final File file, final int n, final File file2) throws IOException {
        final SshPublicKeyFile parse = parse(new FileInputStream(file));
        createFile(parse.toPublicKey(), parse.getComment(), n, file2);
    }
}
