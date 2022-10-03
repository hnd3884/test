package sun.security.tools.policytool;

import java.util.ListResourceBundle;

public class Resources_ja extends ListResourceBundle
{
    private static final Object[][] contents;
    
    public Object[][] getContents() {
        return Resources_ja.contents;
    }
    
    static {
        contents = new Object[][] { { "NEWLINE", "\n" }, { "Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.", "\u8b66\u544a: \u5225\u540d{0}\u306e\u516c\u958b\u9375\u304c\u5b58\u5728\u3057\u307e\u305b\u3093\u3002\u30ad\u30fc\u30b9\u30c8\u30a2\u304c\u6b63\u3057\u304f\u69cb\u6210\u3055\u308c\u3066\u3044\u308b\u3053\u3068\u3092\u78ba\u8a8d\u3057\u3066\u304f\u3060\u3055\u3044\u3002" }, { "Warning.Class.not.found.class", "\u8b66\u544a: \u30af\u30e9\u30b9\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: {0}" }, { "Warning.Invalid.argument.s.for.constructor.arg", "\u8b66\u544a: \u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf\u306e\u5f15\u6570\u304c\u7121\u52b9\u3067\u3059: {0}" }, { "Illegal.Principal.Type.type", "\u4e0d\u6b63\u306a\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u30bf\u30a4\u30d7: {0}" }, { "Illegal.option.option", "\u4e0d\u6b63\u306a\u30aa\u30d7\u30b7\u30e7\u30f3: {0}" }, { "Usage.policytool.options.", "\u4f7f\u7528\u65b9\u6cd5: policytool [options]" }, { ".file.file.policy.file.location", "  [-file <file>]  \u30dd\u30ea\u30b7\u30fc\u30fb\u30d5\u30a1\u30a4\u30eb\u306e\u5834\u6240" }, { "New", "\u65b0\u898f(&N)" }, { "Open", "\u958b\u304f(&O)..." }, { "Save", "\u4fdd\u5b58(&S)" }, { "Save.As", "\u5225\u540d\u4fdd\u5b58(&A)..." }, { "View.Warning.Log", "\u8b66\u544a\u30ed\u30b0\u306e\u8868\u793a(&W)" }, { "Exit", "\u7d42\u4e86(&X)" }, { "Add.Policy.Entry", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30a8\u30f3\u30c8\u30ea\u306e\u8ffd\u52a0(&A)" }, { "Edit.Policy.Entry", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30a8\u30f3\u30c8\u30ea\u306e\u7de8\u96c6(&E)" }, { "Remove.Policy.Entry", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30a8\u30f3\u30c8\u30ea\u306e\u524a\u9664(&R)" }, { "Edit", "\u7de8\u96c6(&E)" }, { "Retain", "\u4fdd\u6301" }, { "Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes", "\u8b66\u544a: \u30d5\u30a1\u30a4\u30eb\u540d\u306b\u30a8\u30b9\u30b1\u30fc\u30d7\u3055\u308c\u305f\u30d0\u30c3\u30af\u30b9\u30e9\u30c3\u30b7\u30e5\u6587\u5b57\u304c\u542b\u307e\u308c\u3066\u3044\u308b\u53ef\u80fd\u6027\u304c\u3042\u308a\u307e\u3059\u3002\u30d0\u30c3\u30af\u30b9\u30e9\u30c3\u30b7\u30e5\u6587\u5b57\u3092\u30a8\u30b9\u30b1\u30fc\u30d7\u3059\u308b\u5fc5\u8981\u306f\u3042\u308a\u307e\u305b\u3093(\u30c4\u30fc\u30eb\u306f\u30dd\u30ea\u30b7\u30fc\u5185\u5bb9\u3092\u6c38\u7d9a\u30b9\u30c8\u30a2\u306b\u66f8\u304d\u8fbc\u3080\u3068\u304d\u306b\u3001\u5fc5\u8981\u306b\u5fdc\u3058\u3066\u6587\u5b57\u3092\u30a8\u30b9\u30b1\u30fc\u30d7\u3057\u307e\u3059)\u3002\n\n\u5165\u529b\u6e08\u306e\u540d\u524d\u3092\u4fdd\u6301\u3059\u308b\u306b\u306f\u300c\u4fdd\u6301\u300d\u3092\u30af\u30ea\u30c3\u30af\u3057\u3001\u540d\u524d\u3092\u7de8\u96c6\u3059\u308b\u306b\u306f\u300c\u7de8\u96c6\u300d\u3092\u30af\u30ea\u30c3\u30af\u3057\u3066\u304f\u3060\u3055\u3044\u3002" }, { "Add.Public.Key.Alias", "\u516c\u958b\u9375\u306e\u5225\u540d\u306e\u8ffd\u52a0" }, { "Remove.Public.Key.Alias", "\u516c\u958b\u9375\u306e\u5225\u540d\u3092\u524a\u9664" }, { "File", "\u30d5\u30a1\u30a4\u30eb(&F)" }, { "KeyStore", "\u30ad\u30fc\u30b9\u30c8\u30a2(&K)" }, { "Policy.File.", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30d5\u30a1\u30a4\u30eb:" }, { "Could.not.open.policy.file.policyFile.e.toString.", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30d5\u30a1\u30a4\u30eb\u3092\u958b\u3051\u307e\u305b\u3093\u3067\u3057\u305f: {0}: {1}" }, { "Policy.Tool", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30c4\u30fc\u30eb" }, { "Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.", "\u30dd\u30ea\u30b7\u30fc\u69cb\u6210\u3092\u958b\u304f\u3068\u304d\u306b\u30a8\u30e9\u30fc\u304c\u767a\u751f\u3057\u307e\u3057\u305f\u3002\u8a73\u7d30\u306f\u8b66\u544a\u30ed\u30b0\u3092\u53c2\u7167\u3057\u3066\u304f\u3060\u3055\u3044\u3002" }, { "Error", "\u30a8\u30e9\u30fc" }, { "OK", "OK" }, { "Status", "\u72b6\u614b" }, { "Warning", "\u8b66\u544a" }, { "Permission.", "\u30a2\u30af\u30bb\u30b9\u6a29:                                                       " }, { "Principal.Type.", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u30bf\u30a4\u30d7:" }, { "Principal.Name.", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u540d\u524d:" }, { "Target.Name.", "\u30bf\u30fc\u30b2\u30c3\u30c8\u540d:                                                    " }, { "Actions.", "\u30a2\u30af\u30b7\u30e7\u30f3:                                                             " }, { "OK.to.overwrite.existing.file.filename.", "\u65e2\u5b58\u306e\u30d5\u30a1\u30a4\u30eb{0}\u306b\u4e0a\u66f8\u304d\u3057\u307e\u3059\u304b\u3002" }, { "Cancel", "\u53d6\u6d88" }, { "CodeBase.", "CodeBase(&C):" }, { "SignedBy.", "SignedBy(&S):" }, { "Add.Principal", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u8ffd\u52a0(&A)" }, { "Edit.Principal", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u7de8\u96c6(&E)" }, { "Remove.Principal", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u524a\u9664(&R)" }, { "Principals.", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb(&P):" }, { ".Add.Permission", "  \u30a2\u30af\u30bb\u30b9\u6a29\u306e\u8ffd\u52a0(&D)" }, { ".Edit.Permission", "  \u30a2\u30af\u30bb\u30b9\u6a29\u306e\u7de8\u96c6(&I)" }, { "Remove.Permission", "\u30a2\u30af\u30bb\u30b9\u6a29\u306e\u524a\u9664(&M)" }, { "Done", "\u5b8c\u4e86" }, { "KeyStore.URL.", "\u30ad\u30fc\u30b9\u30c8\u30a2URL(&U):" }, { "KeyStore.Type.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30bf\u30a4\u30d7(&T):" }, { "KeyStore.Provider.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d7\u30ed\u30d0\u30a4\u30c0(&P):" }, { "KeyStore.Password.URL.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d1\u30b9\u30ef\u30fc\u30c9URL(&W):" }, { "Principals", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb" }, { ".Edit.Principal.", "  \u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u7de8\u96c6:" }, { ".Add.New.Principal.", "  \u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u65b0\u898f\u8ffd\u52a0:" }, { "Permissions", "\u30a2\u30af\u30bb\u30b9\u6a29" }, { ".Edit.Permission.", "  \u30a2\u30af\u30bb\u30b9\u6a29\u306e\u7de8\u96c6:" }, { ".Add.New.Permission.", "  \u65b0\u898f\u30a2\u30af\u30bb\u30b9\u6a29\u306e\u8ffd\u52a0:" }, { "Signed.By.", "\u7f72\u540d\u8005:" }, { "Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name", "\u30ef\u30a4\u30eb\u30c9\u30ab\u30fc\u30c9\u540d\u306e\u306a\u3044\u30ef\u30a4\u30eb\u30c9\u30ab\u30fc\u30c9\u30fb\u30af\u30e9\u30b9\u3092\u4f7f\u7528\u3057\u3066\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u3092\u6307\u5b9a\u3059\u308b\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093" }, { "Cannot.Specify.Principal.without.a.Name", "\u540d\u524d\u3092\u4f7f\u7528\u305b\u305a\u306b\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u3092\u6307\u5b9a\u3059\u308b\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093" }, { "Permission.and.Target.Name.must.have.a.value", "\u30a2\u30af\u30bb\u30b9\u6a29\u3068\u30bf\u30fc\u30b2\u30c3\u30c8\u540d\u306f\u3001\u5024\u3092\u4fdd\u6301\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Remove.this.Policy.Entry.", "\u3053\u306e\u30dd\u30ea\u30b7\u30fc\u30fb\u30a8\u30f3\u30c8\u30ea\u3092\u524a\u9664\u3057\u307e\u3059\u304b\u3002" }, { "Overwrite.File", "\u30d5\u30a1\u30a4\u30eb\u3092\u4e0a\u66f8\u304d\u3057\u307e\u3059" }, { "Policy.successfully.written.to.filename", "\u30dd\u30ea\u30b7\u30fc\u306e{0}\u3078\u306e\u66f8\u8fbc\u307f\u306b\u6210\u529f\u3057\u307e\u3057\u305f" }, { "null.filename", "\u30d5\u30a1\u30a4\u30eb\u540d\u304cnull\u3067\u3059" }, { "Save.changes.", "\u5909\u66f4\u3092\u4fdd\u5b58\u3057\u307e\u3059\u304b\u3002" }, { "Yes", "\u306f\u3044(&Y)" }, { "No", "\u3044\u3044\u3048(&N)" }, { "Policy.Entry", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30a8\u30f3\u30c8\u30ea" }, { "Save.Changes", "\u5909\u66f4\u3092\u4fdd\u5b58\u3057\u307e\u3059" }, { "No.Policy.Entry.selected", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30a8\u30f3\u30c8\u30ea\u304c\u9078\u629e\u3055\u308c\u3066\u3044\u307e\u305b\u3093" }, { "Unable.to.open.KeyStore.ex.toString.", "\u30ad\u30fc\u30b9\u30c8\u30a2{0}\u3092\u958b\u3051\u307e\u305b\u3093" }, { "No.principal.selected", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u304c\u9078\u629e\u3055\u308c\u3066\u3044\u307e\u305b\u3093" }, { "No.permission.selected", "\u30a2\u30af\u30bb\u30b9\u6a29\u304c\u9078\u629e\u3055\u308c\u3066\u3044\u307e\u305b\u3093" }, { "name", "\u540d\u524d" }, { "configuration.type", "\u69cb\u6210\u30bf\u30a4\u30d7" }, { "environment.variable.name", "\u74b0\u5883\u5909\u6570\u540d" }, { "library.name", "\u30e9\u30a4\u30d6\u30e9\u30ea\u540d" }, { "package.name", "\u30d1\u30c3\u30b1\u30fc\u30b8\u540d" }, { "policy.type", "\u30dd\u30ea\u30b7\u30fc\u30fb\u30bf\u30a4\u30d7" }, { "property.name", "\u30d7\u30ed\u30d1\u30c6\u30a3\u540d" }, { "provider.name", "\u30d7\u30ed\u30d0\u30a4\u30c0\u540d" }, { "url", "URL" }, { "method.list", "\u30e1\u30bd\u30c3\u30c9\u30fb\u30ea\u30b9\u30c8" }, { "request.headers.list", "\u30ea\u30af\u30a8\u30b9\u30c8\u30fb\u30d8\u30c3\u30c0\u30fc\u30fb\u30ea\u30b9\u30c8" }, { "Principal.List", "\u30d7\u30ea\u30f3\u30b7\u30d1\u30eb\u306e\u30ea\u30b9\u30c8" }, { "Permission.List", "\u30a2\u30af\u30bb\u30b9\u6a29\u306e\u30ea\u30b9\u30c8" }, { "Code.Base", "\u30b3\u30fc\u30c9\u30fb\u30d9\u30fc\u30b9" }, { "KeyStore.U.R.L.", "\u30ad\u30fc\u30b9\u30c8\u30a2U R L:" }, { "KeyStore.Password.U.R.L.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d1\u30b9\u30ef\u30fc\u30c9U R L:" } };
    }
}
