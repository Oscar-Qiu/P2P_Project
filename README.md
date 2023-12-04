## <u>P2P_Project Group 41</u>

## <u>Group Members</u>

- Pablo Gonzalez, pablogonzalez@ufl.edu
- Yiheng Qui, 
- Raul Rodriguez, raul.rodriguez@ufl.edu

## <u>Contributions</u>

Pablo Gonzalez:
- handshake message
- bit field message
- interested & not interested messaging
- logging

Yiheng Qui:
- set up ssh connection
- reading cfg files
- client & server connection
- message stream between server/client

Raul Rodriguez:
- handshake message
- updating bit field
- request & piece messaging
- peers downloading file
- have messaging
- exiting when all peers are done

## <u>Demo Link</u>

https://youtu.be/dSSPitSSyAY

## <u>Project Limitations</u>

Choking/unchoking protocols are not implemented.

## <u>Running Program</u>

For local use (creating peers manually):
1. Unzip project (if applicable)
2. If desired, change PeerInfo.cfg in /resources (each column describes peer id, peer address, port number, and if the peer has the file)
3. For testing locally, change adresses to localhost and make sure each port is different
4. If desired change Common.cfg to match the target file for download
5. Make sure there are enough peer folders (/peer_<peer id>) and they contain the target file depending on the cfg files
6. In the project directory, move to /src/main/java and compile all java files
```
cd ./src/main/java
```
```
javac *.java
```
7. In the same directory, run java PeerProcess <peer id> for each peer
```
java PeerProcess <peer id>
```

For remote use (creating peers manually):
1. For each peer, ssh into the desired remote machine
2. Change the peer address in PeerInfo.cfg to match the ssh's addresses
3. Sftp the project into the machine(s)
4. Follow the above instructions

An example for remote use can include using Intellij IDEA and WinSCP:
1. Open Intellij and run Start SSH Session (located under tools)
2. Enter host and credentials
3. Open WinSCP and enter the inputs as above
4. Move the project file into the remote host
5. Follow the instructions for remote use

## <u>Protocols</u>

- When a peer is created, it will connect to all peers before it in PeerInfo.cfg
- 
Handshaking:
- When a connection is made, the connecting peer will send a handshake message
- The receiver will send a handshake message in return, where the first peer will check if the peer is the correct one

Bitfield messaging:
- After the handshake process, the peer will send a bitfield message containing their bitfield
- the receiver will also send a bitfield message in return
- 

## <u>Notes</u>
