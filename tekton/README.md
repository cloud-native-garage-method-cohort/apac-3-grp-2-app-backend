create project
```
export PROJECT=grp-2-backend-cicd
oc new-project $PROJECT

oc apply -f build-bot-sa.yaml
oc apply -f role-deployer.yaml
oc apply -f pipeline.yaml
oc apply -f vcs-trigger-sa.yaml
oc apply -f vcs-trigger-roles.yaml
oc apply -f vcs-trigger-rolebindings.yaml
oc apply -f vcs-trigger-el.yaml
oc apply -f vcs-trigger-route.yaml
oc apply -f vcs-trigger-tt.yaml
oc apply -f vcs-trigger-tb.yaml
```
install maven & helm task
```
kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/helm-upgrade-from-source/0.2/helm-upgrade-from-source.yaml
kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/maven/0.2/maven.yaml
```

