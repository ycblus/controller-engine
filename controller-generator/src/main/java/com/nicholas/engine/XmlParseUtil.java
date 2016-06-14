package com.nicholas.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlParseUtil {

    public static List<Map<Object,Object>> parseXml(List<String> tables) throws Exception {
        SAXReader reader = new SAXReader();
        List<Map<Object, Object>> moduleList = new ArrayList<Map<Object, Object>>();
        Map<Object, Object> map = null;
        Map<Object, Object> columnMap = null;
        for(String tableName : tables){
            tableName = tableNameToEntity(tableName);
            File configFile = Resources.getResourceAsFile("tableModel/"+tableName+".xml");
            Document document = reader.read(configFile);
            Element rootElt = document.getRootElement(); //
            @SuppressWarnings("rawtypes")
            Iterator iters = rootElt.elementIterator("Module"); //
            while (iters.hasNext()) {
                List<Map<Object, Object>> columnList = new ArrayList<Map<Object, Object>>();
                Element recordEle = (Element) iters.next();
                map = new HashMap<Object, Object>();
                map.put("pk_name", recordEle.attributeValue("pk_name"));
                map.put("entity", recordEle.attributeValue("entity"));
                map.put("basePath", recordEle.attributeValue("basePath"));
                map.put("targetPath", recordEle.attributeValue("targetPath"));
                map.put("cn_name", recordEle.attributeValue("cn_name"));
                
                Iterator<?> iterFields = recordEle.elementIterator("Field");
                while (iterFields.hasNext()) {
                    columnMap = new HashMap<Object, Object>();
                    Element recordEleField = (Element) iterFields.next();
                    String column = recordEleField.attributeValue("columnName");
                    String cnname = recordEleField.attributeValue("cnname");
                    String dataType = recordEleField.attributeValue("dataType");
                    columnMap.put("column_name", column);
                    columnMap.put("cnname", cnname);
                    columnMap.put("dataType", dataType);
                    columnList.add(columnMap);
                }
                map.put(recordEle.attributeValue("entity")+"_list", columnList);
            }
            moduleList.add(map);
        }
        
        return moduleList;
    }
    
    public static String tableNameToEntity(String table_name) throws Exception{
        String[] tableStrs = table_name.toLowerCase().split("_");
        StringBuffer bf = new StringBuffer();
        for(String str:tableStrs){
            str = firstToUpper(str);
            bf.append(str);
        }
        return bf.toString();
    }
    
    public static String firstToUpper(String str) throws Exception{
        return str.substring(0, 1).toUpperCase()+ str.substring(1, str.length());
    }
}
