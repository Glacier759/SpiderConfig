SpiderConfig
============

配合SpiderManager重构各个模块爬虫

2014-12-13 21:07

###项目说明

>本项目为重构NewsEye项目

>配置文件规则修正

>采用redis作为缓存队列，便于日后再前段进行抓取状态的展示

>应用多线程提高抓取效率

>仿照scrapy、webmagic等项目进行模块划分(我还是做的有些不太对)..

###2014-12-19 22:06

>增加start.sh启动脚本 参数为配置文件id

>增加数据库spider_config字段存储BloomFilter
