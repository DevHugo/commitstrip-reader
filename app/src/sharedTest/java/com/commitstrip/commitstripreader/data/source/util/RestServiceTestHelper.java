package com.commitstrip.commitstripreader.data.source.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RestServiceTestHelper {

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(Context context, String filePath) throws Exception {

        final InputStream stream = RestServiceTestHelper.class.getClassLoader().getResource(filePath).openStream();

        String ret = convertStreamToString(stream);
        stream.close();

        return ret;
    }

}
