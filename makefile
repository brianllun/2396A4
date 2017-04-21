Compile: ImageServer.java ImagePeer.java
	javac ImageServer.java
	javac ImagePeer.java
CServer:ImageServer.java
	javac ImageServer.java
CPeer:ImagePeer.java
	javac ImagePeer.java
RServer:ImageServer.java
	javac ImageServer.java
	java ImageServer
RPeer:ImagePeer.java
	javac ImagePeer.java
	java ImagePeer
Clean:
	rm -f ImageServer.class ImagePeer.class
