# Bugly
热修复笔记


热修复：在我们应用上线后出现bug需要及时修复时，不用再发新的安装包（用户安装周期长，不能保证新的包没有错误），只需要发布补丁包（好像是jar类型的），在客户无感知下修复掉bug；

过程：使用gradle生成补丁包，然后发送给服务器，然后在客户端使用时，去下载补丁包，然后在APP中添加使用方法（我的理解大概是去服务端获取，每获取到一个就保存，并通过路径去调用补丁包）

怎么进行热修复：

服务端：补丁包管理；

用户端：执行热修复；

开发端：生成补丁包；


（开发端）

补丁包是什么：

如何生成补丁包：

开启混淆后如何处理；

如何自动生成补丁包（自动对比修改内容，生成补丁包）：


（用户端）

什么时候执行热修复（不同时机会有什么影响）：

怎么执行热修复（从服务端获取到补丁包后如何去用它）：

android版本兼容问题：


热修复解决方案：常见的有腾讯的Tinker，QZone，阿里的AndFix，美团的Robust

AndFix：基本被淘汰，没有维护了

在native（c/c++）动态替换java层的方法，通过native层hook java层的代码

Robust（对apk的大小会有一定影响）

对每个函数都在编译打包阶段（字节码插桩技术）自动的插入了一段代码。类似于代理，将方法执行的代码重定向到其他方法中。

Tinker（增量更新）（查分算法：bsdiff，bspatch，有window版的exe文件，exe文件的源代码是.c文件，用到android上需要使用ndk部分 ）

Tinker通过计算对比指定的base apk中的dex与修改后的apk中的dex的区别，补丁包中的内容即为两者差分的描述，运行时将base apk中的dex与补丁包进行合成，重启后加载全新的合成后的dex文件

QZone：直接将修改的类打包成dex文件，将dex插入到类加载数组的最前方，这样类加载时，只会加载最前方的修改过的类，不会调用之后的类

管理、安装、卸载程序：pms（packageManager Service）


为什么Tinker Qzone要重启生效？


ClassLoader：

ClassLoader--> BootClassLoader（用于加载framework中的dex文件【Activity.class.getClassLoader()】）

                    --> BaseClassLoader-->PatchClassLoader（用于加载android应用程序中的类【AppCompatActivity.class.getClassLoader()【第三方依赖，不在手机中】，MainActivity.class.getClassLoader()】）

                                                     -->DexClassLoader


PatchClassLoader的构造方法中的ClassLoader parent是BootClassLoader

PatchClassLoader加载方法：现在缓存中找，有则返回，没有去执行parent（BootClassLoader）的loadClass方法，如果还找不到，再自己加载【双亲委托机制】，用的是pathList找（对dexElements（classes.dex，classes2.dex，classes3.dex，顺序固定）进行foreach循环，每个元素（Element）调用dexFile方法，获取DexFile文件（一个DexFile对应应该classes.dex））

为什么使用双亲委托机制：1. 避免重复加载（加载子类，要先加载父类，比如说，MainActivity的加载会先加载Activity，这个时候，BootClassLoader已经加载过Activity，如果不使用双亲委托机制，就可能会再次加载Activity，造成重复加载），2. 安全性考虑，防止核心api库被随意篡改（如果自己写了一个String类，如果不使用双亲委托，会用自己写的类替换掉系统中的String类）

dexElements是有顺序的，所以，将补丁包插入到dexElements数组的前边，就会先找到补丁包中的类，就可以更新了

已经加载过的类还能够替换修复吗？不能，已经加载过的类，会去缓存中查找，不会被重新加载，所以，Tinker和Qzone要重启生效（为了防止想要修改过的类被加载过，有缓存，所以，直接重启【不能直接清除缓存】）

（没必要反射ClassLoader，因为用的太多了）

如何将补丁包插入到Element数组的最前方：

获取当前应用的PathClassLoader；

反射获取到DexPathList属性对象pathList；

反射修改pathList的dexElements

	把补丁包patch.dex转化为Element[](patch)

	获得pathList的dexElements属性(old)

	path+dexElements合并并反射赋值给pathList的dexElements

加载-》链接-》初始化

链接：验证 准备 解析

Class.forName()和ClassLoader.loaderClass()的区别

Class.forName()包含初始化操作（如果有静态代码块会直接运行）

ClassLoader.loaderClass()不进行解析，意味着不进行包括初始化等一些列步骤，那么，静态块和静态对象就不会得到执行

反射中getMethods与getDeclaredMethods的区别

getMethods用于获取public的成员方法，包括从父类继承的public方法；

getDeclaredMethods用户获取当前类中的方法（不分public和非public），不包括从父类继承的方法


getField用于获取public的成员变量，包括从父类继承的public成员变量；

getDeclaredField用户获取当前类中的变量（不分public和非public），不包括从父类继承的变量；



将补丁包插入到Element数组的最前方，需要获取到DexPathList属性对象pathList（父类的私有属性），该怎么获取？


方法执行：

实例方法：new 实例对象.实例方法

静态方法：类.静态 类方法


打补丁包，命令行执行（dx在SDKbuild-tools里）：dx --dex --output=patch.jar com/founq/sdk/bugly/MainActivity.class


android N（7.0）混编

5.0以上，android运行在art上

7.0不会将字节码转换为机器码


c/c++是静态语言，生成本地机器码，可直接在机器上运行

java-》class， android-》dex生成字节码，需要在VM（虚拟机）上解释执行



android N（7.0）混编对热修复的影响：

因为有art（7.0），启动应用时，把art中记录的类加载到ClassLoader中，根据类加载原理，类被加载了，无法被替换，即无法修复

解决：tinker会用自己创建的PathClassLoader替换掉系统中的PathClassLoader【通过反射设置】（会浪费掉一次谷歌的优化，其余时候，因为tinker自己做了dex2oat的优化）{

Thre.currentThr().setContextCl(classLoader);//自己的classLoader

//然后再去修改其他用到classLoader的地方，三个地方，至少


//然后顺手把补丁包的dex添进去


}



CLASS_ISPREVERIFIED：5.0以下版本使用补丁包时，会抛出异常；

原因：如果MainActivity类只引用utils，当打包时，MainActivity和utils在同一个dex中，加载MainActivity时，会被标记为CLASS_ISPREVERIFIED，如果使用补丁包中的utils时，会导致MainActivity与其引用的utils不在同一个dex，但MainActivity已经被标记过，此时出现冲突，校验失败

解决：Qzone，阻止MainActivity被标记；简单的说就是让MainActivity调用另一个dex中的utils方法，让他们从一开始就不在同一个dex中，如何调用其他dex中的类呢，用类加载和反射可以调用，但是，不能使MainActivity直接调用另一个dex中的方法，是间接方法，不行，所以，用编译时打包apk中的dex之前修改class实现，需要使用字节码插桩技术

class -> FileInputStream -> byte[] -> 修改 ->FileOutpustream

修改时需要小心的按照class的格式来修改，而对byte进行修改很容易出错，使用框架来修改：ASM/JAVASSIST


字节码插桩：往class中写代码

如何获得待插桩的class文件？（transform，android提供的gradle api）
Task :app:compileDebugJavaWithJavac  -> 使用javac编译出class文件（输入：.java源文件，输出：.class文件）

Task :app:transformClassesWithDexBuilderForDebug -> 生成dex文件（输入：.class文件，输出：.dex文件）


Task：

doFirst：注册一个监听，在执行Task之前回调

doLast：注册一个监听，在执行Task之后回调

如何获得待插桩的class文件：调用app:transformClassesWithDexBuilderForDebug 的doFirst获取app:transformClassesWithDexBuilderForDebug 的输入class（为什么不能用app:compileDebugJavaWithJavac的输出呢，因为有lib中的class，第一步执行完获取的不是全部的class，只能拿到app模块的，拿不到其他模块的class文件）（lib中只会生成aar/jar，不会生成dex，app:transformClassesWithDexBuilderForDebug 会包含所有class）

所以，获取transformClassesWithDexBuilderForDebug 的task，注册doFirst，然后用getClass获取全部class文件

afterEvaluate：注册监听，在gradle执行之前，要解析build.gradle，解析完会回调这个监听，也就是在其他任务执行之前


要插桩的类：可能出现bug，需要热修复的类，就要插桩（androidx或android support库没必要进行插桩，Application也不要插桩）


导入依赖，retrofit1.0和2.0，不会冲突，会自动使用最新的版本
