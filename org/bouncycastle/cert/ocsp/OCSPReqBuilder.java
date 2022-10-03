package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.OutputStream;
import java.util.Iterator;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import java.util.List;

public class OCSPReqBuilder
{
    private List list;
    private GeneralName requestorName;
    private Extensions requestExtensions;
    
    public OCSPReqBuilder() {
        this.list = new ArrayList();
        this.requestorName = null;
        this.requestExtensions = null;
    }
    
    public OCSPReqBuilder addRequest(final CertificateID certificateID) {
        this.list.add(new RequestObject(certificateID, null));
        return this;
    }
    
    public OCSPReqBuilder addRequest(final CertificateID certificateID, final Extensions extensions) {
        this.list.add(new RequestObject(certificateID, extensions));
        return this;
    }
    
    public OCSPReqBuilder setRequestorName(final X500Name x500Name) {
        this.requestorName = new GeneralName(4, (ASN1Encodable)x500Name);
        return this;
    }
    
    public OCSPReqBuilder setRequestorName(final GeneralName requestorName) {
        this.requestorName = requestorName;
        return this;
    }
    
    public OCSPReqBuilder setRequestExtensions(final Extensions requestExtensions) {
        this.requestExtensions = requestExtensions;
        return this;
    }
    
    private OCSPReq generateRequest(final ContentSigner contentSigner, final X509CertificateHolder[] array) throws OCSPException {
        final Iterator iterator = this.list.iterator();
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        while (iterator.hasNext()) {
            try {
                asn1EncodableVector.add((ASN1Encodable)((RequestObject)iterator.next()).toRequest());
                continue;
            }
            catch (final Exception ex) {
                throw new OCSPException("exception creating Request", ex);
            }
            break;
        }
        final TBSRequest tbsRequest = new TBSRequest(this.requestorName, (ASN1Sequence)new DERSequence(asn1EncodableVector), this.requestExtensions);
        Signature signature = null;
        if (contentSigner != null) {
            if (this.requestorName == null) {
                throw new OCSPException("requestorName must be specified if request is signed.");
            }
            try {
                final OutputStream outputStream = contentSigner.getOutputStream();
                outputStream.write(tbsRequest.getEncoded("DER"));
                outputStream.close();
            }
            catch (final Exception ex2) {
                throw new OCSPException("exception processing TBSRequest: " + ex2, ex2);
            }
            final DERBitString derBitString = new DERBitString(contentSigner.getSignature());
            final AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
            if (array != null && array.length > 0) {
                final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
                for (int i = 0; i != array.length; ++i) {
                    asn1EncodableVector2.add((ASN1Encodable)array[i].toASN1Structure());
                }
                signature = new Signature(algorithmIdentifier, derBitString, (ASN1Sequence)new DERSequence(asn1EncodableVector2));
            }
            else {
                signature = new Signature(algorithmIdentifier, derBitString);
            }
        }
        return new OCSPReq(new OCSPRequest(tbsRequest, signature));
    }
    
    public OCSPReq build() throws OCSPException {
        return this.generateRequest(null, null);
    }
    
    public OCSPReq build(final ContentSigner contentSigner, final X509CertificateHolder[] array) throws OCSPException, IllegalArgumentException {
        if (contentSigner == null) {
            throw new IllegalArgumentException("no signer specified");
        }
        return this.generateRequest(contentSigner, array);
    }
    
    private class RequestObject
    {
        CertificateID certId;
        Extensions extensions;
        
        public RequestObject(final CertificateID certId, final Extensions extensions) {
            this.certId = certId;
            this.extensions = extensions;
        }
        
        public Request toRequest() throws Exception {
            return new Request(this.certId.toASN1Primitive(), this.extensions);
        }
    }
}
