package com.github.everything.index;

import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.imp.FileIndexDaoImpl;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.interceptor.impl.FilePrintInterceptor;
import com.github.everything.index.impl.FileScanImpl;

public interface FileScan {
//    void index(Thing thing);

    /**
     * 遍历Path
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);


    public static void main(String[] args) {
        FileScanImpl fileScan = new FileScanImpl();

        FileInterceptor fileInterceptor = new FilePrintInterceptor()
                ;
        fileScan.interceptor(fileInterceptor);
        FileInterceptor fileInterceptor1 = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()
        ));

        fileScan.interceptor(fileInterceptor1);
        fileScan.index("E:\\BIt\\MySQL数据库课件");
    }
}
