podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    spec:
      containers:
      - name: gradle
        image: gradle:6.3-jdk14
        command:
        - sleep
        args:
        - 99d
        volumeMounts:
        - name: shared-storage
          mountPath: /mnt
      - name: kaniko
        image: gcr.io/kaniko-project/executor:debug
        command:
        - sleep
        args:
        - 9999999
        volumeMounts:
        - name: shared-storage
          mountPath: /mnt
        - name: kaniko-secret
          mountPath: /kaniko/.docker
      restartPolicy: Never
      volumes:
      - name: shared-storage
        persistentVolumeClaim:
          claimName: jenkins-pv-claim
      - name: kaniko-secret
        secret:
            secretName: dockercred
            items:
            - key: .dockerconfigjson
              path: config.json
''') {
  node(POD_LABEL) {
    stage('Run pipeline against a gradle project') {
        git branch: 'master', url: 'https://github.com/kavinasi/Week8.git'
        container('gradle') {
            try {
                stage('Build a gradle project!') {
                    sh '''
                    pwd
                    chmod +x gradlew
                    ./gradlew build
                    mv ./build/libs/calculator-0.0.1-SNAPSHOT.jar /mnt
                    '''
                    }
            } catch (Exception E) {
                echo 'Failure detected'
            }
        }
    }
    if (env.BRANCH_NAME != "playground") {
      stage('Build Java Image') {
        container('kaniko') {
          image_version = ":1.0"
          stage('Build a container') {
            if (env.BRANCH_NAME == "master") {
              image_version = ":1.0"
            }
            if (env.BRANCH_NAME == "feature") {
              image_version = "-feature:0.1"
            }
            sh '''
            echo 'FROM openjdk:8-jre' > Dockerfile
            echo 'COPY ./calculator-0.0.1-SNAPSHOT.jar app.jar' >> Dockerfile
            echo 'ENTRYPOINT ["java", "-jar", "app.jar"]' >> Dockerfile
            ls /mnt/*jar
            mv /mnt/calculator-0.0.1-SNAPSHOT.jar .
            '''
            sh "/kaniko/executor --context `pwd` --destination kavinasi/calculator${image_version}"
          }
        }
      }
    }
  }
}
