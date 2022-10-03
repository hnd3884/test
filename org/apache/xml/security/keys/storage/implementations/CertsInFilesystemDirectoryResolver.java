package org.apache.xml.security.keys.storage.implementations;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.io.FileInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.io.File;
import org.apache.xml.security.keys.storage.StorageResolverException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.storage.StorageResolverSpi;

public class CertsInFilesystemDirectoryResolver extends StorageResolverSpi
{
    static Log log;
    String _merlinsCertificatesDir;
    private List _certs;
    Iterator _iterator;
    
    public CertsInFilesystemDirectoryResolver(final String merlinsCertificatesDir) throws StorageResolverException {
        this._merlinsCertificatesDir = null;
        this._certs = new ArrayList();
        this._iterator = null;
        this._merlinsCertificatesDir = merlinsCertificatesDir;
        this.readCertsFromHarddrive();
        this._iterator = new FilesystemIterator(this._certs);
    }
    
    private void readCertsFromHarddrive() throws StorageResolverException {
        final File file = new File(this._merlinsCertificatesDir);
        final ArrayList list = new ArrayList();
        final String[] list2 = file.list();
        for (int i = 0; i < list2.length; ++i) {
            if (list2[i].endsWith(".crt")) {
                list.add(list2[i]);
            }
        }
        CertificateFactory instance;
        try {
            instance = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {
            throw new StorageResolverException("empty", ex);
        }
        if (instance == null) {
            throw new StorageResolverException("empty");
        }
        for (int j = 0; j < list.size(); ++j) {
            final String string = file.getAbsolutePath() + File.separator + list.get(j);
            final File file2 = new File(string);
            boolean b = false;
            String name = null;
            try {
                final FileInputStream fileInputStream = new FileInputStream(file2);
                final X509Certificate x509Certificate = (X509Certificate)instance.generateCertificate(fileInputStream);
                fileInputStream.close();
                x509Certificate.checkValidity();
                this._certs.add(x509Certificate);
                name = x509Certificate.getSubjectDN().getName();
                b = true;
            }
            catch (final FileNotFoundException ex2) {
                CertsInFilesystemDirectoryResolver.log.debug((Object)("Could not add certificate from file " + string), (Throwable)ex2);
            }
            catch (final IOException ex3) {
                CertsInFilesystemDirectoryResolver.log.debug((Object)("Could not add certificate from file " + string), (Throwable)ex3);
            }
            catch (final CertificateNotYetValidException ex4) {
                CertsInFilesystemDirectoryResolver.log.debug((Object)("Could not add certificate from file " + string), (Throwable)ex4);
            }
            catch (final CertificateExpiredException ex5) {
                CertsInFilesystemDirectoryResolver.log.debug((Object)("Could not add certificate from file " + string), (Throwable)ex5);
            }
            catch (final CertificateException ex6) {
                CertsInFilesystemDirectoryResolver.log.debug((Object)("Could not add certificate from file " + string), (Throwable)ex6);
            }
            if (b && CertsInFilesystemDirectoryResolver.log.isDebugEnabled()) {
                CertsInFilesystemDirectoryResolver.log.debug((Object)("Added certificate: " + name));
            }
        }
    }
    
    public Iterator getIterator() {
        return this._iterator;
    }
    
    public static void main(final String[] array) throws Exception {
        final Iterator iterator = new CertsInFilesystemDirectoryResolver("data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs").getIterator();
        while (iterator.hasNext()) {
            final X509Certificate x509Certificate = iterator.next();
            final byte[] skiBytesFromCert = XMLX509SKI.getSKIBytesFromCert(x509Certificate);
            System.out.println();
            System.out.println("Base64(SKI())=                 \"" + Base64.encode(skiBytesFromCert) + "\"");
            System.out.println("cert.getSerialNumber()=        \"" + x509Certificate.getSerialNumber().toString() + "\"");
            System.out.println("cert.getSubjectDN().getName()= \"" + x509Certificate.getSubjectDN().getName() + "\"");
            System.out.println("cert.getIssuerDN().getName()=  \"" + x509Certificate.getIssuerDN().getName() + "\"");
        }
    }
    
    static {
        CertsInFilesystemDirectoryResolver.log = LogFactory.getLog(CertsInFilesystemDirectoryResolver.class.getName());
    }
    
    private static class FilesystemIterator implements Iterator
    {
        List _certs;
        int _i;
        
        public FilesystemIterator(final List certs) {
            this._certs = null;
            this._certs = certs;
            this._i = 0;
        }
        
        public boolean hasNext() {
            return this._i < this._certs.size();
        }
        
        public Object next() {
            return this._certs.get(this._i++);
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
