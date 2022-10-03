package org.apache.lucene.sandbox.queries.regex;

@Deprecated
public interface RegexQueryCapable
{
    void setRegexImplementation(final RegexCapabilities p0);
    
    RegexCapabilities getRegexImplementation();
}
