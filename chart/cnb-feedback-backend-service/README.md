### Context

Helm chart to deploy components in `scdf-kafka-backend-service` in Kubernetes. This should work in both 
* Local cluster (e.g. Kind, etc)
* Remote cluster

### Decisions & Assumptions


### Developer Guide

#### Tools Used
* kubectl
* helm (Helm 3)
* jq
* python-yq
* Kind (for local k8s cluster)


#### Validating Our Helm Change
Simply do dry-run
```
helm install test . --debug --dry-run=true
```
for remote target, please make sure to specify the corresponding values files.
```
helm install test . --debug --dry-run=true -f values-ibmcloud.yaml
```

#### Installation in Remote Cluster (IBM Cloud K8s)

##### Prepare Cluster Specific Values
This is just example.
```
cat <<EOT >> ./values-ibmcloud.yaml
common:
  clusterDomain: "realtimecampaign-cluster-4667f5c54a9fa16798873d0072267a42-0000.sng01.containers.appdomain.cloud"
  ingressSecretName: "realtimecampaign-cluster-4667f5c54a9fa16798873d0072267a42-0000"
  ingressClass: "public-iks-k8s-nginx"
EOT
```
Please have a look at [values.yaml](values.yaml) to see what you want to override, e.g. Google API Key, etc.

##### Prepare Namespace
* create namespace
```
export NS=scdf
kubectl create ns $NS
```
* copy docker registry and TLS secret 
(Only applicable for IBM Cloud K8s Cluster & Openshift cloud)
```
export DOCKER_REGISTRY_SECRET=all-icr-io
kubectl get secret -n default $DOCKER_REGISTRY_SECRET -o json | jq "del(.status) | del(.metadata) | .metadata={} | .metadata.name=\"$DOCKER_REGISTRY_SECRET\"" | kubectl apply -n $NS -f -
```
Only applicable for IBM Cloud K8s Cluster
```
export INGRESS_TLS_SECRET=$(cat values-ibmcloud.yaml | yq -r .common.ingressSecretName)
kubectl get secret -n default $INGRESS_TLS_SECRET -o json | jq "del(.status) | del(.metadata) | .metadata={} | .metadata.name=\"$INGRESS_TLS_SECRET\"" | kubectl apply -n $NS -f -
```

##### Install Helm Chart
Prepare cluster-specific helm values accordingly.
```
export CLUSTER_VALUES_FILE=values-openshift.yaml
# export CLUSTER_VALUES_FILE=values-ibmcloud.yaml
```

Install (For simplicity, we use namespace name as helm chart name)
```
helm install $NS -n $NS . -f $CLUSTER_VALUES_FILE
```

Make sure all are running
```
kubectl get po -n $NS --watch
```

To upgrade
```
helm upgrade $NS -n $NS . -f $CLUSTER_VALUES_FILE
```

To delete
```
helm delete $NS -n $NS
```

#### Accessing Endpoints
See the endpoints that's being exposed.
```
kubectl get ingress -n $NS
kubectl get ingress -n $NS backend-service -o jsonpath='{.spec.rules[0].host}'
```
or in openshift
```
kubectl get routes -n $NS
```

For dev, by default we install kafka & mongoDB and expose UI endpoints for those as well.
