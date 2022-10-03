package org.bouncycastle.crypto.tls;

import java.util.Hashtable;
import java.io.IOException;
import java.util.Vector;

public abstract class AbstractTlsClient extends AbstractTlsPeer implements TlsClient
{
    protected TlsCipherFactory cipherFactory;
    protected TlsClientContext context;
    protected Vector supportedSignatureAlgorithms;
    protected int[] namedCurves;
    protected short[] clientECPointFormats;
    protected short[] serverECPointFormats;
    protected int selectedCipherSuite;
    protected short selectedCompressionMethod;
    
    public AbstractTlsClient() {
        this(new DefaultTlsCipherFactory());
    }
    
    public AbstractTlsClient(final TlsCipherFactory cipherFactory) {
        this.cipherFactory = cipherFactory;
    }
    
    protected boolean allowUnexpectedServerExtension(final Integer n, final byte[] array) throws IOException {
        switch (n) {
            case 10: {
                TlsECCUtils.readSupportedEllipticCurvesExtension(array);
                return true;
            }
            case 11: {
                TlsECCUtils.readSupportedPointFormatsExtension(array);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    protected void checkForUnexpectedServerExtension(final Hashtable hashtable, final Integer n) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, n);
        if (extensionData != null && !this.allowUnexpectedServerExtension(n, extensionData)) {
            throw new TlsFatalAlert((short)47);
        }
    }
    
    public void init(final TlsClientContext context) {
        this.context = context;
    }
    
    public TlsSession getSessionToResume() {
        return null;
    }
    
    public ProtocolVersion getClientHelloRecordLayerVersion() {
        return this.getClientVersion();
    }
    
    public ProtocolVersion getClientVersion() {
        return ProtocolVersion.TLSv12;
    }
    
    public boolean isFallback() {
        return false;
    }
    
    public Hashtable getClientExtensions() throws IOException {
        Hashtable hashtable = null;
        if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(this.context.getClientVersion())) {
            this.supportedSignatureAlgorithms = TlsUtils.getDefaultSupportedSignatureAlgorithms();
            hashtable = TlsExtensionsUtils.ensureExtensionsInitialised(hashtable);
            TlsUtils.addSignatureAlgorithmsExtension(hashtable, this.supportedSignatureAlgorithms);
        }
        if (TlsECCUtils.containsECCCipherSuites(this.getCipherSuites())) {
            this.namedCurves = new int[] { 23, 24 };
            this.clientECPointFormats = new short[] { 0, 1, 2 };
            hashtable = TlsExtensionsUtils.ensureExtensionsInitialised(hashtable);
            TlsECCUtils.addSupportedEllipticCurvesExtension(hashtable, this.namedCurves);
            TlsECCUtils.addSupportedPointFormatsExtension(hashtable, this.clientECPointFormats);
        }
        return hashtable;
    }
    
    public ProtocolVersion getMinimumVersion() {
        return ProtocolVersion.TLSv10;
    }
    
    public void notifyServerVersion(final ProtocolVersion protocolVersion) throws IOException {
        if (!this.getMinimumVersion().isEqualOrEarlierVersionOf(protocolVersion)) {
            throw new TlsFatalAlert((short)70);
        }
    }
    
    public short[] getCompressionMethods() {
        return new short[] { 0 };
    }
    
    public void notifySessionID(final byte[] array) {
    }
    
    public void notifySelectedCipherSuite(final int selectedCipherSuite) {
        this.selectedCipherSuite = selectedCipherSuite;
    }
    
    public void notifySelectedCompressionMethod(final short selectedCompressionMethod) {
        this.selectedCompressionMethod = selectedCompressionMethod;
    }
    
    public void processServerExtensions(final Hashtable hashtable) throws IOException {
        if (hashtable != null) {
            this.checkForUnexpectedServerExtension(hashtable, TlsUtils.EXT_signature_algorithms);
            this.checkForUnexpectedServerExtension(hashtable, TlsECCUtils.EXT_elliptic_curves);
            if (TlsECCUtils.isECCCipherSuite(this.selectedCipherSuite)) {
                this.serverECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(hashtable);
            }
            else {
                this.checkForUnexpectedServerExtension(hashtable, TlsECCUtils.EXT_ec_point_formats);
            }
            this.checkForUnexpectedServerExtension(hashtable, TlsExtensionsUtils.EXT_padding);
        }
    }
    
    public void processServerSupplementalData(final Vector vector) throws IOException {
        if (vector != null) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    public Vector getClientSupplementalData() throws IOException {
        return null;
    }
    
    public TlsCompression getCompression() throws IOException {
        switch (this.selectedCompressionMethod) {
            case 0: {
                return new TlsNullCompression();
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public TlsCipher getCipher() throws IOException {
        return this.cipherFactory.createCipher(this.context, TlsUtils.getEncryptionAlgorithm(this.selectedCipherSuite), TlsUtils.getMACAlgorithm(this.selectedCipherSuite));
    }
    
    public void notifyNewSessionTicket(final NewSessionTicket newSessionTicket) throws IOException {
    }
}
