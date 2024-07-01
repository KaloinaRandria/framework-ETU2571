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

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void add(String key , Object object) {
        session.setAttribute(key, object);
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }
}
