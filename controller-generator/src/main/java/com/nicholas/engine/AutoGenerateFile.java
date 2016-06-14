
package com.nicholas.engine;

import java.util.List;
import java.util.Map;

public class AutoGenerateFile {

    public void generateController(List<String> tableNames) throws Exception{        
        List<Map<Object, Object>> moduleList = XmlParseUtil.parseXml(tableNames);
        ControllerGenerator controllerGenerator = new ControllerGenerator();
        for (Map<Object, Object> map : moduleList) {
            controllerGenerator.buildFile(map);
        }
    }
    
    public void generateXml(List<String> tableNames,String basePath,String targetPath) throws Exception{
        XmlGenerator xmlGenerator = new XmlGenerator();
        xmlGenerator.generatorTableModel(tableNames, basePath, targetPath);
    }

}
