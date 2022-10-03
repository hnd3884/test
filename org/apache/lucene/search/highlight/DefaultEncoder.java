package org.apache.lucene.search.highlight;

public class DefaultEncoder implements Encoder
{
    @Override
    public String encodeText(final String originalText) {
        return originalText;
    }
}
