本项目是基于上海大学语义智能实验室CEC（Chinese Emergency Corpus）语料库所进行的要素规则挖掘，采用LTP进行分词、词性标注、命名实体识别等，从而对生语料进行
自动化标注，添加对应的标签，并进行格式校验，保存为XML文件等。

Version：1.0.0
开发环境(不保证支持Unix/Linux环境)：
项目编码设定：UTF-8
开发工具：Eclipse 4.4luna
操作系统OS：Windows7 64bit
JDK version：Oracle JDK 1.8+
日志：log4j2.0
单元测试：JUnit

本项目开发及维护者：王旭(wangx89@126.com)
开发时间：2014-2015

请将在LTP-cloud注册之后获取的api_key填入config.properties文件中，覆盖旧值即可；
resources.synset中存放的是从CEC语料库中抽取的不同类型的触发词库，并且以一定的算法，经过同义词词林扩展版扩充之后的触发词库，denoter是包括所有类型的触发词的汇总表



