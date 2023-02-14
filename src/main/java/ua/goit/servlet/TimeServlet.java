package ua.goit.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        String prefix = getServletContext().getRealPath("/WEB-INF/templates/");
        resolver.setPrefix(prefix);
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        now = getZonedDateTime(request, now);
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        Cookie lastTimezone = findCookie(request);
        if (request.getParameter("timezone") != null) {
            response.addCookie(new Cookie("lastTimezone", request.getParameter("timezone").replace(" ", "+")));
        } else if (lastTimezone != null) {
            now = ZonedDateTime.now(ZoneId.of(lastTimezone.getValue().replace(" ", "+")));
            formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
        }

        Context context = new Context();
        context.setVariable("date", formattedDate);

        PrintWriter writer = response.getWriter();
        engine.process("time", context, writer);
        writer.close();
    }

    private ZonedDateTime getZonedDateTime(HttpServletRequest request, ZonedDateTime now) {
        String timezone = request.getParameter("timezone");
        if (timezone != null) {
            now = ZonedDateTime.now(ZoneId.of(timezone.replace(" ", "+")));
        }
        return now;
    }

    private Cookie findCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("lastTimezone")) {
                    return cookie;
                }
            }
        }
        return null;
    }
}