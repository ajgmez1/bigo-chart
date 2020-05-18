package com.ajgmez.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CollectionHandler extends DefaultHandler {

    private CollectionList collectionList;
    private Collection c;
    private MethodAndParamsList methodlist;
    private MethodAndParams mp;
    private String elementValue;
    private boolean methodsXml;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementValue = new String(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        collectionList = new CollectionList();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        switch (qName) {
            case "collection":
                try {
                    c = new Collection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                collectionList.add(c);
                break;
            case "methods":
                methodsXml = true;
                methodlist = c.getMethods();
                break;
            case "method":
                mp = new MethodAndParams();
                methodlist.add(mp);
                break;
        }
    }
 
    @Override
    public void endElement(String uri, String lName, String qName) throws SAXException {
        switch (qName) {
            case "name":
                c.setName(elementValue);
                break;
            case "type":
                mp.setType(elementValue);
                break;
            case "value":
                mp.setName(elementValue);
                break;
            case "description":
                if (methodsXml) {
                    mp.setDescription(elementValue);
                } else {
                    c.setDescription(elementValue);
                }
                break;
            case "paramsValue":
                mp.addParam(elementValue);
                break;
            case "method":
                mp.setMethod(c);
                break;
            case "methods":
                methodsXml = false;
                break;
        }
    }

    public CollectionList getCollections() {
        return collectionList;
    }
}