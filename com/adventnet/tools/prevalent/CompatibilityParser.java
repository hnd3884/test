package com.adventnet.tools.prevalent;

import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.ContentHandler;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import org.xml.sax.helpers.DefaultHandler;

public class CompatibilityParser extends DefaultHandler
{
    private HashMap productMap;
    private HashMap versionMap;
    private int[] productName;
    private int[] productVersion;
    private int[] showBackward;
    private int[] bundleNativeFiles;
    private int[] isTrialMacBased;
    private int[] allowFreeAfterExpiry;
    private int[] oneTimeStandardEval;
    private int[] isAgreementHide;
    private int[] mandatoryMacLicense;
    private Product product;
    private Version version;
    private Component comp;
    private boolean validate;
    
    public CompatibilityParser(final String filePath) throws Exception {
        this.productMap = null;
        this.versionMap = null;
        this.productName = null;
        this.productVersion = null;
        this.showBackward = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.product = null;
        this.version = null;
        this.comp = null;
        this.validate = true;
        this.productMap = new HashMap();
        this.versionMap = new HashMap();
        this.parse(filePath);
    }
    
    BObject getObject() {
        return new BObject(this.productName, this.productVersion, this.showBackward, this.bundleNativeFiles, this.isTrialMacBased, this.allowFreeAfterExpiry, this.oneTimeStandardEval, this.versionMap, this.productMap, this.isAgreementHide, this.mandatoryMacLicense);
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
        XMLReader reader = null;
        reader = saxParser.getXMLReader();
        reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        reader.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String arg0, final String arg1) throws SAXException, IOException {
                return null;
            }
        });
        reader.setContentHandler(this);
        reader.parse(str);
    }
    
    @Override
    public void startElement(final String namespaceURI, final String lName, final String qName, final Attributes at) throws SAXException {
        if (lName.equals("BackwardSupport")) {
            final String pName = at.getValue(at.getIndex("ProductName"));
            this.productName = Encode.shiftBytes(pName);
            final String pVersion = at.getValue(at.getIndex("ProductVersion"));
            this.productVersion = Encode.shiftBytes(pVersion);
            final String backward = at.getValue(at.getIndex("ShowBackward"));
            if (backward != null) {
                this.showBackward = Encode.shiftBytes(backward);
            }
            final String bundleNative = at.getValue(at.getIndex("BundleNativeFiles"));
            if (bundleNative != null) {
                this.bundleNativeFiles = Encode.shiftBytes(bundleNative);
            }
            final String macTrial = at.getValue(at.getIndex("MacBasedTrial"));
            if (macTrial != null) {
                this.isTrialMacBased = Encode.shiftBytes(macTrial);
            }
            final String allowFree = at.getValue(at.getIndex("AllowFreeAfterExpiry"));
            if (allowFree != null) {
                this.allowFreeAfterExpiry = Encode.shiftBytes(allowFree);
            }
            final String oneTimeEval = at.getValue(at.getIndex("OneTimeStandardEvaluation"));
            if (oneTimeEval != null) {
                this.oneTimeStandardEval = Encode.shiftBytes(oneTimeEval);
            }
            final String agreementHide = at.getValue(at.getIndex("isAgreementHide"));
            if (agreementHide != null) {
                this.isAgreementHide = Encode.shiftBytes(agreementHide);
            }
            final String macbasedLicense = at.getValue(at.getIndex("mandatoryMacLicense"));
            if (macbasedLicense != null) {
                this.mandatoryMacLicense = Encode.shiftBytes(macbasedLicense);
            }
        }
        else if (lName.equals("OlderVersion")) {
            this.version = new Version();
            final String ver = at.getValue(at.getIndex("Version"));
            this.version.setVersion(Encode.shiftBytes(ver));
            final String type = at.getValue(at.getIndex("Type"));
            this.version.setType(Encode.shiftBytes(type));
            final String id = at.getValue(at.getIndex("ID"));
            this.version.setID(Encode.shiftBytes(id));
            final String key = ver + "_" + type;
            this.versionMap.put(key, this.version);
        }
        else if (lName.equals("Product")) {
            this.product = new Product();
            final String id2 = at.getValue(at.getIndex("ID"));
            this.product.setID(Encode.shiftBytes(id2));
            final String type = at.getValue(at.getIndex("Type"));
            this.product.setProductLicenseType(Encode.shiftBytes(type));
            final String category = at.getValue(at.getIndex("Category"));
            this.product.setProductCategory(Encode.shiftBytes(category));
            this.productMap.put(id2, this.product);
        }
        else if (lName.equals("Component")) {
            this.comp = new Component();
            this.product.addComponent(this.comp);
            final String name = at.getValue(at.getIndex("Name"));
            this.comp.setName(Encode.swap(name));
        }
        else if (lName.equals("Properties")) {
            final String name = at.getValue(at.getIndex("Name"));
            final String value = at.getValue(at.getIndex("Value"));
            this.comp.setProperty(name, value);
        }
    }
    
    private void test() {
        final BObject ob = this.getObject();
        System.out.println(" getSupportedProduct :" + ob.getSupportedProduct("2.2.1", "Carrier"));
        System.out.println(" getProductName() :" + new String(Encode.revShiftBytes(ob.getProductName())));
        System.out.println(" getProductVersion() :" + new String(Encode.revShiftBytes(ob.getProductVersion())));
        System.out.println(" getBackwardState() :" + new String(Encode.revShiftBytes(ob.getBackwardState())));
        System.out.println(" getNativeFilesState() :" + new String(Encode.revShiftBytes(ob.getNativeFilesState())));
    }
    
    public static void main(final String[] ar) throws Exception {
        final CompatibilityParser co = new CompatibilityParser(ar[0]);
        co.test();
    }
    
    class MyErrorHandler implements ErrorHandler
    {
        @Override
        public void error(final SAXParseException exception) {
            this.printError(exception, "Parse Error Occurred ");
        }
        
        @Override
        public void fatalError(final SAXParseException exception) {
            this.printError(exception, "Fatal error Occurred ");
        }
        
        @Override
        public void warning(final SAXParseException exception) {
            this.printError(exception, "Warning ");
        }
        
        private void printError(final SAXParseException ex, final String message) {
            System.err.println(message + " \"" + ex.getMessage() + "\"");
            System.err.println("at line " + ex.getLineNumber() + " : column " + ex.getColumnNumber());
        }
    }
}
