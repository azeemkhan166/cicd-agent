/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Prateek
 */
public class DateUtils {
    
    static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    
    public static Date convertStringToDate(String field, String dateFormatValue){
        
        try{
        DateFormat dateFormat = new SimpleDateFormat(dateFormatValue);
        return dateFormat.parse(field);
        }catch(ParseException objParseException){
            logger.error("Problem in DateUtils -> convertStringToDate() :: ", objParseException);
        }catch(Exception objException){
            logger.error("Problem in DateUtils -> convertStringToDate() :: ", objException);
        }
        return new Date();
    }
    
    public static boolean isDate(String dateStr,String dateFormat) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
