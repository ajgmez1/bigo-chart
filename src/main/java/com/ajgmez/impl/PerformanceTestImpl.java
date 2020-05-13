package com.ajgmez.impl;

import com.ajgmez.enums.Operation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by gumz11 on 6/23/17.
 */
public class PerformanceTestImpl {

    private final String XML_FILE = "/collectionProperties.xml";
    private Document xmlFile;
    private Method method;
    private Class<?>[] params;

    public PerformanceTestImpl() throws IOException, SAXException, ParserConfigurationException {
        InputStream file = getClass().getResourceAsStream(this.XML_FILE);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        this.xmlFile = db.parse(file);
    }

    public Map<Integer, Long> test(Object collection, String operation, int inputSize) throws NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IllegalAccessException {
        Map<Integer, Long> results = new HashMap<>();

        if (!operation.equals(Operation.Insertion.name())) {
            this.setMethod(collection, Operation.Insertion.name());
            this.run(collection, inputSize);
        }

        this.setMethod(collection, operation);

        for (int i = 1; i <= inputSize; i++) {
            results.put(i, this.run(collection, i));
        }

        return results;
    }

    private void setMethod(Object collection, String operation) throws NoSuchMethodException, ClassNotFoundException {
        Element e = this.xmlFile.getElementById(collection.getClass().getSimpleName());
        NodeList methodList = e.getElementsByTagName("method");

        Element m;
        String methodName;
        NodeList params;

        for (int j = 0; j < methodList.getLength(); j++) {
            m = (Element) methodList.item(j);

            if (m.getElementsByTagName("type").item(0).getTextContent().equals(operation)) {
                methodName = m.getElementsByTagName("value").item(0).getTextContent();
                params = m.getElementsByTagName("paramsValue");
                this.params = new Class<?>[params.getLength()];

                for (int k = 0; k < params.getLength(); k++) {
                    try {
                        this.params[k] = (Class<?>) Class.forName(params.item(k).getTextContent()).getField("TYPE").get(null);
                    } catch(NoSuchFieldException | IllegalAccessException ex) {
                        this.params[k] = Class.forName(params.item(k).getTextContent());
                    }
                }

                this.method = collection.getClass().getDeclaredMethod(methodName, this.params);
            }
        }
    }

    private long run(Object collection, int i) throws InvocationTargetException, IllegalAccessException {
        Random random = new Random();
        long start, total = 0;

        for (int j = 1; j <= i; j++) {

            if (this.params.length == 0) {

                start = System.nanoTime();
                this.method.invoke(collection);
                total += System.nanoTime() - start;

            } else if (this.params.length == 1) {

                start = System.nanoTime();
                this.method.invoke(collection, random.nextInt(i));
                total += System.nanoTime() - start;

            } else {

                start = System.nanoTime();
                this.method.invoke(collection, random.nextInt(), random.nextInt());
                total += System.nanoTime() - start;

            }

        }

        return total;
    }
}
