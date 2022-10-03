package com.microsoft.sqlserver.jdbc;

import java.util.List;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityProperty;
import com.microsoft.sqlserver.jdbc.dataclassification.ColumnSensitivity;
import java.util.ArrayList;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import com.microsoft.sqlserver.jdbc.dataclassification.InformationType;
import com.microsoft.sqlserver.jdbc.dataclassification.Label;

final class StreamColumns extends StreamPacket
{
    private Column[] columns;
    private CekTable cekTable;
    private boolean shouldHonorAEForRead;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    CekTable getCekTable() {
        return this.cekTable;
    }
    
    StreamColumns() {
        super(129);
        this.cekTable = null;
        this.shouldHonorAEForRead = false;
    }
    
    StreamColumns(final boolean honorAE) {
        super(129);
        this.cekTable = null;
        this.shouldHonorAEForRead = false;
        this.shouldHonorAEForRead = honorAE;
    }
    
    CekTableEntry readCEKTableEntry(final TDSReader tdsReader) throws SQLServerException {
        final int databaseId = tdsReader.readInt();
        final int cekId = tdsReader.readInt();
        final int cekVersion = tdsReader.readInt();
        final byte[] cekMdVersion = new byte[8];
        tdsReader.readBytes(cekMdVersion, 0, 8);
        final int cekValueCount = tdsReader.readUnsignedByte();
        final CekTableEntry cekTableEntry = new CekTableEntry(cekValueCount);
        for (int i = 0; i < cekValueCount; ++i) {
            final short encryptedCEKlength = tdsReader.readShort();
            final byte[] encryptedCek = new byte[encryptedCEKlength];
            tdsReader.readBytes(encryptedCek, 0, encryptedCEKlength);
            final int keyStoreLength = tdsReader.readUnsignedByte();
            final String keyStoreName = tdsReader.readUnicodeString(keyStoreLength);
            final int keyPathLength = tdsReader.readShort();
            final String keyPath = tdsReader.readUnicodeString(keyPathLength);
            final int algorithmLength = tdsReader.readUnsignedByte();
            final String algorithmName = tdsReader.readUnicodeString(algorithmLength);
            cekTableEntry.add(encryptedCek, databaseId, cekId, cekVersion, cekMdVersion, keyPath, keyStoreName, algorithmName);
        }
        return cekTableEntry;
    }
    
    void readCEKTable(final TDSReader tdsReader) throws SQLServerException {
        final int tableSize = tdsReader.readShort();
        if (0 != tableSize) {
            this.cekTable = new CekTable(tableSize);
            for (int i = 0; i < tableSize; ++i) {
                this.cekTable.setCekTableEntry(i, this.readCEKTableEntry(tdsReader));
            }
        }
    }
    
    CryptoMetadata readCryptoMetadata(final TDSReader tdsReader) throws SQLServerException {
        short ordinal = 0;
        if (null != this.cekTable) {
            ordinal = tdsReader.readShort();
        }
        final TypeInfo typeInfo = TypeInfo.getInstance(tdsReader, false);
        final byte algorithmId = (byte)tdsReader.readUnsignedByte();
        String algorithmName = null;
        if (0 == algorithmId) {
            final int nameSize = tdsReader.readUnsignedByte();
            algorithmName = tdsReader.readUnicodeString(nameSize);
        }
        final byte encryptionType = (byte)tdsReader.readUnsignedByte();
        final byte normalizationRuleVersion = (byte)tdsReader.readUnsignedByte();
        final CryptoMetadata cryptoMeta = new CryptoMetadata((this.cekTable == null) ? null : this.cekTable.getCekTableEntry(ordinal), ordinal, algorithmId, algorithmName, encryptionType, normalizationRuleVersion);
        cryptoMeta.setBaseTypeInfo(typeInfo);
        return cryptoMeta;
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (129 != tdsReader.readUnsignedByte() && !StreamColumns.$assertionsDisabled) {
            throw new AssertionError();
        }
        final int nTotColumns = tdsReader.readUnsignedShort();
        if (65535 == nTotColumns) {
            return;
        }
        if (tdsReader.getServerSupportsColumnEncryption()) {
            this.readCEKTable(tdsReader);
        }
        this.columns = new Column[nTotColumns];
        for (int numColumns = 0; numColumns < nTotColumns; ++numColumns) {
            final TypeInfo typeInfo = TypeInfo.getInstance(tdsReader, true);
            SQLIdentifier tableName = new SQLIdentifier();
            if (SSType.TEXT == typeInfo.getSSType() || SSType.NTEXT == typeInfo.getSSType() || SSType.IMAGE == typeInfo.getSSType()) {
                tableName = tdsReader.readSQLIdentifier();
            }
            CryptoMetadata cryptoMeta = null;
            if (tdsReader.getServerSupportsColumnEncryption() && typeInfo.isEncrypted()) {
                cryptoMeta = this.readCryptoMetadata(tdsReader);
                cryptoMeta.baseTypeInfo.setFlags(typeInfo.getFlagsAsShort());
                typeInfo.setSQLCollation(cryptoMeta.baseTypeInfo.getSQLCollation());
            }
            final String columnName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
            if (this.shouldHonorAEForRead) {
                this.columns[numColumns] = new Column(typeInfo, columnName, tableName, cryptoMeta);
            }
            else {
                this.columns[numColumns] = new Column(typeInfo, columnName, tableName, null);
            }
        }
        if (tdsReader.getServerSupportsDataClassification() && tdsReader.peekTokenType() == 163) {
            tdsReader.trySetSensitivityClassification(this.processDataClassification(tdsReader));
        }
    }
    
    private String readByteString(final TDSReader tdsReader) throws SQLServerException {
        String value = "";
        final int byteLen = tdsReader.readUnsignedByte();
        value = tdsReader.readUnicodeString(byteLen);
        return value;
    }
    
    private Label readSensitivityLabel(final TDSReader tdsReader) throws SQLServerException {
        final String name = this.readByteString(tdsReader);
        final String id = this.readByteString(tdsReader);
        return new Label(name, id);
    }
    
    private InformationType readSensitivityInformationType(final TDSReader tdsReader) throws SQLServerException {
        final String name = this.readByteString(tdsReader);
        final String id = this.readByteString(tdsReader);
        return new InformationType(name, id);
    }
    
    private SensitivityClassification processDataClassification(final TDSReader tdsReader) throws SQLServerException {
        if (!tdsReader.getServerSupportsDataClassification()) {
            tdsReader.throwInvalidTDS();
        }
        final int dataClassificationToken = tdsReader.readUnsignedByte();
        assert dataClassificationToken == 163;
        SensitivityClassification sensitivityClassification = null;
        final int numLabels = tdsReader.readUnsignedShort();
        final List<Label> labels = new ArrayList<Label>(numLabels);
        for (int i = 0; i < numLabels; ++i) {
            labels.add(this.readSensitivityLabel(tdsReader));
        }
        final int numInformationTypes = tdsReader.readUnsignedShort();
        final List<InformationType> informationTypes = new ArrayList<InformationType>(numInformationTypes);
        for (int j = 0; j < numInformationTypes; ++j) {
            informationTypes.add(this.readSensitivityInformationType(tdsReader));
        }
        final int numResultColumns = tdsReader.readUnsignedShort();
        final List<ColumnSensitivity> columnSensitivities = new ArrayList<ColumnSensitivity>(numResultColumns);
        for (int columnNum = 0; columnNum < numResultColumns; ++columnNum) {
            final int numSources = tdsReader.readUnsignedShort();
            final List<SensitivityProperty> sensitivityProperties = new ArrayList<SensitivityProperty>(numSources);
            for (int sourceNum = 0; sourceNum < numSources; ++sourceNum) {
                final int labelIndex = tdsReader.readUnsignedShort();
                Label label = null;
                if (labelIndex != Integer.MAX_VALUE) {
                    if (labelIndex >= labels.size()) {
                        tdsReader.throwInvalidTDS();
                    }
                    label = labels.get(labelIndex);
                }
                final int informationTypeIndex = tdsReader.readUnsignedShort();
                InformationType informationType = null;
                if (informationTypeIndex != Integer.MAX_VALUE) {
                    if (informationTypeIndex >= informationTypes.size()) {}
                    informationType = informationTypes.get(informationTypeIndex);
                }
                sensitivityProperties.add(new SensitivityProperty(label, informationType));
            }
            columnSensitivities.add(new ColumnSensitivity(sensitivityProperties));
        }
        sensitivityClassification = new SensitivityClassification(labels, informationTypes, columnSensitivities);
        return sensitivityClassification;
    }
    
    Column[] buildColumns(final StreamColInfo colInfoToken, final StreamTabName tabNameToken) throws SQLServerException {
        if (null != colInfoToken && null != tabNameToken) {
            tabNameToken.applyTo(this.columns, colInfoToken.applyTo(this.columns));
        }
        return this.columns;
    }
}
