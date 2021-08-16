package com.yhy.jakit.core.utils;

import java.io.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * 文件工具类
 * <p>
 * Created on 2021-04-05 19:18
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class FileUtils {
    /**
     * 16进制数组常量
     */
    private final static char[] HEX_CODE = "0123456789abcdef".toCharArray();

    /**
     * 禁用构造方法
     */
    private FileUtils() {
        throw new UnsupportedOperationException("FileUtils can not be instantiate.");
    }

    /**
     * 判断文件是否存在
     *
     * @param filename 文件名
     * @return 是否存在
     */
    public static boolean exists(String filename) {
        return exists(new File(filename));
    }

    /**
     * 判断文件是否存在，不存在可控制自动创建
     *
     * @param filename 文件名
     * @param gen      是否自动创建
     * @return 是否存在
     */
    public static boolean exists(String filename, boolean gen) {
        return exists(new File(filename), gen);
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件对象
     * @return 是否存在
     */
    public static boolean exists(File file) {
        return exists(file, false);
    }

    /**
     * 判断文件是否存在，不存在可控制自动创建
     *
     * @param file 文件对象
     * @param gen  是否自动创建
     * @return 文件是否存在
     */
    public static boolean exists(File file, boolean gen) {
        return null != file && (file.exists() || gen && file.mkdirs());
    }

    /**
     * 获取文件大小
     *
     * @param filename 文件名
     * @return 文件大小
     */
    public static long size(String filename) {
        return size(new File(filename));
    }

    /**
     * 获取文件大小
     *
     * @param file 文件对象
     * @return 文件大小
     */
    public static long size(File file) {
        return null != file && file.exists() ? file.length() : 0;
    }

    /**
     * 格式化后的文件大小
     *
     * @param filename 文件名
     * @return 格式化后的文件大小
     */
    public static String sizeFormatted(String filename) {
        return formatSize(size(filename));
    }

    /**
     * 格式化后的文件大小
     *
     * @param file 文件对象
     * @return 格式化后的文件大小
     */
    public static String sizeFormatted(File file) {
        return formatSize(size(file));
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小
     * @return 格式化后的文件大小
     */
    public static String formatSize(long size) {
        double result = size;
        if (result < 1024) {
            return String.format("%.2fB", result);
        }
        result /= 1024;
        if (result < 1024) {
            return String.format("%.2fKB", result);
        }
        result /= 1024;
        if (result < 1024) {
            return String.format("%.2fMB", result);
        }
        result /= 1024;
        if (result < 1024) {
            return String.format("%.2fGB", result);
        }
        result /= 1024;
        if (result < 1024) {
            return String.format("%.2fTB", result);
        }
        return String.format("%.2fPB", result);
    }

    /**
     * 递归删除文件（夹）
     *
     * @param filename 文件名
     */
    public static void delete(String filename) {
        delete(new File(filename));
    }

    /**
     * 递归删除文件（夹）
     *
     * @param file 文件对象
     */
    public static void delete(File file) {
        if (null == file || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        if (null != files && files.length > 0) {
            for (File subFile : files) {
                delete(subFile);
            }
        }
        file.delete();
    }

    /**
     * 拷贝文件
     *
     * @param src  源文件名
     * @param dest 目标文件名
     * @throws IOException IO异常
     */
    public static void copy(String src, String dest) throws IOException {
        copy(new File(src), new File(dest));
    }

    /**
     * 拷贝文件
     * <p>
     * 可配置追加
     *
     * @param src    源文件名
     * @param dest   目标文件名
     * @param append 是否可追加
     * @throws IOException IO异常
     */
    public static void copy(String src, String dest, boolean append) throws IOException {
        copy(new File(src), new File(dest), append);
    }

    /**
     * 拷贝文件
     *
     * @param src  源文件名
     * @param dest 目标文件名
     * @throws IOException IO异常
     */
    public static void copy(File src, String dest) throws IOException {
        copy(src, new File(dest));
    }

    /**
     * 拷贝文件
     * <p>
     * 可配置追加
     *
     * @param src    源文件名
     * @param dest   目标文件名
     * @param append 是否可追加
     * @throws IOException IO异常
     */
    public static void copy(File src, String dest, boolean append) throws IOException {
        copy(src, new File(dest), append);
    }

    /**
     * 拷贝文件
     *
     * @param src  源文件
     * @param dest 目标文件
     * @throws IOException IO异常
     */
    public static void copy(File src, File dest) throws IOException {
        copy(src, dest, false);
    }

    /**
     * 拷贝文件
     * <p>
     * 可配置追加
     *
     * @param src    源文件
     * @param dest   目标文件
     * @param append 是否可追加
     * @throws IOException IO异常
     */
    public static void copy(File src, File dest, boolean append) throws IOException {
        if (null == src || !src.exists() || null == dest) {
            return;
        }
        copy(new FileInputStream(src), new FileOutputStream(dest, append));
    }

    /**
     * 拷贝IO流到文件
     *
     * @param src  源输入流
     * @param dest 目标文件
     * @throws IOException IO异常
     */
    public static void copy(InputStream src, File dest) throws IOException {
        copy(src, dest, false);
    }

    /**
     * 拷贝IO流到文件
     * <p>
     * 可配置追加
     *
     * @param src    源输入流
     * @param dest   目标文件
     * @param append 是否可追加
     * @throws IOException IO异常
     */
    public static void copy(InputStream src, File dest, boolean append) throws IOException {
        if (null == dest) {
            return;
        }
        copy(src, new FileOutputStream(dest, append));
    }

    /**
     * 拷贝文件到IO流
     *
     * @param src  源文件
     * @param dest 目标输出流
     * @throws IOException IO异常
     */
    public static void copy(File src, OutputStream dest) throws IOException {
        if (null == src || !src.exists()) {
            return;
        }
        copy(new FileInputStream(src), dest);
    }

    /**
     * 拷贝IO数据
     *
     * @param src  源输入流
     * @param dest 目标输出流
     * @throws IOException IO异常
     */
    public static void copy(InputStream src, OutputStream dest) throws IOException {
        if (null == src || null == dest) {
            return;
        }
        BufferedInputStream bis = new BufferedInputStream(src);
        BufferedOutputStream bos = new BufferedOutputStream(dest);
        int len = 0;
        byte[] buffer = new byte[20 * 1024];
        while ((len = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            bos.flush();
        }
        bis.close();
        bos.close();
    }

    /**
     * 获取文件md5值
     *
     * @param file 文件对象
     * @return md5 字符串
     * @throws Exception 各种异常
     */
    public static String md5(File file) throws Exception {
        if (null == file || !file.exists()) {
            return null;
        }
        MessageDigest digest = MessageDigest.getInstance("MD5");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        int len = 0;
        byte[] buffer = new byte[20 * 1024];
        while ((len = bis.read()) != -1) {
            digest.update(buffer, 0, len);
        }
        bis.close();
        return hexString(digest.digest());
    }

    /**
     * 获取文件后缀
     *
     * @param file 文件
     * @return 后缀
     */
    public static String suffix(File file) {
        return null == file ? null : suffix(file.getAbsolutePath());
    }

    /**
     * 获取文件后缀
     *
     * @param filename 文件名
     * @return 后缀
     */
    public static String suffix(String filename) {
        if (null == filename) {
            return null;
        }
        int extIndex = filename.lastIndexOf(".");
        if (extIndex == -1) {
            return null;
        }
        int folderIndex = filename.lastIndexOf(File.separator);
        if (folderIndex > extIndex) {
            return null;
        }
        return filename.substring(extIndex);
    }

    /**
     * 临时目录
     *
     * @param root    根路径
     * @param parents 临时目录的父目录 root/parents/temp
     * @return 临时文件夹
     */
    public static File temp(String root, String... parents) {
        return dir(root, "temp", parents);
    }

    /**
     * 正式目录
     *
     * @param root    根目录
     * @param parents 正式文件夹的父目录 root/parents/dest
     * @return 正式文件夹
     */
    public static File dest(String root, String... parents) {
        return dir(root, "dest", parents);
    }

    /**
     * 按日期分目录后的临时目录
     *
     * @param root         根目录
     * @param tempParents  临时目录的父目录 root/tempParents/temp
     * @param dailyParents 日期路径的父目录 temp/dailyParents/dailyPath
     * @return 临时文件夹 root/tempParents/temp/dailyParents/dailyPath
     */
    public static File tempDaily(String root, String[] tempParents, String[] dailyParents) {
        return dailyDir(temp(root, tempParents), dailyParents);
    }

    /**
     * 按日期分目录后的正式目录
     *
     * @param root         根目录
     * @param destParents  正式目录的父目录 root/destParents/temp
     * @param dailyParents 日期路径的父目录 temp/dailyParents/dailyPath
     * @return 临时文件夹 root/destParents/temp/dailyParents/dailyPath
     */
    public static File destDaily(String root, String[] destParents, String[] dailyParents) {
        return dailyDir(dest(root, destParents), dailyParents);
    }

    /**
     * 构建一个目录
     *
     * @param root    根目录
     * @param dirName 需要构建的目录名称
     * @param parents 改目录的父目录们
     * @return 构建好的目录文件夹
     */
    public static File dir(String root, String dirName, String... parents) {
        if (null == root) {
            root = File.separator;
        }
        File dir = new File(pathResolve(root + dirs(parents) + File.separator + dirName));
        return exists(dir, true) ? dir : null;
    }

    /**
     * 按日期分目录
     *
     * @param root    根目录
     * @param parents 日期路径的父目录
     * @return 日期目录文件夹 root/parents/dailyPath
     */
    public static File dailyDir(File root, String... parents) {
        if (null == root) {
            return dailyDir(File.separator, parents);
        }
        return dailyDir(root.getAbsolutePath(), parents);
    }

    /**
     * 按日期分目录
     *
     * @param root    根目录
     * @param parents 日期路径的父目录
     * @return 日期目录文件夹 root/parents/dailyPath
     */
    public static File dailyDir(String root, String... parents) {
        if (null == root) {
            root = File.separator;
        }
        File dir = new File(pathResolve(root + dirs(parents) + dailyPath()));
        return exists(dir, true) ? dir : null;
    }

    /**
     * 按日期分目录
     * <p>
     * 今日目录
     *
     * @return 今日目录
     */
    public static String dailyPath() {
        return new SimpleDateFormat(File.separator + "yyyy" + File.separator + "MM" + File.separator + "dd").format(Calendar.getInstance(Locale.getDefault()).getTime());
    }

    /**
     * 文件路径标准化处理
     *
     * @param path 文件路径ø
     * @return 处理后的路径
     */
    public static String pathResolve(String path) {
        return path.replaceAll("\\{1,}", File.separator).replaceAll("/{2,}", File.separator);
    }

    /**
     * 构建文件夹路径
     *
     * @param dirs 构建夹名称数组
     * @return 构建好的文件夹路径
     */
    private static String dirs(String... dirs) {
        if (null == dirs || dirs.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(dirs).forEach(dir -> {
            sb.append(File.separator).append(dir);
        });
        return sb.toString();
    }

    /**
     * 将字节数组转换为16进制字符串
     *
     * @param data 字节数组
     * @return 16进制字符串
     */
    private static String hexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte bt : data) {
            sb.append(HEX_CODE[(bt >> 4) & 0xf]).append(HEX_CODE[bt & 0xf]);
        }
        return sb.toString();
    }
}
