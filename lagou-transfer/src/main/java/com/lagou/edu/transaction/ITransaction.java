package com.lagou.edu.transaction;

/**
 * 事务接口，支持子类实现不同的事务
 */
public interface ITransaction {

    public void beginTransaction() throws Exception;

    // 提交事务
    public void commit() throws Exception;

    // 回滚事务
    public void rollback() throws Exception;
}
