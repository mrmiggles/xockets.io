package com.tc.websocket.junit.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({SendViaRoutingPathURI.class, SendMessageTest.class, TestRestServices.class, CalcResults.class})
public class AllTests {

}
  