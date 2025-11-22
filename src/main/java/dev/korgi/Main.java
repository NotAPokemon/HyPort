package dev.korgi;

import java.io.IOException;

import dev.korgi.file.JSONObject;

public class Main {

    public static void main(String[] args) throws IOException {

        String name = "NotAPokemon";
        String repo = "HyPort";

        String branchName = "main";

        try {
            JSONObject config = RepoExtractor.extract(name, repo, branchName);
            System.out.println(config.getString("Version"));
        } catch (Exception e) {
            ErrorHander.handleError(e);
        }
    }

}
