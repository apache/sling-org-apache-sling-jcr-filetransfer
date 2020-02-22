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
package org.apache.sling.jcr.filetransfer.it;

import javax.inject.Inject;

import org.apache.sling.testing.paxexam.TestSupport;
import org.apache.sshd.common.file.FileSystemFactory;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.util.Filter;

import static org.apache.sling.testing.paxexam.SlingOptions.slingQuickstartOakTar;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

public abstract class JcrFiletransferTestSupport extends TestSupport {

    @Inject
    @Filter(value = "(type=jcr)")
    protected FileSystemFactory fileSystemFactory;

    @Configuration
    public Option[] configuration() {
        return options(
            baseConfiguration(),
            quickstart(),
            // Sling JCR File Transfer
            testBundle("bundle.filename"),
            mavenBundle().groupId("org.apache.sshd").artifactId("sshd-core").versionAsInProject(),
            mavenBundle().groupId("org.apache.sshd").artifactId("sshd-sftp").versionAsInProject(),
            // Sling Commons JCR File
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.commons.jcr.file").versionAsInProject(),
            // testing
            mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.hamcrest").versionAsInProject(),
            junitBundles()
        );
    }

    protected Option quickstart() {
        final int httpPort = findFreePort();
        final String workingDirectory = workingDirectory();
        return composite(
            slingQuickstartOakTar(workingDirectory, httpPort)
        );
    }

}
