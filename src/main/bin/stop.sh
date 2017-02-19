#!/usr/bin/env bash

cygwin=false;
linux=false;

case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
    Linux*)
    	linux=true
    	;;
esac

get_pid() {
	STR=$1
	PID=$2
    if $cygwin; then
        JAVA_CMD="$JAVA_HOME\bin\java"
        JAVA_CMD=`cygpath --path --unix $JAVA_CMD`
        JAVA_PID=`ps |grep $JAVA_CMD |awk '{print $1}'`
    else
    	if $linux; then
	        if [ ! -z "$PID" ]; then
	        	JAVA_PID=`ps -C java -f --width 1000|grep "$STR"|grep "$PID"|grep -v grep|awk '{print $2}'`
		    else
		        JAVA_PID=`ps -C java -f --width 1000|grep "$STR"|grep -v grep|awk '{print $2}'`
	        fi
	    else
	    	if [ ! -z "$PID" ]; then
	        	JAVA_PID=`ps aux |grep "$STR"|grep "$PID"|grep -v grep|awk '{print $2}'`
		    else
		        JAVA_PID=`ps aux |grep "$STR"|grep -v grep|awk '{print $2}'`
	        fi
	    fi
    fi
    echo $JAVA_PID;
}

base=`dirname $0`/..
pidfile=$base/bin/cdb-binlog.pid
if [ ! -f "$pidfile" ];then
	echo "cdb-binlog not running."
	exit
fi

pid=`cat $pidfile`
if [ "$pid" == "" ] ; then
	pid=`get_pid "appName=cdb-binlog"`
fi

echo -e "`hostname`: stopping cdb-binlog $pid ... "
kill $pid

while (true);
do
	gpid=`get_pid "appName=cdb-binlog" "$pid"`
    if [ "$gpid" == "" ] ; then
    	echo "cdb-binlog has exited!"
    	`rm $pidfile`
    	break;
    fi
    sleep 1
done