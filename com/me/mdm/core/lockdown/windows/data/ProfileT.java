package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profile_t", propOrder = { "allAppsList", "startLayout", "taskbar", "kioskModeApp" })
public class ProfileT
{
    @XmlElement(name = "AllAppsList")
    protected AllappslistT allAppsList;
    @XmlElement(name = "StartLayout")
    protected String startLayout;
    @XmlElement(name = "Taskbar")
    protected TaskbarT taskbar;
    @XmlElement(name = "KioskModeApp")
    protected KioskmodeappT kioskModeApp;
    @XmlAttribute(name = "Id", required = true)
    protected String id;
    @XmlAttribute(name = "Name")
    protected String name;
    
    public AllappslistT getAllAppsList() {
        return this.allAppsList;
    }
    
    public void setAllAppsList(final AllappslistT value) {
        this.allAppsList = value;
    }
    
    public String getStartLayout() {
        return this.startLayout;
    }
    
    public void setStartLayout(final String value) {
        this.startLayout = value;
    }
    
    public TaskbarT getTaskbar() {
        return this.taskbar;
    }
    
    public void setTaskbar(final TaskbarT value) {
        this.taskbar = value;
    }
    
    public KioskmodeappT getKioskModeApp() {
        return this.kioskModeApp;
    }
    
    public void setKioskModeApp(final KioskmodeappT value) {
        this.kioskModeApp = value;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String value) {
        this.id = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
}
