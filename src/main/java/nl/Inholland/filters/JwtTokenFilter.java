package nl.Inholland.filters;

import nl.Inholland.security.JwtTokenProvider;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 * The JwtTokenFilter is applied to each endpoint with exception of the login endpoint.
 *
 * Check for access token in Authorization header. If Access token is found in the header,
 * delegate authentication to JwtTokenProvider otherwise throw authentication exception
 *
 * Invokes success or failure strategies based on the outcome of authentication process performed by JwtTokenProvider
 */

@Component
@Order(1)
public class JwtTokenFilter implements Filter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);

            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
