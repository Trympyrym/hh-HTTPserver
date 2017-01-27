import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
	// write your code here
        HTTPServer httpServer = new HTTPServer();

        httpServer.mainloop();
    }
}
