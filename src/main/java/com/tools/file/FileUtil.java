package com.tools.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * 过滤指路歌曲文件
 * @author dylan_xu
 * @date Mar 11, 2012
 * @modified by
 * @modified date
 * @since JDK1.6
 * http://lavasoft.blog.51cto.com/62575/119747/
 * http://dylanxu.iteye.com/blog/1450070
 * http://www.cnblogs.com/linjiqin/archive/2011/04/21/2023227.html
 * http://www.open-open.com/lib/view/open1418003973448.html
 * http://www.jb51.net/article/46360.htm
 */
public class FileUtil {
    public static Logger logger = Logger.getLogger(FileUtil.class);
    public static Set<String> sets = new HashSet<String>();


    //public static void main(String[] args) {
    //    refreshFileList("G:\\Music");
    //    //moveFolder("G:\\music\\周杰伦", "E:\\Kugou");
    //}


    /**
     * 过滤MP3文件
     *
     * @param strPath
     */
    public static void refreshFileList(String strPath) throws IOException {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath());
            } else {
                String strFilePath = files[i].getAbsolutePath().toLowerCase();
                String strName = files[i].getName();
                if (strName.endsWith(".mp3")) {
                    boolean bFlag = sets.add(strName);
                    if (bFlag == Boolean.FALSE) {
                        // 删除重复文件
                        removeFile(strFilePath);
                    }
                }
                // System.out.println("FILE_PATH:" + strFilePath + "|strName:" +
                // strName);
            }
        }
    }
    /**
     * 创建文件夹
     *
     * @param strFilePath
     *            文件夹路径
     */
    public boolean mkdirFolder(String strFilePath) {
        boolean bFlag = false;
        try {
            File file = new File(strFilePath.toString());
            if (!file.exists()) {
                bFlag = file.mkdir();
            }
        } catch (Exception e) {
            logger.error("新建目录操作出错" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return bFlag;
    }
    public boolean createFile(String strFilePath, String strFileContent) {
        boolean bFlag = false;
        try {
            File file = new File(strFilePath.toString());
            if (!file.exists()) {
                bFlag = file.createNewFile();
            }
            if (bFlag == Boolean.TRUE) {
                FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(strFileContent.toString());
                pw.close();
            }
        } catch (Exception e) {
            logger.error("新建文件操作出错" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return bFlag;
    }

    /**
     * 删除一个文件或者目录
     *
     * @param targetPath 文件或者目录路径
     * @IOException 当操作发生异常时抛出
     */
    public static boolean removeFile(String targetPath) throws IOException {
        boolean result = false;
        File targetFile = new File(targetPath);
        if (targetFile.isDirectory()) {
            FileUtils.deleteDirectory(targetFile);
        } else if (targetFile.isFile()) {
            targetFile.delete();
        }
        return true;
    }
    ///**
    // * 删除文件
    // *
    // * @param strFilePath
    // * @return
    // */
    //public static boolean removeFile(String strFilePath) {
    //    boolean result = false;
    //    if (strFilePath == null || "".equals(strFilePath)) {
    //        return result;
    //    }
    //    File file = new File(strFilePath);
    //    if (file.isFile() && file.exists()) {
    //        result = file.delete();
    //        if (result == Boolean.TRUE) {
    //            logger.debug("[REMOE_FILE:" + strFilePath + "删除成功!]");
    //        } else {
    //            logger.debug("[REMOE_FILE:" + strFilePath + "删除失败]");
    //        }
    //    }
    //    return result;
    //}

    /**
     * 删除文件夹(包括文件夹中的文件内容，文件夹)
     *
     * @param strFolderPath
     * @return
     */
    public static boolean removeFolder(String strFolderPath) {
        boolean bFlag = false;
        try {
            if (strFolderPath == null || "".equals(strFolderPath)) {
                return bFlag;
            }
            File file = new File(strFolderPath.toString());
            bFlag = file.delete();
            if (bFlag == Boolean.TRUE) {
                logger.debug("[REMOE_FOLDER:" + file.getPath() + "删除成功!]");
            } else {
                logger.debug("[REMOE_FOLDER:" + file.getPath() + "删除失败]");
            }
        } catch (Exception e) {
            logger.error("FLOADER_PATH:" + strFolderPath + "删除文件夹失败!");
            e.printStackTrace();
        }
        return bFlag;
    }
    /**
     * 移除所有文件
     *
     * @param strPath
     */
    public static void removeAllFile(String strPath) {
        File file = new File(strPath);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] fileList = file.list();
        File tempFile = null;
        for (int i = 0; i < fileList.length; i++) {
            if (strPath.endsWith(File.separator)) {
                tempFile = new File(strPath + fileList[i]);
            } else {
                tempFile = new File(strPath + File.separator + fileList[i]);
            }
            if (tempFile.isFile()) {
                tempFile.delete();
            }
            if (tempFile.isDirectory()) {
                removeAllFile(strPath + "/" + fileList[i]);// 下删除文件夹里面的文件
                removeFolder(strPath + "/" + fileList[i]);// 删除文件夹
            }
        }
    }

    /**
     * 复制文件或者目录,复制前后文件完全一样。
     *
     * @param resFilePath 源文件路径
     * @param distFolder    目标文件夹
     * @IOException 当操作发生异常时抛出
     */
    public static void copyFile(String resFilePath, String distFolder) throws IOException {
        File resFile = new File(resFilePath);
        File distFile = new File(distFolder);
        if (resFile.isDirectory()) {
            FileUtils.copyDirectoryToDirectory(resFile, distFile);
        } else if (resFile.isFile()) {
            FileUtils.copyFileToDirectory(resFile, distFile, true);
        }
    }
    //public static void copyFile(String oldPath, String newPath) {
    //    try {
    //        int bytesum = 0;
    //        int byteread = 0;
    //        File oldfile = new File(oldPath);
    //        if (oldfile.exists()) { // 文件存在时
    //            InputStream inStream = new FileInputStream(oldPath); // 读入原文件
    //            FileOutputStream fs = new FileOutputStream(newPath);
    //            byte[] buffer = new byte[1444];
    //            while ((byteread = inStream.read(buffer)) != -1) {
    //                bytesum += byteread; // 字节数 文件大小
    //                System.out.println(bytesum);
    //                fs.write(buffer, 0, byteread);
    //            }
    //            inStream.close();
    //            logger.debug("[COPY_FILE:" + oldfile.getPath() + "复制文件成功!]");
    //        }
    //    } catch (Exception e) {
    //        System.out.println("复制单个文件操作出错 ");
    //        e.printStackTrace();
    //    }
    //}

    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/ " + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    logger.debug("[COPY_FILE:" + temp.getPath() + "复制文件成功!]");
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/ " + file[i], newPath + "/ "
                            + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错 ");
            e.printStackTrace();
        }
    }

    /**
     * 移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。
     *
     * @param resFilePath 源文件路径
     * @param distFolder    目标文件夹
     * @IOException 当操作发生异常时抛出
     */
    public static void moveFile(String resFilePath, String distFolder) throws IOException {
        File resFile = new File(resFilePath);
        File distFile = new File(distFolder);
        if (resFile.isDirectory()) {
            FileUtils.moveDirectoryToDirectory(resFile, distFile, true);
        } else if (resFile.isFile()) {
            FileUtils.moveFileToDirectory(resFile, distFile, true);
        }
    }
    //public static void moveFile(String oldPath, String newPath) {
    //    copyFile(oldPath, newPath);
    //    removeFile(oldPath);
    //}

    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        //removeFolder(oldPath);
    }

    /**
     * 读文件
     *
     * @param path
     *        文件名
     * @return 文件内容
     * @throws IOException
     */
    public static byte[] readFile(String path) throws IOException
    {
        return readFile(path, 0);
    }

    /**
     * 读文件
     *
     * @param path
     *        文件名
     * @param offset
     *        偏移位置
     * @return 从偏移位置开始读取的文件内容
     * @throws IOException
     */
    public static byte[] readFile(String path, long offset)
            throws IOException
    {
        return readFile(path, offset, -1);
    }

    /**
     * 读文件
     *
     * @param path
     *        文件名
     * @param offset
     *        偏移位置
     * @param size
     *        读取大小
     * @return 从偏移位置开始读取size大小的文件内容
     * @throws IOException
     */
    public static byte[] readFile(String path, long offset, int size)
            throws IOException
    {
        InputStream is = null;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is = new FileInputStream(path);
            is.skip(offset);
            byte[] buff = new byte[4096];
            int bufLength = -1;
            while ((bufLength = is.read(buff)) >= 0)
            {
                if (size > 0 && bufLength > size - baos.size())
                {
                    baos.write(buff, 0, size - baos.size());
                    break;
                } else
                {
                    baos.write(buff, 0, bufLength);
                }
            }
            return baos.toByteArray();
        } finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    public static String[] readFileByLinesUTF(String path) throws IOException {
        byte[] filebyte=readFile(path);
        String filestr=new String(filebyte,"UTF-8");
        String[] rev=filestr.split("\n");
        return rev;
    }

    public static String[] readFileByLinesGBK(String path) throws IOException {
        byte[] filebyte=readFile(path);
        String filestr=new String(filebyte,"GBK");
        String[] rev=filestr.split("\n");
        return rev;
    }


    /**
     * 读取数据
     *
     * @param inSream
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String readFileToString(InputStream inSream, String charsetName) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while( (len = inSream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }

    /**
     * 一行一行读取文件，适合字符读取，若读取中文字符时会出现乱码
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static Set<String> readFileByChar(String path) throws Exception{
        Set<String> datas=new HashSet<String>();
        FileReader fr=new FileReader(path);
        BufferedReader br=new BufferedReader(fr);
        String line=null;
        while ((line=br.readLine())!=null) {
            datas.add(line);
        }
        br.close();
        fr.close();
        return datas;
    }



    /**
     * 写文件
     *
     * @param path
     *        文件名
     * @param content
     *        文件内容
     * @return 服务端保存文件的实际绝对路径
     * @throws IOException
     */
    public static String writeFile(String path, String content)
            throws IOException
    {
        byte[] matter=content.getBytes("GBK");
        return writeFile(path, matter, false);
    }
    /**
     * 写文件
     *
     * @param path
     *        文件名
     * @param content
     *        文件内容
     * @param append
     *        追加方式
     * @return 服务端保存文件的实际绝对路径
     * @throws IOException
     */
    public static String writeFile(String path, String content,boolean append)throws IOException
    {
        byte[] matter=content.getBytes("GBK");
        return writeFile(path, matter, append);
    }

    public static String writeToFile(String savePath, String content)
    {
        File f = new File(savePath);
        if (f.exists())
            f.delete();
        try
        {
            PrintStream ps = new PrintStream(savePath);
            String tmp[] = content.split("\n");
            for (int i = 0; i < tmp.length; i++)
                ps.println(tmp[i]);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 写文件
     *
     * @param path
     *        文件名
     * @param content
     *        文件内容
     * @return 服务端保存文件的实际绝对路径
     * @throws IOException
     */
    public static String writeFile(String path, byte[] content)
            throws IOException
    {
        return writeFile(path, content, false);
    }

    /**
     * 写文件
     *
     * @param path
     *        文件名
     * @param content
     *        文件内容
     * @param append
     *        追加方式
     * @return 服务端保存文件的实际绝对路径
     * @throws IOException
     */
    public static String writeFile(String path, byte[] content,
                                   boolean append) throws IOException
    {
        if (path == null || path.length() == 0)
        {
            path = File.createTempFile("writeServerFile", ".file")
                    .getAbsolutePath();
        } else
        {
            path = new File(path).getAbsolutePath();
        }
        OutputStream os = null;
        try
        {
            os = new BufferedOutputStream(new FileOutputStream(path, append));
            os.write(content);
            os.flush();
        } finally
        {
            if (os != null)
            {
                os.close();
            }
        }
        return path;
    }


    /**
     * 写入txt文件，可以在原文件内容的基础上追加内容(并判断目录是否存在，不存在则生成目录)
     *
     * @param value
     *            写入文件内容
     * @param fileCatage
     *            文件父目录；
     * @param fileName
     *            文件名字；
     * @param code
     *            文件的编码；
     * @throws IOException
     */
    public void writeFile2(String value, String fileCatage, String fileName,
                          String code) {
        File file = null;
        try {
            file = new File(fileCatage);
            if (!file.isDirectory())
                file.mkdir();
            else {
                file = new File(fileCatage + fileName);
                if (!file.exists())
                    file.createNewFile();
                FileOutputStream out = new FileOutputStream(file, true);
                out.write(value.getBytes(code));
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 覆盖原来的内容；
     *
     * @param filePath
     *            文件的路径
     * @param content
     *            保存的内容；
     * @return
     */
    public boolean saveFile(String filePath, String content) {
        boolean successful = true;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(new File(filePath), false);
            fout.write(content.getBytes());
        } catch (FileNotFoundException e1) {
            successful = false;
        } catch (IOException e) {
            successful = false;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
        return successful;
    }


    /**
     * 按字节【读】取文件的内容；
     *
     * @param Offset
     *            读取内容的开始出
     * @param length
     *            内容的长度；
     * @param filePath
     *            文件的路径；
     * @param code
     *            编码；
     * @return 返回相应的内容；
     * @throws Exception
     */
    public String readFileByByte(int Offset, int length, String filePath,
                                 String code) {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fis.skip(Offset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = new byte[length];
        try {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new String(bytes, code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将流中的文本读入一个 BufferedReader 中
     *
     * @param filePath
     *            文件路径
     * @param code
     *            编码格式
     * @return
     * @throws IOException
     */

    public BufferedReader readToBufferedReader(String filePath, String code)
            throws IOException {
        BufferedReader bufferedReader = null;
        File file = new File(filePath);
        if (file.isFile() && file.exists()) { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), code);// 考虑到编码格式
            bufferedReader = new BufferedReader(read);
        }
        return bufferedReader;
    }

    /**
     * 将流中的文本读入一个 StringBuffer 中
     *
     * @param filePath
     *            文件路径
     * @throws IOException
     */
    public StringBuffer readToBuffer(String filePath, String code) {
        StringBuffer buffer = new StringBuffer();
        InputStream is;
        try {
            File file = new File(filePath);
            if (!file.exists())
                return null;
            is = new FileInputStream(filePath);
            String line; // 用来保存每行读取的内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), code));
            line = reader.readLine(); // 读取第一行
            while (line != null) { // 如果 line 为空说明读完了
                buffer.append(line); // 将读到的内容添加到 buffer 中
                // buffer.append("\n"); // 添加换行符
                line = reader.readLine(); // 读取下一行
            }
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public String loadFile(String filePath, String charset) {
        FileInputStream fin = null;
        StringBuffer sb = new StringBuffer();
        try {
            fin = new FileInputStream(new File(filePath));
            byte[] buffer = new byte[Integer.MAX_VALUE];
            int start = -1;
            while ((start = fin.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, start, charset));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取某个目录下所有文件或者获取某个文件的大小； 单位：MB
     *
     * @param file
     * @return
     */
    public static double getDirSize(File file) {
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {// 如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            System.out.println("获取文件大小错误！！文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    /**
     * 获取某个目录下所有的文件的全路径和文件名的集合；
     *
     * @return
     */
    public List<List<String>> getAllFile(String mulu) {
        File file = new File(mulu);
        File[] files = file.listFiles();
        List<List<String>> ret = new ArrayList<List<String>>();
        List<String> allFilePath = new ArrayList<String>();
        List<String> allFileName = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                allFilePath.add(files[i].toString());
                allFileName.add(files[i].getName());
            }
        }
        ret.add(allFilePath);
        ret.add(allFileName);
        return ret;
    }

    /**
     * 获得该路径下的所有文件名的列表
     * @param Url
     * @return
     */
    public static String[] getAllFile2(String Url)
    {
        File path = new File(Url);
        String[] list;
        list=path.list();
        return list;
    }

    /**
     * 本地某个目录下的文件列表（不递归）
     *
     * @param folder ftp上的某个目录
     * @param suffix 文件的后缀名（比如.mov.xml)
     * @return 文件名称列表
     */
    public static String[] listFilebySuffix(String folder, String suffix) {
        IOFileFilter fileFilter1 = new SuffixFileFilter(suffix);
        IOFileFilter fileFilter2 = new NotFileFilter(DirectoryFileFilter.INSTANCE);
        FilenameFilter filenameFilter = new AndFileFilter(fileFilter1, fileFilter2);
        return new File(folder).list(filenameFilter);
    }

    /**
     * File exist check
     *
     * @param sFileName File Name
     * @return boolean true - exist<br>
     *                 false - not exist
     */
    public static boolean isFileExist(String sFileName) {
        boolean result = false;

        try {
            File f = new File(sFileName);

            //if (f.exists() && f.isFile() && f.canRead()) {
            if (f.exists() && f.isFile()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        /* return */
        return result;
    }

    /**
     * 判断一个文件是否存在
     *
     * @param filePath 文件路径
     * @return 存在返回true，否则返回false
     */
    public static boolean isFileOrFolderExist(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 读取文件或者目录的大小
     *
     * @param distFilePath 目标文件或者文件夹
     * @return 文件或者目录的大小，如果获取失败，则返回-1
     */
    public static long genFileSize(String distFilePath) {
        File distFile = new File(distFilePath);
        if (distFile.isFile()) {
            return distFile.length();
        } else if (distFile.isDirectory()) {
            return FileUtils.sizeOfDirectory(distFile);
        }
        return -1L;
    }
    ///**
    // * Get File Size
    // *
    // * @param sFileName File Name
    // * @return long File's size(byte) when File not exist return -1
    // */
    //public static long genFileSize(String sFileName) {
    //
    //    long lSize = 0;
    //
    //    try {
    //        File f = new File(sFileName);
    //
    //        //exist
    //        if (f.exists()) {
    //            if (f.isFile() && f.canRead()) {
    //                lSize = f.length();
    //            } else {
    //                lSize = -1;
    //            }
    //            //not exist
    //        } else {
    //            lSize = 0;
    //        }
    //    } catch (Exception e) {
    //        lSize = -1;
    //    }
    ///* return */
    //    return lSize;
    //}

    /**
     * File Unzip
     *
     * @param sToPath  Unzip Directory path
     * @param sZipFile Unzip File Name
     */
    @SuppressWarnings("rawtypes")
    public static void releaseZip(String sToPath, String sZipFile) throws Exception {

        if (null == sToPath ||("").equals(sToPath.trim())) {
            File objZipFile = new File(sZipFile);
            sToPath = objZipFile.getParent();
        }
        ZipFile zfile = new ZipFile(sZipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                continue;
            }

            OutputStream os =
                    new BufferedOutputStream(
                            new FileOutputStream(getRealFileName(sToPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * getRealFileName
     *
     * @param  baseDir   Root Directory
     * @param  absFileName  absolute Directory File Name
     * @return java.io.File     Return file
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static File getRealFileName(String baseDir, String absFileName) throws Exception {

        File ret = null;
        List dirs = new ArrayList();
        StringTokenizer st = new StringTokenizer(absFileName, System.getProperty("file.separator"));
        while (st.hasMoreTokens()) {
            dirs.add(st.nextToken());
        }
        ret = new File(baseDir);
        if (dirs.size() > 1) {
            for (int i = 0; i < dirs.size() - 1; i++) {
                ret = new File(ret, (String) dirs.get(i));
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, (String) dirs.get(dirs.size() - 1));
        return ret;
    }

    /**
     * 重命名文件或文件夹
     *
     * @param resFilePath 源文件路径
     * @param newFileName 重命名
     * @return 操作成功标识
     */
    public static boolean renameFile(String resFilePath, String newFileName) {
        String newFilePath = formatPath(getParentPath(resFilePath) + "/" + newFileName);
        File resFile = new File(resFilePath);
        File newFile = new File(newFilePath);
        return resFile.renameTo(newFile);
    }
    ///**
    // * renameFile
    // *
    // * @param  srcFile   Source File
    // * @param  targetFile   Target file
    // */
    //static public void renameFile(String srcFile , String targetFile) throws Exception
    //{
    //    try {
    //        copyFile(srcFile, targetFile);
    //        removeFile(srcFile);
    //    } catch(Exception e){
    //        throw e;
    //    }
    //}

    public static String readTextFile(String realPath) throws Exception {
        File file = new File(realPath);
        if (!file.exists()){
            System.out.println("File not exist!");
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(realPath),"UTF-8"));
        String temp = "";
        String txt="";
        while((temp = br.readLine()) != null){
            txt+=temp;
        }
        br.close();
        return txt;
    }

    /**
     * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
     *
     * @param res            原字符串
     * @param filePath 文件路径
     * @return 成功标记
     */
    public static boolean string2File(String res, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];         //字符缓冲区
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }



    /**
     * 将一个字符串的首字母改为大写或者小写
     *
     * @param srcString 源字符串
     * @param flag     大小写标识，ture小写，false大些
     * @return 改写后的新字符串
     */
    public static String toLowerCaseInitial(String srcString, boolean flag) {
        StringBuilder sb = new StringBuilder();
        if (flag) {
            sb.append(Character.toLowerCase(srcString.charAt(0)));
        } else {
            sb.append(Character.toUpperCase(srcString.charAt(0)));
        }
        sb.append(srcString.substring(1));
        return sb.toString();
    }

    /**
     * 将一个字符串按照句点（.）分隔，返回最后一段
     *
     * @param clazzName 源字符串
     * @return 句点（.）分隔后的最后一段字符串
     */
    public static String getLastName(String clazzName) {
        String[] ls = clazzName.split("\\.");
        return ls[ls.length - 1];
    }

    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号。
     *
     * @param path 文件路径
     * @return 格式化后的文件路径
     */
    public static String formatPath(String path) {
        String reg0 = "\\\\＋";
        String reg = "\\\\＋|/＋";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        if (System.getProperty("file.separator").equals("\\")) {
            temp= temp.replace('/','\\');
        }
        return temp;
    }

    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号(适用于FTP远程文件路径或者Web资源的相对路径)。
     *
     * @param path 文件路径
     * @return 格式化后的文件路径
     */
    public static String formatPath4Ftp(String path) {
        String reg0 = "\\\\＋";
        String reg = "\\\\＋|/＋";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }


    /**
     * 获取文件父路径
     *
     * @param path 文件路径
     * @return 文件父路径
     */
    public static String getParentPath(String path) {
        return new File(path).getParent();
    }

    /**
     * 获取相对路径
     *
     * @param fullPath 全路径
     * @param rootPath 根路径
     * @return 相对根路径的相对路径
     */
    public static String getRelativeRootPath(String fullPath, String rootPath) {
        String relativeRootPath = null;
        String _fullPath = formatPath(fullPath);
        String _rootPath = formatPath(rootPath);

        if (_fullPath.startsWith(_rootPath)) {
            relativeRootPath = fullPath.substring(_rootPath.length());
        } else {
            throw new RuntimeException("要处理的两个字符串没有包含关系，处理失败！");
        }
        if (relativeRootPath == null) return null;
        else
            return formatPath(relativeRootPath);
    }

    /**
     * 获取当前系统换行符
     *
     * @return 系统换行符
     */
    public static String getSystemLineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 将用“|”分隔的字符串转换为字符串集合列表，剔除分隔后各个字符串前后的空格
     *
     * @param series 将用“|”分隔的字符串
     * @return 字符串集合列表
     */
    public static List<String> series2List(String series) {
        return series2List(series, "\\|");
    }

    /**
     * 将用正则表达式regex分隔的字符串转换为字符串集合列表，剔除分隔后各个字符串前后的空格
     *
     * @param series 用正则表达式分隔的字符串
     * @param regex 分隔串联串的正则表达式
     * @return 字符串集合列表
     */
    private static List<String> series2List(String series, String regex) {
        List<String> result = new ArrayList<String>();
        if (series != null && regex != null) {
            for (String s : series.split(regex)) {
                if (s.trim() != null && !s.trim().equals("")) result.add(s.trim());
            }
        }
        return result;
    }

    /**
     * @param strList 字符串集合列表
     * @return 通过“|”串联为一个字符串
     */
    public static String list2series(List<String> strList) {
        StringBuffer series = new StringBuffer();
        for (String s : strList) {
            series.append(s).append("|");
        }
        return series.toString();
    }

    /**
     * 将字符串的首字母转为小写
     *
     * @param resStr 源字符串
     * @return 首字母转为小写后的字符串
     */
    public static String firstToLowerCase(String resStr) {
        if (resStr == null) {
            return null;
        } else if ("".equals(resStr.trim())) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            Character c = resStr.charAt(0);
            if (Character.isLetter(c)) {
                if (Character.isUpperCase(c))
                    c = Character.toLowerCase(c);
                sb.append(resStr);
                sb.setCharAt(0, c);
                return sb.toString();
            }
        }
        return resStr;
    }

    /**
     * 将字符串的首字母转为大写
     *
     * @param resStr 源字符串
     * @return 首字母转为大写后的字符串
     */
    public static String firstToUpperCase(String resStr) {
        if (resStr == null) {
            return null;
        } else if ("".equals(resStr.trim())) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            Character c = resStr.charAt(0);
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c))
                    c = Character.toUpperCase(c);
                sb.append(resStr);
                sb.setCharAt(0, c);
                return sb.toString();
            }
        }
        return resStr;
    }

}