echo "Usage: ./run_tcpserver.sh [server_port [server_name]]"

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.TCP.TCPResourceManager $1
