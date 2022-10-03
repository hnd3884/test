package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OperationPurposeRequestControl extends Control
{
    public static final String OPERATION_PURPOSE_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.19";
    private static final byte TYPE_APP_NAME = Byte.MIN_VALUE;
    private static final byte TYPE_APP_VERSION = -127;
    private static final byte TYPE_CODE_LOCATION = -126;
    private static final byte TYPE_REQUEST_PURPOSE = -125;
    private static final long serialVersionUID = -5552051862785419833L;
    private final String applicationName;
    private final String applicationVersion;
    private final String codeLocation;
    private final String requestPurpose;
    
    public OperationPurposeRequestControl(final String applicationName, final String applicationVersion, final int codeLocationFrames, final String requestPurpose) {
        this(false, applicationName, applicationVersion, generateStackTrace(codeLocationFrames), requestPurpose);
    }
    
    public OperationPurposeRequestControl(final boolean isCritical, final String applicationName, final String applicationVersion, final String codeLocation, final String requestPurpose) {
        super("1.3.6.1.4.1.30221.2.5.19", isCritical, encodeValue(applicationName, applicationVersion, codeLocation, requestPurpose));
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.codeLocation = codeLocation;
        this.requestPurpose = requestPurpose;
    }
    
    public OperationPurposeRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OP_PURPOSE_NO_VALUE.get());
        }
        ASN1Element[] valueElements;
        try {
            valueElements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OP_PURPOSE_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (valueElements.length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OP_PURPOSE_VALUE_SEQUENCE_EMPTY.get());
        }
        String appName = null;
        String appVersion = null;
        String codeLoc = null;
        String reqPurpose = null;
        for (final ASN1Element e2 : valueElements) {
            switch (e2.getType()) {
                case Byte.MIN_VALUE: {
                    appName = ASN1OctetString.decodeAsOctetString(e2).stringValue();
                    break;
                }
                case -127: {
                    appVersion = ASN1OctetString.decodeAsOctetString(e2).stringValue();
                    break;
                }
                case -126: {
                    codeLoc = ASN1OctetString.decodeAsOctetString(e2).stringValue();
                    break;
                }
                case -125: {
                    reqPurpose = ASN1OctetString.decodeAsOctetString(e2).stringValue();
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OP_PURPOSE_VALUE_UNSUPPORTED_ELEMENT.get(StaticUtils.toHex(e2.getType())));
                }
            }
        }
        this.applicationName = appName;
        this.applicationVersion = appVersion;
        this.codeLocation = codeLoc;
        this.requestPurpose = reqPurpose;
    }
    
    private static String generateStackTrace(final int numFrames) {
        final StringBuilder buffer = new StringBuilder();
        final int n = (numFrames > 0) ? numFrames : Integer.MAX_VALUE;
        int c = 0;
        boolean skip = true;
        for (final StackTraceElement e : Thread.currentThread().getStackTrace()) {
            final String className = e.getClassName();
            if (className.equals(OperationPurposeRequestControl.class.getName())) {
                skip = false;
            }
            else if (!skip) {
                if (buffer.length() > 0) {
                    buffer.append(' ');
                }
                final int lastPeriodPos = className.lastIndexOf(46);
                if (lastPeriodPos > 0) {
                    buffer.append(className.substring(lastPeriodPos + 1));
                }
                else {
                    buffer.append(className);
                }
                buffer.append('.');
                buffer.append(e.getMethodName());
                buffer.append(':');
                buffer.append(e.getLineNumber());
                if (++c >= n) {
                    break;
                }
            }
        }
        return buffer.toString();
    }
    
    private static ASN1OctetString encodeValue(final String applicationName, final String applicationVersion, final String codeLocation, final String requestPurpose) {
        Validator.ensureFalse(applicationName == null && applicationVersion == null && codeLocation == null && requestPurpose == null);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        if (applicationName != null) {
            elements.add(new ASN1OctetString((byte)(-128), applicationName));
        }
        if (applicationVersion != null) {
            elements.add(new ASN1OctetString((byte)(-127), applicationVersion));
        }
        if (codeLocation != null) {
            elements.add(new ASN1OctetString((byte)(-126), codeLocation));
        }
        if (requestPurpose != null) {
            elements.add(new ASN1OctetString((byte)(-125), requestPurpose));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getApplicationName() {
        return this.applicationName;
    }
    
    public String getApplicationVersion() {
        return this.applicationVersion;
    }
    
    public String getCodeLocation() {
        return this.codeLocation;
    }
    
    public String getRequestPurpose() {
        return this.requestPurpose;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_OP_PURPOSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("OperationPurposeRequestControl(isCritical=");
        buffer.append(this.isCritical());
        if (this.applicationName != null) {
            buffer.append(", appName='");
            buffer.append(this.applicationName);
            buffer.append('\'');
        }
        if (this.applicationVersion != null) {
            buffer.append(", appVersion='");
            buffer.append(this.applicationVersion);
            buffer.append('\'');
        }
        if (this.codeLocation != null) {
            buffer.append(", codeLocation='");
            buffer.append(this.codeLocation);
            buffer.append('\'');
        }
        if (this.requestPurpose != null) {
            buffer.append(", purpose='");
            buffer.append(this.requestPurpose);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
