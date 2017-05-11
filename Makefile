JAVAEXEC=javac
JAVAPRE = javaPre

compile:
	./preprocess.sh
	$(JAVAEXEC) *.java

clean:
	$(RM) *.class

default: compile

