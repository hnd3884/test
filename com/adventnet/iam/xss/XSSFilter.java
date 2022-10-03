package com.adventnet.iam.xss;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.util.logging.Logger;

public abstract class XSSFilter
{
    Logger logger;
    public XSSFilterConfiguration xssFilterConfig;
    public CSSUtil cssUtil;
    public List<String> removedElementsList;
    public boolean altered;
    
    public XSSFilter() {
        this.logger = Logger.getLogger(XSSFilter.class.getName());
        this.xssFilterConfig = null;
        this.cssUtil = null;
        this.removedElementsList = null;
        this.altered = false;
    }
    
    void init(final Properties prop, final XSSFilterConfiguration xfc) {
        this.xssFilterConfig = xfc;
        this.cssUtil = new CSSUtil(prop);
    }
    
    public abstract String filterXSS(final String p0, final String p1, final String p2);
    
    public abstract String balanceHTMLContent(final String p0, final String p1);
    
    void SET_ALTERED() {
        if (!this.altered) {
            this.altered = true;
        }
    }
    
    void ELEMENT_REMOVED(final String elementName) {
        if (this.removedElementsList == null) {
            this.removedElementsList = new ArrayList<String>();
        }
        this.removedElementsList.add(elementName.toLowerCase());
        this.SET_ALTERED();
    }
    
    public List<String> getRemovedElements() {
        return this.removedElementsList;
    }
    
    public boolean isAltered() {
        return this.altered;
    }
    
    public boolean isCSSUtilAltered() {
        return this.cssUtil != null && this.cssUtil.altered;
    }
    
    public void reset() {
        this.altered = false;
        this.removedElementsList = null;
        if (this.cssUtil != null) {
            this.cssUtil.altered = false;
        }
    }
}
