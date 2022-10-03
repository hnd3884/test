package com.sshtools.publickey;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import com.maverick.ssh.components.SshKeyPair;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class SshPrivateKeyFileFactory
{
    public static final int OPENSSH_FORMAT = 0;
    public static final int SSHTOOLS_FORMAT = 1;
    public static final int SSH1_FORMAT = 3;
    
    public static SshPrivateKeyFile parse(final byte[] array) throws IOException {
        try {
            if (j.d(array)) {
                return new j(array);
            }
            if (Base64EncodedFileFormat.isFormatted(array, i.i, i.k)) {
                return new i(array);
            }
            if (d.b(array)) {
                return new d(array);
            }
            if (g.c(array)) {
                return new g(array);
            }
            if (h.e(array)) {
                return new h(array);
            }
            throw new IOException("A suitable key format could not be found!");
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            throw new IOException("An error occurred parsing a private key file! Is the file corrupt?");
        }
    }
    
    public static SshPrivateKeyFile parse(final InputStream inputStream) throws IOException {
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
    
    public static SshPrivateKeyFile create(final SshKeyPair sshKeyPair, final String s, final String s2, final int n) throws IOException {
        if (!(sshKeyPair.getPrivateKey() instanceof SshRsaPrivateCrtKey) && n == 3) {
            throw new IOException("SSH1 format requires rsa key pair!");
        }
        switch (n) {
            case 0: {
                return new j(sshKeyPair, s);
            }
            case 1: {
                return new i(sshKeyPair, s, s2);
            }
            case 3: {
                return new d(sshKeyPair, s, s2);
            }
            default: {
                throw new IOException("Invalid key format!");
            }
        }
    }
    
    public static void createFile(final SshKeyPair sshKeyPair, final String s, final String s2, final int n, final File file) throws IOException {
        final SshPrivateKeyFile create = create(sshKeyPair, s, s2, n);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(create.getFormattedKey());
            fileOutputStream.flush();
        }
        finally {
            fileOutputStream.close();
        }
    }
    
    public static void convertFile(final File file, final String s, final String s2, final int n, final File file2) throws IOException, InvalidPassphraseException {
        createFile(parse(new FileInputStream(file)).toKeyPair(s), s, s2, n, file2);
    }
}
