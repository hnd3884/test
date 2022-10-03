package org.apache.axiom.blob;

public interface OverflowableBlob extends WritableBlob
{
    WritableBlob getOverflowBlob();
}
