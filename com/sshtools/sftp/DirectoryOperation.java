package com.sshtools.sftp;

import com.maverick.ssh.SshException;
import java.util.Enumeration;
import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpStatusException;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class DirectoryOperation
{
    Vector e;
    Vector d;
    Vector b;
    Vector g;
    Vector f;
    Hashtable c;
    
    public DirectoryOperation() {
        this.e = new Vector();
        this.d = new Vector();
        this.b = new Vector();
        this.g = new Vector();
        this.f = new Vector();
        this.c = new Hashtable();
    }
    
    void b(final File file) {
        this.d.addElement(file);
    }
    
    void b(final File file, final SftpStatusException ex) {
        this.c.put(file, ex);
    }
    
    void d(final File file) {
        this.b.addElement(file);
    }
    
    void e(final File file) {
        this.g.addElement(file);
    }
    
    void c(final File file) {
        this.e.addElement(file);
    }
    
    void b(final SftpFile sftpFile) {
        this.d.addElement(sftpFile);
    }
    
    void e(final SftpFile sftpFile) {
        this.b.addElement(sftpFile);
    }
    
    void c(final SftpFile sftpFile) {
        this.g.addElement(sftpFile);
    }
    
    void d(final SftpFile sftpFile) {
        this.e.addElement(sftpFile);
    }
    
    public Vector getNewFiles() {
        return this.d;
    }
    
    public Vector getUpdatedFiles() {
        return this.b;
    }
    
    public Vector getUnchangedFiles() {
        return this.e;
    }
    
    public Vector getDeletedFiles() {
        return this.g;
    }
    
    public Hashtable getFailedTransfers() {
        return this.c;
    }
    
    public boolean containsFile(final File file) {
        return this.e.contains(file) || this.d.contains(file) || this.b.contains(file) || this.g.contains(file) || this.f.contains(file) || this.c.containsKey(file);
    }
    
    public boolean containsFile(final SftpFile sftpFile) {
        return this.e.contains(sftpFile) || this.d.contains(sftpFile) || this.b.contains(sftpFile) || this.g.contains(sftpFile) || this.f.contains(sftpFile.getAbsolutePath()) || this.c.containsKey(sftpFile);
    }
    
    public void addDirectoryOperation(final DirectoryOperation directoryOperation, final File file) {
        this.b(directoryOperation.getUpdatedFiles(), this.b);
        this.b(directoryOperation.getNewFiles(), this.d);
        this.b(directoryOperation.getUnchangedFiles(), this.e);
        this.b(directoryOperation.getDeletedFiles(), this.g);
        final Enumeration keys = directoryOperation.c.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            this.c.put(nextElement, directoryOperation.c.get(nextElement));
        }
        this.f.addElement(file);
    }
    
    void b(final Vector vector, final Vector vector2) {
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            vector2.addElement(elements.nextElement());
        }
    }
    
    public int getFileCount() {
        return this.d.size() + this.b.size();
    }
    
    public void addDirectoryOperation(final DirectoryOperation directoryOperation, final String s) {
        this.b(directoryOperation.getUpdatedFiles(), this.b);
        this.b(directoryOperation.getNewFiles(), this.d);
        this.b(directoryOperation.getUnchangedFiles(), this.e);
        this.b(directoryOperation.getDeletedFiles(), this.g);
        final Enumeration keys = directoryOperation.c.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            this.c.put(nextElement, directoryOperation.c.get(nextElement));
        }
        this.f.addElement(s);
    }
    
    public long getTransferSize() throws SftpStatusException, SshException {
        long n = 0L;
        final Enumeration elements = this.d.elements();
        while (elements.hasMoreElements()) {
            final Object nextElement = elements.nextElement();
            if (nextElement instanceof File) {
                final File file = (File)nextElement;
                if (!file.isFile()) {
                    continue;
                }
                n += file.length();
            }
            else {
                if (!(nextElement instanceof SftpFile)) {
                    continue;
                }
                final SftpFile sftpFile = (SftpFile)nextElement;
                if (!sftpFile.isFile()) {
                    continue;
                }
                n += sftpFile.getAttributes().getSize().longValue();
            }
        }
        final Enumeration elements2 = this.b.elements();
        while (elements2.hasMoreElements()) {
            final Object nextElement2 = elements2.nextElement();
            if (nextElement2 instanceof File) {
                final File file2 = (File)nextElement2;
                if (!file2.isFile()) {
                    continue;
                }
                n += file2.length();
            }
            else {
                if (!(nextElement2 instanceof SftpFile)) {
                    continue;
                }
                final SftpFile sftpFile2 = (SftpFile)nextElement2;
                if (!sftpFile2.isFile()) {
                    continue;
                }
                n += sftpFile2.getAttributes().getSize().longValue();
            }
        }
        return n;
    }
}
