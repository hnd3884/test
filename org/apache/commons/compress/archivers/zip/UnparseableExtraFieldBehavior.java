package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface UnparseableExtraFieldBehavior
{
    ZipExtraField onUnparseableExtraField(final byte[] p0, final int p1, final int p2, final boolean p3, final int p4) throws ZipException;
}
