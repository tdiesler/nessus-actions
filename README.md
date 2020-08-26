## WildFly Camel

[![Default Build](https://github.com/tdiesler/nessus-actions/workflows/Default%20Build/badge.svg)](https://github.com/tdiesler/nessus-actions/actions)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Explore [Apache Camel](http://camel.apache.org/) based actions inspired by [GitHub Actions](https://docs.github.com/en/actions). 


### Try It

First, you'd want to build this project with ...

```
mvn clean install
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

When you successfully completed the 'Try It' demo, you should be able to access the endpoint defined by the Camel route at

```
http://yourhost:8080/ticker
```

### What happens under the hood?

The 'Try It' portal generates a Camel deployment (i.e. a webapp) that contains a special route builder, which understands the YAML based
route definition. This webapp is then deployed to EAP via the management interface.

### What else could happen?

Here is a random collections of ideas that might be followed up from this

* Context sensitive YAML completion like we have with GitHub Actions
* Support for differnt target runtimes like Docker, OpenShift, etc.
* Perhaps even a standalone (i.e. your YAMl route in an executable jar) 

Enjoy!
