# Request library for KayJam executor (web and cli)
Simple library for GET, POST and other requests

## How use

### Request
- Create a new instance of the `request` class with a URL
```
var req = request("some url");
```

- The default request method is GET, but you can change it using the `method` field
```
req.method = "POST";
```

- To execute the request, call the 'execute' method
```
var response = req.execute();
```

### Build url with query
- Create a map class with params
```
var params = map()
      .put("param1", 123);
```
- Build query params using the function `buildHttpQuery`
```
params = buildHttpQuery(params);
```
- Add params to url
```
var url = "https://google.com?"+params;
```
