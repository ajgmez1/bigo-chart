package com.ajgmez.servlet;

import com.ajgmez.enums.Collection;
import com.ajgmez.enums.Operation;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by gumz11 on 6/17/17.
 */

@WebServlet(urlPatterns = {"/api/collections", "/api/operations"})
public class EnumServlet extends HttpServlet {

    final String COLLECTION_PATH = "collections";
    final String OPERATION_PATH = "operations";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] paths;
        String path;
        Object[] enumValues = {};
        JSONObject jsonObject = new JSONObject();

        // Split URI to get path

        paths = req.getRequestURI().split("/");
        path = paths[paths.length-1];

        // Check path and get appropriate enum

        if (path.equals(COLLECTION_PATH)) {
            enumValues = Collection.values();
        } else if (path.equals(OPERATION_PATH)) {
            enumValues = Operation.values();
        } else {
            super.doGet(req, resp);
        }

        // Parse to JSON and return response

        jsonObject.put("EnumValues", enumValues);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonObject.toString());
    }

}
