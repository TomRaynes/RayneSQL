<div align="center">
    <img src="Assets/banner.png" width="500">
    <p>
        RayneSQL is a transactional SQL-like client-server command-line interface program.
        The server manages concurrent clients through TCP socket connections that are handled through 
        execution in dedicated threads. Queries are processed entirely server-side 
        with a custom-built lexer, parser and executor, with the latter overseeing necessary data read/write 
        locks, ensuring thread-safety.
    </p>
</div>

## How To Run

1. Clone the repo and navigate to its top level
```
git clone git@github.com:TomRaynes/RayneSQL.git
cd RayneSQL
```
2. Start the server
```
./server
```
3. Start the client
```
./client
```

## Usage
RayneSQL implements a query language which is a simplified version SQL, supporting selects, 
updates, joins, inserts, and more. All complex compounded/nested conditions are supported. 
A short usage guide demonstrating some examples of supported queries can be found [here](Assets/Usage.md), 
while a full BNF grammar is of the language syntax is found [here](Assets/BNF.md).

By default, the client and server are configured to communicate locally over port 8888. If this port is 
already in use by another process, the optional "-p" flag can be passed to the client and server 
executables followed by and integer representing the desired port number. This must be the same for both
the client and server, e.g.,
```
./server -p 25000
./client -p 25000
```