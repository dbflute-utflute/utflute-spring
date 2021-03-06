package org.dbflute.utflute.spring.bean;

import javax.annotation.Resource;

import org.dbflute.utflute.spring.dbflute.exbhv.FooBhv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author jflute
 * @since 0.4.0 (2014/03/16 Sunday)
 */
public class FooFacade extends FooBaseFacade {

    @Resource
    private FooBhv fooBhv; // same name as super's

    protected PlatformTransactionManager transactionManager; // no annotation, no setter

    protected FooService fooService; // annotation for protected setter

    public FooBhv myBehaviorInstance() {
        return fooBhv;
    }

    @Override
    public String toString() {
        return "facade:{" + fooBhv + ", " + transactionManager + ", " + fooService + "}@" + Integer.toHexString(hashCode());
    }

    public FooService getFooService() {
        return fooService;
    }

    @Autowired
    protected void setFooService(FooService fooService) {
        this.fooService = fooService;
    }
}
