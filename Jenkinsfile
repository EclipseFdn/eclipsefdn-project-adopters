@Library('common-shared') _

pipeline {
  agent {
    kubernetes {
      label 'kubedeploy-agent-' + env.JOB_NAME.replaceAll("/", "-")
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: kubectl
            image: eclipsefdn/kubectl:okd-c1
            command:
            - cat
            tty: true
            resources:
              limits:
                cpu: 1
                memory: 1Gi
            volumeMounts:
            - mountPath: "/home/default/.kube"
              name: "dot-kube"
              readOnly: false
          - name: jnlp
            resources:
              limits:
                cpu: 1
                memory: 1Gi
          volumes:
          - name: "dot-kube"
            emptyDir: {}
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
    timeout(time: 15, unit: 'MINUTES')
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
              env:
              - name: "MAVEN_OPTS"
                value: "-Duser.home=/home/jenkins"
              volumeMounts:
              - name: settings-xml
                mountPath: /home/jenkins/.m2/settings.xml
                subPath: settings.xml
                readOnly: true
              - name: m2-repo
                mountPath: /home/jenkins/.m2/repository
            - name: jnlp
              resources:
                limits:
                  cpu: 2
                  memory: 4Gi
            volumes:
            - name: settings-xml
              secret:
                secretName: m2-secret-dir
                items:
                - key: settings.xml
                  path: settings.xml
            - name: m2-repo
              emptyDir: {}
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
          unstash name: "target"
          withKubeConfig([credentialsId: 'ci-bot-okd-c1-token', serverUrl: 'https://api.okd-c1.eclipse.org:6443']) {
            sh '''
              kubectl create configmap eclipsefdn-project-adopters-map -n "${NAMESPACE}" --from-file=adopters_json_compressed=target/config/adopters.json --dry-run=client -o yaml | kubectl apply -f -
            '''
          }
          updateContainerImage([
            namespace: "${env.NAMESPACE}",
            selector: "app=${env.APP_NAME},environment=${env.ENVIRONMENT}",
            containerName: "${env.CONTAINER_NAME}",
            newImageRef: "${env.IMAGE_NAME}:${env.TAG_NAME}"
          ])
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
