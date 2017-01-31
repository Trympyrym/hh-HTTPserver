package trympyrymHTTPserver;

import trympyrymHTTPserver.FileServer.FileServer;
import trympyrymHTTPserver.HTTPserver.HTTPServer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
	// write your code here
//        if (args.length == 0)
//        {
//            System.out.println("Not enough arguments.");
//            return;
//        }
//        String configFilename = args[0];
        String configFilename = "config";

        Config config = new Config(configFilename);
        config.read();

        System.out.println("Config loaded");

        ExecutorService executor = Executors.newFixedThreadPool(config.getNThreads());

        FileServer fileServer = new FileServer(config, executor);
        fileServer.start();
        System.out.println("File server started");

        HTTPServer httpServer = new HTTPServer(config, executor, fileServer);

        System.out.println("HTTP server starting");
        httpServer.mainloop();
    }
}
