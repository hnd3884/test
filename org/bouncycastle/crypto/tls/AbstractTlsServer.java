package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

public abstract class AbstractTlsServer extends AbstractTlsPeer implements TlsServer
{
    protected TlsCipherFactory cipherFactory;
    protected TlsServerContext context;
    protected ProtocolVersion clientVersion;
    protected int[] offeredCipherSuites;
    protected short[] offeredCompressionMethods;
    protected Hashtable clientExtensions;
    protected boolean encryptThenMACOffered;
    protected short maxFragmentLengthOffered;
    protected boolean truncatedHMacOffered;
    protected Vector supportedSignatureAlgorithms;
    protected boolean eccCipherSuitesOffered;
    protected int[] namedCurves;
    protected short[] clientECPointFormats;
    protected short[] serverECPointFormats;
    protected ProtocolVersion serverVersion;
    protected int selectedCipherSuite;
    protected short selectedCompressionMethod;
    protected Hashtable serverExtensions;
    
    public AbstractTlsServer() {
        this(new DefaultTlsCipherFactory());
    }
    
    public AbstractTlsServer(final TlsCipherFactory cipherFactory) {
        this.cipherFactory = cipherFactory;
    }
    
    protected boolean allowEncryptThenMAC() {
        return true;
    }
    
    protected boolean allowTruncatedHMac() {
        return false;
    }
    
    protected Hashtable checkServerExtensions() {
        return this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
    }
    
    protected abstract int[] getCipherSuites();
    
    protected short[] getCompressionMethods() {
        return new short[] { 0 };
    }
    
    protected ProtocolVersion getMaximumVersion() {
        return ProtocolVersion.TLSv11;
    }
    
    protected ProtocolVersion getMinimumVersion() {
        return ProtocolVersion.TLSv10;
    }
    
    protected boolean supportsClientECCCapabilities(final int[] array, final short[] array2) {
        if (array == null) {
            return TlsECCUtils.hasAnySupportedNamedCurves();
        }
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i];
            if (NamedCurve.isValid(n) && (!NamedCurve.refersToASpecificNamedCurve(n) || TlsECCUtils.isSupportedNamedCurve(n))) {
                return true;
            }
        }
        return false;
    }
    
    public void init(final TlsServerContext context) {
        this.context = context;
    }
    
    public void notifyClientVersion(final ProtocolVersion clientVersion) throws IOException {
        this.clientVersion = clientVersion;
    }
    
    public void notifyFallback(final boolean b) throws IOException {
        if (b && this.getMaximumVersion().isLaterVersionOf(this.clientVersion)) {
            throw new TlsFatalAlert((short)86);
        }
    }
    
    public void notifyOfferedCipherSuites(final int[] offeredCipherSuites) throws IOException {
        this.offeredCipherSuites = offeredCipherSuites;
        this.eccCipherSuitesOffered = TlsECCUtils.containsECCCipherSuites(this.offeredCipherSuites);
    }
    
    public void notifyOfferedCompressionMethods(final short[] offeredCompressionMethods) throws IOException {
        this.offeredCompressionMethods = offeredCompressionMethods;
    }
    
    public void processClientExtensions(final Hashtable clientExtensions) throws IOException {
        this.clientExtensions = clientExtensions;
        if (clientExtensions != null) {
            this.encryptThenMACOffered = TlsExtensionsUtils.hasEncryptThenMACExtension(clientExtensions);
            this.maxFragmentLengthOffered = TlsExtensionsUtils.getMaxFragmentLengthExtension(clientExtensions);
            if (this.maxFragmentLengthOffered >= 0 && !MaxFragmentLength.isValid(this.maxFragmentLengthOffered)) {
                throw new TlsFatalAlert((short)47);
            }
            this.truncatedHMacOffered = TlsExtensionsUtils.hasTruncatedHMacExtension(clientExtensions);
            this.supportedSignatureAlgorithms = TlsUtils.getSignatureAlgorithmsExtension(clientExtensions);
            if (this.supportedSignatureAlgorithms != null && !TlsUtils.isSignatureAlgorithmsExtensionAllowed(this.clientVersion)) {
                throw new TlsFatalAlert((short)47);
            }
            this.namedCurves = TlsECCUtils.getSupportedEllipticCurvesExtension(clientExtensions);
            this.clientECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(clientExtensions);
        }
    }
    
    public ProtocolVersion getServerVersion() throws IOException {
        if (this.getMinimumVersion().isEqualOrEarlierVersionOf(this.clientVersion)) {
            final ProtocolVersion maximumVersion = this.getMaximumVersion();
            if (this.clientVersion.isEqualOrEarlierVersionOf(maximumVersion)) {
                return this.serverVersion = this.clientVersion;
            }
            if (this.clientVersion.isLaterVersionOf(maximumVersion)) {
                return this.serverVersion = maximumVersion;
            }
        }
        throw new TlsFatalAlert((short)70);
    }
    
    public int getSelectedCipherSuite() throws IOException {
        final Vector usableSignatureAlgorithms = TlsUtils.getUsableSignatureAlgorithms(this.supportedSignatureAlgorithms);
        final boolean supportsClientECCCapabilities = this.supportsClientECCCapabilities(this.namedCurves, this.clientECPointFormats);
        final int[] cipherSuites = this.getCipherSuites();
        for (int i = 0; i < cipherSuites.length; ++i) {
            final int selectedCipherSuite = cipherSuites[i];
            if (Arrays.contains(this.offeredCipherSuites, selectedCipherSuite) && (supportsClientECCCapabilities || !TlsECCUtils.isECCCipherSuite(selectedCipherSuite)) && TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, this.serverVersion) && TlsUtils.isValidCipherSuiteForSignatureAlgorithms(selectedCipherSuite, usableSignatureAlgorithms)) {
                return this.selectedCipherSuite = selectedCipherSuite;
            }
        }
        throw new TlsFatalAlert((short)40);
    }
    
    public short getSelectedCompressionMethod() throws IOException {
        final short[] compressionMethods = this.getCompressionMethods();
        for (int i = 0; i < compressionMethods.length; ++i) {
            if (Arrays.contains(this.offeredCompressionMethods, compressionMethods[i])) {
                return this.selectedCompressionMethod = compressionMethods[i];
            }
        }
        throw new TlsFatalAlert((short)40);
    }
    
    public Hashtable getServerExtensions() throws IOException {
        if (this.encryptThenMACOffered && this.allowEncryptThenMAC() && TlsUtils.isBlockCipherSuite(this.selectedCipherSuite)) {
            TlsExtensionsUtils.addEncryptThenMACExtension(this.checkServerExtensions());
        }
        if (this.maxFragmentLengthOffered >= 0 && MaxFragmentLength.isValid(this.maxFragmentLengthOffered)) {
            TlsExtensionsUtils.addMaxFragmentLengthExtension(this.checkServerExtensions(), this.maxFragmentLengthOffered);
        }
        if (this.truncatedHMacOffered && this.allowTruncatedHMac()) {
            TlsExtensionsUtils.addTruncatedHMacExtension(this.checkServerExtensions());
        }
        if (this.clientECPointFormats != null && TlsECCUtils.isECCCipherSuite(this.selectedCipherSuite)) {
            this.serverECPointFormats = new short[] { 0, 1, 2 };
            TlsECCUtils.addSupportedPointFormatsExtension(this.checkServerExtensions(), this.serverECPointFormats);
        }
        return this.serverExtensions;
    }
    
    public Vector getServerSupplementalData() throws IOException {
        return null;
    }
    
    public CertificateStatus getCertificateStatus() throws IOException {
        return null;
    }
    
    public CertificateRequest getCertificateRequest() throws IOException {
        return null;
    }
    
    public void processClientSupplementalData(final Vector vector) throws IOException {
        if (vector != null) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    public void notifyClientCertificate(final Certificate certificate) throws IOException {
        throw new TlsFatalAlert((short)80);
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
    
    public NewSessionTicket getNewSessionTicket() throws IOException {
        return new NewSessionTicket(0L, TlsUtils.EMPTY_BYTES);
    }
}
