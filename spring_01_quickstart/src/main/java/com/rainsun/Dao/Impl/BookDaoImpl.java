package com.rainsun.Dao.Impl;

import com.rainsun.Dao.BookDao;

import java.util.*;

public class BookDaoImpl implements BookDao{
    private int[] myArray;
    private List<String> myList;
    private Set<String> mySet;
    private Map<String, String> myMap;
    private Properties myProperties;

    public void setMyArray(int[] myArray) {
        this.myArray = myArray;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }

    public void setMySet(Set<String> mySet) {
        this.mySet = mySet;
    }

    public void setMyMap(Map<String, String> myMap) {
        this.myMap = myMap;
    }

    public void setMyProperties(Properties myProperties) {
        this.myProperties = myProperties;
    }

    @Override
    public void save() {
        System.out.println("Book dao save");

        System.out.println("遍历数组：" + Arrays.toString(myArray));
        System.out.println("遍历List: " + myList);
        System.out.println("遍历set: " + mySet);
        System.out.println("遍历map: " + myMap);
        System.out.println("遍历properties: " + myProperties);
    }
}
