# 关闭程序
# fileName为jar包的名称
fileName=api-backend-0.0.1-SNAPSHOT.jar
pid=$(ps -ef | grep $api-backend-0.0.1-SNAPSHOT.jar| grep -v "grep" | awk '{print $2}')
kill -9 $pid
pid=$(ps -ef | grep $api-gateway-0.0.1-SNAPSHOT.jar| grep -v "grep" | awk '{print $2}')
kill -9 $pid

# 启动项目
# /www/wwwroot/object/jdk1.8.0_161/bin/java -jar -Xmx1024M -Xms256M  /www/wwwroot/object/api-backend-0.0.1-SNAPSHOT.jar --# server.port=7529
