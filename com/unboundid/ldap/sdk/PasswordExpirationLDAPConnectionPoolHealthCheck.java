package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.controls.PasswordExpiringControl;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.experimental.DraftBeheraLDAPPasswordPolicy10ResponseControl;
import com.unboundid.ldap.sdk.controls.PasswordExpiredControl;
import java.io.Writer;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordExpirationLDAPConnectionPoolHealthCheck extends LDAPConnectionPoolHealthCheck
{
    private final AtomicLong lastWarningTime;
    private final Long millisBetweenRepeatWarnings;
    private final OutputStream outputStream;
    private final Writer writer;
    
    public PasswordExpirationLDAPConnectionPoolHealthCheck() {
        this(null, null, null);
    }
    
    public PasswordExpirationLDAPConnectionPoolHealthCheck(final OutputStream outputStream) {
        this(outputStream, null, null);
    }
    
    public PasswordExpirationLDAPConnectionPoolHealthCheck(final Writer writer) {
        this(null, writer, null);
    }
    
    public PasswordExpirationLDAPConnectionPoolHealthCheck(final OutputStream outputStream, final Long millisBetweenRepeatWarnings) {
        this(outputStream, null, millisBetweenRepeatWarnings);
    }
    
    public PasswordExpirationLDAPConnectionPoolHealthCheck(final Writer writer, final Long millisBetweenRepeatWarnings) {
        this(null, writer, millisBetweenRepeatWarnings);
    }
    
    private PasswordExpirationLDAPConnectionPoolHealthCheck(final OutputStream outputStream, final Writer writer, final Long millisBetweenRepeatWarnings) {
        this.lastWarningTime = new AtomicLong(0L);
        this.outputStream = outputStream;
        this.writer = writer;
        this.millisBetweenRepeatWarnings = millisBetweenRepeatWarnings;
    }
    
    @Override
    public void ensureConnectionValidAfterAuthentication(final LDAPConnection connection, final BindResult bindResult) throws LDAPException {
        final PasswordExpiredControl expiredControl = PasswordExpiredControl.get(bindResult);
        if (expiredControl != null) {
            if (bindResult.getResultCode() == ResultCode.SUCCESS) {
                throw new LDAPException(ResultCode.ADMIN_LIMIT_EXCEEDED, LDAPMessages.ERR_PW_EXP_WITH_SUCCESS.get());
            }
            if (bindResult.getDiagnosticMessage() == null) {
                throw new LDAPException(bindResult.getResultCode(), LDAPMessages.ERR_PW_EXP_WITH_FAILURE_NO_MSG.get());
            }
            throw new LDAPException(bindResult.getResultCode(), LDAPMessages.ERR_PW_EXP_WITH_FAILURE_WITH_MSG.get(bindResult.getDiagnosticMessage()));
        }
        else {
            final DraftBeheraLDAPPasswordPolicy10ResponseControl pwPolicyControl = DraftBeheraLDAPPasswordPolicy10ResponseControl.get(bindResult);
            if (pwPolicyControl != null && pwPolicyControl.getErrorType() != null) {
                ResultCode resultCode;
                if (bindResult.getResultCode() == ResultCode.SUCCESS) {
                    resultCode = ResultCode.ADMIN_LIMIT_EXCEEDED;
                }
                else {
                    resultCode = bindResult.getResultCode();
                }
                String message;
                if (bindResult.getDiagnosticMessage() == null) {
                    message = LDAPMessages.ERR_PW_POLICY_ERROR_NO_MSG.get(pwPolicyControl.getErrorType().toString());
                }
                else {
                    message = LDAPMessages.ERR_PW_POLICY_ERROR_WITH_MSG.get(pwPolicyControl.getErrorType().toString(), bindResult.getDiagnosticMessage());
                }
                throw new LDAPException(resultCode, message);
            }
            if (this.millisBetweenRepeatWarnings == null) {
                if (!this.lastWarningTime.compareAndSet(0L, System.currentTimeMillis())) {
                    return;
                }
            }
            else if (this.millisBetweenRepeatWarnings > 0L) {
                final long millisSinceLastWarning = System.currentTimeMillis() - this.lastWarningTime.get();
                if (millisSinceLastWarning < this.millisBetweenRepeatWarnings) {
                    return;
                }
            }
            String message2 = null;
            if (pwPolicyControl != null && pwPolicyControl.getWarningType() != null) {
                switch (pwPolicyControl.getWarningType()) {
                    case TIME_BEFORE_EXPIRATION: {
                        message2 = LDAPMessages.WARN_PW_EXPIRING.get(StaticUtils.secondsToHumanReadableDuration(pwPolicyControl.getWarningValue()));
                        break;
                    }
                    case GRACE_LOGINS_REMAINING: {
                        message2 = LDAPMessages.WARN_PW_POLICY_GRACE_LOGIN.get(pwPolicyControl.getWarningValue());
                        break;
                    }
                }
            }
            final PasswordExpiringControl expiringControl = PasswordExpiringControl.get(bindResult);
            if (message2 == null && expiringControl != null) {
                message2 = LDAPMessages.WARN_PW_EXPIRING.get(StaticUtils.secondsToHumanReadableDuration(expiringControl.getSecondsUntilExpiration()));
            }
            if (message2 != null) {
                this.warn(message2);
            }
        }
    }
    
    private void warn(final String message) throws LDAPException {
        if (this.outputStream != null) {
            try {
                this.outputStream.write(StaticUtils.getBytes(message + StaticUtils.EOL));
                this.outputStream.flush();
                this.lastWarningTime.set(System.currentTimeMillis());
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        else {
            if (this.writer == null) {
                this.lastWarningTime.set(System.currentTimeMillis());
                throw new LDAPException(ResultCode.ADMIN_LIMIT_EXCEEDED, message);
            }
            try {
                this.writer.write(message + StaticUtils.EOL);
                this.writer.flush();
                this.lastWarningTime.set(System.currentTimeMillis());
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("WarnAboutPasswordExpirationLDAPConnectionPoolHealthCheck(");
        buffer.append("throwExceptionOnWarning=");
        buffer.append(this.outputStream == null && this.writer == null);
        if (this.millisBetweenRepeatWarnings == null) {
            buffer.append(", suppressSubsequentWarnings=true");
        }
        else if (this.millisBetweenRepeatWarnings > 0L) {
            buffer.append(", millisBetweenRepeatWarnings=");
            buffer.append(this.millisBetweenRepeatWarnings);
        }
        else {
            buffer.append(", suppressSubsequentWarnings=false");
        }
        buffer.append(')');
    }
}
