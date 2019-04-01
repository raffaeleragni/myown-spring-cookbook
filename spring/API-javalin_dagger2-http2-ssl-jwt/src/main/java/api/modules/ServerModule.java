package api.modules;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.javalin.Javalin;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.MDC;

import java.util.SortedMap;
import java.util.UUID;

@dagger.Module
public class ServerModule {

  @dagger.Provides
  Javalin javalin(HealthCheckRegistry healthCheckRegistry, JWTProvider jwtProvider) {
    Javalin app = Javalin.create(config -> {
      config.showJavalinBanner = false;
      config.enforceSsl = true;
      config.server(() -> {
        Server server = new Server();
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreType("PKCS12");
        sslContextFactory.setKeyStoreResource(Resource.newClassPathResource("keystore.p12"));
        sslContextFactory.setKeyStorePassword("changeit");
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        sslContextFactory.setProvider("Conscrypt");

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(8443);
        httpConfig.addCustomizer(new SecureRequestCustomizer());

        // HTTP/2 Connection Factory
        HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpConfig);
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol("h2");

        // SSL Connection Factory
        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

        // HTTPS port + HTTP 2
        ServerConnector http2Connector = new ServerConnector(server, ssl, alpn, h2, new HttpConnectionFactory(httpConfig));
        http2Connector.setPort(8443);
        server.addConnector(http2Connector);

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setIncludedPaths("/*");
        server.setHandler(gzipHandler);

        return server;
      });
    });

    app.before(JavalinJWT.createHeaderDecodeHandler(jwtProvider));


    app.before(c -> {
      String uuid = UUID.randomUUID().toString();
      c.attribute("Request-UUID", uuid);
      MDC.put("Request-UUID", uuid);
    });
    app.after(c -> {
      MDC.remove("Request-UUID");
      c.header("Request-UUID", c.attribute("Request-UUID"));
    });

    app.get("/healthcheck", c -> {
      c.header("Content-Type", "application/json");
      c.header("Cache-Control", "must-revalidate,no-cache,no-store");
      SortedMap<String, HealthCheck.Result> results = healthCheckRegistry.runHealthChecks();
      boolean anyFailed = results.entrySet()
        .stream()
        .map(e -> e.getValue().isHealthy())
        .filter(v -> v == false)
        .findAny()
        .isPresent();
      c.status(anyFailed ? 500 : 200);
      c.json(results);
    });

    return app;
  }

  @dagger.Provides
  MetricRegistry metricRegistry() {
    return new MetricRegistry();
  }

  @dagger.Provides
  HealthCheckRegistry healthCheckRegistry() {
    return new HealthCheckRegistry();
  }

  @dagger.Provides
  JWTProvider jwtProvider() {
    JWTProvider provider = new JWTProvider();
    return provider;
  }
}

