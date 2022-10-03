package sun.text.normalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

final class UCharacterPropertyReader implements ICUBinary.Authenticate
{
    private static final int INDEX_SIZE_ = 16;
    private DataInputStream m_dataInputStream_;
    private int m_propertyOffset_;
    private int m_exceptionOffset_;
    private int m_caseOffset_;
    private int m_additionalOffset_;
    private int m_additionalVectorsOffset_;
    private int m_additionalColumnsCount_;
    private int m_reservedOffset_;
    private byte[] m_unicodeVersion_;
    private static final byte[] DATA_FORMAT_ID_;
    private static final byte[] DATA_FORMAT_VERSION_;
    
    @Override
    public boolean isDataVersionAcceptable(final byte[] array) {
        return array[0] == UCharacterPropertyReader.DATA_FORMAT_VERSION_[0] && array[2] == UCharacterPropertyReader.DATA_FORMAT_VERSION_[2] && array[3] == UCharacterPropertyReader.DATA_FORMAT_VERSION_[3];
    }
    
    protected UCharacterPropertyReader(final InputStream inputStream) throws IOException {
        this.m_unicodeVersion_ = ICUBinary.readHeader(inputStream, UCharacterPropertyReader.DATA_FORMAT_ID_, this);
        this.m_dataInputStream_ = new DataInputStream(inputStream);
    }
    
    protected void read(final UCharacterProperty uCharacterProperty) throws IOException {
        int n = 16;
        this.m_propertyOffset_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_exceptionOffset_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_caseOffset_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_additionalOffset_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_additionalVectorsOffset_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_additionalColumnsCount_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_reservedOffset_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_dataInputStream_.skipBytes(12);
        n -= 3;
        uCharacterProperty.m_maxBlockScriptValue_ = this.m_dataInputStream_.readInt();
        --n;
        uCharacterProperty.m_maxJTGValue_ = this.m_dataInputStream_.readInt();
        --n;
        this.m_dataInputStream_.skipBytes(n << 2);
        uCharacterProperty.m_trie_ = new CharTrie(this.m_dataInputStream_, null);
        this.m_dataInputStream_.skipBytes((this.m_exceptionOffset_ - this.m_propertyOffset_) * 4);
        this.m_dataInputStream_.skipBytes((this.m_caseOffset_ - this.m_exceptionOffset_) * 4);
        this.m_dataInputStream_.skipBytes((this.m_additionalOffset_ - this.m_caseOffset_ << 1) * 2);
        if (this.m_additionalColumnsCount_ > 0) {
            uCharacterProperty.m_additionalTrie_ = new CharTrie(this.m_dataInputStream_, null);
            final int n2 = this.m_reservedOffset_ - this.m_additionalVectorsOffset_;
            uCharacterProperty.m_additionalVectors_ = new int[n2];
            for (int i = 0; i < n2; ++i) {
                uCharacterProperty.m_additionalVectors_[i] = this.m_dataInputStream_.readInt();
            }
        }
        this.m_dataInputStream_.close();
        uCharacterProperty.m_additionalColumnsCount_ = this.m_additionalColumnsCount_;
        uCharacterProperty.m_unicodeVersion_ = VersionInfo.getInstance(this.m_unicodeVersion_[0], this.m_unicodeVersion_[1], this.m_unicodeVersion_[2], this.m_unicodeVersion_[3]);
    }
    
    static {
        DATA_FORMAT_ID_ = new byte[] { 85, 80, 114, 111 };
        DATA_FORMAT_VERSION_ = new byte[] { 5, 0, 5, 2 };
    }
}
