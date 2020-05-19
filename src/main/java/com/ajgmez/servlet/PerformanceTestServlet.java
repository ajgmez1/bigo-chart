package com.ajgmez.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ajgmez.impl.PerformanceTestImpl;

import org.json.JSONObject;

/**
 * Created by gumz11 on 6/17/17.
 */

@WebServlet(urlPatterns = {"/api/collection/test"})
public class PerformanceTestServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger;
    private PerformanceTestImpl impl;

    @Override
    public void init() throws ServletException {
        this.logger = Logger.getAnonymousLogger();

        try {
            this.impl = new PerformanceTestImpl();
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Parse JSON
        JSONObject jsonObject = getJsonObject(req);

        // Perform the operation
        Map<Integer, Long> results = null;
        try {
            results = impl.test(
                jsonObject.get("collection").toString(),
                jsonObject.get("operation").toString(),
                Integer.parseInt(jsonObject.get("n").toString()),
                Integer.parseInt(jsonObject.get("points").toString()));
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
            resp.sendError(422, e.toString());
            e.printStackTrace();
        }

        // Return results
        resp.getWriter().write(new JSONObject(results).toString());
    }

    private JSONObject getJsonObject(HttpServletRequest req) throws IOException {
        String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        logger.log(Level.INFO, String.format("Request: %s", json));

        return new JSONObject(json);
    }

}
