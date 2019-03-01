package com.github.everything.config;

import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class EveryThingPlusConfig {

    private static volatile EveryThingPlusConfig config;

    private EveryThingPlusConfig(){}

    /**
     * 建立索引文件的路径,路径不能重复
     */
    private Set<String> includePath = new HashSet<>();

    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();

    public static EveryThingPlusConfig getInstance(){
        if(config == null){
            synchronized (EveryThingPlusConfig.class){

                if(config == null){
                    config = new EveryThingPlusConfig();
                    //排除的目录

                    //获取文件系统
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

            }
        }

        return config;

    }

    public static void main(String[] args) {
        EveryThingPlusConfig config = EveryThingPlusConfig.getInstance();
        System.out.println(config.getExcludePath());
        System.out.println(config.getIncludePath());
    }

}
