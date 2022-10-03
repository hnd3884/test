package com.adventnet.iam.security.antivirus.restapi;

import com.opswat.metadefender.core.client.HttpConnector;
import java.util.logging.Level;
import com.opswat.metadefender.core.client.responses.FileScanResult;
import com.opswat.metadefender.core.client.exceptions.MetadefenderClientException;
import com.adventnet.iam.security.antivirus.restapi.metadefender.MetadefenderScanResult;
import com.opswat.metadefender.core.client.responses.EngineScanDetail;
import java.io.InputStream;
import java.io.FileInputStream;
import com.opswat.metadefender.core.client.FileScanOptions;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.iam.security.antivirus.restapi.virustotal.VirusTotalException;
import com.adventnet.iam.security.antivirus.restapi.virustotal.FileScanResponse;
import com.adventnet.iam.security.antivirus.AVScanFailureInfo;
import com.adventnet.iam.security.antivirus.AVScanResult;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.iam.security.SecurityUtil;
import com.opswat.metadefender.core.client.MetadefenderCoreClient;
import com.adventnet.iam.security.antivirus.restapi.virustotal.VirusTotal;
import java.net.Proxy;
import java.util.logging.Logger;
import java.io.File;
import com.adventnet.iam.security.antivirus.VendorAV;

public final class RestApiVendorAV extends VendorAV<File>
{
    private static final Logger LOGGER;
    private static final int TIME_OUT = 120000;
    private static final int SCAN_RESULT_CHECK_INTERVAL = 10000;
    private final RestApiConfiguration.Vendor vendorName;
    private final String apiKey;
    private final String url;
    private final Proxy proxy;
    private transient VirusTotal virusTotalClient;
    private transient MetadefenderCoreClient metadefenderCoreClient;
    
    public static VendorAV<File> newInstance(final RestApiConfiguration restApiConfig) {
        if (!SecurityUtil.isValid(restApiConfig.getAPIKey())) {
            throw new IAMSecurityException("INVALID_AV_CONFIGURATION");
        }
        return new RestApiVendorAV(restApiConfig.getURL(), restApiConfig.getAPIKey(), restApiConfig.getVendorName(), restApiConfig.getProxy());
    }
    
    public RestApiVendorAV(final String apiKey, final RestApiConfiguration.Vendor vendorName) {
        this((vendorName == RestApiConfiguration.Vendor.METADEFENDER) ? "https://api.metadefender.com/v4" : "https://www.virustotal.com/vtapi/v2", apiKey, vendorName);
    }
    
    public RestApiVendorAV(final String url, final String apiKey, final RestApiConfiguration.Vendor vendorName) {
        this(url, apiKey, vendorName, null);
    }
    
    public RestApiVendorAV(final String url, final String apiKey, final RestApiConfiguration.Vendor vendorName, final Proxy proxy) {
        super("REST API [" + vendorName.value() + "]");
        this.vendorName = vendorName;
        this.apiKey = apiKey;
        this.url = url;
        this.proxy = proxy;
    }
    
    public AVScanResult<File> checkVirusUsingVirusTotal(final File file) throws InterruptedException, ExecutionException, TimeoutException, FileNotFoundException, IOException {
        try {
            final FileScanResponse.FileScanReport fileReport = this.virusTotalClient.scanFileSync(file, 10000, 120000);
            if (fileReport.getResponseCode() != 1) {
                return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, fileReport.getVerboseMessage());
            }
            final Map<String, FileScanResponse.FileScan> scans = fileReport.getScans();
            final StringBuilder foundedVirusWithEngineName = new StringBuilder();
            for (final String key : scans.keySet()) {
                final FileScanResponse.FileScan report = scans.get(key);
                if (report.isDetected()) {
                    foundedVirusWithEngineName.append("Founded Virus: ").append(report.getMalware()).append(" by Scan Engine: ").append(key).append('\n');
                }
            }
            if (foundedVirusWithEngineName.length() > 0) {
                return AVScanResult.virusDetected(foundedVirusWithEngineName.toString());
            }
        }
        catch (final VirusTotalException e) {
            if (e.getFailedCause() != null) {
                return AVScanResult.failed(e.getFailedCause(), e.getLocalizedMessage());
            }
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getLocalizedMessage());
        }
        return AVScanResult.completed();
    }
    
    public AVScanResult<File> checkVirusUsingMetaDefender(final File file) throws FileNotFoundException, InterruptedException, ExecutionException, TimeoutException {
        try {
            final FileScanOptions fileScanOption = new FileScanOptions();
            final FileScanOptions scanResultRequestHeaders = new FileScanOptions();
            fileScanOption.setApiKey(this.apiKey);
            fileScanOption.setContentType("application/octet-stream");
            scanResultRequestHeaders.setApiKey(this.apiKey);
            final FileScanResult result = this.metadefenderCoreClient.scanFileSync((InputStream)new FileInputStream(file), fileScanOption, scanResultRequestHeaders, 10000, 120000);
            final Map<String, EngineScanDetail> engineResults = result.scan_results.scan_details;
            final StringBuilder foundedVirusWithEngineName = new StringBuilder();
            for (final String key : engineResults.keySet()) {
                final EngineScanDetail details = engineResults.get(key);
                final MetadefenderScanResult metaScanResult = MetadefenderScanResult.getScanResultFromCode(details.scan_result_i);
                if (metaScanResult == null) {
                    continue;
                }
                if (metaScanResult == MetadefenderScanResult.INFECTED || metaScanResult == MetadefenderScanResult.SUSPICIOUS) {
                    foundedVirusWithEngineName.append("Founded Virus: ").append(details.threat_found).append(" by Scan Engine: ").append(key).append('\n');
                }
                else {
                    if (metaScanResult == MetadefenderScanResult.NO_THREAD_FOUND) {
                        continue;
                    }
                    return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, metaScanResult.description());
                }
            }
            if (foundedVirusWithEngineName.length() > 0) {
                return AVScanResult.virusDetected(foundedVirusWithEngineName.toString());
            }
        }
        catch (final MetadefenderClientException e) {
            if (e.getFailedCause() != null) {
                return AVScanResult.failed(e.getFailedCause(), e.getDetailedMessage());
            }
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getDetailedMessage());
        }
        return AVScanResult.completed();
    }
    
    public AVScanResult<File> scanImpl(final File file) {
        try {
            switch (this.vendorName) {
                case VIRUSTOTAL: {
                    return this.checkVirusUsingVirusTotal(file);
                }
                case METADEFENDER: {
                    return this.checkVirusUsingMetaDefender(file);
                }
                default: {
                    return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, "Invalid vendor name");
                }
            }
        }
        catch (final TimeoutException e) {
            RestApiVendorAV.LOGGER.log(Level.SEVERE, " Scan Failed Time Out");
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_SCAN_TIME_OUT, e.getMessage());
        }
        catch (final Exception e2) {
            RestApiVendorAV.LOGGER.log(Level.SEVERE, e2.getMessage());
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e2.getMessage());
        }
    }
    
    public AVScanResult<File> init() {
        try {
            switch (this.vendorName) {
                case VIRUSTOTAL: {
                    this.virusTotalClient = VirusTotal.create(this.apiKey, this.url, this.proxy);
                    break;
                }
                case METADEFENDER: {
                    (this.metadefenderCoreClient = new MetadefenderCoreClient(this.url)).setHttpConnector(new HttpConnector(this.proxy));
                    break;
                }
            }
        }
        catch (final Exception e) {
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
        return AVScanResult.completed();
    }
    
    @Override
    protected void closeImpl() {
        this.virusTotalClient = null;
        this.metadefenderCoreClient = null;
    }
    
    static {
        LOGGER = Logger.getLogger(RestApiVendorAV.class.getName());
    }
}
