package com.adventnet.iam.security;

import com.zoho.security.util.TikaUtil;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.List;
import java.util.logging.Logger;

public class ContentTypeRule
{
    private static final Logger LOGGER;
    private final String name;
    private List<String> contentTypes;
    private List<String> types;
    private List<ContentTypeRule> extendedContentTypeRules;
    protected List<String> extendedRuleNames;
    
    protected ContentTypeRule(final Element contentTypeEle) throws RuntimeException {
        this.name = contentTypeEle.getAttribute("name");
        if (SecurityUtil.isValid(contentTypeEle.getAttribute("extends"))) {
            this.extendedRuleNames = new ArrayList<String>();
            for (final String ruleName : contentTypeEle.getAttribute("extends").split(",")) {
                this.extendedRuleNames.add(ruleName.trim());
            }
        }
        try {
            for (final Element valueElement : RuleSetParser.getChildNodesByTagName(contentTypeEle, "value")) {
                final String value = valueElement.getTextContent();
                if (value.contains("/")) {
                    this.addContentType(value);
                }
                else {
                    this.addType(value);
                }
            }
        }
        catch (final Exception e) {
            ContentTypeRule.LOGGER.log(Level.SEVERE, "Invalid configuration at the <content-type> rule. Name: {0}", new Object[] { this.name });
            throw e;
        }
    }
    
    public ContentTypeRule(final String name) {
        this.name = name;
    }
    
    protected void initExtendedContentTypeRulesIfPresent(final Map<String, ContentTypeRule> contentTypeRuleMap) throws RuntimeException {
        if (this.extendedRuleNames == null || contentTypeRuleMap == null) {
            return;
        }
        for (final String extendedRuleName : this.extendedRuleNames) {
            final ContentTypeRule extendedRule = contentTypeRuleMap.get(extendedRuleName);
            if (extendedRule == null) {
                throw new RuntimeException("The content-type rule '" + this.name + "' extends the content-type rule '" + extendedRuleName + "' is not configured.");
            }
            this.addExtendedRules(extendedRule);
        }
    }
    
    protected void validateConfigurationAgainstTika() throws RuntimeException {
        if (this.contentTypes != null) {
            for (final String contentType : this.contentTypes) {
                if (!TikaUtil.isValidContentType(contentType)) {
                    throw new RuntimeException("The content-type rule '" + this.name + "' contains the value '" + contentType + "' is not supported by tika.");
                }
            }
        }
        if (this.types != null) {
            for (final String type : this.types) {
                if (!TikaUtil.isValidType(type)) {
                    throw new RuntimeException("The content-type rule '" + this.name + "' contains the value '" + type + "' is not supported by tika.");
                }
            }
        }
    }
    
    public void addContentType(final String contentType) {
        if (!SecurityUtil.isValid(contentType)) {
            return;
        }
        if (this.contentTypes == null) {
            this.contentTypes = new ArrayList<String>();
        }
        this.contentTypes.add(contentType);
    }
    
    public void addType(final String type) throws RuntimeException {
        if (!SecurityUtil.isValid(type)) {
            return;
        }
        if (this.types == null) {
            this.types = new ArrayList<String>();
        }
        this.types.add(type);
    }
    
    public void addExtendedRules(final ContentTypeRule contentTypeRule) {
        if (contentTypeRule == null) {
            return;
        }
        if (this.extendedContentTypeRules == null) {
            this.extendedContentTypeRules = new ArrayList<ContentTypeRule>();
        }
        this.extendedContentTypeRules.add(contentTypeRule);
    }
    
    public boolean contains(final String contentType) {
        if (!SecurityUtil.isValid(contentType)) {
            return false;
        }
        if (this.contentTypes != null && this.contentTypes.contains(contentType)) {
            return true;
        }
        if (this.types != null) {
            final String type = contentType.substring(0, contentType.indexOf("/"));
            if (this.types.contains(type)) {
                return true;
            }
        }
        if (this.extendedContentTypeRules != null) {
            for (final ContentTypeRule contentTypeRule : this.extendedContentTypeRules) {
                if (contentTypeRule.contains(contentType)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getContentTypes() {
        return this.contentTypes;
    }
    
    public List<String> getTypes() {
        return this.types;
    }
    
    public List<ContentTypeRule> getExtendedContentTypeRules() {
        return this.extendedContentTypeRules;
    }
    
    static {
        LOGGER = Logger.getLogger(ContentTypeRule.class.getName());
    }
}
