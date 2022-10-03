package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ToCodeArgHelper
{
    private final List<String> argStrings;
    private final String comment;
    
    private ToCodeArgHelper(final String argString, final String comment) {
        this.argStrings = Collections.singletonList(argString);
        this.comment = comment;
    }
    
    private ToCodeArgHelper(final List<String> argStrings, final String comment) {
        this.argStrings = argStrings;
        this.comment = comment;
    }
    
    public static ToCodeArgHelper createByte(final byte b, final boolean includeComment) {
        String s = "0x" + StaticUtils.toHex(b);
        if ((b & 0x80) != 0x0) {
            s = "(byte) " + s;
        }
        String comment;
        if (includeComment && StaticUtils.isPrintableString(new byte[] { b })) {
            comment = "\"" + (char)b + '\"';
        }
        else {
            comment = null;
        }
        return new ToCodeArgHelper(s, comment);
    }
    
    public static ToCodeArgHelper createByteArray(final byte[] b, final boolean includeComments, final String comment) {
        return new ToCodeArgHelper(getByteArrayLines(b, includeComments), comment);
    }
    
    private static List<String> getByteArrayLines(final byte[] b, final boolean includeComments) {
        if (b == null) {
            return Collections.singletonList("(byte[]) null");
        }
        if (b.length == 0) {
            return Collections.singletonList("new byte[0]");
        }
        final ArrayList<String> lines = new ArrayList<String>(3 + b.length);
        lines.add("new byte[]");
        lines.add("{");
        final byte[] oneByteString = { 0 };
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            buffer.setLength(0);
            buffer.append("  ");
            if ((b[i] & 0x80) != 0x0) {
                buffer.append("(byte) 0x");
                StaticUtils.toHex(b[i], buffer);
                if (i < b.length - 1) {
                    buffer.append(',');
                }
            }
            else {
                buffer.append("0x");
                StaticUtils.toHex(b[i], buffer);
                if (i < b.length - 1) {
                    buffer.append(',');
                }
                oneByteString[0] = b[i];
                if (includeComments && StaticUtils.isPrintableString(oneByteString)) {
                    buffer.append(" // \"");
                    buffer.append((char)b[i]);
                    buffer.append('\"');
                }
            }
            lines.add(buffer.toString());
        }
        lines.add("}");
        return lines;
    }
    
    public static ToCodeArgHelper createBoolean(final boolean b, final String comment) {
        return new ToCodeArgHelper(b ? "true" : "false", comment);
    }
    
    public static ToCodeArgHelper createInteger(final long i, final String comment) {
        String valueString = String.valueOf(i);
        if (i > 2147483647L || i < -2147483648L) {
            valueString += 'L';
        }
        return new ToCodeArgHelper(valueString, comment);
    }
    
    public static ToCodeArgHelper createString(final String s, final String comment) {
        if (s == null) {
            return new ToCodeArgHelper("(String) null", comment);
        }
        return new ToCodeArgHelper('\"' + s.replace("\"", "\\\"") + '\"', comment);
    }
    
    public static ToCodeArgHelper createASN1OctetString(final ASN1OctetString s, final String comment) {
        if (s == null) {
            return new ToCodeArgHelper("(ASN1OctetString) null", comment);
        }
        final ArrayList<String> lines = new ArrayList<String>(10);
        final boolean universalType = s.getType() == 4;
        final byte[] valueBytes = s.getValue();
        if (valueBytes.length == 0) {
            if (universalType) {
                lines.add("new ASN1OctetString()");
            }
            else {
                lines.add("new ASN1OctetString(");
                lines.add("     (byte) 0x" + StaticUtils.toHex(s.getType()) + ')');
            }
        }
        else {
            lines.add("new ASN1OctetString(");
            if (!universalType) {
                lines.add("     (byte) 0x" + StaticUtils.toHex(s.getType()) + ',');
            }
            final boolean isPrintable = StaticUtils.isPrintableString(valueBytes);
            if (isPrintable) {
                lines.add("     \"" + s.stringValue() + "\")");
            }
            else {
                final StringBuilder line = new StringBuilder();
                final Iterator<String> iterator = getByteArrayLines(valueBytes, true).iterator();
                while (iterator.hasNext()) {
                    line.setLength(0);
                    line.append("     ");
                    line.append(iterator.next());
                    if (!iterator.hasNext()) {
                        line.append(')');
                    }
                    lines.add(line.toString());
                }
            }
        }
        return new ToCodeArgHelper(lines, comment);
    }
    
    public static ToCodeArgHelper createModificationType(final ModificationType t, final String comment) {
        if (t == null) {
            return new ToCodeArgHelper("(ModificationType) null", comment);
        }
        final ModificationType definedType = ModificationType.definedValueOf(t.intValue());
        if (definedType == null) {
            return new ToCodeArgHelper("ModificationType.valueOf(" + t.intValue() + ')', comment);
        }
        return new ToCodeArgHelper("ModificationType." + definedType.getName(), comment);
    }
    
    public static ToCodeArgHelper createScope(final SearchScope s, final String comment) {
        if (s == null) {
            return new ToCodeArgHelper("(SearchScope) null", comment);
        }
        final SearchScope definedScope = SearchScope.definedValueOf(s.intValue());
        if (definedScope == null) {
            return new ToCodeArgHelper("SearchScope.valueOf(" + s.intValue() + ')', comment);
        }
        return new ToCodeArgHelper("SearchScope." + definedScope.getName(), comment);
    }
    
    public static ToCodeArgHelper createDerefPolicy(final DereferencePolicy p, final String comment) {
        if (p == null) {
            return new ToCodeArgHelper("(DereferencePolicy) null", comment);
        }
        final DereferencePolicy definedPolicy = DereferencePolicy.definedValueOf(p.intValue());
        if (definedPolicy == null) {
            return new ToCodeArgHelper("DereferencePolicy.valueOf(" + p.intValue() + ')', comment);
        }
        return new ToCodeArgHelper("DereferencePolicy." + definedPolicy.getName(), comment);
    }
    
    public static ToCodeArgHelper createAttribute(final Attribute a, final String comment) {
        if (a == null) {
            return new ToCodeArgHelper("(Attribute) null", comment);
        }
        if (!a.hasValue()) {
            return new ToCodeArgHelper("new Attribute(\"" + a.getName() + "\")", comment);
        }
        final ASN1OctetString[] rawValues = a.getRawValues();
        final ArrayList<String> lines = new ArrayList<String>(2 + rawValues.length);
        lines.add("new Attribute(");
        lines.add("     \"" + a.getName() + "\",");
        if (StaticUtils.isSensitiveToCodeAttribute(a.getName())) {
            if (rawValues.length == 1) {
                lines.add("     \"---redacted-value---\")");
            }
            else {
                for (int i = 1; i <= rawValues.length; ++i) {
                    String suffix;
                    if (i == rawValues.length) {
                        suffix = ")";
                    }
                    else {
                        suffix = ",";
                    }
                    lines.add("     \"---redacted-value-" + i + "---\"" + suffix);
                }
            }
        }
        else if (allPrintable(rawValues)) {
            for (int i = 0; i < rawValues.length; ++i) {
                String suffix;
                if (i == rawValues.length - 1) {
                    suffix = ")";
                }
                else {
                    suffix = ",";
                }
                lines.add("     \"" + rawValues[i].stringValue().replace("\"", "\\\"") + '\"' + suffix);
            }
        }
        else {
            for (int i = 0; i < rawValues.length; ++i) {
                String suffix;
                if (i < rawValues.length - 1) {
                    suffix = ",";
                }
                else {
                    suffix = ")";
                }
                final Iterator<String> byteArrayLineIterator = getByteArrayLines(rawValues[i].getValue(), true).iterator();
                while (byteArrayLineIterator.hasNext()) {
                    final String s = byteArrayLineIterator.next();
                    if (byteArrayLineIterator.hasNext()) {
                        lines.add("     " + s);
                    }
                    else {
                        lines.add("     " + s + suffix);
                    }
                }
            }
        }
        return new ToCodeArgHelper(lines, comment);
    }
    
    public static ToCodeArgHelper createModification(final Modification m, final String comment) {
        if (m == null) {
            return new ToCodeArgHelper("(Modification) null", comment);
        }
        final ASN1OctetString[] rawValues = m.getRawValues();
        final ArrayList<String> lines = new ArrayList<String>(3 + rawValues.length);
        lines.add("new Modification(");
        lines.add("     " + createModificationType(m.getModificationType(), null).getLines().get(0) + ',');
        if (rawValues.length == 0) {
            lines.add("     \"" + m.getAttributeName() + "\")");
        }
        else {
            lines.add("     \"" + m.getAttributeName() + "\",");
            if (StaticUtils.isSensitiveToCodeAttribute(m.getAttributeName())) {
                if (rawValues.length == 1) {
                    lines.add("     \"---redacted-value---\")");
                }
                else {
                    for (int i = 1; i <= rawValues.length; ++i) {
                        String suffix;
                        if (i == rawValues.length) {
                            suffix = ")";
                        }
                        else {
                            suffix = ",";
                        }
                        lines.add("     \"---redacted-value-" + i + "---\"" + suffix);
                    }
                }
            }
            else if (allPrintable(rawValues)) {
                for (int i = 0; i < rawValues.length; ++i) {
                    String suffix;
                    if (i == rawValues.length - 1) {
                        suffix = ")";
                    }
                    else {
                        suffix = ",";
                    }
                    lines.add("     \"" + rawValues[i].stringValue().replace("\"", "\\\"") + '\"' + suffix);
                }
            }
            else {
                for (int i = 0; i < rawValues.length; ++i) {
                    String suffix;
                    if (i == rawValues.length - 1) {
                        suffix = ")";
                    }
                    else {
                        suffix = ",";
                    }
                    final Iterator<String> byteArrayLineIterator = getByteArrayLines(rawValues[i].getValue(), true).iterator();
                    while (byteArrayLineIterator.hasNext()) {
                        final String s = byteArrayLineIterator.next();
                        if (byteArrayLineIterator.hasNext()) {
                            lines.add("     " + s);
                        }
                        else {
                            lines.add("     " + s + suffix);
                        }
                    }
                }
            }
        }
        return new ToCodeArgHelper(lines, comment);
    }
    
    public static ToCodeArgHelper createFilter(final Filter f, final String comment) {
        if (f == null) {
            return new ToCodeArgHelper("(Filter) null", comment);
        }
        final ArrayList<String> lines = new ArrayList<String>(10);
        addFilterLines(lines, f, "", "");
        return new ToCodeArgHelper(lines, comment);
    }
    
    private static void addFilterLines(final List<String> lines, final Filter f, final String indent, final String suffix) {
        final String nestedIndent = indent + "     ";
        switch (f.getFilterType()) {
            case -96:
            case -95: {
                final Filter[] components = f.getComponents();
                if (f.getFilterType() == -96) {
                    if (components.length == 0) {
                        lines.add(indent + "Filter.createANDFilter()" + suffix);
                        return;
                    }
                    lines.add(indent + "Filter.createANDFilter(");
                }
                else {
                    if (components.length == 0) {
                        lines.add(indent + "Filter.createORFilter()" + suffix);
                        return;
                    }
                    lines.add(indent + "Filter.createORFilter(");
                }
                for (int i = 0; i < components.length; ++i) {
                    if (i == components.length - 1) {
                        addFilterLines(lines, components[i], nestedIndent, ')' + suffix);
                    }
                    else {
                        addFilterLines(lines, components[i], nestedIndent, ",");
                    }
                }
                break;
            }
            case -94: {
                lines.add(indent + "Filter.createNOTFilter(");
                addFilterLines(lines, f.getNOTComponent(), nestedIndent, ')' + suffix);
                break;
            }
            case -121: {
                lines.add(indent + "Filter.createPresenceFilter(");
                lines.add(nestedIndent + '\"' + f.getAttributeName() + "\")" + suffix);
                break;
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                switch (f.getFilterType()) {
                    case -93: {
                        lines.add(indent + "Filter.createEqualityFilter(");
                        break;
                    }
                    case -91: {
                        lines.add(indent + "Filter.createGreaterOrEqualFilter(");
                        break;
                    }
                    case -90: {
                        lines.add(indent + "Filter.createLessOrEqualFilter(");
                        break;
                    }
                    case -88: {
                        lines.add(indent + "Filter.createApproximateMatchFilter(");
                        break;
                    }
                }
                lines.add(nestedIndent + '\"' + f.getAttributeName() + "\",");
                if (StaticUtils.isSensitiveToCodeAttribute(f.getAttributeName())) {
                    lines.add(nestedIndent + "\"---redacted-value---\")" + suffix);
                    break;
                }
                if (StaticUtils.isPrintableString(f.getAssertionValueBytes())) {
                    lines.add(nestedIndent + '\"' + f.getAssertionValue() + "\")" + suffix);
                    break;
                }
                final Iterator<String> iterator = getByteArrayLines(f.getAssertionValueBytes(), true).iterator();
                while (iterator.hasNext()) {
                    final String line = iterator.next();
                    if (iterator.hasNext()) {
                        lines.add(nestedIndent + line);
                    }
                    else {
                        lines.add(nestedIndent + line + ')' + suffix);
                    }
                }
                break;
            }
            case -92: {
                lines.add(indent + "Filter.createSubstringFilter(");
                lines.add(nestedIndent + '\"' + f.getAttributeName() + "\",");
                if (!StaticUtils.isSensitiveToCodeAttribute(f.getAttributeName())) {
                    boolean allPrintable = (f.getRawSubInitialValue() == null || StaticUtils.isPrintableString(f.getSubInitialBytes())) && (f.getRawSubFinalValue() == null || StaticUtils.isPrintableString(f.getSubFinalBytes()));
                    if (allPrintable && f.getRawSubAnyValues().length > 0) {
                        for (final byte[] b : f.getSubAnyBytes()) {
                            if (!StaticUtils.isPrintableString(b)) {
                                allPrintable = false;
                                break;
                            }
                        }
                    }
                    if (f.getRawSubInitialValue() == null) {
                        lines.add(nestedIndent + "null,");
                    }
                    else if (allPrintable) {
                        lines.add(nestedIndent + '\"' + f.getSubInitialString().replace("\"", "\\\"") + "\",");
                    }
                    else {
                        final Iterator<String> iterator2 = getByteArrayLines(f.getSubInitialBytes(), true).iterator();
                        while (iterator2.hasNext()) {
                            final String line2 = iterator2.next();
                            if (iterator2.hasNext()) {
                                lines.add(nestedIndent + line2);
                            }
                            else {
                                lines.add(nestedIndent + line2 + ',');
                            }
                        }
                    }
                    if (f.getRawSubAnyValues().length == 0) {
                        lines.add(nestedIndent + "null,");
                    }
                    else if (allPrintable) {
                        lines.add(nestedIndent + "new String[]");
                        lines.add(nestedIndent + '{');
                        final String[] subAnyStrings = f.getSubAnyStrings();
                        for (int j = 0; j < subAnyStrings.length; ++j) {
                            String comma;
                            if (j == subAnyStrings.length - 1) {
                                comma = "";
                            }
                            else {
                                comma = ",";
                            }
                            lines.add(nestedIndent + "  \"" + subAnyStrings[j] + '\"' + comma);
                        }
                        lines.add(nestedIndent + "},");
                    }
                    else {
                        lines.add(nestedIndent + "new byte[][]");
                        lines.add(nestedIndent + '{');
                        final byte[][] subAnyBytes = f.getSubAnyBytes();
                        for (int j = 0; j < subAnyBytes.length; ++j) {
                            String comma;
                            if (j == subAnyBytes.length - 1) {
                                comma = "";
                            }
                            else {
                                comma = ",";
                            }
                            final Iterator<String> iterator3 = getByteArrayLines(subAnyBytes[j], true).iterator();
                            while (iterator3.hasNext()) {
                                final String line3 = iterator3.next();
                                if (iterator3.hasNext()) {
                                    lines.add(nestedIndent + "  " + line3);
                                }
                                else {
                                    lines.add(nestedIndent + "  " + line3 + comma);
                                }
                            }
                        }
                        lines.add(nestedIndent + "},");
                    }
                    if (f.getRawSubFinalValue() == null) {
                        lines.add(nestedIndent + "null)" + suffix);
                    }
                    else if (allPrintable) {
                        lines.add(nestedIndent + '\"' + f.getSubFinalString().replace("\"", "\\\"") + "\")" + suffix);
                    }
                    else {
                        final Iterator<String> iterator2 = getByteArrayLines(f.getSubFinalBytes(), true).iterator();
                        while (iterator2.hasNext()) {
                            final String line2 = iterator2.next();
                            if (iterator2.hasNext()) {
                                lines.add(nestedIndent + line2);
                            }
                            else {
                                lines.add(nestedIndent + line2 + ')' + suffix);
                            }
                        }
                    }
                    break;
                }
                if (f.getRawSubInitialValue() == null) {
                    lines.add(nestedIndent + "null,");
                }
                else {
                    lines.add(nestedIndent + "\"---redacted-subInitial---\",");
                }
                if (f.getRawSubAnyValues().length == 0) {
                    lines.add(nestedIndent + "null,");
                }
                else if (f.getRawSubAnyValues().length == 1) {
                    lines.add(nestedIndent + "new String[]");
                    lines.add(nestedIndent + '{');
                    lines.add(nestedIndent + "  \"---redacted-subAny---\"");
                    lines.add(nestedIndent + "},");
                }
                else {
                    lines.add(nestedIndent + "new String[]");
                    lines.add(nestedIndent + '{');
                    for (int i = 1; i <= f.getRawSubAnyValues().length; ++i) {
                        final String comma2 = (i == f.getRawSubAnyValues().length) ? "" : ",";
                        lines.add(nestedIndent + "  \"---redacted-subAny-" + i + "---\"" + comma2);
                    }
                    lines.add(nestedIndent + "},");
                }
                if (f.getRawSubFinalValue() == null) {
                    lines.add(nestedIndent + "null)" + suffix);
                    break;
                }
                lines.add(nestedIndent + "\"---redacted-subFinal---\")" + suffix);
                break;
            }
            case -87: {
                lines.add(indent + "Filter.createExtensibleMatchFilter(");
                if (f.getAttributeName() == null) {
                    lines.add(nestedIndent + "null,");
                }
                else {
                    lines.add(nestedIndent + '\"' + f.getAttributeName() + "\",");
                }
                if (f.getMatchingRuleID() == null) {
                    lines.add(nestedIndent + "null,");
                }
                else {
                    lines.add(nestedIndent + '\"' + f.getMatchingRuleID() + "\",");
                }
                lines.add(nestedIndent + f.getDNAttributes() + ',');
                if (f.getAttributeName() != null && StaticUtils.isSensitiveToCodeAttribute(f.getAttributeName())) {
                    lines.add(nestedIndent + "\"---redacted-value---\")" + suffix);
                    break;
                }
                if (StaticUtils.isPrintableString(f.getAssertionValueBytes())) {
                    lines.add(nestedIndent + '\"' + f.getAssertionValue().replace("\"", "\\\"") + "\")" + suffix);
                    break;
                }
                final Iterator<String> iterator = getByteArrayLines(f.getAssertionValueBytes(), true).iterator();
                while (iterator.hasNext()) {
                    final String line = iterator.next();
                    if (iterator.hasNext()) {
                        lines.add(nestedIndent + line);
                    }
                    else {
                        lines.add(nestedIndent + line + ')' + suffix);
                    }
                }
                break;
            }
        }
    }
    
    public static ToCodeArgHelper createControl(final Control c, final String comment) {
        if (c == null) {
            return new ToCodeArgHelper("(Control) null", comment);
        }
        final ArrayList<String> lines = new ArrayList<String>(10);
        lines.add("new Control(");
        lines.add("     \"" + c.getOID() + "\",");
        if (c.hasValue()) {
            lines.add("     " + c.isCritical() + ',');
            final List<String> valueLines = createASN1OctetString(c.getValue(), null).argStrings;
            final Iterator<String> valueLineIterator = valueLines.iterator();
            while (valueLineIterator.hasNext()) {
                final String s = valueLineIterator.next();
                if (valueLineIterator.hasNext()) {
                    lines.add("     " + s);
                }
                else {
                    lines.add("     " + s + ')');
                }
            }
        }
        else {
            lines.add("     " + c.isCritical() + ')');
        }
        return new ToCodeArgHelper(lines, comment);
    }
    
    public static ToCodeArgHelper createControlArray(final Control[] c, final String comment) {
        if (c == null) {
            return new ToCodeArgHelper("(Control[]) null", comment);
        }
        if (c.length == 0) {
            return new ToCodeArgHelper("new Control[0]", comment);
        }
        final ArrayList<String> lines = new ArrayList<String>(10);
        lines.add("new Control[]");
        lines.add("{");
        for (int i = 0; i < c.length; ++i) {
            final ToCodeArgHelper h = createControl(c[i], null);
            final List<String> hLines = h.argStrings;
            final Iterator<String> iterator = hLines.iterator();
            while (iterator.hasNext()) {
                final String line = iterator.next();
                if (!iterator.hasNext() && i < c.length - 1) {
                    lines.add("  " + line + ',');
                }
                else {
                    lines.add("  " + line);
                }
            }
        }
        lines.add("}");
        return new ToCodeArgHelper(lines, comment);
    }
    
    public static ToCodeArgHelper createRaw(final String s, final String comment) {
        return new ToCodeArgHelper(s, comment);
    }
    
    public static ToCodeArgHelper createRaw(final List<String> s, final String comment) {
        return new ToCodeArgHelper(s, comment);
    }
    
    private static boolean allPrintable(final ASN1OctetString... values) {
        for (final ASN1OctetString s : values) {
            if (!StaticUtils.isPrintableString(s.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    public List<String> getLines() {
        return this.argStrings;
    }
    
    public String getComment() {
        return this.comment;
    }
}
