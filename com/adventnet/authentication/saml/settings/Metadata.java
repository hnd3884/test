package com.adventnet.authentication.saml.settings;

import org.apache.commons.codec.binary.Base64;
import java.security.cert.X509Certificate;
import java.net.URL;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import com.adventnet.authentication.saml.util.SamlUtil;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.authentication.saml.SamlException;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class Metadata
{
    private final String metadataString;
    private static final Logger LOGGER;
    private static Properties spProps;
    private static String metadataTemplate;
    
    public Metadata() throws Exception {
        loadSPSettings();
        final Map<String, String> valueMap = this.generateSubstitutor();
        String unsignedMetadataString = Metadata.metadataTemplate;
        for (final String key : valueMap.keySet()) {
            unsignedMetadataString = unsignedMetadataString.replaceFirst(key, valueMap.get(key));
        }
        Metadata.LOGGER.log(Level.FINE, "metadata --> " + unsignedMetadataString);
        this.metadataString = unsignedMetadataString;
    }
    
    private static void loadSPSettings() throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SAMLSP"));
        sq.addSelectColumn(Column.getColumn("SAMLSP", "*"));
        final DataObject dob = ((ReadOnlyPersistence)BeanUtil.lookup("PureCachedPersistence")).get(sq);
        if (dob.isEmpty()) {
            throw new SamlException("SP settings are not present");
        }
        final Row spRow = dob.getFirstRow("SAMLSP");
        Metadata.spProps.setProperty("saml.sp.entityid", (String)spRow.get("ENTITY_ID"));
        Metadata.spProps.setProperty("saml.sp.assertion_consumer_service.url", (String)spRow.get("ACS_URL"));
        Metadata.spProps.setProperty("saml.sp.x509cert", (spRow.get("CERTIFICATE") != null) ? ((String)spRow.get("CERTIFICATE")) : null);
        Metadata.spProps.setProperty("saml.sp.privatekey", (spRow.get("PRIVATE_KEY") != null) ? ((String)spRow.get("PRIVATE_KEY")) : null);
        Metadata.spProps.setProperty("saml.sp.single_logout_service.url", (String)spRow.get("SLS_URL"));
        Metadata.spProps.setProperty("saml.sp.nameidformat", (String)spRow.get("NAME_ID_FORMAT"));
    }
    
    private Map<String, String> generateSubstitutor() throws CertificateEncodingException, IOException {
        final SamlSettings settings = new SamlSettings(Metadata.spProps);
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("id", SamlUtil.generateUniqueID());
        valueMap.put("spEntityId", settings.getSpEntityId());
        valueMap.put("strAuthnsign", String.valueOf(settings.getAuthnRequestsSigned()));
        valueMap.put("spNameIDFormat", settings.getSpNameIDFormat());
        valueMap.put("spAssertionConsumerServiceBinding", settings.getSpAssertionConsumerServiceBinding());
        valueMap.put("spAssertionConsumerServiceUrl", settings.getSpAssertionConsumerServiceUrl().toString());
        valueMap.put("sls", this.toSLSXml(settings.getSpSingleLogoutServiceUrl(), settings.getSpSingleLogoutServiceBinding()));
        valueMap.put("strKeyDescriptor", this.toX509KeyDescriptorsXML(settings.getSPcert()));
        return valueMap;
    }
    
    private String toSLSXml(final URL spSingleLogoutServiceUrl, final String spSingleLogoutServiceBinding) {
        final StringBuilder slsXml = new StringBuilder();
        if (spSingleLogoutServiceUrl != null) {
            slsXml.append("<md:SingleLogoutService Binding=\"" + spSingleLogoutServiceBinding + "\"");
            slsXml.append(" Location=\"" + spSingleLogoutServiceUrl.toString() + "\"/>");
        }
        return slsXml.toString();
    }
    
    private String toX509KeyDescriptorsXML(final X509Certificate cert) throws CertificateEncodingException {
        final StringBuilder keyDescriptorXml = new StringBuilder();
        if (cert != null) {
            final Base64 encoder = new Base64(64);
            final byte[] encodedCert = cert.getEncoded();
            final String certString = new String(encoder.encode(encodedCert));
            keyDescriptorXml.append("<md:KeyDescriptor use=\"signing\">");
            keyDescriptorXml.append("<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">");
            keyDescriptorXml.append("<ds:X509Data>");
            keyDescriptorXml.append("<ds:X509Certificate>" + certString + "</ds:X509Certificate>");
            keyDescriptorXml.append("</ds:X509Data>");
            keyDescriptorXml.append("</ds:KeyInfo>");
            keyDescriptorXml.append("</md:KeyDescriptor>");
        }
        return keyDescriptorXml.toString();
    }
    
    public final String getMetadataString() {
        return this.metadataString;
    }
    
    static {
        LOGGER = Logger.getLogger(Metadata.class.getName());
        Metadata.spProps = new Properties();
        final StringBuilder template = new StringBuilder();
        template.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        template.append("<md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\"");
        template.append(" entityID=\"spEntityId\"");
        template.append(" ID=\"id\">");
        template.append("<md:SPSSODescriptor AuthnRequestsSigned=\"strAuthnsign\" WantAssertionsSigned=\"true\" protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\">");
        template.append("strKeyDescriptor");
        template.append("sls<md:NameIDFormat>spNameIDFormat</md:NameIDFormat>");
        template.append("<md:AssertionConsumerService Binding=\"spAssertionConsumerServiceBinding\"");
        template.append(" Location=\"spAssertionConsumerServiceUrl\"");
        template.append(" index=\"1\"/>");
        template.append("</md:SPSSODescriptor>");
        template.append("</md:EntityDescriptor>");
        Metadata.metadataTemplate = template.toString();
    }
}
