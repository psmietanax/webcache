#!/bin/bash

cd `dirname $0`/..

if [ -f "bin/jar/webcache-0.0.1-SNAPSHOT.jar" ]; then
	if [ $# -eq 1 ]; then
		java -jar bin/jar/webcache-0.0.1-SNAPSHOT.jar --webcache.maxSize=$1
	else
		java -jar bin/jar/webcache-0.0.1-SNAPSHOT.jar
	fi
else
	echo "Required file 'jar/webcache-0.0.1-SNAPSHOT.jar' doesn't exist."
	exit 1
fi