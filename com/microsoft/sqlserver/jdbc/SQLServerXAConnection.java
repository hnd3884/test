package com.microsoft.sqlserver.jdbc;

import javax.transaction.xa.XAResource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.XAConnection;

public final class SQLServerXAConnection extends SQLServerPooledConnection implements XAConnection
{
    private static final long serialVersionUID = -8154621218821899459L;
    private SQLServerXAResource XAResource;
    private SQLServerConnection physicalControlConnection;
    private Logger xaLogger;
    
    SQLServerXAConnection(final SQLServerDataSource ds, final String user, final String pwd) throws SQLException {
        super(ds, user, pwd);
        this.xaLogger = SQLServerXADataSource.xaLogger;
        final SQLServerConnection con = this.getPhysicalConnection();
        final Properties controlConnectionProperties = (Properties)con.activeConnectionProperties.clone();
        controlConnectionProperties.setProperty(SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), "true");
        controlConnectionProperties.remove(SQLServerDriverStringProperty.SELECT_METHOD.toString());
        final String auth = controlConnectionProperties.getProperty(SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString());
        if (null != auth && AuthenticationScheme.ntlm == AuthenticationScheme.valueOfString(auth)) {
            controlConnectionProperties.setProperty(SQLServerDriverStringProperty.PASSWORD.toString(), pwd);
        }
        String trustStorePassword = ds.getTrustStorePassword();
        if (null == trustStorePassword) {
            final Properties urlProps = Util.parseUrl(ds.getURL(), this.xaLogger);
            trustStorePassword = urlProps.getProperty(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
        }
        if (null != trustStorePassword) {
            controlConnectionProperties.setProperty(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), trustStorePassword);
        }
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer("Creating an internal control connection for" + this.toString());
        }
        this.physicalControlConnection = null;
        if (Util.use43Wrapper()) {
            this.physicalControlConnection = new SQLServerConnection43(this.toString());
        }
        else {
            this.physicalControlConnection = new SQLServerConnection(this.toString());
        }
        this.physicalControlConnection.connect(controlConnectionProperties, null);
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer("Created an internal control connection" + this.physicalControlConnection.toString() + " for " + this.toString() + " Physical connection:" + this.getPhysicalConnection().toString());
        }
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(ds.toString() + " user:" + user);
        }
    }
    
    @Override
    public synchronized XAResource getXAResource() throws SQLException {
        if (this.XAResource == null) {
            this.XAResource = new SQLServerXAResource(this.getPhysicalConnection(), this.physicalControlConnection, this.toString());
        }
        return this.XAResource;
    }
    
    @Override
    public void close() throws SQLException {
        synchronized (this) {
            if (this.XAResource != null) {
                this.XAResource.close();
                this.XAResource = null;
            }
            if (null != this.physicalControlConnection) {
                this.physicalControlConnection.close();
                this.physicalControlConnection = null;
            }
        }
        super.close();
    }
}
