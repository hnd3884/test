package org.apache.poi.poifs.crypt.dsig.facets;

import org.apache.poi.util.POILogFactory;
import org.etsi.uri.x01903.v13.OCSPValuesType;
import org.etsi.uri.x01903.v13.CRLValuesType;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import java.util.UUID;
import java.io.IOException;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Extension;
import java.math.BigInteger;
import org.apache.xml.security.c14n.Canonicalizer;
import java.io.ByteArrayOutputStream;
import org.etsi.uri.x01903.v13.RevocationValuesType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.cert.ocsp.RespID;
import org.etsi.uri.x01903.v13.ResponderIDType;
import org.etsi.uri.x01903.v13.OCSPIdentifierType;
import org.etsi.uri.x01903.v13.OCSPRefType;
import org.etsi.uri.x01903.v13.OCSPRefsType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.CRLIdentifierType;
import org.etsi.uri.x01903.v13.CRLRefType;
import org.etsi.uri.x01903.v13.CRLRefsType;
import org.etsi.uri.x01903.v13.CompleteRevocationRefsType;
import org.etsi.uri.x01903.v13.CertIDType;
import java.util.Iterator;
import java.util.List;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.etsi.uri.x01903.v13.CompleteCertificateRefsType;
import org.etsi.uri.x01903.v14.ValidationDataType;
import org.etsi.uri.x01903.v13.XAdESTimeStampType;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.UnsignedPropertiesType;
import org.w3c.dom.NodeList;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import java.security.cert.CertificateEncodingException;
import org.w3c.dom.Node;
import java.util.ArrayList;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.security.cert.CRLException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.apache.xmlbeans.XmlObject;
import java.util.Collections;
import org.apache.poi.poifs.crypt.dsig.services.RevocationData;
import org.apache.xmlbeans.XmlException;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.apache.poi.util.POILogger;

public class XAdESXLSignatureFacet extends SignatureFacet
{
    private static final POILogger LOG;
    private final CertificateFactory certificateFactory;
    
    public XAdESXLSignatureFacet() {
        try {
            this.certificateFactory = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException e) {
            throw new RuntimeException("X509 JCA error: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void postSign(final Document document) throws MarshalException {
        XAdESXLSignatureFacet.LOG.log(1, new Object[] { "XAdES-X-L post sign phase" });
        QualifyingPropertiesDocument qualDoc = null;
        QualifyingPropertiesType qualProps = null;
        final NodeList qualNl = document.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", "QualifyingProperties");
        if (qualNl.getLength() != 1) {
            throw new MarshalException("no XAdES-BES extension present");
        }
        try {
            qualDoc = QualifyingPropertiesDocument.Factory.parse(qualNl.item(0), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
        catch (final XmlException e) {
            throw new MarshalException((Throwable)e);
        }
        qualProps = qualDoc.getQualifyingProperties();
        UnsignedPropertiesType unsignedProps = qualProps.getUnsignedProperties();
        if (unsignedProps == null) {
            unsignedProps = qualProps.addNewUnsignedProperties();
        }
        UnsignedSignaturePropertiesType unsignedSigProps = unsignedProps.getUnsignedSignatureProperties();
        if (unsignedSigProps == null) {
            unsignedSigProps = unsignedProps.addNewUnsignedSignatureProperties();
        }
        final NodeList nlSigVal = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue");
        if (nlSigVal.getLength() != 1) {
            throw new IllegalArgumentException("SignatureValue is not set.");
        }
        final RevocationData tsaRevocationDataXadesT = new RevocationData();
        XAdESXLSignatureFacet.LOG.log(1, new Object[] { "creating XAdES-T time-stamp" });
        final XAdESTimeStampType signatureTimeStamp = this.createXAdESTimeStamp(Collections.singletonList(nlSigVal.item(0)), tsaRevocationDataXadesT);
        unsignedSigProps.addNewSignatureTimeStamp().set((XmlObject)signatureTimeStamp);
        if (tsaRevocationDataXadesT.hasRevocationDataEntries()) {
            final ValidationDataType validationData = this.createValidationData(tsaRevocationDataXadesT);
            XAdESSignatureFacet.insertXChild((XmlObject)unsignedSigProps, (XmlObject)validationData);
        }
        if (this.signatureConfig.getRevocationDataService() == null) {
            return;
        }
        final CompleteCertificateRefsType completeCertificateRefs = unsignedSigProps.addNewCompleteCertificateRefs();
        final CertIDListType certIdList = completeCertificateRefs.addNewCertRefs();
        final List<X509Certificate> certChain = this.signatureConfig.getSigningCertificateChain();
        final int chainSize = certChain.size();
        if (chainSize > 1) {
            for (final X509Certificate cert : certChain.subList(1, chainSize)) {
                final CertIDType certId = certIdList.addNewCert();
                XAdESSignatureFacet.setCertID(certId, this.signatureConfig, false, cert);
            }
        }
        final CompleteRevocationRefsType completeRevocationRefs = unsignedSigProps.addNewCompleteRevocationRefs();
        final RevocationData revocationData = this.signatureConfig.getRevocationDataService().getRevocationData(certChain);
        if (revocationData.hasCRLs()) {
            final CRLRefsType crlRefs = completeRevocationRefs.addNewCRLRefs();
            completeRevocationRefs.setCRLRefs(crlRefs);
            for (final byte[] encodedCrl : revocationData.getCRLs()) {
                final CRLRefType crlRef = crlRefs.addNewCRLRef();
                X509CRL crl;
                try {
                    crl = (X509CRL)this.certificateFactory.generateCRL(new ByteArrayInputStream(encodedCrl));
                }
                catch (final CRLException e2) {
                    throw new RuntimeException("CRL parse error: " + e2.getMessage(), e2);
                }
                final CRLIdentifierType crlIdentifier = crlRef.addNewCRLIdentifier();
                final String issuerName = crl.getIssuerDN().getName().replace(",", ", ");
                crlIdentifier.setIssuer(issuerName);
                final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
                cal.setTime(crl.getThisUpdate());
                crlIdentifier.setIssueTime(cal);
                crlIdentifier.setNumber(this.getCrlNumber(crl));
                final DigestAlgAndValueType digestAlgAndValue = crlRef.addNewDigestAlgAndValue();
                XAdESSignatureFacet.setDigestAlgAndValue(digestAlgAndValue, encodedCrl, this.signatureConfig.getDigestAlgo());
            }
        }
        if (revocationData.hasOCSPs()) {
            final OCSPRefsType ocspRefs = completeRevocationRefs.addNewOCSPRefs();
            for (final byte[] ocsp : revocationData.getOCSPs()) {
                try {
                    final OCSPRefType ocspRef = ocspRefs.addNewOCSPRef();
                    final DigestAlgAndValueType digestAlgAndValue2 = ocspRef.addNewDigestAlgAndValue();
                    XAdESSignatureFacet.setDigestAlgAndValue(digestAlgAndValue2, ocsp, this.signatureConfig.getDigestAlgo());
                    final OCSPIdentifierType ocspIdentifier = ocspRef.addNewOCSPIdentifier();
                    final OCSPResp ocspResp = new OCSPResp(ocsp);
                    final BasicOCSPResp basicOcspResp = (BasicOCSPResp)ocspResp.getResponseObject();
                    final Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
                    cal2.setTime(basicOcspResp.getProducedAt());
                    ocspIdentifier.setProducedAt(cal2);
                    final ResponderIDType responderId = ocspIdentifier.addNewResponderID();
                    final RespID respId = basicOcspResp.getResponderId();
                    final ResponderID ocspResponderId = respId.toASN1Primitive();
                    final DERTaggedObject derTaggedObject = (DERTaggedObject)ocspResponderId.toASN1Primitive();
                    if (2 == derTaggedObject.getTagNo()) {
                        final ASN1OctetString keyHashOctetString = (ASN1OctetString)derTaggedObject.getObject();
                        final byte[] key = keyHashOctetString.getOctets();
                        responderId.setByKey(key);
                    }
                    else {
                        final X500Name name = X500Name.getInstance((Object)derTaggedObject.getObject());
                        final String nameStr = name.toString();
                        responderId.setByName(nameStr);
                    }
                }
                catch (final Exception e3) {
                    throw new RuntimeException("OCSP decoding error: " + e3.getMessage(), e3);
                }
            }
        }
        final List<Node> timeStampNodesXadesX1 = new ArrayList<Node>();
        timeStampNodesXadesX1.add(nlSigVal.item(0));
        timeStampNodesXadesX1.add(signatureTimeStamp.getDomNode());
        timeStampNodesXadesX1.add(completeCertificateRefs.getDomNode());
        timeStampNodesXadesX1.add(completeRevocationRefs.getDomNode());
        final RevocationData tsaRevocationDataXadesX1 = new RevocationData();
        XAdESXLSignatureFacet.LOG.log(1, new Object[] { "creating XAdES-X time-stamp" });
        final XAdESTimeStampType timeStampXadesX1 = this.createXAdESTimeStamp(timeStampNodesXadesX1, tsaRevocationDataXadesX1);
        if (tsaRevocationDataXadesX1.hasRevocationDataEntries()) {
            final ValidationDataType timeStampXadesX1ValidationData = this.createValidationData(tsaRevocationDataXadesX1);
            XAdESSignatureFacet.insertXChild((XmlObject)unsignedSigProps, (XmlObject)timeStampXadesX1ValidationData);
        }
        unsignedSigProps.addNewSigAndRefsTimeStamp().set((XmlObject)timeStampXadesX1);
        final CertificateValuesType certificateValues = unsignedSigProps.addNewCertificateValues();
        for (final X509Certificate certificate : certChain) {
            final EncapsulatedPKIDataType encapsulatedPKIDataType = certificateValues.addNewEncapsulatedX509Certificate();
            try {
                encapsulatedPKIDataType.setByteArrayValue(certificate.getEncoded());
            }
            catch (final CertificateEncodingException e4) {
                throw new RuntimeException("certificate encoding error: " + e4.getMessage(), e4);
            }
        }
        final RevocationValuesType revocationValues = unsignedSigProps.addNewRevocationValues();
        this.createRevocationValues(revocationValues, revocationData);
        final Node n = document.importNode(qualProps.getDomNode(), true);
        qualNl.item(0).getParentNode().replaceChild(n, qualNl.item(0));
    }
    
    public static byte[] getC14nValue(final List<Node> nodeList, final String c14nAlgoId) {
        final ByteArrayOutputStream c14nValue = new ByteArrayOutputStream();
        try {
            for (final Node node : nodeList) {
                final Canonicalizer c14n = Canonicalizer.getInstance(c14nAlgoId);
                c14nValue.write(c14n.canonicalizeSubtree(node));
            }
        }
        catch (final RuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new RuntimeException("c14n error: " + e2.getMessage(), e2);
        }
        return c14nValue.toByteArray();
    }
    
    private BigInteger getCrlNumber(final X509CRL crl) {
        final byte[] crlNumberExtensionValue = crl.getExtensionValue(Extension.cRLNumber.getId());
        if (null == crlNumberExtensionValue) {
            return null;
        }
        try {
            ASN1InputStream asn1IS1 = null;
            ASN1InputStream asn1IS2 = null;
            try {
                asn1IS1 = new ASN1InputStream(crlNumberExtensionValue);
                final ASN1OctetString octetString = (ASN1OctetString)asn1IS1.readObject();
                final byte[] octets = octetString.getOctets();
                asn1IS2 = new ASN1InputStream(octets);
                final ASN1Integer integer = (ASN1Integer)asn1IS2.readObject();
                return integer.getPositiveValue();
            }
            finally {
                IOUtils.closeQuietly((Closeable)asn1IS2);
                IOUtils.closeQuietly((Closeable)asn1IS1);
            }
        }
        catch (final IOException e) {
            throw new RuntimeException("I/O error: " + e.getMessage(), e);
        }
    }
    
    private XAdESTimeStampType createXAdESTimeStamp(final List<Node> nodeList, final RevocationData revocationData) {
        final byte[] c14nSignatureValueElement = getC14nValue(nodeList, this.signatureConfig.getXadesCanonicalizationMethod());
        return this.createXAdESTimeStamp(c14nSignatureValueElement, revocationData);
    }
    
    private XAdESTimeStampType createXAdESTimeStamp(final byte[] data, final RevocationData revocationData) {
        byte[] timeStampToken;
        try {
            timeStampToken = this.signatureConfig.getTspService().timeStamp(data, revocationData);
        }
        catch (final Exception e) {
            throw new RuntimeException("error while creating a time-stamp: " + e.getMessage(), e);
        }
        final XAdESTimeStampType xadesTimeStamp = XAdESTimeStampType.Factory.newInstance();
        xadesTimeStamp.setId("time-stamp-" + UUID.randomUUID());
        final CanonicalizationMethodType c14nMethod = xadesTimeStamp.addNewCanonicalizationMethod();
        c14nMethod.setAlgorithm(this.signatureConfig.getXadesCanonicalizationMethod());
        final EncapsulatedPKIDataType encapsulatedTimeStamp = xadesTimeStamp.addNewEncapsulatedTimeStamp();
        encapsulatedTimeStamp.setByteArrayValue(timeStampToken);
        encapsulatedTimeStamp.setId("time-stamp-token-" + UUID.randomUUID());
        return xadesTimeStamp;
    }
    
    private ValidationDataType createValidationData(final RevocationData revocationData) {
        final ValidationDataType validationData = ValidationDataType.Factory.newInstance();
        final RevocationValuesType revocationValues = validationData.addNewRevocationValues();
        this.createRevocationValues(revocationValues, revocationData);
        return validationData;
    }
    
    private void createRevocationValues(final RevocationValuesType revocationValues, final RevocationData revocationData) {
        if (revocationData.hasCRLs()) {
            final CRLValuesType crlValues = revocationValues.addNewCRLValues();
            for (final byte[] crl : revocationData.getCRLs()) {
                final EncapsulatedPKIDataType encapsulatedCrlValue = crlValues.addNewEncapsulatedCRLValue();
                encapsulatedCrlValue.setByteArrayValue(crl);
            }
        }
        if (revocationData.hasOCSPs()) {
            final OCSPValuesType ocspValues = revocationValues.addNewOCSPValues();
            for (final byte[] ocsp : revocationData.getOCSPs()) {
                final EncapsulatedPKIDataType encapsulatedOcspValue = ocspValues.addNewEncapsulatedOCSPValue();
                encapsulatedOcspValue.setByteArrayValue(ocsp);
            }
        }
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XAdESXLSignatureFacet.class);
    }
}
