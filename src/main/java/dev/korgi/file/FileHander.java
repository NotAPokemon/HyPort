package dev.korgi.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileHander {

    private static List<Path> tempFiles = new ArrayList<>();

    public static void deleteOnExit(Path path) {
        tempFiles.add(path);
    }

    public static void download(String uri, Path destination) throws IOException {
        URL url = URI.create(uri).toURL();
        InputStream stream = url.openStream();
        Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void executeIn(String command, Path directory) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command.split(" "));
        builder.directory(directory.toFile());
        Process process = builder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed with exit code: " + exitCode);
        }
    }

    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    public static void cleanUp() {
        for (Path path : tempFiles) {
            try {
                deleteDirectoryRecursively(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
