package com.example.beautycamera.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 读取着色器程序的代码成字符串
 */
public class TextResourceReader {

    /**
     * 用于读取 GLSL文件中着色器代码
     *
     * @param context
     * @param resourceId
     * @return 顶点着色器 片元着色器 字符串代码
     */
    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString();
    }
}

