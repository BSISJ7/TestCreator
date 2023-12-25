package TestCreator.utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClassFinder {

    public List<String> getFilesInPackage(String packageName) {
        String packagePath = packageName.replace('.', '/');

        try {
            URL url = getClass().getClassLoader().getResource(packagePath);
            if (url == null) {
                return Collections.emptyList();
            }

            URI uri = url.toURI();
            Path path;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                path = fileSystem.getPath(packagePath);
            } else {
                path = Paths.get(uri);
            }

            List<String> javaFiles = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".class"))
                    .collect(Collectors.toList());

            return javaFiles;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public ObservableList<String> getFilesInDirectory(String packageName) {
        String packagePath = packageName.replace('.', '/');

        try {
            URL url = getClass().getClassLoader().getResource(packagePath);
            if (url == null) {
                return FXCollections.observableArrayList();
            }

            URI uri = url.toURI();
            Path path;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                path = fileSystem.getPath(packagePath);
            } else {
                path = Paths.get(uri);
            }

            List<String> javaFiles = Files.walk(path, 1)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(filename -> filename.substring(0, filename.lastIndexOf('.')))
                    .collect(Collectors.toList());

            return FXCollections.observableArrayList(javaFiles);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}