/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.utflute.spring.web.bean;

import javax.annotation.Resource;

import org.dbflute.utflute.spring.web.WebContainerTestCase;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author jflute
 * @since 0.4.0 (2014/03/16 Sunday)
 */
public class BarActionTest extends WebContainerTestCase {

    protected static BarAction cachedInjectingBarAction;
    protected static BarLogic cachedNestedBarLogic;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected BarLogic barLogic;

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    @Override
    public void setUp() throws Exception {
        assertNull(barLogic);
        assertNull(cachedInjectingBarAction);
        assertNull(cachedNestedBarLogic);
        assertNull(RequestContextHolder.getRequestAttributes());
        super.setUp();
        assertNotNull(barLogic);
    }

    @Override
    public void tearDown() throws Exception {
        assertNotNull(barLogic);
        assertNotNull(cachedInjectingBarAction.barLogic);
        assertNotNull(cachedNestedBarLogic.request);
        BarLogic managedLogic = barLogic; // keep to check
        super.tearDown();
        assertNull(RequestContextHolder.getRequestAttributes());
        assertNull(cachedInjectingBarAction.barLogic);
        assertNull(cachedNestedBarLogic.request); // reverted from inject() ending
        assertNull(cachedInjectingBarAction.barLogic);
        assertNull(barLogic);
        assertNull(managedLogic.request); // reverted from inject() ending
        cachedInjectingBarAction = null;
        cachedNestedBarLogic = null;
    }

    // ===================================================================================
    //                                                                      Inject Request
    //                                                                      ==============
    public void test_inject_request() throws Exception {
        // ## Arrange ##
        assertUTFluteSpringWebSystem();
        BarAction action = new BarAction();
        cachedInjectingBarAction = action;

        // ## Act ##
        inject(action);

        // ## Assert ##
        log(action.barBhv);
        log(action.barLogic);
        log(action.transactionManager);
        log(action.request);
        assertNotNull(action.barBhv);
        assertNotNull(action.barLogic);
        cachedNestedBarLogic = action.barLogic;
        assertNotNull(action.barLogic.request); // injected as special nested binding
        assertNotNull(action.transactionManager);
        assertNotNull(action.request);
        assertUTFluteSpringWebSystem();
    }

    protected void assertUTFluteSpringWebSystem() {
        assertNotNull(RequestContextHolder.getRequestAttributes());
        assertTrue(getApplicationContext() instanceof WebApplicationContext);
        assertNotNull(barLogic);
    }
}
