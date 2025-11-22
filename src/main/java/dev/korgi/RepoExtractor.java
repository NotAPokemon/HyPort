package dev.korgi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import dev.korgi.file.FileHander;
import dev.korgi.file.Zip;
import dev.korgi.json.JSONObject;

public class RepoExtractor {

    public static JSONObject extract(String name, String repo, String branchName) throws IOException {
        String uri = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip", name, repo, branchName);
        Path tempFile = Files.createTempFile(repo + "-", ".zip");
        FileHander.download(uri, tempFile);
        Path output_dir = Files.createTempDirectory(repo + "-extracted-");
        FileHander.deleteOnExit(output_dir);
        FileHander.deleteOnExit(tempFile);
        Zip.unzip(tempFile.toString(), output_dir.toString());
        Path config = output_dir.resolve(String.format("%s-%s/hyport-config.json", repo, branchName));
        System.out.println("Extracted to: " + output_dir.toString());
        if (!Files.exists(config)) {
            throw new IOException("Invalid hyport repo structure: missing hyport-config.json");
        }
        JSONObject configObj = JSONObject.parse(config);
        configObj.addString("extracted-path",
                Path.of(output_dir.toString(), String.format("%s-%s", repo, branchName)).toString());
        return configObj;

    }

    public static JSONObject extract(String name, String repo) throws IOException {
        String mainBranchURI = String.format("https://api.github.com/repos/%s/%s", name, repo);
        Path info = Files.createTempFile("info", ".json");
        FileHander.download(mainBranchURI, info);
        JSONObject repoInfo = JSONObject.parse(info);
        String defaultBranch = repoInfo.getString("default_branch");
        return extract(name, repo, defaultBranch);
    }

}
