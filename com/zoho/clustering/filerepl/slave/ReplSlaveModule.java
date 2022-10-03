package com.zoho.clustering.filerepl.slave;

import java.io.IOException;
import com.zoho.clustering.util.ClassUtil;
import com.zoho.clustering.filerepl.slave.api.HttpMasterStub;
import com.zoho.clustering.filerepl.slave.api.HttpMasterStubConfig;
import com.zoho.clustering.util.logger.LogConfig;
import com.zoho.clustering.filerepl.ErrorHandler;
import com.zoho.clustering.filerepl.slave.api.MasterStub;
import java.util.logging.Level;
import com.zoho.clustering.filerepl.DirectoryList;
import com.zoho.clustering.util.MyProperties;

public class ReplSlaveModule
{
    private static ReplSlave replSlave;
    
    public static ReplSlave getInst() {
        if (ReplSlaveModule.replSlave == null) {
            throw new IllegalStateException("ReplSlave is not yet initialized");
        }
        return ReplSlaveModule.replSlave;
    }
    
    public static void initialize(final MyProperties props, final String masterURL) {
        initialize("clustering.filerepl.slave", props, masterURL);
    }
    
    public static void initialize(final String prefix, final MyProperties props, final String masterURL) {
        if (ReplSlaveModule.replSlave != null) {
            throw new IllegalStateException("ReplSlave is already initialized");
        }
        initializeLogger(prefix + ".log", props);
        final ReplSlave.Config config = new ReplSlave.Config(prefix, props);
        final DirectoryList directoryList = new DirectoryList(props.value(prefix + ".dirList"));
        final MasterStub masterStub = createMasterStub(prefix + ".httpMasterStub", props, masterURL);
        final ErrorHandler errorHandler = createErrorHandler(prefix, props);
        ReplSlaveModule.replSlave = new ReplSlave(config, directoryList, masterStub, errorHandler);
        ReplSlave.logger().log(Level.INFO, "ReplSlaveModule: initialized");
    }
    
    private static void initializeLogger(final String prefix, final MyProperties props) {
        if (props.optionalValue(prefix + ".fileName") != null) {
            new LogConfig(prefix, props).registerLogger("com.zoho.clustering.filerepl");
        }
    }
    
    private static MasterStub createMasterStub(final String prefix, final MyProperties props, final String masterURL) {
        final HttpMasterStubConfig httpConfig = new HttpMasterStubConfig(prefix, props);
        httpConfig.setMasterURL(masterURL);
        httpConfig.makeImmutable();
        return new HttpMasterStub(httpConfig);
    }
    
    private static ErrorHandler createErrorHandler(final String prefix, final MyProperties props) {
        final String errorHandlerClass = props.value(prefix + ".errorHandlerClass", DefaultErrorHandler.class.getName());
        final String errorHandlerPrefix = props.optionalValue(prefix + ".errorHandlerPrefix");
        return (errorHandlerPrefix == null) ? ClassUtil.New(errorHandlerClass) : ClassUtil.New(errorHandlerClass, errorHandlerPrefix, props);
    }
    
    static {
        ReplSlaveModule.replSlave = null;
    }
    
    public static class DefaultErrorHandler implements ErrorHandler
    {
        @Override
        public void handleError(final Exception exp) {
            ReplSlave.logger().log(Level.SEVERE, "ReplSlaveModule.ErrorHandler.onError() invoked ...", exp);
            ReplSlave.logger().log(Level.SEVERE, "Calling System.exit(1)");
            System.exit(1);
        }
    }
    
    public static class Test
    {
        public static void main(final String[] args) throws IOException {
            ReplSlaveModule.initialize(new MyProperties(args[0]), args[1]);
            ReplSlaveModule.getInst().start();
        }
    }
}
