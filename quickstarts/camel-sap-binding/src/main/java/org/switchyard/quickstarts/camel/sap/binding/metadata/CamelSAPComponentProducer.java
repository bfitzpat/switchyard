/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.quickstarts.camel.sap.binding.metadata;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.fusesource.camel.component.sap.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.SapSynchronousRfcServerComponent;
import org.fusesource.camel.component.sap.model.rfc.RepositoryData;

/**
 * Camel SAPComponent producer which setup the required metadata.
 */
public class CamelSAPComponentProducer {

    @Produces
    @Named("sap-srfc-server")
    public SapSynchronousRfcServerComponent create(
            @Named("sap-configuration") SapConnectionConfiguration sapConfiguration,
            @Named("repositoryDataStore") Map<String,RepositoryData> repositoryDataStore ) {
        SapSynchronousRfcServerComponent sap = new SapSynchronousRfcServerComponent();
        sap.setRepositoryDataStore(repositoryDataStore);
        return sap;
    }
}
