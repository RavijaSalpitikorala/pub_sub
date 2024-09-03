JAVAC = javac
JAVA = java
SRC = $(wildcard *.java)
CLASSFILES = $(SRC:.java=.class)
topic = TOPIC_A

all: $(CLASSFILES)

%.class: %.java
	$(JAVAC) $<

server: Server.class
	$(JAVA) Server 5001

publisher: Client.class
	$(JAVA) Client 127.0.0.1 5001 PUBLISHER $(topic)

subscriber: Client.class
	$(JAVA) Client 127.0.0.1 5001 SUBSCRIBER $(topic)

clean:
	rm -f *.class

.PHONY: all clean server publisher subscriber
