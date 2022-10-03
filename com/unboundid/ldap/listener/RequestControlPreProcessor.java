package com.unboundid.ldap.listener;

import java.util.Iterator;
import com.unboundid.ldap.sdk.unboundidds.controls.IgnoreNoUserModificationRequestControl;
import com.unboundid.ldap.sdk.controls.VirtualListViewRequestControl;
import com.unboundid.ldap.sdk.controls.TransactionSpecificationRequestControl;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.ldap.sdk.controls.SubentriesRequestControl;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV1RequestControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PermissiveModifyRequestControl;
import com.unboundid.ldap.sdk.controls.ManageDsaITRequestControl;
import com.unboundid.ldap.sdk.controls.DontUseCopyRequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Control;
import java.util.List;

final class RequestControlPreProcessor
{
    private RequestControlPreProcessor() {
    }
    
    static Map<String, Control> processControls(final byte requestOpType, final List<Control> controls) throws LDAPException {
        final Map<String, Control> m = new LinkedHashMap<String, Control>(StaticUtils.computeMapCapacity(controls.size()));
        for (final Control control : controls) {
            final String oid = control.getOID();
            if (oid.equals("1.3.6.1.1.12")) {
                switch (requestOpType) {
                    case 74:
                    case 99:
                    case 102:
                    case 104:
                    case 108:
                    case 110: {
                        if (m.put(oid, new AssertionRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("2.16.840.1.113730.3.4.16")) {
                switch (requestOpType) {
                    case 96: {
                        if (m.put(oid, new AuthorizationIdentityRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.1.22")) {
                switch (requestOpType) {
                    case 99:
                    case 110: {
                        if (m.put(oid, new DontUseCopyRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("2.16.840.1.113730.3.4.2")) {
                switch (requestOpType) {
                    case 74:
                    case 99:
                    case 102:
                    case 104:
                    case 108:
                    case 110: {
                        if (m.put(oid, new ManageDsaITRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.2.840.113556.1.4.1413")) {
                switch (requestOpType) {
                    case 102: {
                        if (m.put(oid, new PermissiveModifyRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.1.13.2")) {
                switch (requestOpType) {
                    case 102:
                    case 104:
                    case 108: {
                        if (m.put(oid, new PostReadRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.1.13.1")) {
                switch (requestOpType) {
                    case 74:
                    case 102:
                    case 108: {
                        if (m.put(oid, new PreReadRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("2.16.840.1.113730.3.4.12")) {
                switch (requestOpType) {
                    case 74:
                    case 99:
                    case 102:
                    case 104:
                    case 108:
                    case 110: {
                        if (m.put(oid, new ProxiedAuthorizationV1RequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("2.16.840.1.113730.3.4.18")) {
                switch (requestOpType) {
                    case 74:
                    case 99:
                    case 102:
                    case 104:
                    case 108:
                    case 110: {
                        if (m.put(oid, new ProxiedAuthorizationV2RequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.2.840.113556.1.4.473")) {
                switch (requestOpType) {
                    case 99: {
                        if (m.put(oid, new ServerSideSortRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.2.840.113556.1.4.319")) {
                switch (requestOpType) {
                    case 99: {
                        if (m.put(oid, new SimplePagedResultsControl(control.getOID(), control.isCritical(), control.getValue())) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.4.1.7628.5.101.1")) {
                switch (requestOpType) {
                    case 99: {
                        if (m.put(oid, new SubentriesRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.2.840.113556.1.4.805")) {
                switch (requestOpType) {
                    case 74: {
                        if (m.put(oid, new SubtreeDeleteRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.1.21.2")) {
                switch (requestOpType) {
                    case 74:
                    case 102:
                    case 104:
                    case 108: {
                        if (m.put(oid, new TransactionSpecificationRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("2.16.840.1.113730.3.4.9")) {
                switch (requestOpType) {
                    case 99: {
                        if (m.put(oid, new VirtualListViewRequestControl(control)) != null) {
                            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
                        }
                        continue;
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.4.1.4203.1.10.2")) {
                switch (requestOpType) {
                    case 74:
                    case 102:
                    case 104:
                    case 108: {
                        throw new LDAPException(ResultCode.NO_OPERATION, ListenerMessages.ERR_CONTROL_PROCESSOR_NO_OPERATION.get());
                    }
                    default: {
                        if (control.isCritical()) {
                            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                        }
                        continue;
                    }
                }
            }
            else if (oid.equals("1.3.6.1.4.1.30221.2.5.5")) {
                if (requestOpType == 104) {
                    m.put(oid, new IgnoreNoUserModificationRequestControl(control));
                }
                else {
                    if (control.isCritical()) {
                        throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
                    }
                    continue;
                }
            }
            else if (oid.equals("1.3.6.1.4.1.30221.2.5.18")) {
                m.put(oid, control);
            }
            else {
                if (control.isCritical()) {
                    throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_CONTROL_PROCESSOR_UNSUPPORTED_CONTROL.get(oid));
                }
                continue;
            }
        }
        if (m.containsKey("2.16.840.1.113730.3.4.12") && m.containsKey("2.16.840.1.113730.3.4.18")) {
            throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_MULTIPLE_PROXY_CONTROLS.get());
        }
        if (m.containsKey("2.16.840.1.113730.3.4.9")) {
            if (m.containsKey("1.2.840.113556.1.4.319")) {
                throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_CONTROL_PROCESSOR_VLV_AND_PAGED_RESULTS.get());
            }
            if (!m.containsKey("1.2.840.113556.1.4.473")) {
                throw new LDAPException(ResultCode.SORT_CONTROL_MISSING, ListenerMessages.ERR_CONTROL_PROCESSOR_VLV_WITHOUT_SORT.get());
            }
        }
        return m;
    }
}
