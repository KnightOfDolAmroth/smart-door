mkdir -p bin
javac -d bin src/consegna_4/seiot/common/*.java
javac -classpath bin -d bin src/consegna_4/seiot/devices/*.java
javac -classpath bin -d bin src/consegna_4/seiot/devices/simulated/*.java
javac -classpath bin:/usr/share/java/RXTXcomm.jar:/opt/pi4j/lib/'*' -d bin src/consegna_4/seiot/devices/impl/*.java
javac -classpath bin:/usr/share/java/RXTXcomm.jar:/opt/pi4j/lib/'*' -d bin src/consegna_4/RoomInfo.java
javac -classpath bin:/usr/share/java/RXTXcomm.jar:/opt/pi4j/lib/'*' -d bin src/consegna_4/events/*.java
javac -classpath bin:/usr/share/java/RXTXcomm.jar:/opt/pi4j/lib/'*' -d bin src/consegna_4/synchronization/*.java
javac -classpath bin:/usr/share/java/RXTXcomm.jar:/opt/pi4j/lib/'*' -d bin src/consegna_4/*.java
