package org.bouncycastle.cms;

public class PasswordRecipientId extends RecipientId
{
    public PasswordRecipientId() {
        super(3);
    }
    
    @Override
    public int hashCode() {
        return 3;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof PasswordRecipientId;
    }
    
    @Override
    public Object clone() {
        return new PasswordRecipientId();
    }
    
    public boolean match(final Object o) {
        return o instanceof PasswordRecipientInformation;
    }
}
