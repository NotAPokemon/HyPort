package dev.korgi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import dev.korgi.file.FileHander;
import dev.korgi.file.JSONObject;

public class Main {

    public static void main(String[] args) throws IOException {

        String name = "NotAPokemon";
        String repo = "HyPort";

        String branchName = "main";

        try {
            JSONObject config = RepoExtractor.extract(name, repo, branchName);
            Path extractedPath = Path.of(config.getString("extracted-path"));
            String os = System.getProperty("os.name").toLowerCase().contains("win") ? "win" : "unix";
            if (os.equals("unix")) {
                Path gradlewPath = extractedPath.resolve("gradlew");
                if (!Files.isExecutable(gradlewPath)) {
                    gradlewPath.toFile().setExecutable(true);
                }
            }
            if (config.hasKey("Pre-Install")) {
                List<JSONObject> preInstall = config.getObjectList("Pre-Install");
                for (JSONObject command : preInstall) {
                    if (!command.hasKey("exe") || !command.hasKey("args")) {
                        throw new IllegalArgumentException("Invalid Pre-Install command format");
                    }
                    if (command.hasKey("system")) {
                        if (!command.getString("system").equalsIgnoreCase("unix")) {
                            continue;
                        }
                    }
                    String exe = command.getString("exe");
                    List<String> argsList = command.getStringList("args");
                    StringBuilder commandBuilder = new StringBuilder();
                    commandBuilder.append(exe);
                    for (String arg : argsList) {
                        commandBuilder.append(" ").append(arg);
                    }
                    System.out.println("Executing command: " + commandBuilder.toString());
                    FileHander.executeIn(commandBuilder.toString(), extractedPath);
                }
            } else {
                // Default pre-install commands
                Path gradlew = extractedPath.resolve("gradlew.bat");
                if (!Files.exists(gradlew)) {
                    throw new IOException("gradlew.bat not found in extracted repo");
                }
                FileHander.executeIn("gradlew.bat build", extractedPath);
            }
            String projName;
            if (config.hasKey("ProjectName")) {
                projName = config.getString("ProjectName");
            } else {
                projName = repo;
            }
            Path jarPath;
            if (config.hasKey("jar")) {
                jarPath = Path.of(extractedPath.toString(), config.getString("jar"));
            } else {
                jarPath = Path.of(extractedPath.toString(), String.format("build/libs/%s.jar", repo));
            }
            // TODO: update download location to plugin folder if jar isnt already there
            Path finalJarPath = Files
                    .createFile(Path.of(System.getProperty("user.dir"), String.format("%s.jar", projName)));
            Files.copy(jarPath, finalJarPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            ErrorHander.handleError(e);
        }

        FileHander.cleanUp();

    }

}
