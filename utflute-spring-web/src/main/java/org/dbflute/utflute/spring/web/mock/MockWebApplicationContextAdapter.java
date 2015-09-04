package org.dbflute.utflute.spring.web.mock;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author jflute
 */
public class MockWebApplicationContextAdapter implements WebApplicationContext {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ApplicationContext _applicationContext;
    protected final ServletContext _servletContext;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MockWebApplicationContextAdapter(ApplicationContext applicationContext, ServletContext servletContext) {
        _applicationContext = applicationContext;
        _servletContext = servletContext;
    }

    // ===================================================================================
    //                                                                      Normal Context
    //                                                                      ==============
    public void publishEvent(ApplicationEvent event) {
        _applicationContext.publishEvent(event);
    }

    public BeanFactory getParentBeanFactory() {
        return _applicationContext.getParentBeanFactory();
    }

    public boolean containsLocalBean(String name) {
        return _applicationContext.containsLocalBean(name);
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return _applicationContext.getMessage(code, args, defaultMessage, locale);
    }

    public Resource getResource(String location) {
        return _applicationContext.getResource(location);
    }

    public Environment getEnvironment() {
        return _applicationContext.getEnvironment();
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return _applicationContext.getMessage(code, args, locale);
    }

    public boolean containsBeanDefinition(String beanName) {
        return _applicationContext.containsBeanDefinition(beanName);
    }

    public ClassLoader getClassLoader() {
        return _applicationContext.getClassLoader();
    }

    public String getId() {
        return _applicationContext.getId();
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return _applicationContext.getResources(locationPattern);
    }

    public String getApplicationName() {
        return _applicationContext.getApplicationName();
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return _applicationContext.getMessage(resolvable, locale);
    }

    public int getBeanDefinitionCount() {
        return _applicationContext.getBeanDefinitionCount();
    }

    public String getDisplayName() {
        return _applicationContext.getDisplayName();
    }

    public long getStartupDate() {
        return _applicationContext.getStartupDate();
    }

    public String[] getBeanDefinitionNames() {
        return _applicationContext.getBeanDefinitionNames();
    }

    public ApplicationContext getParent() {
        return _applicationContext.getParent();
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return _applicationContext.getAutowireCapableBeanFactory();
    }

    public String[] getBeanNamesForType(Class<?> type) {
        return _applicationContext.getBeanNamesForType(type);
    }

    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return _applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public Object getBean(String name) throws BeansException {
        return _applicationContext.getBean(name);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return _applicationContext.getBean(name, requiredType);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return _applicationContext.getBeansOfType(type);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return _applicationContext.getBean(requiredType);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        return _applicationContext.getBean(name, args);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return _applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return _applicationContext.getBean(requiredType, args);
    }

    public boolean containsBean(String name) {
        return _applicationContext.containsBean(name);
    }

    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return _applicationContext.getBeanNamesForAnnotation(annotationType);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return _applicationContext.isSingleton(name);
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return _applicationContext.getBeansWithAnnotation(annotationType);
    }

    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return _applicationContext.findAnnotationOnBean(beanName, annotationType);
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return _applicationContext.isPrototype(name);
    }

    public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
        return _applicationContext.isTypeMatch(name, targetType);
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return _applicationContext.getType(name);
    }

    public String[] getAliases(String name) {
        return _applicationContext.getAliases(name);
    }

    public String[] getBeanNamesForType(ResolvableType type) {
        return _applicationContext.getBeanNamesForType(type);
    }

    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return _applicationContext.isTypeMatch(name, typeToMatch);
    }

    public void publishEvent(Object event) {
        _applicationContext.publishEvent(event);
    }

    // ===================================================================================
    //                                                                         Web Context
    //                                                                         ===========
    @Override
    public ServletContext getServletContext() {
        return _servletContext;
    }
}
