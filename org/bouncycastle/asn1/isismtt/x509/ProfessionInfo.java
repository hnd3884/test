package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.DirectoryString;
import java.util.Enumeration;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class ProfessionInfo extends ASN1Object
{
    public static final ASN1ObjectIdentifier Rechtsanwltin;
    public static final ASN1ObjectIdentifier Rechtsanwalt;
    public static final ASN1ObjectIdentifier Rechtsbeistand;
    public static final ASN1ObjectIdentifier Steuerberaterin;
    public static final ASN1ObjectIdentifier Steuerberater;
    public static final ASN1ObjectIdentifier Steuerbevollmchtigte;
    public static final ASN1ObjectIdentifier Steuerbevollmchtigter;
    public static final ASN1ObjectIdentifier Notarin;
    public static final ASN1ObjectIdentifier Notar;
    public static final ASN1ObjectIdentifier Notarvertreterin;
    public static final ASN1ObjectIdentifier Notarvertreter;
    public static final ASN1ObjectIdentifier Notariatsverwalterin;
    public static final ASN1ObjectIdentifier Notariatsverwalter;
    public static final ASN1ObjectIdentifier Wirtschaftsprferin;
    public static final ASN1ObjectIdentifier Wirtschaftsprfer;
    public static final ASN1ObjectIdentifier VereidigteBuchprferin;
    public static final ASN1ObjectIdentifier VereidigterBuchprfer;
    public static final ASN1ObjectIdentifier Patentanwltin;
    public static final ASN1ObjectIdentifier Patentanwalt;
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionItems;
    private ASN1Sequence professionOIDs;
    private String registrationNumber;
    private ASN1OctetString addProfessionInfo;
    
    public static ProfessionInfo getInstance(final Object o) {
        if (o == null || o instanceof ProfessionInfo) {
            return (ProfessionInfo)o;
        }
        if (o instanceof ASN1Sequence) {
            return new ProfessionInfo((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private ProfessionInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        ASN1Encodable asn1Encodable = objects.nextElement();
        if (asn1Encodable instanceof ASN1TaggedObject) {
            if (((ASN1TaggedObject)asn1Encodable).getTagNo() != 0) {
                throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)asn1Encodable).getTagNo());
            }
            this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)asn1Encodable, true);
            asn1Encodable = objects.nextElement();
        }
        this.professionItems = ASN1Sequence.getInstance(asn1Encodable);
        if (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable2 = objects.nextElement();
            if (asn1Encodable2 instanceof ASN1Sequence) {
                this.professionOIDs = ASN1Sequence.getInstance(asn1Encodable2);
            }
            else if (asn1Encodable2 instanceof DERPrintableString) {
                this.registrationNumber = DERPrintableString.getInstance(asn1Encodable2).getString();
            }
            else {
                if (!(asn1Encodable2 instanceof ASN1OctetString)) {
                    throw new IllegalArgumentException("Bad object encountered: " + asn1Encodable2.getClass());
                }
                this.addProfessionInfo = ASN1OctetString.getInstance(asn1Encodable2);
            }
        }
        if (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable3 = objects.nextElement();
            if (asn1Encodable3 instanceof DERPrintableString) {
                this.registrationNumber = DERPrintableString.getInstance(asn1Encodable3).getString();
            }
            else {
                if (!(asn1Encodable3 instanceof DEROctetString)) {
                    throw new IllegalArgumentException("Bad object encountered: " + ((DEROctetString)asn1Encodable3).getClass());
                }
                this.addProfessionInfo = (DEROctetString)asn1Encodable3;
            }
        }
        if (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable4 = objects.nextElement();
            if (!(asn1Encodable4 instanceof DEROctetString)) {
                throw new IllegalArgumentException("Bad object encountered: " + ((DEROctetString)asn1Encodable4).getClass());
            }
            this.addProfessionInfo = (DEROctetString)asn1Encodable4;
        }
    }
    
    public ProfessionInfo(final NamingAuthority namingAuthority, final DirectoryString[] array, final ASN1ObjectIdentifier[] array2, final String registrationNumber, final ASN1OctetString addProfessionInfo) {
        this.namingAuthority = namingAuthority;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.professionItems = new DERSequence(asn1EncodableVector);
        if (array2 != null) {
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            for (int j = 0; j != array2.length; ++j) {
                asn1EncodableVector2.add(array2[j]);
            }
            this.professionOIDs = new DERSequence(asn1EncodableVector2);
        }
        this.registrationNumber = registrationNumber;
        this.addProfessionInfo = addProfessionInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.namingAuthority != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.namingAuthority));
        }
        asn1EncodableVector.add(this.professionItems);
        if (this.professionOIDs != null) {
            asn1EncodableVector.add(this.professionOIDs);
        }
        if (this.registrationNumber != null) {
            asn1EncodableVector.add(new DERPrintableString(this.registrationNumber, true));
        }
        if (this.addProfessionInfo != null) {
            asn1EncodableVector.add(this.addProfessionInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public ASN1OctetString getAddProfessionInfo() {
        return this.addProfessionInfo;
    }
    
    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }
    
    public DirectoryString[] getProfessionItems() {
        final DirectoryString[] array = new DirectoryString[this.professionItems.size()];
        int n = 0;
        final Enumeration objects = this.professionItems.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = DirectoryString.getInstance(objects.nextElement());
        }
        return array;
    }
    
    public ASN1ObjectIdentifier[] getProfessionOIDs() {
        if (this.professionOIDs == null) {
            return new ASN1ObjectIdentifier[0];
        }
        final ASN1ObjectIdentifier[] array = new ASN1ObjectIdentifier[this.professionOIDs.size()];
        int n = 0;
        final Enumeration objects = this.professionOIDs.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = ASN1ObjectIdentifier.getInstance(objects.nextElement());
        }
        return array;
    }
    
    public String getRegistrationNumber() {
        return this.registrationNumber;
    }
    
    static {
        Rechtsanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".1");
        Rechtsanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".2");
        Rechtsbeistand = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".3");
        Steuerberaterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".4");
        Steuerberater = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".5");
        Steuerbevollmchtigte = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".6");
        Steuerbevollmchtigter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".7");
        Notarin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".8");
        Notar = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".9");
        Notarvertreterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".10");
        Notarvertreter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".11");
        Notariatsverwalterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".12");
        Notariatsverwalter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".13");
        Wirtschaftsprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".14");
        Wirtschaftsprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".15");
        VereidigteBuchprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".16");
        VereidigterBuchprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".17");
        Patentanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".18");
        Patentanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".19");
    }
}
