package edu.shu.auto.test;

import org.junit.Test;

import edu.shu.auto.preprocess.PreProcess;
import edu.shu.auto.util.EncodingUtil;

public class TestFileUtil {
	@Test
	public void test() {
		String filePath = "C:\\Users\\TKPad\\Desktop\\四川发生6.1级地震波及云南多个地区.txt";
		// StringBuilder text = PreProcess.getText(filePath);

		StringBuilder text = PreProcess.getText(filePath, EncodingUtil.CHARSET_UTF8);
		System.out.println(text);
	}

	public static void main(String[] args) {
		System.out.println("	AAA范德萨发  的飞洒发".replaceAll("\\s*", ""));
	}
}
