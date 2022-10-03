package org.jfree.chart.imagemap;

public class StandardURLTagFragmentGenerator implements URLTagFragmentGenerator
{
    public String generateURLFragment(final String urlText) {
        return " href=\"" + urlText + "\"";
    }
}
