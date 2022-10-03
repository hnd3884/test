package com.lowagie.text.pdf;

import java.util.ArrayList;
import java.util.List;

public class PdfTextArray
{
    private List<Object> arrayList;
    private String lastStr;
    private Float lastNum;
    
    public PdfTextArray(final String str) {
        this.arrayList = new ArrayList<Object>();
        this.add(str);
    }
    
    public PdfTextArray() {
        this.arrayList = new ArrayList<Object>();
    }
    
    public void add(final PdfNumber number) {
        this.add((float)number.doubleValue());
    }
    
    public void add(final float number) {
        if (number != 0.0f) {
            if (this.lastNum != null) {
                this.lastNum += number;
                if (this.lastNum != 0.0f) {
                    this.replaceLast(this.lastNum);
                }
                else {
                    this.arrayList.remove(this.arrayList.size() - 1);
                }
            }
            else {
                this.lastNum = number;
                this.arrayList.add(this.lastNum);
            }
            this.lastStr = null;
        }
    }
    
    public void add(final String str) {
        if (str.length() > 0) {
            if (this.lastStr != null) {
                this.replaceLast(this.lastStr += str);
            }
            else {
                this.lastStr = str;
                this.arrayList.add(this.lastStr);
            }
            this.lastNum = null;
        }
    }
    
    List getArrayList() {
        return this.arrayList;
    }
    
    private void replaceLast(final Object obj) {
        this.arrayList.set(this.arrayList.size() - 1, obj);
    }
}
