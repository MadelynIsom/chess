import java.net.URI;

public class client {

    public static void main(String[] args) throws Exception {
        new Repl(new URI("http://localhost:8080")).run();
    }
}
