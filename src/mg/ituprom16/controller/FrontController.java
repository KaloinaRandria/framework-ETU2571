package mg.ituprom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.ituprom16.annotations.Controller;
import mg.ituprom16.utilitaire.Mapping;
import mg.ituprom16.utilitaire.ModelView;
import mg.ituprom16.utilitaire.Utils;

public class FrontController extends HttpServlet {
    String packageSource;
    Vector<Class<?>> listeController;
    HashMap<String, Mapping> mapping;

    public void init() throws ServletException {
        try {
            this.packageSource = this.getInitParameter("package-source");
            this.mapping = new HashMap<>();
            String classpath = Utils.modifierClassPath(getServletContext().getResource(this.packageSource).getPath());
            listeController = Utils.getAllClassAnnoted(classpath, Controller.class);
            Utils.scanListClasses(listeController, mapping);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        try {

            String print = "";
            StringBuffer requestURL = req.getRequestURL();
            String[] urlSplitter = requestURL.toString().split("/");
            String getValue = urlSplitter[urlSplitter.length - 1];

            Object myObject = Utils.invokedMethod(this.mapping, getValue);
            if (myObject instanceof String) {
                print += "The Method invoke : " + (String) myObject;
            } else if (myObject instanceof ModelView) {
                ModelView modelView = (ModelView) myObject;
                RequestDispatcher dispatcher = req.getRequestDispatcher(modelView.getUrl());
                HashMap<String, Object> data = modelView.getData();
                Set<String> keys = data.keySet();
                for (String key : keys) {
                    req.setAttribute(key, data.get(key));
                }
                dispatcher.forward(req, resp);
            }
            PrintWriter out = resp.getWriter();
            out.println(print);
            out.close();
        } catch (Exception e) {
            e.getMessage();
        }

    }

}