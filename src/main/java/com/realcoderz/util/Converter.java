/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vineet
 */
@Component
public class Converter extends AbstractHttpMessageConverter<Object> {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    
    static final Logger logger = LoggerFactory.getLogger(Converter.class);

    private final ObjectMapper objectMapper;

    public Converter(ObjectMapper objectMapper) {
        super(MediaType.APPLICATION_JSON,
                new MediaType("application", "*+json", DEFAULT_CHARSET));
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz,
            HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return objectMapper.readValue(decrypt(inputMessage.getBody()), clazz);
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(encrypt(objectMapper.writeValueAsBytes(o)));
    }

    /**
     * requests params of any API
     *
     * @param inputStream inputStream
     * @return inputStream
     */
    private InputStream decrypt(InputStream inputStream) {
        //this is API request params
        StringBuilder requestParamString = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                requestParamString.append((char) c);
            }
        } catch (IOException e) {
            logger.error("Problem in Converter -> decrypt() :: ", e);
        }
        if (requestParamString.toString() != null) {
            return new ByteArrayInputStream(requestParamString.toString().getBytes(StandardCharsets.UTF_8));
        } else {
            return inputStream;
        }
    }

    /**
     * response of API
     *
     * @param bytesToEncrypt byte array of response
     * @return byte array of response
     */
    private byte[] encrypt(byte[] bytesToEncrypt) {
        // do your encryption here
        String apiJsonResponse = new String(bytesToEncrypt);

        String encryptedString = EncryptDecryptUtils.encrypt(apiJsonResponse);
        if (encryptedString != null) {
            //sending encoded json response in data object as follows

            //reference response: {"data":"thisisencryptedstringresponse"}
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("data", encryptedString);
            JSONObject jsob = new JSONObject(hashMap);
            return jsob.toString().getBytes();
        } else {
            return bytesToEncrypt;
        }
    }
}
