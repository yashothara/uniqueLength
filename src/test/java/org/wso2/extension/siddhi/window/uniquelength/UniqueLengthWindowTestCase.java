/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.window.uniquelength;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

public class UniqueLengthWindowTestCase {
    private static final Logger log = Logger.getLogger(UniqueLengthWindowTestCase.class);
    private int count;
    private long value;
    private boolean eventArrived;

    @Before
    public void init() {
        count = 0;
        value = 0;
        eventArrived = false;
    }
    @Test
    public void uniqueLengthWindowTest1() throws InterruptedException {
        log.info("Testing uniqueLength window with no of events smaller than window size");
        final int length = 4;
        SiddhiManager siddhiManager = new SiddhiManager();
        String cseEventStream = "" +
                "define stream LoginEvents (timeStamp long, a string, ip string);";
        String query = "" +
                "@info(name = 'query1') " +
                "from LoginEvents#window.unique:length(ip,"+length+") " +
                "select a, count(ip) as ipCount, ip " +
                "insert all events into uniqueIps ;";
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);
        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    value = (Long) inEvents[inEvents.length - 1].getData(1);
                }
                eventArrived = true;
            }

        });
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A1", "192.10.1.4"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A2", "192.10.1.3"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A3", "192.10.1.3"});
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event max value", 2, value);
        executionPlanRuntime.shutdown();
    }
    @Test
    public void uniqueLengthWindowTest2() throws InterruptedException {
        log.info("Testing uniqueLength window with no of events greater than window size");
        SiddhiManager siddhiManager = new SiddhiManager();
        String cseEventStream = "" +
                "define stream LoginEvents (timeStamp long, a string, ip string);";
        String query = "" +
                "@info(name = 'query1') " +
                "from LoginEvents#window.unique:length(ip,3) " +
                "select a, count(ip) as ipCount, ip " +
                "insert all events into uniqueIps ;";
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);
        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    value = (Long) inEvents[inEvents.length - 1].getData(1);
                }
                eventArrived = true;
            }

        });
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A1", "192.10.1.4"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A2", "192.10.1.3"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A3", "192.10.1.3"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A4", "192.10.1.4"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A5", "192.10.1.5"});
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event max value", 3, value);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void uniqueLengthWindowTest3() throws InterruptedException {
        log.info("Testing uniqueLength window with no of events greater than window size");
        SiddhiManager siddhiManager = new SiddhiManager();
        String cseEventStream = "" +
                "define stream LoginEvents (timeStamp long, a string, ip string);";
        String query = "" +
                "@info(name = 'query1') " +
                "from LoginEvents#window.unique:length(ip,3) " +
                "select a, ip " +
                "insert expired events into uniqueIps ;";
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);
        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    value = (Long) inEvents[inEvents.length - 1].getData(1);
                }
                eventArrived = true;
            }

        });
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A1", "192.10.1.3"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A2", "192.10.1.4"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A3", "192.10.1.4"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A4", "192.10.1.3"});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A5", "192.10.1.5"});
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event max value", 0, value);
        executionPlanRuntime.shutdown();
    }
    @Test
    public void uniqueLengthWindowTest4() throws InterruptedException {
        log.info("Testing uniqueLength window with no of events greater than window size");
        SiddhiManager siddhiManager = new SiddhiManager();
        String cseEventStream = "" +
                "define stream LoginEvents (timeStamp long, a string, ip string, b double);";
        String query = "" +
                "@info(name = 'query1') " +
                "from LoginEvents#window.unique:length(b,3) " +
                "select a, count(ip) as ipCount, ip, b " +
                "insert expired events into uniqueIps ;";
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);
        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    value = (Long) inEvents[inEvents.length - 1].getData(1);
                }
                eventArrived = true;
            }

        });
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A1", "192.10.1.4", 2.112});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A2", "192.10.1.3", 4.33});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A3", "192.10.1.3", 4.333});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A4", "192.10.1.4", 5.3});
        inputHandler.send(new Object[]{System.currentTimeMillis(), "A5", "192.10.1.5", 6.1});
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event max value", 0, value);
        executionPlanRuntime.shutdown();
    }

}
