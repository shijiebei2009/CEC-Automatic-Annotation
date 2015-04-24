package edu.shu.auto.test;

import org.junit.Test;

import edu.shu.auto.util.ReadConfigFile;

public class TestReadConfigFile {
	
	@Test
	public void test(){
		String value = ReadConfigFile.getValue("denoter");
		System.out.println(value);
	}
}
