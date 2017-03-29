#!/bin/bash
for i in $(ls | grep .java.in); do
	out=$(echo $i | sed s/\.java\.in/\.java/)
	javaPre $i > $out
done
# javac *.java
