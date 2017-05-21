package org.dbflute.utflute.spring.bean;

import javax.annotation.Resource;

import org.dbflute.utflute.spring.dbflute.exbhv.FooBhv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author jflute
 * @since 0.1.0 (2011/07/24 Sunday)
 */
public class FooLogic {

    @Resource
    private FooBhv fooBhv; // private field

    @Resource(name = "fooService")
    protected FooService fooHelper; // name wrong but component name is specified

    @Autowired
    protected FooService fooService; // at first but specify none but unsupported so injected

    // no web here
    //@Resource
    //protected HttpServletRequest request; // mocklet

    public String behaviorToString() {
        return fooBhv != null ? fooBhv.toString() : null;
    }

    @Override
    public String toString() {
        return "facade:{" + fooBhv + ", " + fooHelper + ", " + fooService + "}@" + Integer.toHexString(hashCode());
    }

    public PlatformTransactionManager getTransactionManager() {
        return fooBhv != null ? fooBhv.getTransactionManager() : null;
    }
}
