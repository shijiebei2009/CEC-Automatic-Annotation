---
1.本项目是基于上海大学语义智能实验室刘宗田教授、刘炜研究员及各硕士博士研究生所共同构建的中文突发事件语料库CEC-（Chinese Emergency Corpus），针对已标注的语料库中，采用LTP进行分词、词性标注、命名实体识别与依存句法分析等；对事件的要素进行规则挖掘，包括词性规则、命名实体规则、依存关系规则等。进而实现对突发事件类新闻报道的生语料进行自动化标注，添加对应的标签，并进行格式校验，存储为XML文件等。

2.开发环境(测试环境为Windows，不保证支持Unix/Linux环境)
* Licence：Apache Licence Version2
* Version：1.0.0
* 项目编码设定：UTF-8
* 开发工具：Eclipse 4.4 luna
* 操作系统OS：Windows7 64bit
* JDK version：Oracle JDK 1.8+ 64bit
* 日志：log4j2.0
* 单元测试：JUnit4.1.0

3.本项目开发及维护者：王旭(wangx89@126.com)
* 开发时间：2014年-2015年

4.请将在LTP-cloud注册之后获取的api_key填入config.properties文件中，覆盖旧值即可；
resources.synset中存放的是从CEC语料库中抽取的不同类型的触发词库，并且以一定的算法，经过同义词词林扩展版扩充之后的触发词库，denoter是包括所有类型的触发词的汇总表

5.使用方式，拷贝AutoAnnotation.jar到项目之中，拷贝conf目录到项目之中，使用方式参见example.edu.shu.auto.annotation.Demo.java

6.CEC语料库及标注样例参见：https://github.com/shijiebei2009/CEC-Corpus

---