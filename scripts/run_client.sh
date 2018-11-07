# Usage: scripts/run_client.sh [<server_hostname> [<server_rmiobject>]]

java -cp target/classes -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/src/main/java/Client/ Client.RMIClient $1 $2
