# docx2pdf

docx 文件转 pdf 的工具。

## 使用说明

1. 运行程序
   - 第一种方式：源码运行
   - 第二种方式：Docker 运行
    ```bash
    docker run -d -p 8080:8080 --name docx2pdf poneding/docx2pdf:latest
    ```
   - 第三种方式：直接下载 jar 包运行
    ```bash
    java -jar docx2pdf-1.0.0.jar
    ```
2. 访问：http://localhost:8080/swagger-ui/index.html
3. 调用接口：`/convert`，上传文件 docx 文件，下载接口返回的 pdf 文件

