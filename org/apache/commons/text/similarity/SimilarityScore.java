package org.apache.commons.text.similarity;

public interface SimilarityScore<R>
{
    R apply(final CharSequence p0, final CharSequence p1);
}
