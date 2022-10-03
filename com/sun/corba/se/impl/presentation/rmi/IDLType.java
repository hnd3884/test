package com.sun.corba.se.impl.presentation.rmi;

public class IDLType
{
    private Class cl_;
    private String[] modules_;
    private String memberName_;
    
    public IDLType(final Class cl_, final String[] modules_, final String memberName_) {
        this.cl_ = cl_;
        this.modules_ = modules_;
        this.memberName_ = memberName_;
    }
    
    public IDLType(final Class clazz, final String s) {
        this(clazz, new String[0], s);
    }
    
    public Class getJavaClass() {
        return this.cl_;
    }
    
    public String[] getModules() {
        return this.modules_;
    }
    
    public String makeConcatenatedName(final char c, final boolean b) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.modules_.length; ++i) {
            String mangleIDLKeywordClash = this.modules_[i];
            if (i > 0) {
                sb.append(c);
            }
            if (b && IDLNameTranslatorImpl.isIDLKeyword(mangleIDLKeywordClash)) {
                mangleIDLKeywordClash = IDLNameTranslatorImpl.mangleIDLKeywordClash(mangleIDLKeywordClash);
            }
            sb.append(mangleIDLKeywordClash);
        }
        return sb.toString();
    }
    
    public String getModuleName() {
        return this.makeConcatenatedName('_', false);
    }
    
    public String getExceptionName() {
        final String concatenatedName = this.makeConcatenatedName('/', true);
        final String s = "Exception";
        String s2 = this.memberName_;
        if (s2.endsWith(s)) {
            s2 = s2.substring(0, s2.length() - s.length());
        }
        final String string = s2 + "Ex";
        if (concatenatedName.length() == 0) {
            return "IDL:" + string + ":1.0";
        }
        return "IDL:" + concatenatedName + '/' + string + ":1.0";
    }
    
    public String getMemberName() {
        return this.memberName_;
    }
    
    public boolean hasModule() {
        return this.modules_.length > 0;
    }
}
