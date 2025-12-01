package br.com.hellyson.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap para poder ler o corpo várias vezes
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request,10 * 1024 * 1024);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        // Ler o corpo da request
        String requestBody = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
        logger.info("REQUEST -> Method: {}, URI: {}, Body: {}", request.getMethod(), request.getRequestURI(), requestBody);

        // Ler o corpo da response
        String responseBody = new String(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
        logger.info("RESPONSE -> Status: {}, Body: {}", response.getStatus(), responseBody);

        // Copia o conteúdo de volta para a response original
        wrappedResponse.copyBodyToResponse();
    }
}
