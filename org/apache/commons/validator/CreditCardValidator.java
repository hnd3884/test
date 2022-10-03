package org.apache.commons.validator;

import java.util.Iterator;
import org.apache.commons.validator.util.Flags;
import java.util.ArrayList;
import java.util.Collection;

public class CreditCardValidator
{
    public static final int NONE = 0;
    public static final int AMEX = 1;
    public static final int VISA = 2;
    public static final int MASTERCARD = 4;
    public static final int DISCOVER = 8;
    private Collection cardTypes;
    
    public CreditCardValidator() {
        this(15);
    }
    
    public CreditCardValidator(final int options) {
        this.cardTypes = new ArrayList();
        final Flags f = new Flags(options);
        if (f.isOn(2L)) {
            this.cardTypes.add(new Visa());
        }
        if (f.isOn(1L)) {
            this.cardTypes.add(new Amex());
        }
        if (f.isOn(4L)) {
            this.cardTypes.add(new Mastercard());
        }
        if (f.isOn(8L)) {
            this.cardTypes.add(new Discover());
        }
    }
    
    public boolean isValid(final String card) {
        if (card == null || card.length() < 13 || card.length() > 19) {
            return false;
        }
        if (!this.luhnCheck(card)) {
            return false;
        }
        final Iterator types = this.cardTypes.iterator();
        while (types.hasNext()) {
            final CreditCardType type = types.next();
            if (type.matches(card)) {
                return true;
            }
        }
        return false;
    }
    
    public void addAllowedCardType(final CreditCardType type) {
        this.cardTypes.add(type);
    }
    
    protected boolean luhnCheck(final String cardNumber) {
        final int digits = cardNumber.length();
        final int oddOrEven = digits & 0x1;
        long sum = 0L;
        for (int count = 0; count < digits; ++count) {
            int digit = 0;
            try {
                digit = Integer.parseInt(cardNumber.charAt(count) + "");
            }
            catch (final NumberFormatException e) {
                return false;
            }
            if (((count & 0x1) ^ oddOrEven) == 0x0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        return sum != 0L && sum % 10L == 0L;
    }
    
    private class Visa implements CreditCardType
    {
        private static final String PREFIX = "4";
        
        public boolean matches(final String card) {
            return card.substring(0, 1).equals("4") && (card.length() == 13 || card.length() == 16);
        }
    }
    
    private class Amex implements CreditCardType
    {
        private static final String PREFIX = "34,37,";
        
        public boolean matches(final String card) {
            final String prefix2 = card.substring(0, 2) + ",";
            return "34,37,".indexOf(prefix2) != -1 && card.length() == 15;
        }
    }
    
    private class Discover implements CreditCardType
    {
        private static final String PREFIX = "6011";
        
        public boolean matches(final String card) {
            return card.substring(0, 4).equals("6011") && card.length() == 16;
        }
    }
    
    private class Mastercard implements CreditCardType
    {
        private static final String PREFIX = "51,52,53,54,55,";
        
        public boolean matches(final String card) {
            final String prefix2 = card.substring(0, 2) + ",";
            return "51,52,53,54,55,".indexOf(prefix2) != -1 && card.length() == 16;
        }
    }
    
    public interface CreditCardType
    {
        boolean matches(final String p0);
    }
}
