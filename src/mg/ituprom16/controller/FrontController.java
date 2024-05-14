package mg.ituprom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Vector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.ituprom16.annotations.Controller;

public class FrontController extends HttpServlet {
    String packageSource;
    Vector<Class> listeController;

    public void init() {
        try {
            this.packageSource = this.getInitParameter("package-source");    
        } catch (Exception e) {
            throw new RuntimeException();
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
                if (class1.isAnnotationPresent(Controller.class)) {
                    this.listeController.add(class1);
                }
            }
        }    
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.processRequest(req, resp);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        try {
            this.processRequest(req, resp);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void processRequest (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException, ClassNotFoundException {
        PrintWriter out = resp.getWriter();
        out.println(req.getRequestURL());
        String print = "";
        if (this.listeController.equals(null)) {
            for (int i = 0; i < this.listeController.size(); i++) {
                print += listeController.elementAt(i).getName()+"\n"; 
            }
        }
        else {
            this.getListeController();
            for (int i = 0; i < this.listeController.size(); i++) {
                 print += listeController.elementAt(i).getName()+"\n";               
            }
        }
        out.println(print);
        out.close();
    }

} 