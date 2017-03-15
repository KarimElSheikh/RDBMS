SRCPATH = ./src/FauxBots/
CLSSPATH = ./classes/

compile: 
	javac -d $(CLSSPATH) -classpath $(CLSSPATH) $(SRCPATH)*.java -Xlint

all: compile


clean: 
	rm ./classes/FauxBots/*