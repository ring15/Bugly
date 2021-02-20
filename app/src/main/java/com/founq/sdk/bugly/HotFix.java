package com.founq.sdk.bugly;

import android.app.Application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ring on 2021/2/20.
 */
public class HotFix {

    /**
     * 1、获取当前应用的PathClassLoader；
     * 2、反射获取到DexPathList属性对象pathList；
     * 3、反射修改pathList的dexElements
     * 3.1、把补丁包patch.dex转化为Element[](patch)
     * 3.2、获得pathList的dexElements属性(old)
     * 3.3、path+dexElements合并并反射赋值给pathList的dexElements
     */
    public static void installPatch(Application application, File patchFile) {
        if (!patchFile.exists()) {
            return;
        }

        //1、获取当前应用的PathClassLoader；
        ClassLoader classLoader = application.getClassLoader();

        try {
            //2、反射获取到DexPathList属性对象pathList；
            Field pathList = ShareReflectUtil.findField(classLoader, "pathList");
            Object pathListField = pathList.get(classLoader);//从谁中拿到pathList就传谁

            //3、反射修改pathList的dexElements

            //3.1、把补丁包patch.dex转化为Element[](patch)
            List<File> files = new ArrayList<>();
            files.add(patchFile);//生成Element数组需要用到补丁包的集合
            File dexOutputDir = application.getCacheDir();//存放优化后的dex文件，需要获取内部路径，外部路径需要权限，且不安全，8.0之后传空即可
            ArrayList<IOException> suppressedException = new ArrayList<>();//系统代码中就这样写的
            Method method = ShareReflectUtil.findMethod(pathList, "makePathElements", List.class, File.class, List.class);//获取makePathElements方法（在DexPathList.java中）//List不能用ArrayList替换，必须一致
            //反射执行
            //method.invoke(pathList, files, dexOutputDir, suppressedException);//如果是实例方法，先传实例对象，再传参数
            Object[] pathElements = (Object[]) method.invoke(null, files,
                    dexOutputDir, suppressedException);//makePathElements是静态方法,首位传实例对象或者传null都可以，返回值是Element[]，但是，我们没有Element的引用，所以直接写Object[]


            //3.2、获得pathList的dexElements属性(old)
            Field dexElementsField = ShareReflectUtil.findField(pathListField, "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathListField);

            //3.3、path+dexElements合并并反射赋值给pathList的dexElements
            //创建新数组，装载两个数组中所有的元素
            //类名这样写："dalvik.system.DexPathList$Element"//用$连接外部类和内部类
            //Class.forName("dalvik.system.DexPathList$Element");//第一个参数，没必要，但可以
            //如果直接写pathElements.getClass()不行，得到的是Element[].class，而不是Element.class
            //pathElements.getClass().getComponentType()就是获取Element.class对象，也可以获取数组元素再获取class
            Object[] newElements = (Object[]) Array.newInstance(pathElements.getClass().getComponentType(),
                    pathElements.length + dexElements.length);//创建一个数组，参数1:类型，参数2: 长度
            System.arraycopy(pathElements, 0, newElements, 0, pathElements.length);//java提供的数组拷贝的方法，参数1：从谁拷贝，参数2：从哪个位置开始拷贝，参数3：拷贝到谁，参数4：拷贝到的位置从哪里开始，参数5：拷贝原数据多长的内容
            System.arraycopy(dexElements, 0, newElements, pathElements.length, dexElements.length);

            //反射赋值给pathList的dexElements
            dexElementsField.set(pathList, newElements);//取值用get方法，赋值用set方法，参数1：要设置哪一个参数的这个属性，参数2：新属性；把pathList里的dexElements属性设置成新的数据

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
