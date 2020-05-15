package com.ajgmez.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ajgmez.model.Collection;
import com.ajgmez.model.MethodAndParams;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CollectionUtils {
    private static final String XML_FILE = "/collectionProperties.xml";

    public static Map<String, Collection> getCollections() throws Exception {
        InputStream file = CollectionUtils.class.getResourceAsStream(XML_FILE);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document f = db.parse(file);

        // TODO: Use some api that is cleaner...
        NodeList collectionsList = f.getElementsByTagName("collection");
        Map<String, Collection> collections = new HashMap<>();

        for (int i = 0; i < collectionsList.getLength(); i++) {
            Element c = (Element) collectionsList.item(i);
            NodeList methodList = c.getElementsByTagName("method");
            List<MethodAndParams> methods = new ArrayList<>();

            for (int j = 0; j < methodList.getLength(); j++) {
                Element m = (Element) methodList.item(j);
                String type = m.getElementsByTagName("type").item(0).getTextContent();
                String desc = m.getElementsByTagName("description").item(0).getTextContent();
                String methodName = m.getElementsByTagName("value").item(0).getTextContent();
                NodeList params = m.getElementsByTagName("paramsValue");
                methods.add(new MethodAndParams(type, methodName, desc, params));
            }
            String name = c.getAttributes().getNamedItem("id").getNodeValue();
            collections.put(name, new Collection(name, methods));
        }
        
        return collections;
    }
}