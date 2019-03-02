package com.github.everything.core.index.impl;

import com.github.everything.config.EveryThingPlusConfig;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.index.FileScan;

import java.io.File;
import java.util.LinkedList;

public class FileScanImpl implements FileScan {


    //DAO
    private EveryThingPlusConfig config = EveryThingPlusConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
        if(file.isFile()){
            if(config.getExcludePath().contains(file.getParentFile())){
                return;
            }
        } else {
            if (config.getExcludePath().contains(path)) {
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }

        for(FileInterceptor interceptor : this.interceptors) {
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
        //文件变成thing ->写入




}
