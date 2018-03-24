package com.ctl.utils.ref;

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
        SoftReference<Person> aSoftRef=new SoftReference<Person>(aRef);
        WeakReference<Person> aWeatRef=new WeakReference<Person>(aRef);
        WeakReference<Person> aWeatRef2=new WeakReference<Person>( new  Person("ctl",26));
        aRef=null;
        System.out.println(aSoftRef.get());
        System.out.println(aWeatRef.get());
        System.out.println(aWeatRef2.get());
        System.gc(); System.gc();
        System.out.println(aSoftRef.get());
        System.out.println(aWeatRef.get());
        System.out.println(aWeatRef2.get());
    }
}
