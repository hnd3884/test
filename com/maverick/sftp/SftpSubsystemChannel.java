package com.maverick.sftp;

import com.maverick.ssh.message.Message;
import com.maverick.ssh.message.MessageHolder;
import com.maverick.events.Event;
import com.maverick.events.EventServiceImplementation;
import java.util.StringTokenizer;
import java.io.OutputStream;
import java.util.Enumeration;
import java.io.EOFException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import com.maverick.util.UnsignedInteger64;
import java.io.UnsupportedEncodingException;
import com.maverick.ssh.Packet;
import java.io.IOException;
import com.maverick.ssh.SshIOException;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshChannel;
import com.maverick.ssh.SshSession;
import java.util.Hashtable;
import com.maverick.util.UnsignedInteger32;
import java.util.Vector;
import com.maverick.ssh.SubsystemChannel;

public class SftpSubsystemChannel extends SubsystemChannel
{
    private String p;
    public static final int OPEN_READ = 1;
    public static final int OPEN_WRITE = 2;
    public static final int OPEN_APPEND = 4;
    public static final int OPEN_CREATE = 8;
    public static final int OPEN_TRUNCATE = 16;
    public static final int OPEN_EXCLUSIVE = 32;
    public static final int OPEN_TEXT = 64;
    public static int MAX_VERSION;
    int i;
    int l;
    int j;
    Vector o;
    UnsignedInteger32 h;
    Hashtable k;
    _b n;
    Hashtable m;
    
    public SftpSubsystemChannel(final SshSession sshSession) throws SshException {
        super(sshSession);
        this.p = "ISO-8859-1";
        this.i = 4;
        this.l = -1;
        this.j = -1;
        this.o = new Vector();
        this.h = new UnsignedInteger32(0L);
        this.k = new Hashtable();
        this.n = new _b();
        this.m = new Hashtable();
        this.i = SftpSubsystemChannel.MAX_VERSION;
    }
    
    public SftpSubsystemChannel(final SshSession sshSession, final int thisMaxSftpVersion) throws SshException {
        super(sshSession);
        this.p = "ISO-8859-1";
        this.i = 4;
        this.l = -1;
        this.j = -1;
        this.o = new Vector();
        this.h = new UnsignedInteger32(0L);
        this.k = new Hashtable();
        this.n = new _b();
        this.m = new Hashtable();
        this.setThisMaxSftpVersion(thisMaxSftpVersion);
    }
    
    public static void setMaxSftpVersion(final int max_VERSION) {
        SftpSubsystemChannel.MAX_VERSION = max_VERSION;
    }
    
    public void setThisMaxSftpVersion(final int i) {
        this.i = i;
    }
    
    public int getVersion() {
        return this.l;
    }
    
    public String getCanonicalNewline() throws SftpStatusException {
        if (this.l <= 3) {
            throw new SftpStatusException(8, "Newline setting not available for SFTP versions <= 3");
        }
        if (!this.m.containsKey("newline")) {
            return "\r\n";
        }
        return this.m.get("newline");
    }
    
    public void initialize() throws SshException, UnsupportedEncodingException {
        try {
            super.channel.getMessageRouter().addShutdownHook(new Runnable() {
                public void run() {
                    try {
                        SftpSubsystemChannel.this.k.clear();
                        SftpSubsystemChannel.this.o.clear();
                    }
                    catch (final Throwable t) {}
                }
            });
            final Packet packet = this.createPacket();
            packet.write(1);
            packet.writeInt(this.i);
            this.sendMessage(packet);
            final byte[] nextMessage = this.nextMessage();
            if (nextMessage[0] != 2) {
                this.close();
                throw new SshException("Unexpected response from SFTP subsystem.", 6);
            }
            final ByteArrayReader byteArrayReader = new ByteArrayReader(nextMessage);
            byteArrayReader.skip(1L);
            this.j = (int)byteArrayReader.readInt();
            this.l = Math.min(this.j, SftpSubsystemChannel.MAX_VERSION);
            try {
                while (byteArrayReader.available() > 0) {
                    this.m.put(byteArrayReader.readString(), byteArrayReader.readString());
                }
            }
            catch (final Throwable t) {}
            if (this.l <= 3) {
                this.setCharsetEncoding("ISO-8859-1");
            }
            else {
                this.setCharsetEncoding("UTF8");
            }
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(6, ex2);
        }
        catch (final Throwable t2) {
            throw new SshException(6, t2);
        }
    }
    
    public void setCharsetEncoding(final String p) throws SshException, UnsupportedEncodingException {
        if (this.l == -1) {
            throw new SshException("SFTP Channel must be initialized before setting character set encoding", 4);
        }
        "123456890".getBytes(p);
        this.p = p;
    }
    
    public int getServerVersion() {
        return this.j;
    }
    
    public String getCharsetEncoding() {
        return this.p;
    }
    
    public boolean supportsExtension(final String s) {
        return this.m.containsKey(s);
    }
    
    public String getExtension(final String s) {
        return this.m.get(s);
    }
    
    public SftpMessage sendExtensionMessage(final String s, final byte[] array) throws SshException, SftpStatusException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(200);
            packet.writeUINT32(b);
            packet.writeString(s);
            this.sendMessage(packet);
            return this.c(b);
        }
        catch (final IOException ex) {
            throw new SshException(5, ex);
        }
    }
    
    public void changePermissions(final SftpFile sftpFile, final int n) throws SftpStatusException, SshException {
        final SftpFileAttributes sftpFileAttributes = new SftpFileAttributes(this, 5);
        sftpFileAttributes.setPermissions(new UnsignedInteger32(n));
        this.setAttributes(sftpFile, sftpFileAttributes);
    }
    
    public void changePermissions(final String s, final int n) throws SftpStatusException, SshException {
        final SftpFileAttributes sftpFileAttributes = new SftpFileAttributes(this, 5);
        sftpFileAttributes.setPermissions(new UnsignedInteger32(n));
        this.setAttributes(s, sftpFileAttributes);
    }
    
    public void changePermissions(final String s, final String permissions) throws SftpStatusException, SshException {
        final SftpFileAttributes sftpFileAttributes = new SftpFileAttributes(this, 5);
        sftpFileAttributes.setPermissions(permissions);
        this.setAttributes(s, sftpFileAttributes);
    }
    
    public void setAttributes(final String s, final SftpFileAttributes sftpFileAttributes) throws SftpStatusException, SshException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(9);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            packet.write(sftpFileAttributes.toByteArray());
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2, 5);
        }
    }
    
    public void setAttributes(final SftpFile sftpFile, final SftpFileAttributes sftpFileAttributes) throws SftpStatusException, SshException {
        if (!this.c(sftpFile.getHandle())) {
            throw new SftpStatusException(100, "The handle is not an open file handle!");
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(10);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(sftpFile.getHandle());
            packet.write(sftpFileAttributes.toByteArray());
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public UnsignedInteger32 postWriteRequest(final byte[] array, final long n, final byte[] array2, final int n2, final int n3) throws SftpStatusException, SshException {
        if (!this.o.contains(new String(array))) {
            throw new SftpStatusException(100, "The handle is not valid!");
        }
        if (array2.length - n2 < n3) {
            throw new IndexOutOfBoundsException("Incorrect data array size!");
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(6);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(array);
            packet.writeUINT64(n);
            packet.writeBinaryString(array2, n2, n3);
            this.sendMessage(packet);
            return b;
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public void writeFile(final byte[] array, final UnsignedInteger64 unsignedInteger64, final byte[] array2, final int n, final int n2) throws SftpStatusException, SshException {
        this.getOKRequestStatus(this.postWriteRequest(array, unsignedInteger64.longValue(), array2, n, n2));
    }
    
    public void performOptimizedWrite(final byte[] array, final int n, final int n2, final InputStream inputStream, final int n3, final FileTransferProgress fileTransferProgress) throws SftpStatusException, SshException, TransferCancelledException {
        this.performOptimizedWrite(array, n, n2, inputStream, n3, fileTransferProgress, 0L);
    }
    
    public void performOptimizedWrite(final byte[] array, final int n, final int n2, final InputStream inputStream, int n3, final FileTransferProgress fileTransferProgress, final long n4) throws SftpStatusException, SshException, TransferCancelledException {
        try {
            if (!this.o.contains(new String(array))) {
                throw new SftpStatusException(100, "The file handle is invalid!");
            }
            if (n < 4096) {
                throw new SshException("Block size cannot be less than 4096", 4);
            }
            if (n4 < 0L) {
                throw new SshException("Position value must be greater than zero!", 4);
            }
            if (n4 > 0L && fileTransferProgress != null) {
                fileTransferProgress.progressed(n4);
            }
            if (n3 <= 0) {
                n3 = n;
            }
            final byte[] array2 = new byte[n];
            long n5 = n4;
            final Vector vector = new Vector();
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, n3);
            while (true) {
                final int read = bufferedInputStream.read(array2);
                if (read == -1) {
                    final Enumeration elements = vector.elements();
                    while (elements.hasMoreElements()) {
                        this.getOKRequestStatus((UnsignedInteger32)elements.nextElement());
                    }
                    vector.removeAllElements();
                    break;
                }
                vector.addElement(this.postWriteRequest(array, n5, array2, 0, read));
                n5 += read;
                if (fileTransferProgress != null) {
                    if (fileTransferProgress.isCancelled()) {
                        throw new TransferCancelledException();
                    }
                    fileTransferProgress.progressed(n5);
                }
                if (vector.size() <= n2) {
                    continue;
                }
                final UnsignedInteger32 unsignedInteger32 = (UnsignedInteger32)vector.elementAt(0);
                vector.removeElementAt(0);
                this.getOKRequestStatus(unsignedInteger32);
            }
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final EOFException ex2) {
            try {
                this.close();
            }
            catch (final SshIOException ex3) {
                throw ex3.getRealException();
            }
            catch (final IOException ex4) {
                throw new SshException(ex4.getMessage(), 6);
            }
            throw new SftpStatusException(7, "The SFTP channel terminated unexpectedly");
        }
        catch (final IOException ex5) {
            throw new SshException(ex5);
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            throw new SshException("Resource Shortage: try reducing the local file buffer size", 4);
        }
    }
    
    public void performOptimizedRead(final byte[] array, final long n, final int n2, final OutputStream outputStream, final int n3, final FileTransferProgress fileTransferProgress) throws SftpStatusException, SshException, TransferCancelledException {
        this.performOptimizedRead(array, n, n2, outputStream, n3, fileTransferProgress, 0L);
    }
    
    public void performOptimizedRead(final byte[] array, long n, int n2, final OutputStream outputStream, final int n3, final FileTransferProgress fileTransferProgress, long n4) throws SftpStatusException, SshException, TransferCancelledException {
        if (n <= 0L) {
            n = Long.MAX_VALUE;
        }
        try {
            if (!this.o.contains(new String(array))) {
                throw new SftpStatusException(100, "The file handle is invalid!");
            }
            if (n2 < 1 || n2 > 32768) {
                n2 = 32768;
            }
            if (n4 < 0L) {
                throw new SshException("Position value must be greater than zero!", 4);
            }
            final byte[] array2 = new byte[n2];
            final int file = this.readFile(array, new UnsignedInteger64(0L), array2, 0, array2.length);
            if (file == -1) {
                return;
            }
            if (file > n4) {
                outputStream.write(array2, (int)n4, (int)(file - n4));
                n -= file - n4;
                n4 = file;
            }
            if (n4 + n <= file) {
                return;
            }
            if (file < n2 && n > file) {
                n2 = file;
            }
            final long n5 = n / n2;
            long n6 = n3;
            if (n4 > 0L && fileTransferProgress != null) {
                fileTransferProgress.progressed(n4);
            }
            final Vector vector = new Vector<UnsignedInteger32>(n3);
            long n7 = n4;
            if (n5 < n6) {
                n6 = n5 + 1L;
            }
            if (n6 <= 0L) {
                n6 = 1L;
            }
            final long n8 = n5 + 2L;
            int n9 = 0;
            long n10 = n4;
            for (int n11 = 0; n11 < n6; ++n11) {
                vector.addElement(this.postReadRequest(array, n7, n2));
                n7 += n2;
                if (fileTransferProgress != null && fileTransferProgress.isCancelled()) {
                    throw new TransferCancelledException();
                }
            }
            while (true) {
                final UnsignedInteger32 unsignedInteger32 = (UnsignedInteger32)vector.elementAt(0);
                vector.removeElementAt(0);
                final SftpMessage c = this.c(unsignedInteger32);
                if (c.getType() == 103) {
                    final byte[] binaryString = c.readBinaryString();
                    outputStream.write(binaryString);
                    ++n9;
                    if (fileTransferProgress != null) {
                        fileTransferProgress.progressed(n10 += binaryString.length);
                    }
                    if (vector.isEmpty() || n9 + vector.size() < n8) {
                        vector.addElement(this.postReadRequest(array, n7, n2));
                        n7 += n2;
                    }
                    if (fileTransferProgress != null && fileTransferProgress.isCancelled()) {
                        throw new TransferCancelledException();
                    }
                    continue;
                }
                else {
                    if (c.getType() != 101) {
                        this.close();
                        throw new SshException("The server responded with an unexpected message", 6);
                    }
                    final int n12 = (int)c.readInt();
                    if (n12 == 1) {
                        return;
                    }
                    if (this.l >= 3) {
                        throw new SftpStatusException(n12, c.readString().trim());
                    }
                    throw new SftpStatusException(n12);
                }
            }
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final EOFException ex2) {
            try {
                this.close();
            }
            catch (final SshIOException ex3) {
                throw ex3.getRealException();
            }
            catch (final IOException ex4) {
                throw new SshException(ex4.getMessage(), 6);
            }
            throw new SftpStatusException(7, "The SFTP channel terminated unexpectedly");
        }
        catch (final IOException ex5) {
            throw new SshException(ex5);
        }
    }
    
    public void performSynchronousRead(final byte[] array, int n, final OutputStream outputStream, final FileTransferProgress fileTransferProgress, final long n2) throws SftpStatusException, SshException, TransferCancelledException {
        if (!this.o.contains(new String(array))) {
            throw new SftpStatusException(100, "The file handle is invalid!");
        }
        if (n < 1 || n > 32768) {
            n = 32768;
        }
        if (n2 < 0L) {
            throw new SshException("Position value must be greater than zero!", 4);
        }
        final byte[] array2 = new byte[n];
        UnsignedInteger64 add = new UnsignedInteger64(n2);
        if (n2 > 0L && fileTransferProgress != null) {
            fileTransferProgress.progressed(n2);
        }
        try {
            int file;
            while ((file = this.readFile(array, add, array2, 0, array2.length)) > -1) {
                if (fileTransferProgress != null && fileTransferProgress.isCancelled()) {
                    throw new TransferCancelledException();
                }
                outputStream.write(array2, 0, file);
                add = UnsignedInteger64.add(add, file);
                if (fileTransferProgress == null) {
                    continue;
                }
                fileTransferProgress.progressed(add.longValue());
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    public UnsignedInteger32 postReadRequest(final byte[] array, final long n, final int n2) throws SftpStatusException, SshException {
        try {
            if (!this.o.contains(new String(array))) {
                throw new SftpStatusException(100, "The file handle is invalid!");
            }
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(5);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(array);
            packet.writeUINT64(n);
            packet.writeInt(n2);
            this.sendMessage(packet);
            return b;
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public int readFile(final byte[] array, final UnsignedInteger64 unsignedInteger64, final byte[] array2, final int n, final int n2) throws SftpStatusException, SshException {
        try {
            if (!this.o.contains(new String(array))) {
                throw new SftpStatusException(100, "The file handle is invalid!");
            }
            if (array2.length - n < n2) {
                throw new IndexOutOfBoundsException("Output array size is smaller than read length!");
            }
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(5);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(array);
            packet.write(unsignedInteger64.toByteArray());
            packet.writeInt(n2);
            this.sendMessage(packet);
            final SftpMessage c = this.c(b);
            if (c.getType() == 103) {
                final byte[] binaryString = c.readBinaryString();
                System.arraycopy(binaryString, 0, array2, n, binaryString.length);
                return binaryString.length;
            }
            if (c.getType() != 101) {
                this.close();
                throw new SshException("The server responded with an unexpected message", 6);
            }
            final int n3 = (int)c.readInt();
            if (n3 == 1) {
                return -1;
            }
            if (this.l >= 3) {
                throw new SftpStatusException(n3, c.readString().trim());
            }
            throw new SftpStatusException(n3);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public SftpFile getFile(final String s) throws SftpStatusException, SshException {
        final String absolutePath = this.getAbsolutePath(s);
        final SftpFile sftpFile = new SftpFile(absolutePath, this.getAttributes(absolutePath));
        sftpFile.e = this;
        return sftpFile;
    }
    
    public String getAbsolutePath(final SftpFile sftpFile) throws SftpStatusException, SshException {
        return this.getAbsolutePath(sftpFile.getFilename());
    }
    
    public void createSymbolicLink(final String s, final String s2) throws SftpStatusException, SshException {
        if (this.l < 3) {
            throw new SftpStatusException(8, "Symbolic links are not supported by the server SFTP version " + String.valueOf(this.l));
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(20);
            packet.writeInt(b.longValue());
            packet.writeString(s2, this.p);
            packet.writeString(s, this.p);
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public String getSymbolicLinkTarget(final String s) throws SftpStatusException, SshException {
        if (this.l < 3) {
            throw new SftpStatusException(8, "Symbolic links are not supported by the server SFTP version " + String.valueOf(this.l));
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(19);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            this.sendMessage(packet);
            return this.b(this.c(b), null)[0].getAbsolutePath();
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public String getDefaultDirectory() throws SftpStatusException, SshException {
        return this.getAbsolutePath("");
    }
    
    public String getAbsolutePath(final String s) throws SftpStatusException, SshException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(16);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            this.sendMessage(packet);
            final SftpMessage c = this.c(b);
            if (c.getType() == 104) {
                final SftpFile[] b2 = this.b(c, null);
                if (b2.length != 1) {
                    this.close();
                    throw new SshException("Server responded to SSH_FXP_REALPATH with too many files!", 6);
                }
                return b2[0].getAbsolutePath();
            }
            else {
                if (c.getType() != 101) {
                    this.close();
                    throw new SshException("The server responded with an unexpected message", 6);
                }
                final int n = (int)c.readInt();
                if (this.l >= 3) {
                    throw new SftpStatusException(n, c.readString().trim());
                }
                throw new SftpStatusException(n);
            }
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public int listChildren(SftpFile openDirectory, final Vector vector) throws SftpStatusException, SshException {
        if (!openDirectory.isDirectory()) {
            throw new SshException("Cannot list children for this file object", 4);
        }
        if (!this.c(openDirectory.getHandle())) {
            openDirectory = this.openDirectory(openDirectory.getAbsolutePath());
            if (!this.c(openDirectory.getHandle())) {
                throw new SftpStatusException(4, "Failed to open directory");
            }
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(12);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(openDirectory.getHandle());
            this.sendMessage(packet);
            final SftpMessage c = this.c(b);
            if (c.getType() == 104) {
                final SftpFile[] b2 = this.b(c, openDirectory.getAbsolutePath());
                for (int i = 0; i < b2.length; ++i) {
                    vector.addElement(b2[i]);
                }
                return b2.length;
            }
            if (c.getType() != 101) {
                this.close();
                throw new SshException("The server responded with an unexpected message", 6);
            }
            final int n = (int)c.readInt();
            if (n == 1) {
                return -1;
            }
            if (this.l >= 3) {
                throw new SftpStatusException(n, c.readString().trim());
            }
            throw new SftpStatusException(n);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    SftpFile[] b(final SftpMessage sftpMessage, String string) throws SshException {
        try {
            if (string != null && !string.endsWith("/")) {
                string += "/";
            }
            final SftpFile[] array = new SftpFile[(int)sftpMessage.readInt()];
            String string2 = null;
            for (int i = 0; i < array.length; ++i) {
                final String string3 = sftpMessage.readString(this.p);
                if (this.l <= 3) {
                    string2 = sftpMessage.readString(this.p);
                }
                array[i] = new SftpFile((string != null) ? (string + string3) : string3, new SftpFileAttributes(this, sftpMessage));
                array[i].f = string2;
                if (string2 != null && this.l <= 3) {
                    try {
                        final StringTokenizer stringTokenizer = new StringTokenizer(string2);
                        stringTokenizer.nextToken();
                        stringTokenizer.nextToken();
                        final String nextToken = stringTokenizer.nextToken();
                        final String nextToken2 = stringTokenizer.nextToken();
                        array[i].getAttributes().b(nextToken);
                        array[i].getAttributes().c(nextToken2);
                    }
                    catch (final Exception ex) {}
                }
                array[i].b(this);
            }
            return array;
        }
        catch (final SshIOException ex2) {
            throw ex2.getRealException();
        }
        catch (final IOException ex3) {
            throw new SshException(ex3);
        }
    }
    
    public void recurseMakeDirectory(final String s) throws SftpStatusException, SshException {
        if (s.trim().length() > 0) {
            try {
                this.openDirectory(s).close();
            }
            catch (final SshException ex) {
                int i = 0;
                do {
                    i = s.indexOf(47, i);
                    final String s2 = (i > -1) ? s.substring(0, i + 1) : s;
                    try {
                        this.openDirectory(s2).close();
                    }
                    catch (final SshException ex2) {
                        this.makeDirectory(s2);
                    }
                } while (i > -1);
            }
        }
    }
    
    public SftpFile openFile(final String s, final int n) throws SftpStatusException, SshException {
        return this.openFile(s, n, new SftpFileAttributes(this, 5));
    }
    
    public SftpFile openFile(final String s, final int n, SftpFileAttributes sftpFileAttributes) throws SftpStatusException, SshException {
        if (sftpFileAttributes == null) {
            sftpFileAttributes = new SftpFileAttributes(this, 5);
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(3);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            packet.writeInt(n);
            packet.write(sftpFileAttributes.toByteArray());
            this.sendMessage(packet);
            final byte[] b2 = this.b(b);
            this.o.addElement(new String(b2));
            final SftpFile sftpFile = new SftpFile(s, null);
            sftpFile.b(b2);
            sftpFile.b(this);
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 26, true).addAttribute("FILE_NAME", sftpFile.getAbsolutePath()));
            return sftpFile;
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public SftpFile openDirectory(final String s) throws SftpStatusException, SshException {
        final String absolutePath = this.getAbsolutePath(s);
        final SftpFileAttributes attributes = this.getAttributes(absolutePath);
        if (!attributes.isDirectory()) {
            throw new SftpStatusException(4, s + " is not a directory");
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(11);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            this.sendMessage(packet);
            final byte[] b2 = this.b(b);
            this.o.addElement(new String(b2));
            final SftpFile sftpFile = new SftpFile(absolutePath, attributes);
            sftpFile.b(b2);
            sftpFile.b(this);
            return sftpFile;
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    void b(final byte[] array) throws SftpStatusException, SshException {
        if (!this.c(array)) {
            throw new SftpStatusException(100, "The handle is invalid!");
        }
        try {
            this.o.removeElement(new String(array));
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(4);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(array);
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public void closeFile(final SftpFile sftpFile) throws SftpStatusException, SshException {
        if (sftpFile.getHandle() != null) {
            this.b(sftpFile.getHandle());
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 25, true).addAttribute("FILE_NAME", sftpFile.getAbsolutePath()));
            sftpFile.b((byte[])null);
        }
    }
    
    boolean c(final byte[] array) {
        return this.o.contains(new String(array));
    }
    
    public void removeDirectory(final String s) throws SftpStatusException, SshException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(15);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 29, true).addAttribute("DIRECTORY_PATH", s));
    }
    
    public void removeFile(final String s) throws SftpStatusException, SshException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(13);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 28, true).addAttribute("FILE_NAME", s));
    }
    
    public void renameFile(final String s, final String s2) throws SftpStatusException, SshException {
        if (this.l < 2) {
            throw new SftpStatusException(8, "Renaming files is not supported by the server SFTP version " + String.valueOf(this.l));
        }
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(18);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            packet.writeString(s2, this.p);
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 27, true).addAttribute("FILE_NAME", s).addAttribute("FILE_NEW_NAME", s2));
    }
    
    public SftpFileAttributes getAttributes(final String s) throws SftpStatusException, SshException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(17);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            if (this.l > 3) {
                packet.writeInt(-2147483139L);
            }
            this.sendMessage(packet);
            return this.b(this.c(b));
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    SftpFileAttributes b(final SftpMessage sftpMessage) throws SftpStatusException, SshException {
        try {
            if (sftpMessage.getType() == 105) {
                return new SftpFileAttributes(this, sftpMessage);
            }
            if (sftpMessage.getType() != 101) {
                this.close();
                throw new SshException("The server responded with an unexpected message.", 6);
            }
            final int n = (int)sftpMessage.readInt();
            if (this.l >= 3) {
                throw new SftpStatusException(n, sftpMessage.readString().trim());
            }
            throw new SftpStatusException(n);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public SftpFileAttributes getAttributes(final SftpFile sftpFile) throws SftpStatusException, SshException {
        try {
            if (!this.c(sftpFile.getHandle())) {
                return this.getAttributes(sftpFile.getAbsolutePath());
            }
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(8);
            packet.writeInt(b.longValue());
            packet.writeBinaryString(sftpFile.getHandle());
            if (this.l > 3) {
                packet.writeInt(-2147483139L);
            }
            this.sendMessage(packet);
            return this.b(this.c(b));
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public void makeDirectory(final String s) throws SftpStatusException, SshException {
        this.makeDirectory(s, new SftpFileAttributes(this, 2));
    }
    
    public void makeDirectory(final String s, final SftpFileAttributes sftpFileAttributes) throws SftpStatusException, SshException {
        try {
            final UnsignedInteger32 b = this.b();
            final Packet packet = this.createPacket();
            packet.write(14);
            packet.writeInt(b.longValue());
            packet.writeString(s, this.p);
            packet.write(sftpFileAttributes.toByteArray());
            this.sendMessage(packet);
            this.getOKRequestStatus(b);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    byte[] b(final UnsignedInteger32 unsignedInteger32) throws SftpStatusException, SshException {
        try {
            final SftpMessage c = this.c(unsignedInteger32);
            if (c.getType() == 102) {
                return c.readBinaryString();
            }
            if (c.getType() != 101) {
                this.close();
                throw new SshException("The server responded with an unexpected message!", 6);
            }
            final int n = (int)c.readInt();
            if (this.l >= 3) {
                throw new SftpStatusException(n, c.readString().trim());
            }
            throw new SftpStatusException(n);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public void getOKRequestStatus(final UnsignedInteger32 unsignedInteger32) throws SftpStatusException, SshException {
        try {
            final SftpMessage c = this.c(unsignedInteger32);
            if (c.getType() != 101) {
                this.close();
                throw new SshException("The server responded with an unexpected message!", 6);
            }
            final int n = (int)c.readInt();
            if (n == 0) {
                return;
            }
            if (this.l >= 3) {
                throw new SftpStatusException(n, c.readString().trim());
            }
            throw new SftpStatusException(n);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    SftpMessage c(final UnsignedInteger32 unsignedInteger32) throws SshException {
        final MessageHolder messageHolder = new MessageHolder();
        while (messageHolder.msg == null) {
            try {
                if (this.n.b(unsignedInteger32, messageHolder)) {
                    final SftpMessage sftpMessage = new SftpMessage(this.nextMessage());
                    this.k.put(new UnsignedInteger32(sftpMessage.getMessageId()), sftpMessage);
                }
            }
            catch (final InterruptedException ex) {
                try {
                    this.close();
                }
                catch (final SshIOException ex2) {
                    throw ex2.getRealException();
                }
                catch (final IOException ex3) {
                    throw new SshException(ex3.getMessage(), 6);
                }
                throw new SshException("The thread was interrupted", 6);
            }
            catch (final IOException ex4) {
                throw new SshException(5, ex4);
            }
            finally {
                this.n.b();
            }
        }
        return this.k.remove(unsignedInteger32);
    }
    
    UnsignedInteger32 b() {
        return this.h = UnsignedInteger32.add(this.h, 1);
    }
    
    static {
        SftpSubsystemChannel.MAX_VERSION = 4;
    }
    
    class _b
    {
        boolean b;
        
        _b() {
            this.b = false;
        }
        
        public synchronized boolean b(final UnsignedInteger32 unsignedInteger32, final MessageHolder messageHolder) throws InterruptedException {
            final boolean b = !this.b;
            if (SftpSubsystemChannel.this.k.containsKey(unsignedInteger32)) {
                messageHolder.msg = SftpSubsystemChannel.this.k.get(unsignedInteger32);
                return false;
            }
            if (b) {
                this.b = true;
            }
            else {
                this.wait();
            }
            return b;
        }
        
        public synchronized void b() {
            this.b = false;
            this.notifyAll();
        }
    }
}
