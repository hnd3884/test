package com.zoho.clustering.failover;

import com.zoho.clustering.failover.handlers.TakeoverFileHandler;
import java.util.Properties;
import com.zoho.clustering.failover.handlers.RemoteMonitorHandler;
import com.zoho.clustering.failover.handlers.ReplSlaveHandler;
import com.zoho.clustering.failover.handlers.CoreHandler;
import com.zoho.clustering.failover.handlers.CompositeHandler;
import com.zoho.clustering.util.logger.LogConfig;
import com.zoho.clustering.util.MyProperties;

public class FOSMain
{
    private static FOS fos;
    private static MyProperties props;
    static final String PREFIX = "clustering.fos";
    
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.print("\nInCorrect Usage. Correct usage is ");
            System.out.print("\njava " + FOSMain.class.getName() + "<fos.conf file>");
            System.exit(1);
        }
        try {
            initialize("clustering.fos", FOSMain.props = new MyProperties(args[0]));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    FOSMain.fos.stop();
                }
            }, "fos-shutdownHook"));
            FOSMain.fos.start();
        }
        catch (final RuntimeException exp) {
            FOS.Console.out("Problem while starting the FOS service", exp);
        }
    }
    
    public static FOS getFOS() {
        if (FOSMain.fos == null) {
            throw new IllegalStateException("FOS service not yet initialized");
        }
        return FOSMain.fos;
    }
    
    private static void initialize(final String prefix, final MyProperties props) {
        if (FOSMain.fos != null) {
            throw new IllegalArgumentException("FOSMain is already initialized");
        }
        final String logPrefix = prefix + ".log";
        new LogConfig(logPrefix, props).registerLogger("com.zoho.clustering");
        FOSUtil.initialize(props.value(prefix + ".toolsDir", "tools"));
        final FOS.Mode mode = FOS.Mode.valueOf(props.value(prefix + ".mode").toUpperCase());
        final FOSConfig config = new FOSConfig(prefix, props, mode);
        config.makeImmutable();
        final FOSHandler fosHandler = createFOSHandler(prefix, props, mode, config);
        FOSMain.fos = new FOS(mode, config, fosHandler);
    }
    
    private static FOSHandler createFOSHandler(final String prefix, final MyProperties props, final FOS.Mode mode, final FOSConfig fosConfig) {
        final CompositeHandler compositeHandler = new CompositeHandler();
        final String appServiceName = props.value(prefix + ".appServiceName");
        final int startWaitTimeInSecs = props.intValue(prefix + ".startWaitTimeInSecs", 0);
        final int stopWaitTimeInSecs = props.intValue(prefix + ".stopWaitTimeInSecs", 0);
        compositeHandler.addHandler(new CoreHandler(appServiceName, startWaitTimeInSecs, stopWaitTimeInSecs));
        if (FOS.Mode.MASTER == mode) {
            return compositeHandler;
        }
        final String replSlaveConfFile = props.optionalValue(prefix + ".replSlaveConfFile");
        if (replSlaveConfFile != null) {
            compositeHandler.addHandler(new ReplSlaveHandler(replSlaveConfFile, fosConfig.masterURL()));
        }
        final String remoteMonitorPrefix = prefix + ".slave.remoteMonitor";
        final boolean enableRemoteMonitor = props.boolValue(remoteMonitorPrefix + ".enable", false);
        if (enableRemoteMonitor) {
            final String removeURI = props.value(remoteMonitorPrefix + ".removeURI");
            final int connTimeoutMillis = props.intValue(remoteMonitorPrefix + ".connTimeoutMillis", 1000);
            final int readTimeoutMillis = props.intValue(remoteMonitorPrefix + ".readTimeoutMillis", 1000);
            final String hostnameVerifierClassName = props.optionalValue(prefix + ".hostnameVerifierClassName");
            final RemoteMonitorHandler.Config config = new RemoteMonitorHandler.Config(fosConfig.nodeId(), fosConfig.masterURL(), removeURI, connTimeoutMillis, readTimeoutMillis, hostnameVerifierClassName);
            compositeHandler.addHandler(new RemoteMonitorHandler(config));
        }
        final String takeoverFile = props.optionalValue(prefix + ".event.onSlaveTakeover.file");
        if (takeoverFile != null) {
            final Properties fileProps = new Properties();
            fileProps.setProperty("nodeId", fosConfig.nodeId());
            fileProps.setProperty("masterURL", fosConfig.masterURL());
            compositeHandler.addHandler(new TakeoverFileHandler(takeoverFile, fileProps));
        }
        return compositeHandler;
    }
    
    static FOSHandler reinitialize(final FOS fos, final FOS.Mode mode, final FOSConfig config) {
        return createFOSHandler("clustering.fos", FOSMain.props, mode, config);
    }
    
    static {
        FOSMain.fos = null;
    }
}
