package com.zoho.security.zsecpiidetector.detector;

import com.zoho.security.zsecpiidetector.handler.Indexer;
import com.zoho.security.zsecpiidetector.PIIScoreFilter;
import com.zoho.security.zsecpiidetector.PIIResult;
import com.zoho.security.zsecpiidetector.handler.PIIHandler;
import com.zoho.security.zsecpiidetector.PIIClassifier;

public abstract class PIIDetector
{
    protected PIIClassifier piiClassifier;
    protected PIIHandler piiHandler;
    
    public PIIDetector(final PIIClassifier localPiiClassifier, final PIIHandler localPiiHandler) {
        this.piiClassifier = localPiiClassifier;
        this.piiHandler = localPiiHandler;
    }
    
    public abstract PIIResult detect(final String p0);
    
    public abstract PIIResult detect(final String p0, final boolean p1);
    
    public abstract PIIResult detect(final String p0, final PIIScoreFilter p1);
    
    public abstract PIIResult detect(final String p0, final PIIScoreFilter p1, final boolean p2);
    
    public PIIResult.InfoFormat getResultInfoFormat(final boolean enableDetailDetector) {
        PIIResult.InfoFormat infoFormate;
        if (enableDetailDetector) {
            if (this.piiHandler instanceof Indexer) {
                infoFormate = PIIResult.InfoFormat.INDEX;
            }
            else {
                infoFormate = PIIResult.InfoFormat.NORMAL;
            }
        }
        else {
            infoFormate = PIIResult.InfoFormat.DISABLE;
        }
        return infoFormate;
    }
}
