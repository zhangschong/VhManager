package com.leon.tools.filemanager;

import com.leon.tools.obj.ILifeObj;

/**
 * 文件管理者,用于缓存文件管理
 */
public interface IFileManager extends ILifeObj {

    void register(IFileOperator file);

    void unRegister(IFileOperator file);

    boolean allFileStart();

    boolean fileStart(String key);

    /**
     * @author twisted.ye 文件执行者
     */
    static interface IFileOperator {

        String getUniqueKey();

        boolean operator();

    }

}
