package mg.ituprom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.ituprom16.annotations.Controller;
import mg.ituprom16.utilitaire.Mapping;
import mg.ituprom16.utilitaire.Utils;

public class FrontController extends HttpServlet {
    String packageSource;
    Vector<Class<?>> listeController;
    HashMap<String, Mapping> mapping;

    public void init() throws ServletException {
        try {
            this.packageSource = this.getInitParameter("package-source");
            this.mapping = new HashMap<>();
            listeController = Utils.getAllClassAnnoted(this.packageSource, Controller.class, getServletContext());
            Utils.scanListClasses(listeController, mapping);
        } catch (Exception e) {
            e.getMessage();
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
            PrintWriter out = resp.getWriter();
            String print = "";
            StringBuffer requestURL = req.getRequestURL();
            String[] urlSplitter = requestURL.toString().split("/");
            String getValue = urlSplitter[urlSplitter.length - 1];

            if (this.mapping.containsKey(getValue)) {
                Mapping map = this.mapping.get(getValue);
                print += requestURL.toString() + "\n";
                print += map.getClassName() + "\n";
                print += map.getMethodName() + "\n";

                Class<?> myClass = Class.forName(map.getClassName());
                Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                Method myMethod = myClass.getDeclaredMethod(map.getMethodName(), new Class[0]);

                print += "The Method invoke : " + (String) (myMethod.invoke(myObject, new Object[0])) + "\n";
            } else {
                print = "404";
            }
            out.println(print);
            out.close();
        } catch (Exception e) {
            e.getMessage();
        }

    }

}