package com.storytellers.tutor.app.Utils;

public class CheckerUtils {

    public static boolean isEmailValid(String email_str) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email_str).matches();
    }

}
