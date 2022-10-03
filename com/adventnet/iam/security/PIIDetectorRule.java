package com.adventnet.iam.security;

import org.w3c.dom.Element;
import com.zoho.security.zsecpiidetector.handler.PIIHandler;
import com.zoho.security.zsecpiidetector.PIIClassifier;

public class PIIDetectorRule
{
    private PIIClassifier piiClassifier;
    private PIIHandler piiHandler;
    
    public PIIDetectorRule(final Element classifier) {
        this.piiClassifier = null;
        this.piiHandler = null;
        final Element categoryElement = RuleSetParser.getFirstChildNodeByTagName(classifier, "categories");
        final Element sensitivityElement = RuleSetParser.getFirstChildNodeByTagName(classifier, "sensitivities");
        this.setPiiClassifier(new PIIClassifier(categoryElement, sensitivityElement, (Element)null));
        this.setPiiHandler(PIIHandler.DEFAULT_PII_HANDLER);
    }
    
    public PIIClassifier getPiiClassifier() {
        return this.piiClassifier;
    }
    
    public void setPiiClassifier(final PIIClassifier localPiiClassifier) {
        this.piiClassifier = localPiiClassifier;
    }
    
    public PIIHandler getPiiHandler() {
        return this.piiHandler;
    }
    
    public void setPiiHandler(final PIIHandler localPiiHandler) {
        this.piiHandler = localPiiHandler;
    }
}
