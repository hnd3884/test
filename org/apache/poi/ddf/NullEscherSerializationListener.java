package org.apache.poi.ddf;

public class NullEscherSerializationListener implements EscherSerializationListener
{
    @Override
    public void beforeRecordSerialize(final int offset, final short recordId, final EscherRecord record) {
    }
    
    @Override
    public void afterRecordSerialize(final int offset, final short recordId, final int size, final EscherRecord record) {
    }
}
