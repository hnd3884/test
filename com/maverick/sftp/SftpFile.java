package com.maverick.sftp;

import com.maverick.ssh.SshException;

public class SftpFile
{
    String c;
    byte[] g;
    SftpFileAttributes d;
    SftpSubsystemChannel e;
    String b;
    String f;
    
    public SftpFile(final String b, final SftpFileAttributes d) {
        this.b = b;
        this.d = d;
        if (this.b.equals("/")) {
            this.c = "/";
        }
        else {
            this.b = this.b.trim();
            if (this.b.endsWith("/")) {
                this.b = this.b.substring(0, this.b.length() - 1);
            }
            final int lastIndex = this.b.lastIndexOf(47);
            if (lastIndex > -1) {
                this.c = this.b.substring(lastIndex + 1);
            }
            else {
                this.c = this.b;
            }
        }
    }
    
    public SftpFile getParent() throws SshException, SftpStatusException {
        if (this.b.lastIndexOf(47) == -1) {
            return this.e.getFile(this.e.getDefaultDirectory());
        }
        final String absolutePath = this.e.getAbsolutePath(this.b);
        if (absolutePath.equals("/")) {
            return null;
        }
        if (this.c.equals(".") || this.c.equals("..")) {
            return this.e.getFile(absolutePath).getParent();
        }
        String substring = absolutePath.substring(0, absolutePath.lastIndexOf(47));
        if (substring.equals("")) {
            substring = "/";
        }
        return this.e.getFile(substring);
    }
    
    public String toString() {
        return this.b;
    }
    
    public int hashCode() {
        return this.b.hashCode();
    }
    
    public String getLongname() {
        return this.f;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof SftpFile)) {
            return false;
        }
        final boolean equals = ((SftpFile)o).getAbsolutePath().equals(this.b);
        if (this.g == null && ((SftpFile)o).g == null) {
            return equals;
        }
        if (this.g != null && ((SftpFile)o).g != null) {
            for (int i = 0; i < this.g.length; ++i) {
                if (((SftpFile)o).g[i] != this.g[i]) {
                    return false;
                }
            }
        }
        return equals;
    }
    
    public void delete() throws SftpStatusException, SshException {
        if (this.e == null) {
            throw new SshException("Instance not connected to SFTP subsystem", 4);
        }
        if (this.isDirectory()) {
            this.e.removeDirectory(this.getAbsolutePath());
        }
        else {
            this.e.removeFile(this.getAbsolutePath());
        }
    }
    
    public boolean canWrite() throws SftpStatusException, SshException {
        return (this.getAttributes().getPermissions().longValue() & 0x80L) == 0x80L;
    }
    
    public boolean canRead() throws SftpStatusException, SshException {
        return (this.getAttributes().getPermissions().longValue() & 0x100L) == 0x100L;
    }
    
    public boolean isOpen() {
        return this.e != null && this.e.c(this.g);
    }
    
    void b(final byte[] g) {
        this.g = g;
    }
    
    public byte[] getHandle() {
        return this.g;
    }
    
    void b(final SftpSubsystemChannel e) {
        this.e = e;
    }
    
    public SftpSubsystemChannel getSFTPChannel() {
        return this.e;
    }
    
    public String getFilename() {
        return this.c;
    }
    
    public SftpFileAttributes getAttributes() throws SftpStatusException, SshException {
        if (this.d == null) {
            this.d = this.e.getAttributes(this.getAbsolutePath());
        }
        return this.d;
    }
    
    public String getAbsolutePath() {
        return this.b;
    }
    
    public void close() throws SftpStatusException, SshException {
        this.e.closeFile(this);
    }
    
    public boolean isDirectory() throws SftpStatusException, SshException {
        return this.getAttributes().isDirectory();
    }
    
    public boolean isFile() throws SftpStatusException, SshException {
        return this.getAttributes().isFile();
    }
    
    public boolean isLink() throws SftpStatusException, SshException {
        return this.getAttributes().isLink();
    }
    
    public boolean isFifo() throws SftpStatusException, SshException {
        return (this.getAttributes().getPermissions().longValue() & 0x1000L) == 0x1000L;
    }
    
    public boolean isBlock() throws SftpStatusException, SshException {
        return (this.getAttributes().getPermissions().longValue() & 0x6000L) == 0x6000L;
    }
    
    public boolean isCharacter() throws SftpStatusException, SshException {
        return (this.getAttributes().getPermissions().longValue() & 0x2000L) == 0x2000L;
    }
    
    public boolean isSocket() throws SftpStatusException, SshException {
        return (this.getAttributes().getPermissions().longValue() & 0xC000L) == 0xC000L;
    }
}
