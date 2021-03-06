/*
 * Copyright 2011-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.regions;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProviderChain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite {@link AwsRegionProvider} that sequentially delegates to a chain of providers looking
 * for region information.
 */
public class AwsRegionProviderChain extends AwsRegionProvider {

    private static final Log LOG = LogFactory.getLog(AWSCredentialsProviderChain.class);

    private final List<AwsRegionProvider> providers;

    public AwsRegionProviderChain(AwsRegionProvider... providers) {
        this.providers = new ArrayList<AwsRegionProvider>(providers.length);
        Collections.addAll(this.providers, providers);
    }

    @Override
    public String getRegion() throws AmazonClientException {
        for (AwsRegionProvider provider : providers) {
            try {
                final String region = provider.getRegion();
                if (region != null) {
                    return region;
                }
            } catch (Exception e) {
                // Ignore any exceptions and move onto the next provider
                LOG.debug("Unable to load region from " + provider.toString() +
                          ": " + e.getMessage());
            }
        }
        throw new AmazonClientException("Unable to load region information from any provider in the chain");
    }
}
