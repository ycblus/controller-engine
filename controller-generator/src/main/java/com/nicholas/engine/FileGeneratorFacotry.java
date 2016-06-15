/**   
 * @Title: FileGeneratorFacotry.java 
 * @Package zttc.itat.user.modul
 * @Description: 负责文件读取及生成
 * @author yangchao 
 * @date 2016-6-9 下午11:46:22 
 * @version V1.0   
 */
package com.nicholas.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

/**
 * @ClassName: FileGeneratorFacotry
 * @Description: 生成文件
 * @author yangchao
 * 
 */
public class FileGeneratorFacotry {
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * @Title: getTemplateContents 
     * @Description: 读取模板内容
     * @param templateName
     * @return
     * @throws IOException
     * @return StringBuffer
     */
    public StringBuffer getTemplateContents(String templateName) throws IOException{
        StringBuffer sb = new StringBuffer();

        InputStream inputStream = FileGeneratorFacotry.class.getClassLoader().getResourceAsStream(templateName);//Resources.getResourceAsStream("template/"+templateName);
        InputStreamReader isr = new InputStreamReader(inputStream,"UTF-8");
        BufferedReader reader = new BufferedReader(isr);
        while (reader.read() != -1) {
            sb = sb.append(reader.readLine().replaceAll("\n", "") + "\n");
        }
        
        return sb;
    }
    
    /**
     * @Title: WriteToFile
     * @Description: 写文件到指定包路径下
     * @param filePath
     *            要创建的文件路径
     * @param content
     *            文件内容
     * @throws Exception
     * @return void
     */
    public void WriteToFile(String filePath, StringBuffer content,String charSet) throws Exception {
        try {
            if (!new File(filePath).exists()) {
                if (!new File(filePath).getParentFile().exists()) {
                    if (new File(filePath).getParentFile().mkdirs()) {
                        new File(filePath).createNewFile();
                    }
                } else {
                    new File(filePath).createNewFile();
                }
            } else {
                new File(filePath).delete();
            }

            FileOutputStream out = new FileOutputStream(filePath, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, charSet);
            BufferedWriter writer = new BufferedWriter(osw);

            writer.write(new String(content), 0, content.length());

            writer.flush();
            out.close();
            osw.close();
            writer.close();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    
    /**
     * 
     * @Title: firstToLower 
     * @Description: 首字母小写
     * @param str
     * @return
     * @throws Exception
     * @return String
     */
    public String firstToLower(String str) throws Exception{
        return str.substring(0, 1).toLowerCase()+ str.substring(1, str.length());
    }
    
    /**
     * 
     * @Title: firstToUpper 
     * @Description: 首字母大写
     * @param str
     * @return
     * @throws Exception
     * @return String
     */
    public String firstToUpper(String str) throws Exception{
        return str.substring(0, 1).toUpperCase()+ str.substring(1, str.length());
    }
}
