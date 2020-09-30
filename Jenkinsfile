@Library('common-shared') _

pipeline {
  agent {
    kubernetes {
      label 'kubedeploy-agent'
      yaml '''
      apiVersion: v1
      kind: Pod
      spec:
        containers:
        - name: kubectl
          image: eclipsefdn/kubectl:1.9-alpine
          command:
          - cat
          tty: true
          resources:
            limits:
              cpu: 1
              memory: 1Gi
        - name: jnlp
          resources:
            limits:
              cpu: 1
              memory: 1Gi
      '''
    }
  }

  environment {
    APP_NAME = 'eclipsefdn-project-adopters'
    NAMESPACE = 'foundation-internal-webdev-apps'
    IMAGE_NAME = 'eclipsefdn/eclipsefdn-project-adopters'
    CONTAINER_NAME = 'app'
    ENVIRONMENT = sh(
      script: """
        if [ "${env.BRANCH_NAME}" = "master" ]; then
          printf "production"
        else
          printf "${env.BRANCH_NAME}"
        fi
      """,
      returnStdout: true
    )
    TAG_NAME = sh(
      script: """
        GIT_COMMIT_SHORT=\$(git rev-parse --short ${env.GIT_COMMIT})
        if [ "${env.ENVIRONMENT}" = "" ]; then
          printf \${GIT_COMMIT_SHORT}-${env.BUILD_NUMBER}
        else
          printf ${env.ENVIRONMENT}-\${GIT_COMMIT_SHORT}-${env.BUILD_NUMBER}
        fi
      """,
      returnStdout: true
    )
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  triggers { 
    // build once a week to keep up with parents images updates
    cron('H H * * H') 
  }

  stages {
    stage('Build build environment docker image') {
      agent {
        label 'docker-build'
      }
      steps {
        withDockerRegistry([credentialsId: '04264967-fea0-40c2-bf60-09af5aeba60f', url: 'https://index.docker.io/v1/']) {
          sh '''
             docker build -f src/main/docker/Dockerfile.agent --no-cache -t ${IMAGE_NAME}-build-env:${TAG_NAME} -t ${IMAGE_NAME}-build-env:latest .
             docker push ${IMAGE_NAME}-build-env:${TAG_NAME} 
             docker push ${IMAGE_NAME}-build-env:latest 
          '''
        }
      }
    }
    stage('Build project') {
      agent {
        kubernetes {
          label 'buildenv-agent'
          yaml '''
          apiVersion: v1
          kind: Pod
          spec:
            containers:
            - name: buildcontainer
              image: eclipsefdn/eclipsefdn-project-adopters-build-env:latest
              imagePullPolicy: Always
              command:
              - cat
              tty: true
              resources:
                limits:
                  cpu: 2
                  memory: 4Gi
            - name: jnlp
              resources:
                limits:
                  cpu: 2
                  memory: 4Gi
          '''
        }
      }
      steps {
        container('buildcontainer') {
          sh '''
             npm ci --no-cache
             npm run build
             mvn package
          '''
          stash name: "target", includes: "target/**/*"
        }
      }
    }
    stage('Build docker image') {
      agent {
        label 'docker-build'
      }
      steps {
        unstash name: "target"
        sh '''
           docker build -f src/main/docker/Dockerfile.jvm --no-cache -t ${IMAGE_NAME}:${TAG_NAME} -t ${IMAGE_NAME}:latest .
        '''
      }
    }

    stage('Push docker image') {
      agent {
        label 'docker-build'
      }
      when {
        anyOf {
          environment name: 'ENVIRONMENT', value: 'production'
          environment name: 'ENVIRONMENT', value: 'staging'
        }
      }
      steps {
        withDockerRegistry([credentialsId: '04264967-fea0-40c2-bf60-09af5aeba60f', url: 'https://index.docker.io/v1/']) {
          sh '''
            docker push ${IMAGE_NAME}:${TAG_NAME}
            docker push ${IMAGE_NAME}:latest
          '''
        }
      }
    }

    stage('Deploy to cluster') {
      when {
        anyOf {
          environment name: 'ENVIRONMENT', value: 'production'
          environment name: 'ENVIRONMENT', value: 'staging'
        }
      }
      steps {
        container('kubectl') {
          withKubeConfig([credentialsId: '6ad93d41-e6fc-4462-b6bc-297e360784fd', serverUrl: 'https://api.okd-c1.eclipse.org:6443']) {
            sh '''
              DEPLOYMENT="$(k8s getFirst deployment "${NAMESPACE}" "app=${APP_NAME},environment=${ENVIRONMENT}")"
              if [[ $(echo "${DEPLOYMENT}" | jq -r 'length') -eq 0 ]]; then
                echo "ERROR: Unable to find a deployment to patch matching 'app=${APP_NAME},environment=${ENVIRONMENT}' in namespace ${NAMESPACE}"
                exit 1
              else 
                DEPLOYMENT_NAME="$(echo "${DEPLOYMENT}" | jq -r '.metadata.name')"
                kubectl set image "deployment.v1.apps/${DEPLOYMENT_NAME}" -n "${NAMESPACE}" "${CONTAINER_NAME}=${IMAGE_NAME}:${TAG_NAME}" --record=true
                if ! kubectl rollout status "deployment.v1.apps/${DEPLOYMENT_NAME}" -n "${NAMESPACE}"; then
                  # will fail if rollout does not succeed in less than .spec.progressDeadlineSeconds
                  kubectl rollout undo "deployment.v1.apps/${DEPLOYMENT_NAME}" -n "${NAMESPACE}"
                  exit 1
                fi
              fi
            '''
          }
        }
      }
    }
  }

  post {
    always {
      deleteDir() /* clean up workspace */
      sendNotifications currentBuild
    }
  }
}
