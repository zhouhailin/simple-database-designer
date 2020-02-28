# 数据库脚本工具 - 简单

----------
## 简要说明

    使用Excel设计表结构，生成多种数据库脚本，及数据库Word文档
    
    希望路过的小伙伴，多关注，项目达到百星后可免费向IDEA官网申请激活码。

----------
## 数据库

    目前计划支持主流数据库：MySQL，ORACLE，SQLSERVER
    
----------
## 使用源码
    
    git clone https://github.com/zhouhailin/simple-database-designer
    cd simple-database-designer
    mvn package
    java -jar target/simple-database-designer-*.jar 

----------
## 使用发布包

----------
## 配置

    修改 config/application.properties 配置文件中 simpleGenerateDir 指定工作目录
    
    运行时扫描工作目录下所有 *.xlsx 文件生成一个数据库脚本

----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
