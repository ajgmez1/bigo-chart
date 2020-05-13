package com.ajgmez.servlet;

import com.ajgmez.impl.PerformanceTestImpl;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gumz11 on 6/17/17.
 */

@WebServlet(urlPatterns = {"/api/collection/test"})
public class PerformanceTestServlet extends HttpServlet {

    private Logger logger;
    private PerformanceTestImpl impl;

    @Override
    public void init() throws ServletException {
        this.logger = Logger.getAnonymousLogger();

        try {
            this.impl = new PerformanceTestImpl();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.log(Level.INFO, e.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Parse JSON
        JSONObject jsonObject = getJsonObject(req);

        // Create the collection
        Object collection = createTheCollection(jsonObject.get("collection").toString());

        // Perform the operation
        Map<Integer, Long> results = null;
        try {
            results = impl.test(collection,
                    jsonObject.get("operation").toString(),
                    Integer.parseInt(jsonObject.get("inputSize").toString()));
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            logger.log(Level.INFO, e.toString());
            resp.sendError(422, e.toString());
        } catch (InvocationTargetException e) {
            logger.log(Level.INFO, e.getCause().toString());
            resp.sendError(422, e.getCause().toString());
        }

        // Return results
        resp.getWriter().write(new JSONObject(results).toString());
    }

    private JSONObject getJsonObject(HttpServletRequest req) throws IOException {
        String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        logger.log(Level.INFO, String.format("Request: %s", json));

        return new JSONObject(json);
    }

    private Object createTheCollection(String className) {
        Object c = null;

        try {
            Class<?> cls = Class.forName("java.util." + className);
            Constructor<?> constructor = cls.getConstructor();
            c = constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return c;
    }

}
