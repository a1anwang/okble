package com.a1anwang.okble.common;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a1anwang.com on 2017/6/27.
 * contact：www.a1anwang.com
 */

public class OKBLEDataUtils {
    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 获取低位
     * @param v
     * @return
     */
    public static byte loUint16(int v) {
        return (byte) (v & 0xFF);
    }

    /**
     * 获取高位
     * @param v
     * @return
     */
    public static byte hiUint16(int v) {
        return (byte) (v >> 8);
    }

    /**
     * 高低位 组成int，
     * @param hi
     * @param lo
     * @return
     */
    public static int buildUint16(byte hi, byte lo) {
        return (int) ((hi << 8) + (lo & 0xff));
    }
    /**
     *  字节数组转十六进制字符串
     *
     * @param b: byte[] bytes_1=new byte[]{(byte) 0xA0,(byte) 0xB1,2}
     * @return "A0B102"
     */
    public static String BytesToHexString(byte[] b) {
        if(b==null){
            return null;
        }
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    /**
     * 十六进制字符串 转 字节数组
     * @param hexString  "A0B102"
     * @return  new byte[]{(byte) 0xA0,(byte) 0xB1,2}
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        if(hexString.length()%2!=0){
            hexString="0"+hexString;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 16进制字符串转成  int 整数, "A0B102"=>10531074
     * @param valueStr   "A0B102"
     * @return  int:10531074
     */
    public static int hexStrToInt(String valueStr) {
        valueStr = valueStr.toUpperCase();
        if (valueStr.length() % 2 != 0) {
            valueStr = "0" + valueStr;
        }

        int returnValue = 0;

        int length = valueStr.length();

        for (int i = 0; i < length; i++) {

            int value = charToByte(valueStr.charAt(i));

            returnValue += Math.pow(16, length - i - 1) * value;
        }
        return returnValue;
    }

    /**
     *  int 转16进制字符串
     * @param i  10
     * @return  "A0"
     */
    public static String intToHexStr(int i){
        String hexStr=	Integer.toHexString(i);

        if(hexStr.length()%2!=0){
            hexStr="0"+hexStr;
        }
        return hexStr.toUpperCase();
    }

    /**
     * byte转bit
     * @param b
     * @return
     */
    public static String byteToBit(byte b) {
        return "" +(byte)((b >> 7) & 0x1) +
                (byte)((b >> 6) & 0x1) +
                (byte)((b >> 5) & 0x1) +
                (byte)((b >> 4) & 0x1) +
                (byte)((b >> 3) & 0x1) +
                (byte)((b >> 2) & 0x1) +
                (byte)((b >> 1) & 0x1) +
                (byte)((b >> 0) & 0x1);
    }

    /**
     * bit转byte
     * @param byteStr
     * @return
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }


    /**
     *截取byte数组
     * @param src
     * @param start
     * @param length
     * @return
     */
    public static byte[] subByteArray(byte[] src,int start,int length){


        byte[] result=new byte[length];

        System.arraycopy(src, start, result, 0, length);


        return result;

    }
    public static boolean isValidUUID(String uuid){
        String regEx = "^[a-fA-F0-9]{8}[-][a-fA-F0-9]{4}[-][a-fA-F0-9]{4}[-][a-fA-F0-9]{4}[-][a-fA-F0-9]{12}$";  //FDA50693-A4E2-4FB1-AFCF-C6EB07647825
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(uuid);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();

        return rs;
    }
    public static boolean isValidShortUUID(String uuid){
        String regEx = "^[a-fA-F0-9]{4}$";  //FDA50693-A4E2-4FB1-AFCF-C6EB07647825
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(uuid);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();

        return rs;
    }
    /**
     *
     * @param targetLenth  补齐后的长度
     * @param src     需要补齐的字符串
     * @param leftCharacter   用来补齐长度的字符
     * @return  补齐字符串
     */
    public static String formatStringLenth(int targetLenth, String src, char leftCharacter) {

        if(src.length()>targetLenth){
            return src.substring(src.length()-targetLenth);
        }else{

            int delta=targetLenth-src.length();
            for (int i = 0; i <delta ; i++) {
                src=leftCharacter+src;
            }
            return src;
        }
    }
}
