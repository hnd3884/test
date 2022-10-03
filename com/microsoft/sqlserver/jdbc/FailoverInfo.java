package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.logging.Level;

final class FailoverInfo
{
    private String failoverPartner;
    private int portNumber;
    private String failoverInstance;
    private boolean setUpInfocalled;
    private boolean useFailoverPartner;
    
    boolean getUseFailoverPartner() {
        return this.useFailoverPartner;
    }
    
    FailoverInfo(final String failover, final SQLServerConnection con, final boolean actualFailoverPartner) {
        this.failoverPartner = failover;
        this.useFailoverPartner = actualFailoverPartner;
        this.portNumber = -1;
    }
    
    void log(final SQLServerConnection con) {
        if (con.getConnectionLogger().isLoggable(Level.FINE)) {
            con.getConnectionLogger().fine(con.toString() + " Failover server :" + this.failoverPartner + " Failover partner is primary : " + this.useFailoverPartner);
        }
    }
    
    private void setupInfo(final SQLServerConnection con) throws SQLServerException {
        if (this.setUpInfocalled) {
            return;
        }
        if (0 == this.failoverPartner.length()) {
            this.portNumber = SQLServerConnection.DEFAULTPORT;
        }
        else {
            final int px = this.failoverPartner.indexOf(92);
            if (px >= 0) {
                if (con.getConnectionLogger().isLoggable(Level.FINE)) {
                    con.getConnectionLogger().fine(con.toString() + " Failover server :" + this.failoverPartner);
                }
                final String instanceValue = this.failoverPartner.substring(px + 1, this.failoverPartner.length());
                this.failoverPartner = this.failoverPartner.substring(0, px);
                con.validateMaxSQLLoginName(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), instanceValue);
                this.failoverInstance = instanceValue;
                final String instancePort = con.getInstancePort(this.failoverPartner, instanceValue);
                try {
                    this.portNumber = Integer.parseInt(instancePort);
                }
                catch (final NumberFormatException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                    final Object[] msgArgs = { instancePort };
                    SQLServerException.makeFromDriverError(con, null, form.format(msgArgs), null, false);
                }
            }
            else {
                this.portNumber = SQLServerConnection.DEFAULTPORT;
            }
        }
        this.setUpInfocalled = true;
    }
    
    synchronized ServerPortPlaceHolder failoverPermissionCheck(final SQLServerConnection con, final boolean link) throws SQLServerException {
        this.setupInfo(con);
        return new ServerPortPlaceHolder(this.failoverPartner, this.portNumber, this.failoverInstance, link);
    }
    
    synchronized void failoverAdd(final SQLServerConnection connection, final boolean actualUseFailoverPartner, final String actualFailoverPartner) throws SQLServerException {
        if (this.useFailoverPartner != actualUseFailoverPartner) {
            if (connection.getConnectionLogger().isLoggable(Level.FINE)) {
                connection.getConnectionLogger().fine(connection.toString() + " Failover detected. failover partner=" + actualFailoverPartner);
            }
            this.useFailoverPartner = actualUseFailoverPartner;
        }
        if (!actualUseFailoverPartner && !this.failoverPartner.equals(actualFailoverPartner)) {
            this.failoverPartner = actualFailoverPartner;
            this.setUpInfocalled = false;
        }
    }
}
