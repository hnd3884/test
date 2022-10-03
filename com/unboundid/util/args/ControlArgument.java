package com.unboundid.util.args;

import java.util.HashMap;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Base64;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ControlArgument extends Argument
{
    private static final Map<String, String> OIDS_BY_NAME;
    private static final long serialVersionUID = -1889200072476038957L;
    private final List<ArgumentValueValidator> validators;
    private final List<Control> defaultValues;
    private final List<Control> values;
    
    public ControlArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 0, null, description);
    }
    
    public ControlArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (List<Control>)null);
    }
    
    public ControlArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Control defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public ControlArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final List<Control> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_CONTROL.get() : valuePlaceholder, description);
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends Control>)defaultValues);
        }
        this.values = new ArrayList<Control>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
    }
    
    private ControlArgument(final ControlArgument source) {
        super(source);
        this.defaultValues = source.defaultValues;
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
        this.values = new ArrayList<Control>(5);
    }
    
    public List<Control> getDefaultValues() {
        return this.defaultValues;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        String oid = null;
        boolean isCritical = false;
        ASN1OctetString value = null;
        final int firstColonPos = valueString.indexOf(58);
        if (firstColonPos < 0) {
            oid = valueString;
        }
        else {
            oid = valueString.substring(0, firstColonPos);
            final int secondColonPos = valueString.indexOf(58, firstColonPos + 1);
            String criticalityStr = null;
            Label_0179: {
                if (secondColonPos < 0) {
                    criticalityStr = valueString.substring(firstColonPos + 1);
                }
                else {
                    criticalityStr = valueString.substring(firstColonPos + 1, secondColonPos);
                    final int doubleColonPos = valueString.indexOf("::");
                    if (doubleColonPos == secondColonPos) {
                        try {
                            value = new ASN1OctetString(Base64.decode(valueString.substring(doubleColonPos + 2)));
                            break Label_0179;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new ArgumentException(ArgsMessages.ERR_CONTROL_ARG_INVALID_BASE64_VALUE.get(valueString, this.getIdentifierString(), valueString.substring(doubleColonPos + 2)), e);
                        }
                    }
                    value = new ASN1OctetString(valueString.substring(secondColonPos + 1));
                }
            }
            final String lowerCriticalityStr = StaticUtils.toLowerCase(criticalityStr);
            if (lowerCriticalityStr.equals("true") || lowerCriticalityStr.equals("t") || lowerCriticalityStr.equals("yes") || lowerCriticalityStr.equals("y") || lowerCriticalityStr.equals("on") || lowerCriticalityStr.equals("1")) {
                isCritical = true;
            }
            else {
                if (!lowerCriticalityStr.equals("false") && !lowerCriticalityStr.equals("f") && !lowerCriticalityStr.equals("no") && !lowerCriticalityStr.equals("n") && !lowerCriticalityStr.equals("off") && !lowerCriticalityStr.equals("0")) {
                    throw new ArgumentException(ArgsMessages.ERR_CONTROL_ARG_INVALID_CRITICALITY.get(valueString, this.getIdentifierString(), criticalityStr));
                }
                isCritical = false;
            }
        }
        if (!StaticUtils.isNumericOID(oid)) {
            final String providedOID = oid;
            oid = ControlArgument.OIDS_BY_NAME.get(StaticUtils.toLowerCase(providedOID));
            if (oid == null) {
                throw new ArgumentException(ArgsMessages.ERR_CONTROL_ARG_INVALID_OID.get(valueString, this.getIdentifierString(), providedOID));
            }
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(new Control(oid, isCritical, value));
    }
    
    public Control getValue() {
        if (!this.values.isEmpty()) {
            return this.values.get(0);
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<Control> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        return Collections.unmodifiableList((List<? extends Control>)this.values);
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        List<Control> controls;
        if (this.values.isEmpty()) {
            if (!useDefault) {
                return Collections.emptyList();
            }
            controls = this.defaultValues;
        }
        else {
            controls = this.values;
        }
        if (controls == null || controls.isEmpty()) {
            return Collections.emptyList();
        }
        final StringBuilder buffer = new StringBuilder();
        final ArrayList<String> valueStrings = new ArrayList<String>(controls.size());
        for (final Control c : controls) {
            buffer.setLength(0);
            buffer.append(c.getOID());
            buffer.append(':');
            buffer.append(c.isCritical());
            if (c.hasValue()) {
                final byte[] valueBytes = c.getValue().getValue();
                if (StaticUtils.isPrintableString(valueBytes)) {
                    buffer.append(':');
                    buffer.append(c.getValue().stringValue());
                }
                else {
                    buffer.append("::");
                    Base64.encode(valueBytes, buffer);
                }
            }
            valueStrings.add(buffer.toString());
        }
        return Collections.unmodifiableList((List<? extends String>)valueStrings);
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_CONTROL_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_CONTROL_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public ControlArgument getCleanCopy() {
        return new ControlArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            final StringBuilder buffer = new StringBuilder();
            for (final Control c : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    buffer.setLength(0);
                    buffer.append(c.getOID());
                    buffer.append(':');
                    buffer.append(c.isCritical());
                    if (c.hasValue()) {
                        final byte[] valueBytes = c.getValue().getValue();
                        if (StaticUtils.isPrintableString(valueBytes)) {
                            buffer.append(':');
                            buffer.append(c.getValue().stringValue());
                        }
                        else {
                            buffer.append("::");
                            Base64.encode(valueBytes, buffer);
                        }
                    }
                    argStrings.add(buffer.toString());
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ControlArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(this.defaultValues.get(0).toString());
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<Control> iterator = this.defaultValues.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next().toString());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
            }
        }
        buffer.append(')');
    }
    
    static {
        final HashMap<String, String> oidsByName = new HashMap<String, String>(StaticUtils.computeMapCapacity(100));
        oidsByName.put("authzid", "2.16.840.1.113730.3.4.16");
        oidsByName.put("authorizationidentity", "2.16.840.1.113730.3.4.16");
        oidsByName.put("authorization-identity", "2.16.840.1.113730.3.4.16");
        oidsByName.put("nocopy", "1.3.6.1.1.22");
        oidsByName.put("dontusecopy", "1.3.6.1.1.22");
        oidsByName.put("no-copy", "1.3.6.1.1.22");
        oidsByName.put("dont-use-copy", "1.3.6.1.1.22");
        oidsByName.put("noop", "1.3.6.1.4.1.4203.1.10.2");
        oidsByName.put("nooperation", "1.3.6.1.4.1.4203.1.10.2");
        oidsByName.put("no-op", "1.3.6.1.4.1.4203.1.10.2");
        oidsByName.put("no-operation", "1.3.6.1.4.1.4203.1.10.2");
        oidsByName.put("subentries", "1.3.6.1.4.1.7628.5.101.1");
        oidsByName.put("ldapsubentries", "1.3.6.1.4.1.7628.5.101.1");
        oidsByName.put("ldap-subentries", "1.3.6.1.4.1.7628.5.101.1");
        oidsByName.put("managedsait", "2.16.840.1.113730.3.4.2");
        oidsByName.put("manage-dsa-it", "2.16.840.1.113730.3.4.2");
        oidsByName.put("permissivemodify", "1.2.840.113556.1.4.1413");
        oidsByName.put("permissive-modify", "1.2.840.113556.1.4.1413");
        oidsByName.put("pwpolicy", "1.3.6.1.4.1.42.2.27.8.5.1");
        oidsByName.put("passwordpolicy", "1.3.6.1.4.1.42.2.27.8.5.1");
        oidsByName.put("pw-policy", "1.3.6.1.4.1.42.2.27.8.5.1");
        oidsByName.put("password-policy", "1.3.6.1.4.1.42.2.27.8.5.1");
        oidsByName.put("subtreedelete", "1.2.840.113556.1.4.805");
        oidsByName.put("treedelete", "1.2.840.113556.1.4.805");
        oidsByName.put("subtree-delete", "1.2.840.113556.1.4.805");
        oidsByName.put("tree-delete", "1.2.840.113556.1.4.805");
        oidsByName.put("accountusable", "1.3.6.1.4.1.42.2.27.9.5.8");
        oidsByName.put("accountusability", "1.3.6.1.4.1.42.2.27.9.5.8");
        oidsByName.put("account-usable", "1.3.6.1.4.1.42.2.27.9.5.8");
        oidsByName.put("account-usability", "1.3.6.1.4.1.42.2.27.9.5.8");
        oidsByName.put("generatepassword", "1.3.6.1.4.1.30221.2.5.58");
        oidsByName.put("generate-password", "1.3.6.1.4.1.30221.2.5.58");
        oidsByName.put("generatepw", "1.3.6.1.4.1.30221.2.5.58");
        oidsByName.put("generate-pw", "1.3.6.1.4.1.30221.2.5.58");
        oidsByName.put("backendsetid", "1.3.6.1.4.1.30221.2.5.33");
        oidsByName.put("getbackendsetid", "1.3.6.1.4.1.30221.2.5.33");
        oidsByName.put("backendset-id", "1.3.6.1.4.1.30221.2.5.33");
        oidsByName.put("backend-set-id", "1.3.6.1.4.1.30221.2.5.33");
        oidsByName.put("get-backendset-id", "1.3.6.1.4.1.30221.2.5.33");
        oidsByName.put("get-backend-set-id", "1.3.6.1.4.1.30221.2.5.33");
        oidsByName.put("effectiverights", "1.3.6.1.4.1.42.2.27.9.5.2");
        oidsByName.put("geteffectiverights", "1.3.6.1.4.1.42.2.27.9.5.2");
        oidsByName.put("effective-rights", "1.3.6.1.4.1.42.2.27.9.5.2");
        oidsByName.put("get-effective-rights", "1.3.6.1.4.1.42.2.27.9.5.2");
        oidsByName.put("pwpolicystateissues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("getpwpolicystateissues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("passwordpolicystateissues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("getpasswordpolicystateissues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("pw-policy-state-issues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("get-pw-policy-state-issues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("password-policy-state-issues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("get-password-policy-state-issues", "1.3.6.1.4.1.30221.2.5.46");
        oidsByName.put("serverid", "1.3.6.1.4.1.30221.2.5.14");
        oidsByName.put("getserverid", "1.3.6.1.4.1.30221.2.5.14");
        oidsByName.put("server-id", "1.3.6.1.4.1.30221.2.5.14");
        oidsByName.put("get-server-id", "1.3.6.1.4.1.30221.2.5.14");
        oidsByName.put("userresourcelimits", "1.3.6.1.4.1.30221.2.5.25");
        oidsByName.put("getuserresourcelimits", "1.3.6.1.4.1.30221.2.5.25");
        oidsByName.put("user-resource-limits", "1.3.6.1.4.1.30221.2.5.25");
        oidsByName.put("get-user-resource-limits", "1.3.6.1.4.1.30221.2.5.25");
        oidsByName.put("harddelete", "1.3.6.1.4.1.30221.2.5.22");
        oidsByName.put("hard-delete", "1.3.6.1.4.1.30221.2.5.22");
        oidsByName.put("ignorenousermod", "1.3.6.1.4.1.30221.2.5.5");
        oidsByName.put("ignorenousermodification", "1.3.6.1.4.1.30221.2.5.5");
        oidsByName.put("ignore-no-user-mod", "1.3.6.1.4.1.30221.2.5.5");
        oidsByName.put("ignore-no-user-modification", "1.3.6.1.4.1.30221.2.5.5");
        oidsByName.put("purgepassword", "1.3.6.1.4.1.30221.2.5.32");
        oidsByName.put("purgeretiredpassword", "1.3.6.1.4.1.30221.2.5.32");
        oidsByName.put("purge-password", "1.3.6.1.4.1.30221.2.5.32");
        oidsByName.put("purge-retired-password", "1.3.6.1.4.1.30221.2.5.32");
        oidsByName.put("realattrsonly", "2.16.840.1.113730.3.4.17");
        oidsByName.put("realattributesonly", "2.16.840.1.113730.3.4.17");
        oidsByName.put("real-attrs-only", "2.16.840.1.113730.3.4.17");
        oidsByName.put("real-attributes-only", "2.16.840.1.113730.3.4.17");
        oidsByName.put("replrepair", "1.3.6.1.4.1.30221.1.5.2");
        oidsByName.put("replicationrepair", "1.3.6.1.4.1.30221.1.5.2");
        oidsByName.put("repl-repair", "1.3.6.1.4.1.30221.1.5.2");
        oidsByName.put("replication-repair", "1.3.6.1.4.1.30221.1.5.2");
        oidsByName.put("retainidentity", "1.3.6.1.4.1.30221.2.5.3");
        oidsByName.put("retain-identity", "1.3.6.1.4.1.30221.2.5.3");
        oidsByName.put("retirepassword", "1.3.6.1.4.1.30221.2.5.31");
        oidsByName.put("retire-password", "1.3.6.1.4.1.30221.2.5.31");
        oidsByName.put("returnconflictentries", "1.3.6.1.4.1.30221.2.5.13");
        oidsByName.put("return-conflict-entries", "1.3.6.1.4.1.30221.2.5.13");
        oidsByName.put("softdelete", "1.3.6.1.4.1.30221.2.5.20");
        oidsByName.put("soft-delete", "1.3.6.1.4.1.30221.2.5.20");
        oidsByName.put("softdeleteentryaccess", "1.3.6.1.4.1.30221.2.5.24");
        oidsByName.put("softdeletedentryaccess", "1.3.6.1.4.1.30221.2.5.24");
        oidsByName.put("soft-delete-entry-access", "1.3.6.1.4.1.30221.2.5.24");
        oidsByName.put("soft-deleted-entry-access", "1.3.6.1.4.1.30221.2.5.24");
        oidsByName.put("suppressreferentialintegrity", "1.3.6.1.4.1.30221.2.5.30");
        oidsByName.put("suppressreferentialintegrityupdates", "1.3.6.1.4.1.30221.2.5.30");
        oidsByName.put("suppress-referential-integrity", "1.3.6.1.4.1.30221.2.5.30");
        oidsByName.put("suppress-referential-integrity-updates", "1.3.6.1.4.1.30221.2.5.30");
        oidsByName.put("undelete", "1.3.6.1.4.1.30221.2.5.23");
        oidsByName.put("virtualattrsonly", "2.16.840.1.113730.3.4.19");
        oidsByName.put("virtualattributesonly", "2.16.840.1.113730.3.4.19");
        oidsByName.put("virtual-attrs-only", "2.16.840.1.113730.3.4.19");
        oidsByName.put("virtual-attributes-only", "2.16.840.1.113730.3.4.19");
        OIDS_BY_NAME = Collections.unmodifiableMap((Map<? extends String, ? extends String>)oidsByName);
    }
}
