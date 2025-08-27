pipeline{
    agent{
        label 'Jenkins-Agent'
    }
    tools{
        maven 'MAVEN'
        dockerTool 'Docker'
    }

    environment{
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-rhb')
        IMAGE_NAME = "garagekandau" +"/" + "crm-prod"
        IMAGE_TAG = "stable-${BUILD_NUMBER}"
    }

    stages{
        stage("Cleanup Workspace"){
            steps{
                cleanWs()
            }
        }

        stage('Prepare Environment'){
            steps{
                git branch:'main', credentialsId: 'GithubToken', url:'https://github.com/Mean-Machine-Dee/crm-api.git'
            }
        }

        stage('Unit Testing'){
            steps{
                echo 'Running unit and integration tests ...'
            }
        }

        stage('Build and Push'){
            steps{
                sh 'docker login -u $DOCKERHUB_CREDENTIALS_USR --password $DOCKERHUB_CREDENTIALS_PSW'
                sh "docker build -t $IMAGE_NAME ."
                sh "docker tag $IMAGE_NAME $IMAGE_NAME:stable"
                sh "docker push $IMAGE_NAME:stable"
            }
        }

        stage('Deploy'){
            steps{
                sshagent(['production-sshkey']){
                    sh """
                        ssh -o StrictHostKeyChecking=no -l root 92.205.180.22 << END
whoami
uptime
cd /var/www/pipelines/crm/api
docker compose pull crm-api
END
                    """
                }
            }
        }

        stage("Clean Artifacts"){
            steps{
                sh "docker rmi $IMAGE_NAME:stable"
            }
        }
    }
}