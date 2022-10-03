package com.sun.corba.se.impl.dynamicany;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynFixed;

public class DynFixedImpl extends DynAnyBasicImpl implements DynFixed
{
    private DynFixedImpl() {
        this(null, null, false);
    }
    
    protected DynFixedImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
    }
    
    protected DynFixedImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.index = -1;
    }
    
    @Override
    public String get_value() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.any.extract_fixed().toString();
    }
    
    @Override
    public boolean set_value(final String s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        int fixed_digits = 0;
        boolean b = true;
        try {
            fixed_digits = this.any.type().fixed_digits();
            this.any.type().fixed_scale();
        }
        catch (final BadKind badKind) {}
        String s2 = s.trim();
        if (s2.length() == 0) {
            throw new TypeMismatch();
        }
        String s3 = "";
        if (s2.charAt(0) == '-') {
            s3 = "-";
            s2 = s2.substring(1);
        }
        else if (s2.charAt(0) == '+') {
            s3 = "+";
            s2 = s2.substring(1);
        }
        int n = s2.indexOf(100);
        if (n == -1) {
            n = s2.indexOf(68);
        }
        if (n != -1) {
            s2 = s2.substring(0, n);
        }
        if (s2.length() == 0) {
            throw new TypeMismatch();
        }
        final int index = s2.indexOf(46);
        String substring;
        String s4;
        int n2;
        if (index == -1) {
            substring = s2;
            s4 = null;
            n2 = substring.length();
        }
        else if (index == 0) {
            substring = null;
            s4 = s2;
            n2 = s4.length();
        }
        else {
            substring = s2.substring(0, index);
            s4 = s2.substring(index + 1);
            n2 = substring.length() + s4.length();
        }
        if (n2 > fixed_digits) {
            b = false;
            if (substring.length() < fixed_digits) {
                s4 = s4.substring(0, fixed_digits - substring.length());
            }
            else {
                if (substring.length() != fixed_digits) {
                    throw new InvalidValue();
                }
                s4 = null;
            }
        }
        BigDecimal bigDecimal;
        try {
            new BigInteger(substring);
            if (s4 == null) {
                bigDecimal = new BigDecimal(s3 + substring);
            }
            else {
                new BigInteger(s4);
                bigDecimal = new BigDecimal(s3 + substring + "." + s4);
            }
        }
        catch (final NumberFormatException ex) {
            throw new TypeMismatch();
        }
        this.any.insert_fixed(bigDecimal, this.any.type());
        return b;
    }
    
    @Override
    public String toString() {
        int fixed_digits = 0;
        int fixed_scale = 0;
        try {
            fixed_digits = this.any.type().fixed_digits();
            fixed_scale = this.any.type().fixed_scale();
        }
        catch (final BadKind badKind) {}
        return "DynFixed with value=" + this.get_value() + ", digits=" + fixed_digits + ", scale=" + fixed_scale;
    }
}
