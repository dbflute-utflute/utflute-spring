package org.dbflute.utflute.spring.bean;

import javax.annotation.Resource;

import org.dbflute.utflute.spring.dbflute.exbhv.FooBhv;

/**
 * @author jflute
 * @since 0.4.0 (2014/03/20 Thursday)
 */
public abstract class FooBaseFacade {

    @Resource
    private FooBhv fooBhv; // super's private field

    public FooBhv superBehaviorInstance() {
        return fooBhv;
    }
}
