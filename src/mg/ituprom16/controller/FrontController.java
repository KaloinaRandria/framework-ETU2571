package mg.ituprom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    String packageSource;
    Vector<Class> listeController;

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