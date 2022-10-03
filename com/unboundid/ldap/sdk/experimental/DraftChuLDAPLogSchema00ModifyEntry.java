package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.ModifyRequest;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Collections;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00ModifyEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_ATTRIBUTE_CHANGES = "reqMod";
    public static final String ATTR_FORMER_ATTRIBUTE = "reqOld";
    private static final long serialVersionUID = 5787071409404025072L;
    private final List<Attribute> formerAttributes;
    private final List<Modification> modifications;
    
    public DraftChuLDAPLogSchema00ModifyEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.MODIFY);
        final byte[][] changes = entry.getAttributeValueByteArrays("reqMod");
        if (changes == null || changes.length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqMod"));
        }
        final ArrayList<Modification> mods = new ArrayList<Modification>(changes.length);
        for (final byte[] changeBytes : changes) {
            int colonPos = -1;
            for (int i = 0; i < changeBytes.length; ++i) {
                if (changeBytes[i] == 58) {
                    colonPos = i;
                    break;
                }
            }
            if (colonPos < 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_COLON.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            if (colonPos == 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_ATTR.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            final String attrName = StaticUtils.toUTF8String(changeBytes, 0, colonPos);
            if (colonPos == changeBytes.length - 1) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_CHANGE_TYPE.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
            }
            ModificationType modType = null;
            boolean needValue = false;
            switch (changeBytes[colonPos + 1]) {
                case 43: {
                    modType = ModificationType.ADD;
                    needValue = true;
                    break;
                }
                case 45: {
                    modType = ModificationType.DELETE;
                    needValue = false;
                    break;
                }
                case 61: {
                    modType = ModificationType.REPLACE;
                    needValue = false;
                    break;
                }
                case 35: {
                    modType = ModificationType.INCREMENT;
                    needValue = true;
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_INVALID_CHANGE_TYPE.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes)));
                }
            }
            if (changeBytes.length == colonPos + 2) {
                if (needValue) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_VALUE.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes), modType.getName()));
                }
                mods.add(new Modification(modType, attrName));
            }
            else {
                if (changeBytes.length == colonPos + 3 || changeBytes[colonPos + 2] != 32) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_SPACE.get(entry.getDN(), "reqMod", StaticUtils.toUTF8String(changeBytes), modType.getName()));
                }
                final byte[] attrValue = new byte[changeBytes.length - colonPos - 3];
                if (attrValue.length > 0) {
                    System.arraycopy(changeBytes, colonPos + 3, attrValue, 0, attrValue.length);
                }
                if (mods.isEmpty()) {
                    mods.add(new Modification(modType, attrName, attrValue));
                }
                else {
                    final Modification lastMod = mods.get(mods.size() - 1);
                    if (lastMod.getModificationType() == modType && lastMod.getAttributeName().equalsIgnoreCase(attrName)) {
                        final byte[][] lastModValues = lastMod.getValueByteArrays();
                        final byte[][] newValues = new byte[lastModValues.length + 1][];
                        System.arraycopy(lastModValues, 0, newValues, 0, lastModValues.length);
                        newValues[lastModValues.length] = attrValue;
                        mods.set(mods.size() - 1, new Modification(modType, lastMod.getAttributeName(), newValues));
                    }
                    else {
                        mods.add(new Modification(modType, attrName, attrValue));
                    }
                }
            }
        }
        this.modifications = Collections.unmodifiableList((List<? extends Modification>)mods);
        final byte[][] formerAttrBytes = entry.getAttributeValueByteArrays("reqOld");
        if (formerAttrBytes == null || formerAttrBytes.length == 0) {
            this.formerAttributes = Collections.emptyList();
            return;
        }
        final LinkedHashMap<String, List<Attribute>> attrMap = new LinkedHashMap<String, List<Attribute>>(StaticUtils.computeMapCapacity(formerAttrBytes.length));
        for (final byte[] attrBytes : formerAttrBytes) {
            int colonPos2 = -1;
            for (int j = 0; j < attrBytes.length; ++j) {
                if (attrBytes[j] == 58) {
                    colonPos2 = j;
                    break;
                }
            }
            if (colonPos2 < 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_OLD_ATTR_MISSING_COLON.get(entry.getDN(), "reqOld", StaticUtils.toUTF8String(attrBytes)));
            }
            if (colonPos2 == 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_OLD_ATTR_MISSING_ATTR.get(entry.getDN(), "reqOld", StaticUtils.toUTF8String(attrBytes)));
            }
            if (colonPos2 == attrBytes.length - 1 || attrBytes[colonPos2 + 1] != 32) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MODIFY_OLD_ATTR_MISSING_SPACE.get(entry.getDN(), "reqOld", StaticUtils.toUTF8String(attrBytes)));
            }
            final String attrName2 = StaticUtils.toUTF8String(attrBytes, 0, colonPos2);
            final String lowerName = StaticUtils.toLowerCase(attrName2);
            List<Attribute> attrList = attrMap.get(lowerName);
            if (attrList == null) {
                attrList = new ArrayList<Attribute>(10);
                attrMap.put(lowerName, attrList);
            }
            final byte[] attrValue2 = new byte[attrBytes.length - colonPos2 - 2];
            if (attrValue2.length > 0) {
                System.arraycopy(attrBytes, colonPos2 + 2, attrValue2, 0, attrValue2.length);
            }
            attrList.add(new Attribute(attrName2, attrValue2));
        }
        final ArrayList<Attribute> oldAttributes = new ArrayList<Attribute>(attrMap.size());
        for (final List<Attribute> attrList2 : attrMap.values()) {
            if (attrList2.size() == 1) {
                oldAttributes.addAll(attrList2);
            }
            else {
                final byte[][] valueArray = new byte[attrList2.size()][];
                for (int k = 0; k < attrList2.size(); ++k) {
                    valueArray[k] = attrList2.get(k).getValueByteArray();
                }
                oldAttributes.add(new Attribute(attrList2.get(0).getName(), valueArray));
            }
        }
        this.formerAttributes = Collections.unmodifiableList((List<? extends Attribute>)oldAttributes);
    }
    
    public List<Modification> getModifications() {
        return this.modifications;
    }
    
    public List<Attribute> getFormerAttributes() {
        return this.formerAttributes;
    }
    
    public ModifyRequest toModifyRequest() {
        return new ModifyRequest(this.getTargetEntryDN(), this.modifications, this.getRequestControlArray());
    }
}
