package com.microsoft.sqlserver.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Hashtable;

public class SQLServerVSMEnclaveProvider implements ISQLServerEnclaveProvider
{
    private static EnclaveSessionCache enclaveCache;
    private VSMAttestationParameters vsmParams;
    private VSMAttestationResponse hgsResponse;
    private String attestationUrl;
    private EnclaveSession enclaveSession;
    private static Hashtable<String, X509CertificateEntry> certificateCache;
    
    public SQLServerVSMEnclaveProvider() {
        this.vsmParams = null;
        this.hgsResponse = null;
        this.attestationUrl = null;
        this.enclaveSession = null;
    }
    
    @Override
    public void getAttestationParameters(final String url) throws SQLServerException {
        if (null == this.vsmParams) {
            this.attestationUrl = url;
            this.vsmParams = new VSMAttestationParameters();
        }
    }
    
    @Override
    public ArrayList<byte[]> createEnclaveSession(final SQLServerConnection connection, final String userSql, final String preparedTypeDefinitions, final Parameter[] params, final ArrayList<String> parameterNames) throws SQLServerException {
        final ArrayList<byte[]> b = this.describeParameterEncryption(connection, userSql, preparedTypeDefinitions, params, parameterNames);
        if (null != this.hgsResponse && !connection.enclaveEstablished()) {
            final EnclaveCacheEntry entry = SQLServerVSMEnclaveProvider.enclaveCache.getSession(connection.getServerName() + this.attestationUrl);
            if (null != entry) {
                this.enclaveSession = entry.getEnclaveSession();
                this.vsmParams = (VSMAttestationParameters)entry.getBaseAttestationRequest();
                return b;
            }
            try {
                this.enclaveSession = new EnclaveSession(this.hgsResponse.getSessionID(), this.vsmParams.createSessionSecret(this.hgsResponse.getDHpublicKey()));
                SQLServerVSMEnclaveProvider.enclaveCache.addEntry(connection.getServerName(), connection.enclaveAttestationUrl, this.vsmParams, this.enclaveSession);
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
            SQLServerVSMEnclaveProvider.enclaveCache.removeEntry(this.enclaveSession);
        }
        this.enclaveSession = null;
        this.vsmParams = null;
        this.attestationUrl = null;
    }
    
    @Override
    public EnclaveSession getEnclaveSession() {
        return this.enclaveSession;
    }
    
    private void validateAttestationResponse() throws SQLServerException {
        if (null != this.hgsResponse) {
            try {
                final byte[] attestationCerts = this.getAttestationCertificates();
                this.hgsResponse.validateCert(attestationCerts);
                this.hgsResponse.validateStatementSignature();
                this.hgsResponse.validateDHPublicKey();
            }
            catch (final IOException | GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
    }
    
    private byte[] getAttestationCertificates() throws IOException {
        byte[] certData = null;
        final X509CertificateEntry cacheEntry = SQLServerVSMEnclaveProvider.certificateCache.get(this.attestationUrl);
        if (null != cacheEntry && !cacheEntry.expired()) {
            certData = cacheEntry.getCertificates();
        }
        else if (null != cacheEntry && cacheEntry.expired()) {
            SQLServerVSMEnclaveProvider.certificateCache.remove(this.attestationUrl);
        }
        if (null == certData) {
            final URL url = new URL(this.attestationUrl + "/attestationservice.svc/v2.0/signingCertificates/");
            final URLConnection con = url.openConnection();
            final byte[] buff = new byte[con.getInputStream().available()];
            con.getInputStream().read(buff, 0, buff.length);
            final String s = new String(buff);
            final String[] bytesString = s.substring(1, s.length() - 1).split(",");
            certData = new byte[bytesString.length];
            for (int i = 0; i < certData.length; ++i) {
                certData[i] = (byte)Integer.parseInt(bytesString[i]);
            }
            SQLServerVSMEnclaveProvider.certificateCache.put(this.attestationUrl, new X509CertificateEntry(certData));
        }
        return certData;
    }
    
    private ArrayList<byte[]> describeParameterEncryption(final SQLServerConnection connection, final String userSql, final String preparedTypeDefinitions, final Parameter[] params, final ArrayList<String> parameterNames) throws SQLServerException {
        final ArrayList<byte[]> enclaveRequestedCEKs = new ArrayList<byte[]>();
        try (final PreparedStatement stmt = connection.prepareStatement(connection.enclaveEstablished() ? "EXEC sp_describe_parameter_encryption ?,?" : "EXEC sp_describe_parameter_encryption ?,?,?");
             final ResultSet rs = connection.enclaveEstablished() ? this.executeSDPEv1(stmt, userSql, preparedTypeDefinitions) : this.executeSDPEv2(stmt, userSql, preparedTypeDefinitions, this.vsmParams)) {
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
                        this.hgsResponse = new VSMAttestationResponse(hgsRs.getBytes(1));
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
        SQLServerVSMEnclaveProvider.enclaveCache = new EnclaveSessionCache();
        SQLServerVSMEnclaveProvider.certificateCache = new Hashtable<String, X509CertificateEntry>();
    }
}
