echo "Usage: ./run_tcpclient.sh [[server_hostname] server_port]"

java -cp target/classes -Djava.security.policy=java.policy Client.TCPClient $1 $2
