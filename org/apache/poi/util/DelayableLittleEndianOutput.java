package org.apache.poi.util;

public interface DelayableLittleEndianOutput extends LittleEndianOutput
{
    LittleEndianOutput createDelayedOutput(final int p0);
}
