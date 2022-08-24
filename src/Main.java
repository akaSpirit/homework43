import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            server.start();
            initRoutes(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 9889);

        System.out.printf("Start the server at: http://%s:%s%n", address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 50); //backlog - количество попыток.
        System.out.println("Successfully!");

        return server;
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleRootRequest);
        server.createContext("/apps/", Main::handleAppsRequest);
        server.createContext("/apps/profile/", Main::handleProfileRequest);
    }

    private static void handleRootRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Context-Type", "text/plain; charset=UTF-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                write(writer, "HTTP method", method);
                write(writer, "Root request", uri.toString());
                write(writer, "Root processed through", ctxPath);
                writeHeaders(writer, "Root request headers", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAppsRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Context-Type", "text/plain; charset=UTF-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                write(writer, "HTTP method", method);
                write(writer, "Apps request", uri.toString());
                write(writer, "Apps processed through", ctxPath);
                writeHeaders(writer, "Apps request headers", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleProfileRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Context-Type", "text/plain; charset=UTF-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                write(writer, "HTTP method", method);
                write(writer, "Profile request", uri.toString());
                write(writer, "Profile processed through", ctxPath);
                writeHeaders(writer, "Profile request headers", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }

    private static void write(Writer writer, String msg, String method) {
        String data = String.format("%s: %s%n%n", msg, method);

        try {
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k,v) -> write(writer, "\t" + k, v.toString()));
    }

    public static BufferedReader getReader(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, charset);
        return new BufferedReader(isr);
    }

    public static void writeData(Writer writer, HttpExchange exchange) {
        try(BufferedReader reader = getReader(exchange)) {
            if(!reader.ready()) return;
            write(writer, "Data block", "");
            reader.lines().forEach(e -> write(writer, "\t", e));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}