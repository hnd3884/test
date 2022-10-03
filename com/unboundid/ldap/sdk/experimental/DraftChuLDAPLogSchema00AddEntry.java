package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.AddRequest;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00AddEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_ATTRIBUTE_CHANGES = "reqMod";
    private static final long serialVersionUID = 1236828283266120444L;
    private final List<Attribute> attributes;
    
    public DraftChuLDAPLogSchema00AddEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.ADD);
        final byte[][] changes = entry.getAttributeValueByteArrays("reqMod");
        if (changes == null || changes.length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqMod"));
        }
        final LinkedHashMap<String, List<Attribute>> attrMap = new LinkedHashMap<String, List<Attribute>>(StaticUtils.computeMapCapacity(changes.length));
        for (final byte[] changeBytes : changes) {
            int colonPos = -1;
            for (int i = 0; i < changeBytes.length; ++i) {
                if (changeBytes[i] == 58) {
                    colonPos = i;
                    break;
                }
            }
            if (colonPos < 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_ADD_CHANGE_MISSING_COLON.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            if (colonPos == 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_ADD_CHANGE_MISSING_ATTR.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            if (colonPos == changeBytes.length - 1 || changeBytes[colonPos + 1] != 43) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_ADD_CHANGE_TYPE_NOT_PLUS.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            if (colonPos == changeBytes.length - 2 || changeBytes[colonPos + 2] != 32) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_ADD_CHANGE_NO_SPACE_AFTER_PLUS.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            final String attrName = StaticUtils.toUTF8String(changeBytes, 0, colonPos);
            final String lowerName = StaticUtils.toLowerCase(attrName);
            List<Attribute> attrList = attrMap.get(lowerName);
            if (attrList == null) {
                attrList = new ArrayList<Attribute>(10);
                attrMap.put(lowerName, attrList);
            }
            final byte[] attrValue = new byte[changeBytes.length - colonPos - 3];
            if (attrValue.length > 0) {
                System.arraycopy(changeBytes, colonPos + 3, attrValue, 0, attrValue.length);
            }
            attrList.add(new Attribute(attrName, attrValue));
        }
        final ArrayList<Attribute> addAttributes = new ArrayList<Attribute>(attrMap.size());
        for (final List<Attribute> attrList2 : attrMap.values()) {
            if (attrList2.size() == 1) {
                addAttributes.addAll(attrList2);
            }
            else {
                final byte[][] valueArray = new byte[attrList2.size()][];
                for (int j = 0; j < attrList2.size(); ++j) {
                    valueArray[j] = attrList2.get(j).getValueByteArray();
                }
                addAttributes.add(new Attribute(attrList2.get(0).getName(), valueArray));
            }
        }
        this.attributes = Collections.unmodifiableList((List<? extends Attribute>)addAttributes);
    }
    
    public List<Attribute> getAddAttributes() {
        return this.attributes;
    }
    
    public AddRequest toAddRequest() {
        return new AddRequest(this.getTargetEntryDN(), this.attributes, this.getRequestControlArray());
    }
}
