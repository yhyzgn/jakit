package com.yhy.jakit.core.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * class 工具类
 * <p>
 * Created on 2022-03-23 17:54
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ClassUtils {
    private static final String PROTOCOL_FILE = "file";
    private static final String PROTOCOL_JAR = "jar";
    private static final String CLASS_SUFFIX = ".class";
    private static final String PACKAGE_SEPARATOR = ".";

    /**
     * 该类是否被某个注解注解
     *
     * @param clazz      类
     * @param annotation 注解
     * @return 是否被注解
     */
    public static boolean isAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return null != clazz && null != annotation && null != clazz.getAnnotation(annotation);
    }

    /**
     * 获取包下的所有类
     *
     * @param pkg         包
     * @param annotations 注解
     * @return 类
     */
    public static List<Class<?>> getClassList(Package pkg, List<Class<? extends Annotation>> annotations) {
        return getClassList(pkg.getName(), annotations);
    }

    /**
     * 获取包下的所有类
     *
     * @param packageName 包
     * @param annotations 注解
     * @return 类
     */
    public static List<Class<?>> getClassList(String packageName, List<Class<? extends Annotation>> annotations) {
        List<Class<?>> classList = loadClassListInPackage(packageName);
        return getClassList(classList, annotations);
    }

    /**
     * 获取被注解的类
     *
     * @param classList   所有类
     * @param annotations 注解
     * @return 被注解的类
     */
    public static List<Class<?>> getClassList(List<Class<?>> classList, List<Class<? extends Annotation>> annotations) {
        if (null != annotations && annotations.size() > 0) {
            List<Class<?>> temp = new ArrayList<>(classList);
            classList = new ArrayList<>();
            for (Class<?> clazz : temp) {
                Annotation[] tempAnnotations = clazz.getAnnotations();
                List<Class<? extends Annotation>> annotationList = Arrays.stream(tempAnnotations).map(Annotation::annotationType).collect(Collectors.toList());
                annotationList.retainAll(annotations);
                if (annotationList.size() == annotations.size()) {
                    classList.add(clazz);
                }
            }
        }
        return classList;
    }

    /**
     * 获取接口实现类，普通子类
     *
     * @param packageName 包
     * @param assigned    父类，包括接口类
     * @return 类
     */
    public static List<Class<?>> getClassList(String packageName, Class<?> assigned) {
        List<Class<?>> classList = loadClassListInPackage(packageName);
        return getClassList(classList, assigned);
    }

    /**
     * 获取接口实现类，普通子类
     *
     * @param classList 所有类
     * @param assigned  父类，包括接口类
     * @return 被注解的类
     */
    public static List<Class<?>> getClassList(List<Class<?>> classList, Class<?> assigned) {
        if (CollectionUtils.isNotEmpty(classList)) {
            // 如果是接口类，找到其所有实现类
            // 如果是普通类，找到其子类
            // 排除自身
            return classList.stream().filter(clazz -> assigned.isAssignableFrom(clazz) && clazz != assigned).collect(Collectors.toList());
        }
        return classList;
    }

    @NotNull
    private static List<Class<?>> loadClassListInPackage(String packageName) {
        List<Class<?>> classList = new ArrayList<>();
        String packagePath = packageName.replace(PACKAGE_SEPARATOR, File.separator);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> urls = loader.getResources(packagePath);
            URL url;
            String protocol;
            String dirPath;
            while (urls.hasMoreElements()) {
                if (null != (url = urls.nextElement())) {
                    protocol = url.getProtocol();
                    if (PROTOCOL_FILE.equals(protocol)) {
                        // 普通文件
                        dirPath = URLDecoder.decode(url.getFile(), "UTF-8");
                        loadClassListInPackage(packageName, dirPath, classList);
                    } else if (PROTOCOL_JAR.equals(protocol)) {
                        // jar包
                        loadClassListInJar(packagePath, url, classList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    /**
     * 加载包内的类
     *
     * @param packageName 包
     * @param dirPath     包对应的目录
     * @param classList   类
     */
    private static void loadClassListInPackage(String packageName, String dirPath, List<Class<?>> classList) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles(temp -> temp.isDirectory() || temp.getName().endsWith(CLASS_SUFFIX));
        if (null == files) {
            return;
        }

        String className;
        for (File file : files) {
            if (file.isDirectory()) {
                loadClassListInPackage(packageName + PACKAGE_SEPARATOR + file.getName(), file.getAbsolutePath(), classList);
            } else {
                className = file.getName().replace(CLASS_SUFFIX, "");
                try {
                    classList.add(Class.forName(packageName + PACKAGE_SEPARATOR + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 加载jar包中的类
     *
     * @param packagePath 包路径
     * @param url         jar路径
     * @param classList   类
     */
    private static void loadClassListInJar(String packagePath, URL url, List<Class<?>> classList) {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            JarEntry entry;
            String name;
            int index;
            String className;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                name = entry.getName();
                if (name.startsWith(File.separator)) {
                    name = name.substring(1);
                }
                if (name.startsWith(packagePath)) {
                    index = name.lastIndexOf(File.separator);
                    if (index != -1) {
                        String packageName = name.substring(0, index).replace(File.separator, PACKAGE_SEPARATOR);
                        if (name.endsWith(CLASS_SUFFIX) && !entry.isDirectory()) {
                            className = name.substring(packageName.length() + 1, name.length() - CLASS_SUFFIX.length());
                            try {
                                classList.add(Class.forName(packageName + PACKAGE_SEPARATOR + className));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}