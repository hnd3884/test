package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Attribute;

public interface TermToBytesRefAttribute extends Attribute
{
    BytesRef getBytesRef();
}
