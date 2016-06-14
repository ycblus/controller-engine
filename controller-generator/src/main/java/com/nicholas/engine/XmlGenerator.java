package com.nicholas.engine;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

/**
 * @ClassName: XmlGeneratorUtil
 * @Description: 生成tablemodel文件
 * @author yangchao
 * 
 */
public class XmlGenerator extends FileGeneratorFacotry{
    public void generatorTableModel(List<String> tableNames,String basePath,String targetPath) throws Exception{
        Map<String,List<Map<String, String>>> tableMap = new DatabaseUtil().queryTableColumns(tableNames);
        List<Map<String, String>> columnList = null;
        
        StringBuffer sb = this.getTemplateContents("tableModel.vm");
        
        for(String table_name:tableMap.keySet()){
            String contentStr = sb.toString();
            columnList = tableMap.get(table_name);
            Map<String, String> mapHead = columnList.get(0);
            String entity = MapUtils.getString(mapHead, "entity");
            contentStr = contentStr.replace("$"+"{entity}", entity)
            .replace("${tableName}", MapUtils.getString(mapHead, "tableName"))
            .replace("${cn_name}", MapUtils.getString(mapHead, "cn_name"))
            .replace("${pk_column}", MapUtils.getString(mapHead, "pk_column"))
            .replace("${pkName}", MapUtils.getString(mapHead, "pkName"))
            .replace("${basePath}", basePath)
            .replace("${targetPath}", targetPath);
            
            String fieldStr = "";
            int count = 1;
            for(Map<String, String> mapField : columnList){
                String lineStr = "\n"+"       ";
                fieldStr += "<Field id=\"${column_id}\" columnName=\"${columnName}\" cnname=\"${cnname}\" length=\"${dataLength}\" dataType=\"${dataType}\" canBeNull=\"${nullable}\"/>";
                
                if(count!=columnList.size()){
                    fieldStr+=lineStr;
                }
                
                fieldStr = fieldStr.replace("${column_id}", MapUtils.getString(mapField, "columnId"))
                .replace("${columnName}", MapUtils.getString(mapField, "columnName"))
                .replace("${cnname}", MapUtils.getString(mapField, "cnname"))
                .replace("${dataType}", MapUtils.getString(mapField, "dataType"))
                .replace("${dataLength}", MapUtils.getString(mapField, "dataLength"))
                .replace("${nullable}", MapUtils.getString(mapField, "nullable"));
                
                count++;
            }
            contentStr = contentStr.replace("${field}", fieldStr);
            
            String filePath = "src\\main\\resources\\tableModel\\" + MapUtils.getString(mapHead, "entity") + ".xml";

            logger.info("生成tableModel文件:" + filePath);

            this.WriteToFile(filePath, new StringBuffer(contentStr),"UTF-8");
        }
    }
}
