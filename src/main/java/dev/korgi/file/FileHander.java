package dev.korgi.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileHander {

    public static void download(String uri, Path destination) throws IOException {
        URL url = URI.create(uri).toURL();
        InputStream stream = url.openStream();
        Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
    }

}
