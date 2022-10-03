package com.lowagie.text.pdf;

class PdfResources extends PdfDictionary
{
    void add(final PdfName key, final PdfDictionary resource) {
        if (resource.size() == 0) {
            return;
        }
        final PdfDictionary dic = this.getAsDict(key);
        if (dic == null) {
            this.put(key, resource);
        }
        else {
            dic.putAll(resource);
        }
    }
}
