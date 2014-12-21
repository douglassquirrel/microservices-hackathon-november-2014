package combo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.singletonList;

public final class ComboFactory {

    public static Combo httpCombo(final URI baseUrl, final RestTemplateVisitor... restTemplateVisitors) {
        return new HttpCombo(restTemplate(baseUrl, restTemplateVisitors));
    }

    private ComboFactory() {
    }

    private static RestTemplate restTemplate(final URI baseUrl, final RestTemplateVisitor... restTemplateVisitors) {
        final RestTemplate restTemplate = new RestTemplate(singletonList(new GsonHttpMessageConverter()));
        for (final RestTemplateVisitor visitor : restTemplateVisitors) {
            visitor.visit(restTemplate);
        }
        restTemplate.getInterceptors().add(new BaseUrlInterceptor(baseUrl));
        restTemplate.getInterceptors().add(new LoggingInterceptor());
        return restTemplate;
    }

    public interface RestTemplateVisitor {
        void visit(final RestTemplate restTemplate);
    }

    private static final class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
            final int current = counter.getAndIncrement();
            System.out.printf(" [%s] %s -> %s%n", current, request.getMethod(), request.getURI());
            final ClientHttpResponse execute = execution.execute(request, body);
            System.out.printf(" [%s] %s%n", current, execute.getStatusCode());
            return execute;
        }
    }

    private static final class BaseUrlInterceptor implements ClientHttpRequestInterceptor {

        private final URI baseUri;

        private BaseUrlInterceptor(final URI baseUri) {
            this.baseUri = baseUri;
        }

        @Override
        public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
            return execution.execute(wrapHttpRequest(request, baseUri), body);
        }

        private static HttpRequest wrapHttpRequest(final HttpRequest request, final URI baseUri) {
            return new HttpRequest() {
                @Override public HttpMethod getMethod() {
                    return request.getMethod();
                }

                @Override public URI getURI() {
                    try {
                        return new URI(baseUri + request.getURI().toString());
                    } catch (final URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override public HttpHeaders getHeaders() {
                    return request.getHeaders();
                }
            };
        }
    }
}
