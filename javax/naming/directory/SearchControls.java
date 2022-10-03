package javax.naming.directory;

import java.io.Serializable;

public class SearchControls implements Serializable
{
    public static final int OBJECT_SCOPE = 0;
    public static final int ONELEVEL_SCOPE = 1;
    public static final int SUBTREE_SCOPE = 2;
    private int searchScope;
    private int timeLimit;
    private boolean derefLink;
    private boolean returnObj;
    private long countLimit;
    private String[] attributesToReturn;
    private static final long serialVersionUID = -2480540967773454797L;
    
    public SearchControls() {
        this.searchScope = 1;
        this.timeLimit = 0;
        this.countLimit = 0L;
        this.derefLink = false;
        this.returnObj = false;
        this.attributesToReturn = null;
    }
    
    public SearchControls(final int searchScope, final long countLimit, final int timeLimit, final String[] attributesToReturn, final boolean returnObj, final boolean derefLink) {
        this.searchScope = searchScope;
        this.timeLimit = timeLimit;
        this.derefLink = derefLink;
        this.returnObj = returnObj;
        this.countLimit = countLimit;
        this.attributesToReturn = attributesToReturn;
    }
    
    public int getSearchScope() {
        return this.searchScope;
    }
    
    public int getTimeLimit() {
        return this.timeLimit;
    }
    
    public boolean getDerefLinkFlag() {
        return this.derefLink;
    }
    
    public boolean getReturningObjFlag() {
        return this.returnObj;
    }
    
    public long getCountLimit() {
        return this.countLimit;
    }
    
    public String[] getReturningAttributes() {
        return this.attributesToReturn;
    }
    
    public void setSearchScope(final int searchScope) {
        this.searchScope = searchScope;
    }
    
    public void setTimeLimit(final int timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public void setDerefLinkFlag(final boolean derefLink) {
        this.derefLink = derefLink;
    }
    
    public void setReturningObjFlag(final boolean returnObj) {
        this.returnObj = returnObj;
    }
    
    public void setCountLimit(final long countLimit) {
        this.countLimit = countLimit;
    }
    
    public void setReturningAttributes(final String[] attributesToReturn) {
        this.attributesToReturn = attributesToReturn;
    }
}
