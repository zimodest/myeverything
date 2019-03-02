package com.github.everything.core.index;

import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.imp.FileIndexDaoImpl;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.interceptor.impl.FilePrintInterceptor;
import com.github.everything.core.index.impl.FileScanImpl;

public interface FileScan {
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
}
