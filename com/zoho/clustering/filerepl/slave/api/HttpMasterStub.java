package com.zoho.clustering.filerepl.slave.api;

import java.util.Iterator;
import java.util.List;
import java.io.File;
import com.zoho.clustering.util.MyProperties;
import java.util.logging.Logger;
import com.zoho.clustering.util.UrlUtil;
import java.io.InputStream;
import com.zoho.clustering.util.HttpMethod;
import java.io.Closeable;
import com.zoho.clustering.util.FileUtil;
import java.io.IOException;
import com.zoho.clustering.filerepl.event.EventList;
import com.zoho.clustering.filerepl.event.EventLogPosition;

public class HttpMasterStub implements MasterStub
{
    private HttpMasterStubConfig config;
    
    public HttpMasterStub(final HttpMasterStubConfig config) {
        this.config = null;
        this.config = config;
    }
    
    @Override
    public EventList getEvents(final EventLogPosition curr, final int noOfEvents) throws APIException, APIResourceException {
        final HttpMethod httpMeth = this.createHttpMethod(this.getFetchEventsURL(curr, noOfEvents));
        InputStream responseStream = null;
        try {
            final int status = httpMeth.execute();
            if (status == 200) {
                responseStream = httpMeth.getResponseAsStream();
                return APIDeserializer.getInst().parseGetEventsResponse(responseStream);
            }
            if (status == 502 || status == 503 || status == 504) {
                throw new APIResourceException("HTTP GET failed with status [" + status + "].URL [" + httpMeth.getURL() + "]");
            }
            throw new RuntimeException("HTTP GET failed with status [" + status + "].URL [" + httpMeth.getURL() + "]");
        }
        catch (final IOException exp) {
            throw new APIResourceException(exp);
        }
        finally {
            FileUtil.Close(responseStream);
        }
    }
    
    @Override
    public InputStream downloadFile(final int baseDirId, final String filePath) throws APIException, APIResourceException {
        final HttpMethod httpGet = this.createHttpMethod(this.getDownloadFileURL(baseDirId, filePath));
        try {
            final int status = httpGet.execute();
            if (status == 200) {
                return httpGet.getResponseAsStream();
            }
            if (status == 404) {
                return null;
            }
            if (status == 502 || status == 503 || status == 504) {
                throw new APIResourceException("HTTP GET failed with status [" + status + "].URL [" + httpGet.getURL() + "]");
            }
            throw new APIException("HTTP GET for URL [" + httpGet.getURL() + "] failed with status [" + status + "].");
        }
        catch (final IOException exp) {
            throw new APIResourceException(exp);
        }
    }
    
    @Override
    public String takeSnapshot(final boolean resetLog) throws APIException, APIResourceException {
        final HttpMethod httpGet = this.createHttpMethod(this.getTakeSnapshotURL(resetLog));
        InputStream responseStream = null;
        try {
            final int status = httpGet.execute();
            if (status == 200) {
                responseStream = httpGet.getResponseAsStream();
                return APIDeserializer.getInst().parseTakeSnapshotResponse(responseStream);
            }
            if (status == 502 || status == 503 || status == 504) {
                throw new APIResourceException("HTTP GET failed with status [" + status + "].URL [" + httpGet.getURL() + "]");
            }
            throw new RuntimeException("HTTP GET failed with status [" + status + "].URL [" + httpGet.getURL() + "]");
        }
        catch (final IOException exp) {
            throw new APIResourceException(exp);
        }
        finally {
            FileUtil.Close(responseStream);
        }
    }
    
    @Override
    public InputStream downloadSnapshot(final String snapshotName) throws APIException, APIResourceException {
        final HttpMethod httpGet = this.createHttpMethod(this.getDownloadSnapshotURL(snapshotName));
        httpGet.setReadTimeout(this.config.readTimeoutMillis() * 2);
        try {
            final int status = httpGet.execute();
            if (status == 200) {
                return httpGet.getResponseAsStream();
            }
            if (status == 404) {
                return null;
            }
            if (status == 502 || status == 503 || status == 504) {
                throw new APIResourceException("HTTP GET failed with status [" + status + "].URL [" + httpGet.getURL() + "]");
            }
            throw new APIException("HTTP GET for URL [" + httpGet.getURL() + "] failed with status [" + status + "].");
        }
        catch (final IOException exp) {
            throw new APIResourceException(exp);
        }
    }
    
    private HttpMethod createHttpMethod(final String url) {
        final HttpMethod httpMeth = new HttpMethod(url);
        httpMeth.setConnectionTimeout(this.config.connTimeoutMillis());
        httpMeth.setReadTimeout(this.config.readTimeoutMillis());
        httpMeth.setHostnameVerifierClassName(this.config.hostNameVerifierClassName());
        return httpMeth;
    }
    
    private String getFetchEventsURL(final EventLogPosition logPos, final int noOfEvents) {
        return UrlUtil.createURL(this.config.masterURL(), this.config.uriGetEvents()) + "?log-pos=" + logPos.toString() + "&count=" + noOfEvents;
    }
    
    private String getDownloadFileURL(final int baseDirId, final String filePath) {
        return UrlUtil.createURL(this.config.masterURL(), this.config.uriDownloadFile()) + "?basedir=" + baseDirId + "&filepath=" + UrlUtil.encode(filePath);
    }
    
    private String getTakeSnapshotURL(final boolean resetLog) {
        return UrlUtil.createURL(this.config.masterURL(), this.config.uriTakeSnapshot()) + "?resetlog=" + String.valueOf(resetLog);
    }
    
    private String getDownloadSnapshotURL(final String snapshotName) {
        return UrlUtil.createURL(this.config.masterURL(), this.config.uriDownloadSnapshot()) + "?name=" + UrlUtil.encode(snapshotName);
    }
    
    public static class Test
    {
        private static Logger logger;
        
        public static void main(final String[] args) {
            final MyProperties props = new MyProperties("../conf/repl-slave.conf");
            final HttpMasterStubConfig config = new HttpMasterStubConfig("clustering.filerepl.slave.httpMasterStub", props);
            config.setMasterURL(args[0]);
            config.makeImmutable();
            final HttpMasterStub master = new HttpMasterStub(config);
            testSnapshot(master);
        }
        
        private static void testSnapshot(final HttpMasterStub master) {
            final String snapshotName = master.takeSnapshot(true);
            Test.logger.info("Name: " + snapshotName);
            final InputStream in = master.downloadSnapshot(snapshotName);
            Test.logger.info("download over");
            FileUtil.copyToFile(in, new File(snapshotName));
            FileUtil.Close(in);
        }
        
        private static void testDownload(final HttpMasterStub master, final int baseDir, final String filePath) {
            final InputStream in = master.downloadFile(baseDir, filePath);
            FileUtil.copyToFile(in, new File("tt.txt"));
            FileUtil.Close(in);
        }
        
        private static void testGetEvents(final HttpMasterStub master, final EventLogPosition offset, final int noOfEvents) {
            final EventList eventList = master.getEvents(offset, noOfEvents);
            Test.logger.info("Next:" + eventList.getNextPos());
            final List<String> events = eventList.getEvents();
            for (final String event : events) {
                Test.logger.info("Event: " + event);
            }
        }
        
        static {
            Test.logger = Logger.getLogger(Test.class.getName());
        }
    }
}
