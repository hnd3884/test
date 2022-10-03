package com.adventnet.client.view.web;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.State;
import com.adventnet.client.view.ViewModel;

public class WebViewModel extends ViewModel
{
    private String urlToForward;
    private ViewController controller;
    private State stateObj;
    
    public WebViewModel(final DataObject viewConfig) {
        super(viewConfig);
    }
    
    public void setController(final ViewController viewController) {
        this.controller = viewController;
    }
    
    public String getForwardURL() {
        try {
            if (this.urlToForward != null) {
                return this.urlToForward;
            }
            String compUrl = null;
            if (this.viewConfig.containsTable("WebViewConfig")) {
                compUrl = (String)this.viewConfig.getFirstValue("WebViewConfig", 2);
            }
            if (compUrl == null && this.uiComponentConfig != null) {
                compUrl = (String)this.uiComponentConfig.getFirstValue("WebUIComponent", 2);
            }
            if (compUrl == null) {
                throw new RuntimeException("View configuration " + this.viewConfig.getFirstValue("ViewConfiguration", 2) + " is neither associated with a component nor is the Web View configuration is specified");
            }
            compUrl += ((compUrl.indexOf(63) > -1) ? "&" : "?");
            compUrl += WebViewAPI.getAdditionalParamsAsURLQueryString(this.viewConfig);
            synchronized (this) {
                this.urlToForward = compUrl;
            }
        }
        catch (final DataAccessException ex) {
            throw new RuntimeException((Throwable)ex);
        }
        return this.urlToForward;
    }
    
    public ViewController getController() {
        return this.controller;
    }
    
    public State getState() {
        return this.stateObj;
    }
    
    public void setState(final State s) {
        this.stateObj = s;
    }
}
