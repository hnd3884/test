package sun.security.krb5;

import sun.security.krb5.internal.PAData;
import sun.security.util.DerInputStream;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KDCReq;

abstract class KrbKdcRep
{
    static void check(final boolean b, final KDCReq kdcReq, final KDCRep kdcRep, final EncryptionKey encryptionKey) throws KrbApErrException {
        if (b && !kdcReq.reqBody.cname.equals(kdcRep.cname) && ((!kdcReq.reqBody.kdcOptions.get(15) && kdcReq.reqBody.cname.getNameType() != 10) || !kdcRep.encKDCRepPart.flags.get(15))) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
        }
        if (!kdcReq.reqBody.sname.equals(kdcRep.encKDCRepPart.sname)) {
            final String[] nameStrings = kdcRep.encKDCRepPart.sname.getNameStrings();
            if (b || !kdcReq.reqBody.kdcOptions.get(15) || nameStrings == null || nameStrings.length != 2 || !nameStrings[0].equals("krbtgt") || !kdcRep.encKDCRepPart.sname.getRealmString().equals(kdcReq.reqBody.sname.getRealmString())) {
                kdcRep.encKDCRepPart.key.destroy();
                throw new KrbApErrException(41);
            }
        }
        if (kdcReq.reqBody.getNonce() != kdcRep.encKDCRepPart.nonce) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
        }
        if (kdcReq.reqBody.addresses != null && kdcRep.encKDCRepPart.caddr != null && !kdcReq.reqBody.addresses.equals(kdcRep.encKDCRepPart.caddr)) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
        }
        for (int i = 2; i < 6; ++i) {
            if (kdcReq.reqBody.kdcOptions.get(i) != kdcRep.encKDCRepPart.flags.get(i)) {
                if (Krb5.DEBUG) {
                    System.out.println("> KrbKdcRep.check: at #" + i + ". request for " + kdcReq.reqBody.kdcOptions.get(i) + ", received " + kdcRep.encKDCRepPart.flags.get(i));
                }
                throw new KrbApErrException(41);
            }
        }
        if (kdcReq.reqBody.kdcOptions.get(8) && !kdcRep.encKDCRepPart.flags.get(8)) {
            throw new KrbApErrException(41);
        }
        if ((kdcReq.reqBody.from == null || kdcReq.reqBody.from.isZero()) && kdcRep.encKDCRepPart.starttime != null && !kdcRep.encKDCRepPart.starttime.inClockSkew()) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(37);
        }
        if (kdcReq.reqBody.from != null && !kdcReq.reqBody.from.isZero() && kdcRep.encKDCRepPart.starttime != null && !kdcReq.reqBody.from.equals(kdcRep.encKDCRepPart.starttime)) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
        }
        if (!kdcReq.reqBody.till.isZero() && kdcRep.encKDCRepPart.endtime.greaterThan(kdcReq.reqBody.till)) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
        }
        if (kdcRep.encKDCRepPart.flags.get(15)) {
            boolean b2 = false;
            boolean verifyAnyChecksum = false;
            if (kdcReq.pAData != null) {
                final PAData[] paData = kdcReq.pAData;
                for (int length = paData.length, j = 0; j < length; ++j) {
                    if (paData[j].getType() == 149) {
                        b2 = true;
                        break;
                    }
                }
            }
            if (kdcRep.encKDCRepPart.pAData != null) {
                for (final PAData paData3 : kdcRep.encKDCRepPart.pAData) {
                    if (paData3.getType() == 149) {
                        try {
                            verifyAnyChecksum = new Checksum(new DerInputStream(paData3.getValue()).getDerValue()).verifyAnyChecksum(kdcReq.asn1Encode(), encryptionKey, 56);
                        }
                        catch (final Exception ex) {
                            if (Krb5.DEBUG) {
                                ex.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }
            if (b2 && !verifyAnyChecksum) {
                throw new KrbApErrException(41);
            }
        }
        if (kdcReq.reqBody.kdcOptions.get(8) && kdcReq.reqBody.rtime != null && !kdcReq.reqBody.rtime.isZero() && (kdcRep.encKDCRepPart.renewTill == null || kdcRep.encKDCRepPart.renewTill.greaterThan(kdcReq.reqBody.rtime))) {
            kdcRep.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
        }
    }
}
