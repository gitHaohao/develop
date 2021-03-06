package com.blog.web.util;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonTools {

	private static Log Logger = LogFactory.getLog(CommonTools.class);

	private static byte[] lock = new byte[0]; 

	private static int generateCount = 0;

	public static final int TIMESTAMP_START_POS = 5;
	public static final int MAX_GENERATE_COUNT = 99;

	
	public static <T extends Enum<T>> String[] enumNameToStringArray(T[] values) {  
	    int i = 0;  
	    String[] result = new String[values.length];  
	    for (T value: values) {  
	        result[i++] = value.name();  
	    }  
	    return result;  
	}  
	  
	/**  
	 * 获取一个唯一的数字id 根据当前时间的毫秒数+2位随机数获取
	 */
	public static String getUniqueString() {
		synchronized (lock) {
			if (generateCount > MAX_GENERATE_COUNT) {
				generateCount = 0;
			}
			final String uniqueNumber = Long.toString(
					System.currentTimeMillis()).substring(TIMESTAMP_START_POS)
					+ Integer.toString(generateCount);
			generateCount++;
			return uniqueNumber;
		}
	}
	public static String getUuidString() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 删除磁盘上的软件包文件
	 */
	public static void deleteFile(String fileUrl) {
		final File file = new File(fileUrl);
		if (file.exists()) {
			boolean delOk = false;
			if (file.isFile()) {
				delOk = file.delete();
			}
			if (!delOk) {
				Logger.error("delete file failed:" + fileUrl);
			}
		}
	}
	
	/**
	 * 拷贝文件
	 */
	public static long copyFile(String srcFile, String targetPath,
			String targetFileName) {
		long bytesum = 0;
		int byteread = 0;
		Logger.debug("copy file from " + srcFile + " to " + targetPath + " name "
				+ targetFileName);
		InputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			final File oldfile = new File(srcFile);
			final File newfile = new File(targetPath);
			if (!newfile.exists()) {
				boolean mkdirOk = false;
				mkdirOk = newfile.mkdirs();
				if (!mkdirOk) {
					Logger.error("mkdir failed:" + targetPath);
				}
			}
			if (oldfile.exists()) {
				inStream = new FileInputStream(srcFile); 
				outStream = new FileOutputStream(targetPath + targetFileName);
				final byte[] buffer = new byte[1444];
				byteread = inStream.read(buffer);
				while (byteread != -1) {
					bytesum += byteread; 
					outStream.write(buffer, 0, byteread);
					byteread = inStream.read(buffer);
				}
				outStream.close();
				inStream.close();
			} else {
				Logger.error("src file " + srcFile + " not exists!");
			}
		} catch (Exception e) {
			Logger.error(" error", e);
		} finally {
			CommonTools.closeIgnoringException(inStream);
			CommonTools.closeIgnoringException(outStream);
		}
		return bytesum;
	}

	/**
	 * 返回当天的的字符串表示
	 */
	public static String getDateStr(Date date) {
        if (date == null) {
            date = new Date();
        }
		final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		return sf.format(date);
	}

	/**  
	 * 返回年月日分割的文件路径
	 */
	public static String getFilePathStrFromDate(Date date) {     
        if (date == null) {
            date = new Date();
        }
	    final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");     
	    final String curDate = sf.format(date);     
	    final String[] cur = curDate.split("-");        
	    final String year_month_day = cur[0] + "/" + cur[1] + "/" + cur[2] + "/";      
	    return year_month_day;      
	}
	
	/**
	 * 公用的关闭流的代码
	 */
	public static void closeIgnoringException(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException ex) {
				Logger.error("error occured in close file:", ex);
			}
		}
	}
	
    /**  
     *获取请求的来源ip
     */
    public static String getRequestSrcIp(HttpServletRequest request) {
        
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            final int idx = ip.indexOf(',');
            if (idx > -1) {
                ip = ip.substring(0, idx);
            }
        }
        return ip;
    }
    
    /**  
     *根据给定的ip 列表验证给定的ip 是否合法
     * @param srcIp
     * @param valideIps
     * @return 
     */
    public static boolean isValidRequestIp(String srcIp, List<String> validIps) {
        if (validIps == null || validIps.size() == 0) {
            return false;
        }
        Iterator<String> iter = validIps.iterator();
        while (iter.hasNext()) {
            String ipRegx = iter.next();
            if (srcIp.matches(ipRegx)) {
                return true;
            }
        }
        return false;
    }

    private static byte uniteBytes(String src0, String src1) {  
        byte b0 = Byte.decode("0x" + src0).byteValue();  
        b0 = (byte) (b0 << 4);  
        byte b1 = Byte.decode("0x" + src1).byteValue();  
        byte ret = (byte) (b0 | b1);  
        return ret;  
    }  
      
    /**  
     *十六进制字符集转换成 byte
     * @param hexStr
     * @return 
     */
    public static byte[] hexStr2Bytes(String hexStr) {  
        int m = 0, n = 0;  
        int l = hexStr.length() / 2;  
        byte[] ret = new byte[l];  
        for (int i = 0; i < l; i++) {  
            m = i * 2 + 1;  
            n = m + 1;  
            ret[i] = uniteBytes(hexStr.substring(i * 2, m), hexStr.substring(m, n));  
        }  
        return ret;  
    }      

    /**  
     * bytes 转换成十六进制字符串
     *
     * @param b
     * @return 
     */
    public static String byte2HexStr(byte[] b) {  
        String hs = "";  
        String stmp = "";  
        for (int n = 0; n < b.length; n++) {  
            stmp = (Integer.toHexString(b[n] & 0XFF));  
            if (stmp.length() == 1)  
                hs = hs + "0" + stmp;  
            else  
                hs = hs + stmp;  
        }  
        return hs.toUpperCase();  
    }
    


    /**
     * 获取邮件服务器的访问地址
     * @param email
     * @return
     */
    public static String getEmailUrl(String email) {
        if (email != null && !"".equals(email)) {
            String url = email.substring(email.indexOf("@"));
            String ema = url.substring(1, url.indexOf("."));
            String em = ema.toLowerCase();

            if (em.equals("sohu")) {
                return "http://mail.sohu.com";
            } else if (em.equals("qq")) {
                return "http://mail.qq.com";
            } else if (em.equals("126")) {
                return "http://mail.126.com";
            } else if (em.equals("163")) {
                return "http://mail.163.com";
            } else if (em.equals("yeah")) {
                return "http://www.yeah.net";
            } else if (em.equals("yahoo")) {
                return "http://mail.cn.yahoo.com/?cns";
            } else if (em.equals("sina")) {
                return "http://mail.sina.com.cn/";
            } else if (em.equals("hotmail")) {
                return "http://www.hotmail.com";
            } else if (em.equals("gmail")) {
                return "http://gmail.google.com";
            } else if (em.equals("3g2win")) {
                return "http://mail.3g2win.com:3000/";
            } else {
                return "http://mail." + url.substring(1);
            }
        } else {
            return null;
        }
    }

   
}
