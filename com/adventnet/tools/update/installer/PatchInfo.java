package com.adventnet.tools.update.installer;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class PatchInfo implements Externalizable
{
    private static final long serialVersionUID = 4652469667967483948L;
    private String fileName;
    private String productVersion;
    private String checkSum;
    
    public PatchInfo() {
    }
    
    public PatchInfo(final String fileName, final String productVersion) {
        this.fileName = fileName;
        this.productVersion = productVersion;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getProductVersion() {
        return this.productVersion;
    }
    
    public String getCheckSum() {
        return this.checkSum;
    }
    
    public void setCheckSum(final String checkSum) {
        this.checkSum = checkSum;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.fileName);
        out.writeObject(this.productVersion);
        if (this.checkSum != null) {
            out.writeObject(this.checkSum);
        }
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.fileName = (String)in.readObject();
        this.productVersion = (String)in.readObject();
        if (in.available() >= 0) {
            this.checkSum = (String)in.readObject();
        }
    }
}
