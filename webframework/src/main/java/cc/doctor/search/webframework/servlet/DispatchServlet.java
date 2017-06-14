package cc.doctor.search.webframework.servlet;

import cc.doctor.search.common.utils.Container;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.common.utils.SerializeUtils;
import cc.doctor.search.webframework.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static cc.doctor.search.common.utils.Container.container;
import static cc.doctor.search.webframework.handler.RequestParser.requestParser;

/**
 * Created by doctor on 2017/3/18.
 */
public class DispatchServlet extends HttpServlet {
    private static final long serialVersionUID = -6726851046106695269L;
    private static final Logger log = LoggerFactory.getLogger(Container.class);
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
            ExceptionHandler exceptionHandler = null;
            if (serviceMethod.size() == 3) {
                try {
                    exceptionHandler = (ExceptionHandler) container.getOrCreateComponent(Class.forName(serviceMethod.get(2)));
                } catch (ClassNotFoundException e) {
                    log.error("", e);
                }
            }
            try {
                Object invoke = requestParser.invoke(req, serviceMethod.get(0), serviceMethod.get(1));
                if (invoke != null) {
                    writer.write(SerializeUtils.objectToJson(invoke));
                }
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    writer.write(SerializeUtils.objectToJson(exceptionHandler.handleException(e)));
                }
            }
        } else {
            resp.sendError(404);
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
}
