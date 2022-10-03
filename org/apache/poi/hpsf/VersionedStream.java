package org.apache.poi.hpsf;

import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.Internal;

@Internal
public class VersionedStream
{
    private final GUID _versionGuid;
    private final IndirectPropertyName _streamName;
    
    public VersionedStream() {
        this._versionGuid = new GUID();
        this._streamName = new IndirectPropertyName();
    }
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        this._versionGuid.read(lei);
        this._streamName.read(lei);
    }
}
