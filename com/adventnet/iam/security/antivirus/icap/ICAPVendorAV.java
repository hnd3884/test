package com.adventnet.iam.security.antivirus.icap;

import com.adventnet.iam.security.SecurityUtil;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import ch.mimo.netty.handler.codec.icap.IcapResponseStatus;
import ch.mimo.netty.handler.codec.icap.IcapResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.adventnet.iam.security.antivirus.AVScanFailureInfo;
import com.adventnet.iam.security.antivirus.AVScanResult;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Logger;
import java.io.File;
import com.adventnet.iam.security.antivirus.VendorAV;

public class ICAPVendorAV extends VendorAV<File>
{
    private static final Logger LOGGER;
    private static final byte[] EICAR_AV_TEST_BYTE;
    private transient IcapClient client;
    private final String host;
    private final int port;
    private final String service;
    private int readTimeout;
    private boolean isPreviewModeSupport;
    private Integer previewSize;
    private boolean isAsyncScanMode;
    
    public ICAPVendorAV(final String host, final int port, final String service, final String vendor) {
        super("ICAP [" + vendor + "]");
        this.readTimeout = 60000;
        this.host = host;
        this.port = port;
        this.service = service;
    }
    
    public ICAPVendorAV(final String host, final int port, final String service) {
        this(host, port, service, IcapAvConfiguration.Vendor.UNKNOWN_VENDOR.value());
    }
    
    public static ICAPVendorAV newInstance(final IcapAvConfiguration icapConfig) {
        final String customService = icapConfig.getService();
        final String host = icapConfig.getHost();
        final int port = icapConfig.getPort();
        if (!isValidHost(host) || !isValidPort(port)) {
            throw new IAMSecurityException("INVALID_AV_CONFIGURATION");
        }
        final ICAPVendorAV icapVendor = new ICAPVendorAV(host, port, customService, icapConfig.getVendorName().value());
        if (icapConfig.isPreviewModeSupported()) {
            icapVendor.setPreviewModeSupport(icapConfig.isPreviewModeSupported());
            icapVendor.setPreviewSize(icapConfig.getPreviewSize());
        }
        icapVendor.setReadTimeout(icapConfig.getReadTimeout());
        return icapVendor;
    }
    
    protected static ICAPVendorAV newAsyncInstance(final IcapAvConfiguration icapConfig) {
        final ICAPVendorAV asyncVendorAV = newInstance(icapConfig);
        asyncVendorAV.isAsyncScanMode = true;
        return asyncVendorAV;
    }
    
    protected static AVScanResult<File> getAsyncScanInitiatedResult(final ICAPVendorAV asyncVendorAV) {
        if (asyncVendorAV == null || asyncVendorAV.client == null) {
            throw new IllegalArgumentException("Invalid argument");
        }
        if (!asyncVendorAV.isAsyncScanMode) {
            throw new IllegalStateException("Async mode not enabled for given IcapVendorAV");
        }
        AVScanResult<File> result;
        try {
            result = handleIcapResponse(asyncVendorAV.client.getIcapResponse());
        }
        catch (final InterruptedException e) {
            result = AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
        return result;
    }
    
    public boolean isAsyncScanModeEnabled() {
        return this.isAsyncScanMode;
    }
    
    private AVScanResult<File> scanFile(final File file) throws IcapClientException, IOException, InterruptedException {
        try (final InputStream fileInputStream = new FileInputStream(file)) {
            return this.scanStream(fileInputStream);
        }
    }
    
    private AVScanResult<File> scanStream(final InputStream inputStream) throws IOException, InterruptedException, IcapClientException {
        if (this.isPreviewModeSupport) {
            this.client.send(inputStream, this.previewSize);
        }
        else {
            this.client.send(inputStream, null);
        }
        if (this.isAsyncScanMode) {
            return AVScanResult.completed();
        }
        return handleIcapResponse(this.client.getIcapResponse());
    }
    
    private static AVScanResult<File> handleIcapResponse(final IcapResponse response) {
        if (response.getStatus().equals((Object)IcapResponseStatus.OK)) {
            if (response.getHeader("X-Infection-Found") != null) {
                return AVScanResult.virusDetected(response.getHeader("X-Infection-Found"));
            }
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_FILE_SIZE_LIMIT_EXCEED, "Response status is: " + response.toString());
        }
        else {
            if (response.getStatus().equals((Object)IcapResponseStatus.NO_CONTENT)) {
                return AVScanResult.completed();
            }
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, "Invalid response from ICAP server. Response Status is: " + response.toString());
        }
    }
    
    private void setPreviewSize() throws IcapClientException, InterruptedException {
        this.previewSize = this.getPreviewSizeFromIcapServer();
        if (this.previewSize != null) {
            ICAPVendorAV.LOGGER.log(Level.INFO, "Preview Size set by automatically");
        }
        else {
            ICAPVendorAV.LOGGER.log(Level.INFO, "Preview Size not supported");
        }
    }
    
    public void setPreviewSize(final Integer previewSize) {
        this.previewSize = previewSize;
    }
    
    public void setPreviewModeSupport(final boolean isPreviewModeSupported) {
        this.isPreviewModeSupport = isPreviewModeSupported;
    }
    
    public void setReadTimeout(final int timeout) {
        this.readTimeout = timeout;
    }
    
    public static Integer getPreviewSize(final String host, final String service) {
        return getPreviewSize(host, 1344, service);
    }
    
    public static Integer getPreviewSize(final String host, final int port, final String service) {
        try (final ICAPVendorAV icapVendorAV = new ICAPVendorAV(host, port, service)) {
            icapVendorAV.init();
            return icapVendorAV.getPreviewSizeFromIcapServer();
        }
        catch (final IcapClientException | InterruptedException e) {
            ICAPVendorAV.LOGGER.log(Level.SEVERE, "Exception while retrieving the previews size from Host: \"{0}\" Port: \"{1}\" Service:\"{2}\"", new Object[] { host, port, service });
            return null;
        }
    }
    
    public static boolean testIcapServer(final String host, final int port, final String service) {
        try (final ICAPVendorAV icapVendorAV = new ICAPVendorAV(host, port, service)) {
            AVScanResult<File> avScanResult = icapVendorAV.init();
            if (avScanResult.status() != AVScanResult.Status.COMPLETED) {
                return false;
            }
            try {
                avScanResult = icapVendorAV.scanStream(new ByteArrayInputStream(ICAPVendorAV.EICAR_AV_TEST_BYTE));
                return avScanResult.status() == AVScanResult.Status.VIRUS_DETECTED;
            }
            catch (final IcapClientException | IOException | InterruptedException e) {
                return false;
            }
        }
    }
    
    public Integer getPreviewSizeFromIcapServer() throws IcapClientException, InterruptedException {
        final IcapResponse optionsResponse = this.client.sendOption();
        if (optionsResponse.getStatus() != IcapResponseStatus.OK) {
            throw new IcapClientException(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, "Invalid response from ICAP server when send ICAP Options request. Response Status is: " + optionsResponse.getStatus());
        }
        final String preview = optionsResponse.getHeader("Preview");
        if (preview != null) {
            return Integer.parseInt(preview);
        }
        return null;
    }
    
    @Override
    protected AVScanResult<File> scanImpl(final File file) {
        try {
            return this.scanFile(file);
        }
        catch (final IcapClientException e) {
            if (e.getFailedCause() != null) {
                return AVScanResult.failed(e.getFailedCause(), e.getMessage());
            }
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
        catch (final IOException | InterruptedException e2) {
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e2.getMessage());
        }
    }
    
    @Override
    protected AVScanResult<File> init() {
        try {
            (this.client = new IcapClient(this.host, this.port, this.service)).setResonseTimeOut(this.readTimeout);
            this.client.initChannel();
            if (this.isPreviewModeSupport && this.previewSize == null) {
                this.setPreviewSize();
            }
            return AVScanResult.completed();
        }
        catch (final IcapClientException e) {
            if (e.getFailedCause() != null) {
                return AVScanResult.failed(e.getFailedCause(), e.getMessage());
            }
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
        catch (final InterruptedException e2) {
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e2.getMessage());
        }
    }
    
    @Override
    protected void closeImpl() {
        if (this.client == null) {
            return;
        }
        this.client.close();
        if (!this.client.isClosed()) {
            ICAPVendorAV.LOGGER.log(Level.WARNING, "Channel close failed. Exception: {0}", (this.client.getCloseCause() != null) ? this.client.getCloseCause().getLocalizedMessage() : "");
        }
    }
    
    private static boolean isValidPort(final int port) {
        return port >= 0;
    }
    
    private static boolean isValidHost(final String host) {
        return SecurityUtil.isValid(host);
    }
    
    static {
        LOGGER = Logger.getLogger(ICAPVendorAV.class.getName());
        EICAR_AV_TEST_BYTE = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes();
    }
}
