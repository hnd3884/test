package org.bouncycastle.mail.smime.validator;

import org.bouncycastle.i18n.ErrorBundle;
import org.bouncycastle.i18n.LocalizedException;

public class SignedMailValidatorException extends LocalizedException
{
    public SignedMailValidatorException(final ErrorBundle errorBundle, final Throwable t) {
        super(errorBundle, t);
    }
    
    public SignedMailValidatorException(final ErrorBundle errorBundle) {
        super(errorBundle);
    }
}
