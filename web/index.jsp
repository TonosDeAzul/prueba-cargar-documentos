<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Upload File</title>
</head>
<body>
    <h2>Upload File</h2>
    <form action="uploadServlet" method="post" enctype="multipart/form-data">
        <input type="file" name="file" />
        <input type="submit" value="Upload" />
    </form>
    <br />
    <a href="listFilesServlet">View Uploaded Files</a>
</body>
</html>
