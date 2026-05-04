/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.util;

import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author anwar
 */
public class MapValidation {
    
    public static boolean containsAllKeys(String[] keys,Map map){
        return Arrays.stream(keys).allMatch(map::containsKey);
    }
    
    public static boolean notContainsNull(Map map) {
        boolean flag = true;
        for (Object key : map.keySet()) {
            if (map.get(key.toString()) == null) {
                flag = false;
                return flag;
            }
        }
        return flag;
    }
}
