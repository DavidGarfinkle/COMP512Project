#!/bin/bash 

#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab1-1, cs-2, etc...)
MACHINES=(mimi.cs.mcgill.ca mimi.cs.mcgill.ca mimi.cs.mcgill.ca mimi.cs.mcgill.ca mimi.cs.mcgill.ca)

tmux new-session \; \
    rename-session servers \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 1 \; \
	send-keys "ssh -t ${MACHINES[0]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; scripts/run_server.sh Flight\"" C-m \; \
	select-pane -t 2 \; \
	send-keys "ssh -t ${MACHINES[1]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; scripts/run_server.sh Car\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t ${MACHINES[2]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; scripts/run_server.sh Room\"" C-m \; \
	select-pane -t 0 \; \
	send-keys "ssh -t ${MACHINES[4]} \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; sleep .5s; scripts/run_middleware.sh ${MACHINES[0]} ${MACHINES[1]} ${MACHINES[2]}\"" C-m \;
