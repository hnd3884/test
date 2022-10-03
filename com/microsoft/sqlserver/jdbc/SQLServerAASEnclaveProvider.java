package com.microsoft.sqlserver.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.io.IOException;

public class SQLServerAASEnclaveProvider implements ISQLServerEnclaveProvider
{
    private static EnclaveSessionCache enclaveCache;
    private AASAttestationParameters aasParams;
    private AASAttestationResponse hgsResponse;
    private String attestationURL;
    private EnclaveSession enclaveSession;
    
    public SQLServerAASEnclaveProvider() {
        this.aasParams = null;
        this.hgsResponse = null;
        this.attestationURL = null;
        this.enclaveSession = null;
    }
    
    @Override
    public void getAttestationParameters(final String url) throws SQLServerException {
        if (null == this.aasParams) {
            this.attestationURL = url;
            try {
                this.aasParams = new AASAttestationParameters(this.attestationURL);
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
    }
    
    @Override
    public ArrayList<byte[]> createEnclaveSession(final SQLServerConnection connection, final String userSql, final String preparedTypeDefinitions, final Parameter[] params, final ArrayList<String> parameterNames) throws SQLServerException {
        final ArrayList<byte[]> b = this.describeParameterEncryption(connection, userSql, preparedTypeDefinitions, params, parameterNames);
        if (null != this.hgsResponse && !connection.enclaveEstablished()) {
            final EnclaveCacheEntry entry = SQLServerAASEnclaveProvider.enclaveCache.getSession(connection.getServerName() + this.attestationURL);
            if (null != entry) {
                this.enclaveSession = entry.getEnclaveSession();
                this.aasParams = (AASAttestationParameters)entry.getBaseAttestationRequest();
                return b;
            }
            try {
                this.enclaveSession = new EnclaveSession(this.hgsResponse.getSessionID(), this.aasParams.createSessionSecret(this.hgsResponse.getDHpublicKey()));
                SQLServerAASEnclaveProvider.enclaveCache.addEntry(connection.getServerName(), connection.enclaveAttestationUrl, this.aasParams, this.enclaveSession);
            }
            catch (final GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(connection, this, e.getLocalizedMessage(), "0", false);
            }
        }
        return b;
    }
    
    @Override
    public void invalidateEnclaveSession() {
        if (null != this.enclaveSession) {
            SQLServerAASEnclaveProvider.enclaveCache.removeEntry(this.enclaveSession);
        }
        this.enclaveSession = null;
        this.aasParams = null;
        this.attestationURL = null;
    }
    
    @Override
    public EnclaveSession getEnclaveSession() {
        return this.enclaveSession;
    }
    
    private void validateAttestationResponse() throws SQLServerException {
        if (null != this.hgsResponse) {
            try {
                this.hgsResponse.validateToken(this.attestationURL, this.aasParams.getNonce());
                this.hgsResponse.validateDHPublicKey(this.aasParams.getNonce());
            }
            catch (final GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
    }
    
    private ArrayList<byte[]> describeParameterEncryption(final SQLServerConnection connection, final String userSql, final String preparedTypeDefinitions, final Parameter[] params, final ArrayList<String> parameterNames) throws SQLServerException {
        final ArrayList<byte[]> enclaveRequestedCEKs = new ArrayList<byte[]>();
        try (final PreparedStatement stmt = connection.prepareStatement(connection.enclaveEstablished() ? "EXEC sp_describe_parameter_encryption ?,?" : "EXEC sp_describe_parameter_encryption ?,?,?");
             final ResultSet rs = connection.enclaveEstablished() ? this.executeSDPEv1(stmt, userSql, preparedTypeDefinitions) : this.executeSDPEv2(stmt, userSql, preparedTypeDefinitions, this.aasParams)) {
            if (null == rs) {
                final ArrayList<byte[]> list = enclaveRequestedCEKs;
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                return list;
            }
            this.processSDPEv1(userSql, preparedTypeDefinitions, params, parameterNames, connection, stmt, rs, enclaveRequestedCEKs);
            if (connection.isAEv2() && stmt.getMoreResults()) {
                try (final ResultSet hgsRs = stmt.getResultSet()) {
                    if (hgsRs.next()) {
                        this.hgsResponse = new AASAttestationResponse(hgsRs.getBytes(1));
                        this.validateAttestationResponse();
                    }
                    else {
                        SQLServerException.makeFromDriverError(null, this, SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), "0", false);
                    }
                }
            }
        }
        catch (final SQLException | IOException e) {
            if (e instanceof SQLServerException) {
                throw (SQLServerException)e;
            }
            throw new SQLServerException(SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), null, 0, e);
        }
        return enclaveRequestedCEKs;
    }
    
    static {
        SQLServerAASEnclaveProvider.enclaveCache = new EnclaveSessionCache();
    }
}
