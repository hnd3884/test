package org.bouncycastle.asn1.x509;

import org.bouncycastle.util.Strings;
import java.util.Collections;
import org.bouncycastle.util.Arrays;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.Iterator;
import java.util.Map;
import org.bouncycastle.util.Integers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.DERIA5String;
import java.util.HashSet;
import java.util.Set;

public class PKIXNameConstraintValidator implements NameConstraintValidator
{
    private Set excludedSubtreesDN;
    private Set excludedSubtreesDNS;
    private Set excludedSubtreesEmail;
    private Set excludedSubtreesURI;
    private Set excludedSubtreesIP;
    private Set permittedSubtreesDN;
    private Set permittedSubtreesDNS;
    private Set permittedSubtreesEmail;
    private Set permittedSubtreesURI;
    private Set permittedSubtreesIP;
    
    public PKIXNameConstraintValidator() {
        this.excludedSubtreesDN = new HashSet();
        this.excludedSubtreesDNS = new HashSet();
        this.excludedSubtreesEmail = new HashSet();
        this.excludedSubtreesURI = new HashSet();
        this.excludedSubtreesIP = new HashSet();
    }
    
    public void checkPermitted(final GeneralName generalName) throws NameConstraintValidatorException {
        switch (generalName.getTagNo()) {
            case 1: {
                this.checkPermittedEmail(this.permittedSubtreesEmail, this.extractNameAsString(generalName));
                break;
            }
            case 2: {
                this.checkPermittedDNS(this.permittedSubtreesDNS, DERIA5String.getInstance(generalName.getName()).getString());
                break;
            }
            case 4: {
                this.checkPermittedDN(X500Name.getInstance(generalName.getName()));
                break;
            }
            case 6: {
                this.checkPermittedURI(this.permittedSubtreesURI, DERIA5String.getInstance(generalName.getName()).getString());
                break;
            }
            case 7: {
                this.checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
                break;
            }
        }
    }
    
    public void checkExcluded(final GeneralName generalName) throws NameConstraintValidatorException {
        switch (generalName.getTagNo()) {
            case 1: {
                this.checkExcludedEmail(this.excludedSubtreesEmail, this.extractNameAsString(generalName));
                break;
            }
            case 2: {
                this.checkExcludedDNS(this.excludedSubtreesDNS, DERIA5String.getInstance(generalName.getName()).getString());
                break;
            }
            case 4: {
                this.checkExcludedDN(X500Name.getInstance(generalName.getName()));
                break;
            }
            case 6: {
                this.checkExcludedURI(this.excludedSubtreesURI, DERIA5String.getInstance(generalName.getName()).getString());
                break;
            }
            case 7: {
                this.checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(generalName.getName()).getOctets());
                break;
            }
        }
    }
    
    public void intersectPermittedSubtree(final GeneralSubtree generalSubtree) {
        this.intersectPermittedSubtree(new GeneralSubtree[] { generalSubtree });
    }
    
    public void intersectPermittedSubtree(final GeneralSubtree[] array) {
        final HashMap hashMap = new HashMap();
        for (int i = 0; i != array.length; ++i) {
            final GeneralSubtree generalSubtree = array[i];
            final Integer value = Integers.valueOf(generalSubtree.getBase().getTagNo());
            if (hashMap.get(value) == null) {
                hashMap.put(value, new HashSet());
            }
            ((Set)hashMap.get(value)).add(generalSubtree);
        }
        for (final Map.Entry entry : hashMap.entrySet()) {
            switch ((int)entry.getKey()) {
                case 1: {
                    this.permittedSubtreesEmail = this.intersectEmail(this.permittedSubtreesEmail, (Set)entry.getValue());
                    continue;
                }
                case 2: {
                    this.permittedSubtreesDNS = this.intersectDNS(this.permittedSubtreesDNS, (Set)entry.getValue());
                    continue;
                }
                case 4: {
                    this.permittedSubtreesDN = this.intersectDN(this.permittedSubtreesDN, (Set)entry.getValue());
                    continue;
                }
                case 6: {
                    this.permittedSubtreesURI = this.intersectURI(this.permittedSubtreesURI, (Set)entry.getValue());
                    continue;
                }
                case 7: {
                    this.permittedSubtreesIP = this.intersectIP(this.permittedSubtreesIP, (Set)entry.getValue());
                    continue;
                }
            }
        }
    }
    
    public void intersectEmptyPermittedSubtree(final int n) {
        switch (n) {
            case 1: {
                this.permittedSubtreesEmail = new HashSet();
                break;
            }
            case 2: {
                this.permittedSubtreesDNS = new HashSet();
                break;
            }
            case 4: {
                this.permittedSubtreesDN = new HashSet();
                break;
            }
            case 6: {
                this.permittedSubtreesURI = new HashSet();
                break;
            }
            case 7: {
                this.permittedSubtreesIP = new HashSet();
                break;
            }
        }
    }
    
    public void addExcludedSubtree(final GeneralSubtree generalSubtree) {
        final GeneralName base = generalSubtree.getBase();
        switch (base.getTagNo()) {
            case 1: {
                this.excludedSubtreesEmail = this.unionEmail(this.excludedSubtreesEmail, this.extractNameAsString(base));
                break;
            }
            case 2: {
                this.excludedSubtreesDNS = this.unionDNS(this.excludedSubtreesDNS, this.extractNameAsString(base));
                break;
            }
            case 4: {
                this.excludedSubtreesDN = this.unionDN(this.excludedSubtreesDN, (ASN1Sequence)base.getName().toASN1Primitive());
                break;
            }
            case 6: {
                this.excludedSubtreesURI = this.unionURI(this.excludedSubtreesURI, this.extractNameAsString(base));
                break;
            }
            case 7: {
                this.excludedSubtreesIP = this.unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(base.getName()).getOctets());
                break;
            }
        }
    }
    
    @Override
    public int hashCode() {
        return this.hashCollection(this.excludedSubtreesDN) + this.hashCollection(this.excludedSubtreesDNS) + this.hashCollection(this.excludedSubtreesEmail) + this.hashCollection(this.excludedSubtreesIP) + this.hashCollection(this.excludedSubtreesURI) + this.hashCollection(this.permittedSubtreesDN) + this.hashCollection(this.permittedSubtreesDNS) + this.hashCollection(this.permittedSubtreesEmail) + this.hashCollection(this.permittedSubtreesIP) + this.hashCollection(this.permittedSubtreesURI);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PKIXNameConstraintValidator)) {
            return false;
        }
        final PKIXNameConstraintValidator pkixNameConstraintValidator = (PKIXNameConstraintValidator)o;
        return this.collectionsAreEqual(pkixNameConstraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && this.collectionsAreEqual(pkixNameConstraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && this.collectionsAreEqual(pkixNameConstraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && this.collectionsAreEqual(pkixNameConstraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && this.collectionsAreEqual(pkixNameConstraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && this.collectionsAreEqual(pkixNameConstraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && this.collectionsAreEqual(pkixNameConstraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && this.collectionsAreEqual(pkixNameConstraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && this.collectionsAreEqual(pkixNameConstraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && this.collectionsAreEqual(pkixNameConstraintValidator.permittedSubtreesURI, this.permittedSubtreesURI);
    }
    
    @Override
    public String toString() {
        String s = "" + "permitted:\n";
        if (this.permittedSubtreesDN != null) {
            s = s + "DN:\n" + this.permittedSubtreesDN.toString() + "\n";
        }
        if (this.permittedSubtreesDNS != null) {
            s = s + "DNS:\n" + this.permittedSubtreesDNS.toString() + "\n";
        }
        if (this.permittedSubtreesEmail != null) {
            s = s + "Email:\n" + this.permittedSubtreesEmail.toString() + "\n";
        }
        if (this.permittedSubtreesURI != null) {
            s = s + "URI:\n" + this.permittedSubtreesURI.toString() + "\n";
        }
        if (this.permittedSubtreesIP != null) {
            s = s + "IP:\n" + this.stringifyIPCollection(this.permittedSubtreesIP) + "\n";
        }
        String s2 = s + "excluded:\n";
        if (!this.excludedSubtreesDN.isEmpty()) {
            s2 = s2 + "DN:\n" + this.excludedSubtreesDN.toString() + "\n";
        }
        if (!this.excludedSubtreesDNS.isEmpty()) {
            s2 = s2 + "DNS:\n" + this.excludedSubtreesDNS.toString() + "\n";
        }
        if (!this.excludedSubtreesEmail.isEmpty()) {
            s2 = s2 + "Email:\n" + this.excludedSubtreesEmail.toString() + "\n";
        }
        if (!this.excludedSubtreesURI.isEmpty()) {
            s2 = s2 + "URI:\n" + this.excludedSubtreesURI.toString() + "\n";
        }
        if (!this.excludedSubtreesIP.isEmpty()) {
            s2 = s2 + "IP:\n" + this.stringifyIPCollection(this.excludedSubtreesIP) + "\n";
        }
        return s2;
    }
    
    private void checkPermittedDN(final X500Name x500Name) throws NameConstraintValidatorException {
        this.checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(x500Name.toASN1Primitive()));
    }
    
    private void checkExcludedDN(final X500Name x500Name) throws NameConstraintValidatorException {
        this.checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(x500Name));
    }
    
    private static boolean withinDNSubtree(final ASN1Sequence asn1Sequence, final ASN1Sequence asn1Sequence2) {
        if (asn1Sequence2.size() < 1) {
            return false;
        }
        if (asn1Sequence2.size() > asn1Sequence.size()) {
            return false;
        }
        for (int i = asn1Sequence2.size() - 1; i >= 0; --i) {
            if (!asn1Sequence2.getObjectAt(i).equals(asn1Sequence.getObjectAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private void checkPermittedDN(final Set set, final ASN1Sequence asn1Sequence) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        if (set.isEmpty() && asn1Sequence.size() == 0) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (withinDNSubtree(asn1Sequence, (ASN1Sequence)iterator.next())) {
                return;
            }
        }
        throw new NameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
    }
    
    private void checkExcludedDN(final Set set, final ASN1Sequence asn1Sequence) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (withinDNSubtree(asn1Sequence, (ASN1Sequence)iterator.next())) {
                throw new NameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
            }
        }
    }
    
    private Set intersectDN(final Set set, final Set set2) {
        final HashSet set3 = new HashSet();
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            final ASN1Sequence instance = ASN1Sequence.getInstance(((GeneralSubtree)iterator.next()).getBase().getName().toASN1Primitive());
            if (set == null) {
                if (instance == null) {
                    continue;
                }
                set3.add(instance);
            }
            else {
                for (final ASN1Sequence asn1Sequence : set) {
                    if (withinDNSubtree(instance, asn1Sequence)) {
                        set3.add(instance);
                    }
                    else {
                        if (!withinDNSubtree(asn1Sequence, instance)) {
                            continue;
                        }
                        set3.add(asn1Sequence);
                    }
                }
            }
        }
        return set3;
    }
    
    private Set unionDN(final Set set, final ASN1Sequence asn1Sequence) {
        if (!set.isEmpty()) {
            final HashSet set2 = new HashSet();
            for (final ASN1Sequence asn1Sequence2 : set) {
                if (withinDNSubtree(asn1Sequence, asn1Sequence2)) {
                    set2.add(asn1Sequence2);
                }
                else if (withinDNSubtree(asn1Sequence2, asn1Sequence)) {
                    set2.add(asn1Sequence);
                }
                else {
                    set2.add(asn1Sequence2);
                    set2.add(asn1Sequence);
                }
            }
            return set2;
        }
        if (asn1Sequence == null) {
            return set;
        }
        set.add(asn1Sequence);
        return set;
    }
    
    private Set intersectEmail(final Set set, final Set set2) {
        final HashSet set3 = new HashSet();
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            final String nameAsString = this.extractNameAsString(((GeneralSubtree)iterator.next()).getBase());
            if (set == null) {
                if (nameAsString == null) {
                    continue;
                }
                set3.add(nameAsString);
            }
            else {
                final Iterator iterator2 = set.iterator();
                while (iterator2.hasNext()) {
                    this.intersectEmail(nameAsString, (String)iterator2.next(), set3);
                }
            }
        }
        return set3;
    }
    
    private Set unionEmail(final Set set, final String s) {
        if (!set.isEmpty()) {
            final HashSet set2 = new HashSet();
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                this.unionEmail((String)iterator.next(), s, set2);
            }
            return set2;
        }
        if (s == null) {
            return set;
        }
        set.add(s);
        return set;
    }
    
    private Set intersectIP(final Set set, final Set set2) {
        final HashSet set3 = new HashSet();
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            final byte[] octets = ASN1OctetString.getInstance(((GeneralSubtree)iterator.next()).getBase().getName()).getOctets();
            if (set == null) {
                if (octets == null) {
                    continue;
                }
                set3.add(octets);
            }
            else {
                final Iterator iterator2 = set.iterator();
                while (iterator2.hasNext()) {
                    set3.addAll(this.intersectIPRange((byte[])iterator2.next(), octets));
                }
            }
        }
        return set3;
    }
    
    private Set unionIP(final Set set, final byte[] array) {
        if (!set.isEmpty()) {
            final HashSet set2 = new HashSet();
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                set2.addAll(this.unionIPRange((byte[])iterator.next(), array));
            }
            return set2;
        }
        if (array == null) {
            return set;
        }
        set.add(array);
        return set;
    }
    
    private Set unionIPRange(final byte[] array, final byte[] array2) {
        final HashSet set = new HashSet();
        if (Arrays.areEqual(array, array2)) {
            set.add(array);
        }
        else {
            set.add(array);
            set.add(array2);
        }
        return set;
    }
    
    private Set intersectIPRange(final byte[] array, final byte[] array2) {
        if (array.length != array2.length) {
            return Collections.EMPTY_SET;
        }
        final byte[][] iPsAndSubnetMasks = this.extractIPsAndSubnetMasks(array, array2);
        final byte[] array3 = iPsAndSubnetMasks[0];
        final byte[] array4 = iPsAndSubnetMasks[1];
        final byte[] array5 = iPsAndSubnetMasks[2];
        final byte[] array6 = iPsAndSubnetMasks[3];
        final byte[][] minMaxIPs = this.minMaxIPs(array3, array4, array5, array6);
        if (compareTo(max(minMaxIPs[0], minMaxIPs[2]), min(minMaxIPs[1], minMaxIPs[3])) == 1) {
            return Collections.EMPTY_SET;
        }
        return Collections.singleton(this.ipWithSubnetMask(or(minMaxIPs[0], minMaxIPs[2]), or(array4, array6)));
    }
    
    private byte[] ipWithSubnetMask(final byte[] array, final byte[] array2) {
        final int length = array.length;
        final byte[] array3 = new byte[length * 2];
        System.arraycopy(array, 0, array3, 0, length);
        System.arraycopy(array2, 0, array3, length, length);
        return array3;
    }
    
    private byte[][] extractIPsAndSubnetMasks(final byte[] array, final byte[] array2) {
        final int n = array.length / 2;
        final byte[] array3 = new byte[n];
        final byte[] array4 = new byte[n];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array, n, array4, 0, n);
        final byte[] array5 = new byte[n];
        final byte[] array6 = new byte[n];
        System.arraycopy(array2, 0, array5, 0, n);
        System.arraycopy(array2, n, array6, 0, n);
        return new byte[][] { array3, array4, array5, array6 };
    }
    
    private byte[][] minMaxIPs(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        final int length = array.length;
        final byte[] array5 = new byte[length];
        final byte[] array6 = new byte[length];
        final byte[] array7 = new byte[length];
        final byte[] array8 = new byte[length];
        for (int i = 0; i < length; ++i) {
            array5[i] = (byte)(array[i] & array2[i]);
            array6[i] = (byte)((array[i] & array2[i]) | ~array2[i]);
            array7[i] = (byte)(array3[i] & array4[i]);
            array8[i] = (byte)((array3[i] & array4[i]) | ~array4[i]);
        }
        return new byte[][] { array5, array6, array7, array8 };
    }
    
    private void checkPermittedEmail(final Set set, final String s) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (this.emailIsConstrained(s, (String)iterator.next())) {
                return;
            }
        }
        if (s.length() == 0 && set.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("Subject email address is not from a permitted subtree.");
    }
    
    private void checkExcludedEmail(final Set set, final String s) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (this.emailIsConstrained(s, (String)iterator.next())) {
                throw new NameConstraintValidatorException("Email address is from an excluded subtree.");
            }
        }
    }
    
    private void checkPermittedIP(final Set set, final byte[] array) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (this.isIPConstrained(array, (byte[])iterator.next())) {
                return;
            }
        }
        if (array.length == 0 && set.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("IP is not from a permitted subtree.");
    }
    
    private void checkExcludedIP(final Set set, final byte[] array) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (this.isIPConstrained(array, (byte[])iterator.next())) {
                throw new NameConstraintValidatorException("IP is from an excluded subtree.");
            }
        }
    }
    
    private boolean isIPConstrained(final byte[] array, final byte[] array2) {
        final int length = array.length;
        if (length != array2.length / 2) {
            return false;
        }
        final byte[] array3 = new byte[length];
        System.arraycopy(array2, length, array3, 0, length);
        final byte[] array4 = new byte[length];
        final byte[] array5 = new byte[length];
        for (int i = 0; i < length; ++i) {
            array4[i] = (byte)(array2[i] & array3[i]);
            array5[i] = (byte)(array[i] & array3[i]);
        }
        return Arrays.areEqual(array4, array5);
    }
    
    private boolean emailIsConstrained(final String s, final String s2) {
        final String substring = s.substring(s.indexOf(64) + 1);
        if (s2.indexOf(64) != -1) {
            if (s.equalsIgnoreCase(s2)) {
                return true;
            }
        }
        else if (s2.charAt(0) != '.') {
            if (substring.equalsIgnoreCase(s2)) {
                return true;
            }
        }
        else if (this.withinDomain(substring, s2)) {
            return true;
        }
        return false;
    }
    
    private boolean withinDomain(final String s, final String s2) {
        String substring = s2;
        if (substring.startsWith(".")) {
            substring = substring.substring(1);
        }
        final String[] split = Strings.split(substring, '.');
        final String[] split2 = Strings.split(s, '.');
        if (split2.length <= split.length) {
            return false;
        }
        final int n = split2.length - split.length;
        for (int i = -1; i < split.length; ++i) {
            if (i == -1) {
                if (split2[i + n].equals("")) {
                    return false;
                }
            }
            else if (!split[i].equalsIgnoreCase(split2[i + n])) {
                return false;
            }
        }
        return true;
    }
    
    private void checkPermittedDNS(final Set set, final String s) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        for (final String s2 : set) {
            if (this.withinDomain(s, s2) || s.equalsIgnoreCase(s2)) {
                return;
            }
        }
        if (s.length() == 0 && set.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("DNS is not from a permitted subtree.");
    }
    
    private void checkExcludedDNS(final Set set, final String s) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        for (final String s2 : set) {
            if (this.withinDomain(s, s2) || s.equalsIgnoreCase(s2)) {
                throw new NameConstraintValidatorException("DNS is from an excluded subtree.");
            }
        }
    }
    
    private void unionEmail(final String s, final String s2, final Set set) {
        if (s.indexOf(64) != -1) {
            final String substring = s.substring(s.indexOf(64) + 1);
            if (s2.indexOf(64) != -1) {
                if (s.equalsIgnoreCase(s2)) {
                    set.add(s);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(substring, s2)) {
                    set.add(s2);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (substring.equalsIgnoreCase(s2)) {
                set.add(s2);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s.startsWith(".")) {
            if (s2.indexOf(64) != -1) {
                if (this.withinDomain(s2.substring(s.indexOf(64) + 1), s)) {
                    set.add(s);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(s, s2) || s.equalsIgnoreCase(s2)) {
                    set.add(s2);
                }
                else if (this.withinDomain(s2, s)) {
                    set.add(s);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (this.withinDomain(s2, s)) {
                set.add(s);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s2.indexOf(64) != -1) {
            if (s2.substring(s.indexOf(64) + 1).equalsIgnoreCase(s)) {
                set.add(s);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s2.startsWith(".")) {
            if (this.withinDomain(s, s2)) {
                set.add(s2);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s.equalsIgnoreCase(s2)) {
            set.add(s);
        }
        else {
            set.add(s);
            set.add(s2);
        }
    }
    
    private void unionURI(final String s, final String s2, final Set set) {
        if (s.indexOf(64) != -1) {
            final String substring = s.substring(s.indexOf(64) + 1);
            if (s2.indexOf(64) != -1) {
                if (s.equalsIgnoreCase(s2)) {
                    set.add(s);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(substring, s2)) {
                    set.add(s2);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (substring.equalsIgnoreCase(s2)) {
                set.add(s2);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s.startsWith(".")) {
            if (s2.indexOf(64) != -1) {
                if (this.withinDomain(s2.substring(s.indexOf(64) + 1), s)) {
                    set.add(s);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(s, s2) || s.equalsIgnoreCase(s2)) {
                    set.add(s2);
                }
                else if (this.withinDomain(s2, s)) {
                    set.add(s);
                }
                else {
                    set.add(s);
                    set.add(s2);
                }
            }
            else if (this.withinDomain(s2, s)) {
                set.add(s);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s2.indexOf(64) != -1) {
            if (s2.substring(s.indexOf(64) + 1).equalsIgnoreCase(s)) {
                set.add(s);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s2.startsWith(".")) {
            if (this.withinDomain(s, s2)) {
                set.add(s2);
            }
            else {
                set.add(s);
                set.add(s2);
            }
        }
        else if (s.equalsIgnoreCase(s2)) {
            set.add(s);
        }
        else {
            set.add(s);
            set.add(s2);
        }
    }
    
    private Set intersectDNS(final Set set, final Set set2) {
        final HashSet set3 = new HashSet();
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            final String nameAsString = this.extractNameAsString(((GeneralSubtree)iterator.next()).getBase());
            if (set == null) {
                if (nameAsString == null) {
                    continue;
                }
                set3.add(nameAsString);
            }
            else {
                for (final String s : set) {
                    if (this.withinDomain(s, nameAsString)) {
                        set3.add(s);
                    }
                    else {
                        if (!this.withinDomain(nameAsString, s)) {
                            continue;
                        }
                        set3.add(nameAsString);
                    }
                }
            }
        }
        return set3;
    }
    
    private Set unionDNS(final Set set, final String s) {
        if (!set.isEmpty()) {
            final HashSet set2 = new HashSet();
            for (final String s2 : set) {
                if (this.withinDomain(s2, s)) {
                    set2.add(s);
                }
                else if (this.withinDomain(s, s2)) {
                    set2.add(s2);
                }
                else {
                    set2.add(s2);
                    set2.add(s);
                }
            }
            return set2;
        }
        if (s == null) {
            return set;
        }
        set.add(s);
        return set;
    }
    
    private void intersectEmail(final String s, final String s2, final Set set) {
        if (s.indexOf(64) != -1) {
            final String substring = s.substring(s.indexOf(64) + 1);
            if (s2.indexOf(64) != -1) {
                if (s.equalsIgnoreCase(s2)) {
                    set.add(s);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(substring, s2)) {
                    set.add(s);
                }
            }
            else if (substring.equalsIgnoreCase(s2)) {
                set.add(s);
            }
        }
        else if (s.startsWith(".")) {
            if (s2.indexOf(64) != -1) {
                if (this.withinDomain(s2.substring(s.indexOf(64) + 1), s)) {
                    set.add(s2);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(s, s2) || s.equalsIgnoreCase(s2)) {
                    set.add(s);
                }
                else if (this.withinDomain(s2, s)) {
                    set.add(s2);
                }
            }
            else if (this.withinDomain(s2, s)) {
                set.add(s2);
            }
        }
        else if (s2.indexOf(64) != -1) {
            if (s2.substring(s2.indexOf(64) + 1).equalsIgnoreCase(s)) {
                set.add(s2);
            }
        }
        else if (s2.startsWith(".")) {
            if (this.withinDomain(s, s2)) {
                set.add(s);
            }
        }
        else if (s.equalsIgnoreCase(s2)) {
            set.add(s);
        }
    }
    
    private void checkExcludedURI(final Set set, final String s) throws NameConstraintValidatorException {
        if (set.isEmpty()) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (this.isUriConstrained(s, (String)iterator.next())) {
                throw new NameConstraintValidatorException("URI is from an excluded subtree.");
            }
        }
    }
    
    private Set intersectURI(final Set set, final Set set2) {
        final HashSet set3 = new HashSet();
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            final String nameAsString = this.extractNameAsString(((GeneralSubtree)iterator.next()).getBase());
            if (set == null) {
                if (nameAsString == null) {
                    continue;
                }
                set3.add(nameAsString);
            }
            else {
                final Iterator iterator2 = set.iterator();
                while (iterator2.hasNext()) {
                    this.intersectURI((String)iterator2.next(), nameAsString, set3);
                }
            }
        }
        return set3;
    }
    
    private Set unionURI(final Set set, final String s) {
        if (!set.isEmpty()) {
            final HashSet set2 = new HashSet();
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                this.unionURI((String)iterator.next(), s, set2);
            }
            return set2;
        }
        if (s == null) {
            return set;
        }
        set.add(s);
        return set;
    }
    
    private void intersectURI(final String s, final String s2, final Set set) {
        if (s.indexOf(64) != -1) {
            final String substring = s.substring(s.indexOf(64) + 1);
            if (s2.indexOf(64) != -1) {
                if (s.equalsIgnoreCase(s2)) {
                    set.add(s);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(substring, s2)) {
                    set.add(s);
                }
            }
            else if (substring.equalsIgnoreCase(s2)) {
                set.add(s);
            }
        }
        else if (s.startsWith(".")) {
            if (s2.indexOf(64) != -1) {
                if (this.withinDomain(s2.substring(s.indexOf(64) + 1), s)) {
                    set.add(s2);
                }
            }
            else if (s2.startsWith(".")) {
                if (this.withinDomain(s, s2) || s.equalsIgnoreCase(s2)) {
                    set.add(s);
                }
                else if (this.withinDomain(s2, s)) {
                    set.add(s2);
                }
            }
            else if (this.withinDomain(s2, s)) {
                set.add(s2);
            }
        }
        else if (s2.indexOf(64) != -1) {
            if (s2.substring(s2.indexOf(64) + 1).equalsIgnoreCase(s)) {
                set.add(s2);
            }
        }
        else if (s2.startsWith(".")) {
            if (this.withinDomain(s, s2)) {
                set.add(s);
            }
        }
        else if (s.equalsIgnoreCase(s2)) {
            set.add(s);
        }
    }
    
    private void checkPermittedURI(final Set set, final String s) throws NameConstraintValidatorException {
        if (set == null) {
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (this.isUriConstrained(s, (String)iterator.next())) {
                return;
            }
        }
        if (s.length() == 0 && set.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("URI is not from a permitted subtree.");
    }
    
    private boolean isUriConstrained(final String s, final String s2) {
        final String hostFromURL = extractHostFromURL(s);
        if (!s2.startsWith(".")) {
            if (hostFromURL.equalsIgnoreCase(s2)) {
                return true;
            }
        }
        else if (this.withinDomain(hostFromURL, s2)) {
            return true;
        }
        return false;
    }
    
    private static String extractHostFromURL(final String s) {
        String s2 = s.substring(s.indexOf(58) + 1);
        if (s2.indexOf("//") != -1) {
            s2 = s2.substring(s2.indexOf("//") + 2);
        }
        if (s2.lastIndexOf(58) != -1) {
            s2 = s2.substring(0, s2.lastIndexOf(58));
        }
        final String substring = s2.substring(s2.indexOf(58) + 1);
        String s3 = substring.substring(substring.indexOf(64) + 1);
        if (s3.indexOf(47) != -1) {
            s3 = s3.substring(0, s3.indexOf(47));
        }
        return s3;
    }
    
    private String extractNameAsString(final GeneralName generalName) {
        return DERIA5String.getInstance(generalName.getName()).getString();
    }
    
    private static byte[] max(final byte[] array, final byte[] array2) {
        for (int i = 0; i < array.length; ++i) {
            if ((array[i] & 0xFFFF) > (array2[i] & 0xFFFF)) {
                return array;
            }
        }
        return array2;
    }
    
    private static byte[] min(final byte[] array, final byte[] array2) {
        for (int i = 0; i < array.length; ++i) {
            if ((array[i] & 0xFFFF) < (array2[i] & 0xFFFF)) {
                return array;
            }
        }
        return array2;
    }
    
    private static int compareTo(final byte[] array, final byte[] array2) {
        if (Arrays.areEqual(array, array2)) {
            return 0;
        }
        if (Arrays.areEqual(max(array, array2), array)) {
            return 1;
        }
        return -1;
    }
    
    private static byte[] or(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array3[i] = (byte)(array[i] | array2[i]);
        }
        return array3;
    }
    
    private int hashCollection(final Collection collection) {
        if (collection == null) {
            return 0;
        }
        int n = 0;
        for (final Object next : collection) {
            if (next instanceof byte[]) {
                n += Arrays.hashCode((byte[])next);
            }
            else {
                n += next.hashCode();
            }
        }
        return n;
    }
    
    private boolean collectionsAreEqual(final Collection collection, final Collection collection2) {
        if (collection == collection2) {
            return true;
        }
        if (collection == null || collection2 == null) {
            return false;
        }
        if (collection.size() != collection2.size()) {
            return false;
        }
        for (final Object next : collection) {
            final Iterator iterator2 = collection2.iterator();
            boolean b = false;
            while (iterator2.hasNext()) {
                if (this.equals(next, iterator2.next())) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equals(final Object o, final Object o2) {
        if (o == o2) {
            return true;
        }
        if (o == null || o2 == null) {
            return false;
        }
        if (o instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.areEqual((byte[])o, (byte[])o2);
        }
        return o.equals(o2);
    }
    
    private String stringifyIP(final byte[] array) {
        String string = "";
        for (int i = 0; i < array.length / 2; ++i) {
            string = string + Integer.toString(array[i] & 0xFF) + ".";
        }
        String s = string.substring(0, string.length() - 1) + "/";
        for (int j = array.length / 2; j < array.length; ++j) {
            s = s + Integer.toString(array[j] & 0xFF) + ".";
        }
        return s.substring(0, s.length() - 1);
    }
    
    private String stringifyIPCollection(final Set set) {
        String s = "" + "[";
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            s = s + this.stringifyIP((byte[])iterator.next()) + ",";
        }
        if (s.length() > 1) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "]";
    }
}
