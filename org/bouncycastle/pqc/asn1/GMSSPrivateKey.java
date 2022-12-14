package org.bouncycastle.pqc.asn1;

import java.math.BigInteger;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.crypto.gmss.GMSSParameters;
import org.bouncycastle.pqc.crypto.gmss.GMSSRootSig;
import org.bouncycastle.pqc.crypto.gmss.GMSSRootCalc;
import org.bouncycastle.pqc.crypto.gmss.GMSSLeaf;
import java.util.Vector;
import org.bouncycastle.pqc.crypto.gmss.Treehash;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Object;

public class GMSSPrivateKey extends ASN1Object
{
    private ASN1Primitive primitive;
    
    private GMSSPrivateKey(final ASN1Sequence asn1Sequence) {
        final ASN1Sequence asn1Sequence2 = (ASN1Sequence)asn1Sequence.getObjectAt(0);
        final int[] array = new int[asn1Sequence2.size()];
        for (int i = 0; i < asn1Sequence2.size(); ++i) {
            array[i] = checkBigIntegerInIntRange(asn1Sequence2.getObjectAt(i));
        }
        final ASN1Sequence asn1Sequence3 = (ASN1Sequence)asn1Sequence.getObjectAt(1);
        final byte[][] array2 = new byte[asn1Sequence3.size()][];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = ((DEROctetString)asn1Sequence3.getObjectAt(j)).getOctets();
        }
        final ASN1Sequence asn1Sequence4 = (ASN1Sequence)asn1Sequence.getObjectAt(2);
        final byte[][] array3 = new byte[asn1Sequence4.size()][];
        for (int k = 0; k < array3.length; ++k) {
            array3[k] = ((DEROctetString)asn1Sequence4.getObjectAt(k)).getOctets();
        }
        final ASN1Sequence asn1Sequence5 = (ASN1Sequence)asn1Sequence.getObjectAt(3);
        final byte[][][] array4 = new byte[asn1Sequence5.size()][][];
        for (int l = 0; l < array4.length; ++l) {
            final ASN1Sequence asn1Sequence6 = (ASN1Sequence)asn1Sequence5.getObjectAt(l);
            array4[l] = new byte[asn1Sequence6.size()][];
            for (int n = 0; n < array4[l].length; ++n) {
                array4[l][n] = ((DEROctetString)asn1Sequence6.getObjectAt(n)).getOctets();
            }
        }
        final ASN1Sequence asn1Sequence7 = (ASN1Sequence)asn1Sequence.getObjectAt(4);
        final byte[][][] array5 = new byte[asn1Sequence7.size()][][];
        for (int n2 = 0; n2 < array5.length; ++n2) {
            final ASN1Sequence asn1Sequence8 = (ASN1Sequence)asn1Sequence7.getObjectAt(n2);
            array5[n2] = new byte[asn1Sequence8.size()][];
            for (int n3 = 0; n3 < array5[n2].length; ++n3) {
                array5[n2][n3] = ((DEROctetString)asn1Sequence8.getObjectAt(n3)).getOctets();
            }
        }
        final Treehash[][] array6 = new Treehash[((ASN1Sequence)asn1Sequence.getObjectAt(5)).size()][];
    }
    
    public GMSSPrivateKey(final int[] array, final byte[][] array2, final byte[][] array3, final byte[][][] array4, final byte[][][] array5, final Treehash[][] array6, final Treehash[][] array7, final Vector[] array8, final Vector[] array9, final Vector[][] array10, final Vector[][] array11, final byte[][][] array12, final GMSSLeaf[] array13, final GMSSLeaf[] array14, final GMSSLeaf[] array15, final int[] array16, final byte[][] array17, final GMSSRootCalc[] array18, final byte[][] array19, final GMSSRootSig[] array20, final GMSSParameters gmssParameters, final AlgorithmIdentifier algorithmIdentifier) {
        this.primitive = this.encode(array, array2, array3, array4, array5, array12, array6, array7, array8, array9, array10, array11, array13, array14, array15, array16, array17, array18, array19, array20, gmssParameters, new AlgorithmIdentifier[] { algorithmIdentifier });
    }
    
    private ASN1Primitive encode(final int[] array, final byte[][] array2, final byte[][] array3, final byte[][][] array4, final byte[][][] array5, final byte[][][] array6, final Treehash[][] array7, final Treehash[][] array8, final Vector[] array9, final Vector[] array10, final Vector[][] array11, final Vector[][] array12, final GMSSLeaf[] array13, final GMSSLeaf[] array14, final GMSSLeaf[] array15, final int[] array16, final byte[][] array17, final GMSSRootCalc[] array18, final byte[][] array19, final GMSSRootSig[] array20, final GMSSParameters gmssParameters, final AlgorithmIdentifier[] array21) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        for (int i = 0; i < array.length; ++i) {
            asn1EncodableVector2.add(new ASN1Integer(array[i]));
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
        for (int j = 0; j < array2.length; ++j) {
            asn1EncodableVector3.add(new DEROctetString(array2[j]));
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector3));
        final ASN1EncodableVector asn1EncodableVector4 = new ASN1EncodableVector();
        for (int k = 0; k < array3.length; ++k) {
            asn1EncodableVector4.add(new DEROctetString(array3[k]));
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector4));
        ASN1EncodableVector asn1EncodableVector5 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector6 = new ASN1EncodableVector();
        for (int l = 0; l < array4.length; ++l) {
            for (int n = 0; n < array4[l].length; ++n) {
                asn1EncodableVector5.add(new DEROctetString(array4[l][n]));
            }
            asn1EncodableVector6.add(new DERSequence(asn1EncodableVector5));
            asn1EncodableVector5 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector6));
        ASN1EncodableVector asn1EncodableVector7 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector8 = new ASN1EncodableVector();
        for (int n2 = 0; n2 < array5.length; ++n2) {
            for (int n3 = 0; n3 < array5[n2].length; ++n3) {
                asn1EncodableVector7.add(new DEROctetString(array5[n2][n3]));
            }
            asn1EncodableVector8.add(new DERSequence(asn1EncodableVector7));
            asn1EncodableVector7 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector8));
        final ASN1EncodableVector asn1EncodableVector9 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector10 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector11 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector12 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector13 = new ASN1EncodableVector();
        for (int n4 = 0; n4 < array7.length; ++n4) {
            for (int n5 = 0; n5 < array7[n4].length; ++n5) {
                asn1EncodableVector11.add(new DERSequence(array21[0]));
                final int n6 = array7[n4][n5].getStatInt()[1];
                asn1EncodableVector12.add(new DEROctetString(array7[n4][n5].getStatByte()[0]));
                asn1EncodableVector12.add(new DEROctetString(array7[n4][n5].getStatByte()[1]));
                asn1EncodableVector12.add(new DEROctetString(array7[n4][n5].getStatByte()[2]));
                for (int n7 = 0; n7 < n6; ++n7) {
                    asn1EncodableVector12.add(new DEROctetString(array7[n4][n5].getStatByte()[3 + n7]));
                }
                asn1EncodableVector11.add(new DERSequence(asn1EncodableVector12));
                asn1EncodableVector12 = new ASN1EncodableVector();
                asn1EncodableVector13.add(new ASN1Integer(array7[n4][n5].getStatInt()[0]));
                asn1EncodableVector13.add(new ASN1Integer(n6));
                asn1EncodableVector13.add(new ASN1Integer(array7[n4][n5].getStatInt()[2]));
                asn1EncodableVector13.add(new ASN1Integer(array7[n4][n5].getStatInt()[3]));
                asn1EncodableVector13.add(new ASN1Integer(array7[n4][n5].getStatInt()[4]));
                asn1EncodableVector13.add(new ASN1Integer(array7[n4][n5].getStatInt()[5]));
                for (int n8 = 0; n8 < n6; ++n8) {
                    asn1EncodableVector13.add(new ASN1Integer(array7[n4][n5].getStatInt()[6 + n8]));
                }
                asn1EncodableVector11.add(new DERSequence(asn1EncodableVector13));
                asn1EncodableVector13 = new ASN1EncodableVector();
                asn1EncodableVector10.add(new DERSequence(asn1EncodableVector11));
                asn1EncodableVector11 = new ASN1EncodableVector();
            }
            asn1EncodableVector9.add(new DERSequence(asn1EncodableVector10));
            asn1EncodableVector10 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector9));
        final ASN1EncodableVector asn1EncodableVector14 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector15 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector16 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector17 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector18 = new ASN1EncodableVector();
        for (int n9 = 0; n9 < array8.length; ++n9) {
            for (int n10 = 0; n10 < array8[n9].length; ++n10) {
                asn1EncodableVector16.add(new DERSequence(array21[0]));
                final int n11 = array8[n9][n10].getStatInt()[1];
                asn1EncodableVector17.add(new DEROctetString(array8[n9][n10].getStatByte()[0]));
                asn1EncodableVector17.add(new DEROctetString(array8[n9][n10].getStatByte()[1]));
                asn1EncodableVector17.add(new DEROctetString(array8[n9][n10].getStatByte()[2]));
                for (int n12 = 0; n12 < n11; ++n12) {
                    asn1EncodableVector17.add(new DEROctetString(array8[n9][n10].getStatByte()[3 + n12]));
                }
                asn1EncodableVector16.add(new DERSequence(asn1EncodableVector17));
                asn1EncodableVector17 = new ASN1EncodableVector();
                asn1EncodableVector18.add(new ASN1Integer(array8[n9][n10].getStatInt()[0]));
                asn1EncodableVector18.add(new ASN1Integer(n11));
                asn1EncodableVector18.add(new ASN1Integer(array8[n9][n10].getStatInt()[2]));
                asn1EncodableVector18.add(new ASN1Integer(array8[n9][n10].getStatInt()[3]));
                asn1EncodableVector18.add(new ASN1Integer(array8[n9][n10].getStatInt()[4]));
                asn1EncodableVector18.add(new ASN1Integer(array8[n9][n10].getStatInt()[5]));
                for (int n13 = 0; n13 < n11; ++n13) {
                    asn1EncodableVector18.add(new ASN1Integer(array8[n9][n10].getStatInt()[6 + n13]));
                }
                asn1EncodableVector16.add(new DERSequence(asn1EncodableVector18));
                asn1EncodableVector18 = new ASN1EncodableVector();
                asn1EncodableVector15.add(new DERSequence(asn1EncodableVector16));
                asn1EncodableVector16 = new ASN1EncodableVector();
            }
            asn1EncodableVector14.add(new DERSequence(new DERSequence(asn1EncodableVector15)));
            asn1EncodableVector15 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector14));
        ASN1EncodableVector asn1EncodableVector19 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector20 = new ASN1EncodableVector();
        for (int n14 = 0; n14 < array6.length; ++n14) {
            for (int n15 = 0; n15 < array6[n14].length; ++n15) {
                asn1EncodableVector19.add(new DEROctetString(array6[n14][n15]));
            }
            asn1EncodableVector20.add(new DERSequence(asn1EncodableVector19));
            asn1EncodableVector19 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector20));
        ASN1EncodableVector asn1EncodableVector21 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector22 = new ASN1EncodableVector();
        for (int n16 = 0; n16 < array9.length; ++n16) {
            for (int n17 = 0; n17 < array9[n16].size(); ++n17) {
                asn1EncodableVector21.add(new DEROctetString((byte[])array9[n16].elementAt(n17)));
            }
            asn1EncodableVector22.add(new DERSequence(asn1EncodableVector21));
            asn1EncodableVector21 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector22));
        ASN1EncodableVector asn1EncodableVector23 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector24 = new ASN1EncodableVector();
        for (int n18 = 0; n18 < array10.length; ++n18) {
            for (int n19 = 0; n19 < array10[n18].size(); ++n19) {
                asn1EncodableVector23.add(new DEROctetString((byte[])array10[n18].elementAt(n19)));
            }
            asn1EncodableVector24.add(new DERSequence(asn1EncodableVector23));
            asn1EncodableVector23 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector24));
        ASN1EncodableVector asn1EncodableVector25 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector26 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector27 = new ASN1EncodableVector();
        for (int n20 = 0; n20 < array11.length; ++n20) {
            for (int n21 = 0; n21 < array11[n20].length; ++n21) {
                for (int n22 = 0; n22 < array11[n20][n21].size(); ++n22) {
                    asn1EncodableVector25.add(new DEROctetString((byte[])array11[n20][n21].elementAt(n22)));
                }
                asn1EncodableVector26.add(new DERSequence(asn1EncodableVector25));
                asn1EncodableVector25 = new ASN1EncodableVector();
            }
            asn1EncodableVector27.add(new DERSequence(asn1EncodableVector26));
            asn1EncodableVector26 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector27));
        ASN1EncodableVector asn1EncodableVector28 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector29 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector30 = new ASN1EncodableVector();
        for (int n23 = 0; n23 < array12.length; ++n23) {
            for (int n24 = 0; n24 < array12[n23].length; ++n24) {
                for (int n25 = 0; n25 < array12[n23][n24].size(); ++n25) {
                    asn1EncodableVector28.add(new DEROctetString((byte[])array12[n23][n24].elementAt(n25)));
                }
                asn1EncodableVector29.add(new DERSequence(asn1EncodableVector28));
                asn1EncodableVector28 = new ASN1EncodableVector();
            }
            asn1EncodableVector30.add(new DERSequence(asn1EncodableVector29));
            asn1EncodableVector29 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector30));
        final ASN1EncodableVector asn1EncodableVector31 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector32 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector33 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector34 = new ASN1EncodableVector();
        for (int n26 = 0; n26 < array13.length; ++n26) {
            asn1EncodableVector32.add(new DERSequence(array21[0]));
            final byte[][] statByte = array13[n26].getStatByte();
            asn1EncodableVector33.add(new DEROctetString(statByte[0]));
            asn1EncodableVector33.add(new DEROctetString(statByte[1]));
            asn1EncodableVector33.add(new DEROctetString(statByte[2]));
            asn1EncodableVector33.add(new DEROctetString(statByte[3]));
            asn1EncodableVector32.add(new DERSequence(asn1EncodableVector33));
            asn1EncodableVector33 = new ASN1EncodableVector();
            final int[] statInt = array13[n26].getStatInt();
            asn1EncodableVector34.add(new ASN1Integer(statInt[0]));
            asn1EncodableVector34.add(new ASN1Integer(statInt[1]));
            asn1EncodableVector34.add(new ASN1Integer(statInt[2]));
            asn1EncodableVector34.add(new ASN1Integer(statInt[3]));
            asn1EncodableVector32.add(new DERSequence(asn1EncodableVector34));
            asn1EncodableVector34 = new ASN1EncodableVector();
            asn1EncodableVector31.add(new DERSequence(asn1EncodableVector32));
            asn1EncodableVector32 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector31));
        final ASN1EncodableVector asn1EncodableVector35 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector36 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector37 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector38 = new ASN1EncodableVector();
        for (int n27 = 0; n27 < array14.length; ++n27) {
            asn1EncodableVector36.add(new DERSequence(array21[0]));
            final byte[][] statByte2 = array14[n27].getStatByte();
            asn1EncodableVector37.add(new DEROctetString(statByte2[0]));
            asn1EncodableVector37.add(new DEROctetString(statByte2[1]));
            asn1EncodableVector37.add(new DEROctetString(statByte2[2]));
            asn1EncodableVector37.add(new DEROctetString(statByte2[3]));
            asn1EncodableVector36.add(new DERSequence(asn1EncodableVector37));
            asn1EncodableVector37 = new ASN1EncodableVector();
            final int[] statInt2 = array14[n27].getStatInt();
            asn1EncodableVector38.add(new ASN1Integer(statInt2[0]));
            asn1EncodableVector38.add(new ASN1Integer(statInt2[1]));
            asn1EncodableVector38.add(new ASN1Integer(statInt2[2]));
            asn1EncodableVector38.add(new ASN1Integer(statInt2[3]));
            asn1EncodableVector36.add(new DERSequence(asn1EncodableVector38));
            asn1EncodableVector38 = new ASN1EncodableVector();
            asn1EncodableVector35.add(new DERSequence(asn1EncodableVector36));
            asn1EncodableVector36 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector35));
        final ASN1EncodableVector asn1EncodableVector39 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector40 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector41 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector42 = new ASN1EncodableVector();
        for (int n28 = 0; n28 < array15.length; ++n28) {
            asn1EncodableVector40.add(new DERSequence(array21[0]));
            final byte[][] statByte3 = array15[n28].getStatByte();
            asn1EncodableVector41.add(new DEROctetString(statByte3[0]));
            asn1EncodableVector41.add(new DEROctetString(statByte3[1]));
            asn1EncodableVector41.add(new DEROctetString(statByte3[2]));
            asn1EncodableVector41.add(new DEROctetString(statByte3[3]));
            asn1EncodableVector40.add(new DERSequence(asn1EncodableVector41));
            asn1EncodableVector41 = new ASN1EncodableVector();
            final int[] statInt3 = array15[n28].getStatInt();
            asn1EncodableVector42.add(new ASN1Integer(statInt3[0]));
            asn1EncodableVector42.add(new ASN1Integer(statInt3[1]));
            asn1EncodableVector42.add(new ASN1Integer(statInt3[2]));
            asn1EncodableVector42.add(new ASN1Integer(statInt3[3]));
            asn1EncodableVector40.add(new DERSequence(asn1EncodableVector42));
            asn1EncodableVector42 = new ASN1EncodableVector();
            asn1EncodableVector39.add(new DERSequence(asn1EncodableVector40));
            asn1EncodableVector40 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector39));
        final ASN1EncodableVector asn1EncodableVector43 = new ASN1EncodableVector();
        for (int n29 = 0; n29 < array16.length; ++n29) {
            asn1EncodableVector43.add(new ASN1Integer(array16[n29]));
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector43));
        final ASN1EncodableVector asn1EncodableVector44 = new ASN1EncodableVector();
        for (int n30 = 0; n30 < array17.length; ++n30) {
            asn1EncodableVector44.add(new DEROctetString(array17[n30]));
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector44));
        final ASN1EncodableVector asn1EncodableVector45 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector46 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector47 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector48 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector49 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector50 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector51 = new ASN1EncodableVector();
        for (int n31 = 0; n31 < array18.length; ++n31) {
            asn1EncodableVector46.add(new DERSequence(array21[0]));
            final ASN1EncodableVector asn1EncodableVector52 = new ASN1EncodableVector();
            final int n32 = array18[n31].getStatInt()[0];
            final int n33 = array18[n31].getStatInt()[7];
            asn1EncodableVector48.add(new DEROctetString(array18[n31].getStatByte()[0]));
            for (int n34 = 0; n34 < n32; ++n34) {
                asn1EncodableVector48.add(new DEROctetString(array18[n31].getStatByte()[1 + n34]));
            }
            for (int n35 = 0; n35 < n33; ++n35) {
                asn1EncodableVector48.add(new DEROctetString(array18[n31].getStatByte()[1 + n32 + n35]));
            }
            asn1EncodableVector46.add(new DERSequence(asn1EncodableVector48));
            asn1EncodableVector48 = new ASN1EncodableVector();
            asn1EncodableVector49.add(new ASN1Integer(n32));
            asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[1]));
            asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[2]));
            asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[3]));
            asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[4]));
            asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[5]));
            asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[6]));
            asn1EncodableVector49.add(new ASN1Integer(n33));
            for (int n36 = 0; n36 < n32; ++n36) {
                asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[8 + n36]));
            }
            for (int n37 = 0; n37 < n33; ++n37) {
                asn1EncodableVector49.add(new ASN1Integer(array18[n31].getStatInt()[8 + n32 + n37]));
            }
            asn1EncodableVector46.add(new DERSequence(asn1EncodableVector49));
            asn1EncodableVector49 = new ASN1EncodableVector();
            ASN1EncodableVector asn1EncodableVector53 = new ASN1EncodableVector();
            ASN1EncodableVector asn1EncodableVector54 = new ASN1EncodableVector();
            ASN1EncodableVector asn1EncodableVector55 = new ASN1EncodableVector();
            if (array18[n31].getTreehash() != null) {
                for (int n38 = 0; n38 < array18[n31].getTreehash().length; ++n38) {
                    asn1EncodableVector53.add(new DERSequence(array21[0]));
                    final int n39 = array18[n31].getTreehash()[n38].getStatInt()[1];
                    asn1EncodableVector54.add(new DEROctetString(array18[n31].getTreehash()[n38].getStatByte()[0]));
                    asn1EncodableVector54.add(new DEROctetString(array18[n31].getTreehash()[n38].getStatByte()[1]));
                    asn1EncodableVector54.add(new DEROctetString(array18[n31].getTreehash()[n38].getStatByte()[2]));
                    for (int n40 = 0; n40 < n39; ++n40) {
                        asn1EncodableVector54.add(new DEROctetString(array18[n31].getTreehash()[n38].getStatByte()[3 + n40]));
                    }
                    asn1EncodableVector53.add(new DERSequence(asn1EncodableVector54));
                    asn1EncodableVector54 = new ASN1EncodableVector();
                    asn1EncodableVector55.add(new ASN1Integer(array18[n31].getTreehash()[n38].getStatInt()[0]));
                    asn1EncodableVector55.add(new ASN1Integer(n39));
                    asn1EncodableVector55.add(new ASN1Integer(array18[n31].getTreehash()[n38].getStatInt()[2]));
                    asn1EncodableVector55.add(new ASN1Integer(array18[n31].getTreehash()[n38].getStatInt()[3]));
                    asn1EncodableVector55.add(new ASN1Integer(array18[n31].getTreehash()[n38].getStatInt()[4]));
                    asn1EncodableVector55.add(new ASN1Integer(array18[n31].getTreehash()[n38].getStatInt()[5]));
                    for (int n41 = 0; n41 < n39; ++n41) {
                        asn1EncodableVector55.add(new ASN1Integer(array18[n31].getTreehash()[n38].getStatInt()[6 + n41]));
                    }
                    asn1EncodableVector53.add(new DERSequence(asn1EncodableVector55));
                    asn1EncodableVector55 = new ASN1EncodableVector();
                    asn1EncodableVector50.add(new DERSequence(asn1EncodableVector53));
                    asn1EncodableVector53 = new ASN1EncodableVector();
                }
            }
            asn1EncodableVector46.add(new DERSequence(asn1EncodableVector50));
            asn1EncodableVector50 = new ASN1EncodableVector();
            ASN1EncodableVector asn1EncodableVector56 = new ASN1EncodableVector();
            if (array18[n31].getRetain() != null) {
                for (int n42 = 0; n42 < array18[n31].getRetain().length; ++n42) {
                    for (int n43 = 0; n43 < array18[n31].getRetain()[n42].size(); ++n43) {
                        asn1EncodableVector56.add(new DEROctetString((byte[])array18[n31].getRetain()[n42].elementAt(n43)));
                    }
                    asn1EncodableVector51.add(new DERSequence(asn1EncodableVector56));
                    asn1EncodableVector56 = new ASN1EncodableVector();
                }
            }
            asn1EncodableVector46.add(new DERSequence(asn1EncodableVector51));
            asn1EncodableVector51 = new ASN1EncodableVector();
            asn1EncodableVector45.add(new DERSequence(asn1EncodableVector46));
            asn1EncodableVector46 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector45));
        final ASN1EncodableVector asn1EncodableVector57 = new ASN1EncodableVector();
        for (int n44 = 0; n44 < array19.length; ++n44) {
            asn1EncodableVector57.add(new DEROctetString(array19[n44]));
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector57));
        final ASN1EncodableVector asn1EncodableVector58 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector59 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector60 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector61 = new ASN1EncodableVector();
        ASN1EncodableVector asn1EncodableVector62 = new ASN1EncodableVector();
        for (int n45 = 0; n45 < array20.length; ++n45) {
            asn1EncodableVector59.add(new DERSequence(array21[0]));
            final ASN1EncodableVector asn1EncodableVector63 = new ASN1EncodableVector();
            asn1EncodableVector61.add(new DEROctetString(array20[n45].getStatByte()[0]));
            asn1EncodableVector61.add(new DEROctetString(array20[n45].getStatByte()[1]));
            asn1EncodableVector61.add(new DEROctetString(array20[n45].getStatByte()[2]));
            asn1EncodableVector61.add(new DEROctetString(array20[n45].getStatByte()[3]));
            asn1EncodableVector61.add(new DEROctetString(array20[n45].getStatByte()[4]));
            asn1EncodableVector59.add(new DERSequence(asn1EncodableVector61));
            asn1EncodableVector61 = new ASN1EncodableVector();
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[0]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[1]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[2]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[3]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[4]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[5]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[6]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[7]));
            asn1EncodableVector62.add(new ASN1Integer(array20[n45].getStatInt()[8]));
            asn1EncodableVector59.add(new DERSequence(asn1EncodableVector62));
            asn1EncodableVector62 = new ASN1EncodableVector();
            asn1EncodableVector58.add(new DERSequence(asn1EncodableVector59));
            asn1EncodableVector59 = new ASN1EncodableVector();
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector58));
        final ASN1EncodableVector asn1EncodableVector64 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector65 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector66 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector67 = new ASN1EncodableVector();
        for (int n46 = 0; n46 < gmssParameters.getHeightOfTrees().length; ++n46) {
            asn1EncodableVector65.add(new ASN1Integer(gmssParameters.getHeightOfTrees()[n46]));
            asn1EncodableVector66.add(new ASN1Integer(gmssParameters.getWinternitzParameter()[n46]));
            asn1EncodableVector67.add(new ASN1Integer(gmssParameters.getK()[n46]));
        }
        asn1EncodableVector64.add(new ASN1Integer(gmssParameters.getNumOfLayers()));
        asn1EncodableVector64.add(new DERSequence(asn1EncodableVector65));
        asn1EncodableVector64.add(new DERSequence(asn1EncodableVector66));
        asn1EncodableVector64.add(new DERSequence(asn1EncodableVector67));
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector64));
        final ASN1EncodableVector asn1EncodableVector68 = new ASN1EncodableVector();
        for (int n47 = 0; n47 < array21.length; ++n47) {
            asn1EncodableVector68.add(array21[n47]);
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector68));
        return new DERSequence(asn1EncodableVector);
    }
    
    private static int checkBigIntegerInIntRange(final ASN1Encodable asn1Encodable) {
        final BigInteger value = ((ASN1Integer)asn1Encodable).getValue();
        if (value.compareTo(BigInteger.valueOf(2147483647L)) > 0 || value.compareTo(BigInteger.valueOf(-2147483648L)) < 0) {
            throw new IllegalArgumentException("BigInteger not in Range: " + value.toString());
        }
        return value.intValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.primitive;
    }
}
