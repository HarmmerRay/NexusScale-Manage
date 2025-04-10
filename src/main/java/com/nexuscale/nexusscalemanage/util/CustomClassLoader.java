package com.nexuscale.nexusscalemanage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// 自定义类加载器，继承自 ClassLoader
public class CustomClassLoader extends ClassLoader {
    // 类文件所在的基础目录
    private String classPath;

    public CustomClassLoader(String classPath) {
        this.classPath = classPath;
    }

    // 重写 findClass 方法
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 根据类的全限定名获取类的字节码
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        // 将字节码转换为 Class 对象
        return defineClass(name, classData, 0, classData.length);
    }

    // 加载类的字节码
    private byte[] loadClassData(String name) {
        // 将类的全限定名转换为文件路径
        String path = classPath + File.separatorChar + name.replace('.', File.separatorChar) + ".class";
        try (InputStream inputStream = new FileInputStream(path);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            // 读取类文件内容
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // 创建自定义类加载器实例
        CustomClassLoader classLoader = new CustomClassLoader("path/to/your/classes");
        // 加载指定类
        Class<?> clazz = classLoader.loadClass("com.example.MyClass");
        // 创建该类的实例
        Object obj = clazz.getDeclaredConstructor().newInstance();
        System.out.println(obj.getClass().getClassLoader());
    }
}
