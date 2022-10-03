package com.sun.corba.se.impl.naming.namingutil;

import java.util.List;
import java.util.ArrayList;

public abstract class INSURLBase implements INSURL
{
    protected boolean rirFlag;
    protected ArrayList theEndpointInfo;
    protected String theKeyString;
    protected String theStringifiedName;
    
    public INSURLBase() {
        this.rirFlag = false;
        this.theEndpointInfo = null;
        this.theKeyString = "NameService";
        this.theStringifiedName = null;
    }
    
    @Override
    public boolean getRIRFlag() {
        return this.rirFlag;
    }
    
    @Override
    public List getEndpointInfo() {
        return this.theEndpointInfo;
    }
    
    @Override
    public String getKeyString() {
        return this.theKeyString;
    }
    
    @Override
    public String getStringifiedName() {
        return this.theStringifiedName;
    }
    
    @Override
    public abstract boolean isCorbanameURL();
    
    @Override
    public void dPrint() {
        System.out.println("URL Dump...");
        System.out.println("Key String = " + this.getKeyString());
        System.out.println("RIR Flag = " + this.getRIRFlag());
        System.out.println("isCorbanameURL = " + this.isCorbanameURL());
        for (int i = 0; i < this.theEndpointInfo.size(); ++i) {
            ((IIOPEndpointInfo)this.theEndpointInfo.get(i)).dump();
        }
        if (this.isCorbanameURL()) {
            System.out.println("Stringified Name = " + this.getStringifiedName());
        }
    }
}
