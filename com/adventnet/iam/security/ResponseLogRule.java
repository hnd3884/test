package com.adventnet.iam.security;

import org.w3c.dom.Element;
import java.util.List;

public class ResponseLogRule
{
    private long size;
    private List<String> allowedContentTypes;
    
    public ResponseLogRule(final Element element) {
        this.size = -1L;
        this.allowedContentTypes = null;
        final String sizeAsString = element.getAttribute("size");
        if (SecurityUtil.isValid(sizeAsString)) {
            this.size = Long.parseLong(sizeAsString);
        }
        this.allowedContentTypes = SecurityUtil.getStringAsList(element.getAttribute("content-types"), ",");
    }
    
    public long getAllowedSize() {
        return this.size;
    }
    
    public List<String> getAllowedContentTypes() {
        return this.allowedContentTypes;
    }
}
