# jersey-cors-everywhere

Requirement:
- Java 8
- Tomcat >= 7

# Run

Open browser:

```bash
http://localhost:8080/jersey-cors-everywhere/proxy/http://example.com/
```

Or JavaScript fetch:

```javascript
let endpoint = 'http://example.com/';
let proxy = 'http://localhost:8080/jersey-cors-everywhere/proxy'

fetch(proxy + '/' + endpoint);
```

# Customize

Add your necessary headers `Access-Control-Allow-Headers` to com.felix.filter.CORSFilter class.

```java
...
    @Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		response.getHttpHeaders().putSingle("Access-Control-Allow-Origin", "*");
		response.getHttpHeaders().putSingle("Access-Control-Allow-Credentials", "true");
		response.getHttpHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
		response.getHttpHeaders().putSingle("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Access-Control-Allow-Methods, Access-Control-Allow-Headers, Access-Control-Allow-Origin, X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
		return response;
	}
...
```
