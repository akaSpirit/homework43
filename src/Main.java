import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        server.createContext("/", Main::handleIndexRequest);
        server.createContext("/apps/", Main::handleAppsRequest);
        server.createContext("/apps/profile/", Main::handleProfileRequest);
    }

    private static void handleAppsRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
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
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
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

    private static void handleIndexRequest(HttpExchange exchange) {
        Path path = Paths.get("./index.html");
        if (Files.exists(path)) {
            System.out.println("Exists");
            try {
                exchange.getResponseHeaders().add("Content-Type", "text/css, text/html; charset=UTF_8");
                int responseCode = 200;
                String response = readFile(path, StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(responseCode, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

                try (PrintWriter writer = getWriterFrom(exchange)) {
                    String method = exchange.getRequestMethod();
                    URI uri = exchange.getRequestURI();
                    String ctxPath = exchange.getHttpContext().getPath();
                    write(writer, "HTTP method", method);
                    write(writer, "Index request", uri.toString());
                    write(writer, "Index processed through", ctxPath);
                    writeHeaders(writer, "Index request headers", exchange.getRequestHeaders());
                    writeData(writer, exchange);
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println("Does not Exists");
    }

    static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
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