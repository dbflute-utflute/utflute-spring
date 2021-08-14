/*
 * Copyright 2014-2021 the original author or authors.
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
package org.dbflute.utflute.spring.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dbflute.utflute.core.binding.ComponentBinder;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.dbflute.utflute.mocklet.MockletHttpServletRequestImpl;
import org.dbflute.utflute.mocklet.MockletHttpServletResponse;
import org.dbflute.utflute.mocklet.MockletHttpServletResponseImpl;
import org.dbflute.utflute.mocklet.MockletHttpSession;
import org.dbflute.utflute.mocklet.MockletServletConfig;
import org.dbflute.utflute.mocklet.MockletServletConfigImpl;
import org.dbflute.utflute.mocklet.MockletServletContext;
import org.dbflute.utflute.mocklet.MockletServletContextImpl;
import org.dbflute.utflute.spring.ContainerTestCase;
import org.dbflute.utflute.spring.web.mock.MockWebApplicationContextAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;

/**
 * @author jflute
 * @since 0.4.0 (2014/03/16 Sunday)
 */
public abstract class WebContainerTestCase extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                         Static Cached
    //                                         -------------
    /** The cached determination of suppressing web mock. (NullAllowed: null means beginning or ending) */
    private static Boolean _xcachedSuppressWebMock;

    // -----------------------------------------------------
    //                                              Web Mock
    //                                              --------
    /** The mock request of the test case execution. (NullAllowed: when no web mock or beginning or ending) */
    private MockletHttpServletRequest _xmockRequest;

    /** The mock response of the test case execution. (NullAllowed: when no web mock or beginning or ending) */
    private MockletHttpServletResponse _xmockResponse;

    /** The mock listener of the test case request. (NullAllowed: when no web mock or beginning or ending) */
    private RequestContextListener _xmockListener;

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    // -----------------------------------------------------
    //                                     Prepare Container
    //                                     -----------------
    @Override
    protected void xprepareTestCaseContainer() {
        super.xprepareTestCaseContainer();
        xdoPrepareWebMockContext();
        xdoListenRequestInitialized();
        xdoAdaptApplicationContextToWeb();
    }

    @Override
    protected boolean xconfigCanAcceptContainerRecycle(String[] configFiles) {
        final boolean superResult = super.xconfigCanAcceptContainerRecycle(configFiles);
        if (superResult) {
            return true;
        }
        return xgetCachedContext() != null && xgetCachedContext() instanceof WebApplicationContext;
    }

    @Override
    protected boolean xcanRecycleContainer(String[] configFiles) {
        final boolean superResult = super.xcanRecycleContainer(configFiles);
        return superResult && xisCachedApplicationContextWeb() && xwebMockCanAcceptContainerRecycle();
    }

    protected boolean xisCachedApplicationContextWeb() {
        return xgetCachedContext() != null && xgetCachedContext() instanceof WebApplicationContext;
    }

    protected boolean xwebMockCanAcceptContainerRecycle() {
        return _xcachedSuppressWebMock == null || _xcachedSuppressWebMock.equals(isSuppressWebMock()); // no mark or no change
    }

    @Override
    protected void xsaveCachedInstance(String[] configFiles) {
        super.xsaveCachedInstance(configFiles);
        _xcachedSuppressWebMock = isSuppressWebMock();
    }

    /**
     * Does it suppress web mock? e.g. HttpServletRequest, HttpSession
     * @return The determination, true or false.
     */
    protected boolean isSuppressWebMock() {
        return false;
    }

    protected void xdoPrepareWebMockContext() {
        if (isSuppressWebMock()) {
            return;
        }
        final MockletServletConfig servletConfig = createMockletServletConfig();
        servletConfig.setServletContext(createMockletServletContext());
        xregisterWebMockContext(servletConfig);
    }

    protected void xdoListenRequestInitialized() {
        final MockletHttpServletRequest request = getMockRequest();
        if (request != null) {
            ServletRequestEvent event = new ServletRequestEvent(request.getServletContext(), request);
            _xmockListener = new RequestContextListener();
            _xmockListener.requestInitialized(event);
        }
    }

    protected void xdoAdaptApplicationContextToWeb() {
        final MockletHttpServletRequest request = getMockRequest();
        if (request != null) {
            final ApplicationContext appContext = getApplicationContext();
            final ServletContext servletContext = request.getServletContext();
            xsetCurrentActiveContext(new MockWebApplicationContextAdapter(appContext, servletContext));
        }
    }

    @Override
    public void tearDown() throws Exception {
        xdoListenRequestDestroyed();
        xdoClearWebMockContext();
        super.tearDown();
    }

    protected void xdoListenRequestDestroyed() {
        if (_xmockListener != null) {
            final MockletHttpServletRequest request = getMockRequest();
            if (request != null) { // just in case
                ServletRequestEvent event = new ServletRequestEvent(request.getServletContext(), request);
                _xmockListener.requestDestroyed(event);
                _xmockListener = null;
            }
        }
    }

    protected void xdoClearWebMockContext() {
        _xmockRequest = null;
        _xmockResponse = null;
    }

    // ===================================================================================
    //                                                                   Component Binding
    //                                                                   =================
    @Override
    protected void xadjustOuterComponentBinder(Object bean, ComponentBinder binder) {
        super.xadjustOuterComponentBinder(bean, binder);
        final MockletHttpServletRequest request = getMockRequest();
        if (request != null) {
            binder.addNestedBindingComponent(HttpServletRequest.class, request);
        }
        final MockletHttpSession session = getMockSession();
        if (session != null) {
            binder.addNestedBindingComponent(HttpSession.class, session);
        }
        final MockletHttpServletResponse response = getMockResponse();
        if (response != null) {
            binder.addNestedBindingComponent(HttpServletResponse.class, response);
        }
    }

    // ===================================================================================
    //                                                                     Spring Handling
    //                                                                     ===============
    // -----------------------------------------------------
    //                                              Web Mock
    //                                              --------
    protected void xregisterWebMockContext(MockletServletConfig servletConfig) {
        final MockletHttpServletRequest request = createMockletHttpServletRequest(servletConfig.getServletContext());
        final MockletHttpServletResponse response = createMockletHttpServletResponse(request);
        final HttpSession session = request.getSession(true);
        // unknown how to set request and response to Spring DI system
        // and unknown how to new-create mocks per test case execution
        // so register them as mock instance for now
        // (but they cannot be injected to normal component)
        registerMockInstance(request);
        registerMockInstance(response);
        registerMockInstance(session);
        xkeepMockRequestInstance(request, response); // for web mock handling methods
    }

    protected MockletServletConfig createMockletServletConfig() {
        return new MockletServletConfigImpl();
    }

    protected MockletServletContext createMockletServletContext() {
        return new MockletServletContextImpl(prepareMockContextPath());
    }

    protected String prepareMockContextPath() { // you can override
        return "utcontext";
    }

    protected MockletHttpServletRequest createMockletHttpServletRequest(ServletContext servletContext) {
        return new MockletHttpServletRequestImpl(servletContext, prepareMockServletPath());
    }

    protected MockletHttpServletResponse createMockletHttpServletResponse(HttpServletRequest request) {
        return new MockletHttpServletResponseImpl(request);
    }

    protected String prepareMockServletPath() { // v
        return "/utservlet";
    }

    protected void xkeepMockRequestInstance(MockletHttpServletRequest request, MockletHttpServletResponse response) {
        _xmockRequest = request;
        _xmockResponse = response;
    }

    // ===================================================================================
    //                                                                   Web Mock Handling
    //                                                                   =================
    // -----------------------------------------------------
    //                                               Request
    //                                               -------
    protected MockletHttpServletRequest getMockRequest() {
        return (MockletHttpServletRequest) _xmockRequest;
    }

    protected void addMockRequestHeader(String name, String value) {
        final MockletHttpServletRequest request = getMockRequest();
        if (request != null) {
            request.addHeader(name, value);
        }
    }

    @SuppressWarnings("unchecked")
    protected <ATTRIBUTE> ATTRIBUTE getMockRequestParameter(String name) {
        final MockletHttpServletRequest request = getMockRequest();
        return request != null ? (ATTRIBUTE) request.getParameter(name) : null;
    }

    protected void addMockRequestParameter(String name, String value) {
        final MockletHttpServletRequest request = getMockRequest();
        if (request != null) {
            request.addParameter(name, value);
        }
    }

    @SuppressWarnings("unchecked")
    protected <ATTRIBUTE> ATTRIBUTE getMockRequestAttribute(String name) {
        final MockletHttpServletRequest request = getMockRequest();
        return request != null ? (ATTRIBUTE) request.getAttribute(name) : null;
    }

    protected void setMockRequestAttribute(String name, Object value) {
        final MockletHttpServletRequest request = getMockRequest();
        if (request != null) {
            request.setAttribute(name, value);
        }
    }

    // -----------------------------------------------------
    //                                              Response
    //                                              --------
    protected MockletHttpServletResponse getMockResponse() {
        return (MockletHttpServletResponse) _xmockResponse;
    }

    protected Cookie[] getMockResponseCookies() {
        final MockletHttpServletResponse response = getMockResponse();
        return response != null ? response.getCookies() : null;
    }

    protected int getMockResponseStatus() {
        final MockletHttpServletResponse response = getMockResponse();
        return response != null ? response.getStatus() : 0;
    }

    protected String getMockResponseString() {
        final MockletHttpServletResponse response = getMockResponse();
        return response != null ? response.getResponseString() : null;
    }

    // -----------------------------------------------------
    //                                               Session
    //                                               -------
    /**
     * @return The instance of mock session. (NotNull: if no session, new-created)
     */
    protected MockletHttpSession getMockSession() {
        return _xmockRequest != null ? (MockletHttpSession) _xmockRequest.getSession(true) : null;
    }

    protected void invalidateMockSession() {
        final MockletHttpSession session = getMockSession();
        if (session != null) {
            session.invalidate();
        }
    }

    @SuppressWarnings("unchecked")
    protected <ATTRIBUTE> ATTRIBUTE getMockSessionAttribute(String name) {
        final MockletHttpSession session = getMockSession();
        return session != null ? (ATTRIBUTE) session.getAttribute(name) : null;
    }

    protected void setMockSessionAttribute(String name, Object value) {
        final MockletHttpSession session = getMockSession();
        if (session != null) {
            session.setAttribute(name, value);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected static Boolean xgetCachedSuppressWebMock() {
        return _xcachedSuppressWebMock;
    }

    protected static void xsetCachedSuppressWebMock(Boolean xcachedSuppressWebMock) {
        _xcachedSuppressWebMock = xcachedSuppressWebMock;
    }

    protected MockletHttpServletRequest xgetMockRequest() {
        return _xmockRequest;
    }

    protected void xsetMockRequest(MockletHttpServletRequest xmockRequest) {
        _xmockRequest = xmockRequest;
    }

    protected MockletHttpServletResponse xgetMockResponse() {
        return _xmockResponse;
    }

    protected void xsetMockResponse(MockletHttpServletResponse xmockResponse) {
        _xmockResponse = xmockResponse;
    }

    protected RequestContextListener xgetMockListener() {
        return _xmockListener;
    }

    protected void xsetMockListener(RequestContextListener xmockListener) {
        _xmockListener = xmockListener;
    }
}
