package org.bouncycastle.tsp;

import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Enumeration;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSProcessableByteArray;
import java.util.Collection;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.Extensions;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import java.io.OutputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import java.io.IOException;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAttributeTableGenerationException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.util.HashMap;
import java.util.ArrayList;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.cms.SignerInfoGenerator;
import java.util.Map;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import java.util.Locale;

public class TimeStampTokenGenerator
{
    public static final int R_SECONDS = 0;
    public static final int R_TENTHS_OF_SECONDS = 1;
    public static final int R_MICROSECONDS = 2;
    public static final int R_MILLISECONDS = 3;
    private int resolution;
    private Locale locale;
    private int accuracySeconds;
    private int accuracyMillis;
    private int accuracyMicros;
    boolean ordering;
    GeneralName tsa;
    private ASN1ObjectIdentifier tsaPolicyOID;
    private List certs;
    private List crls;
    private List attrCerts;
    private Map otherRevoc;
    private SignerInfoGenerator signerInfoGen;
    
    public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGenerator, final DigestCalculator digestCalculator, final ASN1ObjectIdentifier asn1ObjectIdentifier) throws IllegalArgumentException, TSPException {
        this(signerInfoGenerator, digestCalculator, asn1ObjectIdentifier, false);
    }
    
    public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGen, final DigestCalculator digestCalculator, final ASN1ObjectIdentifier tsaPolicyOID, final boolean b) throws IllegalArgumentException, TSPException {
        this.resolution = 0;
        this.locale = null;
        this.accuracySeconds = -1;
        this.accuracyMillis = -1;
        this.accuracyMicros = -1;
        this.ordering = false;
        this.tsa = null;
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this.attrCerts = new ArrayList();
        this.otherRevoc = new HashMap();
        this.signerInfoGen = signerInfoGen;
        this.tsaPolicyOID = tsaPolicyOID;
        if (!signerInfoGen.hasAssociatedCertificate()) {
            throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate");
        }
        final X509CertificateHolder associatedCertificate = signerInfoGen.getAssociatedCertificate();
        TSPUtil.validateCertificate(associatedCertificate);
        try {
            final OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(associatedCertificate.getEncoded());
            outputStream.close();
            if (digestCalculator.getAlgorithmIdentifier().getAlgorithm().equals((Object)OIWObjectIdentifiers.idSHA1)) {
                final byte[] digest = digestCalculator.getDigest();
                IssuerSerial issuerSerial;
                if (b) {
                    final GeneralNames generalNames;
                    issuerSerial = new IssuerSerial(generalNames, associatedCertificate.getSerialNumber());
                    generalNames = new GeneralNames(new GeneralName(associatedCertificate.getIssuer()));
                }
                else {
                    issuerSerial = null;
                }
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator() {
                    final /* synthetic */ ESSCertID val$essCertid = new ESSCertID(digest, issuerSerial);
                    
                    public AttributeTable getAttributes(final Map map) throws CMSAttributeTableGenerationException {
                        final AttributeTable attributes = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(map);
                        if (attributes.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null) {
                            return attributes.add(PKCSObjectIdentifiers.id_aa_signingCertificate, (ASN1Encodable)new SigningCertificate(this.val$essCertid));
                        }
                        return attributes;
                    }
                }, signerInfoGen.getUnsignedAttributeTableGenerator());
            }
            else {
                final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(digestCalculator.getAlgorithmIdentifier().getAlgorithm());
                final byte[] digest2 = digestCalculator.getDigest();
                IssuerSerial issuerSerial2;
                if (b) {
                    final GeneralNames generalNames2;
                    final ASN1Integer asn1Integer;
                    issuerSerial2 = new IssuerSerial(generalNames2, asn1Integer);
                    generalNames2 = new GeneralNames(new GeneralName(associatedCertificate.getIssuer()));
                    asn1Integer = new ASN1Integer(associatedCertificate.getSerialNumber());
                }
                else {
                    issuerSerial2 = null;
                }
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator() {
                    final /* synthetic */ ESSCertIDv2 val$essCertid = new ESSCertIDv2(algorithmIdentifier, digest2, issuerSerial2);
                    
                    public AttributeTable getAttributes(final Map map) throws CMSAttributeTableGenerationException {
                        final AttributeTable attributes = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(map);
                        if (attributes.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null) {
                            return attributes.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, (ASN1Encodable)new SigningCertificateV2(this.val$essCertid));
                        }
                        return attributes;
                    }
                }, signerInfoGen.getUnsignedAttributeTableGenerator());
            }
        }
        catch (final IOException ex) {
            throw new TSPException("Exception processing certificate.", ex);
        }
    }
    
    public void addCertificates(final Store store) {
        this.certs.addAll(store.getMatches((Selector)null));
    }
    
    public void addCRLs(final Store store) {
        this.crls.addAll(store.getMatches((Selector)null));
    }
    
    public void addAttributeCertificates(final Store store) {
        this.attrCerts.addAll(store.getMatches((Selector)null));
    }
    
    public void addOtherRevocationInfo(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Store store) {
        this.otherRevoc.put(asn1ObjectIdentifier, store.getMatches((Selector)null));
    }
    
    public void setResolution(final int resolution) {
        this.resolution = resolution;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public void setAccuracySeconds(final int accuracySeconds) {
        this.accuracySeconds = accuracySeconds;
    }
    
    public void setAccuracyMillis(final int accuracyMillis) {
        this.accuracyMillis = accuracyMillis;
    }
    
    public void setAccuracyMicros(final int accuracyMicros) {
        this.accuracyMicros = accuracyMicros;
    }
    
    public void setOrdering(final boolean ordering) {
        this.ordering = ordering;
    }
    
    public void setTSA(final GeneralName tsa) {
        this.tsa = tsa;
    }
    
    public TimeStampToken generate(final TimeStampRequest timeStampRequest, final BigInteger bigInteger, final Date date) throws TSPException {
        return this.generate(timeStampRequest, bigInteger, date, null);
    }
    
    public TimeStampToken generate(final TimeStampRequest timeStampRequest, final BigInteger bigInteger, final Date date, final Extensions extensions) throws TSPException {
        final MessageImprint messageImprint = new MessageImprint(new AlgorithmIdentifier(timeStampRequest.getMessageImprintAlgOID(), (ASN1Encodable)DERNull.INSTANCE), timeStampRequest.getMessageImprintDigest());
        Accuracy accuracy = null;
        if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
            ASN1Integer asn1Integer = null;
            if (this.accuracySeconds > 0) {
                asn1Integer = new ASN1Integer((long)this.accuracySeconds);
            }
            ASN1Integer asn1Integer2 = null;
            if (this.accuracyMillis > 0) {
                asn1Integer2 = new ASN1Integer((long)this.accuracyMillis);
            }
            ASN1Integer asn1Integer3 = null;
            if (this.accuracyMicros > 0) {
                asn1Integer3 = new ASN1Integer((long)this.accuracyMicros);
            }
            accuracy = new Accuracy(asn1Integer, asn1Integer2, asn1Integer3);
        }
        ASN1Boolean instance = null;
        if (this.ordering) {
            instance = ASN1Boolean.getInstance(this.ordering);
        }
        ASN1Integer asn1Integer4 = null;
        if (timeStampRequest.getNonce() != null) {
            asn1Integer4 = new ASN1Integer(timeStampRequest.getNonce());
        }
        ASN1ObjectIdentifier asn1ObjectIdentifier = this.tsaPolicyOID;
        if (timeStampRequest.getReqPolicy() != null) {
            asn1ObjectIdentifier = timeStampRequest.getReqPolicy();
        }
        Extensions extensions2 = timeStampRequest.getExtensions();
        if (extensions != null) {
            final ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            if (extensions2 != null) {
                final Enumeration oids = extensions2.oids();
                while (oids.hasMoreElements()) {
                    extensionsGenerator.addExtension(extensions2.getExtension(ASN1ObjectIdentifier.getInstance(oids.nextElement())));
                }
            }
            final Enumeration oids2 = extensions.oids();
            while (oids2.hasMoreElements()) {
                extensionsGenerator.addExtension(extensions.getExtension(ASN1ObjectIdentifier.getInstance(oids2.nextElement())));
            }
            extensions2 = extensionsGenerator.generate();
        }
        ASN1GeneralizedTime generalizedTime;
        if (this.resolution == 0) {
            generalizedTime = ((this.locale == null) ? new ASN1GeneralizedTime(date) : new ASN1GeneralizedTime(date, this.locale));
        }
        else {
            generalizedTime = this.createGeneralizedTime(date);
        }
        final TSTInfo tstInfo = new TSTInfo(asn1ObjectIdentifier, messageImprint, new ASN1Integer(bigInteger), generalizedTime, accuracy, instance, asn1Integer4, this.tsa, extensions2);
        try {
            final CMSSignedDataGenerator cmsSignedDataGenerator = new CMSSignedDataGenerator();
            if (timeStampRequest.getCertReq()) {
                cmsSignedDataGenerator.addCertificates((Store)new CollectionStore((Collection)this.certs));
                cmsSignedDataGenerator.addAttributeCertificates((Store)new CollectionStore((Collection)this.attrCerts));
            }
            cmsSignedDataGenerator.addCRLs((Store)new CollectionStore((Collection)this.crls));
            if (!this.otherRevoc.isEmpty()) {
                for (final ASN1ObjectIdentifier asn1ObjectIdentifier2 : this.otherRevoc.keySet()) {
                    cmsSignedDataGenerator.addOtherRevocationInfo(asn1ObjectIdentifier2, (Store)new CollectionStore((Collection)this.otherRevoc.get(asn1ObjectIdentifier2)));
                }
            }
            cmsSignedDataGenerator.addSignerInfoGenerator(this.signerInfoGen);
            return new TimeStampToken(cmsSignedDataGenerator.generate(new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, tstInfo.getEncoded("DER")), true));
        }
        catch (final CMSException ex) {
            throw new TSPException("Error generating time-stamp token", ex);
        }
        catch (final IOException ex2) {
            throw new TSPException("Exception encoding info", ex2);
        }
    }
    
    private ASN1GeneralizedTime createGeneralizedTime(final Date date) throws TSPException {
        final String s = "yyyyMMddHHmmss.SSS";
        final SimpleDateFormat simpleDateFormat = (this.locale == null) ? new SimpleDateFormat(s) : new SimpleDateFormat(s, this.locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        final StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));
        final int index = sb.indexOf(".");
        if (index < 0) {
            sb.append("Z");
            return new ASN1GeneralizedTime(sb.toString());
        }
        switch (this.resolution) {
            case 1: {
                if (sb.length() > index + 2) {
                    sb.delete(index + 2, sb.length());
                    break;
                }
                break;
            }
            case 2: {
                if (sb.length() > index + 3) {
                    sb.delete(index + 3, sb.length());
                    break;
                }
                break;
            }
            case 3: {
                break;
            }
            default: {
                throw new TSPException("unknown time-stamp resolution: " + this.resolution);
            }
        }
        while (sb.charAt(sb.length() - 1) == '0') {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (sb.length() - 1 == index) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("Z");
        return new ASN1GeneralizedTime(sb.toString());
    }
}
