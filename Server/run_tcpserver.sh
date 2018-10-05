echo "Usage: ./run_tcpserver.sh [server_port]"

./run_rmi.sh > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Server.TCP.TCPResourceManager $1
