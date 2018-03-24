package com.ctl.utils.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Created by 康亚梅 on 2018/3/24.
 */
class Person{
    String name;
    int age;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
public class ReferenceDemo {
    public static void main(String args[]){
        Person aRef = new  Person("ctl",27);
        ReferenceQueue<Person> queue = new ReferenceQueue<Person>();
        ReferenceQueue<Person> aWeatRef2queue = new ReferenceQueue<Person>();

        SoftReference<Person> aSoftRef=new SoftReference<Person>(aRef);
        WeakReference<Person> aWeatRef=new WeakReference<Person>(aRef);
        WeakReference<Person> aWeatRef2=new WeakReference<Person>( new  Person("ctl",26),aWeatRef2queue);
        PhantomReference<Person> aPhantomRef = new PhantomReference<Person>(new  Person("ctl",25), queue);


        aRef=null;
        System.out.println("aSoftRef:"+aSoftRef.get());
        System.out.println("aWeatRef:"+aWeatRef.get());
        System.out.println("aWeatRef2:"+aWeatRef2.get());
        System.out.println("aPhantomRef:"+aPhantomRef.get());
        System.gc();
        try {
            Thread.sleep(200);//给gc充足的时间,如果此处没有sleep则aWeatRef2queue.poll()可能为空因为调用时可能还没有完成内存回收
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("aSoftRef:"+aSoftRef.get());
        System.out.println("aWeatRef:"+aWeatRef.get());
        System.out.println("aWeatRef2:"+aWeatRef2.get());
        System.out.println("aPhantomRef:"+aPhantomRef.get());
        System.out.println(queue.poll());
        System.out.println(aWeatRef2queue.poll() );

    }
}
