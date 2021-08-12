#### MongoDB Setup Notes
If you are using shared mongoDB instance. Please make sure to create your own db and configure your helm accordingly. e.g. 
```
common:
  secrets:
    mongodb:
      url:
        # created 'scdf' db from mongo operator http://ops-mongodb.techgarage-4667f5c54a9fa16798873d0072267a42-0000.sng01.containers.appdomain.cloud/v2/60d1f42ec1da8d503b060620#deployment/topology
        value: "mongodb://my-mongodb-replicaset-2.my-mongodb-replicaset-svc.mongodb.svc.cluster.local:27017/scdf?retryWrites=true&w=majority&tls=true&tlsInsecure=true"

```
if you need to create new deployment
* From mongodb-operator UI
    * create new project e.g. `mongo-dev`
    * take note of the project ID
* create configMap
```
k create configmap -n mongodb mongo-dev-project-config --from-literal=projectName=mongo-dev --from-literal=orgId=60d1ef18c1da8d503b05f9b7 --from-literal=baseUrl=http://ops-manager-svc.mongodb.svc.cluster.local:8080
```
* create the mongodb CR. e.g.
```
apiVersion: mongodb.com/v1
kind: MongoDB
metadata:
  name: mongodb-dev
  namespace: mongodb
spec:
  credentials: test-org-api-key
  members: 1
  opsManager:
    configMapRef:
      name: mongo-dev-project-config
  persistent: true
  type: ReplicaSet
  version: 4.2.2-ent
```

#### Kafka Setup Notes

if kafka is installed using strimzi, make sure 
* kafka cluster is created (with topic operator set), e.g.
```
cat <<EOF | kubectl apply -n kafka -f -
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  clusterCa:
    generateCertificateAuthority: true
    generateSecretOwnerReference: false
  entityOperator:
    topicOperator:
      reconciliationIntervalSeconds: 60
    userOperator: {}
  kafka:
    config:
      inter.broker.protocol.version: "2.8"
      log.message.format.version: "2.8"
      offsets.topic.replication.factor: 3
      transaction.state.log.min.isr: 3
      transaction.state.log.replication.factor: 3
    listeners:
    - name: plain
      port: 9092
      tls: false
      type: internal
    - name: tls
EOF
```
* kafka bridge is created, e.g.
```
cat <<EOF | kubectl apply -n kafka -f -
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaBridge
metadata:
  name: kafka
spec:
  bootstrapServers: my-cluster-kafka-bootstrap:9092
  http:
    port: 8080
    cors:
      allowedMethods:
      - GET
      - POST
      - PUT
      - DELETE
      - OPTIONS
      - PATCH
      allowedOrigins:
      - .+
  replicas: 1
EOF
```
Note: CORS config above is defined to support frontend development.
* topics are created.
```
cat <<EOF | kubectl apply -n kafka -f -
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: scdf-incident-events
  labels:
    strimzi.io/cluster: my-cluster
spec:
  partitions: 1
  replicas: 1
  config:
    retention.ms: 604800000
    segment.bytes: 1073741824
---
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: scdf-incidents
  labels:
    strimzi.io/cluster: my-cluster
spec:
  partitions: 1
  replicas: 1
  config:
    retention.ms: 604800000
    segment.bytes: 1073741824
---
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: internal-incident-locations
  labels:
    strimzi.io/cluster: my-cluster
spec:
  partitions: 1
  replicas: 1
  config:
    retention.ms: 604800000
    segment.bytes: 1073741824
EOF

```
  
Otherwise, kafka topic creation can be done
```
export KAFKA_POD=$(kubectl get po -n $NS -o jsonpath='{.items[0].metadata.name}' -l io.kompose.service=kafka-cluster)
kubectl exec -n $NS $KAFKA_POD -- kafka-topics --create --topic scdf-incident-events --replication-factor 1 --partitions 1 --zookeeper 127.0.0.1:2181
```