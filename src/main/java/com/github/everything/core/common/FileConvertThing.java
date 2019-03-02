package com.github.everything.core.common;

import com.github.everything.core.model.FileType;
import com.github.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类：将File对象转换成Thing对象
 *
 * 不需要类继承或创建对象
 */
public final class FileConvertThing {
    private FileConvertThing(){}

    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(computerFileType(file));
        return thing;
    }


    private static int computerFileDepth(File file){

        int dept = 0;
        String[] segments =  file.getAbsolutePath().split("\\\\");
        return segments.length;


    }

    private static FileType computerFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }

        //文件路径中最后一个 . 出现的索引就是后缀名
        int index = file.getName().lastIndexOf(".");
        String fileName = file.getName();

        //文件名 abc. 会出现问题

        if(index != -1 && index < fileName.length()-1){
            //获取文件扩展名
            String extend = fileName.substring(index+1);
            //找到扩展名对应的文件类型
            return FileType.lookup(extend);
        }else{
            return FileType.OTHER;
        }

    }

}
