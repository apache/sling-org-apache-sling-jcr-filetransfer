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
import java.util.Collections;

import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    property = {
        Constants.SERVICE_DESCRIPTION + "=Apache Sling JCR File Transfer SFTP Service",
        Constants.SERVICE_VENDOR + "=The Apache Software Foundation"
    }
)
@Designate(
    ocd = SftpServiceConfiguration.class
)
public class SftpService {

    @Reference(
        target = "(type=jcr)"
    )
    private volatile FileSystemFactory fileSystemFactory;

    @Reference
    private volatile PasswordAuthenticator passwordAuthenticator;

    @Reference
    private volatile KeyPairProvider keyPairProvider;

    private SftpSubsystemFactory sftpSubsystemFactory = new SftpSubsystemFactory();

    private SshServer sshServer;

    private SftpServiceConfiguration configuration;

    private final Logger logger = LoggerFactory.getLogger(SftpService.class);

    @Activate
    private void activate(final SftpServiceConfiguration configuration) throws IOException {
        logger.debug("activating");
        this.configuration = configuration;
        sshServer = sshServer();
        sshServer.start();
    }

    @Deactivate
    private void deactivate() throws IOException {
        logger.debug("deactivating");
        sshServer.stop();
        sshServer = null;
        configuration = null;
    }

    private SshServer sshServer() {
        final SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setPort(configuration.ssh_server_port());
        sshServer.setPasswordAuthenticator(passwordAuthenticator);
        sshServer.setKeyPairProvider(keyPairProvider);
        sshServer.setSubsystemFactories(Collections.singletonList(sftpSubsystemFactory));
        sshServer.setFileSystemFactory(fileSystemFactory);
        return sshServer;
    }

}
