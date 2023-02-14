package ua.goit.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.TimeZone;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String timezone = request.getParameter("timezone");
        Cookie lastTimezone = findCookie(request);

        if ((timezone == null || timezone.isEmpty() || TimeZone.getTimeZone(timezone) == null) && lastTimezone == null) {
            response.setStatus(400);
            response.getWriter().write("Invalid timezone");
            return;
        } else {
            request.setAttribute("lastTimezone", lastTimezone.getValue().replace(" ", "+"));
        }
        filterChain.doFilter(servletRequest, servletResponse);
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
