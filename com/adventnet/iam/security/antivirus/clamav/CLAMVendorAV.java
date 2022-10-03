package com.adventnet.iam.security.antivirus.clamav;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.channels.UnresolvedAddressException;
import com.adventnet.iam.security.antivirus.AVScanFailureInfo;
import java.io.IOException;
import java.util.logging.Level;
import com.adventnet.iam.security.antivirus.AVScanResult;
import java.nio.channels.SocketChannel;
import java.net.Socket;
import java.util.logging.Logger;
import java.io.File;
import com.adventnet.iam.security.antivirus.VendorAV;

public class CLAMVendorAV extends VendorAV<File>
{
    private static final Logger LOGGER;
    private static final String SCAN_ENGINE_NAME = "Clam AV";
    private static final String ILLEGAL_PERSISTENT_CONFIGURE = "Persistent mode already configured";
    private static transient CLAMAVConfiguration persistentConfig;
    private final CLAMAVConfiguration config;
    private transient Socket socket;
    private transient ClamAvInstrumentation clamAvInstruemntation;
    private transient SocketChannel channel;
    
    private CLAMVendorAV(final CLAMAVConfiguration localConfig) {
        super("Clam AV");
        this.config = localConfig;
    }
    
    public static CLAMVendorAV getInstance(final CLAMAVConfiguration localConfig) {
        if (localConfig.isNonPersistentScan()) {
            return newNonPersistentInstance(localConfig);
        }
        if (CLAMVendorAV.persistentConfig == null) {
            configurePersistent(localConfig);
        }
        else if (CLAMVendorAV.persistentConfig != localConfig) {
            throw new IllegalArgumentException("Persistent mode already configured");
        }
        return getPersistentInstance();
    }
    
    public static CLAMVendorAV newNonPersistentInstance(final CLAMAVConfiguration localConfig) {
        return new CLAMVendorAV(localConfig);
    }
    
    public static synchronized void configurePersistent(final CLAMAVConfiguration localConfig) {
        if (CLAMVendorAV.persistentConfig == null) {
            ConnectionFactory.getFactory(localConfig);
            CLAMVendorAV.persistentConfig = localConfig;
        }
        else if (CLAMVendorAV.persistentConfig != localConfig) {
            throw new IllegalArgumentException("Persistent mode already configured");
        }
    }
    
    public static CLAMVendorAV getPersistentInstance() {
        if (CLAMVendorAV.persistentConfig == null) {
            throw new IllegalStateException("Persistent mode not configured");
        }
        return new CLAMVendorAV(CLAMVendorAV.persistentConfig);
    }
    
    @Override
    protected AVScanResult<File> init() {
        return this.initImpl(false);
    }
    
    @Override
    protected AVScanResult<File> scanImpl(final File file) {
        return this.scanImpl(file, false);
    }
    
    public void closeImpl() {
        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        }
        catch (final IOException ioex) {
            CLAMVendorAV.LOGGER.log(Level.WARNING, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Socket connection to anti-virus host could not be closed ", this.config.getHost(), this.config.getPort()), ioex);
        }
        if (ConnectionFactory.getFactory() != null) {
            ConnectionFactory.getFactory().returnConnectionToPool(this.channel);
        }
    }
    
    private AVScanResult<File> initImpl(final boolean explicitNonPersistentScan) {
        try {
            if (explicitNonPersistentScan || this.config.isNonPersistentScan()) {
                this.socket = Agent.initNonPersistentScan(this.config);
            }
            else if (ConnectionFactory.getFactory() != null) {
                this.channel = ConnectionFactory.getFactory().getChannel();
            }
            else {
                this.channel = ConnectionFactory.getFactory(this.config).getChannel();
            }
            this.clamAvInstruemntation = Agent.getClamAvInstrumentation(this.config);
        }
        catch (final UnresolvedAddressException | IOException ex) {
            CLAMVendorAV.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Could not create new connection to Clam AV server ", this.config.getHost(), this.config.getPort()), ex);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_CONNECTION_FAILED, "Couldn't create new connection to Clam AV server " + ex.getMessage());
        }
        catch (final Exception e) {
            CLAMVendorAV.LOGGER.log(Level.SEVERE, String.format("ZohoSecurity ClamAV Host: %s and Port: %d. Exception occurs while initializing clam av ", this.config.getHost(), this.config.getPort()), e);
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
        }
        return AVScanResult.completed();
    }
    
    private AVScanResult<File> scanImpl(final File file, final boolean explicitNonPersistentScan) {
        Label_0046: {
            if (!explicitNonPersistentScan) {
                if (!this.config.isNonPersistentScan()) {
                    break Label_0046;
                }
            }
            try {
                return Agent.nonPersistentScanCall(new FileInputStream(file), this.socket, this.clamAvInstruemntation);
            }
            catch (final FileNotFoundException e) {
                return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e.getMessage());
            }
        }
        AVScanResult<File> scanResult = null;
        try {
            scanResult = Agent.persistentScanCall(new FileInputStream(file), this.channel, ConnectionFactory.getFactory().getExecutor(), this.config.getReadTimeOut(), this.clamAvInstruemntation);
        }
        catch (final FileNotFoundException e2) {
            return AVScanResult.failed(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, e2.getMessage());
        }
        if (scanResult == null) {
            CLAMVendorAV.LOGGER.log(Level.WARNING, "Trying non persistent scan due to persitent scan failed");
            scanResult = this.initImpl(true);
            if (scanResult.status() == AVScanResult.Status.COMPLETED) {
                scanResult = this.scanImpl(file, true);
            }
        }
        return scanResult;
    }
    
    static {
        LOGGER = Logger.getLogger(CLAMVendorAV.class.getName());
    }
}
