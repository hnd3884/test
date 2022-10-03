package org.apache.axiom.om.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.axiom.om.OMNamedInformationItem;
import java.io.UnsupportedEncodingException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMText;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMNode;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import org.apache.axiom.om.OMDocument;

public class DigestGenerator
{
    public static final String md5DigestAlgorithm = "MD5";
    public static final String shaDigestAlgorithm = "SHA";
    public static final String sha1DigestAlgorithm = "SHA1";
    
    public byte[] getDigest(final OMDocument document, final String digestAlgorithm) throws OMException {
        byte[] digest = new byte[0];
        try {
            final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(9);
            final Collection childNodes = this.getValidElements(document);
            dos.writeInt(childNodes.size());
            for (final OMNode node : childNodes) {
                if (node.getType() == 3) {
                    dos.write(this.getDigest((OMProcessingInstruction)node, digestAlgorithm));
                }
                else {
                    if (node.getType() != 1) {
                        continue;
                    }
                    dos.write(this.getDigest((OMElement)node, digestAlgorithm));
                }
            }
            dos.close();
            md.update(baos.toByteArray());
            digest = md.digest();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (final IOException e2) {
            throw new OMException(e2);
        }
        return digest;
    }
    
    public byte[] getDigest(final OMNode node, final String digestAlgorithm) {
        if (node.getType() == 1) {
            return this.getDigest((OMElement)node, digestAlgorithm);
        }
        if (node.getType() == 4) {
            return this.getDigest((OMText)node, digestAlgorithm);
        }
        if (node.getType() == 3) {
            return this.getDigest((OMProcessingInstruction)node, digestAlgorithm);
        }
        return new byte[0];
    }
    
    public byte[] getDigest(final OMElement element, final String digestAlgorithm) throws OMException {
        byte[] digest = new byte[0];
        try {
            final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(1);
            dos.write(this.getExpandedName(element).getBytes("UnicodeBigUnmarked"));
            dos.write(0);
            dos.write(0);
            final Collection attrs = this.getAttributesWithoutNS(element);
            dos.writeInt(attrs.size());
            Iterator itr = attrs.iterator();
            while (itr.hasNext()) {
                dos.write(this.getDigest(itr.next(), digestAlgorithm));
            }
            OMNode node = element.getFirstOMChild();
            int length = 0;
            itr = element.getChildren();
            while (itr.hasNext()) {
                final OMNode child = itr.next();
                if (child instanceof OMElement || child instanceof OMText || child instanceof OMProcessingInstruction) {
                    ++length;
                }
            }
            dos.writeInt(length);
            while (node != null) {
                dos.write(this.getDigest(node, digestAlgorithm));
                node = node.getNextOMSibling();
            }
            dos.close();
            md.update(baos.toByteArray());
            digest = md.digest();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (final IOException e2) {
            throw new OMException(e2);
        }
        return digest;
    }
    
    public byte[] getDigest(final OMProcessingInstruction pi, final String digestAlgorithm) throws OMException {
        byte[] digest = new byte[0];
        try {
            final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)7);
            md.update(pi.getTarget().getBytes("UnicodeBigUnmarked"));
            md.update((byte)0);
            md.update((byte)0);
            md.update(pi.getValue().getBytes("UnicodeBigUnmarked"));
            digest = md.digest();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new OMException(e2);
        }
        return digest;
    }
    
    public byte[] getDigest(final OMAttribute attribute, final String digestAlgorithm) throws OMException {
        byte[] digest = new byte[0];
        if (!attribute.getLocalName().equals("xmlns") && !attribute.getLocalName().startsWith("xmlns:")) {
            try {
                final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
                md.update((byte)0);
                md.update((byte)0);
                md.update((byte)0);
                md.update((byte)2);
                md.update(this.getExpandedName(attribute).getBytes("UnicodeBigUnmarked"));
                md.update((byte)0);
                md.update((byte)0);
                md.update(attribute.getAttributeValue().getBytes("UnicodeBigUnmarked"));
                digest = md.digest();
            }
            catch (final NoSuchAlgorithmException e) {
                throw new OMException(e);
            }
            catch (final UnsupportedEncodingException e2) {
                throw new OMException(e2);
            }
        }
        return digest;
    }
    
    public byte[] getDigest(final OMText text, final String digestAlgorithm) throws OMException {
        byte[] digest = new byte[0];
        try {
            final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)3);
            md.update(text.getText().getBytes("UnicodeBigUnmarked"));
            digest = md.digest();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new OMException(e2);
        }
        return digest;
    }
    
    public String getExpandedName(final OMElement element) {
        return this.internalGetExpandedName(element);
    }
    
    public String getExpandedName(final OMAttribute attribute) {
        return this.internalGetExpandedName(attribute);
    }
    
    private String internalGetExpandedName(final OMNamedInformationItem informationItem) {
        final String uri = informationItem.getNamespaceURI();
        return (uri == null) ? informationItem.getLocalName() : (uri + ":" + informationItem.getLocalName());
    }
    
    public Collection getAttributesWithoutNS(final OMElement element) {
        final SortedMap map = new TreeMap();
        final Iterator itr = element.getAllAttributes();
        while (itr.hasNext()) {
            final OMAttribute attribute = itr.next();
            if (!attribute.getLocalName().equals("xmlns") && !attribute.getLocalName().startsWith("xmlns:")) {
                map.put(this.getExpandedName(attribute), attribute);
            }
        }
        return map.values();
    }
    
    public Collection getValidElements(final OMDocument document) {
        final ArrayList list = new ArrayList();
        final Iterator itr = document.getChildren();
        while (itr.hasNext()) {
            final OMNode node = itr.next();
            if (node.getType() == 1 || node.getType() == 3) {
                list.add(node);
            }
        }
        return list;
    }
    
    public String getStringRepresentation(final byte[] array) {
        String str = "";
        for (int i = 0; i < array.length; ++i) {
            str += array[i];
        }
        return str;
    }
    
    public boolean compareOMNode(final OMNode node, final OMNode comparingNode, final String digestAlgorithm) {
        return Arrays.equals(this.getDigest(node, digestAlgorithm), this.getDigest(comparingNode, digestAlgorithm));
    }
    
    public boolean compareOMDocument(final OMDocument document, final OMDocument comparingDocument, final String digestAlgorithm) {
        return Arrays.equals(this.getDigest(document, digestAlgorithm), this.getDigest(comparingDocument, digestAlgorithm));
    }
    
    public boolean compareOMAttribute(final OMAttribute attribute, final OMAttribute comparingAttribute, final String digestAlgorithm) {
        return Arrays.equals(this.getDigest(attribute, digestAlgorithm), this.getDigest(comparingAttribute, digestAlgorithm));
    }
}
