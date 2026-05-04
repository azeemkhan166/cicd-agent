/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class MonthHandler {

    static List<Map<String, Object>> monthArr = new ArrayList<>();

       static {
        monthArr.add(createMonth("Apr", "April", 4));
        monthArr.add(createMonth("May", "May", 5));
        monthArr.add(createMonth("Jun", "June", 6));
        monthArr.add(createMonth("Jul", "July", 7));
        monthArr.add(createMonth("Aug", "August", 8));
        monthArr.add(createMonth("Sep", "September", 9));
        monthArr.add(createMonth("Oct", "October", 10));
        monthArr.add(createMonth("Nov", "November", 11));
        monthArr.add(createMonth("Dec", "December", 12));
        monthArr.add(createMonth("Jan", "January", 1));
        monthArr.add(createMonth("Feb", "February", 2));
        monthArr.add(createMonth("Mar", "March", 3));
    }

         // Optimized method to get months between considerFrom and considerTo
    public static List<Map<String, Object>> getMonthsBetween(int considerFrom, int considerTo) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Calculate the starting and ending indices based on the custom month order
        int startIndex = getMonthIndex(considerFrom);
        int endIndex = getMonthIndex(considerTo);

        // Traverse from start to end, handling wrap-around (April to March)
        int index = startIndex;
        do {
            result.add(monthArr.get(index));
            index = (index + 1) % monthArr.size();  // Increment and wrap around the list
        } while (index != (endIndex + 1) % monthArr.size());  // Stop when the end index is reached

        return result;
    }

        // Helper function to create a month map
    private static Map<String, Object> createMonth(String title, String description, int value) {
        Map<String, Object> month = new HashMap<>();
        month.put("title", title);
        month.put("description", description);
        month.put("value", value);
        return month;
    }

     // Helper function to get the index of a month based on its value (4 = April, 5 = May, etc.)
    private static int getMonthIndex(int monthValue) {
        for (int i = 0; i < monthArr.size(); i++) {
            if ((int) monthArr.get(i).get("value") == monthValue) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid month value: " + monthValue);
    }


}
