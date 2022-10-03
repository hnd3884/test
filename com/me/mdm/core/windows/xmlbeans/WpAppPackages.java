package com.me.mdm.core.windows.xmlbeans;

import java.util.Iterator;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Packages")
public class WpAppPackages
{
    private ArrayList<WpAppPackageDetails> listOfPackages;
    
    @XmlElement(name = "Package")
    public void setListOfPackages(final ArrayList<WpAppPackageDetails> listOfPackages) {
        this.listOfPackages = listOfPackages;
    }
    
    public ArrayList<WpAppPackageDetails> getListOfPackages() {
        if (this.listOfPackages == null) {
            this.listOfPackages = new ArrayList<WpAppPackageDetails>();
        }
        return this.listOfPackages;
    }
    
    @Override
    public String toString() {
        String packageDetails = "";
        for (final WpAppPackageDetails pack : this.listOfPackages) {
            packageDetails = packageDetails + pack.toString() + "\n";
        }
        return packageDetails;
    }
}
