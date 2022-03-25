# ast-auto
利用编译时注解，在编译期动态修改 AST，织入一些代码，实现一些功能。

编译时注解不依赖其他框架，**性能没有任何损失**。

## 已实现功能

### @AutoLog

打印方法**入参**、**出参**及**执行耗时**。

* 添加在<font color=#FF4500>**方法**</font>上仅对标记方法生效
* 添加在<font color=#FF4500>**类**</font>上对**除构造方法以外**的所有方法生效

#### 源码

```java
@AutoLog
private Student test(String name, int age) {
    if (name == null) return new Student("无名", 0);
    if (age > 100) {
        throw new IllegalArgumentException("太老了");
    }
    return new Student(name, age);
}
```

#### 编译后

```java
private Student test(String name, int age) {
    long $$time$$ = System.currentTimeMillis();
    AutoLogAdapter.logArgs("<Main.test> args: ", new Arg("name", name), new Arg("age", age));
    Student $$ret$$ = null;
    try {
        if (name == null) {
            $$ret$$ = new Student("无名", 0);
            return $$ret$$;
        }
        if (age > 100) {
            throw new IllegalArgumentException("太老了");
        }
        $$ret$$ = new Student(name, age);
    } finally {
        AutoLogAdapter.logReturn("<Main.test> return: ", $$ret$$);
        AutoLogAdapter.logTime("<Main.test> time: ", System.currentTimeMillis() - $$time$$);
    }
    return $$ret$$;
}
```

#### 执行

```
<Main.test> args: name=张三, age=20
<Main.test> return: Student(stuName=张三, stuAge=20)
<Main.test> time: 1
```

## 计划实现功能

正在想
