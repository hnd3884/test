package javax.naming.ldap;

public class SortKey
{
    private String attrID;
    private boolean reverseOrder;
    private String matchingRuleID;
    
    public SortKey(final String attrID) {
        this.reverseOrder = false;
        this.matchingRuleID = null;
        this.attrID = attrID;
    }
    
    public SortKey(final String attrID, final boolean b, final String matchingRuleID) {
        this.reverseOrder = false;
        this.matchingRuleID = null;
        this.attrID = attrID;
        this.reverseOrder = !b;
        this.matchingRuleID = matchingRuleID;
    }
    
    public String getAttributeID() {
        return this.attrID;
    }
    
    public boolean isAscending() {
        return !this.reverseOrder;
    }
    
    public String getMatchingRuleID() {
        return this.matchingRuleID;
    }
}
