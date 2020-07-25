package com.example.demo;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoTest {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    /**
     * 部署
     */
    public void deploy() {
        //得到流程引擎的方式三，利用底层封装，来加载配置文件，只需要调用方法即可
        ProcessEngine pec = ProcessEngines.getDefaultProcessEngine();

        //部署的服务对象
        RepositoryService repositoryService = pec.getRepositoryService();

        //部署请假任务
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("processes/process.bpmn")
                .name("请假")
                .deploy();

        System.out.println("请假部署ID：" + deploy.getId());
    }


    /**
     * 部署流程
     */
    @Test
    public void prepare() {
        // 创建一个部署对象
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("processes/process.bpmn")
                .deploy();
        System.out.println("部署ID：" + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }

    /**
     * 启动流程实例
     */
    public void startProcess() {
        String processDefinitionKey = "myProcess_1";
        Map<String, Object> map = new HashMap<>();

        //使用UEL 表达式设置

        // 学生填写申请单    Assignee：${student}
        map.put ("student", "lucy");

        // 班主任审批    Assignee：${teacher}
        map.put ("teacher", "jack");

        ProcessInstance instance = runtimeService.startProcessInstanceByKey (processDefinitionKey, map);
        System.out.println ("流程实例ID:" + instance.getId ());
        System.out.println ("流程定义ID:" + instance.getProcessDefinitionId ());
    }



    /**
     * 任务查询
     */
    public List<String> searchTask() throws UnsupportedEncodingException {
        //流程启动后，各各任务的负责人就可以查询自己当前需要处理的任务，查询出来的任务都是该用户的待办任务。
        List<Task> list = taskService.createTaskQuery ()
                //流程实例key
                .processDefinitionKey ("myProcess_1")
                //查询谁的任务
                //.taskAssignee("student")
                .list ();
        List<String> idList = new ArrayList<>();

        for (Task task : list) {
            idList.add (task.getId ());
            System.out.println ("任务ID:" + task.getId ());
            System.out.println ("任务名称:" + new String(task.getName().getBytes(),"GBK"));
            // System.out.println ("Assignee:" + task.getAssignee());

            System.out.println ("任务的创建时间:" + task.getCreateTime ());
            System.out.println ("任务的办理人:" + task.getAssignee ());
            System.out.println ("流程实例ID：" + task.getProcessInstanceId ());
            System.out.println ("执行对象ID:" + task.getExecutionId ());
            System.out.println ("流程定义ID:" + task.getProcessDefinitionId ());
        }

        return idList;
    }

    /**
     * 处理任务
     */
    public void disposeTask(List<String> list) {
        for (String id : list) {
            // 任务id
            taskService.complete (id);
            System.out.println ("处理任务id：" + id);
        }
    }



    @Test
    public void run() throws UnsupportedEncodingException {

        // 1.部署流程
        prepare ();

        // 2.启动一个流程实例
        startProcess();

        // 3.任务查询
        List list = searchTask ();

        // 4.处理任务
        disposeTask (list);
    }

    @Test
    public void testStrSize(){
        System.out.println("v1.0".length());
    }



}
