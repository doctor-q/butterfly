package cc.doctor.wiki.web.servlet;

import cc.doctor.wiki.utils.FileUtils;
import cc.doctor.wiki.utils.SerializeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static cc.doctor.wiki.web.RequestParser.requestParser;

/**
 * Created by doctor on 2017/3/18.
 */
public class DispatchServlet extends HttpServlet {
    private static final long serialVersionUID = -6726851046106695269L;
    public static Map routes;
    static {
        String json = FileUtils.readFile(DispatchServlet.class.getResource("/").getPath() + "/" + "routes.json");
        routes = SerializeUtils.jsonToObject(json, TreeMap.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        String pathInfo = req.getPathInfo();
        List<String> serviceMethod = findServiceMethod(routes, pathInfo);
        if (serviceMethod != null) {
            Object invoke = requestParser.invoke(req, serviceMethod.get(0), serviceMethod.get(1));
            if (invoke != null) {
                writer.write(SerializeUtils.objectToJson(invoke));
            }
        }
    }

    private List<String> findServiceMethod(Map routes, String fullPath) {
        for (Object path : routes.keySet()) {
            if (fullPath.startsWith(path.toString())) {
                Object subRoutes = routes.get(path);
                if (subRoutes instanceof List) {
                    return (List)subRoutes;
                } else {
                    return findServiceMethod((Map)subRoutes, fullPath.substring(path.toString().length()));
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        DispatchServlet dispatchServlet = new DispatchServlet();
        List<String> serviceMethod = dispatchServlet.findServiceMethod(routes, "/indices/list");
        System.out.println(serviceMethod);
    }
}
