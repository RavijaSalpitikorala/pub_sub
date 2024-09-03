JAVAC = javac
JAVA = java
SRC = $(wildcard *.java)
CLASSFILES = $(SRC:.java=.class)

all: $(CLASSFILES)

%.class: %.java
	$(JAVAC) $<

server: Server.class
	$(JAVA) Server 5001

publisher: Client.class
	$(JAVA) Client 127.0.0.1 5001 PUBLISHER

subscriber: Client.class
	$(JAVA) Client 127.0.0.1 5001 SUBSCRIBER

clean:
	rm -f *.class

.PHONY: all clean server publisher subscriber
