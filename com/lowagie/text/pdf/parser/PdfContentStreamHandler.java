package com.lowagie.text.pdf.parser;

import java.util.ListIterator;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PRTokeniser;
import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfIndirectReference;
import java.util.Locale;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.CMapAwareDocumentFont;
import java.util.Iterator;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfLiteral;
import java.util.HashMap;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.Stack;

public class PdfContentStreamHandler
{
    Stack<Collection<TextAssemblyBuffer>> textFragmentStreams;
    Stack<String> contextNames;
    Collection<TextAssemblyBuffer> textFragments;
    public Map<String, ContentOperator> operators;
    public Stack<GraphicsState> gsStack;
    public Matrix textMatrix;
    public Matrix textLineMatrix;
    boolean useContainerMarkup;
    TextAssembler renderListener;
    
    public PdfContentStreamHandler(final TextAssembler renderListener) {
        this.textFragmentStreams = new Stack<Collection<TextAssemblyBuffer>>();
        this.contextNames = new Stack<String>();
        this.textFragments = new ArrayList<TextAssemblyBuffer>();
        this.renderListener = renderListener;
        this.installDefaultOperators();
        this.reset();
    }
    
    public void registerContentOperator(final ContentOperator operator) {
        final String operatorString = operator.getOperatorName();
        if (this.operators.containsKey(operatorString)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("operator.1.already.registered", operatorString));
        }
        this.operators.put(operatorString, operator);
    }
    
    protected void installDefaultOperators() {
        this.operators = new HashMap<String, ContentOperator>();
        this.registerContentOperator(new PushGraphicsState());
        this.registerContentOperator(new PopGraphicsState());
        this.registerContentOperator(new ModifyCurrentTransformationMatrix());
        this.registerContentOperator(new ProcessGraphicsStateResource());
        final SetTextCharacterSpacing tcOperator = new SetTextCharacterSpacing();
        this.registerContentOperator(tcOperator);
        final SetTextWordSpacing twOperator = new SetTextWordSpacing();
        this.registerContentOperator(twOperator);
        this.registerContentOperator(new SetTextHorizontalScaling());
        final SetTextLeading tlOperator = new SetTextLeading();
        this.registerContentOperator(tlOperator);
        this.registerContentOperator(new SetTextFont());
        this.registerContentOperator(new SetTextRenderMode());
        this.registerContentOperator(new SetTextRise());
        this.registerContentOperator(new BeginText());
        this.registerContentOperator(new EndText());
        final TextMoveStartNextLine tdOperator = new TextMoveStartNextLine();
        this.registerContentOperator(tdOperator);
        this.registerContentOperator(new TextMoveStartNextLineWithLeading(tdOperator, tlOperator));
        this.registerContentOperator(new TextSetTextMatrix());
        final TextMoveNextLine tstarOperator = new TextMoveNextLine(tdOperator);
        this.registerContentOperator(tstarOperator);
        final ShowText tjOperator = new ShowText();
        this.registerContentOperator(new ShowText());
        final MoveNextLineAndShowText tickOperator = new MoveNextLineAndShowText(tstarOperator, tjOperator);
        this.registerContentOperator(tickOperator);
        this.registerContentOperator(new MoveNextLineAndShowTextWithSpacing(twOperator, tcOperator, tickOperator));
        this.registerContentOperator(new ShowTextArray());
        this.registerContentOperator(new BeginMarked());
        this.registerContentOperator(new BeginMarkedDict());
        this.registerContentOperator(new EndMarked());
        this.registerContentOperator(new Do());
    }
    
    public ContentOperator lookupOperator(final String operatorName) {
        return this.operators.get(operatorName);
    }
    
    public void invokeOperator(final PdfLiteral operator, final ArrayList<PdfObject> operands, final PdfDictionary resources) {
        final String operatorName = operator.toString();
        final ContentOperator op = this.lookupOperator(operatorName);
        if (op == null) {
            return;
        }
        op.invoke(operands, this, resources);
    }
    
    void popContext() {
        final String contextName = this.contextNames.pop();
        final Collection<TextAssemblyBuffer> newBuffer = this.textFragmentStreams.pop();
        this.renderListener.reset();
        for (final TextAssemblyBuffer fragment : this.textFragments) {
            fragment.accumulate(this.renderListener, contextName);
        }
        final FinalText contextResult = this.renderListener.endParsingContext(contextName);
        if (contextResult != null && contextResult.getText().length() > 0) {
            newBuffer.add(contextResult);
        }
        this.textFragments = newBuffer;
    }
    
    void pushContext(final String newContextName) {
        this.contextNames.push(newContextName);
        this.textFragmentStreams.push(this.textFragments);
        this.textFragments = new ArrayList<TextAssemblyBuffer>();
    }
    
    GraphicsState gs() {
        return this.gsStack.peek();
    }
    
    public void reset() {
        if (this.gsStack == null || this.gsStack.isEmpty()) {
            this.gsStack = new Stack<GraphicsState>();
        }
        this.gsStack.add(new GraphicsState());
        this.textMatrix = null;
        this.textLineMatrix = null;
    }
    
    protected Matrix getCurrentTextMatrix() {
        return this.textMatrix;
    }
    
    protected Matrix getCurrentTextLineMatrix() {
        return this.textLineMatrix;
    }
    
    void applyTextAdjust(final float tj) {
        final float adjustBy = -tj / 1000.0f * this.gs().fontSize * this.gs().horizontalScaling;
        this.textMatrix = new Matrix(adjustBy, 0.0f).multiply(this.textMatrix);
    }
    
    public CMapAwareDocumentFont getCurrentFont() {
        return this.gs().font;
    }
    
    void displayPdfString(final PdfString string) {
        final ParsedText renderInfo = new ParsedText(string, this.gs(), this.textMatrix);
        if (this.contextNames.peek() != null) {
            this.textFragments.add(renderInfo);
        }
        this.textMatrix = new Matrix(renderInfo.getUnscaledTextWidth(this.gs()), 0.0f).multiply(this.textMatrix);
    }
    
    public String getResultantText() {
        if (this.contextNames.size() > 0) {
            throw new RuntimeException("can't get text with unprocessed stack items");
        }
        final StringBuffer res = new StringBuffer();
        for (final TextAssemblyBuffer fragment : this.textFragments) {
            res.append(fragment.getText());
        }
        return res.toString();
    }
    
    static class ShowTextArray implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "TJ";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfArray array = operands.get(0);
            float tj = 0.0f;
            final Iterator<?> i = array.listIterator();
            while (i.hasNext()) {
                final Object entryObj = i.next();
                if (entryObj instanceof PdfString) {
                    handler.displayPdfString((PdfString)entryObj);
                    tj = 0.0f;
                }
                else {
                    tj = ((PdfNumber)entryObj).floatValue();
                    handler.applyTextAdjust(tj);
                }
            }
        }
    }
    
    static class BeginText implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "BT";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            handler.textMatrix = new Matrix();
            handler.textLineMatrix = handler.textMatrix;
        }
    }
    
    static class EndText implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "ET";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            handler.textMatrix = null;
            handler.textLineMatrix = null;
        }
    }
    
    static class ModifyCurrentTransformationMatrix implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "cm";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final float a = operands.get(0).floatValue();
            final float b = operands.get(1).floatValue();
            final float c = operands.get(2).floatValue();
            final float d = operands.get(3).floatValue();
            final float e = operands.get(4).floatValue();
            final float f = operands.get(5).floatValue();
            final Matrix matrix = new Matrix(a, b, c, d, e, f);
            final GraphicsState gs = handler.gsStack.peek();
            gs.ctm = gs.ctm.multiply(matrix);
        }
    }
    
    static class MoveNextLineAndShowText implements ContentOperator
    {
        private final TextMoveNextLine textMoveNextLine;
        private final ShowText showText;
        
        @Override
        public String getOperatorName() {
            return "'";
        }
        
        public MoveNextLineAndShowText(final TextMoveNextLine textMoveNextLine, final ShowText showText) {
            this.textMoveNextLine = textMoveNextLine;
            this.showText = showText;
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            this.textMoveNextLine.invoke(new ArrayList<PdfObject>(0), handler, resources);
            this.showText.invoke(operands, handler, resources);
        }
    }
    
    static class MoveNextLineAndShowTextWithSpacing implements ContentOperator
    {
        private final SetTextWordSpacing setTextWordSpacing;
        private final SetTextCharacterSpacing setTextCharacterSpacing;
        private final MoveNextLineAndShowText moveNextLineAndShowText;
        
        @Override
        public String getOperatorName() {
            return "\"";
        }
        
        public MoveNextLineAndShowTextWithSpacing(final SetTextWordSpacing setTextWordSpacing, final SetTextCharacterSpacing setTextCharacterSpacing, final MoveNextLineAndShowText moveNextLineAndShowText) {
            this.setTextWordSpacing = setTextWordSpacing;
            this.setTextCharacterSpacing = setTextCharacterSpacing;
            this.moveNextLineAndShowText = moveNextLineAndShowText;
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber aw = operands.get(0);
            final PdfNumber ac = operands.get(1);
            final PdfString string = operands.get(2);
            final ArrayList<PdfObject> twOperands = new ArrayList<PdfObject>(1);
            twOperands.add(0, aw);
            this.setTextWordSpacing.invoke(twOperands, handler, resources);
            final ArrayList<PdfObject> tcOperands = new ArrayList<PdfObject>(1);
            tcOperands.add(0, ac);
            this.setTextCharacterSpacing.invoke(tcOperands, handler, resources);
            final ArrayList<PdfObject> tickOperands = new ArrayList<PdfObject>(1);
            tickOperands.add(0, string);
            this.moveNextLineAndShowText.invoke(tickOperands, handler, resources);
        }
    }
    
    static class PopGraphicsState implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Q";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            handler.gsStack.pop();
        }
    }
    
    static class ProcessGraphicsStateResource implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "gs";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfName dictionaryName = operands.get(0);
            final PdfDictionary extGState = resources.getAsDict(PdfName.EXTGSTATE);
            if (extGState == null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("resources.do.not.contain.extgstate.entry.unable.to.process.operator.1", this.getOperatorName()));
            }
            final PdfDictionary gsDic = extGState.getAsDict(dictionaryName);
            if (gsDic == null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("1.is.an.unknown.graphics.state.dictionary", dictionaryName));
            }
            final PdfArray fontParameter = gsDic.getAsArray(PdfName.FONT);
            if (fontParameter != null) {
                final CMapAwareDocumentFont font = new CMapAwareDocumentFont((PRIndirectReference)fontParameter.getPdfObject(0));
                final float size = fontParameter.getAsNumber(1).floatValue();
                handler.gs().font = font;
                handler.gs().fontSize = size;
            }
        }
    }
    
    static class PushGraphicsState implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "q";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final GraphicsState gs = handler.gsStack.peek();
            final GraphicsState copy = new GraphicsState(gs);
            handler.gsStack.push(copy);
        }
    }
    
    static class SetTextCharacterSpacing implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tc";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber charSpace = operands.get(0);
            handler.gs().characterSpacing = charSpace.floatValue();
        }
    }
    
    static class SetTextFont implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tf";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfName fontResourceName = operands.get(0);
            final float size = operands.get(1).floatValue();
            final PdfDictionary fontsDictionary = resources.getAsDict(PdfName.FONT);
            final CMapAwareDocumentFont font = new CMapAwareDocumentFont((PRIndirectReference)fontsDictionary.get(fontResourceName));
            handler.gs().font = font;
            handler.gs().fontSize = size;
        }
    }
    
    static class TextSetTextMatrix implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tm";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final float a = operands.get(0).floatValue();
            final float b = operands.get(1).floatValue();
            final float c = operands.get(2).floatValue();
            final float d = operands.get(3).floatValue();
            final float e = operands.get(4).floatValue();
            final float f = operands.get(5).floatValue();
            handler.textLineMatrix = new Matrix(a, b, c, d, e, f);
            handler.textMatrix = handler.textLineMatrix;
        }
    }
    
    static class TextMoveStartNextLineWithLeading implements ContentOperator
    {
        private final TextMoveStartNextLine moveStartNextLine;
        private final SetTextLeading setTextLeading;
        
        @Override
        public String getOperatorName() {
            return "TD";
        }
        
        public TextMoveStartNextLineWithLeading(final TextMoveStartNextLine moveStartNextLine, final SetTextLeading setTextLeading) {
            this.moveStartNextLine = moveStartNextLine;
            this.setTextLeading = setTextLeading;
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final float ty = operands.get(1).floatValue();
            final ArrayList<PdfObject> tlOperands = new ArrayList<PdfObject>(1);
            tlOperands.add(0, new PdfNumber(-ty));
            this.setTextLeading.invoke(tlOperands, handler, resources);
            this.moveStartNextLine.invoke(operands, handler, resources);
        }
    }
    
    static class ShowText implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tj";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfString string = operands.get(0);
            handler.displayPdfString(string);
        }
    }
    
    static class TextMoveNextLine implements ContentOperator
    {
        private final TextMoveStartNextLine moveStartNextLine;
        
        @Override
        public String getOperatorName() {
            return "T*";
        }
        
        public TextMoveNextLine(final TextMoveStartNextLine moveStartNextLine) {
            this.moveStartNextLine = moveStartNextLine;
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final ArrayList<PdfObject> tdoperands = new ArrayList<PdfObject>(2);
            tdoperands.add(0, new PdfNumber(0));
            tdoperands.add(1, new PdfNumber(-handler.gs().leading));
            this.moveStartNextLine.invoke(tdoperands, handler, resources);
        }
    }
    
    static class TextMoveStartNextLine implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Td";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final float tx = operands.get(0).floatValue();
            final float ty = operands.get(1).floatValue();
            final Matrix translationMatrix = new Matrix(tx, ty);
            handler.textMatrix = translationMatrix.multiply(handler.textLineMatrix);
            handler.textLineMatrix = handler.textMatrix;
        }
    }
    
    static class SetTextRenderMode implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tr";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber render = operands.get(0);
            handler.gs().renderMode = render.intValue();
        }
    }
    
    static class SetTextRise implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Ts";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber rise = operands.get(0);
            handler.gs().rise = rise.floatValue();
        }
    }
    
    static class SetTextLeading implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "TL";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber leading = operands.get(0);
            handler.gs().leading = leading.floatValue();
        }
    }
    
    static class SetTextHorizontalScaling implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tz";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber scale = operands.get(0);
            handler.gs().horizontalScaling = scale.floatValue();
        }
    }
    
    static class SetTextWordSpacing implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Tw";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfNumber wordSpace = operands.get(0);
            handler.gs().wordSpacing = wordSpace.floatValue();
        }
    }
    
    private static class BeginMarked implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "BMC";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfName tagName = operands.get(0);
            final String realName = tagName.toString().substring(1).toLowerCase(Locale.ROOT);
            if ("artifact".equals(tagName) || "placedpdf".equals(tagName)) {
                handler.pushContext(null);
            }
            else {
                handler.pushContext(realName);
            }
        }
    }
    
    private static class BeginMarkedDict implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "BDC";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            String tagName = operands.get(0).toString().substring(1).toLowerCase(Locale.ROOT);
            if ("artifact".equals(tagName) || "placedpdf".equals(tagName) || handler.contextNames.peek() == null) {
                tagName = null;
            }
            else if ("l".equals(tagName)) {
                tagName = "ul";
            }
            final PdfDictionary attrs = this.getBDCDictionary(operands, resources);
            if (attrs != null && tagName != null) {
                final PdfString alternateText = attrs.getAsString(PdfName.E);
                if (alternateText != null) {
                    handler.pushContext(tagName);
                    handler.textFragments.add(new FinalText(alternateText.toString()));
                    handler.popContext();
                    handler.pushContext(null);
                    return;
                }
                if (attrs.get(PdfName.TYPE) != null) {
                    tagName = "";
                }
            }
            handler.pushContext(tagName);
        }
        
        private PdfDictionary getBDCDictionary(final ArrayList<PdfObject> operands, final PdfDictionary resources) {
            PdfObject o = operands.get(1);
            if (o.isName()) {
                final PdfDictionary properties = resources.getAsDict(PdfName.PROPERTIES);
                final PdfIndirectReference ir = properties.getAsIndirectObject((PdfName)o);
                if (ir != null) {
                    o = ir.getIndRef();
                }
                else {
                    o = properties.getAsDict((PdfName)o);
                }
            }
            final PdfDictionary attrs = (PdfDictionary)o;
            return attrs;
        }
    }
    
    private static class EndMarked implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "EMC";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            handler.popContext();
        }
    }
    
    private class Do implements ContentOperator
    {
        @Override
        public String getOperatorName() {
            return "Do";
        }
        
        @Override
        public void invoke(final ArrayList<PdfObject> operands, final PdfContentStreamHandler handler, final PdfDictionary resources) {
            final PdfObject firstOperand = operands.get(0);
            if (firstOperand instanceof PdfName) {
                final PdfName name = (PdfName)firstOperand;
                final PdfDictionary dictionary = resources.getAsDict(PdfName.XOBJECT);
                if (dictionary == null) {
                    return;
                }
                final PdfStream stream = (PdfStream)dictionary.getDirectObject(name);
                final PdfName subType = stream.getAsName(PdfName.SUBTYPE);
                if (PdfName.FORM.equals(subType)) {
                    final PdfDictionary resources2 = stream.getAsDict(PdfName.RESOURCES);
                    byte[] data = null;
                    try {
                        data = this.getContentBytesFromPdfObject(stream);
                    }
                    catch (final IOException ex) {
                        throw new ExceptionConverter(ex);
                    }
                    new PushGraphicsState().invoke(operands, handler, resources);
                    this.processContent(data, resources2);
                    new PopGraphicsState().invoke(operands, handler, resources);
                }
            }
        }
        
        private void processContent(final byte[] contentBytes, final PdfDictionary resources) {
            try {
                final PdfContentParser ps = new PdfContentParser(new PRTokeniser(contentBytes));
                final ArrayList<PdfObject> operands = new ArrayList<PdfObject>();
                while (ps.parse(operands).size() > 0) {
                    final PdfLiteral operator = operands.get(operands.size() - 1);
                    PdfContentStreamHandler.this.invokeOperator(operator, operands, resources);
                }
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        private byte[] getContentBytesFromPdfObject(final PdfObject object) throws IOException {
            switch (object.type()) {
                case 10: {
                    return this.getContentBytesFromPdfObject(PdfReader.getPdfObject(object));
                }
                case 7: {
                    return PdfReader.getStreamBytes((PRStream)PdfReader.getPdfObject(object));
                }
                case 5: {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ListIterator<PdfObject> iter = ((PdfArray)object).listIterator();
                    while (iter.hasNext()) {
                        final PdfObject element = iter.next();
                        baos.write(this.getContentBytesFromPdfObject(element));
                    }
                    return baos.toByteArray();
                }
                default: {
                    throw new IllegalStateException("Unsupported type: " + object.getClass().getCanonicalName());
                }
            }
        }
    }
}
