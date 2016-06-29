package com.leon.tools.utils;

import org.apache.http.NameValuePair;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * 集成java工具方法
 *
 * @author zhanghong
 * @time 2015-6-8
 */
public class JavaUtils {

    public static float toFloat(String f) {
        if (null != f) {
            return Float.parseFloat(f);
        }
        return 0f;
    }

    public static int toInt(String i) {
        if (null != i) {
            return Integer.parseInt(i);
        }
        return 0;
    }

    public static String getCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    public static double toDouble(String d) {
        if (null != d) {
            return Double.parseDouble(d);
        }
        return 0d;
    }

    public static String fromFloat(float f) {
        return String.valueOf(f);
    }

    public static String fromInt(int i) {
        return String.valueOf(i);
    }

    public static String fromDouble(Double d) {
        return String.valueOf(d);
    }

    public static int getInteger(byte integer) {
        return integer & 0xff;
    }

    public static byte getByte(int integer, int offset) {
        return (byte) ((integer >> offset) & 0xff);
    }

    private static int getInteger(byte integer, int offset) {
        return getInteger(integer) << offset;
    }

    // byte[]{0x11,0x22,0x33,0x44}(height->low)->0x44332211
    public static int getInteger(byte... integer) {
        int nInteger = 0;
        int size = integer.length > 4 ? 4 : integer.length;
        // for (int i = size; i > 0; i--) {
        // nInteger += getInteger(integer[i - 1], (size - i) << 3);
        // }
        for (int i = 0; i < size; i++) {
            nInteger |= getInteger(integer[i], i << 3);
        }
        return nInteger;
    }

    // byte[]{0x11,0x22,0x33,0x44,0x55,0x66,0x77,0x88}(height->low)->0x8877665544332211
    public static long getLong(byte... longer) {
        long nLonger = 0;
        int size = longer.length > 8 ? 8 : longer.length;
        for (int i = 0; i < size; i++) {
            nLonger |= getLong(longer[i], i << 3);
        }
        return nLonger;
    }

    // 0x8877665544332211->byte[]{0x11,0x22,0x33,0x44,0x55,0x66,0x77,0x88}(height
    // -> low)
    public static byte[] getBytesFromLong(long lInt) {
        byte[] data = new byte[8];
        System.arraycopy(getBytes((int) (lInt >> 32)), 0, data, 4, 4);
        System.arraycopy(getBytes((int) (lInt)), 0, data, 0, 4);
        return data;
    }

    public static long getLong(byte longer, int offset) {
        return ((((long) longer) & (long) 0x00ff) << offset);
    }

    /*
     * 0x11223344-> byte[]{0x44,0x33,0x22,0x11}(height->low);
     */
    public static byte[] getBytes(int integer) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = getByte(integer, i << 3);
        }
        return bytes;
    }

    public static byte getByte(int integer) {
        return getByte(integer, 0);
    }

    public static byte[] getBytes(char[] data) {
        byte[] bytes = new byte[data.length];
        // System.arraycopy(array, 0, bytes, 0, bytes.length);
        for (int i = data.length - 1; i >= 0; i--) {
            bytes[i] = (byte) data[i];
        }
        return bytes;
    }

    public static char[] getChars(byte[] data) {
        char[] chars = new char[data.length];
        for (int i = data.length - 1; i >= 0; i--) {
            chars[i] = (char) data[i];
        }
        return chars;
    }

    public static byte[] getBytes(String data) {
        char[] array = data.toCharArray();
        return getBytes(array);
    }

    /**
     * get number of bit 1 in a binary integer number
     *
     * @param x
     * @return
     */
    public static int getBitCounts(int x) {
        int counts = 0;
        while (x != 0) {
            x = x & (x - 1);
            counts++;
        }
        return counts;
    }

    public static String getString(byte[] bytes) {
        if (null != bytes) {
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(b);
                builder.append(" ");
            }
            return builder.toString();
        }
        return null;
    }

    public static int getBitIndex(int content) {
        content = content > 0 ? content : -content;
        int i = 0;
        for (i = 0; content != 0; i++) {
            content = content >> 1;
        }
        return i;
    }

    public static int[] getBitIndexes(int content) {
        final int mask = content > 0 ? content : -content;
        final int counts = JavaUtils.getBitCounts(mask);
        if (counts > 0) {
            int[] indexes = new int[counts];
            int index = 0;
            for (int i = 0; i < 31; i++) {
                if (isBitMask(mask, i)) {
                    indexes[index++] = i;
                    if (index >= counts) {
                        break;
                    }
                }
            }
            return indexes;
        }
        return null;
    }

    public static boolean isBitMask(int mask, int index) {
        return (mask & (1 << index)) != 0;
    }

    public static String getHexString(byte[] bytes) {
        if (null != bytes) {
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append("0x");
                builder.append(Integer.toHexString(getInteger(b)));
                builder.append(", ");
            }
            return builder.toString();
        }
        return null;
    }

    public static String getHexString(byte b) {
        return Integer.toHexString(getInteger(b));
    }

    public static String getHexString(int i) {
        return Integer.toHexString(i);
    }

    public static String getCharString(byte... data) {
        if (null != data) {
            return new String(data);
        } else {
            return "";
        }
    }

    public static boolean isEquals(byte[] bytes1, byte[] bytes2) {
        if (null != bytes1 && null != bytes2) {
            if (bytes1 != bytes2) {
                if (bytes1.length == bytes2.length) {
                    for (int i = bytes1.length - 1; i >= 0; i--) {
                        if (bytes1[i] != bytes2[i]) {
                            return false;
                        }
                    }
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmail(String strEmail) {
        // String strPattern =
        // "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 通过url获取一个md5名字
     *
     * @param url
     * @return
     */
    public final static String createFileNameByUrl(String url) {
        return JavaUtils.getStringMD5(url) + JavaUtils.getCRC32(url);
    }

    public static String getStringMD5(String str) {
        if (null != str) {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            byte[] byteArray = str.getBytes();
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            String jmh = hexValue.toString();
            return jmh;
        }
        return "";
    }

    /**
     * 保留小数
     */
    public static String getCalculateDecimal(double number, int index) {
        return getCalculateDecimal(number, index, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 保留小数
     */
    public static String getCalculateDecimal(double number, int index, int roundModel) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(index, roundModel);
        return bd.toString();
    }

    public static String createUrlGetParams(HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet()) {
            builder.append(key);
            builder.append("=");
            builder.append(params.get(key));
            builder.append("&");
        }
        return builder.toString().substring(0, builder.length() - 1);
    }

    public static String createUrlGetParams(Collection<NameValuePair> params) {
        StringBuilder builder = new StringBuilder();
        if (params.size() > 0) {
            for (NameValuePair pair : params) {
                builder.append(pair.getName());
                builder.append("=");
                builder.append(pair.getValue());
                builder.append("&");
            }
            return builder.toString().substring(0, builder.length() - 1);
        }
        return "";
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        deleteFile(new File(path));
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteFile(f);
                }
            } else {
                file.delete();
            }
        }
    }

    public static long fileSize(String filePath) {
        return fileSize(new File(filePath));
    }

    public static long fileSize(File file) {
        long size = file.length();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                size += fileSize(f);
            }
        }
        return size;
    }

    public static File createFile(String path) throws IOException {
        File file = new File(path);
        return createFile(file);
    }

    public static File createFile(File file) throws IOException {
        if (!file.exists()) {
            File pfile = file.getParentFile();
            if (!pfile.exists()) {
                pfile.mkdirs();
            }
            file.createNewFile();
        }
        return file;
    }

    /**
     * 文件复制
     *
     * @param is
     * @param filePath
     * @return
     */
    public static File copyFile(InputStream is, String filePath) {
        BufferedInputStream bis = new BufferedInputStream(is);
        FileOutputStream fos = null;
        File tmp = null;
        File file = null;
        try {
            tmp = createFile(filePath + ".tmp");
            fos = new FileOutputStream(tmp);
            byte[] data = new byte[1024];
            int length = 0;
            while ((length = bis.read(data)) != -1) {
                fos.write(data, 0, length);
                fos.flush();
            }
            file = new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != file) {
            tmp.renameTo(file);
            return file;
        }
        return null;
    }

    public static int getMothInterval(String dateStart, String dateEnd, String format) {
        Date sDate = getDateFromString(dateStart, format);
        Date eDate = getDateFromString(dateEnd, format);
        if (null != sDate && null != eDate) {
            Calendar sCalendar = Calendar.getInstance(Locale.getDefault());
            sCalendar.setTime(sDate);
            Calendar eCalendar = Calendar.getInstance(Locale.getDefault());
            eCalendar.setTime(eDate);
            int startYear = sCalendar.get(Calendar.YEAR);
            int endYear = eCalendar.get(Calendar.YEAR);
            int startMth = sCalendar.get(Calendar.MONTH);
            int endMth = eCalendar.get(Calendar.MONTH);
            return endMth - startMth + (endYear - startYear) * 12;
        }
        return 0;
    }

    public static Date getDateFromString(String date, String format) {
        try {
            SimpleDateFormat f = new SimpleDateFormat(format);
            return f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getCRC32(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        return crc.getValue();
    }

    public static String getSHA1String(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1"); // 选择SHA-1，也可以选择MD5
            byte[] digest = md.digest(inStr.getBytes()); // 返回的是byet[]，要转化为String存储比较方便
            outStr = new String(digest);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }

    public static String getMoneyFormate(double money) {
        try {
            DecimalFormat df = new DecimalFormat("###,##0.00");
            String data = df.format(money);
            if (!data.contains(".")) {
                return data + ".00";
            } else if (data.startsWith(".")) {
                return "0" + data;
            }
            return data;
        } catch (Exception e) {
        }
        return "0.00";
    }

    public static String getIntMoneyFormate(int money) {
        try {
            DecimalFormat df = new DecimalFormat("###,###");
            String data = df.format(money);

            return data;
        } catch (Exception e) {
        }
        return "0";
    }

    public static String getHideFormate(int head, int foot, String message) {

        if (message.length() > foot + head) {
            StringBuffer temp = new StringBuffer(message.substring(0, head));
            for (int i = 0; i < message.length() - foot - head; i++) {
                temp.append("*");
            }
            temp.append(message.substring(message.length() - foot, message.length()));
            return temp.toString();
        }
        return message;
    }

    /**
     * 两点间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2) + Math.abs(y1 - y2) * Math.abs(y1 - y2));
    }

    /**
     * 计算点a(x,y)的角度
     *
     * @param x
     * @param y
     * @return
     */
    public static double pointTotoDegrees(double x, double y) {
        return Math.toDegrees(Math.atan2(x, y));
    }

    /**
     * 是否不为空
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return s != null && !"".equals(s.trim());
    }

    /**
     * 是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    /**
     * 通过{n},格式化.
     *
     * @param src
     * @param objects
     * @return
     */
    public static String format(String src, Object... objects) {
        int k = 0;
        for (Object obj : objects) {
            src = src.replace("{" + k + "}", obj.toString());
            k++;
        }
        return src;
    }

    /**
     * 点在圆肉
     *
     * @param sx
     * @param sy
     * @param r
     * @param x
     * @param y
     * @return
     */
    public static boolean checkInRound(float sx, float sy, float r, float x, float y) {
        return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
    }

    /*
     * 中文名输入校验 *
     */
    public static boolean validName(String name) {
        if (name == null && name == "" && name.length() == 0) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]{2,7}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            return matcher.matches();
        }
    }

    /**
     * 获取泛型的内部类型
     *
     * @param clazz
     * @param index
     * @return
     */
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        // 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return null;
        }
        // 返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * @param formate 数字格式
     * @param number  数字
     * @return
     */
    public static String getDecimalFormat(String formate, double number) {
        return new DecimalFormat(formate).format(number);
    }

    /**
     * 取默认两位小数
     *
     * @param number
     * @return
     */
    public static String getDecimalFormat(double number) {
        return new DecimalFormat("0.00").format(number);
    }

}
