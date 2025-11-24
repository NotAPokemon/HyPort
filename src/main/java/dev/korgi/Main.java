package dev.korgi;

import java.io.IOException;
import dev.korgi.installation.RepoExtractor;

public class Main {

    public static void main(String[] args) throws IOException {

        String name = "NotAPokemon";
        String repo = "HyPort";

        String branchName = "main";

        if (args.length >= 2) {
            name = args[0];
            repo = args[1];
            branchName = null;
        } else if (args.length >= 3) {
            name = args[0];
            repo = args[1];
            branchName = args[2];
        } else {
            System.out.println(
                    "Using default repository: NotAPokemon/HyPort (Equivalent to passing 'NotAPokemon HyPort' as arguments this essentially updates this tool)");
        }

        RepoExtractor.install(name, repo, branchName);

    }

}
