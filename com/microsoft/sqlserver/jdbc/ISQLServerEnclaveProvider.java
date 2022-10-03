package com.microsoft.sqlserver.jdbc;

import java.util.Map;
import java.text.MessageFormat;
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public interface ISQLServerEnclaveProvider
{
    public static final String SDPE1 = "EXEC sp_describe_parameter_encryption ?,?";
    public static final String SDPE2 = "EXEC sp_describe_parameter_encryption ?,?,?";
    
    default byte[] getEnclavePackage(final String userSQL, final ArrayList<byte[]> enclaveCEKs) throws SQLServerException {
        final EnclaveSession enclaveSession = this.getEnclaveSession();
        if (null != enclaveSession) {
            try {
                final ByteArrayOutputStream enclavePackage = new ByteArrayOutputStream();
                enclavePackage.write(enclaveSession.getSessionID());
                final ByteArrayOutputStream keys = new ByteArrayOutputStream();
                final byte[] randomGUID = new byte[16];
                SecureRandom.getInstanceStrong().nextBytes(randomGUID);
                keys.write(randomGUID);
                keys.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(enclaveSession.getCounter()).array());
                keys.write(MessageDigest.getInstance("SHA-256").digest(userSQL.getBytes(StandardCharsets.UTF_16LE)));
                for (final byte[] b : enclaveCEKs) {
                    keys.write(b);
                }
                enclaveCEKs.clear();
                final SQLServerAeadAes256CbcHmac256EncryptionKey encryptedKey = new SQLServerAeadAes256CbcHmac256EncryptionKey(enclaveSession.getSessionSecret(), "AEAD_AES_256_CBC_HMAC_SHA256");
                final SQLServerAeadAes256CbcHmac256Algorithm algo = new SQLServerAeadAes256CbcHmac256Algorithm(encryptedKey, SQLServerEncryptionType.Randomized, (byte)1);
                enclavePackage.write(algo.encryptData(keys.toByteArray()));
                return enclavePackage.toByteArray();
            }
            catch (final GeneralSecurityException | SQLServerException | IOException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
        return null;
    }
    
    default ResultSet executeSDPEv2(final PreparedStatement stmt, final String userSql, final String preparedTypeDefinitions, final BaseAttestationRequest req) throws SQLException, IOException {
        ((SQLServerPreparedStatement)stmt).isInternalEncryptionQuery = true;
        stmt.setNString(1, userSql);
        if (preparedTypeDefinitions != null && preparedTypeDefinitions.length() != 0) {
            stmt.setNString(2, preparedTypeDefinitions);
        }
        else {
            stmt.setNString(2, "");
        }
        stmt.setBytes(3, req.getBytes());
        return ((SQLServerPreparedStatement)stmt).executeQueryInternal();
    }
    
    default ResultSet executeSDPEv1(final PreparedStatement stmt, final String userSql, final String preparedTypeDefinitions) throws SQLException {
        ((SQLServerPreparedStatement)stmt).isInternalEncryptionQuery = true;
        stmt.setNString(1, userSql);
        if (preparedTypeDefinitions != null && preparedTypeDefinitions.length() != 0) {
            stmt.setNString(2, preparedTypeDefinitions);
        }
        else {
            stmt.setNString(2, "");
        }
        return ((SQLServerPreparedStatement)stmt).executeQueryInternal();
    }
    
    default void processSDPEv1(final String userSql, final String preparedTypeDefinitions, final Parameter[] params, final ArrayList<String> parameterNames, final SQLServerConnection connection, final PreparedStatement stmt, ResultSet rs, final ArrayList<byte[]> enclaveRequestedCEKs) throws SQLException {
        final Map<Integer, CekTableEntry> cekList = new HashMap<Integer, CekTableEntry>();
        CekTableEntry cekEntry = null;
        boolean isRequestedByEnclave = false;
        while (rs.next()) {
            final int currentOrdinal = rs.getInt(DescribeParameterEncryptionResultSet1.KeyOrdinal.value());
            if (!cekList.containsKey(currentOrdinal)) {
                cekEntry = new CekTableEntry(currentOrdinal);
                cekList.put(cekEntry.ordinal, cekEntry);
            }
            else {
                cekEntry = cekList.get(currentOrdinal);
            }
            final String keyStoreName = rs.getString(DescribeParameterEncryptionResultSet1.ProviderName.value());
            final String algo = rs.getString(DescribeParameterEncryptionResultSet1.KeyEncryptionAlgorithm.value());
            final String keyPath = rs.getString(DescribeParameterEncryptionResultSet1.KeyPath.value());
            final int dbID = rs.getInt(DescribeParameterEncryptionResultSet1.DbId.value());
            final byte[] mdVer = rs.getBytes(DescribeParameterEncryptionResultSet1.KeyMdVersion.value());
            final int keyID = rs.getInt(DescribeParameterEncryptionResultSet1.KeyId.value());
            final byte[] encryptedKey = rs.getBytes(DescribeParameterEncryptionResultSet1.EncryptedKey.value());
            cekEntry.add(encryptedKey, dbID, keyID, rs.getInt(DescribeParameterEncryptionResultSet1.KeyVersion.value()), mdVer, keyPath, keyStoreName, algo);
            if (ColumnEncryptionVersion.AE_v2.value() <= connection.getServerColumnEncryptionVersion().value()) {
                isRequestedByEnclave = rs.getBoolean(DescribeParameterEncryptionResultSet1.IsRequestedByEnclave.value());
            }
            if (isRequestedByEnclave) {
                final byte[] keySignature = rs.getBytes(DescribeParameterEncryptionResultSet1.EnclaveCMKSignature.value());
                final String serverName = connection.getTrustedServerNameAE();
                SQLServerSecurityUtility.verifyColumnMasterKeyMetadata(connection, keyStoreName, keyPath, serverName, isRequestedByEnclave, keySignature);
                final ByteBuffer aev2CekEntry = ByteBuffer.allocate(46);
                aev2CekEntry.order(ByteOrder.LITTLE_ENDIAN).putInt(dbID);
                aev2CekEntry.put(mdVer);
                aev2CekEntry.putShort((short)keyID);
                aev2CekEntry.put(connection.getColumnEncryptionKeyStoreProvider(keyStoreName).decryptColumnEncryptionKey(keyPath, algo, encryptedKey));
                enclaveRequestedCEKs.add(aev2CekEntry.array());
            }
        }
        if (!stmt.getMoreResults()) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_UnexpectedDescribeParamFormat"), null, 0, false);
        }
        rs = stmt.getResultSet();
        while (rs.next() && null != params) {
            final String paramName = rs.getString(DescribeParameterEncryptionResultSet2.ParameterName.value());
            final int paramIndex = parameterNames.indexOf(paramName);
            final int cekOrdinal = rs.getInt(DescribeParameterEncryptionResultSet2.ColumnEncryptionKeyOrdinal.value());
            cekEntry = cekList.get(cekOrdinal);
            if (null != cekEntry && cekList.size() < cekOrdinal) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEncryptionKeyOrdinal"));
                final Object[] msgArgs = { cekOrdinal, cekEntry.getSize() };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            final SQLServerEncryptionType encType = SQLServerEncryptionType.of((byte)rs.getInt(DescribeParameterEncryptionResultSet2.ColumnEncrytionType.value()));
            if (SQLServerEncryptionType.PlainText != encType) {
                SQLServerSecurityUtility.decryptSymmetricKey(params[paramIndex].cryptoMeta = new CryptoMetadata(cekEntry, (short)cekOrdinal, (byte)rs.getInt(DescribeParameterEncryptionResultSet2.ColumnEncryptionAlgorithm.value()), null, encType.value, (byte)rs.getInt(DescribeParameterEncryptionResultSet2.NormalizationRuleVersion.value())), connection);
            }
            else {
                if (!params[paramIndex].getForceEncryption()) {
                    continue;
                }
                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAETrue_UnencryptedColumn"));
                final Object[] msgArgs2 = { userSql, paramIndex + 1 };
                SQLServerException.makeFromDriverError(null, connection, form2.format(msgArgs2), "0", true);
            }
        }
    }
    
    void getAttestationParameters(final String p0) throws SQLServerException;
    
    ArrayList<byte[]> createEnclaveSession(final SQLServerConnection p0, final String p1, final String p2, final Parameter[] p3, final ArrayList<String> p4) throws SQLServerException;
    
    void invalidateEnclaveSession();
    
    EnclaveSession getEnclaveSession();
}
