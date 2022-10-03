package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.logging.Logger;

final class TDSParser
{
    private static Logger logger;
    
    static void parse(final TDSReader tdsReader, final String logContext) throws SQLServerException {
        parse(tdsReader, new TDSTokenHandler(logContext));
    }
    
    static void parse(final TDSReader tdsReader, final TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        parse(tdsReader, tdsTokenHandler, false);
    }
    
    static void parse(final TDSReader tdsReader, final TDSTokenHandler tdsTokenHandler, final boolean readOnlyWarningsFlag) throws SQLServerException {
        final boolean isLogging = TDSParser.logger.isLoggable(Level.FINEST);
        boolean parsing = true;
        boolean isLoginAck = false;
        boolean isFeatureExtAck = false;
        while (parsing) {
            final int tdsTokenType = tdsReader.peekTokenType();
            if (isLogging) {
                TDSParser.logger.finest(tdsReader.toString() + ": " + tdsTokenHandler.logContext + ": Processing " + ((-1 == tdsTokenType) ? "EOF" : TDS.getTokenName(tdsTokenType)));
            }
            if (readOnlyWarningsFlag && 171 != tdsTokenType) {
                parsing = false;
                return;
            }
            switch (tdsTokenType) {
                case 237: {
                    parsing = tdsTokenHandler.onSSPI(tdsReader);
                    continue;
                }
                case 173: {
                    isLoginAck = true;
                    parsing = tdsTokenHandler.onLoginAck(tdsReader);
                    continue;
                }
                case 174: {
                    isFeatureExtAck = true;
                    tdsReader.getConnection().processFeatureExtAck(tdsReader);
                    parsing = true;
                    continue;
                }
                case 227: {
                    parsing = tdsTokenHandler.onEnvChange(tdsReader);
                    continue;
                }
                case 121: {
                    parsing = tdsTokenHandler.onRetStatus(tdsReader);
                    continue;
                }
                case 172: {
                    parsing = tdsTokenHandler.onRetValue(tdsReader);
                    continue;
                }
                case 253:
                case 254:
                case 255: {
                    tdsReader.getCommand().checkForInterrupt();
                    parsing = tdsTokenHandler.onDone(tdsReader);
                    continue;
                }
                case 170: {
                    parsing = tdsTokenHandler.onError(tdsReader);
                    continue;
                }
                case 171: {
                    parsing = tdsTokenHandler.onInfo(tdsReader);
                    continue;
                }
                case 169: {
                    parsing = tdsTokenHandler.onOrder(tdsReader);
                    continue;
                }
                case 129: {
                    parsing = tdsTokenHandler.onColMetaData(tdsReader);
                    continue;
                }
                case 209: {
                    parsing = tdsTokenHandler.onRow(tdsReader);
                    continue;
                }
                case 210: {
                    parsing = tdsTokenHandler.onNBCRow(tdsReader);
                    continue;
                }
                case 165: {
                    parsing = tdsTokenHandler.onColInfo(tdsReader);
                    continue;
                }
                case 164: {
                    parsing = tdsTokenHandler.onTabName(tdsReader);
                    continue;
                }
                case 238: {
                    parsing = tdsTokenHandler.onFedAuthInfo(tdsReader);
                    continue;
                }
                case -1: {
                    tdsReader.getCommand().onTokenEOF();
                    tdsTokenHandler.onEOF(tdsReader);
                    parsing = false;
                    continue;
                }
                default: {
                    throwUnexpectedTokenException(tdsReader, tdsTokenHandler.logContext);
                    continue;
                }
            }
        }
        if (isLoginAck && !isFeatureExtAck) {
            tdsReader.tryProcessFeatureExtAck(isFeatureExtAck);
        }
    }
    
    static void throwUnexpectedTokenException(final TDSReader tdsReader, final String logContext) throws SQLServerException {
        if (TDSParser.logger.isLoggable(Level.SEVERE)) {
            TDSParser.logger.severe(tdsReader.toString() + ": " + logContext + ": Encountered unexpected " + TDS.getTokenName(tdsReader.peekTokenType()));
        }
        tdsReader.throwInvalidTDSToken(TDS.getTokenName(tdsReader.peekTokenType()));
    }
    
    static void ignoreLengthPrefixedToken(final TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedByte();
        final int envValueLength = tdsReader.readUnsignedShort();
        final byte[] envValueData = new byte[envValueLength];
        tdsReader.readBytes(envValueData, 0, envValueLength);
    }
    
    static {
        TDSParser.logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.TOKEN");
    }
}
