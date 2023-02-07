#!/bin/sh

# nohup sends processes to the background
# so the ssh session can be detached 
# without the ngrok service being terminated
nohup /usr/bin/python3 server_start.py &
nohup /usr/local/bin/ngrok start --all &
