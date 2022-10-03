package org.apache.lucene.search.vectorhighlight;

import org.apache.lucene.search.highlight.Encoder;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;

public interface FragmentsBuilder
{
    String createFragment(final IndexReader p0, final int p1, final String p2, final FieldFragList p3) throws IOException;
    
    String[] createFragments(final IndexReader p0, final int p1, final String p2, final FieldFragList p3, final int p4) throws IOException;
    
    String createFragment(final IndexReader p0, final int p1, final String p2, final FieldFragList p3, final String[] p4, final String[] p5, final Encoder p6) throws IOException;
    
    String[] createFragments(final IndexReader p0, final int p1, final String p2, final FieldFragList p3, final int p4, final String[] p5, final String[] p6, final Encoder p7) throws IOException;
}
