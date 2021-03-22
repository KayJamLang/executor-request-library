# Request library for KayJam executor (web and cli)
Simple library for GET, POST and other requests

## How use
Create a new instance of the `request` class with a URL
```
var req = request("some url");
```

The default request method is GET, but you can change it using the `method` field
```
req.method = "POST";
```

To execute the request, call the 'execute' method
```
var response = req.execute();
```
