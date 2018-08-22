/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.jcr.filetransfer.sftp.internal;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    property = {
        Constants.SERVICE_DESCRIPTION + "=Apache Sling JCR File Transfer 'JCR Password Authenticator'",
        Constants.SERVICE_VENDOR + "=The Apache Software Foundation"
    }
)
public class JcrPasswordAuthenticator implements PasswordAuthenticator {

    @Reference
    private volatile Repository repository;

    private final Logger logger = LoggerFactory.getLogger(JcrPasswordAuthenticator.class);

    @Override
    public boolean authenticate(final String username, final String password, final ServerSession serverSession) throws PasswordChangeRequiredException, AsyncAuthException {
        logger.info("authenticating {} with password", username);
        Session session = null;
        try {
            session = repository.login(new SimpleCredentials(username, password.toCharArray()));
            logger.info("authenticating user {} ({}) succeeded", username, session.getUserID());
            return true;
        } catch (Exception e) {
            logger.error("authenticating user {} failed: {}", username, e.getMessage());
        } finally {
            if (session != null) {
                session.logout();
            }
        }
        return false;
    }

}
