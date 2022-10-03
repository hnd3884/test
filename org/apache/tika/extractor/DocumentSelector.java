package org.apache.tika.extractor;

import org.apache.tika.metadata.Metadata;

public interface DocumentSelector
{
    boolean select(final Metadata p0);
}
