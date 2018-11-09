package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void responseSuccess(HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        LOGGER.debug("Request processed");
    }

    public static void responseFailed(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.getWriter().println("error");
        response.setContentType("text/html;charset=utf-8");
        LOGGER.error("Could not process request", e);
    }
}
