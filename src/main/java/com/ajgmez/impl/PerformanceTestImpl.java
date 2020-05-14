package com.ajgmez.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ajgmez.enums.Operation;
import com.ajgmez.model.Collection;
import com.ajgmez.model.MethodAndParams;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by gumz11 on 6/23/17.
 */
public class PerformanceTestImpl {

    private final String XML_FILE = "/collectionProperties.xml";
    private Logger logger;
    private Map<String, Collection> collections;
    private Method method;
    private Class<?>[] params;

    public PerformanceTestImpl() throws Exception {
        this.logger = Logger.getAnonymousLogger();

        InputStream file = getClass().getResourceAsStream(this.XML_FILE);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        this.collections = this.parseCollectionProperties(db.parse(file));
    }

    private Map<String, Collection> parseCollectionProperties(Document file) throws Exception {
        NodeList collectionsList = file.getElementsByTagName("collection");
        Map<String, Collection> collections = new HashMap<>();

        for (int i = 0; i < collectionsList.getLength(); i++) {
            Element c = (Element) collectionsList.item(i);
            NodeList methodList = c.getElementsByTagName("method");
            List<MethodAndParams> methods = new ArrayList<>();

            for (int j = 0; j < methodList.getLength(); j++) {
                Element m = (Element) methodList.item(j);
                String type = m.getElementsByTagName("type").item(0).getTextContent();
                String methodName = m.getElementsByTagName("value").item(0).getTextContent();
                NodeList params = m.getElementsByTagName("paramsValue");
                Class<?>[] classParams = new Class<?>[params.getLength()];

                for (int k = 0; k < params.getLength(); k++) {
                    try {
                        classParams[k] = (Class<?>) Class.forName(params.item(k).getTextContent()).getField("TYPE").get(null);
                    } catch (Exception e) {
                        classParams[k] = Class.forName(params.item(k).getTextContent());
                    }
                }
                methods.add(new MethodAndParams(type, methodName, classParams));
            }
            String name = c.getAttributes().getNamedItem("id").getNodeValue();
            collections.put(name, new Collection(name, methods));
        }
        
        return collections;
    }

    public Map<Integer, Long> test(String collection, String operation, int inputSize) throws Exception {
        Map<Integer, Long> results = new HashMap<>();

        Collection c = collections.get(collection);
        MethodAndParams mp = c.getMethodAndParams(operation);
        Method method = mp.getMethod();
        Object o = c.getObject();
        
        for (int i = 1; i <= inputSize; i++) {
            // populate collection before testing
            if (!operation.equals(Operation.Insertion.name())) {
                Method insert = c.getMethodAndParams(Operation.Insertion.name()).getMethod();
                this.run(o, insert, i);
            }
            // run performance test
            Method sizeM = c.getMethodAndParams("size").getMethod();
            logger.log(Level.INFO, "size: " + sizeM.invoke(o).toString());
            if (i % 100 == 0) {
                results.put(i, this.run(o, method, i));
            }   
        }

        return this.filter(results);
    }

    private long run(Object collection, Method method, int inputSize) throws InvocationTargetException, IllegalAccessException {
        Random random = new Random();
        int params = method.getParameterCount();
        Object []args = new Object[params];

        for (int p = 0; p < params; p++) {
            args[p] = random.nextInt(inputSize);
        }
    
        long start = System.nanoTime();
        method.invoke(collection, args);
        return System.nanoTime() - start;
    }

    private Map<Integer, Long> filter(Map<Integer, Long> results) {
        // TODO: filter outliers by standard deviation? 
        // need to double check statistics...

        // TODO: chart.js can only handle so many data points
        // also figure out how to send fewer data points 
        // for very large inputSize
        return results;
    }
}
