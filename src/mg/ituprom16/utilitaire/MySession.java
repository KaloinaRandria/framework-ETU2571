package mg.ituprom16.utilitaire;

import jakarta.servlet.http.HttpSession;

public class MySession {
    HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }

    public MySession() {}

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void get(String key) {

    }

    public void add(String key , Object object) {

    }

    public void delete(String key) {

    }
}
