package com.pc.project.apistarter.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

public class EncodeUtils {

    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    /**
     * BASE64编码
     */
    public static String encode(String text) throws UnsupportedEncodingException {
        return BASE64_ENCODER.encodeToString(text.getBytes(DEFAULT_URL_ENCODING));
    }

    /**
     * BASE64解码
     */
    public static String decode(String encodedText) throws UnsupportedEncodingException {
        return new String(BASE64_DECODER.decode(encodedText), DEFAULT_URL_ENCODING);
    }

    /**
     * URL 编码
     */
    public static String urlEncode(String part) throws UnsupportedEncodingException {
        return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
    }

    /**
     * URL 解码
     */
    public static String urlDecode(String part) throws UnsupportedEncodingException {
        return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
    }

}
