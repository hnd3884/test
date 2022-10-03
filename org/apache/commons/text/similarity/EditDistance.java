package org.apache.commons.text.similarity;

public interface EditDistance<R> extends SimilarityScore<R>
{
    R apply(final CharSequence p0, final CharSequence p1);
}
