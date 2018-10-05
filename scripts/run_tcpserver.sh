echo "Usage: ./run_tcpserver.sh [server_port [server_name]]"

java -cp target/classes -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/../src/main/java/Server/ Server.TCP.TCPResourceManager $1
