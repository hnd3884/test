package sun.util.locale.provider;

import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.text.Collator;
import java.util.Locale;
import java.util.Set;
import java.text.spi.CollatorProvider;

public class CollatorProviderImpl extends CollatorProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public CollatorProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public boolean isSupportedLocale(final Locale locale) {
        return LocaleProviderAdapter.isSupportedLocale(locale, this.type, this.langtags);
    }
    
    @Override
    public Collator getInstance(final Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }
        final String collationData = LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getCollationData();
        RuleBasedCollator ruleBasedCollator;
        try {
            ruleBasedCollator = new RuleBasedCollator("='\u200b'=\u200c=\u200d=\u200e=\u200f=\u0000 =\u0001 =\u0002 =\u0003 =\u0004=\u0005 =\u0006 =\u0007 =\b ='\t'='\u000b' =\u000e=\u000f ='\u0010' =\u0011 =\u0012 =\u0013=\u0014 =\u0015 =\u0016 =\u0017 =\u0018=\u0019 =\u001a =\u001b =\u001c =\u001d=\u001e =\u001f =\u007f=\u0080 =\u0081 =\u0082 =\u0083 =\u0084 =\u0085=\u0086 =\u0087 =\u0088 =\u0089 =\u008a =\u008b=\u008c =\u008d =\u008e =\u008f =\u0090 =\u0091=\u0092 =\u0093 =\u0094 =\u0095 =\u0096 =\u0097=\u0098 =\u0099 =\u009a =\u009b =\u009c =\u009d=\u009e =\u009f;' ';'�';'\u2000';'\u2001';'\u2002';'\u2003';'\u2004';'\u2005';'\u2006';'\u2007';'\u2008';'\u2009';'\u200a';'\u3000';'\ufeff';'\r' ;'\t' ;'\n';'\f';'\u000b';\u0301;\u0300;\u0306;\u0302;\u030c;\u030a;\u030d;\u0308;\u030b;\u0303;\u0307;\u0304;\u0337;\u0327;\u0328;\u0323;\u0332;\u0305;\u0309;\u030e;\u030f;\u0310;\u0311;\u0312;\u0313;\u0314;\u0315;\u0316;\u0317;\u0318;\u0319;\u031a;\u031b;\u031c;\u031d;\u031e;\u031f;\u0320;\u0321;\u0322;\u0324;\u0325;\u0326;\u0329;\u032a;\u032b;\u032c;\u032d;\u032e;\u032f;\u0330;\u0331;\u0333;\u0334;\u0335;\u0336;\u0338;\u0339;\u033a;\u033b;\u033c;\u033d;\u033e;\u033f;\u0342;\u0344;\u0345;\u0360;\u0361;\u0483;\u0484;\u0485;\u0486;\u20d0;\u20d1;\u20d2;\u20d3;\u20d4;\u20d5;\u20d6;\u20d7;\u20d8;\u20d9;\u20da;\u20db;\u20dc;\u20dd;\u20de;\u20df;\u20e0;\u20e1,'-';\u00ad;\u2010;\u2011;\u2012;\u2013;\u2014;\u2015;\u2212<'_'<�<','<';'<':'<'!'<�<'?'<�<'/'<'.'<�<'`'<'^'<�<'~'<�<�<'''<'\"'<�<�<'('<')'<'['<']'<'{'<'}'<�<�<�<�<'@'<�<\u0e3f<�<\u20a1<\u20a2<'$'<\u20ab<\u20ac<\u20a3<\u20a4<\u20a5<\u20a6<\u20a7<�<\u20a8<\u20aa<\u20a9<�<'*'<'\\'<'&'<'#'<'%'<'+'<�<\u00f7<\u00d7<'<'<'='<'>'<�<'|'<�<�<�<0<1<2<3<4<5<6<7<8<9<�<�<�<a,A<b,B<c,C<d,D<\u00f0,\u00d0<e,E<f,F<g,G<h,H<i,I<j,J<k,K<l,L<m,M<n,N<o,O<p,P<q,Q<r,R<s, S & SS,\u00df<t,T& TH, \u00de &TH, \u00fe <u,U<v,V<w,W<x,X<y,Y<z,Z&AE,\u00c6&AE,\u00e6&OE,\u0152&OE,\u0153" + collationData);
        }
        catch (final ParseException ex) {
            try {
                ruleBasedCollator = new RuleBasedCollator("='\u200b'=\u200c=\u200d=\u200e=\u200f=\u0000 =\u0001 =\u0002 =\u0003 =\u0004=\u0005 =\u0006 =\u0007 =\b ='\t'='\u000b' =\u000e=\u000f ='\u0010' =\u0011 =\u0012 =\u0013=\u0014 =\u0015 =\u0016 =\u0017 =\u0018=\u0019 =\u001a =\u001b =\u001c =\u001d=\u001e =\u001f =\u007f=\u0080 =\u0081 =\u0082 =\u0083 =\u0084 =\u0085=\u0086 =\u0087 =\u0088 =\u0089 =\u008a =\u008b=\u008c =\u008d =\u008e =\u008f =\u0090 =\u0091=\u0092 =\u0093 =\u0094 =\u0095 =\u0096 =\u0097=\u0098 =\u0099 =\u009a =\u009b =\u009c =\u009d=\u009e =\u009f;' ';'�';'\u2000';'\u2001';'\u2002';'\u2003';'\u2004';'\u2005';'\u2006';'\u2007';'\u2008';'\u2009';'\u200a';'\u3000';'\ufeff';'\r' ;'\t' ;'\n';'\f';'\u000b';\u0301;\u0300;\u0306;\u0302;\u030c;\u030a;\u030d;\u0308;\u030b;\u0303;\u0307;\u0304;\u0337;\u0327;\u0328;\u0323;\u0332;\u0305;\u0309;\u030e;\u030f;\u0310;\u0311;\u0312;\u0313;\u0314;\u0315;\u0316;\u0317;\u0318;\u0319;\u031a;\u031b;\u031c;\u031d;\u031e;\u031f;\u0320;\u0321;\u0322;\u0324;\u0325;\u0326;\u0329;\u032a;\u032b;\u032c;\u032d;\u032e;\u032f;\u0330;\u0331;\u0333;\u0334;\u0335;\u0336;\u0338;\u0339;\u033a;\u033b;\u033c;\u033d;\u033e;\u033f;\u0342;\u0344;\u0345;\u0360;\u0361;\u0483;\u0484;\u0485;\u0486;\u20d0;\u20d1;\u20d2;\u20d3;\u20d4;\u20d5;\u20d6;\u20d7;\u20d8;\u20d9;\u20da;\u20db;\u20dc;\u20dd;\u20de;\u20df;\u20e0;\u20e1,'-';\u00ad;\u2010;\u2011;\u2012;\u2013;\u2014;\u2015;\u2212<'_'<�<','<';'<':'<'!'<�<'?'<�<'/'<'.'<�<'`'<'^'<�<'~'<�<�<'''<'\"'<�<�<'('<')'<'['<']'<'{'<'}'<�<�<�<�<'@'<�<\u0e3f<�<\u20a1<\u20a2<'$'<\u20ab<\u20ac<\u20a3<\u20a4<\u20a5<\u20a6<\u20a7<�<\u20a8<\u20aa<\u20a9<�<'*'<'\\'<'&'<'#'<'%'<'+'<�<\u00f7<\u00d7<'<'<'='<'>'<�<'|'<�<�<�<0<1<2<3<4<5<6<7<8<9<�<�<�<a,A<b,B<c,C<d,D<\u00f0,\u00d0<e,E<f,F<g,G<h,H<i,I<j,J<k,K<l,L<m,M<n,N<o,O<p,P<q,Q<r,R<s, S & SS,\u00df<t,T& TH, \u00de &TH, \u00fe <u,U<v,V<w,W<x,X<y,Y<z,Z&AE,\u00c6&AE,\u00e6&OE,\u0152&OE,\u0153");
            }
            catch (final ParseException ex2) {
                throw new InternalError(ex2);
            }
        }
        ruleBasedCollator.setDecomposition(0);
        return (Collator)ruleBasedCollator.clone();
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}
