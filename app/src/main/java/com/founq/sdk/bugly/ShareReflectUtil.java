package com.founq.sdk.bugly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ring on 2021/2/20.
 */
public class ShareReflectUtil {

    /**
     * 反射获取对象中的属性
     *
     * @param instance
     * @param name
     * @return
     */
    public static Field findField(Object instance, String name) {
        Class<?> clazz = instance.getClass();
        //判断clazz 不为空，或者，不是Object.class，判断clazz 不为空会多执行一次
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(name);//去找私有变量
                //如果找不到（在父类中，不能直接获取的情况）
                if (field != null) {
                    //设置访问权限
                    field.setAccessible(true);
                    return field;
                }
                clazz = clazz.getSuperclass();//获取父类
            } catch (NoSuchFieldException e) {
            }
        }
        throw new RuntimeException(name + " field not found");
    }

    /**
     * 反射获取对象中的方法
     *
     * @param instance
     * @param name
     * @return
     */
    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes) {
        Class<?> clazz = instance.getClass();
        //判断clazz 不为空，或者，不是Object.class，判断clazz 不为空会多执行一次
        while (clazz != Object.class) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);//去找私有方法（因为方法有重载，所以，要传参数类型）
                //如果找不到（在父类中，不能直接获取的情况）
                if (method != null) {
                    //设置访问权限
                    method.setAccessible(true);
                    return method;
                }
                clazz = clazz.getSuperclass();//获取父类
            } catch (NoSuchMethodException e) {
            }
        }
        throw new RuntimeException(name + " method not found");
    }
}
