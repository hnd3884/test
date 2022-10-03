package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface ExtraFieldParsingBehavior extends UnparseableExtraFieldBehavior
{
    ZipExtraField createExtraField(final ZipShort p0) throws ZipException, InstantiationException, IllegalAccessException;
    
    ZipExtraField fill(final ZipExtraField p0, final byte[] p1, final int p2, final int p3, final boolean p4) throws ZipException;
}
