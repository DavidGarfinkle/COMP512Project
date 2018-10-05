echo "Instructions for launching the TCP middleware"
echo '  $1 - port to use for middleware socket'
echo '  $2 - hostname of Flights'
echo '  $3 - port of Flights'
echo '  $4 - hostname of Cars'
echo '  $5 - port of Flights'
echo '  $6 - hostname of Rooms'
echo '  $7 - port of Flights'
echo '  $8 - hostname of Customers'
echo '  $9 - port of Flights'


java -cp target/classes -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/../src/main/java/Server/ Server.TCP.TCPMiddleware $1 $2 $3 $4 $5 $6 $7 $8 $9
