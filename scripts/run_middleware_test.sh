scripts/run_rmi.sh > /dev/null

echo "Edit file run_middleware.sh to include instructions for launching the middleware"
echo '  $1 - hostname of Resources'

java -cp target/classes -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/../src/main/java/Server/ Server.RMI.RMIMiddleware $1
