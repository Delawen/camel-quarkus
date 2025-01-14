/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.core.cloud.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import org.apache.camel.model.cloud.ServiceCallConfiguration;
import org.apache.camel.quarkus.core.deployment.spi.CamelServiceDestination;
import org.apache.camel.quarkus.core.deployment.spi.CamelServicePatternBuildItem;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

class CloudProcessor {
    @BuildStep
    void servicePatterns(BuildProducer<CamelServicePatternBuildItem> services) {
        services.produce(new CamelServicePatternBuildItem(
                CamelServiceDestination.DISCOVERY,
                true,
                "META-INF/services/org/apache/camel/cloud/*"));

    }

    @BuildStep
    void reflectiveClasses(BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            CombinedIndexBuildItem combinedIndexBuildItem) {
        // TODO: remove when https://github.com/apache/camel-quarkus/issues/2955 is fixed
        IndexView index = combinedIndexBuildItem.getIndex();
        index.getAllKnownSubclasses(DotName.createSimple(ServiceCallConfiguration.class.getName()))
                .stream()
                .map(classInfo -> new ReflectiveClassBuildItem(true, false, classInfo.name().toString()))
                .forEach(reflectiveClass::produce);
    }

}
