/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.gridgain.demo.springdata.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class DemoConfig {
    @Bean(name = "igniteInstance")
    public Ignite igniteInstance(Ignite ignite) {
        return ignite;
    }

    @Bean
    public IgniteConfigurer configurer() {
        return igniteConfiguration -> {
            TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
            TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
            ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47510"));
            tcpDiscoverySpi.setIpFinder(ipFinder);
            tcpDiscoverySpi.setLocalPort(10800);
            // Changing local port range. This is an optional action.
            tcpDiscoverySpi.setLocalPortRange(9);
            //tcpDiscoverySpi.setLocalAddress("localhost");
            igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
            igniteConfiguration.setClientMode(true);

            TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
            communicationSpi.setLocalAddress("100.64.0.1");
//        communicationSpi.setLocalPort(10801);
            communicationSpi.setSlowClientQueueLimit(1000);
            igniteConfiguration.setCommunicationSpi(communicationSpi);
            igniteConfiguration.setPeerClassLoadingEnabled(true);
        };
    }

    @Bean
    public ClientConfiguration clientConfiguration() {
        // If you provide a whole ClientConfiguration bean then configuration properties will not be used.
        ClientConfiguration cfg = new ClientConfiguration();
        cfg.setAddresses("127.0.0.1:10800");
        return cfg;
    }

}


//@Configuration
//@EnableIgniteRepositories(value = "com.cache.*")
////public class IgniteCacheConfiguration {
//public class DemoConfig {
//
//    @Bean
//    public Ignite igniteInstance() {
//        return Ignition.start(igniteConfiguration());
//    }
//
//    @Bean(name = "igniteConfiguration")
//    public IgniteConfiguration igniteConfiguration() {
//        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
//        igniteConfiguration.setIgniteInstanceName("testIgniteInstance");
//        //igniteConfiguration.setClientMode(true);
//        igniteConfiguration.setPeerClassLoadingEnabled(true);
//        igniteConfiguration.setLocalHost("127.0.0.1");
//
//        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
//        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
//        tcpDiscoverySpi.setIpFinder(ipFinder);
//        tcpDiscoverySpi.setLocalPort(10800);
//        // Changing local port range. This is an optional action.
//        tcpDiscoverySpi.setLocalPortRange(9);
//        //tcpDiscoverySpi.setLocalAddress("localhost");
//        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
//
////        TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
////        communicationSpi.setLocalAddress("127.0.0.1");
//////        communicationSpi.setLocalPort(10801);
////        communicationSpi.setSlowClientQueueLimit(1000);
////        igniteConfiguration.setCommunicationSpi(communicationSpi);
//
////        igniteConfiguration.setCacheConfiguration(cacheConfiguration());
//
//        return igniteConfiguration;
//
//    }
//
////    @Bean(name = "cacheConfiguration")
////    public CacheConfiguration[] cacheConfiguration() {
////        List<CacheConfiguration> cacheConfigurations = new ArrayList<>();
////        // Defining and creating a new cache to be used by Ignite Spring Data
////        // repository.
////        CacheConfiguration ccfg = new CacheConfiguration("PersonCache");
////        // Setting SQL schema for the cache.
////        ccfg.setIndexedTypes(Long.class, Person.class);
////
////        cacheConfigurations.add(ccfg);
////
////        return cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()]);
////    }
//}
