package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.io.File;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;

public class CertsInFilesystemDirectoryResolver extends StorageResolverSpi
{
    private static final Logger LOG;
    private String merlinsCertificatesDir;
    private List<X509Certificate> certs;
    
    public CertsInFilesystemDirectoryResolver(final String merlinsCertificatesDir) throws StorageResolverException {
        this.certs = new ArrayList<X509Certificate>();
        this.merlinsCertificatesDir = merlinsCertificatesDir;
        this.readCertsFromHarddrive();
    }
    
    private void readCertsFromHarddrive() throws StorageResolverException {
        final File file = new File(this.merlinsCertificatesDir);
        final ArrayList list = new ArrayList();
        final String[] list2 = file.list();
        if (list2 != null) {
            for (int i = 0; i < list2.length; ++i) {
                if (list2[i].endsWith(".crt")) {
                    list.add(list2[i]);
                }
            }
        }
        CertificateFactory instance;
        try {
            instance = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {
            throw new StorageResolverException(ex);
        }
        for (int j = 0; j < list.size(); ++j) {
            final String string = file.getAbsolutePath() + File.separator + (String)list.get(j);
            boolean b = false;
            Object name = null;
            try (final InputStream inputStream = Files.newInputStream(Paths.get(string, new String[0]), new OpenOption[0])) {
                final X509Certificate x509Certificate = (X509Certificate)instance.generateCertificate(inputStream);
                x509Certificate.checkValidity();
                this.certs.add(x509Certificate);
                name = x509Certificate.getSubjectX500Principal().getName();
                b = true;
            }
            catch (final FileNotFoundException ex2) {
                if (CertsInFilesystemDirectoryResolver.LOG.isDebugEnabled()) {
                    CertsInFilesystemDirectoryResolver.LOG.debug("Could not add certificate from file " + string, ex2);
                }
            }
            catch (final CertificateNotYetValidException ex3) {
                if (CertsInFilesystemDirectoryResolver.LOG.isDebugEnabled()) {
                    CertsInFilesystemDirectoryResolver.LOG.debug("Could not add certificate from file " + string, ex3);
                }
            }
            catch (final CertificateExpiredException ex4) {
                if (CertsInFilesystemDirectoryResolver.LOG.isDebugEnabled()) {
                    CertsInFilesystemDirectoryResolver.LOG.debug("Could not add certificate from file " + string, ex4);
                }
            }
            catch (final CertificateException ex5) {
                if (CertsInFilesystemDirectoryResolver.LOG.isDebugEnabled()) {
                    CertsInFilesystemDirectoryResolver.LOG.debug("Could not add certificate from file " + string, ex5);
                }
            }
            catch (final IOException ex6) {
                if (CertsInFilesystemDirectoryResolver.LOG.isDebugEnabled()) {
                    CertsInFilesystemDirectoryResolver.LOG.debug("Could not add certificate from file " + string, ex6);
                }
            }
            if (b) {
                CertsInFilesystemDirectoryResolver.LOG.debug("Added certificate: {}", name);
            }
        }
    }
    
    @Override
    public Iterator<Certificate> getIterator() {
        return new FilesystemIterator(this.certs);
    }
    
    public static void main(final String[] array) throws Exception {
        final Iterator<Certificate> iterator = new CertsInFilesystemDirectoryResolver("data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs").getIterator();
        while (iterator.hasNext()) {
            final X509Certificate x509Certificate = iterator.next();
            final byte[] skiBytesFromCert = XMLX509SKI.getSKIBytesFromCert(x509Certificate);
            System.out.println();
            System.out.println("Base64(SKI())=                 '" + XMLUtils.encodeToString(skiBytesFromCert) + "'");
            System.out.println("cert.getSerialNumber()=        '" + x509Certificate.getSerialNumber().toString() + "'");
            System.out.println("cert.getSubjectX500Principal().getName()= '" + x509Certificate.getSubjectX500Principal().getName() + "'");
            System.out.println("cert.getIssuerX500Principal().getName()=  '" + x509Certificate.getIssuerX500Principal().getName() + "'");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CertsInFilesystemDirectoryResolver.class);
    }
    
    private static class FilesystemIterator implements Iterator<Certificate>
    {
        private List<X509Certificate> certs;
        private int i;
        
        public FilesystemIterator(final List<X509Certificate> certs) {
            this.certs = certs;
            this.i = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this.i < this.certs.size();
        }
        
        @Override
        public Certificate next() {
            return this.certs.get(this.i++);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
