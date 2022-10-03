package com.sun.corba.se.impl.encoding;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public final class CodeSetComponentInfo
{
    private CodeSetComponent forCharData;
    private CodeSetComponent forWCharData;
    public static final CodeSetComponentInfo JAVASOFT_DEFAULT_CODESETS;
    public static final CodeSetContext LOCAL_CODE_SETS;
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeSetComponentInfo)) {
            return false;
        }
        final CodeSetComponentInfo codeSetComponentInfo = (CodeSetComponentInfo)o;
        return this.forCharData.equals(codeSetComponentInfo.forCharData) && this.forWCharData.equals(codeSetComponentInfo.forWCharData);
    }
    
    @Override
    public int hashCode() {
        return this.forCharData.hashCode() ^ this.forWCharData.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CodeSetComponentInfo(");
        sb.append("char_data:");
        sb.append(this.forCharData.toString());
        sb.append(" wchar_data:");
        sb.append(this.forWCharData.toString());
        sb.append(")");
        return sb.toString();
    }
    
    public CodeSetComponentInfo() {
        this.forCharData = CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.forCharData;
        this.forWCharData = CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.forWCharData;
    }
    
    public CodeSetComponentInfo(final CodeSetComponent forCharData, final CodeSetComponent forWCharData) {
        this.forCharData = forCharData;
        this.forWCharData = forWCharData;
    }
    
    public void read(final MarshalInputStream marshalInputStream) {
        (this.forCharData = new CodeSetComponent()).read(marshalInputStream);
        (this.forWCharData = new CodeSetComponent()).read(marshalInputStream);
    }
    
    public void write(final MarshalOutputStream marshalOutputStream) {
        this.forCharData.write(marshalOutputStream);
        this.forWCharData.write(marshalOutputStream);
    }
    
    public CodeSetComponent getCharComponent() {
        return this.forCharData;
    }
    
    public CodeSetComponent getWCharComponent() {
        return this.forWCharData;
    }
    
    public static CodeSetComponent createFromString(final String s) {
        final ORBUtilSystemException value = ORBUtilSystemException.get("rpc.encoding");
        if (s == null || s.length() == 0) {
            throw value.badCodeSetString();
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ", ", false);
        int intValue;
        int[] array;
        try {
            intValue = Integer.decode(stringTokenizer.nextToken());
            if (OSFCodeSetRegistry.lookupEntry(intValue) == null) {
                throw value.unknownNativeCodeset(new Integer(intValue));
            }
            final ArrayList list = new ArrayList(10);
            while (stringTokenizer.hasMoreTokens()) {
                final Integer decode = Integer.decode(stringTokenizer.nextToken());
                if (OSFCodeSetRegistry.lookupEntry(decode) == null) {
                    throw value.unknownConversionCodeSet(decode);
                }
                list.add(decode);
            }
            array = new int[list.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (int)list.get(i);
            }
        }
        catch (final NumberFormatException ex) {
            throw value.invalidCodeSetNumber(ex);
        }
        catch (final NoSuchElementException ex2) {
            throw value.invalidCodeSetString(ex2, s);
        }
        return new CodeSetComponent(intValue, array);
    }
    
    static {
        JAVASOFT_DEFAULT_CODESETS = new CodeSetComponentInfo(new CodeSetComponent(OSFCodeSetRegistry.ISO_8859_1.getNumber(), new int[] { OSFCodeSetRegistry.UTF_8.getNumber(), OSFCodeSetRegistry.ISO_646.getNumber() }), new CodeSetComponent(OSFCodeSetRegistry.UTF_16.getNumber(), new int[] { OSFCodeSetRegistry.UCS_2.getNumber() }));
        LOCAL_CODE_SETS = new CodeSetContext(OSFCodeSetRegistry.ISO_8859_1.getNumber(), OSFCodeSetRegistry.UTF_16.getNumber());
    }
    
    public static final class CodeSetComponent
    {
        int nativeCodeSet;
        int[] conversionCodeSets;
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CodeSetComponent)) {
                return false;
            }
            final CodeSetComponent codeSetComponent = (CodeSetComponent)o;
            return this.nativeCodeSet == codeSetComponent.nativeCodeSet && Arrays.equals(this.conversionCodeSets, codeSetComponent.conversionCodeSets);
        }
        
        @Override
        public int hashCode() {
            int nativeCodeSet = this.nativeCodeSet;
            for (int i = 0; i < this.conversionCodeSets.length; ++i) {
                nativeCodeSet = 37 * nativeCodeSet + this.conversionCodeSets[i];
            }
            return nativeCodeSet;
        }
        
        public CodeSetComponent() {
        }
        
        public CodeSetComponent(final int nativeCodeSet, final int[] conversionCodeSets) {
            this.nativeCodeSet = nativeCodeSet;
            if (conversionCodeSets == null) {
                this.conversionCodeSets = new int[0];
            }
            else {
                this.conversionCodeSets = conversionCodeSets;
            }
        }
        
        public void read(final MarshalInputStream marshalInputStream) {
            this.nativeCodeSet = marshalInputStream.read_ulong();
            final int read_long = marshalInputStream.read_long();
            marshalInputStream.read_ulong_array(this.conversionCodeSets = new int[read_long], 0, read_long);
        }
        
        public void write(final MarshalOutputStream marshalOutputStream) {
            marshalOutputStream.write_ulong(this.nativeCodeSet);
            marshalOutputStream.write_long(this.conversionCodeSets.length);
            marshalOutputStream.write_ulong_array(this.conversionCodeSets, 0, this.conversionCodeSets.length);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("CodeSetComponent(");
            sb.append("native:");
            sb.append(Integer.toHexString(this.nativeCodeSet));
            sb.append(" conversion:");
            if (this.conversionCodeSets == null) {
                sb.append("null");
            }
            else {
                for (int i = 0; i < this.conversionCodeSets.length; ++i) {
                    sb.append(Integer.toHexString(this.conversionCodeSets[i]));
                    sb.append(' ');
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
    
    public static final class CodeSetContext
    {
        private int char_data;
        private int wchar_data;
        
        public CodeSetContext() {
        }
        
        public CodeSetContext(final int char_data, final int wchar_data) {
            this.char_data = char_data;
            this.wchar_data = wchar_data;
        }
        
        public void read(final MarshalInputStream marshalInputStream) {
            this.char_data = marshalInputStream.read_ulong();
            this.wchar_data = marshalInputStream.read_ulong();
        }
        
        public void write(final MarshalOutputStream marshalOutputStream) {
            marshalOutputStream.write_ulong(this.char_data);
            marshalOutputStream.write_ulong(this.wchar_data);
        }
        
        public int getCharCodeSet() {
            return this.char_data;
        }
        
        public int getWCharCodeSet() {
            return this.wchar_data;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("CodeSetContext char set: ");
            sb.append(Integer.toHexString(this.char_data));
            sb.append(" wchar set: ");
            sb.append(Integer.toHexString(this.wchar_data));
            return sb.toString();
        }
    }
}
