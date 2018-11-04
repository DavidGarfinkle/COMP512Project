# comp512-project

To run the RMI resource manager:

**Make sure to run from the project ```root``` directory instead of from the ```scripts/``` directory**
```
scripts/run_server.sh [<rmi_name>] # starts a single ResourceManager
scripts/run_servers.sh # convenience script for starting multiple resource managers
```

To run the RMI client:

```
cd Client
scripts/run_client.sh [<server_hostname> [<server_rmi_name>]]
```
