package org.apache.commons.text.similarity;

interface Tokenizer<T>
{
    T[] tokenize(final CharSequence p0);
}
