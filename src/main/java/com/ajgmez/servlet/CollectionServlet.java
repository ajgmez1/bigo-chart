package com.ajgmez.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ajgmez.impl.CollectionImpl;

import org.json.JSONObject;

/**
 * Created by gumz11 on 6/17/17.
 */

@WebServlet(urlPatterns = { "/api/collections", "/api/operations" })
public class CollectionServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger;
    final String COLLECTION_PATH = "collections";
    final String OPERATION_PATH = "operations";
    private CollectionImpl impl;

    @Override
    public void init() throws ServletException {
        this.logger = Logger.getAnonymousLogger();

        try {
            this.impl = new CollectionImpl();
        } catch (Exception e) {
            logger.log(Level.INFO, e.toString());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = new JSONObject();
        String[] paths = req.getRequestURI().split("/");
        String path = paths[paths.length-1];

        switch (path) {
            case COLLECTION_PATH:
                jsonObject = impl.getCollections();
                break;
            case OPERATION_PATH:
                jsonObject = impl.getOperations();
                break;
        }

        resp.setContentType("application/json");
        resp.getWriter().write(jsonObject.toString());
    }

}
