package org.apache.xerces.impl.xs.models;

import java.util.Arrays;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.impl.dv.xs.EqualityHelper;
import java.util.Collection;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.impl.xs.SchemaGrammar;
import java.util.ArrayList;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import java.util.List;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XSElementDeclHelper;

public final class XS11CMRestriction implements XSElementDeclHelper
{
    private final SubstitutionGroupHandler sgh;
    private final XSGrammarBucket gb;
    private final CMBuilder cmb;
    private final XSConstraints xsc;
    private XS11CM base;
    private XS11CM derived;
    private XS11AllCM allb;
    private XS11AllCM alld;
    private XSDFACM dfab;
    private final QName qname;
    private final List states;
    private StatePair pair;
    private short wType;
    private final List wNSList;
    private final List wNSListTemp;
    private final List wDNList;
    private boolean wDD;
    private boolean wDS;
    private final List wANList;
    private List globals;
    private List siblingsB;
    private List siblingsD;
    private XSElementDecl eb;
    private XSElementDecl ed;
    private XSWildcardDecl wb;
    private XSWildcardDecl wd;
    private int[] b;
    private int[] bn;
    private int[] d;
    private int[] dn;
    private final int[] indexb;
    private final int[] indexd;
    private boolean matchedHead;
    
    public XS11CMRestriction(final XSCMValidator xscmValidator, final XSCMValidator xscmValidator2, final SubstitutionGroupHandler sgh, final XSGrammarBucket gb, final CMBuilder cmb, final XSConstraints xsc) {
        this.qname = new QName();
        this.states = new ArrayList();
        this.pair = null;
        this.wNSList = new ArrayList();
        this.wNSListTemp = new ArrayList();
        this.wDNList = new ArrayList();
        this.wANList = new ArrayList();
        this.indexb = new int[1];
        this.indexd = new int[1];
        this.base = (XS11CM)xscmValidator;
        this.derived = (XS11CM)xscmValidator2;
        this.sgh = sgh;
        this.gb = gb;
        this.cmb = cmb;
        this.xsc = xsc;
    }
    
    private void addState() {
        this.derived.optimizeStates(this.base, this.bn, this.dn, this.indexb[0]);
        if (this.pair == null) {
            this.pair = new StatePair(this.bn, this.dn);
        }
        else {
            this.pair.set(this.bn, this.dn);
        }
        if (!this.states.contains(this.pair)) {
            this.states.add(this.pair);
            this.pair = null;
        }
    }
    
    private void copyDerivedWildcard() {
        this.wType = this.wd.fType;
        this.wDD = this.wd.fDisallowedDefined;
        this.wDS = this.wd.fDisallowedSibling;
        this.wNSList.clear();
        if (this.wType == 1) {
            this.wType = 2;
        }
        else {
            for (int n = (this.wd.fNamespaceList == null) ? 0 : this.wd.fNamespaceList.length, i = 0; i < n; ++i) {
                this.wNSList.add(this.wd.fNamespaceList[i]);
            }
        }
        for (int n2 = (this.wd.fDisallowedNamesList == null) ? 0 : this.wd.fDisallowedNamesList.length, j = 0; j < n2; ++j) {
            this.wDNList.add(this.wd.fDisallowedNamesList[j].uri);
            this.wDNList.add(this.wd.fDisallowedNamesList[j].localpart);
        }
        this.wANList.clear();
    }
    
    private void addAN(final String uri, final String localpart) {
        if (!this.allowNS(uri)) {
            return;
        }
        int i = 0;
        while (i < this.wDNList.size()) {
            if (this.wDNList.get(i++) == uri && this.wDNList.get(i++) == localpart) {
                return;
            }
        }
        this.qname.uri = uri;
        this.qname.localpart = localpart;
        if (!this.derived.allowExpandedName(this.wd, this.qname, this.sgh, this)) {
            return;
        }
        this.wANList.add(uri);
        this.wANList.add(localpart);
    }
    
    private boolean allowNS(final String s) {
        return this.wType == 1 || (this.wType == 2 && !this.wNSList.contains(s)) || (this.wType == 3 && this.wNSList.contains(s));
    }
    
    private boolean allowName(final String uri, final String localpart) {
        if (!this.allowNS(uri)) {
            return false;
        }
        for (int i = 0; i < this.wDNList.size(); i += 2) {
            if (uri == this.wDNList.get(i) && localpart == this.wDNList.get(i + 1)) {
                return false;
            }
        }
        for (int j = 0; j < this.wANList.size(); j += 2) {
            if (uri == this.wANList.get(j) && localpart == this.wANList.get(j + 1)) {
                return true;
            }
        }
        this.qname.uri = uri;
        this.qname.localpart = localpart;
        return this.derived.allowExpandedName(this.wd, this.qname, this.sgh, this);
    }
    
    private boolean emptyWildcard() {
        return this.wType == 3 && this.wNSList.size() == 0 && this.wANList.size() == 0;
    }
    
    private void getGlobalElements() {
        this.globals = new ArrayList();
        final SchemaGrammar[] grammars = this.gb.getGrammars();
        for (int i = 0; i < grammars.length; ++i) {
            this.addGlobals(grammars[i]);
        }
    }
    
    private void addGlobals(final SchemaGrammar schemaGrammar) {
        final XSNamedMap components = schemaGrammar.getComponents((short)2);
        for (int i = 0; i < components.getLength(); ++i) {
            final XSElementDecl xsElementDecl = (XSElementDecl)components.item(i);
            this.globals.add(xsElementDecl.fTargetNamespace);
            this.globals.add(xsElementDecl.fName);
        }
    }
    
    private void getBaseSiblings() {
        this.siblingsB = this.base.getDefinedNames(this.sgh);
    }
    
    private void getDerivedSiblings() {
        this.siblingsD = this.derived.getDefinedNames(this.sgh);
    }
    
    public XSElementDecl getGlobalElementDecl(final QName qName) {
        final SchemaGrammar grammar = this.gb.getGrammar(qName.uri);
        return (grammar != null) ? grammar.getGlobalElementDecl(qName.localpart) : null;
    }
    
    public boolean check() {
        this.b = this.base.startContentModel();
        this.bn = this.base.startContentModel();
        this.d = this.derived.startContentModel();
        this.dn = this.derived.startContentModel();
        if (this.derived instanceof XS11AllCM) {
            this.alld = (XS11AllCM)this.derived;
            final Boolean checkAllDerived = this.checkAllDerived();
            if (checkAllDerived != null) {
                return checkAllDerived;
            }
        }
        final ArrayList list = new ArrayList();
        int i = 0;
        this.states.add(new StatePair(this.b, this.d));
        while (i < this.states.size()) {
            ((StatePair)this.states.get(i++)).getStates(this.b, this.d);
            list.clear();
            this.indexd[0] = -1;
            while ((this.ed = this.derived.nextElementTransition(this.d, this.dn, this.indexd)) != null) {
                list.add(this.ed.fTargetNamespace);
                list.add(this.ed.fName);
                if (this.ed.getScope() == 1) {
                    final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.ed, (short)4);
                    for (int j = 0; j < substitutionGroup.length; ++j) {
                        list.add(substitutionGroup[j].fTargetNamespace);
                        list.add(substitutionGroup[j].fName);
                    }
                }
                if (!this.matchElementInBase()) {
                    return false;
                }
            }
            this.indexd[0] = -1;
            while ((this.wd = this.derived.nextWildcardTransition(this.d, this.dn, this.indexd)) != null) {
                this.wDNList.clear();
                this.wDNList.addAll(list);
                if (!this.matchWildcardInBase()) {
                    return false;
                }
            }
        }
        return this.checkFinalStates();
    }
    
    private boolean matchElementInBase() {
        this.matchedHead = false;
        if (!this.findElementInBase()) {
            return false;
        }
        this.addState();
        if (this.matchedHead) {
            return true;
        }
        final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.ed, (short)4);
        for (int i = 0; i < substitutionGroup.length; ++i) {
            this.ed = substitutionGroup[i];
            if (!this.findElementInBase()) {
                return false;
            }
            this.addState();
        }
        return true;
    }
    
    private boolean findElementInBase() {
        this.indexb[0] = -1;
        while ((this.eb = this.base.nextElementTransition(this.b, this.bn, this.indexb)) != null) {
            if (this.matchElementWithBaseElement()) {
                return this.checkEERestriction();
            }
        }
        this.indexb[0] = -1;
        while ((this.wb = this.base.nextWildcardTransition(this.b, this.bn, this.indexb)) != null) {
            this.qname.uri = this.ed.fTargetNamespace;
            this.qname.localpart = this.ed.fName;
            if (this.base.allowExpandedName(this.wb, this.qname, this.sgh, this)) {
                return this.checkEWRestriction();
            }
        }
        return false;
    }
    
    private boolean matchElementWithBaseElement() {
        if (this.eb.getName() == this.ed.getName() && this.eb.getNamespace() == this.ed.getNamespace()) {
            this.matchedHead = (this.eb == this.ed);
            return true;
        }
        final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.eb, (short)4);
        for (int i = 0; i < substitutionGroup.length; ++i) {
            if (substitutionGroup[i].getName() == this.ed.getName() && substitutionGroup[i].getNamespace() == this.ed.getNamespace()) {
                this.eb = substitutionGroup[i];
                return true;
            }
        }
        return false;
    }
    
    private boolean checkEERestriction() {
        if (this.eb == this.ed) {
            return true;
        }
        if (!this.eb.getNillable() && this.ed.getNillable()) {
            return false;
        }
        if (this.eb.getConstraintType() == 2) {
            if (this.ed.getConstraintType() != 2) {
                return false;
            }
            if (!EqualityHelper.isEqual(this.eb.fDefault, this.ed.fDefault, (short)4)) {
                return false;
            }
        }
        return this.checkIDConstraintRestriction(this.ed, this.eb) && (this.ed.fBlock & this.eb.fBlock) == this.eb.fBlock && this.xsc.checkTypeDerivationOk(this.ed.fType, this.eb.fType, (short)25) && (!XS11TypeHelper.isTypeTablesComparable(this.eb.getTypeAlternatives(), this.ed.getTypeAlternatives()) || this.xsc.isTypeTablesEquivalent(this.eb, this.ed));
    }
    
    private boolean checkIDConstraintRestriction(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2) {
        final IdentityConstraint[] idConstraints = xsElementDecl.getIDConstraints();
        final IdentityConstraint[] idConstraints2 = xsElementDecl2.getIDConstraints();
        final int n = (idConstraints2 == null) ? 0 : idConstraints2.length;
        final int n2 = (idConstraints == null) ? 0 : idConstraints.length;
        for (int i = 0; i < n; ++i) {
            int n3;
            for (n3 = 0; n3 < n2 && idConstraints2[i] != idConstraints[n3]; ++n3) {}
            if (n3 == idConstraints.length) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkEWRestriction() {
        if (this.wb.getProcessContents() == 2) {
            return true;
        }
        this.qname.uri = this.ed.fTargetNamespace;
        this.qname.localpart = this.ed.fName;
        this.eb = this.getGlobalElementDecl(this.qname);
        if (this.eb == null) {
            return this.wb.getProcessContents() == 3;
        }
        return this.checkEERestriction();
    }
    
    private boolean matchWildcardInBase() {
        this.copyDerivedWildcard();
        if (this.derived.isOpenContent(this.wd)) {
            final int[] array = { -1 };
            XSWildcardDecl nextWildcardTransition;
            while (!this.emptyWildcard() && (nextWildcardTransition = this.derived.nextWildcardTransition(this.d, this.dn, array)) != null) {
                if (nextWildcardTransition != this.wd) {
                    this.subtractWildcard(nextWildcardTransition, true);
                }
            }
        }
        this.indexb[0] = -1;
        while ((this.eb = this.base.nextElementTransition(this.b, this.bn, this.indexb)) != null) {
            final Boolean checkWERestriction = this.checkWERestriction();
            if (checkWERestriction != null) {
                if (!checkWERestriction) {
                    return false;
                }
                this.addState();
            }
            if (this.eb.getScope() == 1) {
                final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.eb, (short)4);
                for (int i = 0; i < substitutionGroup.length; ++i) {
                    this.eb = substitutionGroup[i];
                    final Boolean checkWERestriction2 = this.checkWERestriction();
                    if (checkWERestriction2 != null) {
                        if (!checkWERestriction2) {
                            return false;
                        }
                        this.addState();
                    }
                }
            }
        }
        this.indexb[0] = -1;
        while (!this.emptyWildcard() && (this.wb = this.base.nextWildcardTransition(this.b, this.bn, this.indexb)) != null) {
            if (this.subtractWildcard(this.wb, false)) {
                if (this.wd.weakerProcessContents(this.wb)) {
                    return false;
                }
                this.addState();
            }
        }
        return this.emptyWildcard();
    }
    
    private Boolean checkWERestriction() {
        if (!this.allowName(this.eb.fTargetNamespace, this.eb.fName)) {
            return null;
        }
        if (this.wd.fProcessContents == 2) {
            return Boolean.FALSE;
        }
        this.ed = this.getGlobalElementDecl(this.qname);
        if (this.ed == null) {
            return Boolean.FALSE;
        }
        if (this.ed != this.eb && !this.checkEERestriction()) {
            return Boolean.FALSE;
        }
        this.wDNList.add(this.eb.fTargetNamespace);
        this.wDNList.add(this.eb.fName);
        return Boolean.TRUE;
    }
    
    private boolean subtractWildcard(final XSWildcardDecl xsWildcardDecl, final boolean b) {
        boolean b2 = false;
        if (!b && this.base.isOpenContent(xsWildcardDecl) && this.wANList.size() > 0) {
            int i = 0;
            while (i < this.wANList.size()) {
                this.qname.uri = (String)this.wANList.get(i++);
                this.qname.localpart = (String)this.wANList.get(i++);
                if (!this.base.allowExpandedName(xsWildcardDecl, this.qname, this.sgh, this)) {
                    return false;
                }
            }
            this.wANList.clear();
            b2 = true;
        }
        for (int n = (xsWildcardDecl.fDisallowedNamesList == null) ? 0 : xsWildcardDecl.fDisallowedNamesList.length, j = 0; j < n; ++j) {
            this.addAN(xsWildcardDecl.fDisallowedNamesList[j].uri, xsWildcardDecl.fDisallowedNamesList[j].localpart);
        }
        if (xsWildcardDecl.fDisallowedDefined && !this.wDD) {
            if (this.globals == null) {
                this.getGlobalElements();
            }
            int k = 0;
            while (k < this.globals.size()) {
                final String s = this.globals.get(k++);
                final String s2 = this.globals.get(k++);
                if (xsWildcardDecl.allowNamespace(s)) {
                    this.addAN(s, s2);
                }
            }
        }
        if (xsWildcardDecl.fDisallowedSibling) {
            if (!b) {
                if (this.siblingsB == null) {
                    this.getBaseSiblings();
                }
                int l = 0;
                while (l < this.siblingsB.size()) {
                    final String s3 = this.siblingsB.get(l++);
                    final String s4 = this.siblingsB.get(l++);
                    if (xsWildcardDecl.allowNamespace(s3)) {
                        this.addAN(s3, s4);
                    }
                }
            }
            else if (!this.wDS) {
                if (this.siblingsD == null) {
                    this.getDerivedSiblings();
                }
                int n2 = 0;
                while (n2 < this.siblingsD.size()) {
                    final String s5 = this.siblingsD.get(n2++);
                    final String s6 = this.siblingsD.get(n2++);
                    if (xsWildcardDecl.allowNamespace(s5)) {
                        this.addAN(s5, s6);
                    }
                }
            }
        }
        if (xsWildcardDecl.getConstraintType() == 1) {
            this.wType = 3;
            this.wNSList.clear();
            return true;
        }
        if (xsWildcardDecl.getConstraintType() != 2) {
            if (this.wType == 2) {
                for (int n3 = 0; n3 < xsWildcardDecl.fNamespaceList.length; ++n3) {
                    if (!this.wNSList.contains(xsWildcardDecl.fNamespaceList[n3])) {
                        this.wNSList.add(xsWildcardDecl.fNamespaceList[n3]);
                        b2 = true;
                    }
                }
            }
            else {
                for (int n4 = 0; n4 < xsWildcardDecl.fNamespaceList.length; ++n4) {
                    if (this.wNSList.remove(xsWildcardDecl.fNamespaceList[n4])) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
        if (this.wType == 2) {
            this.wType = 3;
            this.wNSListTemp.clear();
            for (int n5 = 0; n5 < xsWildcardDecl.fNamespaceList.length; ++n5) {
                if (!this.wNSList.contains(xsWildcardDecl.fNamespaceList[n5])) {
                    this.wNSListTemp.add(xsWildcardDecl.fNamespaceList[n5]);
                }
            }
            this.wNSList.clear();
            this.wNSList.addAll(this.wNSListTemp);
            return true;
        }
        this.wNSListTemp.clear();
        for (int n6 = 0; n6 < xsWildcardDecl.fNamespaceList.length; ++n6) {
            if (this.wNSList.contains(xsWildcardDecl.fNamespaceList[n6])) {
                this.wNSListTemp.add(xsWildcardDecl.fNamespaceList[n6]);
            }
        }
        if (this.wNSList.size() == this.wNSListTemp.size()) {
            return b2;
        }
        this.wNSList.clear();
        this.wNSList.addAll(this.wNSListTemp);
        return true;
    }
    
    private boolean checkFinalStates() {
        for (int i = 0; i < this.states.size(); ++i) {
            ((StatePair)this.states.get(i)).getStates(this.b, this.d);
            if (this.derived.endContentModel(this.d) && !this.base.endContentModel(this.b)) {
                return false;
            }
        }
        return true;
    }
    
    private Boolean checkAllDerived() {
        if (this.base instanceof XSEmptyCM) {
            final Boolean checkAllEmpty = this.checkAllEmpty();
            if (checkAllEmpty != null) {
                return checkAllEmpty;
            }
        }
        if (this.base instanceof XS11AllCM) {
            this.allb = (XS11AllCM)this.base;
            final Boolean checkAllAll = this.checkAllAll();
            if (checkAllAll == null) {
                this.base = this.allb;
                this.derived = this.alld;
                this.cmb.testOccurrences(this.alld.calOccurs());
            }
            return checkAllAll;
        }
        this.dfab = (XSDFACM)this.base;
        final Boolean checkAllDFA = this.checkAllDFA();
        if (checkAllDFA == null) {
            this.base = this.dfab;
            this.derived = this.alld;
            this.cmb.testOccurrences(this.alld.calOccurs());
        }
        return checkAllDFA;
    }
    
    private Boolean checkAllEmpty() {
        final int[] array = { -1 };
        if (this.base.nextWildcardTransition(this.b, this.bn, array) != null) {
            this.base = new XS11AllCM(false, 0, null, ((XSEmptyCM)this.base).getOpenContent());
            return null;
        }
        return this.derived.nextElementTransition(this.d, this.dn, array) == null && this.derived.nextWildcardTransition(this.d, this.dn, array) == null;
    }
    
    private Boolean checkAllAll() {
        if (this.allb.getOpenContent() != null && this.allb.getOpenContent().fMode == 2 && this.alld.getOpenContent() != null && this.alld.getOpenContent().fMode == 1) {
            return null;
        }
        if (this.siblingsD == null) {
            this.getDerivedSiblings();
        }
        if (this.alld.hasOptionalContent()) {
            if (!this.checkOptionalContent()) {
                return Boolean.FALSE;
            }
        }
        else if (this.allb.hasOptionalContent()) {
            return null;
        }
        final int[] startContentModel = this.derived.startContentModel();
        final int[] startContentModel2 = this.base.startContentModel();
        this.indexd[0] = -1;
        while ((this.ed = this.derived.nextElementTransition(this.d, this.dn, this.indexd)) != null) {
            startContentModel[this.indexd[0]] = -1;
            this.matchedHead = false;
            if (!this.matchEE(startContentModel, startContentModel2)) {
                return Boolean.FALSE;
            }
            if (this.matchedHead || this.ed.getScope() != 1) {
                continue;
            }
            final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.ed, (short)4);
            for (int i = 0; i < substitutionGroup.length; ++i) {
                this.ed = substitutionGroup[i];
                if (!this.matchEE(startContentModel, startContentModel2)) {
                    return Boolean.FALSE;
                }
            }
        }
        this.wDNList.clear();
        this.wDNList.addAll(this.siblingsD);
        this.indexd[0] = -1;
        while ((this.wd = this.derived.nextWildcardTransition(this.d, this.dn, this.indexd)) != null) {
            if (!this.matchWE(startContentModel2)) {
                return Boolean.FALSE;
            }
        }
        final int[] startContentModel3 = this.base.startContentModel();
        final int[] startContentModel4 = this.base.startContentModel();
        this.indexd[0] = -1;
        while ((this.ed = this.derived.nextElementTransition(this.d, this.dn, this.indexd)) != null) {
            this.indexb[0] = startContentModel[this.indexd[0]];
            if (this.indexb[0] >= 0) {
                this.alld.collectOccurs(startContentModel3, startContentModel4, this.indexb[0], this.indexd[0]);
            }
        }
        this.allb = this.allb.copy();
        this.alld = this.alld.copy();
        if (!this.allb.removeAsBase(startContentModel3, startContentModel4, startContentModel2)) {
            return Boolean.FALSE;
        }
        this.alld.removeAsDerived(startContentModel4, startContentModel2, startContentModel);
        return null;
    }
    
    private boolean checkOptionalContent() {
        final int totalMin = this.allb.totalMin();
        if (!this.allb.hasOptionalContent() && totalMin != 0) {
            return false;
        }
        final XSOpenContentDecl openContent = this.alld.getOpenContent();
        this.wd = ((openContent == null) ? null : openContent.fWildcard);
        if (this.wd == null) {
            return true;
        }
        this.wDNList.clear();
        this.wDNList.addAll(this.siblingsD);
        this.copyDerivedWildcard();
        final int[] array = { -1 };
        XSWildcardDecl nextWildcardTransition;
        while (!this.emptyWildcard() && (nextWildcardTransition = this.derived.nextWildcardTransition(this.d, this.dn, array)) != null) {
            if (nextWildcardTransition != this.wd) {
                this.subtractWildcard(nextWildcardTransition, true);
            }
        }
        if (this.emptyWildcard()) {
            return true;
        }
        this.indexb[0] = -1;
        while ((this.eb = this.base.nextElementTransition(this.b, this.bn, this.indexb)) != null) {
            final int min = this.allb.min(this.indexb[0]);
            if (this.allowName(this.eb.fTargetNamespace, this.eb.fName)) {
                if (min > 1 || min < totalMin) {
                    return false;
                }
                continue;
            }
            else {
                if (this.eb.getScope() != 1) {
                    continue;
                }
                final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.eb, (short)4);
                int i = 0;
                while (i < substitutionGroup.length) {
                    this.eb = substitutionGroup[i];
                    if (this.allowName(this.eb.fTargetNamespace, this.eb.fName)) {
                        if (min > 1 || min < totalMin) {
                            return false;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        this.indexb[0] = -1;
        while ((this.wb = this.base.nextWildcardTransition(this.b, this.bn, this.indexb)) != null) {
            if (!this.base.isOpenContent(this.wb) && this.overlap()) {
                final int min2 = this.allb.min(this.indexb[0]);
                if (min2 > 1 || min2 < totalMin) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    private boolean overlap() {
        int i = 0;
        while (i < this.wANList.size()) {
            this.qname.uri = (String)this.wANList.get(i++);
            this.qname.localpart = (String)this.wANList.get(i++);
            if (this.base.allowExpandedName(this.wb, this.qname, this.sgh, this)) {
                return true;
            }
        }
        if (this.wType == 1 || this.wb.fType == 1 || (this.wType == 2 && this.wb.fType == 2)) {
            return true;
        }
        if (this.wType == 3 && this.wb.fType == 3) {
            for (int j = 0; j < this.wb.fNamespaceList.length; ++j) {
                if (this.wNSList.contains(this.wb.fNamespaceList[j])) {
                    return true;
                }
            }
            return false;
        }
        if (this.wType == 2) {
            for (int k = 0; k < this.wb.fNamespaceList.length; ++k) {
                if (!this.wNSList.contains(this.wb.fNamespaceList[k])) {
                    return true;
                }
            }
            return false;
        }
        for (int l = 0; l < this.wNSList.size(); ++l) {
            String s;
            int n;
            for (s = this.wNSList.get(l), n = 0; n < this.wb.fNamespaceList.length && (s != null || this.wb.fNamespaceList[n] != null) && (s == null || !s.equals(this.wb.fNamespaceList[n])); ++n) {}
            if (n == this.wb.fNamespaceList.length) {
                return true;
            }
        }
        return false;
    }
    
    private boolean matchEE(final int[] array, final int[] array2) {
        final int n = this.indexd[0];
        this.indexb[0] = -1;
        while ((this.eb = this.base.nextElementTransition(this.b, this.bn, this.indexb)) != null) {
            final int n2 = this.indexb[0];
            if (this.eb.getName() == this.ed.getName() && this.eb.getNamespace() == this.ed.getNamespace()) {
                if (array[n] != -1 && array[n] != n2) {
                    array2[n2] = (array2[array[n]] = -1);
                    return true;
                }
                if (array2[n2] < 0) {
                    array[n] = n2;
                    return true;
                }
                if (array[n] == -1) {
                    array[n] = n2;
                    final int n3 = n2;
                    ++array2[n3];
                }
                this.matchedHead = (this.eb == this.ed);
                return this.checkEERestriction();
            }
            else {
                final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.eb, (short)4);
                int i = 0;
                while (i < substitutionGroup.length) {
                    if (substitutionGroup[i].getName() == this.ed.getName() && substitutionGroup[i].getNamespace() == this.ed.getNamespace()) {
                        if (array[n] != -1 && array[n] != n2) {
                            if (array[n] >= 0) {
                                array2[array[n]] = -1;
                            }
                            array2[n2] = -1;
                            return true;
                        }
                        if (array2[n2] < 0) {
                            array[n] = n2;
                            return true;
                        }
                        if (array[n] == -1) {
                            array[n] = n2;
                            final int n4 = n2;
                            ++array2[n4];
                        }
                        this.eb = substitutionGroup[i];
                        return this.checkEERestriction();
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        if (array[n] >= 0) {
            array2[array[n]] = -1;
        }
        array[n] = -2;
        return true;
    }
    
    private boolean matchWE(final int[] array) {
        this.indexb[0] = -1;
        while ((this.eb = this.base.nextElementTransition(this.b, this.bn, this.indexb)) != null) {
            final int n = this.indexb[0];
            if (array[n] <= 0) {
                continue;
            }
            final Boolean checkWERestriction = this.checkWERestriction();
            if (checkWERestriction != null) {
                if (!checkWERestriction) {
                    return false;
                }
                array[n] = -1;
                return true;
            }
            else {
                if (this.eb.getScope() != 1) {
                    continue;
                }
                final XSElementDecl[] substitutionGroup = this.sgh.getSubstitutionGroup(this.eb, (short)4);
                int i = 0;
                while (i < substitutionGroup.length) {
                    this.eb = substitutionGroup[i];
                    final Boolean checkWERestriction2 = this.checkWERestriction();
                    if (checkWERestriction2 != null) {
                        if (!checkWERestriction2) {
                            return false;
                        }
                        array[n] = -1;
                        return true;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        return true;
    }
    
    private Boolean checkAllDFA() {
        return null;
    }
    
    private static class StatePair
    {
        private final int[] states;
        
        public StatePair(final int[] array, final int[] array2) {
            System.arraycopy(array, 0, this.states = new int[array.length + array2.length], 0, array.length);
            System.arraycopy(array2, 0, this.states, this.states.length - array2.length, array2.length);
        }
        
        private void set(final int[] array, final int[] array2) {
            System.arraycopy(array, 0, this.states, 0, array.length);
            System.arraycopy(array2, 0, this.states, this.states.length - array2.length, array2.length);
        }
        
        private void getStates(final int[] array, final int[] array2) {
            System.arraycopy(this.states, 0, array, 0, array.length);
            System.arraycopy(this.states, this.states.length - array2.length, array2, 0, array2.length);
        }
        
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.states.length; ++i) {
                n = n * 7 + this.states[i];
            }
            return n;
        }
        
        public boolean equals(final Object o) {
            return o instanceof StatePair && Arrays.equals(this.states, ((StatePair)o).states);
        }
    }
    
    interface XS11CM extends XSCMValidator
    {
        XSElementDecl nextElementTransition(final int[] p0, final int[] p1, final int[] p2);
        
        XSWildcardDecl nextWildcardTransition(final int[] p0, final int[] p1, final int[] p2);
        
        boolean isOpenContent(final XSWildcardDecl p0);
        
        boolean allowExpandedName(final XSWildcardDecl p0, final QName p1, final SubstitutionGroupHandler p2, final XSElementDeclHelper p3);
        
        List getDefinedNames(final SubstitutionGroupHandler p0);
        
        void optimizeStates(final XS11CM p0, final int[] p1, final int[] p2, final int p3);
    }
}
