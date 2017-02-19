package com.pingcap.tools.cdb.binlog.instance.spring;

import com.pingcap.tools.cdb.binlog.instance.core.CDBInstance;
import com.pingcap.tools.cdb.binlog.instance.core.CDBInstanceGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Created by iamxy on 2017/2/17.
 */
public class SpringCDBInstanceGenerator implements CDBInstanceGenerator, BeanFactoryAware {

    private String defaultName = "instance";
    private BeanFactory beanFactory;

    @Override
    public CDBInstance generate(String destination) {
        String beanName = destination;
        if (!beanFactory.containsBean(beanName)) {
            beanName = defaultName;
        }

        return (CDBInstance) beanFactory.getBean(beanName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
