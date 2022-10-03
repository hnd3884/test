package com.adventnet.iam.security.antivirus.restapi.virustotal;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Objects;
import java.net.Proxy;

public class VirusTotal
{
    private final Engine engine;
    
    public static VirusTotal create(final String apikey, final String virustotalUrl) {
        return create(apikey, virustotalUrl, null);
    }
    
    public static VirusTotal create(final String apikey, final String virustotalUrl, final Proxy proxy) {
        if (!Objects.isNull(apikey) && !apikey.isEmpty()) {
            return new VirusTotal(apikey, virustotalUrl, proxy);
        }
        throw new VirusTotalException("Valid API key is required");
    }
    
    private VirusTotal(final String apikey, final String virustotalUrl, final Proxy proxy) {
        this.engine = new Engine(apikey, virustotalUrl, proxy);
    }
    
    public FileScanResponse.FileScanMetaData scanFile(final File file) throws FileNotFoundException, IOException {
        return this.engine.scanFile(file);
    }
    
    public FileScanResponse.FileScanMetaData scanFile(final String fileName, final FileInputStream inputStream) throws IOException {
        return this.engine.scanFile(fileName, inputStream);
    }
    
    public FileScanResponse.FileScanReport scanFileSync(final File file, final int pollingInterval, final int timeOut) throws FileNotFoundException, InterruptedException, ExecutionException, TimeoutException, IOException, VirusTotalException {
        return this.engine.scanFileSync(file, pollingInterval, timeOut);
    }
    
    public FileScanResponse.FileScanMetaData reScanFile(final String resource) {
        return this.engine.reScanFile(resource);
    }
    
    public List<FileScanResponse.FileScanMetaData> reScanFiles(final String[] resources) {
        return this.engine.reScanFiles(resources);
    }
    
    public FileScanResponse.FileScanReport getFileReport(final String resource) {
        return this.engine.getFileReport(resource);
    }
    
    public List<FileScanResponse.FileScanReport> getFilesReport(final String[] resources) {
        return this.engine.getFilesReport(resources);
    }
}
