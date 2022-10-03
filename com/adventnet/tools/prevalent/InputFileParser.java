package com.adventnet.tools.prevalent;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import org.xml.sax.helpers.DefaultHandler;

public class InputFileParser extends DefaultHandler
{
    private ArrayList usersMap;
    private ArrayList userNameList;
    private ArrayList licenseeMap;
    private User user;
    private Details details;
    private Component comp;
    private NSComponent nsComp;
    private DataClass data;
    private String licenseeDetailsID;
    private String cDATAID;
    private String propertyID;
    private String previousID;
    private String licenseString;
    private StringBuffer wholeBuffer;
    private Group group;
    private SubGroup subGroup;
    
    public InputFileParser(final String filePath) throws Exception {
        this.usersMap = null;
        this.userNameList = null;
        this.licenseeMap = null;
        this.user = null;
        this.details = null;
        this.comp = null;
        this.nsComp = null;
        this.data = null;
        this.licenseeDetailsID = null;
        this.cDATAID = null;
        this.propertyID = null;
        this.previousID = null;
        this.licenseString = null;
        this.wholeBuffer = null;
        this.group = null;
        this.subGroup = null;
        this.usersMap = new ArrayList();
        this.userNameList = new ArrayList();
        this.licenseeMap = new ArrayList();
        this.wholeBuffer = new StringBuffer();
        this.parse(filePath);
    }
    
    public DataClass getDataClass() {
        return new DataClass(this.usersMap, this.licenseeMap, this.userNameList, this.licenseString, this.wholeBuffer);
    }
    
    private void parse(final String str) throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        final SAXParser saxParser = factory.newSAXParser();
        final File ff = new File(str);
        if (ff.exists()) {
            saxParser.parse(new File(str), this);
        }
        else {
            if (!str.startsWith("http://")) {
                throw new Exception("License File " + str + " is invalid.");
            }
            final URL fileUrl = new URL(str);
            final HttpURLConnection confFileConnection = (HttpURLConnection)fileUrl.openConnection();
            final InputStream fis = confFileConnection.getInputStream();
            saxParser.parse(fis, this);
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String lName, final String qName, final Attributes at) throws SAXException {
        if (lName.equals("UserInfo")) {
            this.user = new User();
            final String name = at.getValue(at.getIndex("Name"));
            this.user.setName(name);
            this.usersMap.add(name);
            this.userNameList.add(name);
            this.usersMap.add(this.user);
            final String company = at.getValue(at.getIndex("CompanyName"));
            this.user.setCompanyName(company);
            final String emailId = at.getValue(at.getIndex("EmailID"));
            this.user.setMailId(emailId);
            final String macId = at.getValue(at.getIndex("ACNTRL"));
            this.user.setMacId(macId);
            final String date = at.getValue(at.getIndex("ExpiryDate"));
            this.user.setExpiryDate(date);
            final String days = at.getValue(at.getIndex("NoOfDays"));
            this.user.setNumberOfDays(days);
            final String type = at.getValue(at.getIndex("LicenseType"));
            this.user.setLicenseType(type);
            final int rtNumber = at.getIndex("NoOfRTLicense");
            String rtLicense = null;
            if (rtNumber != -1) {
                rtLicense = at.getValue(rtNumber);
                this.user.setNoOfRTLicense(rtLicense);
            }
            final int resIndex = at.getIndex("EmailRestrict");
            String restrict = null;
            if (resIndex != -1) {
                restrict = at.getValue(resIndex);
                this.user.setEmailRestrict(restrict);
            }
            final int allowed = at.getIndex("DownloadPerEmailID");
            String na = null;
            if (allowed != -1) {
                na = at.getValue(allowed);
                this.user.setDownloadPerEmailID(na);
            }
            final int relative = at.getIndex("IsExpiryRelative");
            String rel = null;
            if (relative != -1) {
                rel = at.getValue(relative);
                this.user.setExpiryRelative(rel);
            }
            final int genIndex = at.getIndex("GeneratedDate");
            String generatedDate = null;
            if (genIndex != -1) {
                generatedDate = at.getValue(genIndex);
                this.user.setGeneratedDate(generatedDate);
            }
            final String maxEvalPeriod = at.getValue(at.getIndex("MaxEvalPeriod"));
            if (maxEvalPeriod != null) {
                this.user.setMaxTrialPeriod(maxEvalPeriod);
            }
            final String isMacBased = at.getValue(at.getIndex("IsACNTRLPolicy"));
            if (isMacBased != null) {
                this.user.setTrialMACPolicy(isMacBased);
            }
            String key = at.getValue(at.getIndex("Key"));
            if (key == null) {
                key = Encode.getKey(name, company, emailId, macId, date, days, type, null, null, null, null, rtLicense, restrict, generatedDate, maxEvalPeriod, isMacBased, na, rel);
            }
            this.user.setKey(key);
        }
        else if (lName.equals("ID")) {
            this.cDATAID = "ID";
        }
        else if (lName.equals("LicenseeDetails")) {
            this.details = new Details();
            this.licenseeDetailsID = at.getValue(at.getIndex("ID"));
            this.licenseeMap.add(this.licenseeDetailsID);
            this.licenseeMap.add(this.details);
            this.wholeBuffer.append(this.licenseeDetailsID);
            this.details.setID(this.licenseeDetailsID);
        }
        else if (lName.equals("Product")) {
            final String name = at.getValue(at.getIndex("Name"));
            this.details.setProductName(name);
            this.wholeBuffer.append(name);
            final String version = at.getValue(at.getIndex("Version"));
            this.details.setProductVersion(version);
            this.wholeBuffer.append(version);
            final String type2 = at.getValue(at.getIndex("Type"));
            this.details.setProductLicenseType(type2);
            this.wholeBuffer.append(type2);
            final String category = at.getValue(at.getIndex("Category"));
            this.details.setProductCategory(category);
            this.wholeBuffer.append(category);
        }
        else if (lName.equals("Component")) {
            this.propertyID = "Component";
            this.comp = new Component();
            this.details.addComponent(this.comp);
            final String name = at.getValue(at.getIndex("Name"));
            this.comp.setName(name);
            this.wholeBuffer.append(name);
        }
        else if (lName.equals("NSComponent")) {
            this.propertyID = "NSComponent";
            this.nsComp = new NSComponent();
            this.details.addNSComponent(this.nsComp);
            final String name = at.getValue(at.getIndex("Name"));
            this.nsComp.setName(name);
        }
        else if (lName.equals("Properties")) {
            final String name = at.getValue(at.getIndex("Name"));
            final String value = at.getValue(at.getIndex("Value"));
            final int limit = at.getIndex("Limit");
            String limitValue = null;
            if (limit != -1) {
                limitValue = at.getValue(limit);
            }
            if (this.propertyID.equals("Group")) {
                this.subGroup.setProperty(name, value);
                this.wholeBuffer.append(name);
                this.wholeBuffer.append(value);
                if (limitValue != null) {
                    this.subGroup.setLimitProperty(name, limitValue);
                    this.wholeBuffer.append(limitValue);
                }
            }
            if (this.propertyID.equals("Component")) {
                this.comp.setProperty(name, value);
                this.wholeBuffer.append(name);
                this.wholeBuffer.append(value);
                if (limitValue != null) {
                    this.comp.setLimitProperty(name, limitValue);
                    this.wholeBuffer.append(limitValue);
                }
            }
            else if (this.propertyID.equals("NSComponent")) {
                this.nsComp.setProperty(name, value);
            }
        }
        else if (lName.equals("ComboGroup") || lName.equals("RadioGroup") || lName.equals("CheckBoxGroup")) {
            this.group = new Group(lName);
            final String name = at.getValue(at.getIndex("Name"));
            final String defaultGroup = at.getValue(at.getIndex("DefaultGroup"));
            this.wholeBuffer.append(name);
            this.wholeBuffer.append(defaultGroup);
            this.group.setName(name);
            this.group.setDefaultSubGroupName(defaultGroup);
            if (this.propertyID.equals("Component")) {
                this.previousID = "Component";
                this.comp.addGroup(this.group);
            }
            else if (this.propertyID.equals("NSComponent")) {
                this.previousID = "NSComponent";
                this.nsComp.addGroup(this.group);
            }
        }
        else if (lName.equals("Group")) {
            this.propertyID = "Group";
            final String name = at.getValue(at.getIndex("Name"));
            this.subGroup = new SubGroup();
            this.wholeBuffer.append(name);
            this.subGroup.setName(name);
            this.group.addSubGroup(this.subGroup);
        }
        else if (lName.equals("LicenseKey")) {
            this.cDATAID = "LicenseKey";
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        if (localName.equals("RadioGroup") || localName.equals("CheckBoxGroup") || localName.equals("ComboGroup")) {
            this.propertyID = this.previousID;
        }
    }
    
    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        final String id = new String(buf, offset, len);
        if (!id.trim().equals("") && !id.trim().equals("/>")) {
            if (this.cDATAID.equals("ID")) {
                this.user.addID(id);
                this.wholeBuffer.append(id);
            }
            else if (this.cDATAID.equals("LicenseKey")) {
                if (this.licenseString == null) {
                    this.licenseString = new String(id);
                }
                else {
                    this.licenseString = this.licenseString.concat(id);
                }
            }
        }
    }
}
