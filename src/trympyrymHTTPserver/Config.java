package trympyrymHTTPserver;

import trympyrymHTTPserver.FileServer.FileOption;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Trympyrym on 27.01.2017.
 */
public class Config {

    private final String filename;

    private String directory;
    private int port;
    private int nThreads = 4;
    private int minCacheBlock = 100000;


    public int getNThreads() {
        return nThreads;
    }

    public int getMinCacheBlock() {
        return minCacheBlock;
    }


    public Map<Path, Set<FileOption>> getFileOptions() {
        return fileOptions;
    }

    private Map<Path, Set<FileOption>> fileOptions = new HashMap<>();

    private ParserState state;

    public Config(String filename) {
        this.filename = filename;
    }

    public void read() throws IOException {
        state = ParserState.REGULAR;
        Files.lines(Paths.get(filename)).forEachOrdered(line->parseArgString(line.trim()));
    }

    public String getDirectory() {
        return directory;
    }

    public int getPort() {
        return port;
    }

    private enum ParserState{
        REGULAR, READING_FILES_NO_CACHE, READING_FILES_IGNORE
    }
    private void parseArgString(String line)
    {
        if (line.equals("NO_CACHE"))
        {
            state = ParserState.READING_FILES_NO_CACHE;
            return;
        }
        if (line.equals("IGNORE"))
        {
            state = ParserState.READING_FILES_IGNORE;
            return;
        }
        if (state == ParserState.REGULAR)
        {
            String[] splittedLine = line.split(" = ");
            String name = splittedLine[0];
            String value = splittedLine[1];
            if (name.equals("minCacheBlock"))
            {
                minCacheBlock = Integer.parseInt(value);
                return;
            }
            if (name.equals("nThreads"))
            {
                nThreads = Integer.parseInt(value);
                return;
            }
            if (name.equals("directory"))
            {
                directory = value;
                return;
            }
            if (name.equals("port"))
            {
                port = Integer.parseInt(value);
                return;
            }
            return;
        }
        if (state == ParserState.READING_FILES_NO_CACHE)
        {
            if (!fileOptions.containsKey(line))
            {
                fileOptions.put(getPath(line), new HashSet<>());
            }
            fileOptions.get(getPath(line)).add(FileOption.NO_CACHE);
        }
        if (state == ParserState.READING_FILES_IGNORE)
        {
            if (!fileOptions.containsKey(line))
            {
                fileOptions.put(getPath(line), new HashSet<>());
            }
            fileOptions.get(getPath(line)).add(FileOption.IGNORE);
        }
    }

    private Path getPath(String argString)
    {
        return Paths.get(directory + File.separator + argString);
    }


}
