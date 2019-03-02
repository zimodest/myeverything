package com.github.everything.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
public class EveryThingPlusConfig {

    private static volatile EveryThingPlusConfig config;

    private EveryThingPlusConfig(){
    }

    /**
     * 建立索引文件的路径,路径不能重复
     */
    private Set<String> includePath = new HashSet<>();

    /**
     * 排除索引文件的路径
     */

    /**
     * 检索最大的返回值数量
     */
    @Setter
    private Integer maxReturn = 30;

    /**
     * 深度排序的规则，默认是升序
     * order by dept asc limit 30 offset 0
     */
    @Setter
    private Boolean deptOrderAsc = true;


    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "myeverything";
    private Set<String> excludePath = new HashSet<>();

    private void initDefaultPathsConfig(){
        FileSystem fileSystem = FileSystems.getDefault();

        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.getIncludePath().add(path.toString()));

        //排除的目录
        //windows: C:\Program File()
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Windows")) {
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files (X86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");
        }else {
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }

    public static EveryThingPlusConfig getInstance(){
        if(config == null){
            synchronized (EveryThingPlusConfig.class){
                if(config == null){
                    config = new EveryThingPlusConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }

}
