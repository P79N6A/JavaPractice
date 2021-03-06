package com.hanxiaocu.test.service;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @desc:
 * @author: hanchenghai
 * @date: 2018/11/01 4:51 PM
 */

@RunWith(SpringRunner.class)
//SpringBootTest 是springboot 用于测试的注解，可指定启动类或者测试环境等，这里直接默认。
@SpringBootTest
public class UnitTestServiceTest2 {

	@Autowired
	UnitTestService testService;

	//性能测试
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	@Test
	//是个线程 调用100次
	@PerfTest(invocations = 100,threads = 10)
	public void test() {
		String msg = "this is a test";
		String result = testService.process(msg);
		Assert.assertEquals(msg, result);
	}
}
