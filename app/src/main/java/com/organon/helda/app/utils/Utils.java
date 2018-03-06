package com.organon.helda.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Utils {

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        String str = "";

        if (inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inputStream.close();
            }
            str = sb.toString();
        }
        return str;
    }
}
