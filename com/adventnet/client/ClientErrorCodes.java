package com.adventnet.client;

public enum ClientErrorCodes
{
    VIEW_NOT_PRESENT("0", "View not present in database"), 
    EXPIRED_STATE_PASSED("1", "The state passed is for the previous session.Please re-navigate from the home page."), 
    STATE_COOKIE_NOT_PASSED("2", "State cookie is not passed along with the request. Can happen in case of  use of back button."), 
    STATE_SSOID_NOT_PASSED("3", "Session Id has not been passed along with the cookie"), 
    RECURSIVE_LAYOUT("4", "The layout is recursive"), 
    REQUIRED_PARAM_NOT_PASSED("5", "The required parameter was not passed"), 
    COUNT_NOT_SUPPORTED("500", "Count is not supported for this view"), 
    SQL_QUERY_NULL("1001", "sql query for model should not be null"), 
    SQL_QUERY_NULL_FETCHCOUNT("1002", "count sql should not be null if fetchCount or fetchPrevPage is enabled"), 
    VIEW_MENU_NULL("1003", "Either menuId or ViewContext should not be null"), 
    TRANSCTX_MENU_NULL("1004", "Either menuId or TransformerContext should not be null");
    
    private String code;
    private String message;
    
    private ClientErrorCodes(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return this.code + ": " + this.message;
    }
}
