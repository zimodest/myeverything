package com.github.everything.core;

import com.github.everything.config.EveryThingPlusConfig;
import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.dao.imp.FileIndexDaoImpl;
import com.github.everything.core.index.impl.FileScanImpl;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.interceptor.impl.FilePrintInterceptor;
import com.github.everything.core.interceptor.impl.ThingClearInterceptor;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;
import com.github.everything.core.search.FileSearch;
import com.github.everything.core.index.FileScan;
import com.github.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EverythingPlusManager {

    private FileScan fileScan;
    private FileSearch fileSearch;
    private static volatile EverythingPlusManager manager;

    private ThingClearInterceptor thingClearInterceptor;

    /**
     * 清理删除的文件
     */
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    //线程执行器
    private ExecutorService executorService;

    public EverythingPlusManager() {
        this.initComponent();
    }

    private void initComponent(){
        DataSource dataSource = DataSourceFactory.dataSource();
        //检查数据库
        checkDatabase();
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileScan = new FileScanImpl();
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        //发布代码时不需要
//        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing_clear");
        this.backgroundClearThread.setDaemon(true);
    }

    private void checkDatabase() {
        DataSourceFactory.initDataBase();
    }


    public static EverythingPlusManager getInstance(){
        if(manager == null) {
            synchronized (EverythingPlusManager.class) {
                if(manager == null) {
                    manager = new EverythingPlusManager();
                }
            }
        }
        return manager;
    }


    /**
     * 检索
     * @param condition
     * @return
     */
    public List<Thing> search(Condition condition) {
        //Stream 流式处理 JDK8
        //stream 流式处理 JDK8
        return this.fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    if (!flag) {
                        //做删除
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;

                }).collect(Collectors.toList());
    }


    public void buildIndex(){
        Set<String> directories = EveryThingPlusConfig.getInstance().getIncludePath();

        if(this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-"+threadId.getAndIncrement());
                    return thread;
                }
            });
        }
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        System.out.println("Build index start ...");
        for(String path : directories) {
            this.executorService.submit(() -> {
                EverythingPlusManager.this.fileScan.index(path);
                //当前任务完成,值减1
                countDownLatch.countDown();
            });
        }
        /**
         * 阻塞，直到任务完成，值为0
         */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete ..");

    }

    public void startBackgroundClearThread(){
        if(this.backgroundClearThreadStatus.compareAndSet(false, true)){
            this.backgroundClearThread.start();
        }else {
            System.out.println("不能重复启动");
        }
    }
}
