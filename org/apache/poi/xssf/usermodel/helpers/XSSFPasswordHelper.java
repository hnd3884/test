package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.util.StringUtil;
import javax.xml.namespace.QName;
import java.util.Arrays;
import org.apache.xmlbeans.XmlCursor;
import java.util.Base64;
import java.security.SecureRandom;
import java.util.Locale;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;

@Internal(since = "3.15 beta 3")
public final class XSSFPasswordHelper
{
    private XSSFPasswordHelper() {
    }
    
    public static void setPassword(final XmlObject xobj, final String password, final HashAlgorithm hashAlgo, final String prefix) {
        final XmlCursor cur = xobj.newCursor();
        if (password == null) {
            cur.removeAttribute(getAttrName(prefix, "password"));
            cur.removeAttribute(getAttrName(prefix, "algorithmName"));
            cur.removeAttribute(getAttrName(prefix, "hashValue"));
            cur.removeAttribute(getAttrName(prefix, "saltValue"));
            cur.removeAttribute(getAttrName(prefix, "spinCount"));
            return;
        }
        cur.toFirstContentToken();
        if (hashAlgo == null) {
            final int hash = CryptoFunctions.createXorVerifier1(password);
            cur.insertAttributeWithValue(getAttrName(prefix, "password"), String.format(Locale.ROOT, "%04X", hash).toUpperCase(Locale.ROOT));
        }
        else {
            final SecureRandom random = new SecureRandom();
            final byte[] salt = random.generateSeed(16);
            final int spinCount = 100000;
            final byte[] hash2 = CryptoFunctions.hashPassword(password, hashAlgo, salt, spinCount, false);
            final Base64.Encoder enc64 = Base64.getEncoder();
            cur.insertAttributeWithValue(getAttrName(prefix, "algorithmName"), hashAlgo.jceId);
            cur.insertAttributeWithValue(getAttrName(prefix, "hashValue"), enc64.encodeToString(hash2));
            cur.insertAttributeWithValue(getAttrName(prefix, "saltValue"), enc64.encodeToString(salt));
            cur.insertAttributeWithValue(getAttrName(prefix, "spinCount"), "" + spinCount);
        }
        cur.dispose();
    }
    
    public static boolean validatePassword(final XmlObject xobj, final String password, final String prefix) {
        if (password == null) {
            return false;
        }
        final XmlCursor cur = xobj.newCursor();
        final String xorHashVal = cur.getAttributeText(getAttrName(prefix, "password"));
        final String algoName = cur.getAttributeText(getAttrName(prefix, "algorithmName"));
        final String hashVal = cur.getAttributeText(getAttrName(prefix, "hashValue"));
        final String saltVal = cur.getAttributeText(getAttrName(prefix, "saltValue"));
        final String spinCount = cur.getAttributeText(getAttrName(prefix, "spinCount"));
        cur.dispose();
        if (xorHashVal != null) {
            final int hash1 = Integer.parseInt(xorHashVal, 16);
            final int hash2 = CryptoFunctions.createXorVerifier1(password);
            return hash1 == hash2;
        }
        if (hashVal == null || algoName == null || saltVal == null || spinCount == null) {
            return false;
        }
        final Base64.Decoder dec64 = Base64.getDecoder();
        final byte[] hash3 = dec64.decode(hashVal);
        final HashAlgorithm hashAlgo = HashAlgorithm.fromString(algoName);
        final byte[] salt = dec64.decode(saltVal);
        final int spinCnt = Integer.parseInt(spinCount);
        final byte[] hash4 = CryptoFunctions.hashPassword(password, hashAlgo, salt, spinCnt, false);
        return Arrays.equals(hash3, hash4);
    }
    
    private static QName getAttrName(final String prefix, final String name) {
        if (prefix == null || prefix.isEmpty()) {
            return new QName(name);
        }
        return new QName(prefix + StringUtil.toUpperCase(name.charAt(0)) + name.substring(1));
    }
}
