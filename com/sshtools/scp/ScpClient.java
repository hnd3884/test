package com.sshtools.scp;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.maverick.ssh.SshSession;
import com.maverick.ssh.ShellTimeoutException;
import com.sshtools.sftp.GlobRegExpMatching;
import java.io.IOException;
import com.maverick.ssh.SshIOException;
import com.maverick.sftp.SftpStatusException;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.SshException;
import com.maverick.sftp.FileTransferProgress;
import com.maverick.ssh.SshClient;
import java.io.File;
import com.maverick.ssh.Client;
import com.maverick.scp.ScpClientIO;

public class ScpClient extends ScpClientIO implements Client
{
    File l;
    
    public ScpClient(final SshClient sshClient) {
        this(null, sshClient);
    }
    
    public ScpClient(File l, final SshClient sshClient) {
        super(sshClient);
        String property = "";
        if (l == null) {
            try {
                property = System.getProperty("user.home");
            }
            catch (final SecurityException ex) {}
            l = new File(property);
        }
        this.l = l;
    }
    
    public void put(final String s, final String s2, final boolean b) throws SshException, ChannelOpenException, SftpStatusException {
        this.put(s, s2, b, null);
    }
    
    public void putFile(final String s, String s2, final boolean b, final FileTransferProgress fileTransferProgress, final boolean b2) throws SshException, ChannelOpenException {
        File file = new File(s);
        if (!file.isAbsolute()) {
            file = new File(this.l, s);
        }
        if (!file.exists()) {
            throw new SshException(s + " does not exist", 6);
        }
        if (!file.isFile() && !file.isDirectory()) {
            throw new SshException(s + " is not a regular file or directory", 6);
        }
        if (file.isDirectory() && !b) {
            throw new SshException(s + " is a directory, use recursive mode", 6);
        }
        if (s2 == null || s2.equals("")) {
            s2 = ".";
        }
        final ScpEngine scpEngine = new ScpEngine("scp " + ((file.isDirectory() | b2) ? "-d " : "") + "-t " + (b ? "-r " : "") + s2, super.ssh.openSessionChannel());
        try {
            scpEngine.waitForResponse();
            scpEngine.b(file, b, fileTransferProgress);
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException("localfile=" + s + " remotefile=" + s2, 6, ex2);
        }
        finally {
            try {
                scpEngine.close();
            }
            catch (final Throwable t) {}
        }
    }
    
    public void put(final String s, final String s2, final boolean b, final FileTransferProgress fileTransferProgress) throws SshException, ChannelOpenException {
        final GlobRegExpMatching globRegExpMatching = new GlobRegExpMatching();
        String s3 = this.l.getAbsolutePath();
        int n;
        if ((n = s.lastIndexOf(System.getProperty("file.separator"))) > -1 || (n = s.lastIndexOf(47)) > -1) {
            final String substring = s.substring(0, n + 1);
            if (new File(substring).isAbsolute()) {
                s3 = substring;
            }
            else {
                s3 = s3 + System.getProperty("file.separator") + substring;
            }
        }
        final String[] list = new File(s3).list();
        final File[] array = new File[list.length];
        for (int i = 0; i < list.length; ++i) {
            array[i] = new File(s3 + File.separator + list[i]);
        }
        final String[] matchFileNamesWithPattern = globRegExpMatching.matchFileNamesWithPattern(array, s.substring(n + 1));
        if (matchFileNamesWithPattern.length == 0) {
            throw new SshException(s + "No file matches/File does not exist", 6);
        }
        if (matchFileNamesWithPattern.length > 1) {
            this.put(matchFileNamesWithPattern, s2, b, fileTransferProgress);
        }
        else {
            this.putFile(matchFileNamesWithPattern[0], s2, b, fileTransferProgress, false);
        }
    }
    
    public void put(final String[] array, final String s, final boolean b) throws SshException, ChannelOpenException {
        this.put(array, s, b, null);
    }
    
    public void put(final String[] array, final String s, final boolean b, final FileTransferProgress fileTransferProgress) throws SshException, ChannelOpenException {
        for (int i = 0; i < array.length; ++i) {
            this.putFile(array[i], s, b, fileTransferProgress, true);
        }
    }
    
    public void get(final String s, final String[] array, final boolean b) throws SshException, ChannelOpenException {
        this.get(s, array, b, null);
    }
    
    public void get(final String s, final String[] array, final boolean b, final FileTransferProgress fileTransferProgress) throws SshException, ChannelOpenException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append("\"");
            sb.append(array[i]);
            sb.append("\" ");
        }
        this.get(s, sb.toString().trim(), b, fileTransferProgress);
    }
    
    public void get(final String s, final String s2, final boolean b) throws SshException, ChannelOpenException {
        this.get(s, s2, b, null);
    }
    
    public void get(String s, final String s2, final boolean b, final FileTransferProgress fileTransferProgress) throws SshException, ChannelOpenException {
        if (s == null || s.equals("")) {
            s = ".";
        }
        File file = new File(s);
        if (!file.isAbsolute()) {
            file = new File(this.l, s);
        }
        if (file.exists() && !file.isFile() && !file.isDirectory()) {
            throw new SshException(s + " is not a regular file or directory", 6);
        }
        final ScpEngine scpEngine = new ScpEngine("scp -f " + (b ? "-r " : "") + s2, super.ssh.openSessionChannel());
        scpEngine.b(file, fileTransferProgress, false);
        try {
            scpEngine.close();
        }
        catch (final Throwable t) {}
    }
    
    public void exit() throws SshException, ShellTimeoutException, IOException {
    }
    
    protected class ScpEngine extends ScpEngineIO
    {
        protected ScpEngine(final String s, final SshSession sshSession) throws SshException {
            super(s, sshSession);
        }
        
        private boolean c(final File file, final boolean b, final FileTransferProgress fileTransferProgress) throws SshException {
            try {
                if (!b) {
                    this.writeError("File " + file.getName() + " is a directory, use recursive mode");
                    return false;
                }
                super.out.write(("D0755 0 " + file.getName() + "\n").getBytes());
                this.waitForResponse();
                final String[] list = file.list();
                for (int i = 0; i < list.length; ++i) {
                    this.b(new File(file, list[i]), b, fileTransferProgress);
                }
                super.out.write("E\n".getBytes());
                return true;
            }
            catch (final IOException ex) {
                this.close();
                throw new SshException(ex, 6);
            }
        }
        
        private void b(final File file, final boolean b, final FileTransferProgress fileTransferProgress) throws SshException {
            try {
                if (file.isDirectory()) {
                    if (!this.c(file, b, fileTransferProgress)) {
                        return;
                    }
                }
                else {
                    if (!file.isFile()) {
                        throw new SshException(file.getName() + " not valid for SCP", 6);
                    }
                    super.out.write(("C0644 " + file.length() + " " + file.getName() + "\n").getBytes());
                    if (fileTransferProgress != null) {
                        fileTransferProgress.started(file.length(), file.getName());
                    }
                    this.waitForResponse();
                    this.writeCompleteFile(new FileInputStream(file), file.length(), fileTransferProgress);
                    if (fileTransferProgress != null) {
                        fileTransferProgress.completed();
                    }
                    this.writeOk();
                }
                this.waitForResponse();
            }
            catch (final SshIOException ex) {
                throw ex.getRealException();
            }
            catch (final IOException ex2) {
                this.close();
                throw new SshException(ex2, 6);
            }
        }
        
        private void b(final File file, final FileTransferProgress fileTransferProgress, final boolean b) throws SshException {
            try {
                final String[] array = new String[3];
                this.writeOk();
                String string = null;
            Label_0365:
                while (true) {
                    try {
                        string = this.readString();
                    }
                    catch (final EOFException ex) {
                        return;
                    }
                    catch (final SshIOException ex2) {
                        return;
                    }
                    final char char1 = string.charAt(0);
                    switch (char1) {
                        case 69: {
                            this.writeOk();
                            return;
                        }
                        case 84: {
                            continue;
                        }
                        case 67:
                        case 68: {
                            String s = file.getAbsolutePath();
                            this.parseCommand(string, array);
                            if (file.isDirectory()) {
                                s = s + File.separator + array[2];
                            }
                            final File file2 = new File(s);
                            if (char1 == 'D') {
                                if (file2.exists()) {
                                    if (!file2.isDirectory()) {
                                        final String string2 = "Invalid target " + file2.getName() + ", must be a directory";
                                        this.writeError(string2);
                                        throw new IOException(string2);
                                    }
                                }
                                else if (!file2.mkdir()) {
                                    final String string3 = "Could not create directory: " + file2.getName();
                                    this.writeError(string3);
                                    throw new IOException(string3);
                                }
                                this.b(file2, fileTransferProgress, true);
                                continue;
                            }
                            final long long1 = Long.parseLong(array[1]);
                            final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            this.writeOk();
                            if (fileTransferProgress != null) {
                                fileTransferProgress.started(long1, s);
                            }
                            this.readCompleteFile(fileOutputStream, long1, fileTransferProgress);
                            if (fileTransferProgress != null) {
                                fileTransferProgress.completed();
                            }
                            try {
                                this.waitForResponse();
                                this.writeOk();
                                continue;
                            }
                            catch (final SshIOException ex3) {
                                if (ex3.getRealException().getReason() == 1 && !b) {
                                    return;
                                }
                                throw ex3;
                            }
                            break Label_0365;
                            continue;
                        }
                        default: {
                            break Label_0365;
                        }
                    }
                }
                this.writeError("Unexpected cmd: " + string);
                throw new IOException("SCP unexpected cmd: " + string);
            }
            catch (final SshIOException ex4) {
                throw ex4.getRealException();
            }
            catch (final IOException ex5) {
                this.close();
                throw new SshException(ex5, 6);
            }
        }
    }
}
