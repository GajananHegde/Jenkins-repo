def inject_env (String build_branch){
    env.deploy_test_var1='Var 1 - Hello'
    env.deploy_test_var2='Var 2 - World'
    env.environ_file='.Build-Dir/Test-2/.build/env'

    switch(build_branch) {
        case 'develop':
            env.deploy_test_var2='Var 2 - World - Develop branch'
            stage('Stage: Testing Stage execution in switch Statement'){
                echo "Hello I m executing within a stage statement inside a switch case! Yaay!!!!"
                sh 'echo helloshscript'
            }
            break
    }
}

def inject_stage (String build_branch){
    def brr="${env.build_branch}"
    echo "${build_branch}"
    echo "${env.build_branch}"
    switch(build_branch){
        case 'develop':
            echo "We are in the switch condition of inject_stage method!"
            echo "brr"
            echo '${brr}'
            echo '"${brr}"'
            break
    }
}


// def mainfunc(String build_branch, String build_number, String build_job, String build_url) {
def mainfunc(String parallel_stage, String param12, String param13){
    switch(parallel_stage){
        case 'Frontend':
            echo "We are in the frontend section"
            env.stringArray=["one","two","three"]
            env.stringSize=${env.stringArray}.size()
            echo "${env.stringSize}"
            echo param12
            echo param13
            break
        case 'Backend':
            echo "We are in the Backend section"
            echo "HELLO BACKEND"
            sleep(15)
            break
    }

    // inject_env(build_branch)
    // inject_stage(build_branch)   
    // sleep 15     
}

def mainfunc2(String build_branch, String build_number, String build_job, String build_url){
    inject_env(build_branch)    
    stage('Only stage'){

        echo "hello-World"
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'alchemy_core_mock_deploy_django_db_creds_develop' , usernameVariable: 'deploy_django_db_user', passwordVariable: 'deploy_django_db_password']]) {
        sh """
            sed -i "s/<% DEPLOY_DJANGO_DB_PASSWORD %>/${deploy_django_db_password}/g" ${environ_file}
            sed -i "s/<% DEPLOY_DJANGO_DB_USER %>/${deploy_django_db_user}/g" ${environ_file}
        """
        }

        sh """
        cat ${environ_file}
        ls -larth
        """
        sleep 20
    }
    stage('Parallel stage'){
        echo "hello-World - Parallel"

        sh """
        echo 'This is the parallel stage'
        ls -larth
        """  
    }
}

return this
