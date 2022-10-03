package com.lowagie.text.pdf;

import java.util.ArrayList;
import com.lowagie.text.pdf.collection.PdfTargetDictionary;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;

public class PdfAction extends PdfDictionary
{
    public static final int FIRSTPAGE = 1;
    public static final int PREVPAGE = 2;
    public static final int NEXTPAGE = 3;
    public static final int LASTPAGE = 4;
    public static final int PRINTDIALOG = 5;
    public static final int SUBMIT_EXCLUDE = 1;
    public static final int SUBMIT_INCLUDE_NO_VALUE_FIELDS = 2;
    public static final int SUBMIT_HTML_FORMAT = 4;
    public static final int SUBMIT_HTML_GET = 8;
    public static final int SUBMIT_COORDINATES = 16;
    public static final int SUBMIT_XFDF = 32;
    public static final int SUBMIT_INCLUDE_APPEND_SAVES = 64;
    public static final int SUBMIT_INCLUDE_ANNOTATIONS = 128;
    public static final int SUBMIT_PDF = 256;
    public static final int SUBMIT_CANONICAL_FORMAT = 512;
    public static final int SUBMIT_EXCL_NON_USER_ANNOTS = 1024;
    public static final int SUBMIT_EXCL_F_KEY = 2048;
    public static final int SUBMIT_EMBED_FORM = 8196;
    public static final int RESET_EXCLUDE = 1;
    
    public PdfAction() {
    }
    
    public PdfAction(final URL url) {
        this(url.toExternalForm());
    }
    
    public PdfAction(final URL url, final boolean isMap) {
        this(url.toExternalForm(), isMap);
    }
    
    public PdfAction(final String url) {
        this(url, false);
    }
    
    public PdfAction(final String url, final boolean isMap) {
        this.put(PdfName.S, PdfName.URI);
        this.put(PdfName.URI, new PdfString(url));
        if (isMap) {
            this.put(PdfName.ISMAP, PdfBoolean.PDFTRUE);
        }
    }
    
    PdfAction(final PdfIndirectReference destination) {
        this.put(PdfName.S, PdfName.GOTO);
        this.put(PdfName.D, destination);
    }
    
    public PdfAction(final String filename, final String name) {
        this.put(PdfName.S, PdfName.GOTOR);
        this.put(PdfName.F, new PdfString(filename));
        this.put(PdfName.D, new PdfString(name));
    }
    
    public PdfAction(final String filename, final int page) {
        this.put(PdfName.S, PdfName.GOTOR);
        this.put(PdfName.F, new PdfString(filename));
        this.put(PdfName.D, new PdfLiteral("[" + (page - 1) + " /FitH 10000]"));
    }
    
    public PdfAction(final int named) {
        this.put(PdfName.S, PdfName.NAMED);
        switch (named) {
            case 1: {
                this.put(PdfName.N, PdfName.FIRSTPAGE);
                break;
            }
            case 4: {
                this.put(PdfName.N, PdfName.LASTPAGE);
                break;
            }
            case 3: {
                this.put(PdfName.N, PdfName.NEXTPAGE);
                break;
            }
            case 2: {
                this.put(PdfName.N, PdfName.PREVPAGE);
                break;
            }
            case 5: {
                this.put(PdfName.S, PdfName.JAVASCRIPT);
                this.put(PdfName.JS, new PdfString("this.print(true);\r"));
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.named.action"));
            }
        }
    }
    
    public PdfAction(final String application, final String parameters, final String operation, final String defaultDir) {
        this.put(PdfName.S, PdfName.LAUNCH);
        if (parameters == null && operation == null && defaultDir == null) {
            this.put(PdfName.F, new PdfString(application));
        }
        else {
            final PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.F, new PdfString(application));
            if (parameters != null) {
                dic.put(PdfName.P, new PdfString(parameters));
            }
            if (operation != null) {
                dic.put(PdfName.O, new PdfString(operation));
            }
            if (defaultDir != null) {
                dic.put(PdfName.D, new PdfString(defaultDir));
            }
            this.put(PdfName.WIN, dic);
        }
    }
    
    public static PdfAction createLaunch(final String application, final String parameters, final String operation, final String defaultDir) {
        return new PdfAction(application, parameters, operation, defaultDir);
    }
    
    public static PdfAction rendition(final String file, final PdfFileSpecification fs, final String mimeType, final PdfIndirectReference ref) throws IOException {
        final PdfAction js = new PdfAction();
        js.put(PdfName.S, PdfName.RENDITION);
        js.put(PdfName.R, new PdfRendition(file, fs, mimeType));
        js.put(new PdfName("OP"), new PdfNumber(0));
        js.put(new PdfName("AN"), ref);
        return js;
    }
    
    public static PdfAction javaScript(final String code, final PdfWriter writer, final boolean unicode) {
        final PdfAction js = new PdfAction();
        js.put(PdfName.S, PdfName.JAVASCRIPT);
        if (unicode && code.length() < 50) {
            js.put(PdfName.JS, new PdfString(code, "UnicodeBig"));
        }
        else if (!unicode && code.length() < 100) {
            js.put(PdfName.JS, new PdfString(code));
        }
        else {
            try {
                final byte[] b = PdfEncodings.convertToBytes(code, unicode ? "UnicodeBig" : "PDF");
                final PdfStream stream = new PdfStream(b);
                stream.flateCompress(writer.getCompressionLevel());
                js.put(PdfName.JS, writer.addToBody(stream).getIndirectReference());
            }
            catch (final Exception e) {
                js.put(PdfName.JS, new PdfString(code));
            }
        }
        return js;
    }
    
    public static PdfAction javaScript(final String code, final PdfWriter writer) {
        return javaScript(code, writer, false);
    }
    
    static PdfAction createHide(final PdfObject obj, final boolean hide) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.HIDE);
        action.put(PdfName.T, obj);
        if (!hide) {
            action.put(PdfName.H, PdfBoolean.PDFFALSE);
        }
        return action;
    }
    
    public static PdfAction createHide(final PdfAnnotation annot, final boolean hide) {
        return createHide(annot.getIndirectReference(), hide);
    }
    
    public static PdfAction createHide(final String name, final boolean hide) {
        return createHide(new PdfString(name), hide);
    }
    
    static PdfArray buildArray(final Object[] names) {
        final PdfArray array = new PdfArray();
        for (int k = 0; k < names.length; ++k) {
            final Object obj = names[k];
            if (obj instanceof String) {
                array.add(new PdfString((String)obj));
            }
            else {
                if (!(obj instanceof PdfAnnotation)) {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("the.array.must.contain.string.or.pdfannotation"));
                }
                array.add(((PdfAnnotation)obj).getIndirectReference());
            }
        }
        return array;
    }
    
    public static PdfAction createHide(final Object[] names, final boolean hide) {
        return createHide(buildArray(names), hide);
    }
    
    public static PdfAction createSubmitForm(final String file, final Object[] names, final int flags) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.SUBMITFORM);
        final PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.F, new PdfString(file));
        dic.put(PdfName.FS, PdfName.URL);
        action.put(PdfName.F, dic);
        if (names != null) {
            action.put(PdfName.FIELDS, buildArray(names));
        }
        action.put(PdfName.FLAGS, new PdfNumber(flags));
        return action;
    }
    
    public static PdfAction createResetForm(final Object[] names, final int flags) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.RESETFORM);
        if (names != null) {
            action.put(PdfName.FIELDS, buildArray(names));
        }
        action.put(PdfName.FLAGS, new PdfNumber(flags));
        return action;
    }
    
    public static PdfAction createImportData(final String file) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.IMPORTDATA);
        action.put(PdfName.F, new PdfString(file));
        return action;
    }
    
    public void next(final PdfAction na) {
        final PdfObject nextAction = this.get(PdfName.NEXT);
        if (nextAction == null) {
            this.put(PdfName.NEXT, na);
        }
        else if (nextAction.isDictionary()) {
            final PdfArray array = new PdfArray(nextAction);
            array.add(na);
            this.put(PdfName.NEXT, array);
        }
        else {
            ((PdfArray)nextAction).add(na);
        }
    }
    
    public static PdfAction gotoLocalPage(final int page, final PdfDestination dest, final PdfWriter writer) {
        final PdfIndirectReference ref = writer.getPageReference(page);
        dest.addPage(ref);
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTO);
        action.put(PdfName.D, dest);
        return action;
    }
    
    public static PdfAction gotoLocalPage(final String dest, final boolean isName) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTO);
        if (isName) {
            action.put(PdfName.D, new PdfName(dest));
        }
        else {
            action.put(PdfName.D, new PdfString(dest, null));
        }
        return action;
    }
    
    public static PdfAction gotoRemotePage(final String filename, final String dest, final boolean isName, final boolean newWindow) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.F, new PdfString(filename));
        action.put(PdfName.S, PdfName.GOTOR);
        if (isName) {
            action.put(PdfName.D, new PdfName(dest));
        }
        else {
            action.put(PdfName.D, new PdfString(dest, null));
        }
        if (newWindow) {
            action.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
        }
        return action;
    }
    
    public static PdfAction gotoEmbedded(final String filename, final PdfTargetDictionary target, final String dest, final boolean isName, final boolean newWindow) {
        if (isName) {
            return gotoEmbedded(filename, target, new PdfName(dest), newWindow);
        }
        return gotoEmbedded(filename, target, new PdfString(dest, null), newWindow);
    }
    
    public static PdfAction gotoEmbedded(final String filename, final PdfTargetDictionary target, final PdfObject dest, final boolean newWindow) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTOE);
        action.put(PdfName.T, target);
        action.put(PdfName.D, dest);
        action.put(PdfName.NEWWINDOW, new PdfBoolean(newWindow));
        if (filename != null) {
            action.put(PdfName.F, new PdfString(filename));
        }
        return action;
    }
    
    public static PdfAction setOCGstate(final ArrayList state, final boolean preserveRB) {
        final PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.SETOCGSTATE);
        final PdfArray a = new PdfArray();
        for (int k = 0; k < state.size(); ++k) {
            final Object o = state.get(k);
            if (o != null) {
                if (o instanceof PdfIndirectReference) {
                    a.add((PdfObject)o);
                }
                else if (o instanceof PdfLayer) {
                    a.add(((PdfLayer)o).getRef());
                }
                else if (o instanceof PdfName) {
                    a.add((PdfObject)o);
                }
                else {
                    if (!(o instanceof String)) {
                        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.type.was.passed.in.state.1", o.getClass().getName()));
                    }
                    PdfName name = null;
                    final String s = (String)o;
                    if (s.equalsIgnoreCase("on")) {
                        name = PdfName.ON;
                    }
                    else if (s.equalsIgnoreCase("off")) {
                        name = PdfName.OFF;
                    }
                    else {
                        if (!s.equalsIgnoreCase("toggle")) {
                            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.string.1.was.passed.in.state.only.on.off.and.toggle.are.allowed", s));
                        }
                        name = PdfName.TOGGLE;
                    }
                    a.add(name);
                }
            }
        }
        action.put(PdfName.STATE, a);
        if (!preserveRB) {
            action.put(PdfName.PRESERVERB, PdfBoolean.PDFFALSE);
        }
        return action;
    }
}
