import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
	// write your code here
//        if (args.length == 0)
//        {
//            System.out.println("Not enough arguments.");
//            return;
//        }
//        String configFilename = args[0];
        String configFilename = "config";
        HTTPServer httpServer = new HTTPServer(configFilename);

        httpServer.mainloop();
    }
}
