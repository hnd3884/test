package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.util.URI;
import java.io.UnsupportedEncodingException;
import java.io.File;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import java.util.Map;

public class XMLEntityStorage
{
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected boolean fWarnDuplicateEntityDef;
    protected Map<String, Entity> fEntities;
    protected Entity.ScannedEntity fCurrentEntity;
    private XMLEntityManager fEntityManager;
    protected XMLErrorReporter fErrorReporter;
    protected PropertyManager fPropertyManager;
    protected boolean fInExternalSubset;
    private static String gUserDir;
    private static String gEscapedUserDir;
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;
    
    public XMLEntityStorage(final PropertyManager propertyManager) {
        this.fEntities = new HashMap<String, Entity>();
        this.fInExternalSubset = false;
        this.fPropertyManager = propertyManager;
    }
    
    public XMLEntityStorage(final XMLEntityManager entityManager) {
        this.fEntities = new HashMap<String, Entity>();
        this.fInExternalSubset = false;
        this.fEntityManager = entityManager;
    }
    
    public void reset(final PropertyManager propertyManager) {
        this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntities.clear();
        this.fCurrentEntity = null;
    }
    
    public void reset() {
        this.fEntities.clear();
        this.fCurrentEntity = null;
    }
    
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        this.fWarnDuplicateEntityDef = componentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntities.clear();
        this.fCurrentEntity = null;
    }
    
    public Entity getEntity(final String name) {
        return this.fEntities.get(name);
    }
    
    public boolean hasEntities() {
        return this.fEntities != null;
    }
    
    public int getEntitySize() {
        return this.fEntities.size();
    }
    
    public Enumeration getEntityKeys() {
        return Collections.enumeration(this.fEntities.keySet());
    }
    
    public void addInternalEntity(final String name, final String text) {
        if (!this.fEntities.containsKey(name)) {
            final Entity entity = new Entity.InternalEntity(name, text, this.fInExternalSubset);
            this.fEntities.put(name, entity);
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
        }
    }
    
    public void addExternalEntity(final String name, final String publicId, final String literalSystemId, String baseSystemId) {
        if (!this.fEntities.containsKey(name)) {
            if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
                baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
            }
            this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
            final Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandSystemId(literalSystemId, baseSystemId)), null, this.fInExternalSubset);
            this.fEntities.put(name, entity);
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
        }
    }
    
    public boolean isExternalEntity(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null && entity.isExternal();
    }
    
    public boolean isEntityDeclInExternalSubset(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null && entity.isEntityDeclInExternalSubset();
    }
    
    public void addUnparsedEntity(final String name, final String publicId, final String systemId, final String baseSystemId, final String notation) {
        this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
        if (!this.fEntities.containsKey(name)) {
            final Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, systemId, baseSystemId, null), notation, this.fInExternalSubset);
            this.fEntities.put(name, entity);
        }
        else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
        }
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null && entity.isUnparsed();
    }
    
    public boolean isDeclaredEntity(final String entityName) {
        final Entity entity = this.fEntities.get(entityName);
        return entity != null;
    }
    
    public static String expandSystemId(final String systemId) {
        return expandSystemId(systemId, null);
    }
    
    private static synchronized String getUserDir() {
        String userDir = "";
        try {
            userDir = SecuritySupport.getSystemProperty("user.dir");
        }
        catch (final SecurityException ex) {}
        if (userDir.length() == 0) {
            return "";
        }
        if (userDir.equals(XMLEntityStorage.gUserDir)) {
            return XMLEntityStorage.gEscapedUserDir;
        }
        XMLEntityStorage.gUserDir = userDir;
        final char separator = File.separatorChar;
        userDir = userDir.replace(separator, '/');
        final int len = userDir.length();
        final StringBuffer buffer = new StringBuffer(len * 3);
        if (len >= 2 && userDir.charAt(1) == ':') {
            final int ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 65 && ch <= 90) {
                buffer.append('/');
            }
        }
        int i;
        for (i = 0; i < len; ++i) {
            final int ch = userDir.charAt(i);
            if (ch >= 128) {
                break;
            }
            if (XMLEntityStorage.gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(XMLEntityStorage.gAfterEscaping1[ch]);
                buffer.append(XMLEntityStorage.gAfterEscaping2[ch]);
            }
            else {
                buffer.append((char)ch);
            }
        }
        if (i < len) {
            byte[] bytes = null;
            try {
                bytes = userDir.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                return userDir;
            }
            for (final byte b : bytes) {
                if (b < 0) {
                    final int ch = b + 256;
                    buffer.append('%');
                    buffer.append(XMLEntityStorage.gHexChs[ch >> 4]);
                    buffer.append(XMLEntityStorage.gHexChs[ch & 0xF]);
                }
                else if (XMLEntityStorage.gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(XMLEntityStorage.gAfterEscaping1[b]);
                    buffer.append(XMLEntityStorage.gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }
        if (!userDir.endsWith("/")) {
            buffer.append('/');
        }
        return XMLEntityStorage.gEscapedUserDir = buffer.toString();
    }
    
    public static String expandSystemId(final String systemId, final String baseSystemId) {
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        try {
            new URI(systemId);
            return systemId;
        }
        catch (final URI.MalformedURIException ex) {
            final String id = fixURI(systemId);
            URI base = null;
            URI uri = null;
            try {
                if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId.equals(systemId)) {
                    final String dir = getUserDir();
                    base = new URI("file", "", dir, null, null);
                }
                else {
                    try {
                        base = new URI(fixURI(baseSystemId));
                    }
                    catch (final URI.MalformedURIException e) {
                        if (baseSystemId.indexOf(58) != -1) {
                            base = new URI("file", "", fixURI(baseSystemId), null, null);
                        }
                        else {
                            String dir2 = getUserDir();
                            dir2 += fixURI(baseSystemId);
                            base = new URI("file", "", dir2, null, null);
                        }
                    }
                }
                uri = new URI(base, id);
            }
            catch (final Exception ex2) {}
            if (uri == null) {
                return systemId;
            }
            return uri.toString();
        }
    }
    
    protected static String fixURI(String str) {
        str = str.replace(File.separatorChar, '/');
        if (str.length() >= 2) {
            final char ch1 = str.charAt(1);
            if (ch1 == ':') {
                final char ch2 = Character.toUpperCase(str.charAt(0));
                if (ch2 >= 'A' && ch2 <= 'Z') {
                    str = "/" + str;
                }
            }
            else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        return str;
    }
    
    public void startExternalSubset() {
        this.fInExternalSubset = true;
    }
    
    public void endExternalSubset() {
        this.fInExternalSubset = false;
    }
    
    static {
        XMLEntityStorage.gNeedEscaping = new boolean[128];
        XMLEntityStorage.gAfterEscaping1 = new char[128];
        XMLEntityStorage.gAfterEscaping2 = new char[128];
        XMLEntityStorage.gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (int i = 0; i <= 31; ++i) {
            XMLEntityStorage.gNeedEscaping[i] = true;
            XMLEntityStorage.gAfterEscaping1[i] = XMLEntityStorage.gHexChs[i >> 4];
            XMLEntityStorage.gAfterEscaping2[i] = XMLEntityStorage.gHexChs[i & 0xF];
        }
        XMLEntityStorage.gNeedEscaping[127] = true;
        XMLEntityStorage.gAfterEscaping1[127] = '7';
        XMLEntityStorage.gAfterEscaping2[127] = 'F';
        for (final char ch : new char[] { ' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' }) {
            XMLEntityStorage.gNeedEscaping[ch] = true;
            XMLEntityStorage.gAfterEscaping1[ch] = XMLEntityStorage.gHexChs[ch >> 4];
            XMLEntityStorage.gAfterEscaping2[ch] = XMLEntityStorage.gHexChs[ch & '\u000f'];
        }
    }
}
