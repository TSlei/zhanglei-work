package com.lagou.edu.service.impl;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.factory.BeanFactory;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.utils.ConnectionUtils;
import com.lagou.edu.utils.TransactionManager;

/**
 * @author 应癫
 */
@Service("transferService")
public class TransferServiceImpl implements TransferService {

    @Autowired
    private AccountDao accountDao;

    @Override
    @Transactional()
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
        try{
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
//            int c = 1/0;
            accountDao.updateAccountByCardNo(from);

        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
