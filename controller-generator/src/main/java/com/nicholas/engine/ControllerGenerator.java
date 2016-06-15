/**   
* @Title: ControllerGenerator.java 
* @Package zttc.itat.user.modul
* @Description: TODO(用一句话描述该文件做什么) 
* @author yangchao 
* @date 2016-6-10 上午1:27:06 
* @version V1.0   
*/
package com.nicholas.engine;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class ControllerGenerator extends FileGeneratorFacotry{
    public void buildFile(Map<Object, Object> map) throws Exception {
        StringBuffer sb = this.getTemplateContents("controller.vm");

        String entityName = MapUtils.getString(map, "entity");
        String entityNameToLower = this.firstToLower(entityName);
        String pk_name = MapUtils.getString(map, "pk_name");
        String basePath = (String) map.get("basePath");
        String targetPath = ((String) map.get("targetPath")).replace(".", "\\");

        StringBuffer fieldDefAddStr = new StringBuffer();
        StringBuffer fieldSetAddStr = new StringBuffer();
        StringBuffer fieldDefUpdateStr = new StringBuffer();
        StringBuffer fieldSetUpdateStr = new StringBuffer();
        @SuppressWarnings({ "rawtypes" })
        List fieldList = (List) map.get(entityName + "_list");

        for (Object obj : fieldList) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Map<String, String> fieldMap = (Map) obj;
            String fieldName = MapUtils.getString(fieldMap, "column_name");
            String dataType = MapUtils.getString(fieldMap, "dataType");
            if(!fieldName.equals(pk_name)){
                if("NUMBER".equals(dataType)){
                    fieldDefUpdateStr.append("BigDecimal "+fieldName+" = new BigDecimal(request.getParameter(\""+fieldName+"\"));"+"\n"+"            ");
                }else{
                    fieldDefUpdateStr.append("String "+fieldName+" = request.getParameter(\""+fieldName+"\");"+"\n"+"            ");
                }
                fieldSetUpdateStr.append(entityNameToLower+".set"+firstToUpper(fieldName)+"("+fieldName+");"+"\n"+"            ");
            } 
            
            if("NUMBER".equals(dataType)){
                fieldDefAddStr.append("BigDecimal "+fieldName+" = new BigDecimal(request.getParameter(\""+fieldName+"\"));"+"\n"+"            ");
            }else{
                fieldDefAddStr.append("String "+fieldName+" = request.getParameter(\""+fieldName+"\");"+"\n"+"            ");
            }
            fieldSetAddStr.append(entityNameToLower+".set"+firstToUpper(fieldName)+"("+fieldName+");"+"\n"+"            ");
        }

        // 替换模板变量
        String content = sb.toString()
                .replace("${Entity}", entityName)
                .replace("${entity}", entityNameToLower)
                .replace("${pk_name}", pk_name)
                .replace("${basePath}", basePath)
                .replace("${fieldAddDef}", fieldDefAddStr)
                .replace("${fieldAddSet}", fieldSetAddStr)
                .replace("${fieldUpdateDef}", fieldDefUpdateStr)
                .replace("${fieldUpdateSet}", fieldSetUpdateStr);
        
        URL base = ControllerGenerator.class.getResource("");
        String classpath = base.getPath();
        String projectPath = classpath.substring(0, classpath.indexOf("target"));
        String fileName = entityName + "Controller.java";
        String javaFilePath = projectPath + "src\\main\\java\\" + targetPath + "\\" + fileName;
    
        logger.info("生成文件:" + javaFilePath);
        WriteToFile(javaFilePath,new StringBuffer(content),"UTF-8");
    }
}
