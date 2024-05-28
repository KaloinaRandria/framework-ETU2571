package mg.ituprom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Vector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.ituprom16.annotations.Controller;
import mg.ituprom16.annotations.Get;
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
            this.getListeController();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void getListeController() throws MalformedURLException, ClassNotFoundException {
        ServletContext servletContext = getServletContext();
        String classpath = Utils.modifierClassPath(servletContext.getResource(this.packageSource).getPath());
        File classPathDirectory = new File(classpath);
        this.listeController = new Vector<Class<?>>();

        for (File file : classPathDirectory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                Class<?> class1 = Thread.currentThread().getContextClassLoader()
                        .loadClass(this.packageSource.split("classes/")[1].replace("/", ".") + className);
                if (class1.isAnnotationPresent(Controller.class)) {
                    this.listeController.add(class1);
                    Method[] methods = class1.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i].isAnnotationPresent(Get.class)) {
                            Get getAnnot = methods[i].getAnnotation(Get.class);
                            this.mapping.put(getAnnot.value(), new Mapping(class1.getName(), methods[i].getName()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.processRequest(req, resp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.processRequest(req, resp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        PrintWriter out = resp.getWriter();
        // out.println(req.getRequestURL());
        String print = "";
        StringBuffer requestURL = req.getRequestURL();
        String[] urlSplitter = requestURL.toString().split("/");
        String getValue = urlSplitter[urlSplitter.length - 1];


        if (this.mapping.containsKey(getValue)) {
            Mapping map = this.mapping.get(getValue);
            print += requestURL.toString() + "\n";
            print += map.getClassName() + "\n";
            print += map.getMethodName() + "\n";

            Class myClass = Class.forName(map.getClassName());
            Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Method myMethod = myClass.getDeclaredMethod(map.getMethodName(), new Class[0]);

            print += "The Method invoke : " + (String) (myMethod.invoke(myObject, new Object[0]))  + "\n"; 
        } else {
            print = "404";
        }

        // if (this.listeController != null) {
        // for (int i = 0; i < this.listeController.size(); i++) {
        // print += listeController.elementAt(i).getName() + "\n";
        // }
        // } else {
        // print += "Aucun controller trouve";
        // }

        // if (this.mapping != null) {
        // for (Map.Entry<String, Mapping> entry : mapping.entrySet()) {
        // String key = entry.getKey();
        // Mapping value = entry.getValue();

        // print += key + ": " + value.getClassName() + " with " + value.getMethodName()
        // + "\n";
        // }
        // } else {
        // print += "mapping is Null \n";
        // }

        out.println(print);
        out.close();
    }

}