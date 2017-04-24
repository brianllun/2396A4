
Compile: ImageServer.java ImagePeer.java
	javac ImageServer.java
	javac ImagePeer.java

Server:ImageServer.java
	javac ImageServer.java
	java ImageServer
Peer:ImagePeer.java
	javac ImagePeer.java
	java ImagePeer
Clean:
	rm -f ImageServer.class ImagePeer.class
