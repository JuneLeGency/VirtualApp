package com.lc.puppet.storage.backup;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by lichen:) on 2016/9/15.
 */
public class PaperExt {
    static PaperExt paperExt;
    private final Context context;

    synchronized public static PaperExt getPaperExt(Context context) {
        if (paperExt == null) {
            synchronized (PaperExt.class) {
                paperExt = new PaperExt(context);
            }
        }
        return paperExt;
    }

    public static final String backupPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "va_puppets";

    public static PaperExt getPaperExt() {
        return paperExt;
    }

    public PaperExt(Context context) {
        this.context = context.getApplicationContext();
    }

    public void copy() {
        String path = context.getFilesDir().getPath();
        copyFolder(path, backupPath);
    }

    public void recover() {
        String path = context.getFilesDir().getPath();
        copyFolder(backupPath, path);
    }

    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + File.separator +
                            (temp.getName()));
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + File.separator  + file[i], newPath +File.separator  + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }

    }
}
