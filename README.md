# AIproject

|                AI and MAS: Searchclient               |

|                        README                         |

This readme describes how to use the included Java searchclient with the server that is contained in server.jar.

The search client requires at least a JDK for Java 11, and has been tested with OpenJDK.

Note that if you have the CLASSPATH environment variable set, the following commands may/will fail.
You should not have the CLASSPATH environment variable set unless you know what you're doing.

All the following commands assume the working directory is main\src.

You can read about the server options using the -h argument:
    $ java -jar server.jar -h

Compiling the searchclient:
    $ javac *.java

Starting the server using the searchclient:
    $ java -jar server.jar -l C:\Users\***\...\levels\SAD1.lvl -c "java SearchClient"

Mathias:
java -jar server.jar -l C:\Users\Mathi\Desktop\8_semester\AI\AIproject\levels\Complevel.lvl -c "java SearchClient" -g

Simon:
java -jar server.jar -l C:\Users\Simon\IdeaProjects\AIproject\levels\SAD1.lvl -c "java SearchClient"

Oline:

java -jar server.jar -l /Users/olinestaerke/Desktop/AIproject/levels/test8.lvl -c "java SearchClient"

RUN ALL:
Mathias:
java -jar server.jar -l C:\Users\Mathi\Desktop\8_semester\AI\AIproject\levels\2019SA -c "java SearchClient" -t 180 -o "C:\Users\Mathi\Desktop\8_semester\AI\AIproject\OUTSA2019.zip"

RUN ALL:
Oline:
java -jar server.jar -l /Users/olinestaerke/Desktop/AIproject/levels/2019MA -c "java SearchClient" -t 180 -o "/Users/olinestaerke/Desktop/OUTMA.zip"
