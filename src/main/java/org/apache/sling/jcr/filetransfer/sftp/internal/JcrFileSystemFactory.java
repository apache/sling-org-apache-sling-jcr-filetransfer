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

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.filetransfer.ChrootDirectoryProvider;
import org.apache.sshd.common.file.FileSystemFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = FileSystemFactory.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "=TODO",
        Constants.SERVICE_VENDOR + "=The Apache Software Foundation",
        "type=jcr"
    }
)
public class JcrFileSystemFactory implements FileSystemFactory {

    @Reference(
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY,
        target = "(type=jcr)"
    )
    private volatile FileSystemProvider fileSystemProvider;

    @Reference(
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile SlingRepository slingRepository;

    @Reference(
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile ChrootDirectoryProvider chrootDirectoryProvider;

    private final Logger logger = LoggerFactory.getLogger(JcrFileSystemFactory.class);

    public JcrFileSystemFactory() {
    }

    @Override
    public FileSystem createFileSystem(final org.apache.sshd.common.session.Session session) throws IOException {
        logger.info("creating file system for {}", session.getUsername());
        try {
            final String username = session.getUsername();
            final String chroot = chrootDirectoryProvider.getChrootDirectory(session);
            final Session jcrSession = createUserSession(username);
            final URI uri = new URI("jcr", null, chroot, null);
            final Map<String, Object> env = new HashMap<>();
            env.put(Session.class.getName(), jcrSession);
            logger.debug("file system URI: {}", uri.toString());
            return fileSystemProvider.newFileSystem(uri, env);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private Session createUserSession(final String username) throws RepositoryException {
        final Session session = slingRepository.loginAdministrative(null);
        final Credentials credentials = new SimpleCredentials(username, "".toCharArray());
        return session.impersonate(credentials);
    }

}
