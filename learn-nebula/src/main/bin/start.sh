#!/bin/sh
#-------------------------------------------------------------------------------------------------------------
#该脚本的使用方式为-->[sh run.sh]
#该脚本可在服务器上的任意目录下执行,不会影响到日志的输出位置等
#-------------------------------------------------------------------------------------------------------------
SCRIPT="$0"
while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

APP_HOME=`dirname "$SCRIPT"`/..

JAVA_HOME="/usr/java/jdk1.8.0_60"
if [ $# -lt 4 ] ; then
  echo "导入数据到nebula"
  echo "USAGE: ./bin/start.sh dataFile 数据类型【1 点, 2 边】 numThread batchNum"
  echo " e.g.: ./bin/start.sh vertexs.txt 1 10 100"
  exit 1;
fi

APP_LOG=${APP_HOME}/logs
APP_HOME=`cd "$SCRIPT"; pwd`
CLASSPATH=$APP_HOME/conf
for jarFile in ${APP_HOME}/lib/*.jar;
do
   CLASSPATH=$CLASSPATH:$jarFile
done

#参数处理
APP_MAIN="com.vesoft.nebula.examples.GraphMultiThreadWriteExample"

params=$@
JAVA_OPTS="-Duser.timezone=GMT+8 -server -Xms2048m -Xmx2048m -Xloggc:${APP_LOG}/gc.log -DLOG_DIR=${APP_LOG}"


startup(){
  aparams=($params)
  #echo "params len "${#aparams[@]}
  len=${#aparams[@]}
  for ((i=0;i<$len;i++));do
    echo "第${i}参数:${aparams[$i]}";
    str=$str" "${aparams[$i]};
  done
  echo "Starting $APP_MAIN"
  echo "$JAVA_HOME/bin/java $JAVA_OPTS -classpath $CLASSPATH $APP_MAIN $str"
  $JAVA_HOME/bin/java $JAVA_OPTS -classpath $CLASSPATH $APP_MAIN $str
}
startup