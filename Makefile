JAVAEXEC=javac

compile:
	$(JAVAEXEC) *.java

clean:
	$(RM) *.class

default: compile

