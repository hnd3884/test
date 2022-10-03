package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.compressing.CompressingTermVectorsFormat;

public final class Lucene50TermVectorsFormat extends CompressingTermVectorsFormat
{
    public Lucene50TermVectorsFormat() {
        super("Lucene50TermVectors", "", CompressionMode.FAST, 4096, 1024);
    }
}
