<%@page language="java" contentType="text/html; charset=utf-8" %>
<html>
<body>
<h2>Hello World!</h2>

spring mvc上传文件
<form name="form1" action="manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="上传文件"/>
</form>
<br>
<br>
富文本文件上传
<form name="form2" action="manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="上传文件"/>
</form>

</body>
</html>
