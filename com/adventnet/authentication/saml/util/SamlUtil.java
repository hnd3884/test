package com.adventnet.authentication.saml.util;

import java.util.Hashtable;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import javax.xml.bind.DatatypeConverter;
import java.io.StringWriter;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import java.util.ArrayList;
import javax.servlet.ServletException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataAccess;
import org.apache.commons.fileupload.FileItem;
import java.util.List;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.apache.xml.security.exceptions.XMLSecurityException;
import java.security.Key;
import org.apache.xml.security.transforms.Transforms;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.zip.Inflater;
import java.text.ParseException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Element;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.XMLUtils;
import javax.xml.parsers.DocumentBuilder;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Properties;
import org.w3c.dom.Document;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.IOException;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.security.KeyFactory;
import org.apache.xml.security.Init;
import java.security.PrivateKey;
import com.adventnet.authentication.saml.settings.SamlSettings;
import java.util.logging.Logger;

public class SamlUtil
{
    private static final Logger LOGGER;
    public static final String RESPONSE_SIGNATURE_XPATH = "/samlp:Response/ds:Signature";
    public static final String ASSERTION_SIGNATURE_XPATH = "/samlp:Response/saml:Assertion/ds:Signature";
    private static final String SAML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String SAML20_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static SamlSettings samlSettings;
    
    public static PrivateKey loadPrivateKey(String keyString) throws GeneralSecurityException {
        Init.init();
        keyString = formatPrivateKey(keyString);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final byte[] encoded = Base64.decodeBase64(keyString);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        final PrivateKey privKey = kf.generatePrivate(keySpec);
        return privKey;
    }
    
    private static String formatPrivateKey(final String key) {
        String xKey = "";
        if (key != null) {
            xKey = key.replace("\\x0D", "").replace("\r", "").replace("\\x0A", "").replace("\n", "").replace(" ", "");
            if (!xKey.isEmpty()) {
                if (xKey.startsWith("-----BEGINPRIVATEKEY-----")) {
                    xKey = xKey.replace("-----BEGINPRIVATEKEY-----", "").replace("-----ENDPRIVATEKEY-----", "");
                }
                else {
                    xKey = xKey.replace("-----BEGINRSAPRIVATEKEY-----", "").replace("-----ENDRSAPRIVATEKEY-----", "");
                }
            }
        }
        return xKey;
    }
    
    public static X509Certificate loadCert(String certString) throws CertificateException {
        SamlUtil.LOGGER.log(Level.FINE, "certSring before formatting :: " + certString);
        certString = formatCert(certString);
        SamlUtil.LOGGER.log(Level.FINE, "certSring after formatting :: " + certString);
        final X509Certificate cert = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(toBytesUtf8(certString)));
        return cert;
    }
    
    private static String formatCert(final String cert) {
        String x509cert = "";
        if (cert != null) {
            x509cert = cert.replace("\\x0D", "").replace("\r", "").replace("\\x0A", "").replace("\n", "").replace(" ", "");
            if (!x509cert.isEmpty()) {
                x509cert = x509cert.replace("-----BEGINCERTIFICATE-----", "").replace("-----ENDCERTIFICATE-----", "");
                x509cert = "-----BEGIN CERTIFICATE-----\n\r" + x509cert + "-----END CERTIFICATE-----";
            }
        }
        return x509cert;
    }
    
    public static String generateUniqueID() {
        return "ME_" + UUID.randomUUID();
    }
    
    public static String deflatedBase64encoded(final String input) throws IOException {
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        final Deflater deflater = new Deflater(8, true);
        final DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, deflater);
        deflaterStream.write(input.getBytes(Charset.forName("UTF-8")));
        deflaterStream.finish();
        return new String(Base64.encodeBase64(bytesOut.toByteArray()));
    }
    
    private static String base64encoder(final byte[] input) {
        return toStringUtf8(Base64.encodeBase64(input));
    }
    
    public static String base64encoder(final String input) {
        return base64encoder(toBytesUtf8(input));
    }
    
    public static String urlEncoder(final String input) {
        if (input != null) {
            try {
                return URLEncoder.encode(input, "UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                SamlUtil.LOGGER.log(Level.SEVERE, "URL encoder error :: ", e);
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
    
    private static String signatureAlgConversion(final String sign) {
        String convertedSignatureAlg = "";
        if (sign == null) {
            convertedSignatureAlg = "SHA1withRSA";
        }
        else if (sign.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
            convertedSignatureAlg = "SHA1withDSA";
        }
        else if (sign.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
            convertedSignatureAlg = "SHA256withRSA";
        }
        else if (sign.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")) {
            convertedSignatureAlg = "SHA384withRSA";
        }
        else if (sign.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {
            convertedSignatureAlg = "SHA512withRSA";
        }
        else {
            convertedSignatureAlg = "SHA1withRSA";
        }
        return convertedSignatureAlg;
    }
    
    public static void sendRedirect(final HttpServletResponse response, final String location, final Map<String, String> parameters) throws IOException {
        String target = location;
        if (!parameters.isEmpty()) {
            boolean first = !location.contains("?");
            for (final Map.Entry<String, String> parameter : parameters.entrySet()) {
                if (first) {
                    target += "?";
                    first = false;
                }
                else {
                    target += "&";
                }
                target += parameter.getKey();
                if (!parameter.getValue().isEmpty()) {
                    target = target + "=" + urlEncoder(parameter.getValue());
                }
            }
        }
        response.sendRedirect(target);
    }
    
    public static byte[] base64decoder(final String input) {
        return Base64.decodeBase64(toBytesUtf8(input));
    }
    
    private static byte[] toBytesUtf8(final String str) {
        try {
            return str.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static Document loadXML(final String xml) {
        try {
            if (xml.contains("<!ENTITY")) {
                throw new Exception("Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");
            }
            return convertStringToDocument(xml);
        }
        catch (final Exception e) {
            SamlUtil.LOGGER.log(Level.FINE, "Load XML error: " + e.getMessage(), e);
            return null;
        }
    }
    
    public static Document convertStringToDocument(final String samlString) {
        try {
            final Properties dbfFeatures = new Properties();
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/external-general-entities", false);
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/external-parameter-entities", false);
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/namespaces", true);
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://apache.org/xml/features/include-comments", false);
            final DocumentBuilder samlDocument = SecurityUtil.createDocumentBuilder(false, false, dbfFeatures);
            return samlDocument.parse(new ByteArrayInputStream(samlString.getBytes()));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception occurred while converting string to document :: " + ex.getMessage());
        }
    }
    
    public static String convertDocumentToString(final Document doc) {
        Init.init();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLUtils.outputDOM((Node)doc, (OutputStream)baos);
        return toStringUtf8(baos.toByteArray());
    }
    
    private static String toStringUtf8(final byte[] byteArray) {
        try {
            return new String(byteArray, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static NodeList query(final Document dom, final String query, final Node context) throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(final String prefix) {
                String result = null;
                if (prefix.equals("samlp") || prefix.equals("samlp2")) {
                    result = "urn:oasis:names:tc:SAML:2.0:protocol";
                }
                else if (prefix.equals("saml") || prefix.equals("saml2")) {
                    result = "urn:oasis:names:tc:SAML:2.0:assertion";
                }
                else if (prefix.equals("ds")) {
                    result = "http://www.w3.org/2000/09/xmldsig#";
                }
                else if (prefix.equals("xenc")) {
                    result = "http://www.w3.org/2001/04/xmlenc#";
                }
                else if (prefix.equals("md")) {
                    result = "urn:oasis:names:tc:SAML:2.0:metadata";
                }
                return result;
            }
            
            @Override
            public String getPrefix(final String namespaceURI) {
                return null;
            }
            
            @Override
            public Iterator getPrefixes(final String namespaceURI) {
                return null;
            }
        });
        NodeList nodeList;
        if (context == null) {
            nodeList = (NodeList)xpath.evaluate(query, dom, XPathConstants.NODESET);
        }
        else {
            nodeList = (NodeList)xpath.evaluate(query, context, XPathConstants.NODESET);
        }
        return nodeList;
    }
    
    public static boolean validateSign(final Document doc, final X509Certificate cert, final String xpath) {
        try {
            final NodeList signatures = query(doc, xpath, null);
            return signatures.getLength() == 1 && validateSignNode(signatures.item(0), cert);
        }
        catch (final XPathExpressionException e) {
            SamlUtil.LOGGER.log(Level.WARNING, "Failed to find signature nodes", e);
            return false;
        }
    }
    
    private static Boolean validateSignNode(final Node signNode, final X509Certificate cert) {
        Boolean res = false;
        try {
            Init.init();
            final Element sigElement = (Element)signNode;
            final XMLSignature signature = new XMLSignature(sigElement, "");
            if (cert != null) {
                res = signature.checkSignatureValue(cert);
            }
        }
        catch (final Exception e) {
            SamlUtil.LOGGER.log(Level.WARNING, "Error executing validateSignNode: " + e.getMessage(), e);
        }
        return res;
    }
    
    public static Date parseDate(final String source) throws ParseException {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(source);
        }
        catch (final ParseException pe) {
            final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf2.parse(source);
        }
    }
    
    public static String base64decodedInflated(final String input) {
        if (input.isEmpty()) {
            return input;
        }
        final byte[] decoded = Base64.decodeBase64(input);
        try {
            final Inflater decompresser = new Inflater(true);
            decompresser.setInput(decoded);
            final byte[] result = new byte[1024];
            String inflated = "";
            while (!decompresser.finished()) {
                final int resultLength = decompresser.inflate(result);
                inflated += new String(result, 0, resultLength, "UTF-8");
            }
            decompresser.end();
            return inflated;
        }
        catch (final Exception e) {
            return new String(decoded);
        }
    }
    
    public static Boolean validateBinarySignature(final String signedQuery, final byte[] signature, final X509Certificate cert, final String signAlg) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Boolean valid = false;
        try {
            Init.init();
            final String convertedSigAlg = signatureAlgConversion(signAlg);
            final Signature sig = Signature.getInstance(convertedSigAlg);
            sig.initVerify(cert.getPublicKey());
            sig.update(signedQuery.getBytes());
            valid = sig.verify(signature);
        }
        catch (final Exception e) {
            SamlUtil.LOGGER.log(Level.WARNING, "Error executing validateSign: " + e.getMessage(), e);
        }
        return valid;
    }
    
    public static String formatDateTime(final long timeInMillis) {
        final Date date = new Date(timeInMillis);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
    
    public static String addSign(final Document document, final PrivateKey key, final X509Certificate certificate, String signAlgorithm) throws XMLSecurityException, XPathExpressionException {
        Init.init();
        if (document == null) {
            throw new IllegalArgumentException("Provided document was null");
        }
        if (document.getDocumentElement() == null) {
            throw new IllegalArgumentException("The Xml Document has no root element.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Provided key was null");
        }
        if (certificate == null) {
            throw new IllegalArgumentException("Provided certificate was null");
        }
        if (signAlgorithm == null || signAlgorithm.isEmpty()) {
            signAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
        final String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        final XMLSignature sig = new XMLSignature(document, (String)null, signAlgorithm, c14nMethod);
        final Element root = document.getDocumentElement();
        document.setXmlStandalone(false);
        final NodeList issuerNodes = query(document, "//saml:Issuer", null);
        if (issuerNodes.getLength() > 0) {
            final Node issuer = issuerNodes.item(0);
            root.insertBefore(sig.getElement(), issuer.getNextSibling());
        }
        else {
            root.insertBefore(sig.getElement(), root.getFirstChild());
        }
        String reference;
        final String id = reference = root.getAttribute("ID");
        if (!id.isEmpty()) {
            root.setIdAttributeNS(null, "ID", true);
            reference = "#" + id;
        }
        final Transforms transforms = new Transforms(document);
        transforms.addTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.addTransform(c14nMethod);
        sig.addDocument(reference, transforms, "http://www.w3.org/2000/09/xmldsig#sha1");
        sig.addKeyInfo(certificate);
        sig.sign((Key)key);
        return convertDocumentToString(document);
    }
    
    public static void loadSamlProperties() throws Exception {
        final Properties props = new Properties();
        loadSPSettings(props);
        loadIDPSettings(props);
        SamlUtil.samlSettings = new SamlSettings(props);
    }
    
    private static void loadIDPSettings(final Properties props) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SAMLIDP"));
        sq.addSelectColumn(Column.getColumn("SAMLIDP", "*"));
        sq.setCriteria(new Criteria(new Column("SAMLIDP", "ENABLED"), (Object)true, 0));
        final DataObject dob = ((ReadOnlyPersistence)BeanUtil.lookup("PureCachedPersistence")).get(sq);
        if (dob.isEmpty()) {
            throw new Exception("No IDP is enabled ");
        }
        final Row spRow = dob.getFirstRow("SAMLIDP");
        props.setProperty("saml.idp.x509cert", (String)spRow.get("CERTIFICATE"));
        props.setProperty("saml.idp.single_logout_service.url", (String)((spRow.get("SLO_URL") != null) ? spRow.get("SLO_URL") : ""));
        props.setProperty("saml.idp.single_sign_on_service.url", (String)spRow.get("SSO_URL"));
        props.setProperty("saml.idp.single_logout_service.response.url", (String)((spRow.get("SLO_RESPONSE_URL") != null) ? spRow.get("SLO_RESPONSE_URL") : ""));
        props.setProperty("saml.security.signature_algorithm", (String)spRow.get("ALGORITHM"));
        props.setProperty("saml.security.logoutresponse_signed", Boolean.toString((boolean)spRow.get("SIGNED")));
        props.setProperty("saml.security.logoutrequest_signed", Boolean.toString((boolean)spRow.get("SIGNED")));
        SamlUtil.LOGGER.log(Level.FINE, "idp props in util :: " + props);
    }
    
    private static void loadSPSettings(final Properties props) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SAMLSP"));
        sq.addSelectColumn(Column.getColumn("SAMLSP", "*"));
        final DataObject dob = ((ReadOnlyPersistence)BeanUtil.lookup("PureCachedPersistence")).get(sq);
        if (dob.isEmpty()) {
            throw new Exception("SP settings are not present");
        }
        final Row spRow = dob.getFirstRow("SAMLSP");
        props.setProperty("saml.sp.entityid", (String)spRow.get("ENTITY_ID"));
        props.setProperty("saml.sp.assertion_consumer_service.url", (String)spRow.get("ACS_URL"));
        if (spRow.get("CERTIFICATE") != null) {
            props.setProperty("saml.sp.x509cert", (String)spRow.get("CERTIFICATE"));
        }
        if (spRow.get("PRIVATE_KEY") != null) {
            props.setProperty("saml.sp.privatekey", (String)spRow.get("PRIVATE_KEY"));
        }
        if (spRow.get("SLS_URL") != null) {
            props.setProperty("saml.sp.single_logout_service.url", (String)spRow.get("SLS_URL"));
        }
        props.setProperty("saml.sp.nameidformat", (String)spRow.get("NAME_ID_FORMAT"));
    }
    
    public static void addIDP(final HttpServletRequest request) throws Exception {
        final Row idpRow = new Row("SAMLIDP");
        final Map<String, List<FileItem>> map = new HashMap<String, List<FileItem>>();
        fillRow(request, false, idpRow, map);
        final DataObject dob = DataAccess.constructDataObject();
        dob.addRow(idpRow);
        ((Persistence)BeanUtil.lookup("Persistence")).add(dob);
    }
    
    public static void updateIDP(final HttpServletRequest request) throws Exception {
        Map<String, List<FileItem>> map = new HashMap<String, List<FileItem>>();
        map = getFormDataInMap(request);
        Row idpRow = new Row("SAMLIDP");
        idpRow.set("ID", (Object)map.get("ID").get(0).getString());
        final DataObject dob = ((Persistence)BeanUtil.lookup("Persistence")).get("SAMLIDP", idpRow);
        idpRow = dob.getFirstRow("SAMLIDP");
        fillRow(request, true, idpRow, map);
        dob.updateRow(idpRow);
        ((Persistence)BeanUtil.lookup("Persistence")).update(dob);
        if (idpRow.get("ENABLED")) {
            loadSamlProperties();
        }
    }
    
    private static Map<String, List<FileItem>> getFormDataInMap(final HttpServletRequest request) throws FileUploadException {
        final DiskFileItemFactory dfi = new DiskFileItemFactory();
        dfi.setSizeThreshold(10000000);
        final ServletFileUpload sfu = new ServletFileUpload((FileItemFactory)dfi);
        final Map<String, List<FileItem>> map = sfu.parseParameterMap(request);
        return map;
    }
    
    private static void fillRow(final HttpServletRequest request, final boolean update, final Row idpRow, Map<String, List<FileItem>> map) throws Exception {
        InputStream certFileContents = null;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        boolean isValidCert = false;
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new ServletException("Content type is not multipart/form-data");
        }
        final List<String> idpKeys = new ArrayList<String>();
        idpKeys.add("ALGORITHM");
        idpKeys.add("SLO_RESPONSE_URL");
        idpKeys.add("SLO_URL");
        idpKeys.add("SSO_URL");
        idpKeys.add("ID");
        idpKeys.add("SIGNED");
        String certificateKey = null;
        if (update) {
            certificateKey = "CERTIFICATE_UPD";
        }
        else {
            certificateKey = "CERTIFICATE";
            map = getFormDataInMap(request);
        }
        idpKeys.add(certificateKey);
        for (final String key : idpKeys) {
            if (map.get(key) == null) {
                continue;
            }
            if (key.equals(certificateKey)) {
                certFileContents = map.get(key).get(0).getInputStream();
                if (certFileContents == null) {
                    throw new ServletException("Exception while validating certificate");
                }
                final int available = certFileContents.available();
                if (available <= 0) {
                    continue;
                }
                final byte[] b = new byte[available];
                certFileContents.read(b);
                final ByteArrayInputStream bis = new ByteArrayInputStream(b);
                isValidCert = false;
                try {
                    bis.mark(bis.available());
                    isValidCert = isValidCertificate(bis);
                    if (!isValidCert) {
                        throw new ServletException("certificate uploaded is invalid");
                    }
                    bis.reset();
                    result = new ByteArrayOutputStream();
                    IOUtils.copy((InputStream)bis, (OutputStream)result);
                    String certificate = result.toString("UTF-8");
                    if (!certificate.contains("BEGIN CERTIFICATE")) {
                        bis.reset();
                        certificate = ConvertDER_to_PEM(bis);
                    }
                    idpRow.set("CERTIFICATE", (Object)certificate);
                }
                finally {
                    if (certFileContents != null) {
                        certFileContents.close();
                        bis.close();
                    }
                }
            }
            else {
                idpRow.set(key, (Object)map.get(key).get(0).getString());
                if (!key.equalsIgnoreCase("SSO_URL")) {
                    continue;
                }
                final URI uri = new URI((String)idpRow.get("SSO_URL"));
                idpRow.set("DOMAIN", (Object)uri.getHost());
            }
        }
    }
    
    private static boolean isValidCertificate(final InputStream is) {
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cf.generateCertificate(is);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void deleteIDPdata(final String[] ids) throws Exception {
        deleteIDP(ids);
    }
    
    public static void deleteIDPdata(final Long[] ids) throws Exception {
        deleteIDP(ids);
    }
    
    private static void deleteIDP(final Object[] ids) throws Exception {
        Criteria allCr = null;
        for (int i = 0; i < ids.length; ++i) {
            final Criteria cr = new Criteria(new Column("SAMLIDP", "ID"), ids[i], 0);
            allCr = ((allCr == null) ? cr : allCr.or(cr));
            allCr = allCr.and(new Criteria(new Column("SAMLIDP", "ENABLED"), (Object)true, 0));
        }
        final DataObject dob = ((ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence")).get("SAMLIDP", allCr);
        if (!dob.isEmpty()) {
            throw new Exception("Currently enabled IDENTITY PROVIDER properties cannot be deleted");
        }
        allCr = null;
        for (int j = 0; j < ids.length; ++j) {
            final Criteria cr2 = new Criteria(new Column("SAMLIDP", "ID"), ids[j], 0);
            allCr = ((allCr == null) ? cr2 : allCr.or(cr2));
        }
        ((Persistence)BeanUtil.lookup("Persistence")).delete(allCr);
    }
    
    public static SamlSettings getSamlSettings() throws Exception {
        if (SamlUtil.samlSettings != null && SamlUtil.samlSettings.arePropsLoaded()) {
            return SamlUtil.samlSettings;
        }
        loadSamlProperties();
        return SamlUtil.samlSettings;
    }
    
    public static void enableIDP(final HttpServletRequest request) throws Exception {
        DataObject dob = ((Persistence)BeanUtil.lookup("Persistence")).get("SAMLIDP", (Criteria)null);
        dob.set("SAMLIDP", "ENABLED", (Object)false);
        final Row row = dob.getRow("SAMLIDP", new Criteria(new Column("SAMLIDP", "ID"), (Object)Long.valueOf(request.getParameter("ID")), 0));
        row.set("ENABLED", (Object)true);
        dob.updateRow(row);
        ((Persistence)BeanUtil.lookup("Persistence")).update(dob);
        dob = ((Persistence)BeanUtil.lookup("Persistence")).get("AUTHCONFIG", (Criteria)null);
        dob.set("AUTHCONFIG", "AUTH_MODE", (Object)"SAML");
        ((Persistence)BeanUtil.lookup("Persistence")).update(dob);
        loadSamlProperties();
    }
    
    public static void disableSAML(final HttpServletRequest request) throws Exception {
        DataObject dob = ((Persistence)BeanUtil.lookup("Persistence")).get("SAMLIDP", (Criteria)null);
        dob.set("SAMLIDP", "ENABLED", (Object)false);
        ((Persistence)BeanUtil.lookup("Persistence")).update(dob);
        dob = ((Persistence)BeanUtil.lookup("Persistence")).get("AUTHCONFIG", (Criteria)null);
        dob.set("AUTHCONFIG", "AUTH_MODE", (Object)"GENERAL");
        ((Persistence)BeanUtil.lookup("Persistence")).update(dob);
        request.getSession(false).setAttribute("saml", (Object)"false");
    }
    
    public static boolean isSamlEnabled() throws Exception {
        final DataObject dob = ((ReadOnlyPersistence)BeanUtil.lookup("PureCachedPersistence")).get("AUTHCONFIG", (Criteria)null);
        return !dob.isEmpty() && ((String)dob.getFirstValue("AUTHCONFIG", "AUTH_MODE")).equals("SAML");
    }
    
    private static String ConvertDER_to_PEM(final ByteArrayInputStream bis) throws Exception {
        bis.mark(bis.available());
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final Certificate c = cf.generateCertificate(bis);
        final StringWriter sw = new StringWriter();
        try {
            sw.write("-----BEGIN CERTIFICATE-----\n");
            sw.write(DatatypeConverter.printBase64Binary(c.getEncoded()).replaceAll("(.{64})", "$1\n"));
            sw.write("\n-----END CERTIFICATE-----\n");
        }
        catch (final CertificateEncodingException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
    
    public static String getContextPath(final HttpServletRequest request) {
        return (request.getContextPath() != "") ? request.getContextPath() : "/";
    }
    
    static {
        LOGGER = Logger.getLogger(SamlUtil.class.getName());
        SamlUtil.samlSettings = null;
    }
}
