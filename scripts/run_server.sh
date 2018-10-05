#Usage: ./run_server.sh [<rmi_name>]

./run_rmi.sh > /dev/null 2>&1
java -cp target/classes -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)../target/classes/Server/ Server.RMI.RMIResourceManager $1
