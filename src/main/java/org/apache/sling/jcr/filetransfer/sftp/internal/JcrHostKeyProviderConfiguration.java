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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Apache Sling JCR File Transfer JCR Host Key Provider",
    description = "Stores the SSH host key in JCR"
)
@interface JcrHostKeyProviderConfiguration {

    @AttributeDefinition(
        name = "service ranking",
        description = "service ranking"
    )
    int service_ranking() default 0;

    @AttributeDefinition(
        name = "path",
        description = "path where host key is stored, ensure path is readable/writable"
    )
    String path() default "/var/sling/ssh/hostkey.ser";

}
