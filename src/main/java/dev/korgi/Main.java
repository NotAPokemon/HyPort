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
            if (config.hasKey("Pre-Install")) {
                List<JSONObject> preInstall = config.getObjectList("Pre-Install");
                for (JSONObject command : preInstall) {
                    String exe = command.getString("exe");
                    List<String> argsList = command.getStringList("args");
                    StringBuilder commandBuilder = new StringBuilder();
                    commandBuilder.append(exe);
                    for (String arg : argsList) {
                        commandBuilder.append(" ").append(arg);
                    }
                    FileHander.executeIn(commandBuilder.toString(), extractedPath);
                }
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
        } finally {
            FileHander.cleanUp();
        }
    }

}
