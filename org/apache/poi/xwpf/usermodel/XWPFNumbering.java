package org.apache.poi.xwpf.usermodel;

import org.apache.xmlbeans.XmlObject;
import java.util.Iterator;
import java.math.BigInteger;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import java.io.IOException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.NumberingDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XWPFNumbering extends POIXMLDocumentPart
{
    protected List<XWPFAbstractNum> abstractNums;
    protected List<XWPFNum> nums;
    boolean isNew;
    private CTNumbering ctNumbering;
    
    public XWPFNumbering(final PackagePart part) {
        super(part);
        this.abstractNums = new ArrayList<XWPFAbstractNum>();
        this.nums = new ArrayList<XWPFNum>();
        this.isNew = true;
    }
    
    public XWPFNumbering() {
        this.abstractNums = new ArrayList<XWPFAbstractNum>();
        this.nums = new ArrayList<XWPFNum>();
        this.abstractNums = new ArrayList<XWPFAbstractNum>();
        this.nums = new ArrayList<XWPFNum>();
        this.isNew = true;
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        NumberingDocument numberingDoc = null;
        final InputStream is = this.getPackagePart().getInputStream();
        try {
            numberingDoc = NumberingDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctNumbering = numberingDoc.getNumbering();
            for (final CTNum ctNum : this.ctNumbering.getNumArray()) {
                this.nums.add(new XWPFNum(ctNum, this));
            }
            for (final CTAbstractNum ctAbstractNum : this.ctNumbering.getAbstractNumArray()) {
                this.abstractNums.add(new XWPFAbstractNum(ctAbstractNum, this));
            }
            this.isNew = false;
        }
        catch (final XmlException e) {
            throw new POIXMLException();
        }
        finally {
            is.close();
        }
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTNumbering.type.getName().getNamespaceURI(), "numbering"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.ctNumbering.save(out, xmlOptions);
        out.close();
    }
    
    public void setNumbering(final CTNumbering numbering) {
        this.ctNumbering = numbering;
    }
    
    public boolean numExist(final BigInteger numID) {
        for (final XWPFNum num : this.nums) {
            if (num.getCTNum().getNumId().equals(numID)) {
                return true;
            }
        }
        return false;
    }
    
    public BigInteger addNum(final XWPFNum num) {
        this.ctNumbering.addNewNum();
        final int pos = this.ctNumbering.sizeOfNumArray() - 1;
        this.ctNumbering.setNumArray(pos, num.getCTNum());
        this.nums.add(num);
        return num.getCTNum().getNumId();
    }
    
    public BigInteger addNum(final BigInteger abstractNumID) {
        final CTNum ctNum = this.ctNumbering.addNewNum();
        ctNum.addNewAbstractNumId();
        ctNum.getAbstractNumId().setVal(abstractNumID);
        ctNum.setNumId(BigInteger.valueOf(this.nums.size() + 1));
        final XWPFNum num = new XWPFNum(ctNum, this);
        this.nums.add(num);
        return ctNum.getNumId();
    }
    
    public void addNum(final BigInteger abstractNumID, final BigInteger numID) {
        final CTNum ctNum = this.ctNumbering.addNewNum();
        ctNum.addNewAbstractNumId();
        ctNum.getAbstractNumId().setVal(abstractNumID);
        ctNum.setNumId(numID);
        final XWPFNum num = new XWPFNum(ctNum, this);
        this.nums.add(num);
    }
    
    public XWPFNum getNum(final BigInteger numID) {
        for (final XWPFNum num : this.nums) {
            if (num.getCTNum().getNumId().equals(numID)) {
                return num;
            }
        }
        return null;
    }
    
    public XWPFAbstractNum getAbstractNum(final BigInteger abstractNumID) {
        for (final XWPFAbstractNum abstractNum : this.abstractNums) {
            if (abstractNum.getAbstractNum().getAbstractNumId().equals(abstractNumID)) {
                return abstractNum;
            }
        }
        return null;
    }
    
    public BigInteger getIdOfAbstractNum(final XWPFAbstractNum abstractNum) {
        final CTAbstractNum copy = (CTAbstractNum)abstractNum.getCTAbstractNum().copy();
        final XWPFAbstractNum newAbstractNum = new XWPFAbstractNum(copy, this);
        for (int i = 0; i < this.abstractNums.size(); ++i) {
            newAbstractNum.getCTAbstractNum().setAbstractNumId(BigInteger.valueOf(i));
            newAbstractNum.setNumbering(this);
            if (newAbstractNum.getCTAbstractNum().valueEquals((XmlObject)this.abstractNums.get(i).getCTAbstractNum())) {
                return newAbstractNum.getCTAbstractNum().getAbstractNumId();
            }
        }
        return null;
    }
    
    public BigInteger addAbstractNum(final XWPFAbstractNum abstractNum) {
        final int pos = this.abstractNums.size();
        if (abstractNum.getAbstractNum() != null) {
            this.ctNumbering.addNewAbstractNum().set((XmlObject)abstractNum.getAbstractNum());
        }
        else {
            this.ctNumbering.addNewAbstractNum();
            abstractNum.getAbstractNum().setAbstractNumId(BigInteger.valueOf(pos));
            this.ctNumbering.setAbstractNumArray(pos, abstractNum.getAbstractNum());
        }
        this.abstractNums.add(abstractNum);
        return abstractNum.getCTAbstractNum().getAbstractNumId();
    }
    
    public boolean removeAbstractNum(final BigInteger abstractNumID) {
        for (final XWPFAbstractNum abstractNum : this.abstractNums) {
            final BigInteger foundNumId = abstractNum.getAbstractNum().getAbstractNumId();
            if (abstractNumID.equals(foundNumId)) {
                this.ctNumbering.removeAbstractNum((int)foundNumId.byteValue());
                this.abstractNums.remove(abstractNum);
                return true;
            }
        }
        return false;
    }
    
    public BigInteger getAbstractNumID(final BigInteger numID) {
        final XWPFNum num = this.getNum(numID);
        if (num == null) {
            return null;
        }
        if (num.getCTNum() == null) {
            return null;
        }
        if (num.getCTNum().getAbstractNumId() == null) {
            return null;
        }
        return num.getCTNum().getAbstractNumId().getVal();
    }
}
