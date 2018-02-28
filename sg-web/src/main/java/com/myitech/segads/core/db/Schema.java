package com.myitech.segads.core.db;

import com.myitech.segads.exceptions.DatabaseInstallFailedException;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/08
 */
public interface Schema {
    void initDatabase() throws DatabaseInstallFailedException;
}
