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

import org.apache.sling.jcr.filetransfer.ChrootDirectoryProvider;
import org.apache.sshd.common.session.Session;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    property = {
        Constants.SERVICE_DESCRIPTION + "=Apache Sling JCR File Transfer Default Chroot Directory Provider",
        Constants.SERVICE_VENDOR + "=The Apache Software Foundation"
    }
)
@Designate(
    ocd = DefaultChrootDirectoryProviderConfiguration.class
)
public class DefaultChrootDirectoryProvider implements ChrootDirectoryProvider {

    private final Logger logger = LoggerFactory.getLogger(DefaultChrootDirectoryProvider.class);

    public DefaultChrootDirectoryProvider() {
    }

    @Override
    @NotNull
    public String getChrootDirectory(@NotNull final Session session) {
        final String username = session.getUsername();
        final String chroot = String.format("/var/filetransfer/%s", username);
        logger.info("providing chroot {} for user {}", chroot, username);
        return chroot;
    }

}
