def for_loop_test(String numbers)
{
    numbers.tokenize(",").each { num ->
        echo "$num"
    }
    // String command=""
    // for ( str in stringList )
    // {
    //     command= command+str+" "
    // }
    // env.command1=command
}

def inject_env (String variable1){
    env.HEY_YO_THIS_IS_THE_PLACE_HOLDER = '--firstArg first --secondArg second'
    env.deploy_test_var1='Var 1 - Hello'
    env.deploy_test_var2='Var 2 - World'
    env.environ_file='.Build-Dir/project-Jenkins/.build/env'
    env.nginx_file_path = '.Build-Dir/project-Jenkins/.build/'
    env.docker_compose_file_nginx='.Build-Dir/project-Jenkins/.build/docker-compose-nginx.yml'
    env.env_file_name="\'p-bmo-commercial-nginx-us-redirect-prod-app-1\' \'p-bmo-commercial-nginx-us-redirect-prod-app-2\'"
    env.aws_region='us-west-2'
    // test_cli_command(env.env_file_name)
    // env.stringArray="one,two,three"
    // for_loop_test(env.stringArray)
    // print(env.stringArray instanceof String)
    sh """
    echo "${variable1} this is the test"
    cd ${nginx_file_path}
    docker-compose -f ${docker_compose_file_nginx} up -d
    docker-compose -f ${docker_compose_file_nginx} exec -T nginx nginx -s reload
    docker-compose -f ${docker_compose_file_nginx} down
    """

    // switch(build_branch) {
    //     case 'develop':
    //         env.deploy_test_var2='Var 2 - World - Develop branch'
    //         stage('Stage: Testing Stage execution in switch Statement'){
    //             echo "Hello I m executing within a stage statement inside a switch case! Yaay!!!!"
    //             sh 'echo helloshscript'
    //         }
    //         break
    // }
}

def test_cli_command(String env_file)
{
    sh """
    aws s3 ls
    aws ssm get-parameters --with-decryption --names ${env_file} --region ${aws_region} | jq -r '.Parameters[].Value'
    """
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
def mainfunc(String choices, String param){
    echo "-- ENV for building - ${param}"
    env.variable1 = 'Hohoho'
    env.variable2 = 'Hehehe'
    switch(param){
        case 'Frontend':
            withCredentials([usernamePassword(credentialsId: 'bad9031b-235c-4373-be6f-4448ea28e601', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            def encodedPassword = URLEncoder.encode("$GIT_PASSWORD",'UTF-8')
            sh '''
            echo hey this is the p[assword] "${GIT_PASSWORD}"
            '''
            }
            echo "We are in the frontend section"
            inject_env(env.variable1)
            inject_env(env.variable2)
            // for ( str in choices){
            //     print(str)
            // }
            // sh """
            //     sed -i '' -e "s/<% DEPLOY_AIRFLOW_DB_USER %>/${param}-${param}/g" ${environ_file}
            //     sed -i '' -e "s/<% Hey_yo_this_is_the_place_holder %>/${HEY_YO_THIS_IS_THE_PLACE_HOLDER}/g" ${environ_file}
            //     cat ${environ_file}
            // """
            // def stringArray=["one","two","three"]
            // String command = ""
            // for ( str in stringArray )
            // {
            //     command = command + str + " "
            // }
            // env.stringSize=stringArray.size()
            // echo command
            // echo param
            // echo "${env.command1}"
            // sh """
            // cat ${env.environ_file}
            // """
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
