package com.wl2o2o.smartojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.util.List;

/**
 * @Author <a href="https://github.com/wl2o2o">程序员CSGUIDER</a>
 * @From <a href="https://wl2o2o.github.io">CSGUIDER博客</a>
 * @CreateTime 2023/12/28
 */

public class DockerDemo {
    public static void main(String[] args) throws InterruptedException {
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        // 拉取镜像
        String image = "hello-world:latest";
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("下载镜像：" + item.getStatus());
                super.onNext(item);
            }
        };
        pullImageCmd.exec(pullImageResultCallback).awaitCompletion();

        // 列举镜像
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> imageList = listImagesCmd.exec();
        for (Image img : imageList) {
            System.out.println("镜像id: " + img.getId());
        }

        // 创建容器
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(image)
                .withCmd("echo", "HelloWorld")
                .exec();

        // 获取容器id
        String containerId = createContainerResponse.getId();

        // 查看容器状态
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containerList = listContainersCmd
                .withShowAll(true)
                .exec();
        for (Container container : containerList) {
            System.out.println("容器id: " + container.getId());
        }
        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();


        // 获取日志
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println("日志：" + new String(item.getPayload()));
                super.onNext(item);
            }
        };

        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(logContainerResultCallback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            System.out.println("获取日志失败");
            throw new RuntimeException(e);
        }

        // 删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        System.out.println("删除容器成功！");
        // 删除镜像
        dockerClient.removeImageCmd(image).withForce(true).exec();
        System.out.println("删除镜像成功！");


    }
}
