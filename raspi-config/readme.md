# Raspberry Pi Config

The following is a short documentation of setting up a 
rasperry pi as a http server along with ssh access.   
In the case of this project, we used the raspberry pi 
for the contintuous integration server itself. 

Steps will assume macOS.

## Setup with Ngrok

1. `brew install ngrok/ngrok/ngrok`
2. `ngrok config {your-ngrok-authtoken}`
3. `ngrok config edit`

```json
authtoken: {your-ngrok-authtoken}

tunnels:
  http_apache:
    proto: http
    addr: 8000
  tcp_ssh:
    proto: tcp
    addr: 22
```
4. `cd ~ && mkdir ngrok`
5. We used python as a HTTP server   
`touch server_start.py`

```python
import http.server
import socketserver

PORT = 8000

Handler = http.server.SimpleHTTPRequestHandler

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print("serving at port", PORT)
    httpd.serve_forever()
```

6. Create a bash script for running

`touch run.sh`

```bash
#!/bin/sh

# nohup sends processes to the background
# so the ssh session can be detached 
# without the ngrok service being terminated
nohup /usr/bin/python3 server_start.py &
nohup /usr/local/bin/ngrok start --all &
```
7. Set permissions   
`chmod u+x run.sh`

8. Make an landing page
`touch index.html`

```html
<h1>hello, world!</h1>
```
9. Run the run.sh script

`./run.sh`

Now under Cloud Edge -> Endpoints in your ngrok user account, 
you should see your public http address and tcp tunnel.

With a tcp address as such `tcp://6.tcp.eu.ngrok.io:101010`   
the ssh login syntax is as follows:

`ssh {your-pi-user}@6.tcp.eu.ngrok.io -p 15583`
