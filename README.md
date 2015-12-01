SpiderConfig
============

配合SpiderManager重构各个模块爬虫

2014-12-12 17:35

###项目说明

>本项目为SpiderManager项目数据获取端

>采用IDEA+Maven开发

>各个模块独立一个Module

###模块任务分配

>雄:

>>网络媒体(腾讯新闻.网易新闻.新浪新闻等), 论坛类(Discuz!), 人人网, 影评(豆瓣,Mtime)

>凯:

>>博客类(网易博客.CSDN.新浪博客.博客园.wordpress等), 百度贴吧, 招聘类(简历:58, 招聘:58.智联.大街网等)

>翔:

>>微博类(人物.搜索.话题等), 百度知道.爱问.知乎等问答类, 购物网站类

###详细设计要求

>统一在SpiderConfig下建立相应模块的目录，在此目录中进行功能代码编写

>>雄和我用idea就导入SpiderConfig工程，在底下新建一个Module；

>目录中附带程序启动脚本，用shell编写

>例如：

>><p>#!/bin/bash</p>

>><p>/usr/java/jdk1.7.0_55/bin/java -jar spider.jar args1 args2</p>

>args1表示执行程序的用户名

>args2表示该程序执行需要的配置文件(统一为XML格式，内容自定义)

>

>程序设计模块化

>> A. configure - 配置文件解析模块

>> B. crawler - 抓取模块

>>> a. downloader - 网页源码获取

>>> b. pageprocessor - 网页解析模块		(依据配置文件进行数据提取)

>>> c. scheduler - 缓存队列管理模块		(使用redis进行url缓存管理, key为登录用户名, value为url队列)

>>>										(去重采用BloomFilter)

>>> d. pipeline - 数据持久化	(文件形式存储，自定义存储格式，统一使用XML)

>> C. login - 登录模块	(对于需要进行模拟登陆的网站需要实现登录模块)

>日志管理: 使用log4j规范日志类型

>> <p>样式: "%d{yyyy-MM-dd HH:mm:ss} [%p] - %m {%l}%n"</p>

