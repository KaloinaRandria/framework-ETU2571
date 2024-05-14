package mg.ituprom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Vector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    String packageSource;
    Vector<Class> listeController;

    public void init() {
        try {
            this.packageSource = this.getInitParameter("package-source");    
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getListeController() throws MalformedURLException, ClassNotFoundException {
        ServletContext servletContext = getServletContext();
        String classpath = servletContext.getResource(this.packageSource).getPath();
        File classPathDirectory = new File(classpath);
        this.listeController = new Vector<Class>();
        for(File file : classPathDirectory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                Class<?> class1 = Thread.currentThread().getContextClassLoader().loadClass(className);
                
            }
        }    
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    protected void processRequest (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println(req.getRequestURL());
    }

} 