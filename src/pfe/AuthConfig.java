package pfe;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Protège l'API des rapports : une session authentifiée est obligatoire. */
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    public static final String SESSION_KEY = "utilisateur";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute(SESSION_KEY) != null) {
                    return true;
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"erreur\":\"Authentification requise.\"}");
                return false;
            }
        }).addPathPatterns("/api/rapports", "/api/rapports/**");
    }
}