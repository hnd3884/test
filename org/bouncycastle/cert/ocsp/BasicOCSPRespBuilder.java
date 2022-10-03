package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.OutputStream;
import java.util.Iterator;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import java.util.Date;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.Extensions;
import java.util.List;

public class BasicOCSPRespBuilder
{
    private List list;
    private Extensions responseExtensions;
    private RespID responderID;
    
    public BasicOCSPRespBuilder(final RespID responderID) {
        this.list = new ArrayList();
        this.responseExtensions = null;
        this.responderID = responderID;
    }
    
    public BasicOCSPRespBuilder(final SubjectPublicKeyInfo subjectPublicKeyInfo, final DigestCalculator digestCalculator) throws OCSPException {
        this.list = new ArrayList();
        this.responseExtensions = null;
        this.responderID = new RespID(subjectPublicKeyInfo, digestCalculator);
    }
    
    public BasicOCSPRespBuilder addResponse(final CertificateID certificateID, final CertificateStatus certificateStatus) {
        this.addResponse(certificateID, certificateStatus, new Date(), null, null);
        return this;
    }
    
    public BasicOCSPRespBuilder addResponse(final CertificateID certificateID, final CertificateStatus certificateStatus, final Extensions extensions) {
        this.addResponse(certificateID, certificateStatus, new Date(), null, extensions);
        return this;
    }
    
    public BasicOCSPRespBuilder addResponse(final CertificateID certificateID, final CertificateStatus certificateStatus, final Date date, final Extensions extensions) {
        this.addResponse(certificateID, certificateStatus, new Date(), date, extensions);
        return this;
    }
    
    public BasicOCSPRespBuilder addResponse(final CertificateID certificateID, final CertificateStatus certificateStatus, final Date date, final Date date2) {
        this.addResponse(certificateID, certificateStatus, date, date2, null);
        return this;
    }
    
    public BasicOCSPRespBuilder addResponse(final CertificateID certificateID, final CertificateStatus certificateStatus, final Date date, final Date date2, final Extensions extensions) {
        this.list.add(new ResponseObject(certificateID, certificateStatus, date, date2, extensions));
        return this;
    }
    
    public BasicOCSPRespBuilder setResponseExtensions(final Extensions responseExtensions) {
        this.responseExtensions = responseExtensions;
        return this;
    }
    
    public BasicOCSPResp build(final ContentSigner contentSigner, final X509CertificateHolder[] array, final Date date) throws OCSPException {
        final Iterator iterator = this.list.iterator();
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        while (iterator.hasNext()) {
            try {
                asn1EncodableVector.add((ASN1Encodable)((ResponseObject)iterator.next()).toResponse());
                continue;
            }
            catch (final Exception ex) {
                throw new OCSPException("exception creating Request", ex);
            }
            break;
        }
        final ResponseData responseData = new ResponseData(this.responderID.toASN1Primitive(), new ASN1GeneralizedTime(date), (ASN1Sequence)new DERSequence(asn1EncodableVector), this.responseExtensions);
        DERBitString derBitString;
        try {
            final OutputStream outputStream = contentSigner.getOutputStream();
            outputStream.write(responseData.getEncoded("DER"));
            outputStream.close();
            derBitString = new DERBitString(contentSigner.getSignature());
        }
        catch (final Exception ex2) {
            throw new OCSPException("exception processing TBSRequest: " + ex2.getMessage(), ex2);
        }
        final AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
        Object o = null;
        if (array != null && array.length > 0) {
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            for (int i = 0; i != array.length; ++i) {
                asn1EncodableVector2.add((ASN1Encodable)array[i].toASN1Structure());
            }
            o = new DERSequence(asn1EncodableVector2);
        }
        return new BasicOCSPResp(new BasicOCSPResponse(responseData, algorithmIdentifier, derBitString, (ASN1Sequence)o));
    }
    
    private class ResponseObject
    {
        CertificateID certId;
        CertStatus certStatus;
        ASN1GeneralizedTime thisUpdate;
        ASN1GeneralizedTime nextUpdate;
        Extensions extensions;
        
        public ResponseObject(final CertificateID certId, final CertificateStatus certificateStatus, final Date date, final Date date2, final Extensions extensions) {
            this.certId = certId;
            if (certificateStatus == null) {
                this.certStatus = new CertStatus();
            }
            else if (certificateStatus instanceof UnknownStatus) {
                this.certStatus = new CertStatus(2, (ASN1Encodable)DERNull.INSTANCE);
            }
            else {
                final RevokedStatus revokedStatus = (RevokedStatus)certificateStatus;
                if (revokedStatus.hasRevocationReason()) {
                    this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(revokedStatus.getRevocationTime()), CRLReason.lookup(revokedStatus.getRevocationReason())));
                }
                else {
                    this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(revokedStatus.getRevocationTime()), (CRLReason)null));
                }
            }
            this.thisUpdate = (ASN1GeneralizedTime)new DERGeneralizedTime(date);
            if (date2 != null) {
                this.nextUpdate = (ASN1GeneralizedTime)new DERGeneralizedTime(date2);
            }
            else {
                this.nextUpdate = null;
            }
            this.extensions = extensions;
        }
        
        public SingleResponse toResponse() throws Exception {
            return new SingleResponse(this.certId.toASN1Primitive(), this.certStatus, this.thisUpdate, this.nextUpdate, this.extensions);
        }
    }
}
