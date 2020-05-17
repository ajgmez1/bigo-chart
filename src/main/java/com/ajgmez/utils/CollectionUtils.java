package com.ajgmez.utils;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.ajgmez.model.CollectionHandler;
import com.ajgmez.model.CollectionList;

public class CollectionUtils {
    private static final String XML_FILE = "/collectionProperties.xml";

    public static CollectionList getCollections() throws Exception {
        InputStream file = CollectionUtils.class.getResourceAsStream(XML_FILE);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        CollectionHandler cHandler = new CollectionHandler();
        saxParser.parse(file, cHandler);
        
        return cHandler.getCollections();
    }
}