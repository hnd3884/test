package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.logging.Logger;

class TDSTokenHandler
{
    final String logContext;
    private SQLServerError databaseError;
    private static Logger logger;
    
    final SQLServerError getDatabaseError() {
        return this.databaseError;
    }
    
    TDSTokenHandler(final String logContext) {
        this.logContext = logContext;
    }
    
    boolean onSSPI(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
    
    boolean onLoginAck(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
    
    boolean onFeatureExtensionAck(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
    
    boolean onEnvChange(final TDSReader tdsReader) throws SQLServerException {
        tdsReader.getConnection().processEnvChange(tdsReader);
        return true;
    }
    
    boolean onRetStatus(final TDSReader tdsReader) throws SQLServerException {
        new StreamRetStatus().setFromTDS(tdsReader);
        return true;
    }
    
    boolean onRetValue(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
    
    boolean onDone(final TDSReader tdsReader) throws SQLServerException {
        final StreamDone doneToken = new StreamDone();
        doneToken.setFromTDS(tdsReader);
        return true;
    }
    
    boolean onError(final TDSReader tdsReader) throws SQLServerException {
        if (null == this.databaseError) {
            (this.databaseError = new SQLServerError()).setFromTDS(tdsReader);
        }
        else {
            new SQLServerError().setFromTDS(tdsReader);
        }
        return true;
    }
    
    boolean onInfo(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }
    
    boolean onOrder(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }
    
    boolean onColMetaData(final TDSReader tdsReader) throws SQLServerException {
        if (TDSTokenHandler.logger.isLoggable(Level.SEVERE)) {
            TDSTokenHandler.logger.severe(tdsReader.toString() + ": " + this.logContext + ": Encountered " + TDS.getTokenName(tdsReader.peekTokenType()) + ". SHOWPLAN is ON, ignoring.");
        }
        return false;
    }
    
    boolean onRow(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
    
    boolean onNBCRow(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
    
    boolean onColInfo(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }
    
    boolean onTabName(final TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }
    
    void onEOF(final TDSReader tdsReader) throws SQLServerException {
        if (null != this.getDatabaseError()) {
            SQLServerException.makeFromDatabaseError(tdsReader.getConnection(), null, this.getDatabaseError().getErrorMessage(), this.getDatabaseError(), false);
        }
    }
    
    boolean onFedAuthInfo(final TDSReader tdsReader) throws SQLServerException {
        tdsReader.getConnection().processFedAuthInfo(tdsReader, this);
        return true;
    }
    
    static {
        TDSTokenHandler.logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.TOKEN");
    }
}
