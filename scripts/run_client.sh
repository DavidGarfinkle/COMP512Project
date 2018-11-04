# Usage: ./run_client.sh [<server_hostname> [<server_rmiobject>]]

java -cp target/classes -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ Client.RMIClient $1 $2
