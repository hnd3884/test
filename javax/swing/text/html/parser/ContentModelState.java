package javax.swing.text.html.parser;

class ContentModelState
{
    ContentModel model;
    long value;
    ContentModelState next;
    
    public ContentModelState(final ContentModel contentModel) {
        this(contentModel, null, 0L);
    }
    
    ContentModelState(final Object o, final ContentModelState contentModelState) {
        this(o, contentModelState, 0L);
    }
    
    ContentModelState(final Object o, final ContentModelState next, final long value) {
        this.model = (ContentModel)o;
        this.next = next;
        this.value = value;
    }
    
    public ContentModel getModel() {
        ContentModel contentModel = this.model;
        for (int n = 0; n < this.value; ++n) {
            if (contentModel.next == null) {
                return null;
            }
            contentModel = contentModel.next;
        }
        return contentModel;
    }
    
    public boolean terminate() {
        switch (this.model.type) {
            case 43: {
                if (this.value == 0L && !this.model.empty()) {
                    return false;
                }
                return this.next == null || this.next.terminate();
            }
            case 42:
            case 63: {
                return this.next == null || this.next.terminate();
            }
            case 124: {
                for (ContentModel next = (ContentModel)this.model.content; next != null; next = next.next) {
                    if (next.empty()) {
                        return this.next == null || this.next.terminate();
                    }
                }
                return false;
            }
            case 38: {
                ContentModel next2 = (ContentModel)this.model.content;
                int n = 0;
                while (next2 != null) {
                    if ((this.value & 1L << n) == 0x0L && !next2.empty()) {
                        return false;
                    }
                    ++n;
                    next2 = next2.next;
                }
                return this.next == null || this.next.terminate();
            }
            case 44: {
                ContentModel contentModel = (ContentModel)this.model.content;
                for (int n2 = 0; n2 < this.value; ++n2, contentModel = contentModel.next) {}
                while (contentModel != null && contentModel.empty()) {
                    contentModel = contentModel.next;
                }
                return contentModel == null && (this.next == null || this.next.terminate());
            }
            default: {
                return false;
            }
        }
    }
    
    public Element first() {
        switch (this.model.type) {
            case 38:
            case 42:
            case 63:
            case 124: {
                return null;
            }
            case 43: {
                return this.model.first();
            }
            case 44: {
                ContentModel next = (ContentModel)this.model.content;
                for (int n = 0; n < this.value; ++n, next = next.next) {}
                return next.first();
            }
            default: {
                return this.model.first();
            }
        }
    }
    
    public ContentModelState advance(final Object o) {
        switch (this.model.type) {
            case 43: {
                if (this.model.first(o)) {
                    return new ContentModelState(this.model.content, new ContentModelState(this.model, this.next, this.value + 1L)).advance(o);
                }
                if (this.value == 0L) {
                    break;
                }
                if (this.next != null) {
                    return this.next.advance(o);
                }
                return null;
            }
            case 42: {
                if (this.model.first(o)) {
                    return new ContentModelState(this.model.content, this).advance(o);
                }
                if (this.next != null) {
                    return this.next.advance(o);
                }
                return null;
            }
            case 63: {
                if (this.model.first(o)) {
                    return new ContentModelState(this.model.content, this.next).advance(o);
                }
                if (this.next != null) {
                    return this.next.advance(o);
                }
                return null;
            }
            case 124: {
                for (ContentModel next = (ContentModel)this.model.content; next != null; next = next.next) {
                    if (next.first(o)) {
                        return new ContentModelState(next, this.next).advance(o);
                    }
                }
                break;
            }
            case 44: {
                ContentModel next2 = (ContentModel)this.model.content;
                for (int n = 0; n < this.value; ++n, next2 = next2.next) {}
                if (!next2.first(o) && !next2.empty()) {
                    break;
                }
                if (next2.next == null) {
                    return new ContentModelState(next2, this.next).advance(o);
                }
                return new ContentModelState(next2, new ContentModelState(this.model, this.next, this.value + 1L)).advance(o);
            }
            case 38: {
                ContentModel next3 = (ContentModel)this.model.content;
                boolean b = true;
                int n2 = 0;
                while (next3 != null) {
                    if ((this.value & 1L << n2) == 0x0L) {
                        if (next3.first(o)) {
                            return new ContentModelState(next3, new ContentModelState(this.model, this.next, this.value | 1L << n2)).advance(o);
                        }
                        if (!next3.empty()) {
                            b = false;
                        }
                    }
                    ++n2;
                    next3 = next3.next;
                }
                if (!b) {
                    break;
                }
                if (this.next != null) {
                    return this.next.advance(o);
                }
                return null;
            }
            default: {
                if (this.model.content != o) {
                    break;
                }
                if (this.next == null && o instanceof Element && ((Element)o).content != null) {
                    return new ContentModelState(((Element)o).content);
                }
                return this.next;
            }
        }
        return null;
    }
}
