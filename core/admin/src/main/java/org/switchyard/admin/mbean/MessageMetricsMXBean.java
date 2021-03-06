/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.admin.mbean;

/**
 * Reports messaging metrics for entities in a SwitchYard domain : service, 
 * reference, runtime, etc.
 */
public interface MessageMetricsMXBean {

    /**
     * Return the number of successful messages.
     * @return success count
     */
    int getSuccessCount();
    /**
     * Return the number of failed messages.
     * @return fault count
     */
    int getFaultCount();
    /**
     * Return the total number of messages processed.  This is equivalent to
     * getSuccessCount() + getFaultCount().
     * @return total message count
     */
    int getTotalCount();
    /**
     * Total processing time for all messages in TimeUnit.MILLISECONDS.
     * @return total processing time
     */
    long getTotalProcessingTime();
    /**
     * Average processing time for all messages in TimeUnit.MILLISECONDS.
     * @return average processing time
     */
    double getAverageProcessingTime();
    /**
     * Minimum processing time for a message in TimeUnit.MILLISECONDS.
     * @return min processing time
     */
    int getMinProcessingTime();
    /**
     * Maximum processing time for a message in TimeUnit.MILLISECONDS.
     * @return max processing time
     */
    int getMaxProcessingTime();

    /**
     * Reset all collected metrics.
     */
    void reset();


}
