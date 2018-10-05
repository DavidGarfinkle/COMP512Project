echo "Usage: ./run_tcpclient.sh [[server_hostname] server_port]"

java -Djava.security.policy=java.policy -cp ../Server/RMIInterface.jar:. Client.TCPClient $1 $2
