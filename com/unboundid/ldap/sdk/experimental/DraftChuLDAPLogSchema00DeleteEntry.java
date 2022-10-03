package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.DeleteRequest;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00DeleteEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_DELETED_ATTRIBUTE = "reqOld";
    private static final long serialVersionUID = -4326357861964770357L;
    private final List<Attribute> deletedAttributes;
    
    public DraftChuLDAPLogSchema00DeleteEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.DELETE);
        final byte[][] deletedAttrBytes = entry.getAttributeValueByteArrays("reqOld");
        if (deletedAttrBytes == null || deletedAttrBytes.length == 0) {
            this.deletedAttributes = Collections.emptyList();
            return;
        }
        final LinkedHashMap<String, List<Attribute>> attrMap = new LinkedHashMap<String, List<Attribute>>(StaticUtils.computeMapCapacity(deletedAttrBytes.length));
        for (final byte[] attrBytes : deletedAttrBytes) {
            int colonPos = -1;
            for (int i = 0; i < attrBytes.length; ++i) {
                if (attrBytes[i] == 58) {
                    colonPos = i;
                    break;
                }
            }
            if (colonPos < 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_DELETE_OLD_ATTR_MISSING_COLON.get(entry.getDN(), "reqOld", StaticUtils.toUTF8String(attrBytes)));
            }
            if (colonPos == 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_DELETE_OLD_ATTR_MISSING_ATTR.get(entry.getDN(), "reqOld", StaticUtils.toUTF8String(attrBytes)));
            }
            if (colonPos == attrBytes.length - 1 || attrBytes[colonPos + 1] != 32) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_DELETE_OLD_ATTR_MISSING_SPACE.get(entry.getDN(), "reqOld", StaticUtils.toUTF8String(attrBytes)));
            }
            final String attrName = StaticUtils.toUTF8String(attrBytes, 0, colonPos);
            final String lowerName = StaticUtils.toLowerCase(attrName);
            List<Attribute> attrList = attrMap.get(lowerName);
            if (attrList == null) {
                attrList = new ArrayList<Attribute>(10);
                attrMap.put(lowerName, attrList);
            }
            final byte[] attrValue = new byte[attrBytes.length - colonPos - 2];
            if (attrValue.length > 0) {
                System.arraycopy(attrBytes, colonPos + 2, attrValue, 0, attrValue.length);
            }
            attrList.add(new Attribute(attrName, attrValue));
        }
        final ArrayList<Attribute> oldAttributes = new ArrayList<Attribute>(attrMap.size());
        for (final List<Attribute> attrList2 : attrMap.values()) {
            if (attrList2.size() == 1) {
                oldAttributes.addAll(attrList2);
            }
            else {
                final byte[][] valueArray = new byte[attrList2.size()][];
                for (int j = 0; j < attrList2.size(); ++j) {
                    valueArray[j] = attrList2.get(j).getValueByteArray();
                }
                oldAttributes.add(new Attribute(attrList2.get(0).getName(), valueArray));
            }
        }
        this.deletedAttributes = Collections.unmodifiableList((List<? extends Attribute>)oldAttributes);
    }
    
    public List<Attribute> getDeletedAttributes() {
        return this.deletedAttributes;
    }
    
    public DeleteRequest toDeleteRequest() {
        return new DeleteRequest(this.getTargetEntryDN(), this.getRequestControlArray());
    }
}
