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

package org.apache.jackrabbit.oak.segment;

import java.io.IOException;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.oak.commons.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

/**
 * An OSGi wrapper for segment node store monitoring configurations.
 */
@Component(policy = ConfigurationPolicy.REQUIRE,
        metatype = true,
        label = "Oak Segment Tar Monitoring service",
        description = "This service is responsible for different configurations related to " + 
                "Oak Segment Tar read/write monitoring."
)
public class SegmentNodeStoreMonitorService {
    
    @Property(label = "Writer groups",
            unbounded = PropertyUnbounded.ARRAY,
            description = "Writer groups for which commits are tracked individually"
    )
    private static final String COMMITS_TRACKER_WRITER_GROUPS = "commitsTrackerWriterGroups";

    @Reference
    private SegmentNodeStoreStatsMBean snsStatsMBean;
    
    @Activate
    public void activate(ComponentContext context, Map<String, ?> config) throws IOException {
        augmentSegmentNodeStoreStatsMBean(config);
    }

    private void augmentSegmentNodeStoreStatsMBean(Map<String, ?> config) {
        snsStatsMBean.setWriterGroupsForLastMinuteCounts(
                PropertiesUtil.toStringArray(config.get(COMMITS_TRACKER_WRITER_GROUPS), null));
    }
}
