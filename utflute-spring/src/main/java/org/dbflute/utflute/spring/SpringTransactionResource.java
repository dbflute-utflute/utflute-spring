/*
 * Copyright 2014-2022 the original author or authors.
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
package org.dbflute.utflute.spring;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.utflute.core.transaction.TransactionFailureException;
import org.dbflute.utflute.core.transaction.TransactionResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * @author jflute
 * @since 0.1.1 (2011/07/25 Monday)
 */
public class SpringTransactionResource implements TransactionResource {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<OneTransactionElement> _transactionManagerList = new ArrayList<OneTransactionElement>(4);

    public static class OneTransactionElement {
        protected PlatformTransactionManager _platformTransactionManager;
        protected TransactionStatus _transactionStatus;

        public PlatformTransactionManager getTransactionManager() {
            return _platformTransactionManager;
        }

        public void setTransactionManager(PlatformTransactionManager transactionManager) {
            _platformTransactionManager = transactionManager;
        }

        public TransactionStatus getTransactionStatus() {
            return _transactionStatus;
        }

        public void setTransactionStatus(TransactionStatus transactionStatus) {
            _transactionStatus = transactionStatus;
        }
    }

    // ===================================================================================
    //                                                                 Transaction Control
    //                                                                 ===================
    public void commit() {
        if (_transactionManagerList.isEmpty()) {
            return;
        }
        final OneTransactionElement mainTx = _transactionManagerList.get(0); // because of TransactionSynchronization
        try {
            final PlatformTransactionManager manager = mainTx.getTransactionManager();
            final TransactionStatus status = mainTx.getTransactionStatus();
            manager.commit(status);
        } catch (RuntimeException e) {
            throw new TransactionFailureException("Failed to commit the transaction.", e);
        }
    }

    public void rollback() {
        if (_transactionManagerList.isEmpty()) {
            return;
        }
        final OneTransactionElement mainTx = _transactionManagerList.get(0); // because of TransactionSynchronization
        try {
            final PlatformTransactionManager manager = mainTx.getTransactionManager();
            final TransactionStatus status = mainTx.getTransactionStatus();
            manager.rollback(status);
        } catch (RuntimeException e) {
            throw new TransactionFailureException("Failed to roll-back the transaction.", e);
        }
    }

    // ===================================================================================
    //                                                            Transaction Registration
    //                                                            ========================
    public void registerTransaction(PlatformTransactionManager transactionManager, TransactionStatus transactionStatus) {
        final OneTransactionElement element = new OneTransactionElement();
        element.setTransactionManager(transactionManager);
        element.setTransactionStatus(transactionStatus);
        _transactionManagerList.add(element);
    }
}
