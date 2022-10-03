package com.maverick.scp;

import java.io.EOFException;
import com.maverick.ssh.SshIOException;
import java.io.OutputStream;
import com.maverick.ssh.SshSession;
import java.io.IOException;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.SshException;
import com.maverick.sftp.FileTransferProgress;
import java.io.InputStream;
import com.maverick.ssh.SshClient;

public class ScpClientIO
{
    protected SshClient ssh;
    boolean k;
    
    public ScpClientIO(final SshClient ssh) {
        this.k = true;
        this.ssh = ssh;
    }
    
    public void put(final InputStream inputStream, final long n, final String s, final String s2) throws SshException, ChannelOpenException {
        this.put(inputStream, n, s, s2, null);
    }
    
    public void put(final InputStream inputStream, final long n, final String s, final String s2, final FileTransferProgress fileTransferProgress) throws SshException, ChannelOpenException {
        final ScpEngineIO scpEngineIO = new ScpEngineIO("scp -t " + s2, this.ssh.openSessionChannel());
        try {
            scpEngineIO.waitForResponse();
            if (fileTransferProgress != null) {
                fileTransferProgress.started(n, s2);
            }
            scpEngineIO.writeStreamToRemote(inputStream, n, s, fileTransferProgress);
            if (fileTransferProgress != null) {
                fileTransferProgress.completed();
            }
            scpEngineIO.close();
        }
        catch (final IOException ex) {
            scpEngineIO.close();
            throw new SshException(ex, 6);
        }
    }
    
    public InputStream get(final String s) throws SshException, ChannelOpenException {
        return this.get(s, null);
    }
    
    public InputStream get(final String s, final FileTransferProgress fileTransferProgress) throws SshException, ChannelOpenException {
        final ScpEngineIO scpEngineIO = new ScpEngineIO("scp -f " + s, this.ssh.openSessionChannel());
        try {
            return scpEngineIO.readStreamFromRemote(s, fileTransferProgress);
        }
        catch (final IOException ex) {
            scpEngineIO.close();
            throw new SshException(ex, 6);
        }
    }
    
    public class ScpEngineIO
    {
        protected byte[] buffer;
        protected String cmd;
        protected SshSession session;
        protected OutputStream out;
        protected InputStream in;
        
        protected ScpEngineIO(final String cmd, final SshSession session) throws SshException {
            this.buffer = new byte[16384];
            try {
                this.session = session;
                this.cmd = cmd;
                this.in = session.getInputStream();
                this.out = session.getOutputStream();
                if (!session.executeCommand(cmd)) {
                    session.close();
                    throw new SshException("Failed to execute the command " + cmd, 6);
                }
            }
            catch (final SshIOException ex) {
                throw ex.getRealException();
            }
        }
        
        public void close() throws SshException {
            try {
                this.session.getOutputStream().close();
            }
            catch (final IOException ex) {
                throw new SshException(ex);
            }
            try {
                Thread.sleep(500L);
            }
            catch (final Throwable t) {}
            this.session.close();
        }
        
        protected void writeStreamToRemote(final InputStream inputStream, final long n, final String s, final FileTransferProgress fileTransferProgress) throws IOException {
            this.out.write(("C0644 " + n + " " + s + "\n").getBytes());
            this.waitForResponse();
            this.writeCompleteFile(inputStream, n, fileTransferProgress);
            this.writeOk();
            this.waitForResponse();
        }
        
        protected InputStream readStreamFromRemote(final String s, final FileTransferProgress fileTransferProgress) throws IOException {
            final String[] array = new String[3];
            this.writeOk();
            while (true) {
                String string;
                try {
                    string = this.readString();
                }
                catch (final EOFException ex) {
                    return null;
                }
                catch (final SshIOException ex2) {
                    return null;
                }
                switch (string.charAt(0)) {
                    case 'E': {
                        this.writeOk();
                        return null;
                    }
                    case 'T': {
                        continue;
                    }
                    case 'D': {
                        throw new IOException("Directories cannot be copied to a stream");
                    }
                    case 'C': {
                        this.parseCommand(string, array);
                        final long long1 = Long.parseLong(array[1]);
                        this.writeOk();
                        if (fileTransferProgress != null) {
                            fileTransferProgress.started(long1, s);
                        }
                        return new _b(long1, this.in, this, fileTransferProgress, s);
                    }
                    default: {
                        this.writeError("Unexpected cmd: " + string);
                        throw new IOException("SCP unexpected cmd: " + string);
                    }
                }
            }
        }
        
        protected void parseCommand(final String s, final String[] array) throws IOException {
            final int index = s.indexOf(32);
            final int index2 = s.indexOf(32, index + 1);
            if (index == -1 || index2 == -1) {
                this.writeError("Syntax error in cmd");
                throw new IOException("Syntax error in cmd");
            }
            array[0] = s.substring(1, index);
            array[1] = s.substring(index + 1, index2);
            array[2] = s.substring(index2 + 1);
        }
        
        protected String readString() throws IOException {
            int n = 0;
            int read;
            while ((read = this.in.read()) != 10 && read >= 0) {
                this.buffer[n++] = (byte)read;
            }
            if (read == -1) {
                throw new EOFException("Unexpected EOF");
            }
            if (this.buffer[0] == 10) {
                throw new IOException("Unexpected <NL>");
            }
            if (this.buffer[0] != 2 && this.buffer[0] != 1) {
                return new String(this.buffer, 0, n);
            }
            final String s = new String(this.buffer, 1, n - 1);
            if (this.buffer[0] == 2) {
                throw new IOException(s);
            }
            throw new IOException("SCP returned an unexpected error: " + s);
        }
        
        public void waitForResponse() throws IOException {
            int n = this.in.read();
            if (ScpClientIO.this.k) {
                while (n > 0 && n != 2) {
                    n = this.in.read();
                }
                ScpClientIO.this.k = false;
            }
            if (n == 0) {
                return;
            }
            if (n == -1) {
                throw new EOFException("SCP returned unexpected EOF");
            }
            final String string = this.readString();
            if (n == 2) {
                throw new IOException(string);
            }
            throw new IOException("SCP returned an unexpected error: " + string);
        }
        
        protected void writeOk() throws IOException {
            this.out.write(0);
        }
        
        protected void writeError(final String s) throws IOException {
            this.out.write(1);
            this.out.write(s.getBytes());
        }
        
        protected void writeCompleteFile(final InputStream inputStream, final long n, final FileTransferProgress fileTransferProgress) throws IOException {
            long n2 = 0L;
            try {
                while (n2 < n) {
                    final int read = inputStream.read(this.buffer, 0, (int)((n - n2 < this.buffer.length) ? (n - n2) : this.buffer.length));
                    if (read == -1) {
                        throw new EOFException("SCP received an unexpected EOF");
                    }
                    n2 += read;
                    this.out.write(this.buffer, 0, read);
                    if (fileTransferProgress == null) {
                        continue;
                    }
                    if (fileTransferProgress.isCancelled()) {
                        throw new SshIOException(new SshException("SCP transfer was cancelled by user", 18));
                    }
                    fileTransferProgress.progressed(n2);
                }
            }
            finally {
                inputStream.close();
            }
        }
        
        protected void readCompleteFile(final OutputStream outputStream, final long n, final FileTransferProgress fileTransferProgress) throws IOException {
            long n2 = 0L;
            try {
                while (n2 < n) {
                    final int read = this.in.read(this.buffer, 0, (int)((n - n2 < this.buffer.length) ? (n - n2) : this.buffer.length));
                    if (read == -1) {
                        throw new EOFException("SCP received an unexpected EOF");
                    }
                    n2 += read;
                    outputStream.write(this.buffer, 0, read);
                    if (fileTransferProgress == null) {
                        continue;
                    }
                    if (fileTransferProgress.isCancelled()) {
                        throw new SshIOException(new SshException("SCP transfer was cancelled by user", 18));
                    }
                    fileTransferProgress.progressed(n2);
                }
            }
            finally {
                outputStream.close();
            }
        }
    }
    
    static class _b extends InputStream
    {
        long f;
        InputStream b;
        long e;
        ScpEngineIO d;
        FileTransferProgress c;
        String g;
        
        _b(final long f, final InputStream b, final ScpEngineIO d, final FileTransferProgress c, final String g) {
            this.f = f;
            this.b = b;
            this.d = d;
            this.c = c;
            this.g = g;
        }
        
        public int read() throws IOException {
            if (this.e == this.f) {
                return -1;
            }
            if (this.e >= this.f) {
                throw new EOFException("End of file.");
            }
            final int read = this.b.read();
            if (read == -1) {
                throw new EOFException("Unexpected EOF.");
            }
            ++this.e;
            if (this.e == this.f) {
                this.d.waitForResponse();
                this.d.writeOk();
                if (this.c != null) {
                    this.c.completed();
                }
            }
            if (this.c != null) {
                if (this.c.isCancelled()) {
                    throw new SshIOException(new SshException("SCP transfer was cancelled by user", 18));
                }
                this.c.progressed(this.e);
            }
            return read;
        }
        
        public int available() throws IOException {
            if (this.e == this.f) {
                return -1;
            }
            return (int)(this.f - this.e);
        }
        
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            if (this.e >= this.f) {
                return -1;
            }
            final int read = this.b.read(array, n, (int)((this.f - this.e > n2) ? n2 : (this.f - this.e)));
            if (read == -1) {
                throw new EOFException("Unexpected EOF.");
            }
            this.e += read;
            if (this.e >= this.f) {
                this.d.waitForResponse();
                this.d.writeOk();
                if (this.c != null) {
                    this.c.completed();
                }
            }
            if (this.c != null) {
                if (this.c.isCancelled()) {
                    throw new SshIOException(new SshException("SCP transfer was cancelled by user", 18));
                }
                this.c.progressed(this.e);
            }
            return read;
        }
        
        public void close() throws IOException {
            try {
                this.d.close();
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
        }
    }
}
