package com.adventnet.tools.prevalent;

import java.util.ArrayList;

public class DataClass
{
    private ArrayList usersMap;
    private ArrayList userNameList;
    private ArrayList licenseeMap;
    private ArrayList pCategoryArr;
    private ArrayList pTypeArr;
    private StringBuffer wholeBuffer;
    private String licFileKey;
    private int userSize;
    private int detailSize;
    
    public DataClass(final ArrayList users, final ArrayList licDetails, final ArrayList userNameList, final String key, final StringBuffer buf) {
        this.usersMap = null;
        this.userNameList = null;
        this.licenseeMap = null;
        this.pCategoryArr = null;
        this.pTypeArr = null;
        this.wholeBuffer = null;
        this.licFileKey = null;
        this.userSize = 0;
        this.detailSize = 0;
        this.usersMap = users;
        this.userNameList = userNameList;
        this.licenseeMap = licDetails;
        this.licFileKey = key;
        this.wholeBuffer = buf;
        this.userSize = this.usersMap.size();
        this.detailSize = this.licenseeMap.size();
        this.pCategoryArr = new ArrayList();
        this.pTypeArr = new ArrayList();
    }
    
    public StringBuffer getWholeKeyBuffer() {
        return this.wholeBuffer;
    }
    
    public ArrayList getUsers() {
        return this.usersMap;
    }
    
    public ArrayList getUserList() {
        return this.userNameList;
    }
    
    public ArrayList getDetails() {
        return this.licenseeMap;
    }
    
    public User getUserObject(final String userName) {
        final int index = this.usersMap.indexOf(userName);
        if (index == -1) {
            return null;
        }
        return this.usersMap.get(index + 1);
    }
    
    public Details getDetails(final String iD) {
        final int detailObjIndex = this.licenseeMap.indexOf(iD);
        if (detailObjIndex == -1) {
            return null;
        }
        return this.licenseeMap.get(detailObjIndex + 1);
    }
    
    public Details getDetail(final String product, final String type, final String category) {
        for (int size = this.licenseeMap.size(), i = 1; i < size; i += 2) {
            final Details detail = this.licenseeMap.get(i);
            final String prod = detail.getProductName();
            final String prodType = detail.getProductLicenseType();
            final String categ = detail.getProductCategory();
            if (prod.equals(product) && type.equals(prodType) && categ.equals(category)) {
                return detail;
            }
        }
        return null;
    }
    
    public ArrayList getProductCategoryArray() {
        for (int size = this.licenseeMap.size(), i = 1; i < size; i += 2) {
            final Details detail = this.licenseeMap.get(i);
            final String categ = detail.getProductCategory();
            if (!this.pCategoryArr.contains(categ)) {
                this.pCategoryArr.add(categ);
            }
        }
        return this.pCategoryArr;
    }
    
    public ArrayList getProductTypeArray() {
        for (int size = this.licenseeMap.size(), i = 1; i < size; i += 2) {
            final Details detail = this.licenseeMap.get(i);
            final String type = detail.getProductLicenseType();
            if (!this.pTypeArr.contains(type)) {
                this.pTypeArr.add(type);
            }
        }
        return this.pTypeArr;
    }
    
    public String getLicenseFileKey() {
        return this.licFileKey;
    }
    
    @Override
    public String toString() {
        for (int size = this.licenseeMap.size(), i = 1; i < size; i += 2) {
            final Details detail = this.licenseeMap.get(i);
            final String prod = detail.getProductName();
            final String prodType = detail.getProductLicenseType();
            final String categ = detail.getProductCategory();
            System.out.println("\n\n***Licensee Details***: " + detail);
        }
        return null;
    }
}
