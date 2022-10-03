package com.lowagie.text.pdf;

import java.util.Iterator;
import java.util.HashMap;

class PageResources
{
    protected PdfDictionary fontDictionary;
    protected PdfDictionary xObjectDictionary;
    protected PdfDictionary colorDictionary;
    protected PdfDictionary patternDictionary;
    protected PdfDictionary shadingDictionary;
    protected PdfDictionary extGStateDictionary;
    protected PdfDictionary propertyDictionary;
    protected HashMap forbiddenNames;
    protected PdfDictionary originalResources;
    protected int[] namePtr;
    protected HashMap usedNames;
    
    PageResources() {
        this.fontDictionary = new PdfDictionary();
        this.xObjectDictionary = new PdfDictionary();
        this.colorDictionary = new PdfDictionary();
        this.patternDictionary = new PdfDictionary();
        this.shadingDictionary = new PdfDictionary();
        this.extGStateDictionary = new PdfDictionary();
        this.propertyDictionary = new PdfDictionary();
        this.namePtr = new int[] { 0 };
    }
    
    void setOriginalResources(final PdfDictionary resources, final int[] newNamePtr) {
        if (newNamePtr != null) {
            this.namePtr = newNamePtr;
        }
        this.forbiddenNames = new HashMap();
        this.usedNames = new HashMap();
        if (resources == null) {
            return;
        }
        (this.originalResources = new PdfDictionary()).merge(resources);
        for (final PdfName key : resources.getKeys()) {
            final PdfObject sub = PdfReader.getPdfObject(resources.get(key));
            if (sub != null && sub.isDictionary()) {
                final PdfDictionary dic = (PdfDictionary)sub;
                final Iterator j = dic.getKeys().iterator();
                while (j.hasNext()) {
                    this.forbiddenNames.put(j.next(), null);
                }
                final PdfDictionary dic2 = new PdfDictionary();
                dic2.merge(dic);
                this.originalResources.put(key, dic2);
            }
        }
    }
    
    PdfName translateName(final PdfName name) {
        PdfName translated = name;
        if (this.forbiddenNames != null) {
            translated = this.usedNames.get(name);
            if (translated == null) {
                do {
                    translated = new PdfName("Xi" + this.namePtr[0]++);
                } while (this.forbiddenNames.containsKey(translated));
                this.usedNames.put(name, translated);
            }
        }
        return translated;
    }
    
    PdfName addFont(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.fontDictionary.put(name, reference);
        return name;
    }
    
    PdfName addXObject(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.xObjectDictionary.put(name, reference);
        return name;
    }
    
    PdfName addColor(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.colorDictionary.put(name, reference);
        return name;
    }
    
    void addDefaultColor(final PdfName name, final PdfObject obj) {
        if (obj == null || obj.isNull()) {
            this.colorDictionary.remove(name);
        }
        else {
            this.colorDictionary.put(name, obj);
        }
    }
    
    void addDefaultColor(final PdfDictionary dic) {
        this.colorDictionary.merge(dic);
    }
    
    void addDefaultColorDiff(final PdfDictionary dic) {
        this.colorDictionary.mergeDifferent(dic);
    }
    
    PdfName addShading(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.shadingDictionary.put(name, reference);
        return name;
    }
    
    PdfName addPattern(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.patternDictionary.put(name, reference);
        return name;
    }
    
    PdfName addExtGState(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.extGStateDictionary.put(name, reference);
        return name;
    }
    
    PdfName addProperty(PdfName name, final PdfIndirectReference reference) {
        name = this.translateName(name);
        this.propertyDictionary.put(name, reference);
        return name;
    }
    
    PdfDictionary getResources() {
        final PdfResources resources = new PdfResources();
        if (this.originalResources != null) {
            resources.putAll(this.originalResources);
        }
        resources.put(PdfName.PROCSET, new PdfLiteral("[/PDF /Text /ImageB /ImageC /ImageI]"));
        resources.add(PdfName.FONT, this.fontDictionary);
        resources.add(PdfName.XOBJECT, this.xObjectDictionary);
        resources.add(PdfName.COLORSPACE, this.colorDictionary);
        resources.add(PdfName.PATTERN, this.patternDictionary);
        resources.add(PdfName.SHADING, this.shadingDictionary);
        resources.add(PdfName.EXTGSTATE, this.extGStateDictionary);
        resources.add(PdfName.PROPERTIES, this.propertyDictionary);
        return resources;
    }
    
    boolean hasResources() {
        return this.fontDictionary.size() > 0 || this.xObjectDictionary.size() > 0 || this.colorDictionary.size() > 0 || this.patternDictionary.size() > 0 || this.shadingDictionary.size() > 0 || this.extGStateDictionary.size() > 0 || this.propertyDictionary.size() > 0;
    }
}
