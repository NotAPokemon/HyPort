package dev.korgi;

public class ErrorHander {

    public static void handleError(Exception e) {
        System.out.println("An error occurred: " + e.getMessage());
        e.printStackTrace();
    }

}
