package com.adventnet.iam.security.antivirus.restapi.virustotal;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;
import java.util.List;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.net.UnknownHostException;
import com.adventnet.iam.security.antivirus.AVScanFailureInfo;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Objects;
import java.io.File;
import java.net.Proxy;

class Engine
{
    private static final String FILE_SCAN_URL = "/file/scan";
    private static final String FILE_REPORT_URL = "/file/report";
    private static final String FILE_RE_SCAN_URL = "/file/rescan";
    private final String apikey;
    private final String url;
    private final Proxy proxy;
    private final FileScanResponse responseGson;
    
    Engine(final String apikey, final String virustotalUrl, final Proxy proxy) {
        this.apikey = apikey;
        this.url = virustotalUrl;
        this.proxy = proxy;
        this.responseGson = new FileScanResponse();
    }
    
    FileScanResponse.FileScanMetaData scanFile(final File file) throws VirusTotalException, IOException {
        if (!Objects.isNull(file) && file.canRead()) {
            final String fileName = file.getName();
            try (final FileInputStream fileInputStream = new FileInputStream(file)) {
                return this.scanFile(fileName, fileInputStream);
            }
        }
        throw new VirusTotalException("A valid java.io.File is required");
    }
    
    FileScanResponse.FileScanMetaData scanFile(final String fileName, final FileInputStream inputStream) throws VirusTotalException, IOException {
        if (Objects.isNull(fileName) || fileName.isEmpty()) {
            throw new VirusTotalException("valid file name is required");
        }
        try {
            if (!Objects.isNull(inputStream)) {
                final MultipartEntity multipartEntity = new MultipartEntity();
                multipartEntity.addHeader("Transfer-Encoding", "chunked");
                multipartEntity.addTextBody("apikey", this.apikey);
                multipartEntity.addBinaryBody("file", fileName, inputStream);
                final HttpsClient httpClient = new HttpsClient(this.url + "/file/scan", multipartEntity, this.proxy);
                final String response = httpClient.execute();
                return this.responseGson.parseFileScanMetadata(response, false).get(0);
            }
            throw new VirusTotalException("valid java.io.FileInputStream is required");
        }
        catch (final UnknownHostException e) {
            throw new VirusTotalException(AVScanFailureInfo.FailedCause.AV_CONNECTION_FAILED, "Cannot connect to: " + this.url + " " + e.getMessage());
        }
    }
    
    FileScanResponse.FileScanReport scanFileSync(final File file, final int pollingInterval, final int timeOut) throws InterruptedException, ExecutionException, TimeoutException, FileNotFoundException, IOException, VirusTotalException {
        final FileScanResponse.FileScanMetaData scanFile = this.scanFile(file);
        final String scanID = scanFile.getScanId();
        FileScanResponse.FileScanReport fileReport = null;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<FileScanResponse.FileScanReport> future = executor.submit((Callable<FileScanResponse.FileScanReport>)new VirtualTotalReportCheckTask(scanID, pollingInterval));
        try {
            fileReport = future.get(timeOut, TimeUnit.MILLISECONDS);
        }
        finally {
            future.cancel(true);
            executor.shutdownNow();
        }
        return fileReport;
    }
    
    FileScanResponse.FileScanMetaData reScanFile(final String resource) {
        if (!Objects.isNull(resource) && !resource.isEmpty()) {
            final String response = this.getReports("resource", resource, this.url + "/file/rescan", true, false);
            return this.responseGson.parseFileScanMetadata(response, false).get(0);
        }
        throw new VirusTotalException("A valid resource is required");
    }
    
    List<FileScanResponse.FileScanMetaData> reScanFiles(final String[] resources) {
        if (Objects.isNull(resources) || resources.length <= 0) {
            throw new VirusTotalException("atleast one resource is required");
        }
        final String resourceString = Arrays.toString(resources);
        final String resource = resourceString.substring(1, resourceString.length() - 1);
        final String response = this.getReports("resource", resource, this.url + "/file/rescan", true, false);
        if (resources.length > 1) {
            return this.responseGson.parseFileScanMetadata(response, true);
        }
        return this.responseGson.parseFileScanMetadata(response, false);
    }
    
    private String getReports(final String type, final String resource, final String reportURL, final boolean isPost, final boolean doURLScan) {
        final MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addHeader("Transfer-Encoding", "chunked");
        if (isPost) {
            multipartEntity.addTextBody("apikey", this.apikey);
            multipartEntity.addTextBody(type, resource);
            if (doURLScan) {
                multipartEntity.addTextBody("scan", "1");
            }
            try {
                final HttpsClient httpsClient = new HttpsClient(reportURL, multipartEntity, this.proxy);
                return httpsClient.execute();
            }
            catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        final ConcurrentHashMap<String, String> parameters = new ConcurrentHashMap<String, String>();
        parameters.put("apikey", this.apikey);
        parameters.put(type, resource);
        try {
            final HttpsClient httpClient = new HttpsClient(reportURL, HttpsClient.prepareParameters(parameters), this.proxy);
            return httpClient.execute();
        }
        catch (final IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    FileScanResponse.FileScanReport getFileReport(final String resource) {
        if (!Objects.isNull(resource) && !resource.isEmpty()) {
            final String response = this.getReports("resource", resource, this.url + "/file/report", true, false);
            return this.responseGson.parseFileReports(response, false).get(0);
        }
        throw new VirusTotalException("valid file report resource is required");
    }
    
    List<FileScanResponse.FileScanReport> getFilesReport(final String[] resources) {
        if (Objects.isNull(resources) || resources.length <= 0) {
            throw new VirusTotalException("valid file report resource is required");
        }
        final String resourceString = Arrays.toString(resources);
        final String resource = resourceString.substring(1, resourceString.length() - 1);
        final String response = this.getReports("resource", resource, this.url + "/file/report", true, false);
        if (resources.length > 1) {
            return this.responseGson.parseFileReports(response, true);
        }
        return this.responseGson.parseFileReports(response, false);
    }
    
    class VirtualTotalReportCheckTask implements Callable<FileScanResponse.FileScanReport>
    {
        private final int pollingInterval;
        private final String scanID;
        
        public VirtualTotalReportCheckTask(final String id, final int interval) {
            this.scanID = id;
            this.pollingInterval = interval;
        }
        
        @Override
        public FileScanResponse.FileScanReport call() throws Exception {
            FileScanResponse.FileScanReport fileScanReport;
            do {
                fileScanReport = Engine.this.getFileReport(this.scanID);
                if (!this.isScanFinished(fileScanReport)) {
                    TimeUnit.MILLISECONDS.sleep(this.pollingInterval);
                }
            } while (!this.isScanFinished(fileScanReport));
            return fileScanReport;
        }
        
        private boolean isScanFinished(final FileScanResponse.FileScanReport fileScanReport) {
            return fileScanReport != null && fileScanReport.getVerboseMessage().contains("Scan finished");
        }
    }
}
