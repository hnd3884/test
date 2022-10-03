package com.adventnet.tools.prevalent;

import java.util.Properties;
import java.util.ArrayList;

public final class RTObject
{
    private RTObject runtimeObj;
    private InputFileParser parser;
    private DataClass data;
    private Details details;
    private ArrayList components;
    private boolean rtuser;
    private String userType;
    
    public RTObject(final String filePath, final boolean flag) {
        this.runtimeObj = null;
        this.parser = null;
        this.data = null;
        this.details = null;
        this.components = null;
        this.rtuser = false;
        this.userType = null;
        this.doValidation(filePath, flag);
    }
    
    private void doValidation(final String licenseFile, final boolean mode) {
        InputFileParser parser = null;
        ArrayList ID = null;
        int map = -1;
        try {
            parser = new InputFileParser(licenseFile);
        }
        catch (final Exception exp) {
            this.showError("ERROR CODE : 478", mode, 478);
            return;
        }
        final DataClass data = parser.getDataClass();
        final ArrayList users = data.getUserList();
        for (int noofUsers = users.size(), k = 0; k < noofUsers; ++k) {
            final String userName = users.get(k);
            final User user = data.getUserObject(userName);
            final String company = user.getCompanyName();
            final String mailID = user.getMailId();
            final String macID = user.getMacId();
            final String expiryDate = user.getExpiryDate();
            final String evalDays = user.getNumberOfDays();
            this.userType = user.getLicenseType();
            final String userKey = user.getKey();
            ID = user.getIDs();
            final String returnKey = Encode.getKey(userName, company, mailID, macID, expiryDate, evalDays, this.userType, null, null, null, null);
            if (!userKey.equals(returnKey)) {
                this.showError("ERROR CODE : 477", mode, 477);
                return;
            }
        }
        final String wholeKey = data.getLicenseFileKey();
        final String encodedKey = Encode.getFinalKey(data.getWholeKeyBuffer());
        if (!wholeKey.equals(encodedKey)) {
            this.showError("ERROR CODE : 476", mode, 476);
            return;
        }
        try {
            final Indication indicate = Indication.getInstance();
            indicate.deSerialize();
            map = indicate.getProductName();
            indicate.productNameDeSerialize();
            final int prod = indicate.getProductNameInt();
            final String regCheck = indicate.getTheRegCheck();
            if (map != prod) {
                this.showError("ERROR CODE : 475", mode, 475);
                return;
            }
        }
        catch (final Exception exp2) {
            this.showError("ERROR CODE : 474", mode, 474);
            return;
        }
        for (int idSize = ID.size(), j = 0; j < idSize; ++j) {
            final String mapID = ID.get(j);
            final Object obj = data.getDetails(mapID);
            if (obj == null) {
                this.showError("ERROR CODE : 473", mode, 473);
                return;
            }
            this.details = (Details)obj;
            this.components = this.details.getComponents();
        }
        final String prdName = this.getProductName();
        int value = Laterality.getMapValue(prdName);
        if (value == -1) {
            value = Laterality.getHashCode(prdName);
        }
        if (map != value) {
            this.showError("ERROR CODE : 472", mode, 472);
            return;
        }
        this.rtuser = true;
    }
    
    public String getProductName() {
        return this.details.getProductName();
    }
    
    public String getProductVersion() {
        return this.details.getProductVersion();
    }
    
    public int getType() {
        return LUtil.getNewType(this.details.getProductLicenseType());
    }
    
    public String getTypeString() {
        return this.details.getProductLicenseType();
    }
    
    public int getProductCategory() {
        return LUtil.getNewCategory(this.details.getProductCategory());
    }
    
    public String getUserType() {
        if (this.userType.equals("Runtime")) {
            return "RT";
        }
        return null;
    }
    
    public String getProductCategoryString() {
        return this.details.getProductCategory();
    }
    
    public boolean isModulePresent(final String moduleName) {
        return this.details.isComponentPresent(moduleName);
    }
    
    public Properties getModuleProperties(final String moduleName) {
        return this.details.getComponentProperties(moduleName);
    }
    
    private void showError(final String code, final boolean mode, final int codInt) {
        final String invalidFile = "Invalid License File";
        final String contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        if (mode) {
            LUtil.showError(code, invalidFile, contactMesg, "Error", codInt);
        }
        else {
            LUtil.showCMDError(code, invalidFile, contactMesg, codInt);
        }
    }
    
    public boolean isBare() {
        return this.rtuser;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(" Product Name :" + this.getProductName());
        buf.append(" Product Version:" + this.getProductVersion());
        buf.append(" Product License Type:" + this.getType());
        buf.append(" Product Category Type:" + this.getProductCategory());
        final ArrayList list = this.details.getComponents();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Component comp = list.get(i);
            buf.append("Componet of Licensee Details :" + comp);
        }
        return buf.toString();
    }
}
