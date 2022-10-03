package com.adventnet.client.view.dynamiccontentarea.web;

import com.adventnet.client.view.web.ViewContext;
import java.util.List;

public class DynamicContentAreaModel
{
    List vcList;
    String contentAreaName;
    
    public DynamicContentAreaModel(final String contentAreaNameArg, final List vcListArg) {
        this.vcList = null;
        this.contentAreaName = null;
        this.vcList = vcListArg;
        this.contentAreaName = contentAreaNameArg;
    }
    
    public List getContentList() {
        return this.vcList;
    }
    
    public String getContentAreaName() {
        return this.contentAreaName;
    }
    
    public void addToList(final ViewContext vc) {
        final int index = this.vcList.indexOf(vc);
        if (index > -1) {
            for (int i = this.vcList.size() - 1; i > index; --i) {
                this.vcList.remove(i);
            }
        }
        else {
            this.vcList.add(vc);
        }
    }
    
    public boolean popList() {
        if (this.vcList.size() > 0) {
            this.vcList.remove(this.vcList.size() - 1);
            return true;
        }
        return false;
    }
    
    public void popListTo(final ViewContext vc) {
        final int index = this.vcList.indexOf(vc);
        if (index > -1) {
            for (int i = this.vcList.size() - 1; i >= index; --i) {
                this.vcList.remove(i);
            }
        }
        else {
            this.replaceList(vc);
        }
    }
    
    public void replaceList(final ViewContext vc) {
        this.vcList.clear();
        this.vcList.add(vc);
    }
    
    public void clearList() {
        this.vcList.clear();
    }
    
    public ViewContext getCurrentItem() {
        return (this.vcList.size() > 0) ? this.vcList.get(this.vcList.size() - 1) : null;
    }
    
    public String getContentIdsAsString() {
        final List caList = this.getContentList();
        final StringBuffer dataBuffer = new StringBuffer();
        for (int i = 0; i < caList.size(); ++i) {
            final ViewContext vc = caList.get(i);
            dataBuffer.append(vc.getReferenceId());
            if (caList.size() - 1 != i) {
                dataBuffer.append('-');
            }
        }
        return dataBuffer.toString();
    }
    
    @Override
    public String toString() {
        return this.vcList.toString();
    }
}
