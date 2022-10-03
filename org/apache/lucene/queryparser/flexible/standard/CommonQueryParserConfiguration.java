package org.apache.lucene.queryparser.flexible.standard;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.analysis.Analyzer;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.lucene.search.MultiTermQuery;

public interface CommonQueryParserConfiguration
{
    void setLowercaseExpandedTerms(final boolean p0);
    
    boolean getLowercaseExpandedTerms();
    
    void setAllowLeadingWildcard(final boolean p0);
    
    void setEnablePositionIncrements(final boolean p0);
    
    boolean getEnablePositionIncrements();
    
    void setMultiTermRewriteMethod(final MultiTermQuery.RewriteMethod p0);
    
    MultiTermQuery.RewriteMethod getMultiTermRewriteMethod();
    
    void setFuzzyPrefixLength(final int p0);
    
    void setLocale(final Locale p0);
    
    Locale getLocale();
    
    void setTimeZone(final TimeZone p0);
    
    TimeZone getTimeZone();
    
    void setPhraseSlop(final int p0);
    
    Analyzer getAnalyzer();
    
    boolean getAllowLeadingWildcard();
    
    float getFuzzyMinSim();
    
    int getFuzzyPrefixLength();
    
    int getPhraseSlop();
    
    void setFuzzyMinSim(final float p0);
    
    void setDateResolution(final DateTools.Resolution p0);
}
