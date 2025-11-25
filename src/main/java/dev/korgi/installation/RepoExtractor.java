package dev.korgi.installation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import dev.korgi.ErrorHander;
import dev.korgi.file.FileHander;
import dev.korgi.file.Zip;
import dev.korgi.json.JSONObject;

public class RepoExtractor {

    public static void install(String name, String repo, String branchName) {

        try {
            JSONObject config;
            if (branchName != null) {
                config = RepoExtractor.extract(name, repo, branchName);
            } else {
                config = RepoExtractor.extract(name, repo);
            }
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
                        if (!command.getString("system").equalsIgnoreCase(os)) {
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
                Path gradlew = extractedPath.resolve("gradlew.bat");
                if (!Files.exists(gradlew)) {
                    throw new IOException(
                            "gradlew.bat not found in extracted repo please include build instructions in the hyport-config");
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

            String installProtocal = "name-only";
            if (config.hasKey("install-protocal")) {
                installProtocal = config.getString("install-protocal");
            }
            String finalName;
            if ("name-only".equals(installProtocal)) {
                finalName = String.format("%s.jar", projName);
            } else if ("name-version".equals(installProtocal)) {
                String version = "1.0.0";
                if (config.hasKey("version")) {
                    version = config.getString("version");
                }
                finalName = String.format("%s-%s.jar", projName, version);
            } else if (installProtocal.contains("custom|||")) {
                finalName = installProtocal.replace("custom|||", "");
            } else {
                System.out.println("Unknown install-protocal, defaulting to name-only");
                finalName = String.format("%s.jar", projName);
            }
            // TODO: update download location to plugin folder if jar isnt already there
            Path finalJarPath = Files
                    .createFile(Path.of(System.getProperty("user.dir"), finalName));
            if (finalJarPath.toFile().exists()) {
                finalJarPath.toFile().delete();
            }
            Files.copy(jarPath, finalJarPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            ErrorHander.handleError(e);
        }

        FileHander.cleanUp();

    }

    public static JSONObject extract(String name, String repo, String branchName) throws IOException {
        String uri = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip", name, repo, branchName);
        Path tempFile = Files.createTempFile(repo + "-", ".zip");
        FileHander.download(uri, tempFile);
        Path output_dir = Files.createTempDirectory(repo + "-extracted-");
        FileHander.deleteOnExit(output_dir);
        FileHander.deleteOnExit(tempFile);
        Zip.unzip(tempFile.toString(), output_dir.toString());
        Path config = output_dir.resolve(String.format("%s-%s/hyport-config.json", repo, branchName));
        boolean clean = false;
        if (!Files.exists(config)) {
            JSONObject repoInfo = getRepoInfo(name, repo);
            if (!repoInfo.getString("language").equals("Java")
                    || !output_dir.resolve(String.format("%s-%s/gradlew", repo, branchName)).toFile().exists()) {
                throw new IOException(
                        "Invalid hyport repo structure: missing hyport-config.json and unable to execute default build");
            }
            clean = true;
        }
        JSONObject configObj = clean ? new JSONObject() : JSONObject.parse(config);
        configObj.addString("extracted-path",
                Path.of(output_dir.toString(), String.format("%s-%s", repo, branchName)).toString());
        return configObj;

    }

    private static JSONObject getRepoInfo(String name, String repo) throws IOException {
        String mainBranchURI = String.format("https://api.github.com/repos/%s/%s", name, repo);
        Path info = Files.createTempFile("info", ".json");
        FileHander.download(mainBranchURI, info);
        JSONObject repoInfo = JSONObject.parse(info);
        return repoInfo;
    }

    public static JSONObject extract(String name, String repo) throws IOException {
        JSONObject repoInfo = getRepoInfo(name, repo);
        String defaultBranch = repoInfo.getString("default_branch");
        return extract(name, repo, defaultBranch);
    }

}
