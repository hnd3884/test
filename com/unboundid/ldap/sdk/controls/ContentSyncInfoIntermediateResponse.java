package com.unboundid.ldap.sdk.controls;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import java.text.ParseException;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collections;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.UUID;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.IntermediateResponse;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ContentSyncInfoIntermediateResponse extends IntermediateResponse
{
    public static final String SYNC_INFO_OID = "1.3.6.1.4.1.4203.1.9.1.4";
    private static final long serialVersionUID = 4464376009337157433L;
    private final ASN1OctetString cookie;
    private final boolean refreshDeletes;
    private final boolean refreshDone;
    private final ContentSyncInfoType type;
    private final List<UUID> entryUUIDs;
    
    private ContentSyncInfoIntermediateResponse(final ContentSyncInfoType type, final ASN1OctetString value, final ASN1OctetString cookie, final boolean refreshDone, final boolean refreshDeletes, final List<UUID> entryUUIDs, final Control... controls) {
        super("1.3.6.1.4.1.4203.1.9.1.4", value, controls);
        this.type = type;
        this.cookie = cookie;
        this.refreshDone = refreshDone;
        this.refreshDeletes = refreshDeletes;
        this.entryUUIDs = entryUUIDs;
    }
    
    public static ContentSyncInfoIntermediateResponse createNewCookieResponse(final ASN1OctetString cookie, final Control... controls) {
        Validator.ensureNotNull(cookie);
        final ContentSyncInfoType type = ContentSyncInfoType.NEW_COOKIE;
        return new ContentSyncInfoIntermediateResponse(type, encodeValue(type, cookie, false, null, false), cookie, false, false, null, controls);
    }
    
    public static ContentSyncInfoIntermediateResponse createRefreshDeleteResponse(final ASN1OctetString cookie, final boolean refreshDone, final Control... controls) {
        final ContentSyncInfoType type = ContentSyncInfoType.REFRESH_DELETE;
        return new ContentSyncInfoIntermediateResponse(type, encodeValue(type, cookie, refreshDone, null, false), cookie, refreshDone, false, null, controls);
    }
    
    public static ContentSyncInfoIntermediateResponse createRefreshPresentResponse(final ASN1OctetString cookie, final boolean refreshDone, final Control... controls) {
        final ContentSyncInfoType type = ContentSyncInfoType.REFRESH_PRESENT;
        return new ContentSyncInfoIntermediateResponse(type, encodeValue(type, cookie, refreshDone, null, false), cookie, refreshDone, false, null, controls);
    }
    
    public static ContentSyncInfoIntermediateResponse createSyncIDSetResponse(final ASN1OctetString cookie, final List<UUID> entryUUIDs, final boolean refreshDeletes, final Control... controls) {
        Validator.ensureNotNull(entryUUIDs);
        final ContentSyncInfoType type = ContentSyncInfoType.SYNC_ID_SET;
        return new ContentSyncInfoIntermediateResponse(type, encodeValue(type, cookie, false, entryUUIDs, refreshDeletes), cookie, false, refreshDeletes, Collections.unmodifiableList((List<? extends UUID>)entryUUIDs), controls);
    }
    
    public static ContentSyncInfoIntermediateResponse decode(final IntermediateResponse r) throws LDAPException {
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_NO_VALUE.get());
        }
        ASN1Element valueElement;
        try {
            valueElement = ASN1Element.decode(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_VALUE_NOT_ELEMENT.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final ContentSyncInfoType type = ContentSyncInfoType.valueOf(valueElement.getType());
        if (type == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_VALUE_UNRECOGNIZED_TYPE.get(StaticUtils.toHex(valueElement.getType())));
        }
        ASN1OctetString cookie = null;
        boolean refreshDone = false;
        boolean refreshDeletes = false;
        List<UUID> entryUUIDs = null;
        try {
            switch (type) {
                case NEW_COOKIE: {
                    cookie = new ASN1OctetString(valueElement.getValue());
                    break;
                }
                case REFRESH_DELETE:
                case REFRESH_PRESENT: {
                    refreshDone = true;
                    final ASN1Sequence s = valueElement.decodeAsSequence();
                    for (final ASN1Element e2 : s.elements()) {
                        switch (e2.getType()) {
                            case 4: {
                                cookie = ASN1OctetString.decodeAsOctetString(e2);
                                break;
                            }
                            case 1: {
                                refreshDone = ASN1Boolean.decodeAsBoolean(e2).booleanValue();
                                break;
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_VALUE_INVALID_SEQUENCE_TYPE.get(type.name(), StaticUtils.toHex(e2.getType())));
                            }
                        }
                    }
                    break;
                }
                case SYNC_ID_SET: {
                    final ASN1Sequence s = valueElement.decodeAsSequence();
                    for (final ASN1Element e2 : s.elements()) {
                        switch (e2.getType()) {
                            case 4: {
                                cookie = ASN1OctetString.decodeAsOctetString(e2);
                                break;
                            }
                            case 1: {
                                refreshDeletes = ASN1Boolean.decodeAsBoolean(e2).booleanValue();
                                break;
                            }
                            case 49: {
                                final ASN1Set uuidSet = ASN1Set.decodeAsSet(e2);
                                final ASN1Element[] uuidElements = uuidSet.elements();
                                entryUUIDs = new ArrayList<UUID>(uuidElements.length);
                                for (final ASN1Element uuidElement : uuidElements) {
                                    try {
                                        entryUUIDs.add(StaticUtils.decodeUUID(uuidElement.getValue()));
                                    }
                                    catch (final ParseException pe) {
                                        Debug.debugException(pe);
                                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_INVALID_UUID.get(type.name(), pe.getMessage()), pe);
                                    }
                                }
                                break;
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_VALUE_INVALID_SEQUENCE_TYPE.get(type.name(), StaticUtils.toHex(e2.getType())));
                            }
                        }
                    }
                    if (entryUUIDs == null) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_NO_UUID_SET.get(type.name()));
                    }
                    break;
                }
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SYNC_INFO_IR_VALUE_DECODING_ERROR.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
        return new ContentSyncInfoIntermediateResponse(type, value, cookie, refreshDone, refreshDeletes, entryUUIDs, r.getControls());
    }
    
    private static ASN1OctetString encodeValue(final ContentSyncInfoType type, final ASN1OctetString cookie, final boolean refreshDone, final List<UUID> entryUUIDs, final boolean refreshDeletes) {
        ASN1Element e = null;
        switch (type) {
            case NEW_COOKIE: {
                e = new ASN1OctetString(type.getType(), cookie.getValue());
                break;
            }
            case REFRESH_DELETE:
            case REFRESH_PRESENT: {
                final ArrayList<ASN1Element> l = new ArrayList<ASN1Element>(2);
                if (cookie != null) {
                    l.add(cookie);
                }
                if (!refreshDone) {
                    l.add(new ASN1Boolean(refreshDone));
                }
                e = new ASN1Sequence(type.getType(), l);
                break;
            }
            case SYNC_ID_SET: {
                final ArrayList<ASN1Element> l = new ArrayList<ASN1Element>(3);
                if (cookie != null) {
                    l.add(cookie);
                }
                if (refreshDeletes) {
                    l.add(new ASN1Boolean(refreshDeletes));
                }
                final ArrayList<ASN1Element> uuidElements = new ArrayList<ASN1Element>(entryUUIDs.size());
                for (final UUID uuid : entryUUIDs) {
                    uuidElements.add(new ASN1OctetString(StaticUtils.encodeUUID(uuid)));
                }
                l.add(new ASN1Set(uuidElements));
                e = new ASN1Sequence(type.getType(), l);
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected sync info type:  " + type.name()));
            }
        }
        return new ASN1OctetString(e.encode());
    }
    
    public ContentSyncInfoType getType() {
        return this.type;
    }
    
    public ASN1OctetString getCookie() {
        return this.cookie;
    }
    
    public boolean refreshDone() {
        return this.refreshDone;
    }
    
    public List<UUID> getEntryUUIDs() {
        return this.entryUUIDs;
    }
    
    public boolean refreshDeletes() {
        return this.refreshDeletes;
    }
    
    @Override
    public String getIntermediateResponseName() {
        return ControlMessages.INFO_INTERMEDIATE_RESPONSE_NAME_SYNC_INFO.get();
    }
    
    @Override
    public String valueToString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("syncInfoType='");
        buffer.append(this.type.name());
        buffer.append('\'');
        if (this.cookie != null) {
            buffer.append(" cookie='");
            StaticUtils.toHex(this.cookie.getValue(), buffer);
            buffer.append('\'');
        }
        switch (this.type) {
            case REFRESH_DELETE:
            case REFRESH_PRESENT: {
                buffer.append(" refreshDone='");
                buffer.append(this.refreshDone);
                buffer.append('\'');
                break;
            }
            case SYNC_ID_SET: {
                buffer.append(" entryUUIDs={");
                final Iterator<UUID> iterator = this.entryUUIDs.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next().toString());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(',');
                    }
                }
                buffer.append('}');
                break;
            }
        }
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ContentSyncInfoIntermediateResponse(");
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append("messageID=");
            buffer.append(messageID);
            buffer.append(", ");
        }
        buffer.append("type='");
        buffer.append(this.type.name());
        buffer.append('\'');
        if (this.cookie != null) {
            buffer.append(", cookie='");
            StaticUtils.toHex(this.cookie.getValue(), buffer);
            buffer.append("', ");
        }
        switch (this.type) {
            case REFRESH_DELETE:
            case REFRESH_PRESENT: {
                buffer.append(", refreshDone=");
                buffer.append(this.refreshDone);
                break;
            }
            case SYNC_ID_SET: {
                buffer.append(", entryUUIDs={");
                final Iterator<UUID> iterator = this.entryUUIDs.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(',');
                    }
                }
                buffer.append("}, refreshDeletes=");
                buffer.append(this.refreshDeletes);
                break;
            }
        }
        buffer.append(')');
    }
}
