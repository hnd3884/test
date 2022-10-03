package org.bouncycastle.tsp.cms;

import org.bouncycastle.tsp.TimeStampToken;

public class ImprintDigestInvalidException extends Exception
{
    private TimeStampToken token;
    
    public ImprintDigestInvalidException(final String s, final TimeStampToken token) {
        super(s);
        this.token = token;
    }
    
    public TimeStampToken getTimeStampToken() {
        return this.token;
    }
}
