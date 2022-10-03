package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory
{
    public AssignedAccessConfiguration createAssignedAccessConfiguration() {
        return new AssignedAccessConfiguration();
    }
    
    public ProfileListT createProfileListT() {
        return new ProfileListT();
    }
    
    public ConfigListT createConfigListT() {
        return new ConfigListT();
    }
    
    public SpecialGroupT createSpecialGroupT() {
        return new SpecialGroupT();
    }
    
    public TaskbarT createTaskbarT() {
        return new TaskbarT();
    }
    
    public GroupT createGroupT() {
        return new GroupT();
    }
    
    public KioskmodeappT createKioskmodeappT() {
        return new KioskmodeappT();
    }
    
    public AllowedappsT createAllowedappsT() {
        return new AllowedappsT();
    }
    
    public ProfileIdT createProfileIdT() {
        return new ProfileIdT();
    }
    
    public ProfileT createProfileT() {
        return new ProfileT();
    }
    
    public AllappslistT createAllappslistT() {
        return new AllappslistT();
    }
    
    public AutologonAccountT createAutologonAccountT() {
        return new AutologonAccountT();
    }
    
    public ConfigT createConfigT() {
        return new ConfigT();
    }
    
    public AppT createAppT() {
        return new AppT();
    }
}
