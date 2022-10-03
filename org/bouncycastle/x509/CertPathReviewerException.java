package org.bouncycastle.x509;

import org.bouncycastle.i18n.ErrorBundle;
import java.security.cert.CertPath;
import org.bouncycastle.i18n.LocalizedException;

public class CertPathReviewerException extends LocalizedException
{
    private int index;
    private CertPath certPath;
    
    public CertPathReviewerException(final ErrorBundle errorBundle, final Throwable t) {
        super(errorBundle, t);
        this.index = -1;
        this.certPath = null;
    }
    
    public CertPathReviewerException(final ErrorBundle errorBundle) {
        super(errorBundle);
        this.index = -1;
        this.certPath = null;
    }
    
    public CertPathReviewerException(final ErrorBundle errorBundle, final Throwable t, final CertPath certPath, final int index) {
        super(errorBundle, t);
        this.index = -1;
        this.certPath = null;
        if (certPath == null || index == -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 || (certPath != null && index >= certPath.getCertificates().size())) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = index;
    }
    
    public CertPathReviewerException(final ErrorBundle errorBundle, final CertPath certPath, final int index) {
        super(errorBundle);
        this.index = -1;
        this.certPath = null;
        if (certPath == null || index == -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 || (certPath != null && index >= certPath.getCertificates().size())) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = index;
    }
    
    public CertPath getCertPath() {
        return this.certPath;
    }
    
    public int getIndex() {
        return this.index;
    }
}
