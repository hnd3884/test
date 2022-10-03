package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "taskbar_t")
public class TaskbarT
{
    @XmlAttribute(name = "ShowTaskbar", required = true)
    protected boolean showTaskbar;
    
    public boolean isShowTaskbar() {
        return this.showTaskbar;
    }
    
    public void setShowTaskbar(final boolean value) {
        this.showTaskbar = value;
    }
}
