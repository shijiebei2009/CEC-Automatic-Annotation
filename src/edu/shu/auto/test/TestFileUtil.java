package edu.shu.auto.test;

import org.junit.Test;

import edu.shu.auto.preprocess.PreProcess;
import edu.shu.auto.util.EncodingUtil;

public class TestFileUtil {
	@Test
	public void test() {
		String filePath = "C:\\Users\\TKPad\\Desktop\\a.txt";
		// StringBuilder text = PreProcess.getText(filePath);

		StringBuilder text = PreProcess.getText(filePath );
		System.out.println(text);
	}
}
