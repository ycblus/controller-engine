package com.nicholas.engine;

import java.util.ArrayList;
import java.util.List;

public class TemplateEngine {
    /**
     * 
     * @Title: Generated 
     * @Description: TODO
     * @param tableNames
     * @param basePath 基准包如：com.yc
     * @param targetPath 生成文件的包如:com.yc.controller
     * @throws Exception
     * @return void
     */
    public void Generate(List<String> tableNames,String basePath,String targetPath) throws Exception{
        AutoGenerateFile autoGenerateFile = new AutoGenerateFile();
        //生成tableModel文件
        autoGenerateFile.generateXml(tableNames, basePath, targetPath);
        //根据tableModel文件生成Controller类
        autoGenerateFile.generateController(tableNames);
    }
    
    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(0,"T_USER");
        list.add(1,"Country");
        list.add(2,"t_leave_apply");
        new TemplateEngine().Generate(list, "zttc.itat.user", "zttc.itat.user.controller");
    }
}
