## WildFly Camel

[![Default Build](https://github.com/tdiesler/nessus-actions/workflows/Default%20Build/badge.svg)](https://github.com/tdiesler/nessus-actions/actions)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Explore [Apache Camel](http://camel.apache.org/) based actions inspired by [GitHub Actions](https://docs.github.com/en/actions). 


### Try It

First, you'd want to build this project with ...

```
mvn clean install
```

Then, you spin up a [Kewcloak](https://www.keycloak.org/getting-started/getting-started-docker) instance

```
KEYCLOAK_USER=admin
KEYCLOAK_PASSWORD=admin

docker run --detach \
	--name keycloak \
	-p 8080:8080 \
	-e KEYCLOAK_USER=$KEYCLOAK_USER \
	-e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
	quay.io/keycloak/keycloak 
```

and verify that you can login to the admin console

```
http://yourhost:8080/auth/admin
```

Then, obtain an initial access token

```
curl -X POST http://yourhost:8080/auth/realms/master/protocol/openid-connect/token \
	-H "Content-Type: application/x-www-form-urlencoded" \
	-d "client_id=admin-cli" \
	-d "username=$KEYCLOAK_USER" \
	-d "password=$KEYCLOAK_PASSWORD" \
	-d "grant_type=password"

ACCESS_TOKEN="eyJhbGciOi..."
```

Then import the realm definition

```
curl -X POST http://localhost:8080/auth/admin/realms \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $ACCESS_TOKEN" \
	-d "@docs/realm-export.json"
```

Then, you spin up a wildfly-camel instance

```
docker run --detach \
	--name wfcamel \
	-p 9990:9990 -p 8080:8080 \
	-e WILDFLY_MANAGEMENT_USER=admin \
	-e WILDFLY_MANAGEMENT_PASSWORD=admin \
	wildflyext/wildfly-camel 

```

and verify that you can login to the management interface

```
http://yourhost:9990/console/index.html
```

Then, you spin up a new version of the [RedHat Fuse](https://www.redhat.com/en/technologies/jboss-middleware/fuse) product page

```
docker run --detach \
	--name portal \
	-p 8181:8080 \
	nessusio/nessus-actions-portal
```

and connect to it

```
http://yourhost:8181/nessus-actions-portal
```

Enjoy!
