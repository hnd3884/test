package com.adventnet.sym.server.mdm;

import org.w3c.dom.Document;
import com.dd.plist.XMLPropertyListParser;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.util.Set;
import java.util.HashSet;
import com.dd.plist.NSString;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.util.SecurityManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.EntityResolver;
import com.dd.plist.Base64;
import com.dd.plist.NSData;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSDictionary;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlistWrapper
{
    public Logger logger;
    private static PlistWrapper plistWrapper;
    
    public PlistWrapper() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static PlistWrapper getInstance() {
        if (PlistWrapper.plistWrapper == null) {
            PlistWrapper.plistWrapper = new PlistWrapper();
        }
        return PlistWrapper.plistWrapper;
    }
    
    public String getValueForKeyString(final String strKey, final String strPlist) {
        String keyValue = null;
        try {
            this.logger.log(Level.FINE, "getValueForKeyString(): key value{0}", strKey);
            this.logger.log(Level.FINE, "getValueForKeyString(): input plist string{0}", strPlist);
            final NSDictionary rootDict = (NSDictionary)this.parsePropertyList(strPlist);
            keyValue = rootDict.objectForKey(strKey).toString();
            this.logger.log(Level.FINE, "keyValue: {0}", keyValue);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getValueForKey(): {0}", ex);
        }
        return keyValue;
    }
    
    public String getValueForKeyBytes(final String strKey, final byte[] bytePlist) {
        String keyValue = null;
        try {
            final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(bytePlist);
            keyValue = rootDict.objectForKey(strKey).toString();
            this.logger.log(Level.FINE, "keyValue: {0}", keyValue);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getValueForKey(): {0}", ex);
        }
        return keyValue;
    }
    
    public String getValueForKeyIOStream(final String strKey, final InputStream streamPlist) {
        String keyValue = null;
        try {
            final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(streamPlist);
            keyValue = rootDict.objectForKey(strKey).toString();
            this.logger.log(Level.FINE, "keyValue: {0}", keyValue);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getValueForKey(): {0}", ex);
        }
        return keyValue;
    }
    
    public String getValueForKeyData(final String strKey, final String strPlist) {
        String keyValue = null;
        try {
            this.logger.log(Level.FINE, "getValueForKeyData(): key value{0}", strKey);
            this.logger.log(Level.FINE, "getValueForKeyData(): input plist string{0}", strPlist);
            final NSDictionary rootDict = (NSDictionary)this.parsePropertyList(strPlist);
            keyValue = ((NSData)rootDict.objectForKey(strKey)).getBase64EncodedData().toString();
            this.logger.log(Level.FINE, "keyValue: {0}", keyValue);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getValueForKey(): {0}", ex.toString());
            this.logger.log(Level.INFO, "getValueForKeyData() key not found: key, plist: {0} , {1}", new Object[] { strKey, strPlist });
        }
        return keyValue;
    }
    
    public String getEmptyDict() {
        String strValue = null;
        try {
            final NSDictionary root = new NSDictionary();
            strValue = root.toXMLPropertyList().toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getEmptyDict(): {0}", ex);
        }
        return strValue;
    }
    
    public String getDecodedBase64HexValue(final String encodedString) {
        String hexString = null;
        try {
            this.logger.log(Level.FINE, "getDecodedBase64HexValue(): input encoded string{0}", encodedString);
            final byte[] bytes = Base64.decode(encodedString);
            hexString = BytesToHex(bytes);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getDecodedBase64HexValue(): {0}", ex);
        }
        return hexString;
    }
    
    public static String BytesToHex(final byte[] buf) {
        final StringBuilder strbuf = new StringBuilder(buf.length * 2);
        for (int i = 0; i < buf.length; ++i) {
            if ((buf[i] & 0xFF) < 16) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString(buf[i] & 0xFF, 16));
        }
        return strbuf.toString();
    }
    
    private DocumentBuilder getDocumentBuilderInstance(final boolean isValidating, final boolean isNamespaceAware, final EntityResolver saxHandler) throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(isValidating);
        factory.setNamespaceAware(isNamespaceAware);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", saxHandler == null);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        final int billionLaugh = 10;
        final SecurityManager manager = new SecurityManager();
        manager.setEntityExpansionLimit(billionLaugh);
        factory.setAttribute("http://apache.org/xml/properties/security-manager", manager);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        if (saxHandler != null) {
            builder.setEntityResolver(saxHandler);
            builder.setErrorHandler((ErrorHandler)saxHandler);
        }
        return builder;
    }
    
    public HashMap getHashFromPlist(final String strPlist) {
        final HashMap hsmap = new HashMap();
        try {
            this.logger.log(Level.FINE, "getHashFromPlist(): input plist string{0}", strPlist);
            NSDictionary rootDict = new NSDictionary();
            rootDict = (NSDictionary)this.parsePropertyList(strPlist);
            for (int i = 0; i < rootDict.allKeys().length; ++i) {
                final String key = rootDict.allKeys()[i];
                final Object valueObj = rootDict.objectForKey(key);
                String value = null;
                if (valueObj instanceof NSDictionary) {
                    value = ((NSDictionary)valueObj).toXMLPropertyList();
                }
                else if (valueObj instanceof NSArray) {
                    value = ((NSArray)valueObj).toXMLPropertyList();
                }
                else if (valueObj instanceof NSData) {
                    value = ((NSData)valueObj).getBase64EncodedData();
                }
                else {
                    value = valueObj.toString();
                }
                hsmap.put(key, value);
                this.logger.log(Level.FINE, "getHashFromPlist: key : {0} Value : {1}", new Object[] { key, value });
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getHashFromPlist(): {0}", ex);
        }
        return hsmap;
    }
    
    public HashMap getHashFromDict(final NSDictionary nsDict) {
        this.logger.log(Level.INFO, "Inside getHashFromPlist()");
        final HashMap hsmap = new HashMap();
        try {
            for (int i = 0; i < nsDict.allKeys().length; ++i) {
                final String key = nsDict.allKeys()[i];
                final NSObject nsObj = nsDict.objectForKey(nsDict.allKeys()[i]);
                String value = "";
                if (nsObj instanceof NSData) {
                    final NSData nsData = (NSData)nsObj;
                    value = nsData.getBase64EncodedData();
                    hsmap.put(key, value);
                }
                else if (nsObj instanceof NSDictionary) {
                    final NSDictionary innerDict = (NSDictionary)nsObj;
                    hsmap.put(key, this.getHashFromDict(innerDict));
                }
                else {
                    value = nsObj.toString();
                    hsmap.put(key, value);
                }
                this.logger.log(Level.FINE, "getHashFromDict: key : {0} Value : {1}", new Object[] { key, value });
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getHashFromDict(): {0}", ex);
        }
        return hsmap;
    }
    
    public NSDictionary getDictForKey(final String strKey, final String strPlist) {
        NSDictionary subDict = null;
        try {
            this.logger.log(Level.FINE, "getDictForKey(): input plist string{0}", strPlist);
            this.logger.log(Level.FINE, "getDictForKey(): input keyValue{0}", strKey);
            final NSDictionary rootDict = (NSDictionary)this.parsePropertyList(strPlist);
            subDict = (NSDictionary)rootDict.objectForKey(strKey);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getDictForKey(): {0}", ex);
        }
        return subDict;
    }
    
    public NSArray getArrayForKey(final String strKey, final String strPlist) {
        NSArray nsArr = null;
        try {
            this.logger.log(Level.FINE, "getArrayForKey(): input plist string{0}", strPlist);
            this.logger.log(Level.FINE, "getArrayForKey(): input keyValue{0}", strKey);
            final NSDictionary rootDict = (NSDictionary)this.parsePropertyList(strPlist);
            nsArr = (NSArray)rootDict.objectForKey(strKey);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred getArrayForKey(): {0}", ex);
        }
        return nsArr;
    }
    
    public NSObject replaceNSDataTypeRecursively(final NSObject nsObject) {
        try {
            if (!(nsObject instanceof NSDictionary) && !(nsObject instanceof NSArray)) {
                this.logger.log(Level.FINE, "replaceNSDataTypeRecursively() no dict or array! ");
                return (NSObject)new NSDictionary();
            }
            NSObject returnObject;
            if (nsObject instanceof NSDictionary) {
                returnObject = (NSObject)new NSDictionary();
                this.logger.log(Level.FINE, "replaceNSDataTypeRecursively() parsing Dictionary! {0}", nsObject.toXMLPropertyList());
                final String[] allKeys;
                final String[] keys = allKeys = ((NSDictionary)nsObject).allKeys();
                for (final String key : allKeys) {
                    final NSObject existingValue = ((NSDictionary)nsObject).get((Object)key);
                    if (!(existingValue instanceof NSDictionary) && !(existingValue instanceof NSArray)) {
                        if (existingValue instanceof NSData) {
                            final int byteSize = ((NSData)existingValue).bytes().length;
                            ((NSDictionary)returnObject).put(key, (NSObject)new NSString("This is hidden data bytes of size " + byteSize));
                        }
                        else {
                            ((NSDictionary)returnObject).put(key, existingValue);
                        }
                    }
                    else {
                        ((NSDictionary)returnObject).put(key, this.replaceNSDataTypeRecursively(existingValue));
                    }
                }
            }
            else {
                this.logger.log(Level.FINE, "replaceNSDataTypeRecursively() parsing Array! {0}", nsObject.toXMLPropertyList());
                returnObject = (NSObject)new NSArray(((NSArray)nsObject).count());
                for (int i = 0; i < ((NSArray)nsObject).count(); ++i) {
                    final NSObject existingValue2 = ((NSArray)nsObject).objectAtIndex(i);
                    if (!(existingValue2 instanceof NSDictionary) && !(existingValue2 instanceof NSArray)) {
                        if (existingValue2 instanceof NSData) {
                            final int byteSize2 = ((NSData)existingValue2).bytes().length;
                            ((NSArray)returnObject).setValue(i, (Object)new NSString("This is hidden data bytes of size " + byteSize2));
                        }
                        else {
                            ((NSArray)returnObject).setValue(i, (Object)existingValue2);
                        }
                    }
                    else {
                        ((NSArray)returnObject).setValue(i, (Object)this.replaceNSDataTypeRecursively(existingValue2));
                    }
                }
            }
            return returnObject;
        }
        catch (final Throwable t) {
            this.logger.log(Level.FINE, "PlistWrapper replaceNSDataTypeRecursively() some error occured. See trace! ", t);
            this.logger.log(Level.SEVERE, "PlistWrapper replaceNSDataTypeRecursively() some error occured. See trace in FINE logs! Message = ", t.getMessage());
            if (nsObject instanceof NSDictionary) {
                return (NSObject)new NSDictionary();
            }
            return (NSObject)new NSArray(new NSObject[0]);
        }
    }
    
    public NSObject replaceDictionaryData(final NSObject nsObject, final HashSet<String> keysToBeReplaced, final String replaceBy) {
        try {
            final String ENCAPIKEY = "encapikey";
            if (!(nsObject instanceof NSDictionary) && !(nsObject instanceof NSArray)) {
                this.logger.log(Level.FINE, "replaceSensitiveData() no dict or array! ");
                return (NSObject)new NSDictionary();
            }
            NSObject returnObject;
            if (nsObject instanceof NSDictionary) {
                returnObject = (NSObject)new NSDictionary();
                this.logger.log(Level.FINE, "replaceSensitiveData() parsing Dictionary! {0}", nsObject.toXMLPropertyList());
                final String[] allKeys;
                final String[] keys = allKeys = ((NSDictionary)nsObject).allKeys();
                for (final String key : allKeys) {
                    NSObject existingValue = ((NSDictionary)nsObject).get((Object)key);
                    if (!(existingValue instanceof NSDictionary) && !(existingValue instanceof NSArray)) {
                        if (contains(key, keysToBeReplaced)) {
                            ((NSDictionary)returnObject).put(key, (Object)replaceBy);
                        }
                        else {
                            if (existingValue != null) {
                                final String string = existingValue.toString();
                                if (!string.trim().equals("") && string.toLowerCase().contains("encapikey")) {
                                    existingValue = (NSObject)new NSString(DMSecurityLogger.restrictPasswordEntry(string, "encapikey"));
                                }
                            }
                            ((NSDictionary)returnObject).put(key, existingValue);
                        }
                    }
                    else {
                        ((NSDictionary)returnObject).put(key, this.replaceDictionaryData(existingValue, keysToBeReplaced, replaceBy));
                    }
                }
            }
            else {
                this.logger.log(Level.FINE, "replaceSensitiveData() parsing Array! {0}", nsObject.toXMLPropertyList());
                returnObject = (NSObject)new NSArray(((NSArray)nsObject).count());
                for (int i = 0; i < ((NSArray)nsObject).count(); ++i) {
                    final NSObject existingValue2 = ((NSArray)nsObject).objectAtIndex(i);
                    if (existingValue2 instanceof NSDictionary || existingValue2 instanceof NSArray) {
                        ((NSArray)returnObject).setValue(i, (Object)this.replaceDictionaryData(existingValue2, keysToBeReplaced, replaceBy));
                    }
                    else {
                        ((NSArray)returnObject).setValue(i, (Object)existingValue2);
                    }
                }
            }
            return returnObject;
        }
        catch (final Throwable t) {
            this.logger.log(Level.FINE, "PlistWrapper replaceSensitiveData() some error occured. See trace! ", t);
            this.logger.log(Level.SEVERE, "PlistWrapper replaceSensitiveData() some error occured. See trace in FINE logs! Message = ", t.getMessage());
            if (nsObject instanceof NSDictionary) {
                return (NSObject)new NSDictionary();
            }
            return (NSObject)new NSArray(new NSObject[0]);
        }
    }
    
    public NSObject replaceDictionaryData(final NSObject nsObject, final HashMap<String, String> keyValuesToBeReplaced) {
        try {
            if (!(nsObject instanceof NSDictionary) && !(nsObject instanceof NSArray)) {
                this.logger.log(Level.FINE, "replaceSensitiveData() no dict or array! ");
                return (NSObject)new NSDictionary();
            }
            NSObject returnObject;
            if (nsObject instanceof NSDictionary) {
                returnObject = (NSObject)new NSDictionary();
                this.logger.log(Level.FINE, "replaceSensitiveData() parsing Dictionary! {0}", nsObject.toXMLPropertyList());
                final String[] allKeys;
                final String[] keys = allKeys = ((NSDictionary)nsObject).allKeys();
                for (final String key : allKeys) {
                    final NSObject existingValue = ((NSDictionary)nsObject).get((Object)key);
                    if (!(existingValue instanceof NSDictionary) && !(existingValue instanceof NSArray)) {
                        if (contains(key, keyValuesToBeReplaced.keySet())) {
                            ((NSDictionary)returnObject).put(key, (Object)keyValuesToBeReplaced.get(key));
                        }
                        else {
                            ((NSDictionary)returnObject).put(key, existingValue);
                        }
                    }
                    else {
                        ((NSDictionary)returnObject).put(key, this.replaceDictionaryData(existingValue, keyValuesToBeReplaced));
                    }
                }
            }
            else {
                this.logger.log(Level.FINE, "replaceSensitiveData() parsing Array! {0}", nsObject.toXMLPropertyList());
                returnObject = (NSObject)new NSArray(((NSArray)nsObject).count());
                for (int i = 0; i < ((NSArray)nsObject).count(); ++i) {
                    final NSObject existingValue2 = ((NSArray)nsObject).objectAtIndex(i);
                    if (existingValue2 instanceof NSDictionary || existingValue2 instanceof NSArray) {
                        ((NSArray)returnObject).setValue(i, (Object)this.replaceDictionaryData(existingValue2, keyValuesToBeReplaced));
                    }
                }
            }
            return returnObject;
        }
        catch (final Throwable t) {
            this.logger.log(Level.FINE, "PlistWrapper replaceSensitiveData() some error occured. See trace! ", t);
            this.logger.log(Level.SEVERE, "PlistWrapper replaceSensitiveData() some error occured. See trace in FINE logs! Message = ", t.getMessage());
            if (nsObject instanceof NSDictionary) {
                return (NSObject)new NSDictionary();
            }
            return (NSObject)new NSArray(new NSObject[0]);
        }
    }
    
    private static boolean contains(final String key, final Set hashSet) {
        boolean containsStatus = false;
        if (hashSet.contains(key)) {
            containsStatus = true;
        }
        else {
            final Iterator itr = hashSet.iterator();
            String value = "";
            while (itr.hasNext()) {
                value = itr.next().toString();
                if (key.toLowerCase().contains(value)) {
                    containsStatus = true;
                    break;
                }
            }
        }
        return containsStatus;
    }
    
    public static NSArray convertListToNSArray(final List<NSDictionary> dicts) {
        final int count = dicts.size();
        final NSArray array = new NSArray(count);
        final Iterator<NSDictionary> iterator = dicts.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            final NSDictionary dict = iterator.next();
            array.setValue(index++, (Object)dict);
        }
        return array;
    }
    
    private NSObject parsePropertyList(String strPlist) throws Exception {
        try {
            this.logger.log(Level.INFO, "Going to parsePropertyList(): ");
            return DMSecurityUtil.parsePropertyList(strPlist.getBytes("UTF-8"));
        }
        catch (final IllegalArgumentException iaExc) {
            this.logger.log(Level.WARNING, "Exception Occurred parsePropertyList(): IllegalArgumentException..");
            if (strPlist.contains("ManagedApplicationFeedback") && strPlist.contains("infinity")) {
                this.logger.log(Level.INFO, "Going to remove infinity and try again..");
                strPlist = strPlist.replaceAll("infinity", "0");
                return DMSecurityUtil.parsePropertyList(strPlist.getBytes("UTF-8"));
            }
            this.logger.log(Level.INFO, "parsePropertyList(): Does not contain: ManagedApplicationFeedback && infinity. Throwing exception..");
            throw iaExc;
        }
        catch (final ArrayIndexOutOfBoundsException aiExc) {
            this.logger.log(Level.WARNING, "Exception Occurred parsePropertyList(): ArrayIndexOutOfBounds. So going to try again..");
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(strPlist.getBytes("UTF-8"));
            inputStream.reset();
            final InputSource inputSource = new InputSource(new InputStreamReader(inputStream, "UTF-8"));
            final DocumentBuilder documentBuilder = this.getDocumentBuilderInstance(true, false, new DefaultMDMSaxHandler());
            final Document doc = documentBuilder.parse(inputSource);
            return XMLPropertyListParser.parse(doc);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred parsePropertyList():", ex);
            throw ex;
        }
    }
    
    static {
        PlistWrapper.plistWrapper = null;
    }
}
