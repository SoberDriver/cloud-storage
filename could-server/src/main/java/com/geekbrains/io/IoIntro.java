package com.geekbrains.io;

import java.io.*;

public class IoIntro {

    private static final String APP_NAME = "could-server/";

    private static final byte[] buffer = new byte[1024];

    private void createServerDir(String dirName){
        File dir = new File(APP_NAME + dirName);
        if (!dir.exists()){
             dir.mkdir();
            System.out.println("sucksessful");
        }

    }

    private void transfer(File src, File dst){
        try (FileInputStream is = new FileInputStream(src);
             FileOutputStream os = new FileOutputStream(dst)) {
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String readAsString(String resourceName) throws IOException {
       InputStream inputStream =  getClass().getResourceAsStream(resourceName);
       int read = inputStream.read(buffer);
       return new String(buffer, 0, read);
    }

    public static void main(String[] args) throws IOException {
        IoIntro ioIntro = new IoIntro();
        System.out.println(ioIntro.readAsString("test.txt"));
        ioIntro.createServerDir("root");
        ioIntro.transfer(
                new File("D:\\Work\\Progs\\JavaProgs\\JAvaCorrect\\CloudService\\could-server\\src\\main\\resources\\com\\geekbrains\\io\\test.txt"),
                new File(APP_NAME + "root/copy.txt"));
    }
}
