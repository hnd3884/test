package org.jfree.chart.urls;

import org.jfree.data.general.PieDataset;
import java.io.Serializable;

public class StandardPieURLGenerator implements PieURLGenerator, Serializable
{
    private static final long serialVersionUID = 1626966402065883419L;
    private String prefix;
    private String categoryParameterName;
    private String indexParameterName;
    
    public StandardPieURLGenerator() {
        this.prefix = "index.html";
        this.categoryParameterName = "category";
        this.indexParameterName = "pieIndex";
    }
    
    public StandardPieURLGenerator(final String prefix) {
        this.prefix = "index.html";
        this.categoryParameterName = "category";
        this.indexParameterName = "pieIndex";
        this.prefix = prefix;
    }
    
    public StandardPieURLGenerator(final String prefix, final String categoryParameterName) {
        this.prefix = "index.html";
        this.categoryParameterName = "category";
        this.indexParameterName = "pieIndex";
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
    }
    
    public StandardPieURLGenerator(final String prefix, final String categoryParameterName, final String indexParameterName) {
        this.prefix = "index.html";
        this.categoryParameterName = "category";
        this.indexParameterName = "pieIndex";
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
        this.indexParameterName = indexParameterName;
    }
    
    public String generateURL(final PieDataset data, final Comparable key, final int pieIndex) {
        String url = this.prefix;
        if (url.indexOf("?") > -1) {
            url = url + "&amp;" + this.categoryParameterName + "=" + key.toString();
        }
        else {
            url = url + "?" + this.categoryParameterName + "=" + key.toString();
        }
        if (this.indexParameterName != null) {
            url = url + "&amp;" + this.indexParameterName + "=" + String.valueOf(pieIndex);
        }
        return url;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardPieURLGenerator)) {
            return false;
        }
        final StandardPieURLGenerator generator = (StandardPieURLGenerator)obj;
        return this.categoryParameterName.equals(generator.categoryParameterName) && this.prefix.equals(generator.prefix);
    }
}
