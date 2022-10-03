package sun.security.krb5.internal.ccache;

import java.io.ByteArrayOutputStream;

public class Tag
{
    int length;
    int tag;
    int tagLen;
    Integer time_offset;
    Integer usec_offset;
    
    public Tag(final int n, final int tag, final Integer time_offset, final Integer usec_offset) {
        this.tag = tag;
        this.tagLen = 8;
        this.time_offset = time_offset;
        this.usec_offset = usec_offset;
        this.length = 4 + this.tagLen;
    }
    
    public Tag(final int tag) {
        this.tag = tag;
        this.tagLen = 0;
        this.length = 4 + this.tagLen;
    }
    
    public byte[] toByteArray() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(this.length);
        byteArrayOutputStream.write(this.tag);
        byteArrayOutputStream.write(this.tagLen);
        if (this.time_offset != null) {
            byteArrayOutputStream.write(this.time_offset);
        }
        if (this.usec_offset != null) {
            byteArrayOutputStream.write(this.usec_offset);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
