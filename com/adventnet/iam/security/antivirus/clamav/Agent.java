package com.adventnet.iam.security.antivirus.clamav;

import java.util.regex.Matcher;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import com.adventnet.iam.security.antivirus.AVScanFailureInfo;
import java.io.InputStream;
import java.io.FileInputStream;
import com.adventnet.iam.security.antivirus.AVScanResult;
import java.io.File;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class Agent
{
    private static final Logger LOGGER;
    static final char NULL_CHAR = '\0';
    private static final byte[] ZOHO_MARKUP_BYTES;
    private static final Pattern RESPONSE_VIRUS_FOUND_LINE;
    
    static ClamAvInstrumentation getClamAvInstrumentation(final CLAMAVConfiguration config) {
        if (config.isClamAVInstrumentationEnabled()) {
            final String destIP = config.getHost() + ":" + config.getPort();
            return new ClamAvInstrumentation(destIP);
        }
        return null;
    }
    
    public static AVScanResult<File> scanForFile(final File file, final CLAMAVConfiguration config) {
        if (file == null || !file.exists() || file.isDirectory() || !file.canRead()) {
            throw new IllegalArgumentException("Invalid file argument " + file);
        }
        return scan(file, null, config);
    }
    
    public static AVScanResult<Byte> scanForByte(final byte[] bytes, final CLAMAVConfiguration config) {
        if (bytes == null) {
            throw new IllegalArgumentException("Invalid bytes");
        }
        return scan(null, bytes, config);
    }
    
    public static AVScanResult<File> nonPersistentScanForFile(final File file, final CLAMAVConfiguration config) {
        if (file == null || !file.exists() || file.isDirectory() || !file.canRead()) {
            throw new IllegalArgumentException("Invalid file argument " + file);
        }
        try {
            return nonPersistentStreamScan(new FileInputStream(file), config);
        }
        catch (final FileNotFoundException e) {
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
    }
    
    public static AVScanResult<Byte> nonPersistentScanForByte(final byte[] bytes, final CLAMAVConfiguration config) {
        if (bytes == null) {
            throw new IllegalArgumentException("Invalid bytes");
        }
        return nonPersistentStreamScan(new ByteArrayInputStream(bytes), config);
    }
    
    private static <T> AVScanResult<T> scan(final File file, final byte[] bytes, final CLAMAVConfiguration config) {
        AVScanResult<T> scanResult = null;
        ConnectionFactory factory = null;
        SocketChannel channel = null;
        Socket socket = null;
        try {
            factory = ConnectionFactory.getFactory(config);
            channel = factory.getChannel();
            scanResult = persistentScanCall((file != null) ? new FileInputStream(file) : new ByteArrayInputStream(bytes), channel, factory.getExecutor(), config.getReadTimeOut(), getClamAvInstrumentation(config));
            if (scanResult == null) {
                Agent.LOGGER.log(Level.WARNING, "Trying non persistent scan due to persitent scan failed");
                socket = initNonPersistentScan(config);
                scanResult = nonPersistentScanCall((file != null) ? new FileInputStream(file) : new ByteArrayInputStream(bytes), socket, getClamAvInstrumentation(config));
            }
            return scanResult;
        }
        catch (final FileNotFoundException e) {
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
        catch (final UnresolvedAddressException | IOException ex) {
            Agent.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Could not create new connection to Clam AV server ", config.getHost(), config.getPort()), ex);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_CONNECTION_FAILED, "Couldn't create new connection to Clam AV server " + ex.getMessage());
        }
        finally {
            if (factory != null && channel != null) {
                factory.returnConnectionToPool(channel);
            }
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (final IOException e2) {
                    Agent.LOGGER.log(Level.WARNING, "Socket connection to anti-virus host could not be closed.", e2);
                }
            }
        }
    }
    
    static <T> AVScanResult<T> persistentScanCall(final InputStream inStream, final SocketChannel channel, final ExecutorService executorService, final int readTimeOut, final ClamAvInstrumentation clamavinstruemntation) {
        SReader sr = new SReader(channel);
        final Future<SReader> result = executorService.submit(sr, sr);
        try {
            final OutputStream os = channel.socket().getOutputStream();
            sendStreamContentToClamAV(inStream, os);
            sr = result.get(readTimeOut, TimeUnit.MILLISECONDS);
        }
        catch (final IOException ioex) {
            try {
                sr = result.get(1000L, TimeUnit.MILLISECONDS);
            }
            catch (final Exception e) {
                if (clamavinstruemntation != null) {
                    clamavinstruemntation.completeClamAVConnectionCallExp(ioex);
                }
                Agent.LOGGER.log(Level.WARNING, "IOException while reading/writing to channel.", ioex);
                cleanUp(sr, result, channel);
                return null;
            }
        }
        catch (final TimeoutException e2) {
            if (clamavinstruemntation != null) {
                clamavinstruemntation.completeClamAVConnectionCallExp(e2);
            }
            Agent.LOGGER.log(Level.WARNING, "TimeoutException occurs in persistent mode scan. Error is", e2);
            cleanUp(sr, result, channel);
            return null;
        }
        catch (final Exception e3) {
            if (clamavinstruemntation != null) {
                clamavinstruemntation.completeClamAVConnectionCallExp(e3);
            }
            Agent.LOGGER.log(Level.WARNING, "Exception occurs in persistent mode scan. Error is ", e3);
            cleanUp(sr, result, channel);
            return null;
        }
        finally {
            result.cancel(true);
            if (inStream != null) {
                try {
                    inStream.close();
                }
                catch (final IOException ex) {
                    Agent.LOGGER.log(Level.WARNING, "Input stream of the uploaded file could not be closed.", ex);
                }
            }
        }
        final String response = sr.getMessage();
        if (response.toLowerCase().indexOf("can't allocate memory") != -1) {
            if (clamavinstruemntation != null) {
                clamavinstruemntation.completeClamAVConnectionCall();
                clamavinstruemntation.completeClamAVCall(false);
            }
            try {
                Thread.sleep(1000L);
            }
            catch (final InterruptedException ex2) {}
            cleanUp(sr, result, channel);
            Agent.LOGGER.log(Level.SEVERE, "Response from clam: {0}", response);
            return null;
        }
        final AVScanResult<T> scanResult = handleClamAVResponse(response);
        if (clamavinstruemntation != null) {
            clamavinstruemntation.completeClamAVConnectionCall();
            clamavinstruemntation.completeClamAVCall(scanResult.status() == AVScanResult.Status.VIRUS_DETECTED);
        }
        return scanResult;
    }
    
    public static <T> AVScanResult<T> nonPersistentStreamScan(final InputStream inputStream, final CLAMAVConfiguration config) {
        Socket socket = null;
        try {
            socket = initNonPersistentScan(config);
            return nonPersistentScanCall(inputStream, socket, getClamAvInstrumentation(config));
        }
        catch (final UnresolvedAddressException | IOException ex) {
            Agent.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Could not create new connection to Clam AV server ", config.getHost(), config.getPort()), ex);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_CONNECTION_FAILED, "Couldn't create new connection to Clam AV server " + ex.getMessage());
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (final IOException e) {
                    Agent.LOGGER.log(Level.WARNING, "Socket connection to anti-virus host could not be closed.", e);
                }
            }
        }
    }
    
    static Socket initNonPersistentScan(final CLAMAVConfiguration config) throws IOException {
        final Socket s = new Socket(config.getHost(), config.getPort());
        s.setSoTimeout(config.getReadTimeOut());
        return s;
    }
    
    static <T> AVScanResult<T> nonPersistentScanCall(final InputStream inStream, final Socket s, final ClamAvInstrumentation clamavinstruemntation) {
        String response = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            final OutputStream os = s.getOutputStream();
            is = s.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            sendStreamContentToClamAV(inStream, os);
            response = br.readLine();
        }
        catch (final SocketTimeoutException e) {
            if (clamavinstruemntation != null) {
                clamavinstruemntation.completeClamAVConnectionCallExp(e);
            }
            Agent.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Time out for getting Antivirus scan response ", s.getInetAddress().getHostName(), s.getPort()), e);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_SCAN_TIME_OUT, e.getMessage());
        }
        catch (final UnknownHostException e2) {
            if (clamavinstruemntation != null) {
                clamavinstruemntation.completeClamAVConnectionCallExp(e2);
            }
            Agent.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. UnknownHostException ", s.getInetAddress().getHostName(), s.getPort()), e2);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_CONNECTION_FAILED, "UnknownHostException " + e2.getMessage());
        }
        catch (final IOException e3) {
            boolean isScanResponsePriorlyArrived = false;
            try {
                if (is.available() > 0) {
                    response = br.readLine();
                    isScanResponsePriorlyArrived = true;
                }
            }
            catch (final IOException ex) {}
            if (!isScanResponsePriorlyArrived) {
                if (clamavinstruemntation != null) {
                    clamavinstruemntation.completeClamAVConnectionCallExp(e3);
                }
                Agent.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Error occurred while attempting to check for virus ", s.getInetAddress().getHostName(), s.getPort()), e3);
                return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e3.getMessage());
            }
        }
        catch (final Exception e4) {
            if (clamavinstruemntation != null) {
                clamavinstruemntation.completeClamAVConnectionCallExp(e4);
            }
            Agent.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Error occurred while attempting to check for virus ", s.getInetAddress().getHostName(), s.getPort()), e4);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e4.getMessage());
        }
        finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            }
            catch (final IOException ioex) {
                Agent.LOGGER.log(Level.WARNING, "Socket connection to anti-virus host could not be closed.", ioex);
            }
        }
        final AVScanResult<T> scanResult = handleClamAVResponse(response);
        if (clamavinstruemntation != null) {
            clamavinstruemntation.completeClamAVConnectionCall();
            clamavinstruemntation.completeClamAVCall(scanResult.status() == AVScanResult.Status.VIRUS_DETECTED);
        }
        return scanResult;
    }
    
    private static void sendStreamContentToClamAV(final InputStream fileStream, final OutputStream socketStream) throws SocketTimeoutException, UnknownHostException, Exception {
        final byte[] ep = new byte[4];
        final byte[] bu = new byte[16384];
        socketStream.write(Agent.ZOHO_MARKUP_BYTES);
        int rc;
        while ((rc = fileStream.read(bu)) > 0) {
            final byte[] by = itob(rc);
            socketStream.write(by);
            socketStream.write(bu, 0, rc);
            socketStream.flush();
        }
        socketStream.write(ep);
        socketStream.flush();
    }
    
    private static <T> AVScanResult<T> handleClamAVResponse(String response) {
        if (response != null) {
            response = response.trim();
            if (response.endsWith(ClamAVScanResponse.OK.value)) {
                return AVScanResult.completed();
            }
            if (response.endsWith(ClamAVScanResponse.FOUND.value)) {
                Agent.LOGGER.log(Level.SEVERE, "Looks like virus has been found. ClamAV gave this message, non-persistent connection: {0}", response);
                final Matcher matcher = Agent.RESPONSE_VIRUS_FOUND_LINE.matcher(response);
                String virusName = "Unknown virus Name";
                if (matcher.find()) {
                    virusName = matcher.group(3);
                }
                return AVScanResult.virusDetected(virusName);
            }
            if (response.endsWith(ClamAVScanResponse.ERROR.value)) {
                final String errorMessage = response.substring(0, response.length() - ClamAVScanResponse.ERROR.value.length() - 1);
                Agent.LOGGER.log(Level.SEVERE, "Virus detection for a given file is failed at antivirus scanner. response message is: {0}", response);
                if (errorMessage.contains("INSTREAM size limit exceeded.")) {
                    return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_FILE_SIZE_LIMIT_EXCEED, errorMessage);
                }
            }
        }
        Agent.LOGGER.log(Level.SEVERE, "Invalid Response from Clam AV. Response is: {0}", response);
        return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, "Response from Clam AV is: " + response);
    }
    
    static void cleanUp(final SReader sr, final Future<SReader> result, final SocketChannel channel) {
        try {
            if (sr.isRunning()) {
                sr.shutdown();
            }
            result.cancel(true);
            channel.close();
        }
        catch (final IOException ex) {
            Agent.LOGGER.log(Level.WARNING, "Channel could not be closed during clean up.", ex);
        }
    }
    
    static byte[] itob(final int len) {
        final byte[] b = { (byte)(len >> 24 & 0xFF), (byte)(len >> 16 & 0xFF), (byte)(len >> 8 & 0xFF), (byte)(len & 0xFF) };
        return b;
    }
    
    static {
        LOGGER = Logger.getLogger(Agent.class.getName());
        ZOHO_MARKUP_BYTES = "zINSTREAM\u0000".getBytes();
        RESPONSE_VIRUS_FOUND_LINE = Pattern.compile("(.+: )?(.+): (.+) FOUND");
    }
    
    public enum ClamAVScanResponse
    {
        OK("OK"), 
        FOUND("FOUND"), 
        ERROR("ERROR");
        
        private String value;
        
        private ClamAVScanResponse(final String value) {
            this.value = value;
        }
        
        public String value() {
            return this.value;
        }
    }
}
